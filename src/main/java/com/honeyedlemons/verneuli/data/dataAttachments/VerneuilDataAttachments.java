package com.honeyedlemons.verneuli.data.dataAttachments;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.data.dataTypes.GemVariant;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class VerneuilDataAttachments {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Verneuil.MODID);

	// Stream codec
	public static final Supplier<AttachmentType<GemAppearanceData>> GEM_APPEARANCE_DATA = ATTACHMENT_TYPES.register(
			"gem_appearance_data", () -> AttachmentType.builder(GemAppearanceData::new)
					.sync(GemAppearanceData.STREAM_CODEC)
					.serialize(GemAppearanceData.CODEC.fieldOf("gem_appearance_data"))
					.build()
	);

	public static final Supplier<AttachmentType<Holder<GemVariant>>> GEM_VARIANT = ATTACHMENT_TYPES.register(
			"gem_variant", () -> AttachmentType.<Holder<GemVariant>>builder(_t -> {
				throw new IllegalStateException("No default value, use HasData to check beforehand."); })
					.sync(GemVariant.STREAM_CODEC)
					.serialize(GemVariant.CODEC.fieldOf("gem_variant"))
					.build()
	);
}
