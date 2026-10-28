package com.serilum.cyclepaintings;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.collective.fabric.callbacks.CollectiveBlockEvents;
import com.natamus.collective.services.Services;
import com.serilum.cyclepaintings.data.Constants;
import com.serilum.cyclepaintings.events.BlockEvents;
import com.serilum.cyclepaintings.events.PaintingEvent;
import com.serilum.cyclepaintings.util.Reference;
import com.serilum.cyclepaintings.util.Util;
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
				Util.setPaintings(server.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT));
			}
			catch (Exception ex) {
				Constants.logger.warn("[" + Reference.NAME + "] Something went wrong while loading all paintings.");
			}
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			return PaintingEvent.onClick(player, world, hand, entity, hitResult);
		});

		if (Services.MODLOADER.isModLoaded("fastpaintings")) {
			CollectiveBlockEvents.BLOCK_RIGHT_CLICK.register((level, player, hand, pos, hitVec) -> {
				return BlockEvents.onRightClickBlock(level, player, hand, pos, hitVec);
			});
		}
	}

	private static void setGlobalConstants() {

	}
}
