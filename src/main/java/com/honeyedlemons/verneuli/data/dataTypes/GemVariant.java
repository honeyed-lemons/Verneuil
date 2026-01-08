package com.honeyedlemons.verneuli.data.dataTypes;

import com.honeyedlemons.verneuli.data.dataMaps.BlockMineralDataMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record GemVariant(Optional<Holder<DefaultGemVariant>> parent, Optional<ResourceLocation> entity,
						 ItemStack gemItem, String type, String translation, Optional<Holder<SoundEvent>> talkSound,
						 Optional<List<PaletteData>> palettes, Optional<Map<String, List<String>>> variants,
						 Optional<List<LayerData>> layers, Optional<BlockMineralDataMap.MineralData> crux) {

	public static final Codec<GemVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(DefaultGemVariant.CODEC.optionalFieldOf("parent").forGetter(GemVariant::parent), ResourceLocation.CODEC.optionalFieldOf("entity").forGetter(GemVariant::entity), ItemStack.STRICT_CODEC.fieldOf("gem_item").forGetter(GemVariant::gemItem), Codec.STRING.fieldOf("type").forGetter(GemVariant::type), Codec.STRING.fieldOf("translation").forGetter(GemVariant::translation), SoundEvent.CODEC.optionalFieldOf("talk_sound").forGetter(GemVariant::talkSound), Codec.list(PaletteData.CODEC).optionalFieldOf("palettes").forGetter(GemVariant::palettes), Codec.unboundedMap(Codec.STRING, Codec.list(Codec.STRING)).optionalFieldOf("variants").forGetter(GemVariant::variants), Codec.list(LayerData.CODEC).optionalFieldOf("layers").forGetter(GemVariant::layers), BlockMineralDataMap.MineralData.CODEC.optionalFieldOf("crux").forGetter(GemVariant::crux)).apply(instance, GemVariant::new));

	public static StreamCodec<ByteBuf, GemVariant> DIRECT_STREAM_CODEC = ByteBufCodecs.fromCodec(DIRECT_CODEC);

	public static final Codec<Holder<GemVariant>> CODEC = RegistryFixedCodec.create(VerneuilDataTypes.GEM_VARIANT);

	public static StreamCodec<RegistryFriendlyByteBuf, Holder<GemVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(VerneuilDataTypes.GEM_VARIANT);

	public static final MapCodec<Holder<GemVariant>> MAP_CODEC = MapCodec.assumeMapUnsafe(CODEC);

	public GemVariant() {
		this(Optional.empty(), Optional.empty(), null, null, null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}


	@Override
	public Optional<ResourceLocation> entity() {
		if (this.entity.isEmpty() && parent().isPresent()) {
			return parent().get().value().entity();
		}
		return entity;
	}


	@Override
	public Optional<List<PaletteData>> palettes() {
		if (this.palettes.isEmpty() && parent().isPresent()) {
			return parent().get().value().palettes();
		}
		return palettes;
	}

	@Override
	public Optional<Map<String, List<String>>> variants() {
		if (this.variants.isEmpty() && parent().isPresent()) {
			return parent().get().value().variants();
		}
		if (this.variants.isPresent() && parent().isPresent() && parent().get().value().variants().isPresent()) {
			var parentVariants = parent().get().value().variants().get();
			Map<String, List<String>> newVariants = new HashMap<>();

			this.variants.get().forEach((variantType, list) -> {
				List<String> parentList = parentVariants.get(variantType);
				if (list.isEmpty() && !parentList.isEmpty()) {
					newVariants.put(variantType, parentList);
				}
				else
				{
					newVariants.put(variantType,list);
				}
			});
			return Optional.of(newVariants);
		}
		return variants;
	}

	@Override
	public Optional<List<LayerData>> layers() {
		if (this.layers.isEmpty() && parent().isPresent()) {
			return parent().get().value().layers();
		}
		return layers;
	}

	@Override
	public Optional<Holder<SoundEvent>> talkSound() {
		if (this.talkSound.isEmpty() && parent().isPresent()) {
			return parent().get().value().talkSound();
		}
		return talkSound;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (GemVariant) obj;
		return Objects.equals(this.parent, that.parent) && Objects.equals(this.entity, that.entity) && Objects.equals(this.gemItem, that.gemItem) && Objects.equals(this.type, that.type) && Objects.equals(this.translation, that.translation) && Objects.equals(this.palettes, that.palettes) && Objects.equals(this.variants, that.variants) && Objects.equals(this.layers, that.layers) && Objects.equals(this.crux, that.crux);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parent, entity, gemItem, type, translation, palettes, variants, layers, crux);
	}

	@Override
	public String toString() {
		return "GemVariant[" + "parent=" + parent + ", " + "entity=" + entity + ", " + "gemItem=" + gemItem + ", " + "type=" + type + ", " + "translation=" + translation + ", " + "palettes=" + palettes + ", " + "variants=" + variants + ", " + "layers=" + layers + ", " + "crux=" + crux + ']';
	}

}

