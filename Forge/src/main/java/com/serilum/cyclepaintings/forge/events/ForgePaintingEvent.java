package com.serilum.cyclepaintings.forge.events;

import com.serilum.cyclepaintings.data.Constants;
import com.serilum.cyclepaintings.events.PaintingEvent;
import com.serilum.cyclepaintings.util.Reference;
import com.serilum.cyclepaintings.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgePaintingEvent {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgePaintingEvent.class);
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartedEvent e) {
		try {
			Util.setPaintings(e.getServer().registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT));
		}
		catch (Exception ex) {
			Constants.logger.warn("[" + Reference.NAME + "] Something went wrong while loading all paintings.");
		}
	}

	@SubscribeEvent
	public static void onClick(PlayerInteractEvent.EntityInteractSpecific e) {
		Player player = e.getEntity();
		if (PaintingEvent.onClick(player, e.getLevel(), e.getHand(), e.getTarget(), null).equals(InteractionResult.SUCCESS)) {
			player.swingAndResetAttackStrength(e.getHand(), SwingAnimation.DEFAULT, false);
		}
	}
}
