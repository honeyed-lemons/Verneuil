package com.honeyedlemons.verneuli.blocks;

import net.minecraft.util.StringRepresentable;

public enum DrainedColor implements StringRepresentable {
	Purple("purple"), Red("red"), Blue("blue"), Black("black");

	private final String name;

	DrainedColor(String name) {
		this.name = name;
	}

	public String toString() {
		return this.getSerializedName();
	}

	public String getSerializedName() {
		return this.name;
	}
}
