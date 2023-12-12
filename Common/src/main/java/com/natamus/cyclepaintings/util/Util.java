package com.natamus.cyclepaintings.util;

import com.natamus.cyclepaintings.config.ConfigHandler;
import com.natamus.cyclepaintings.data.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Util {
	private static final List<Holder<PaintingVariant>> allPaintingVariants = new ArrayList<Holder<PaintingVariant>>();

	public static void setPaintings(Registry<PaintingVariant> paintingRegistry) {
		if (!allPaintingVariants.isEmpty()) {
			return;
		}

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
		int xSize = currentVariant.getWidth();
		int ySize = currentVariant.getHeight();

		for (Holder<PaintingVariant> paintingVariantHolder : allPaintingVariants) {
			PaintingVariant paintingVariant = paintingVariantHolder.value();
			if (paintingVariant.getWidth() == xSize && paintingVariant.getHeight() == ySize) {
				similarVariants.add(paintingVariantHolder);
			}
		}

		return similarVariants;
	}
}
