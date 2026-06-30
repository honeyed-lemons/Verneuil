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
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.honeyedlemons.verneuli.Verneuil.MODID;

public class VerneuilBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

	public static final Map<DrainUtil.DrainedColorType, DeferredBlock<?>> DRAINED_STONES = new HashMap<>();

	public static final Map<DrainUtil.DrainedColorType, DeferredBlock<?>> DRAINED_SOILS = new HashMap<>();

	public static final Map<DrainUtil.DrainedColorType, DeferredBlock<?>> DRAINED_DUSTS = new HashMap<>();

	static {
		for (DrainUtil.DrainedColorType color : DrainUtil.DrainedColors)
		{
			var name = color.name();

			DRAINED_STONES.put(color,registerBlock("drained_stone_"+name,true,
					_ -> new BlockIndexed(propertiesOfCopy(Blocks.STONE,"drained_stone_"+name)
							.mapColor(color.mapColor()))));
			DRAINED_SOILS.put(color,registerBlock("drained_soil_"+name,true,
					_ -> new Block(propertiesOfCopy(Blocks.DIRT,"drained_soil_"+name)
							.mapColor(color.mapColor()))));
			DRAINED_DUSTS.put(color,registerBlock("drained_dust_"+name,true,
					_ -> new ColoredFallingBlock(color.color(),propertiesOfCopy(Blocks.SAND,"drained_dust_"+name)
							.mapColor(color.mapColor()))));
		}
	}

	public static final DeferredBlock<GeodeBlockEntity.GeodeBlock> GEODE = registerBlock("geode",true,
			_ -> new GeodeBlockEntity.GeodeBlock(BlockBehaviour.Properties.of()
					.noOcclusion()
					.strength(-1.0F, 3600000.0F) // really funny
					.setId(getRegistryKey("geode"))));

	public static final DeferredBlock<InjectorBlockEntity.InjectorBlock> INJECTOR = registerBlock("injector",true,
			_ -> new InjectorBlockEntity.InjectorBlock(BlockBehaviour.Properties.of()
					.noOcclusion()
					.setId(getRegistryKey("injector"))));

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

	public static final Supplier<BlockEntityType<InjectorBlockEntity>> INJECTOR_ENTITY = BLOCK_ENTITY_TYPES.register("injector_block_entity",
			() -> new BlockEntityType<>(InjectorBlockEntity::new, false, INJECTOR.get()));

	public static List<Block> blocks() {
		var list = new ArrayList<Block>();
		BLOCKS.getEntries().forEach((block) -> list.add(block.get()));
		return list;
	}
}
