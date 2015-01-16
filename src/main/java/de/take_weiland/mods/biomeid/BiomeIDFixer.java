package de.take_weiland.mods.biomeid;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

/**
 * @author diesieben07
 */
public class BiomeIDFixer extends DummyModContainer {

	public BiomeIDFixer() {
		super(new ModMetadata());
		ModMetadata meta = getMetadata();
		meta.modId = "biomeidfix";
		meta.name = "Biome ID Fixer";
		meta.version = "1.7.10-0.1beta";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		return true;
	}
}