package com.natamus.cyclepaintings.util;

import com.natamus.cyclepaintings.config.ConfigHandler;
import com.natamus.cyclepaintings.data.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Util {
	private static final List<Holder<PaintingVariant>> allPaintingVariants = new ArrayList<Holder<PaintingVariant>>();
	
	public static void setPaintings(Registry<PaintingVariant> paintingRegistry) {
		allPaintingVariants.clear();

		String[] allignore = ConfigHandler.ignorePaintingsInCycleResourceLocation.split(",");
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
			ResourceLocation resourceLocation = resourceKey.location();
			if (resourceLocation == null) {
				continue;
			}
			
			boolean allowed = true;
			String stringLocation = resourceLocation.toString().toLowerCase();
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
				
				PaintingVariant motive = paintingRegistry.get(resourceLocation);
				allPaintingVariants.add(paintingRegistry.getHolderOrThrow(resourceKey));
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
}
