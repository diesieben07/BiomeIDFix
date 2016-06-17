package de.take_weiland.mods.biomeid;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author diesieben07
 */
@Mod(modid = "biomeidfix", name = "Biome ID Fixer", version = "@VERSION@", acceptableRemoteVersions = "*")
public class BiomeIDFixer {

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void firstClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			BiomeConflictManager.crashIfNecessary();
			FMLCommonHandler.instance().bus().unregister(this);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.SERVER)
	public void firstServerTick(TickEvent.ServerTickEvent event) {
		BiomeConflictManager.crashIfNecessary();
		FMLCommonHandler.instance().bus().unregister(this);
	}

}
