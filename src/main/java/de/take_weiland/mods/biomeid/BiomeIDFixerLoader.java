package de.take_weiland.mods.biomeid;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * @author diesieben07
 */
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({ "de.take_weiland.mods.biomeid. "})
public final class BiomeIDFixerLoader implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				"de.take_weiland.mods.biomeid.transformers.BiomeGenBaseTransformer"
		};
	}

	@Override
	public String getModContainerClass() {
		return "de.take_weiland.mods.biomeid.BiomeIDFixer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}