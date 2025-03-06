package com.natamus.cyclepaintings.events;

import com.natamus.cyclepaintings.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class PaintingEvent {
	public static InteractionResult onClick(Player player, Level level, InteractionHand hand, Entity target, EntityHitResult hitResult) {
		ItemStack handstack = player.getItemInHand(hand);
		if (!handstack.getItem().equals(Items.PAINTING)) {
			return InteractionResult.PASS;
		}
		
		if (!(target instanceof Painting painting)) {
			return InteractionResult.PASS;
		}

		Holder<PaintingVariant> newVariant = Util.getNewPaintingVariant(player, painting.getVariant());
		if (newVariant == null) {
			return InteractionResult.PASS;
		}

		painting.setVariant(newVariant);

		return InteractionResult.SUCCESS;
	}
}
