package com.natamus.cyclepaintings.neoforge.events;

import com.natamus.cyclepaintings.data.Constants;
import com.natamus.cyclepaintings.events.PaintingEvent;
import com.natamus.cyclepaintings.util.Reference;
import com.natamus.cyclepaintings.util.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber
public class NeoForgePaintingEvent {
	@SubscribeEvent
	public static void onServerStart(ServerStartedEvent e) {
		try {
			Util.setPaintings(e.getServer().registryAccess().registryOrThrow(BuiltInRegistries.PAINTING_VARIANT.key()));
		}
		catch (Exception ex) {
			Constants.logger.warn("[" + Reference.NAME + "] Something went wrong while loading all paintings.");
		}
	}

	@SubscribeEvent
	public static void onClick(PlayerInteractEvent.EntityInteract e) {
		Player player = e.getEntity();
		if (PaintingEvent.onClick(player, e.getLevel(), e.getHand(), e.getTarget(), null).equals(InteractionResult.SUCCESS)) {
			player.swing(e.getHand());
		}
	}
}
