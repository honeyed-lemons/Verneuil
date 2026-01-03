package com.honeyedlemons.verneuli.client.renderer.entity.renderstates;

import com.honeyedlemons.verneuli.data.dataAttachments.GemAppearanceData;
import com.honeyedlemons.verneuli.data.dataTypes.GemVariant;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class GemRenderState extends HumanoidRenderState {
    public GemAppearanceData gemAppearanceData;

    public GemVariant gemVariant;
}
