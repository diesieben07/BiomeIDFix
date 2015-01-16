package de.take_weiland.mods.biomeid;

import com.google.common.base.Objects;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author diesieben07
 */
public final class ASMHooks {

	private static Map<BiomeGenBase, String> biomeToMod = new WeakHashMap<BiomeGenBase, String>();

	public static void onBiomeConstruct(BiomeGenBase biome, int desiredID, boolean register) {
		if (!register) return;

		biomeToMod.put(biome, getModDescCurr());

		BiomeGenBase presentBiome = BiomeGenBase.getBiomeGenArray()[desiredID];
		if (presentBiome != null) {
			CrashReport cr = new CrashReport("Registering Biome", new IllegalArgumentException("Duplicate BiomeID"));
			cr.makeCategory("Biome ID").addCrashSection("ID", desiredID);

			makeBiomeCat(cr, "Biome 1", presentBiome);
			makeBiomeCat(cr, "Biome 2", biome);

			throw new ReportedException(cr);
		}
	}

	private static void makeBiomeCat(CrashReport cr, String catName, BiomeGenBase biome) {
		CrashReportCategory cat = cr.makeCategory("Biome 1");
		cat.addCrashSection("Owning Mod", getOwningMod(biome));
		cat.addCrashSection("Biome class", biome.getClass().getName());
	}

	private static String getOwningMod(BiomeGenBase biome) {
		String mod = biomeToMod.get(biome);
		return Objects.firstNonNull(mod, "Unknown");
	}

	private static String getModDescCurr() {
		try {
			ModContainer mc = Loader.instance().activeModContainer();
			return mc == null ? "Unknown" : (mc.getName() + '[' + mc.getModId() + ']');
		} catch (Throwable e) {
			return "Unknown";
		}
	}

}
