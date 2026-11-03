package com.serilum.cyclepaintings.util;

import com.serilum.cyclepaintings.config.ConfigHandler;
import com.serilum.cyclepaintings.data.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Util {
	private static final List<Holder<PaintingVariant>> allPaintingVariants = new ArrayList<Holder<PaintingVariant>>();
	
	public static void setPaintings(Registry<PaintingVariant> paintingRegistry) {
		allPaintingVariants.clear();

		String[] allignore = ConfigHandler.ignorePaintingsInCycleIdentifier.split(",");
		boolean debug = ConfigHandler.showRegisteredPaintingsDebug;
		
		if (debug) {
			Constants.logger.info("[Cycle Paintings Debug] The config option 'showRegisteredPaintingsDebug' has been enabled. Showing paintings during cycle registration.");
		}

		for (Holder<PaintingVariant> paintingVariantHolder : paintingRegistry.getTagOrEmpty(PaintingVariantTags.PLACEABLE)) {
			Optional<ResourceKey<PaintingVariant>> optional =  paintingVariantHolder.unwrapKey();
			if (optional.isEmpty()) {
				continue;
			}

			ResourceKey<PaintingVariant> resourceKey = optional.get();
			Identifier identifier = resourceKey.identifier();
			if (identifier == null) {
				continue;
			}
			
			boolean allowed = true;
			String stringLocation = identifier.toString().toLowerCase();
			for (String toignore : allignore) {
				toignore = toignore.toLowerCase().trim();
				if (toignore.contains(":")) {
					if (stringLocation.equals(toignore)) {
						allowed = false;
						break;
					}
				}
				else if (stringLocation.split(":")[0].contains(toignore)) {
					allowed = false;
					break;
				}
			}
			
			if (!allowed) {
				if (debug) {
					Constants.logger.info("[Cycle Paintings Debug] " + stringLocation + " (ignored)");
				}
			}
			else {
				if (debug) {
					Constants.logger.info("[Cycle Paintings Debug] " + stringLocation + " (allowed)");
				}
				
				Optional<Holder.Reference<PaintingVariant>> motiveOptionalReference = paintingRegistry.get(identifier);
				motiveOptionalReference.ifPresent(allPaintingVariants::add);
			}
		}
	}
	
	public static List<Holder<PaintingVariant>> getSimilarArt(PaintingVariant currentVariant) {
		List<Holder<PaintingVariant>> similarVariants = new ArrayList<Holder<PaintingVariant>>();
		int xSize = currentVariant.width();
		int ySize = currentVariant.height();
		
		for (Holder<PaintingVariant> paintingVariantHolder : allPaintingVariants) {
			PaintingVariant paintingVariant = paintingVariantHolder.value();
			if (paintingVariant.width() == xSize && paintingVariant.height() == ySize) {
				similarVariants.add(paintingVariantHolder);
			}
		}
		
		return similarVariants;
	}

	public static Holder<PaintingVariant> getNewPaintingVariant(Player player, Holder<PaintingVariant> currentVariant) {
		List<Holder<PaintingVariant>> similarPaintingVariants = Util.getSimilarArt(currentVariant.value());
		if (similarPaintingVariants.isEmpty()) {
			return null;
		}

		if (player.isCrouching()) {
			Collections.reverse(similarPaintingVariants);
		}

		if (similarPaintingVariants.get(similarPaintingVariants.size()-1).value().equals(currentVariant.value())) {
			return similarPaintingVariants.get(0);
		}
		else {
			boolean choosenext = false;
			for (Holder<PaintingVariant> similarVariant : similarPaintingVariants) {
				if (choosenext) {
					return similarVariant;
				}
				if (similarVariant.value().equals(currentVariant.value())) {
					choosenext = true;
				}
			}
		}

		return null;
	}

	public static void setFastPaintingsVariant(Level level, BlockEntity paintingBlockEntity, Identifier variantIdentifier) {
		CompoundTag tag = paintingBlockEntity.saveWithoutMetadata(level.registryAccess());
		tag.putString("variant", variantIdentifier.toString());

		paintingBlockEntity.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag));
		paintingBlockEntity.setChanged();

		BlockState paintingBlockState = paintingBlockEntity.getBlockState();
		level.sendBlockUpdated(paintingBlockEntity.getBlockPos(), paintingBlockState, paintingBlockState, Block.UPDATE_CLIENTS);
	}

	public static Vec3 getFastPaintingCenter(BlockPos masterPosition, Direction facing, PaintingVariant paintingVariant) {
		double widthOffset = (paintingVariant.width() - 1) / 2.0;
		double heightOffset = (paintingVariant.height() - 1) / 2.0;

		Vec3 masterCenterOnWall = Vec3.atCenterOf(masterPosition).relative(facing.getOpposite(), 0.5);
		return masterCenterOnWall.relative(facing.getCounterClockWise(), widthOffset).relative(Direction.DOWN, heightOffset);
	}

	public static void spawnCycleParticles(ServerLevel serverLevel, Vec3 paintingCenter, Direction facing, PaintingVariant paintingVariant) {
		if (!ConfigHandler.showParticlesOnCycle) {
			return;
		}

		Vec3 particlePosition = paintingCenter.relative(facing, 0.2);
		double spreadAlongWall = paintingVariant.width() / 4.0;
		double spreadUp = paintingVariant.height() / 4.0;

		double xSpread = 0.05;
		double zSpread = spreadAlongWall;
		if (facing.getAxis() == Direction.Axis.Z) {
			xSpread = spreadAlongWall;
			zSpread = 0.05;
		}

		DustParticleOptions frameDust = new DustParticleOptions(0x8A6528, 0.5F);
		int particleCount = 3 + paintingVariant.width() * paintingVariant.height() / 2;
		serverLevel.sendParticles(frameDust, particlePosition.x, particlePosition.y, particlePosition.z, particleCount, xSpread, spreadUp, zSpread, 0);
	}
}
