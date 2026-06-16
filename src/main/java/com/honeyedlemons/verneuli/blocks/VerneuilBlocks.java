package com.honeyedlemons.verneuli.blocks;

import com.honeyedlemons.verneuli.items.VerneuilItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.honeyedlemons.verneuli.Verneuil.MODID;

public class VerneuilBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

	public static final DeferredBlock<Block> DRAINED_STONE = registerBlock("drained_stone",true,
			properties -> new Block(BlockBehaviour.Properties
					.ofFullCopy(Blocks.STONE)
					.setId(getRegistryKey("drained_stone"))));

	public static final DeferredBlock<Block> DRAINED_SOIL = registerBlock("drained_soil",true,
			properties -> new Block(BlockBehaviour.Properties
					.ofFullCopy(Blocks.DIRT)
					.setId(getRegistryKey("drained_soil"))));
	public static final DeferredBlock<Block> DRAINED_DUST = registerBlock("drained_dust",true,
			properties -> new SandBlock(new ColorRGBA(6905203), BlockBehaviour.Properties
					.ofFullCopy(Blocks.SAND)
					.setId(getRegistryKey("drained_dust"))));

	public static final DeferredBlock<GeodeBlockEntity.GeodeBlock> GEODE = registerBlock("geode",true, GeodeBlockEntity.GeodeBlock::new);

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
