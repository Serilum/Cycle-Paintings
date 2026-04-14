package com.natamus.cyclepaintings.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.TaskFunctions;
import com.natamus.cyclepaintings.util.Util;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class BlockEvents {
	public static boolean onRightClickBlock(Level level, Player player, InteractionHand hand, BlockPos blockPos, BlockHitResult hitVec) {
        if (level.isClientSide) {
            return true;
        }

		ItemStack handstack = player.getItemInHand(hand);
		if (!handstack.getItem().equals(Items.PAINTING)) {
			return true;
		}

        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (!(block instanceof EntityBlock)) {
            return true;
        }

        ResourceLocation blockResourceLocation = level.registryAccess().registryOrThrow(Registries.BLOCK).getKey(block);
        if (!blockResourceLocation.getNamespace().equals("fastpaintings")) {
            return true;
        }

        Optional<Direction> optionalFacing = blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING);
        if (optionalFacing.isEmpty()) {
            return true;
        }

        Direction facing = optionalFacing.get();
        Pair<Integer, Integer> offsetPair = getOffsetsFromState(blockState);

        BlockPos masterPosition = blockPos.above(offsetPair.getSecond()).relative(facing.getClockWise(), offsetPair.getFirst());

        BlockEntity paintingBlockEntity = level.getBlockEntity(masterPosition);
        if (paintingBlockEntity == null) {
            return true;
        }

        CompoundTag tag = paintingBlockEntity.getUpdateTag();
        if (!tag.contains("variant")) {
            return true;
        }

        String rawVariantResourceLocation = tag.getString("variant");

        ResourceLocation variantResourceLocation;
        try {
            variantResourceLocation = new ResourceLocation(rawVariantResourceLocation);
        }
        catch (ResourceLocationException ex) {
            return true;
        }

        Registry<PaintingVariant> paintingVariantRegistry = level.registryAccess().registryOrThrow(Registries.PAINTING_VARIANT);
        Optional<PaintingVariant> optionalPaintingVariant = paintingVariantRegistry.getOptional(variantResourceLocation);
        if (optionalPaintingVariant.isEmpty()) {
            return true;
        }

        Holder<PaintingVariant> paintingVariantHolder = paintingVariantRegistry.wrapAsHolder(optionalPaintingVariant.get());

        Holder<PaintingVariant> newVariantHolder = Util.getNewPaintingVariant(player, paintingVariantHolder);
        if (newVariantHolder == null) {
            return true;
        }
        PaintingVariant newVariant = newVariantHolder.value();

        int heightBlocks = newVariant.getHeight() / 16;
        int widthBlocks = newVariant.getWidth() / 16;

        BlockPos newPaintingPosition = masterPosition.mutable();
        if (heightBlocks > 1) {
            newPaintingPosition = newPaintingPosition.below(heightBlocks / 2);
        }
        if (widthBlocks > 2) {
            newPaintingPosition = newPaintingPosition.relative(facing.getCounterClockWise(), widthBlocks / 2);
        }

        level.setBlock(masterPosition, Blocks.AIR.defaultBlockState(), 3);

        BlockPos finalNewPaintingPosition = newPaintingPosition.immutable();
        TaskFunctions.enqueueCollectiveServerTask(level.getServer(), () -> {
            level.addFreshEntity(new Painting(level, finalNewPaintingPosition, facing, newVariantHolder));
        }, 0);

        return true;
	}

    @SuppressWarnings("unchecked")
    public static Pair<Integer, Integer> getOffsetsFromState(BlockState state) {
        int xOffset = 0;
        int yOffset = 0;

        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty intProp) {
                switch (intProp.getName()) {
                    case "x_offset" -> xOffset = state.getValue((Property<Integer>)property);
                    case "y_offset" -> yOffset = state.getValue((Property<Integer>)property);
                }
            }
        }

        return Pair.of(xOffset, yOffset);
    }
}
