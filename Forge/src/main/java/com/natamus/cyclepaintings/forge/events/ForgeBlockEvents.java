package com.natamus.cyclepaintings.forge.events;

import com.natamus.cyclepaintings.events.BlockEvents;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeBlockEvents {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeBlockEvents.class);

		PlayerInteractEvent.RightClickBlock.BUS.addListener(ForgeBlockEvents::onRightClickBlock);
	}

	@SubscribeEvent
	public static boolean onRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		if (!BlockEvents.onRightClickBlock(e.getLevel(), e.getEntity(), e.getHand(), e.getPos(), e.getHitVec())) {
			return true;
		}
		return false;
	}
}
