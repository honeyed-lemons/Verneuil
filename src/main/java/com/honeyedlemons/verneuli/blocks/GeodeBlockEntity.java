package com.honeyedlemons.verneuli.blocks;

import com.google.common.util.concurrent.AtomicDouble;
import com.honeyedlemons.verneuli.data.dataMaps.BlockMineralDataMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class GeodeBlockEntity extends BlockEntity {

    public GeodeBlockEntity(BlockPos pos, BlockState blockState) {
        super(VerneuilBlocks.GEODE_ENTITY.get(), pos, blockState);
    }

    private final static Map<String,Float> crux = Map.of(
            "Silica",.5f,
            "Carbon", .5f
    );
    private final StoredMineralData geologicalData = new StoredMineralData(new HashMap<>());

    public static class GeodeBlock extends Block implements EntityBlock {
        public GeodeBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return new GeodeBlockEntity(pos, state);
        }

        @Override
        protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
            if (!(level.getBlockEntity(pos) instanceof GeodeBlockEntity Geode) || level.isClientSide())
                return InteractionResult.FAIL;

            if (player.isCrouching()) {
                var ratio = GetRatio(Geode.geologicalData.data);
                var difference = GetSimilarity(crux, ratio);
                player.displayClientMessage(Component.literal(difference.toString()), true);
                return InteractionResult.SUCCESS;
            }
            
            Holder<Block> holder = level.getBlockState(pos.above()).getBlockHolder();
            var blocKMineralData = holder.getData(BlockMineralDataMap.MINERAL_DATA);
            if (blocKMineralData != null) {
                blocKMineralData.data().forEach((key, value) -> {
                    Geode.geologicalData.addData(new Pair<>(key,value));
                });
                Geode.setChanged();
            }
            
            return InteractionResult.SUCCESS;
        }


        public Map<String,Float> GetRatio(Map<String,Float> map)
        {
            var hashmap = new HashMap<String,Float>();

            Float totalCount = 0f;

            for (Map.Entry<String, Float> entry : map.entrySet())
                totalCount += entry.getValue();

            for (Map.Entry<String, Float> entry : map.entrySet())
                hashmap.put(entry.getKey(), entry.getValue() / totalCount);

            return hashmap;
        }

        public Float GetSimilarity(Map<String,Float> gemCrux, Map<String,Float> ratio)
        {
            AtomicReference<List<Float>> differences = new AtomicReference<>(new ArrayList<>());

            for (Map.Entry<String,Float> entry : ratio.entrySet())
            {
                var key = entry.getKey();
                var value = entry.getValue();
                var gemCruxValue = gemCrux.get(key);
                if (gemCruxValue == null)
                    continue;
                var total = (Math.abs(value - gemCruxValue)/((value + gemCruxValue)/2));
                differences.get().add(total);
            }
            
            var total = new AtomicDouble();
            for (Float difference : differences.get())
                total.addAndGet(difference);
            
            return (total.floatValue() / differences.get().size());
        }

    }

    static public class StoredMineralData {

        Map<String, Float> data;
        public StoredMineralData(Map<String, Float> data) {
            this.data = data;
        }

        public Map<String, Float> getData() {
            return data;
        }

        public void addData(Pair<String, Float> data) {
            if (this.data.containsKey(data.getFirst()))
            {
                var oldValue = this.data.get(data.getFirst());
                this.data.replace(data.getFirst(), oldValue + data.getSecond());
            }
            else this.data.put(data.getFirst(), data.getSecond());
        }
        public void setData(StoredMineralData data) {
            this.data = new HashMap<>(data.data);
        }
        public static final Codec<StoredMineralData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("stored_data").forGetter(StoredMineralData::getData)
                ).apply(instance, StoredMineralData::new)
        );

    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        input.read("geo_data",StoredMineralData.CODEC).ifPresent(this.geologicalData::setData);
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.store("geo_data", StoredMineralData.CODEC, this.geologicalData);
    }
}
