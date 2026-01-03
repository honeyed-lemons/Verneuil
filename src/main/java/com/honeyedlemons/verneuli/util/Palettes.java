package com.honeyedlemons.verneuli.util;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.entities.gems.AbstractGem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Palettes {

    public static int GenerateColorFromPalette(ResourceLocation location, RandomSource random, ServerLevelAccessor server) {
        var color = Color.white.getRGB();
        var resourceManager = Objects.requireNonNull(server.getServer()).getResourceManager();
        var paletteFile = resourceManager.getResource(location);

        if (paletteFile.isEmpty())
            return color;

        BufferedImage palette;

        try
        {
            palette = ImageIO.read(paletteFile.get().open());
        }
        catch (IOException e)
        {
            Verneuil.LOGGER.atError().log("Palette not found at "+location.getPath());
            return color;
        }

        ArrayList<Integer> colors = new ArrayList<>();

        for (int x = 0; x < palette.getWidth(); x++)
            colors.add(palette.getRGB(x,0));

        var randomIndex = random.nextInt(colors.size());

        return colors.get(randomIndex);
    }

    public static ResourceLocation PaletteLocation(AbstractGem gem, String type){
        var gemType = gem.getType().toShortString();
        var gemVariantType = gem.getGemVariant().type();
        return ResourceLocation.fromNamespaceAndPath(Verneuil.MODID,
                "palettes/" + gemType + "/" + gemVariantType+"/" + type + ".png");
    }
}
