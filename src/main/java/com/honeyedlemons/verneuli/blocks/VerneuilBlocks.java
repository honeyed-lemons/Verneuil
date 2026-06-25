package com.honeyedlemons.verneuli.blocks;

import com.honeyedlemons.verneuli.items.VerneuilItems;
import com.honeyedlemons.verneuli.util.DrainUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.honeyedlemons.verneuli.Verneuil.MODID;

public class VerneuilBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

	public static final EnumMap<DrainUtil.DrainedColor, DeferredBlock<?>> DRAINED_STONES =
			new EnumMap<>(DrainUtil.DrainedColor.class);

	public static final EnumMap<DrainUtil.DrainedColor, DeferredBlock<?>> DRAINED_SOILS =
			new EnumMap<>(DrainUtil.DrainedColor.class);

	public static final EnumMap<DrainUtil.DrainedColor, DeferredBlock<?>> DRAINED_DUSTS =
			new EnumMap<>(DrainUtil.DrainedColor.class);

	static {
		for (DrainUtil.DrainedColor color : DrainUtil.DrainedColor.values())
		{
			var name = color.getSerializedName();

			DRAINED_STONES.put(color,registerBlock("drained_stone_"+name,true,
					_ -> new BlockIndexed(propertiesOfCopy(Blocks.STONE,"drained_stone_"+name))));
			DRAINED_SOILS.put(color,registerBlock("drained_soil_"+name,true,
					_ -> new Block(propertiesOfCopy(Blocks.DIRT,"drained_soil_"+name))));
			DRAINED_DUSTS.put(color,registerBlock("drained_dust_"+name,true,
					_ -> new ColoredFallingBlock(new ColorRGBA(Color.WHITE.getRGB()),propertiesOfCopy(Blocks.SAND,"drained_dust_"+name))));
		}
	}

	public static final DeferredBlock<GeodeBlockEntity.GeodeBlock> GEODE = registerBlock("geode",true,
			_ -> new GeodeBlockEntity.GeodeBlock(BlockBehaviour.Properties.of()
					.noOcclusion()
					.setId(getRegistryKey("geode"))));

	private static BlockBehaviour.Properties propertiesOfCopy(Block blockToCopy, String name)
	{
		return BlockBehaviour.Properties.ofFullCopy(blockToCopy).setId(getRegistryKey(name));
	}

	public static ResourceKey<Block> getRegistryKey(String name)
	{
		return ResourceKey.create(Registries.BLOCK,Identifier.fromNamespaceAndPath(MODID, name));
	}

	public static <B extends Block> DeferredBlock<B> registerBlock(String name, boolean item, Function<BlockBehaviour.Properties, ? extends B> func)
	{
		DeferredBlock<B> block = BLOCKS.registerBlock(name,func);
		if (item)
			VerneuilItems.ITEMS.registerSimpleBlockItem(name,block);
		return block;
	}

	public static final Supplier<BlockEntityType<GeodeBlockEntity>> GEODE_ENTITY = BLOCK_ENTITY_TYPES.register("geode_block_entity",
			() -> new BlockEntityType<>(GeodeBlockEntity::new, false, GEODE.get()));

	public static List<Block> blocks() {
		var list = new ArrayList<Block>();
		BLOCKS.getEntries().forEach((block) -> list.add(block.get()));
		return list;
	}
}
