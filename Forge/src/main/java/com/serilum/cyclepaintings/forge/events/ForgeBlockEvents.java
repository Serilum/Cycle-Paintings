package com.serilum.cyclepaintings.forge.events;

import com.serilum.cyclepaintings.events.BlockEvents;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeBlockEvents {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		if (!BlockEvents.onRightClickBlock(e.getLevel(), e.getEntity(), e.getHand(), e.getPos(), e.getHitVec())) {
			e.setCanceled(true);
		}
	}
}
