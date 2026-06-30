package com.honeyedlemons.verneuli.util;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.entities.gems.AbstractGem;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ColorUtil {

	public static int generateColorFromPalette(Identifier location, RandomSource random, ServerLevelAccessor server) {
		var color = Color.white.getRGB();
		var resourceManager = Objects.requireNonNull(server.getServer()).getResourceManager();
		var paletteFile = resourceManager.getResource(location);

		if (paletteFile.isEmpty())
			return color;

		BufferedImage palette;

		try {
			palette = ImageIO.read(paletteFile.get().open());
		} catch (IOException e) {
			Verneuil.LOGGER.atError().log("Palette not found at " + location.getPath());
			return color;
		}

		ArrayList<Integer> colors = new ArrayList<>();

		for (int x = 0; x < palette.getWidth(); x++)
			colors.add(palette.getRGB(x, 0));

		var randomIndex = random.nextInt(colors.size());

		return colors.get(randomIndex);
	}

	public static Identifier paletteLocation(AbstractGem gem, String type) {
		var gemType = gem.getType().toShortString();
		var gemVariantType = gem.getGemVariant().type();
		return Verneuil.id("palettes/" + gemType + "/" + gemVariantType + "/" + type + ".png");
	}

	public static Color colorFromInt(int rgb) {
		return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
	}

	public static Double colorDifference(Color color1, Color color2) {
		long redmean = (color1.getRed() + color2.getRed()) / 2;
		long r = color1.getRed() - color2.getRed();
		long g = color1.getGreen() - color2.getGreen();
		long b = color1.getBlue() - color2.getBlue();
		return Math.sqrt((((512 + redmean) * r * r) >> 8) + 4 * g * g + (((767 - redmean) * b * b) >> 8));
	}

	public static int colorClosestToDye(int intColor) {
		Color target = colorFromInt(intColor);

		double minDifference = Double.MAX_VALUE;
		int closestColor = -1;

		for (DyeColor dye : DyeColor.values()) {
			Color dyeColor = colorFromInt(dye.getTextureDiffuseColor());
			double difference = colorDifference(target, dyeColor);

			if (difference < minDifference) {
				minDifference = difference;
				closestColor = dyeColor.getRGB();
			}
		}

		return closestColor;
	}
}
