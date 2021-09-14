package tools.skyblock.skyhouse.mcmod.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.Loader;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.annotations.*;
import tools.skyblock.skyhouse.mcmod.util.Constants;

import java.lang.reflect.Field;

public class SkyhouseConfig {

    public void processConfig() {
        if (!configOpened) {
            ahOverlayConfig.setRelativeGui(true);
            configOpened = true;
        }
    }

    public void resetPremiumFeatures() {
        filterOptions.skins = true;
        filterOptions.pets = true;
        filterOptions.recombs = true;
        filterOptions.souls = true;
        filterOptions.enchBooks = true;
        generalConfig.fullChromaMode = false;
    }

    public Field openCategory;

    @ConfigCategory(name = "General", description = "General config")
    @SerializedName("general")
    @Expose
    public GeneralConfig generalConfig = new GeneralConfig();

    @ConfigCategory(name = "AH Overlays", description = "Configure the AH overlays")
    @SerializedName("ah_overlay")
    @Expose
    public AhOverlayConfig ahOverlayConfig = new AhOverlayConfig();

    @ConfigCategory(name = "Price Overlays", description = "Miscellaneous price overlays")
    @SerializedName("price_overlays")
    @Expose
    public PriceOverlayOptions priceOverlayConfig = new PriceOverlayOptions();

    @Expose
    @SerializedName("filter_options")
    public FilterOptions filterOptions = new FilterOptions();

    @Expose
    @SerializedName("creation_options")
    public CreationOptions creationOptions = new CreationOptions();

    @Expose
    @SerializedName("config_opened")
    public boolean configOpened = false;

    public class GeneralConfig {

        @Expose
        @SerializedName("full_chroma_mode")
        @ConfigOption(value = "Enable chroma in all Skyhouse GUIs", description = {"\u00a77Turns all white and gray text to chroma",
                "\u00a74This uses SkyblockAddon's chroma shaders, and", "\u00a74as such it requires SBA 1.6.0 or higher to function"}, premium = true)
        public boolean fullChromaMode = false;

        public boolean checkFullChromaMode(boolean checked) {
            return SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2 && Loader.isModLoaded("skyblockaddons");
        }

    }

    public class AhOverlayConfig {

        @Expose
        @SerializedName("gui_scale")
        public float guiScale = 0.5f;

        @Expose
        @SerializedName("gui_left")
        public int guiLeft = 0;

        @Expose
        @SerializedName("gui_top")
        public int guiTop = 0;

        @Expose
        @SerializedName("show_flipping_overlay")
        @ConfigOption(value = "Enable auction flipping overlay", description = {"\u00a77Enables the auction flipping overlay"})
        public boolean showFlippingOverlay = false;

        @Expose
        @SerializedName("show_creation_overlay")
        @ConfigOption(value = "Enable auction creation overlay", description = {"\u00a77Enables the auction creation overlay", "\u00a74This is a work-in-progress feature"})
        public boolean showCreationOverlay = false;

        @Expose
        @SerializedName("save_options")
        @ConfigOption(value = "Save flip search options", description = {"\u00a77Whether or not the search options reset when you close the auction house\u00a7r"})
        public boolean saveOptions = true;

        @Expose
        @SerializedName("relative_gui")
        @ConfigOption(value = "Relative GUI position", description = {"\u00a7aOn: \u00a7r\u00a77Overlay GUI is positioned and scaled relative to the screen size\u00a7r",
                "\u00a74Off: \u00a7r\u00a77Overlay GUI is positioned at a fixed location and scale\u00a7r"})
        public boolean relativeGui = true;

        @ConfigOption(value = "Edit GUI position", description = {"\u00a77Edit the position of the overlay\u00a7r"})
        @CommandButton(value = "skyhouseeditahoverlay", label = "Edit")
        public int editGuiPos;

        public void setRelativeGui(boolean relativeGui) {
            this.relativeGui = relativeGui;
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            guiScale = relativeGui ? (255f * guiScale) / sr.getScaledWidth() : guiScale * sr.getScaledWidth() / 255f;
            guiTop = relativeGui ? (sr.getScaledHeight() - Math.round(256f * guiScale)) / 2 : (sr.getScaledHeight() - 256) / 2;
            guiLeft = relativeGui ? Math.round(sr.getScaledWidth() - 256f * (guiScale * sr.getScaledWidth()) / 255f) : Math.round(sr.getScaledWidth() - 256 * guiScale);
            configOpened = true;
        }

    }


    public class PriceOverlayOptions {

        @Expose
        @SerializedName("show_bits_overlays")
        @ConfigOption(value = "Enable bits price overlay", description = {"\u00a77Enables the bits price overlay\u00a7r",
        "\u00a7aDisplays the coins : bit ratio of each item in the bits shop\u00a7r"})
        public boolean showBitsOverlay = false;

    }

    public class FilterOptions {

        @Expose
        @SerializedName("skins")
        @ItemFilter("skins")
        @HiddenConfigOption(value = "Skins", description = {"\u00a77Whether or not to include skins in the search"})
        public boolean skins = true;

        @Expose
        @SerializedName("pets")
        @ItemFilter("pets")
        @HiddenConfigOption(value = "Pets", description = {"\u00a77Whether or not to include Pets in the search"})
        public boolean pets = true;

        @Expose
        @SerializedName("recombs")
        @ItemFilter("recombs")
        @HiddenConfigOption(value = "Recombs", description = {"\u00a77Whether or not to include recombobulated items in the search"})
        public boolean recombs = true;

        @Expose
        @SerializedName("souls")
        @ItemFilter("souls")
        @HiddenConfigOption(value = "Cake Souls", description = {"\u00a77Whether or not to include cake souls in the search"})
        public boolean souls = true;

        @Expose
        @SerializedName("ench_books")
        @ItemFilter("ench_books")
        @HiddenConfigOption(value = "Ench Books", description = {"\u00a77Whether or not to include enchanted books in the search"})
        public boolean enchBooks = true;

        @Expose
        @SerializedName("max_price")
        public int maxPrice = Constants.DEFAULT_MAX_PRICE;

        @Expose
        @SerializedName("min_profit")
        public int minProfit = Constants.DEFAULT_MIN_PROFIT;

        public boolean checkSkins(boolean checked) {
            return SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2;
        }

        public boolean checkSouls(boolean checked) {
            return SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2;
        }

        public boolean checkPets(boolean checked) {
            return SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2;
        }

        public boolean checkRecombs(boolean checked) {
            return SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2;
        }

        public boolean checkEnchBooks(boolean checked) {
            return SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2;
        }

        public void setMaxPrice(int price) {
            maxPrice = price;
        }

        public void setMinProfit(int profit) {
            minProfit = profit;
        }

    }

    public class CreationOptions {

        @Expose
        @HiddenConfigOption(value = "Include Hot Potato Books", description = {"\u00a77Whether or not to include Hot/Fuming Potato Books in item value calculation"})
        @SerializedName("include_hpbs")
        public boolean includeHpbs = true;

        @Expose
        @HiddenConfigOption(value = "Include Enchants", description = {"\u00a77Whether or not to include Enchants in item value calculation", "\u00a74Not currently implemented"})
        @SerializedName("include_enchants")
        public boolean includeEnchants = true;

        @Expose
        @HiddenConfigOption(value = "Include Frags", description = {"\u00a77Whether or not to include Frags used on fragged items in item value calculation"})
        @SerializedName("include_frags")
        public boolean includeFrags = true;

        @Expose
        @HiddenConfigOption(value = "Include Master Stars", description = {"\u00a77Whether or not to include Master Stars in item value calculation"})
        @SerializedName("include_master_stars")
        public boolean includeMasterStars = true;

        @Expose
        @HiddenConfigOption(value = "Include Reforge", description = {"\u00a77Whether or not to include the item's reforge in item value calculation"})
        @SerializedName("include_reforge")
        public boolean includeReforge = true;

        @Expose
        @HiddenConfigOption(value = "Include Reforge Cost", description = {"\u00a77Whether or not to include the reforge's cost to apply in item value calculation"})
        @SerializedName("include_reforge_cost")
        public boolean includeReforgeCost = true;

        @Expose
        @HiddenConfigOption(value = "Include Recombs", description = {"\u00a77Whether or not to include Recombobulators in item value calculation"})
        @SerializedName("include_recombs")
        public boolean includeRecombs = true;

        @Expose
        @HiddenConfigOption(value = "Include Art of War", description = {"\u00a77Whether or not to include Art of Wars in item value calculation"})
        @SerializedName("include_aow")
        public boolean includeAow = true;


        @Expose
        @HiddenConfigOption(value = "Include Skins", description = {"\u00a77Whether or not to include equipped Skins in item value calculation", "\u00a74Not currently implemented"})
        @SerializedName("include_skins")
        public boolean includeSkins = true;

        @Expose
        @HiddenConfigOption(value = "Include Amount", description = {"\u00a77Whether or not to multiply item value by the amount of items there are in item value calculation"})
        @SerializedName("include_amount")
        public boolean includeAmount = true;

        @Expose
        @HiddenConfigOption(value = "Include Pet Items", description = {"\u00a77Whether or not to include held Pet Items in item value calculation"})
        @SerializedName("include_pet_items")
        public boolean includePetItems = true;

        @Expose
        @HiddenConfigOption(value = "Include Pet Candy", description = {"\u00a77Whether or not to include Pet Candy in item value calculation", "\u00a74Not currently implemented", "", "\u00a7aIf anyone has any clue how this could be calculated please", "\u00a7amessage Septikai#1676 on discord because I have no clue"})
        @SerializedName("include_pet_candy")
        public boolean includePetCandy = true;

        public boolean checkIncludeReforgeCost(boolean checked) {
            return includeReforge;
        }

    }

}
