package com.honeyedlemons.verneuli.datagen;

import com.honeyedlemons.verneuli.Verneuil;
import com.honeyedlemons.verneuli.blocks.VerneuilBlocks;
import com.honeyedlemons.verneuli.entities.VerneuilEntities;
import com.honeyedlemons.verneuli.items.VerneuilItems;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.commons.lang3.StringUtils;
@EventBusSubscriber(modid = Verneuil.MODID)
public class VerneuilLanguageProvider extends LanguageProvider {
	public VerneuilLanguageProvider(PackOutput output) {
		super(output, Verneuil.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		// Blocks
		VerneuilBlocks.DRAINED_STONES.forEach((color, block) ->
				this.add(block.get(), StringUtils.capitalize(color.name()+" Drained Stone")));

		VerneuilBlocks.DRAINED_SOILS.forEach((color, block) ->
				this.add(block.get(), StringUtils.capitalize(color.name()) +" Drained Soil"));

		VerneuilBlocks.DRAINED_DUSTS.forEach((color, block) ->
				this.add(block.get(), StringUtils.capitalize(color.name()+" Drained Dust")));

		this.add(VerneuilBlocks.GEODE.get(),"Geode");
		this.add(VerneuilBlocks.INJECTOR.get(),"Injector");


		// Entity Types
		this.add(VerneuilEntities.QUARTZ.get(),"Quartz");

		// Gem Variants
		this.addGemVariant("amethyst","Amethyst");
		this.addGemVariant("carnelian","Carnelian");
		this.addGemVariant("rose_quartz","Rose Quartz");
		this.addGemVariant("citrine","Citrine");
		this.addGemVariant("red_striped_jasper","Red Striped Jasper");
		this.addGemVariant("ocean_jasper","Ocean Jasper");
		this.addGemVariant("zebra_jasper","Zebra Jasper");
		this.addGemVariant("biggs_jasper","Biggs Jasper");

		// Items
		this.add(VerneuilItems.AMETHYST_GEM.get(),"Amethyst Gen");
		this.add(VerneuilItems.CARNELIAN_GEM.get(),"Carnelian Gen");
		this.add(VerneuilItems.ROSE_QUARTZ_GEM.get(),"Rose Quartz Gen");
		this.add(VerneuilItems.CITRINE_GEM.get(),"Citrine Gen");
		this.add(VerneuilItems.RED_STRIPED_JASPER_GEM.get(),"Red Striped Jasper Gen");
		this.add(VerneuilItems.OCEAN_JASPER_GEM.get(),"Ocean Jasper Gen");
		this.add(VerneuilItems.ZEBRA_JASPER_GEM.get(),"Zebra Jasper Gen");
		this.add(VerneuilItems.BIGGS_JASPER_GEM.get(),"Biggs Jasper Gen");

		this.add(VerneuilItems.GEM_SEED.asItem(),"Gem Seed");

		// Gem Messages
		this.addGemMessage("tame","Claimed %s!");
		this.addGemMessage("movement_0","%s is wandering.");
		this.addGemMessage("movement_1","%s will stay still.");
		this.addGemMessage("movement_2","%s will follow you.");

		this.addGemMessage("gem_item_warning","This gem is either already in the world, or no longer exists, please delete this item!");

		// Misc
		this.addMisc("commands.randomize.success.single","Randomized %s's appearance!");
		this.addMisc("commands.randomize.success.multiple","Randomized %s gems' appearances!");
		this.addMisc("commands.randomize.failure","Failed randomizing appearance, are they not a gem?");

		this.addMisc("configuration.title","Verneuil Configs");
		this.addMisc("configuration.section.verneuil.common.toml","Verneuil Configs");
		this.addMisc("configuration.section.verneuil.common.toml.title","Verneuil Configs");
		this.addMisc("configuration.canPickUp","Gem Equipment Auto Pickup");
		this.addMisc("configuration.incubationTime","Incubation Tick Rate");
		this.addMisc("configuration.injectionDepth","Max Incubation Depth");
	}

	public void addMisc(String key, String name)
	{
		add(Verneuil.MODID+"."+key, name);
	}

	public void addGemVariant(String key, String name) {
		add(Verneuil.MODID+".gem_variant."+key, name);
	}

	public void addGemMessage(String key, String name) {
		add(Verneuil.MODID+".gem_message."+key, name);
	}
}
