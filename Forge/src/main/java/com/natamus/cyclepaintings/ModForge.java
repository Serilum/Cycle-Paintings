package com.natamus.cyclepaintings;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.collective.services.Services;
import com.natamus.cyclepaintings.forge.config.IntegrateForgeConfig;
import com.natamus.cyclepaintings.forge.events.ForgeBlockEvents;
import com.natamus.cyclepaintings.forge.events.ForgePaintingEvent;
import com.natamus.cyclepaintings.util.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class ModForge {
	
	public ModForge(FMLJavaModLoadingContext modLoadingContext) {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		BusGroup busGroup = modLoadingContext.getModBusGroup();
		FMLLoadCompleteEvent.getBus(busGroup).addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();

		IntegrateForgeConfig.registerScreen(modLoadingContext);

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
    	ForgePaintingEvent.registerEventsInBus();

		if (Services.MODLOADER.isModLoaded("fastpaintings")) {
			ForgeBlockEvents.registerEventsInBus();
		}
	}

	private static void setGlobalConstants() {

	}
}