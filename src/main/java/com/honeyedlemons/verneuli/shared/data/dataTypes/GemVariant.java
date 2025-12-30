package com.honeyedlemons.verneuli.shared.data.dataTypes;

import com.honeyedlemons.verneuli.shared.data.dataAttachments.GemAppearanceData;
import com.honeyedlemons.verneuli.shared.data.dataMaps.BlockMineralDataMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public final class GemVariant {

	public static final Codec<GemVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DefaultGemVariant.CODEC.optionalFieldOf("parent").forGetter(GemVariant::parent),
			ResourceLocation.CODEC.optionalFieldOf("entity").forGetter(GemVariant::entity),
			ItemStack.STRICT_CODEC.fieldOf("gem_item").forGetter(GemVariant::gemItem),
			Codec.STRING.fieldOf("type").forGetter(GemVariant::type),
			Codec.STRING.fieldOf("translation").forGetter(GemVariant::translation),
			Codec.list(Codec.STRING).optionalFieldOf("palettes").forGetter(GemVariant::palettes),
			Codec.unboundedMap(Codec.STRING, Codec.list(Codec.STRING)).optionalFieldOf("variants").forGetter(GemVariant::variants),
			Codec.list(LayerData.CODEC).optionalFieldOf("layers").forGetter(GemVariant::layers),
			BlockMineralDataMap.MineralData.CODEC.optionalFieldOf("crux").forGetter(GemVariant::crux)
	).apply(instance, GemVariant::new));

	public static StreamCodec<ByteBuf, GemVariant> DIRECT_STREAM_CODEC = ByteBufCodecs.fromCodec(DIRECT_CODEC);

	public static final Codec<Holder<GemVariant>> CODEC = RegistryFixedCodec.create(VerneuilDataTypes.GEM_VARIANT);

	public static StreamCodec<RegistryFriendlyByteBuf, Holder<GemVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(VerneuilDataTypes.GEM_VARIANT);

	public static final MapCodec<Holder<GemVariant>> MAP_CODEC = MapCodec.assumeMapUnsafe(CODEC);

	private Optional<Holder<DefaultGemVariant>> parent;
	private final Optional<ResourceLocation> entity;
	private final ItemStack gemItem;
	private final String type;
	private final String translation;
	private final Optional<List<String>> palettes;
	private final Optional<Map<String, List<String>>> variants;
	private final Optional<List<LayerData>> layers;
	private final Optional<BlockMineralDataMap.MineralData> crux;

	public GemVariant(
			Optional<Holder<DefaultGemVariant>> parent,
			Optional<ResourceLocation> entity,
			ItemStack gemItem,
			String type,
			String translation,
			Optional<List<String>> palettes,
			Optional<Map<String, List<String>>> variants,
			Optional<List<LayerData>> layers,
			Optional<BlockMineralDataMap.MineralData> crux) {
		this.parent = parent;
		this.entity = entity;
		this.gemItem = gemItem;
		this.type = type;
		this.translation = translation;
		this.palettes = palettes;
		this.variants = variants;
		this.layers = layers;
		this.crux = crux;
	}

	public GemVariant() {
		this(Optional.empty(), Optional.empty(), null, null, null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	public Optional<Holder<DefaultGemVariant>> parent() {
		return parent;
	}

	public Optional<ResourceLocation> entity() {
		if (this.entity.isEmpty() && parent().isPresent())
		{
			return parent().get().value().entity();
		}
		return entity;
	}

	public ItemStack gemItem() {
		return gemItem;
	}

	public String type() {
		return type;
	}

	public String translation() {
		return translation;
	}

	public Optional<List<String>> palettes() {
		if (this.palettes.isEmpty() && parent().isPresent())
		{
			return parent().get().value().palettes();
		}
		return palettes;
	}

	public Optional<Map<String, List<String>>> variants() {
		if (this.variants.isEmpty() && parent().isPresent())
		{
			return parent().get().value().variants();
		}
		return variants;
	}

	public Optional<List<LayerData>> layers() {
		if (this.layers.isEmpty() && parent().isPresent())
		{
			return parent().get().value().layers();
		}
		return layers;
	}

	public Optional<BlockMineralDataMap.MineralData> crux() {
		return crux;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (GemVariant) obj;
		return Objects.equals(this.parent, that.parent) &&
				Objects.equals(this.entity, that.entity) &&
				Objects.equals(this.gemItem, that.gemItem) &&
				Objects.equals(this.type, that.type) &&
				Objects.equals(this.translation, that.translation) &&
				Objects.equals(this.palettes, that.palettes) &&
				Objects.equals(this.variants, that.variants) &&
				Objects.equals(this.layers, that.layers) &&
				Objects.equals(this.crux, that.crux);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parent, entity, gemItem, type, translation, palettes, variants, layers, crux);
	}

	@Override
	public String toString() {
		return "GemVariant[" +
				"parent=" + parent + ", " +
				"entity=" + entity + ", " +
				"gemItem=" + gemItem + ", " +
				"type=" + type + ", " +
				"translation=" + translation + ", " +
				"palettes=" + palettes + ", " +
				"variants=" + variants + ", " +
				"layers=" + layers + ", " +
				"crux=" + crux + ']';
	}

}

