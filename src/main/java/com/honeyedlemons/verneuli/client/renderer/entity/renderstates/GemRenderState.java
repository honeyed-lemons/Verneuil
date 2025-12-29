package com.honeyedlemons.verneuli.client.renderer.entity.renderstates;

import com.honeyedlemons.verneuli.shared.data.dataAttachments.GemAppearanceData;
import com.honeyedlemons.verneuli.shared.data.dataTypes.GemVariant;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import java.util.Map;

public class GemRenderState extends HumanoidRenderState {
    public GemAppearanceData gemAppearanceData;

    public GemVariant gemVariant;
}
