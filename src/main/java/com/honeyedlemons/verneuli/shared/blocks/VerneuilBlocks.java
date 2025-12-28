package com.honeyedlemons.verneuli.shared.blocks;

import com.honeyedlemons.verneuli.shared.items.VerneuilItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.honeyedlemons.verneuli.Verneuil.MODID;

public class VerneuilBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredBlock<Block> DRAINED_BLOCK = BLOCKS.registerBlock("drained_block",
            Block::new);

    public static final DeferredBlock<GeodeBlockEntity.GeodeBlock> GEODE =
            BLOCKS.registerBlock("geode", GeodeBlockEntity.GeodeBlock::new);
    public static final DeferredItem<BlockItem> DRAINED_BLOCK_ITEM = VerneuilItems.ITEMS.registerSimpleBlockItem("drained_block", DRAINED_BLOCK);
    public static final DeferredItem<BlockItem> GEODE_ITEM = VerneuilItems.ITEMS.registerSimpleBlockItem("geode", GEODE);


    public static final Supplier<BlockEntityType<GeodeBlockEntity>> GEODE_ENTITY = BLOCK_ENTITY_TYPES.register(
            "my_block_entity",
            // The block entity type.
            () -> new BlockEntityType<>(
                    GeodeBlockEntity::new,
                    false,
                    GEODE.get()
            )
    );
}
