package com.natamus.cyclepaintings.util;

import com.natamus.cyclepaintings.config.ConfigHandler;
import com.natamus.cyclepaintings.data.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Motive;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Util {
	private static final List<Holder<Motive>> allPaintingVariants = new ArrayList<Holder<Motive>>();

	public static void setPaintings(Registry<Motive> paintingRegistry) {
		if (!allPaintingVariants.isEmpty()) {
			return;
		}

		String[] allignore = ConfigHandler.ignorePaintingsInCycleResourceLocation.split(",");
		boolean debug = ConfigHandler.showRegisteredPaintingsDebug;

		if (debug) {
			Constants.logger.info("[Cycle Paintings Debug] The config option 'showRegisteredPaintingsDebug' has been enabled. Showing paintings during cycle registration.");
		}

		for (Holder<Motive> paintingVariantHolder : paintingRegistry.asHolderIdMap()) {
			Optional<ResourceKey<Motive>> optional =  paintingVariantHolder.unwrapKey();
			if (optional.isEmpty()) {
				continue;
			}

			ResourceKey<Motive> resourceKey = optional.get();
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

				Motive motive = paintingRegistry.get(resourceLocation);
				allPaintingVariants.add(paintingRegistry.getHolderOrThrow(resourceKey));
			}
		}
	}

	public static List<Holder<Motive>> getSimilarArt(Motive currentVariant) {
		List<Holder<Motive>> similarVariants = new ArrayList<Holder<Motive>>();
		int xSize = currentVariant.getWidth();
		int ySize = currentVariant.getHeight();

		for (Holder<Motive> paintingVariantHolder : allPaintingVariants) {
			Motive paintingVariant = paintingVariantHolder.value();
			if (paintingVariant.getWidth() == xSize && paintingVariant.getHeight() == ySize) {
				similarVariants.add(paintingVariantHolder);
			}
		}

		return similarVariants;
	}
}
