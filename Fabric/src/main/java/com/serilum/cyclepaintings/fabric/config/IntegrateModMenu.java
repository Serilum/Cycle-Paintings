package com.serilum.cyclepaintings.fabric.config;

import com.natamus.collective.config.DuskConfig;
import com.serilum.cyclepaintings.util.Reference;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class IntegrateModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> DuskConfig.DuskConfigScreen.getScreen(parent, Reference.MOD_ID);
	}
}