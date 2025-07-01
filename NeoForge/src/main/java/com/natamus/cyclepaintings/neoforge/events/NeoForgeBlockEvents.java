package com.natamus.cyclepaintings.neoforge.events;

import com.natamus.cyclepaintings.events.BlockEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class NeoForgeBlockEvents {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		if (!BlockEvents.onRightClickBlock(e.getLevel(), e.getEntity(), e.getHand(), e.getPos(), e.getHitVec())) {
			e.setCanceled(true);
		}
	}
}
