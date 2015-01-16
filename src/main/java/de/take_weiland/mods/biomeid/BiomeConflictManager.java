package de.take_weiland.mods.biomeid;

import com.google.common.base.Objects;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.*;

/**
 * @author diesieben07
 */
public final class BiomeConflictManager {

	private static Map<BiomeGenBase, String> biomeOwners = new HashMap<BiomeGenBase, String>();
	private static TIntObjectMap<Conflict> conflicts = new TIntObjectHashMap<Conflict>();

	public static void onBiomeConstruct(BiomeGenBase biome, int desiredID, boolean register) {
		if (!register) return;

		biomeOwners.put(biome, getModDescCurr());

		BiomeGenBase presentBiome = BiomeGenBase.getBiomeGenArray()[desiredID];
		if (presentBiome != null) {
			Conflict conflict = conflicts.get(desiredID);
			if (conflict == null) {
				conflict = new Conflict();
				conflicts.put(desiredID, conflict);
			}
			conflict.addBiome(presentBiome);
			conflict.addBiome(biome);
		}
	}

	public static void crashIfNecessary() {
		try {
			if (!conflicts.isEmpty()) {
				String desc = "Biome ID Conflicts";
				CrashReport cr = CrashReport.makeCrashReport(new RuntimeException(desc), desc);
				TIntObjectIterator<Conflict> it = conflicts.iterator();
				while (it.hasNext()) {
					it.advance();
					int id = it.key();
					Conflict conflict = it.value();

					CrashReportCategory cat = cr.makeCategory("Biome ID " + id);
					cat.addCrashSection("ID", id);
					StringBuilder sb = new StringBuilder();
					for (BiomeGenBase biome : conflict.conflicts) {
						sb.append("[Name: ")
								.append(getOrUnknown(biome.biomeName))
								.append(", Owning Mod: ")
								.append(getBiomeOwner(biome))
								.append(", Class: ")
								.append(getBiomeClass(biome))
								.append(']');
					}
					cat.addCrashSection("Biomes", sb.toString().replace("][", "],\n\t        ["));
				}
				throw new ReportedException(cr);
			}
		} finally {
			conflicts = null;
			biomeOwners = null;
		}
	}

	private static String getBiomeOwner(BiomeGenBase biome) {
		return getOrUnknown(biomeOwners.get(biome));
	}

	private static String getBiomeClass(BiomeGenBase biome) {
		return biome.getClass().getName();
	}

	private static String getOrUnknown(Object o) {
		return Objects.firstNonNull(o, "Unknown").toString();
	}

	private static void makeBiomeCat(CrashReport cr, String catName, BiomeGenBase biome) {
		CrashReportCategory cat = cr.makeCategoryDepth(catName, 2);
		cat.addCrashSection("Owning Mod", getOwningMod(biome));
		cat.addCrashSection("Biome class", biome.getClass().getName());
	}

	private static String getOwningMod(BiomeGenBase biome) {
		String mod = biomeOwners.get(biome);
		return Objects.firstNonNull(mod, "Unknown");
	}

	private static String getModDescCurr() {
		try {
			ModContainer mc = Loader.instance().activeModContainer();
			if (mc != null && mc.getModId().equals("Forge")) {
				mc = Loader.instance().getMinecraftModContainer();
			}
			return mc == null ? "Unknown" : (mc.getName() + '(' + mc.getModId() + ')');
		} catch (Throwable e) {
			return "Unknown";
		}
	}

	private static class Conflict {

		private final Set<BiomeGenBase> conflicts = Collections.newSetFromMap(new IdentityHashMap<BiomeGenBase, Boolean>());

		void addBiome(BiomeGenBase biome) {
			conflicts.add(biome);
		}

	}

}
