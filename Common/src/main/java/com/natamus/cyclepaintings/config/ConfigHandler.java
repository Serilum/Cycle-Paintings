package com.natamus.cyclepaintings.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.cyclepaintings.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static String ignorePaintingsInCycleResourceLocation = "infernalexp,";
	@Entry public static boolean showRegisteredPaintingsDebug = false;

	public static void initConfig() {
		configMetaData.put("ignorePaintingsInCycleResourceLocation", Arrays.asList(
			"Split by a , (comma). The paintings to ignore during the cycle. You can either input an entire mod's prefix (only the part before the : (colon)) or the entire line found via 'showRegisteredPaintingsDebug'."
		));
		configMetaData.put("showRegisteredPaintingsDebug", Arrays.asList(
			"When enabled, prints all paintings registered to the console. With this you can find which to add to the 'ignorePaintingsInCycleResourceLocationPrefix' config."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}