package com.natamus.cyclepaintings;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.collective.services.Services;
import com.natamus.cyclepaintings.neoforge.config.IntegrateNeoForgeConfig;
import com.natamus.cyclepaintings.neoforge.events.NeoForgeBlockEvents;
import com.natamus.cyclepaintings.neoforge.events.NeoForgePaintingEvent;
import com.natamus.cyclepaintings.util.Reference;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Reference.MOD_ID)
public class ModNeoForge {
	
	public ModNeoForge(IEventBus modEventBus) {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		modEventBus.addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();

		IntegrateNeoForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		NeoForge.EVENT_BUS.register(NeoForgePaintingEvent.class);

		if (Services.MODLOADER.isModLoaded("fastpaintings")) {
			NeoForge.EVENT_BUS.register(NeoForgeBlockEvents.class);
		}
	}

	private static void setGlobalConstants() {

	}
}