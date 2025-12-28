package com.honeyedlemons.verneuli.shared.data.saveddata;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.shared.entities.gems.AbstractGem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GemSavedData extends SavedData {

    public HashMap<UUID,CompoundTag> gemData;


    public static final Codec<GemSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(UUIDUtil.STRING_CODEC,CompoundTag.CODEC).fieldOf("gem_data").forGetter(data -> data.gemData)
    ).apply(instance, GemSavedData::new));

    public static final SavedDataType<GemSavedData> ID = new SavedDataType<>(
            // The identifier of the saved data
            // Used as the path within the level's `data` folder
            "gem_data",
            // The initial constructor
            GemSavedData::new,
            // The codec used to serialize the data
            CODEC
    );
    private GemSavedData() {
        this(new HashMap<>());
    }

    private GemSavedData(Map<UUID, CompoundTag> gemData) {
        this.gemData = new HashMap<>(gemData);
    }

    public Map<UUID,CompoundTag> getGemData()
    {
        return gemData;
    }

    public ValueInput getGem(UUID uuid, RegistryAccess access)
    {
        try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(() -> "gemSavedData", Verneuil.LOGGER)) {
            var tag = this.gemData.get(uuid);
            return TagValueInput.create(problemreporter$scopedcollector, access, tag);

        }
    }

    public void addGem(UUID uuid, TagValueOutput output)
    {
        CompoundTag tag = output.buildResult();
        this.gemData.put(uuid, tag);
    }

    public void removeGem(UUID uuid)
    {
        this.gemData.remove(uuid);
        this.setDirty();
    }
    public void removeGem(AbstractGem gem)
    {
        removeGem(gem.getUUID());
    }
}
