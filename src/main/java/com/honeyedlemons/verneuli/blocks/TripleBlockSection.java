package com.honeyedlemons.verneuli.blocks;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

public enum TripleBlockSection implements StringRepresentable {
	UPPER,
	MIDDLE,
	LOWER;

	public String toString() { return this.getSerializedName(); }

	public String getSerializedName() {
		return this == UPPER ? "upper" : this == MIDDLE ? "middle" : "lower";
	}
}