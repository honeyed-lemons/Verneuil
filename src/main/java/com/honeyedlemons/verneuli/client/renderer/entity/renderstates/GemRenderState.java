package com.honeyedlemons.verneuli.client.renderer.entity.renderstates;

import com.honeyedlemons.verneuli.shared.data.datatypes.GemVariant;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import java.util.Map;

public class GemRenderState extends HumanoidRenderState {
    public Map<String,Integer> colors;

    public Map<String,String> layerVariants;

    public GemVariant gemVariant;
}
