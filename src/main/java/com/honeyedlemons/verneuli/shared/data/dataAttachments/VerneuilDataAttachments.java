package com.honeyedlemons.verneuli.shared.data.dataAttachments;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.shared.data.dataTypes.GemVariant;
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
					.build()
	);

	public static final Supplier<AttachmentType<GemVariant>> GEM_VARIANT = ATTACHMENT_TYPES.register(
			"gem_variant", () -> AttachmentType.builder(GemVariant::new)
					.sync(GemVariant.DIRECT_STREAM_CODEC)
					.build()
	);


}
