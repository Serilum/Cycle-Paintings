package com.natamus.cyclepaintings.forge.events;

import com.natamus.cyclepaintings.data.Constants;
import com.natamus.cyclepaintings.events.PaintingEvent;
import com.natamus.cyclepaintings.util.Reference;
import com.natamus.cyclepaintings.util.Util;
import net.minecraft.core.Registry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgePaintingEvent {
	@SubscribeEvent
	public void onServerStart(ServerStartedEvent e) {
		try {
			Util.setPaintings(e.getServer().registryAccess().registryOrThrow(Registry.MOTIVE_REGISTRY));
		}
		catch (Exception ex) {
			Constants.logger.warn("[" + Reference.NAME + "] Something went wrong while loading all paintings.");
		}
	}

	@SubscribeEvent
	public void onClick(PlayerInteractEvent.EntityInteract e) {
		Player player = e.getPlayer();
		if (PaintingEvent.onClick(player, e.getWorld(), e.getHand(), e.getTarget(), null).equals(InteractionResult.SUCCESS)) {
			player.swing(e.getHand());
		}
	}
}
