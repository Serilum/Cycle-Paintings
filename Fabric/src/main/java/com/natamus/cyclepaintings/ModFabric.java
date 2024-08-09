package com.natamus.cyclepaintings;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.cyclepaintings.data.Constants;
import com.natamus.cyclepaintings.events.PaintingEvent;
import com.natamus.cyclepaintings.util.Reference;
import com.natamus.cyclepaintings.util.Util;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.registries.Registries;

public class ModFabric implements ModInitializer {
	
	@Override
	public void onInitialize() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		setGlobalConstants();
		ModCommon.init();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			try {
				Util.setPaintings(server.registryAccess().registryOrThrow(Registries.PAINTING_VARIANT));
			}
			catch (Exception ex) {
				Constants.logger.warn("[" + Reference.NAME + "] Something went wrong while loading all paintings.");
			}
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			return PaintingEvent.onClick(player, world, hand, entity, hitResult);
		});
	}

	private static void setGlobalConstants() {

	}
}
