package tools.skyblock.skyhouse.mcmod.managers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.Checkbox;
import tools.skyblock.skyhouse.mcmod.config.ConfigOption;
import tools.skyblock.skyhouse.mcmod.config.CreationOption;
import tools.skyblock.skyhouse.mcmod.config.FilterOption;
import tools.skyblock.skyhouse.mcmod.util.Constants;


public class ConfigManager {

    @Expose
    @SerializedName("show_flipping_overlay")
    @Checkbox
    @ConfigOption(value = "Enable auction flipping overlay", description = {"\u00a77Enables the auction flipping overlay"})
    public boolean showFlippingOverlay = false;

    @Expose
    @SerializedName("show_creation_overlay")
    @Checkbox
    @ConfigOption(value = "Enable auction creation overlay", description = {"\u00a77Enables the auction creation overlay", "\u00a74This is a work-in-progress feature"})
    public boolean showCreationOverlay = false;

    @Expose
    @SerializedName("full_chroma_mode")
    @Checkbox
    @ConfigOption(value = "Enable chroma in all Skyhouse GUI's", description = {"\u00a77Turns all white and gray text to chroma", "\u00a74This uses SkyblockAddon's chroma shaders,", "\u00a74and as such requires SBA to function"})
    public boolean fullChromaMode = false;

    @Expose
    @SerializedName("save_options")
    @Checkbox
    @ConfigOption(value = "Save flip search options", description = {"\u00a77Whether or not the search options reset when you close the auction house\u00a7r"})
    public boolean saveOptions;

    @Expose
    @SerializedName("relative_gui")
    @Checkbox
    @ConfigOption(value = "Relative GUI position", description = {"\u00a7aOn: \u00a7r\u00a77Overlay GUI is positioned and scaled relative to the screen size\u00a7r",
            "\u00a74Off: \u00a7r\u00a77Overlay GUI is positioned at a fixed location and scale\u00a7r"})
    public boolean relativeGui = true;

    @Expose
    @CreationOption
    @SerializedName("include_hpbs")
    @ConfigOption(value = "Include Hot Potato Books", description = {"\u00a77Whether or not to include Hot/Fuming Potato Books in item value calculation"})
    public boolean includeHpbs = true;

    @Expose
    @CreationOption
    @SerializedName("include_enchants")
    @ConfigOption(value = "Include Enchants", description = {"\u00a77Whether or not to include Enchants in item value calculation", "\u00a74Not currently implemented"})
    public boolean includeEnchants = true;

    @Expose
    @CreationOption
    @SerializedName("include_frags")
    @ConfigOption(value = "Include Frags", description = {"\u00a77Whether or not to include Frags used on fragged items in item value calculation"})
    public boolean includeFrags = true;

    @Expose
    @CreationOption
    @SerializedName("include_master_stars")
    @ConfigOption(value = "Include Master Stars", description = {"\u00a77Whether or not to include Master Stars in item value calculation"})
    public boolean includeMasterStars = true;

    @Expose
    @CreationOption
    @SerializedName("include_reforge")
    @ConfigOption(value = "Include Reforge", description = {"\u00a77Whether or not to include the item's reforge in item value calculation"})
    public boolean includeReforge = true;

    @Expose
    @CreationOption
    @SerializedName("include_reforge_cost")
    @ConfigOption(value = "Include Reforge Cost", description = {"\u00a77Whether or not to include the reforge's cost to apply in item value calculation"})
    public boolean includeReforgeCost = true;

    @Expose
    @CreationOption
    @SerializedName("include_recombs")
    @ConfigOption(value = "Include Recombs", description = {"\u00a77Whether or not to include Recombobulators in item value calculation"})
    public boolean includeRecombs = true;

    @Expose
    @CreationOption
    @SerializedName("include_aow")
    @ConfigOption(value = "Include Art of War", description = {"\u00a77Whether or not to include Art of Wars in item value calculation"})
    public boolean includeAow = true;


    @Expose
    @CreationOption
    @SerializedName("include_skins")
    @ConfigOption(value = "Include Skins", description = {"\u00a77Whether or not to include equipped Skins in item value calculation", "\u00a74Not currently implemented"})
    public boolean includeSkins = true;

    @Expose
    @CreationOption
    @SerializedName("include_amount")
    @ConfigOption(value = "Include Amount", description = {"\u00a77Whether or not to multiply item value by the amount of items there are in item value calculation"})
    public boolean includeAmount = true;

    @Expose
    @CreationOption
    @SerializedName("include_pet_items")
    @ConfigOption(value = "Include Pet Items", description = {"\u00a77Whether or not to include held Pet Items in item value calculation"})
    public boolean includePetItems = true;

    @Expose
    @CreationOption
    @SerializedName("include_pet_candy")
    @ConfigOption(value = "Include Pet Candy", description = {"\u00a77Whether or not to include Pet Candy in item value calculation", "\u00a74Not currently implemented", "", "\u00a7aIf anyone has any clue how this could be calculated please", "\u00a7amessage Septikai#1676 on discord because I have no clue"})
    public boolean includePetCandy = true;

    @Expose
    @SerializedName("max_price")
    public int maxPrice = Constants.DEFAULT_MAX_PRICE;

    @Expose
    @SerializedName("min_profit")
    public int minProfit = Constants.DEFAULT_MIN_PROFIT;

    @Expose
    @SerializedName("search_skins")
    @FilterOption
    @ConfigOption(value = "Skins", description = {"\u00a77Whether or not to include skins in the search"})
    public boolean skinsInSearch = true;

    @Expose
    @SerializedName("search_cake_souls")
    @FilterOption
    @ConfigOption(value = "Cake Souls", description = {"\u00a77Whether or not to include cake souls in the search"})
    public boolean cakeSoulsInSearch = true;

    @Expose
    @SerializedName("search_pets")
    @FilterOption
    @ConfigOption(value = "Pets", description = {"\u00a77Whether or not to include Pets in the search"})
    public boolean petsInSearch = true;

    @Expose
    @SerializedName("search_recombs")
    @FilterOption
    @ConfigOption(value = "Recombs", description = {"\u00a77Whether or not to include recombobulated items in the search"})
    public boolean recombsInSearch = true;

    @Expose
    @SerializedName("gui_left")
    public int guiLeft;

    @Expose
    @SerializedName("gui_top")
    public int guiTop;

    @Expose
    @SerializedName("gui_scale")
    public float guiScale = 1f;

    @Expose
    @SerializedName("config_opened")
    public boolean configOpened;

    public void resetPremiumFeatures() {
        setSkinsInSearch(true);
        setCakeSoulsInSearch(true);
        setPetsInSearch(true);
        setRecombsInSearch(true);
    }

    public void processConfig() {
        if (!configOpened) {
            setRelativeGui(true);
       configOpened = true;
        }
    }


    public void setSaveOptions(boolean saveOptions) {
        this.saveOptions = saveOptions;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setMinProfit(int minProfit) {
        this.minProfit = minProfit;
    }

    public void setShowFlippingOverlay(boolean showFlippingOverlay) {
        this.showFlippingOverlay = showFlippingOverlay;
    }

    public void setShowCreationOverlay(boolean showCreationOverlay) {
        this.showCreationOverlay = showCreationOverlay;
    }

    public void setFullChromaMode(boolean fullChromaMode) {
        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2) {
            this.fullChromaMode = fullChromaMode;
        }
    }

    public boolean useFullChromaMode() {
        return SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2 && this.fullChromaMode;
    }

    public void setRelativeGui(boolean relativeGui) {
        this.relativeGui = relativeGui;

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        guiScale = relativeGui ? 256f / sr.getScaledWidth() : 1;
        guiTop = relativeGui ? (sr.getScaledHeight() - Math.round(256f * guiScale)) / 2 : (sr.getScaledHeight() - 256) / 2;
        guiLeft = relativeGui ?  Math.round(sr.getScaledWidth() - 256f * (guiScale * sr.getScaledWidth()) / 255f) : sr.getScaledWidth() - 266;
        configOpened = true;
    }

    public boolean checkShowOverlay(boolean checked) {
        return true;
    }

    public void setIncludeHpbs(boolean includeHpbs) {
        this.includeHpbs = includeHpbs;
    }

    public void setIncludeEnchants(boolean includeEnchants) {
        this.includeEnchants = includeEnchants;
    }

    public void setIncludeFrags(boolean includeFrags) {
        this.includeFrags = includeFrags;
    }

    public void setIncludeMasterStars(boolean includeMasterStars) {
        this.includeMasterStars = includeMasterStars;
    }

    public void setIncludeReforge(boolean includeReforge) {
        this.includeReforge = includeReforge;
    }

    public void setIncludeReforgeCost(boolean includeReforgeCost) {
        this.includeReforgeCost = includeReforgeCost;
    }

    public void setIncludeRecombs(boolean includeRecombs) {
        this.includeRecombs = includeRecombs;
    }

    public void setIncludeAow(boolean includeAow) {
        this.includeAow = includeAow;
    }

    public void setIncludeSkins(boolean includeSkins) {
        this.includeSkins = includeSkins;
    }

    public void setIncludeAmount(boolean includeAmount) {
        this.includeAmount = includeAmount;
    }

    public void setIncludePetItems(boolean includePetItems) {
        this.includePetItems = includePetItems;
    }

    public void setIncludePetCandy(boolean includePetCandy) {
        this.includePetCandy = includePetCandy;
    }

    public boolean checkIncludeHpbs(boolean checked) {
        return true;
    }

    public boolean checkIncludeEnchants(boolean checked) {
        return true;
    }

    public boolean checkIncludeFrags(boolean checked) {
        return true;
    }

    public boolean checkIncludeMasterStars(boolean checked) {
        return true;
    }

    public boolean checkIncludeReforge(boolean checked) {
        return true;
    }

    public boolean checkIncludeReforgeCost(boolean checked) {
        return includeReforge;
    }

    public boolean checkIncludeRecombs(boolean checked) {
        return true;
    }

    public boolean checkIncludeAow(boolean checked) {
        return true;
    }

    public boolean checkIncludePetSkins(boolean checked) {
        return true;
    }

    public boolean checkIncludeAmount(boolean checked) {
        return true;
    }

    public boolean checkIncludePetItems(boolean checked) {
        return true;
    }

    public boolean checkIncludePetCandy(boolean checked) {
        return true;
    }

    public boolean getIncludeHpbs() {
        return this.includeHpbs;
    }

    public boolean getIncludeEnchants() {
        return this.includeEnchants;
    }

    public boolean getIncludeFrags() {
        return this.includeFrags;
    }

    public boolean getIncludeMasterStars() {
        return this.includeMasterStars;
    }

    public boolean getIncludeReforge() {
        return this.includeReforge;
    }

    public boolean getIncludeReforgeCost() {
        return this.includeReforge && this.includeReforgeCost;
    }

    public boolean getIncludeRecombs() {
        return this.includeRecombs;
    }

    public boolean getIncludeAow() {
        return this.includeAow;
    }

    public boolean getIncludeSkins() {
        return this.includeSkins;
    }

    public boolean getIncludeAmount() {
        return this.includeAmount;
    }

    public boolean getIncludePetItems() {
        return this.includePetItems;
    }

    public boolean getIncludePetCandy() {
        return this.includePetCandy;
    }

    public void setSkinsInSearch(boolean skinsInSearch) {
        this.skinsInSearch = skinsInSearch;
    }

    public boolean checkSkinsInSearch(boolean checked) {
        return true;
    }

    public boolean getSkinsInSearch() {
        return this.skinsInSearch;
    }

    public void setCakeSoulsInSearch(boolean cakeSoulsInSearch) {
        this.cakeSoulsInSearch = cakeSoulsInSearch;
    }

    public boolean checkCakeSoulsInSearch(boolean checked) {
        return true;
    }

    public boolean getCakeSoulsInSearch() {
        return this.cakeSoulsInSearch;
    }

    public void setPetsInSearch(boolean petsInSearch) {
        this.petsInSearch = petsInSearch;
    }

    public boolean checkPetsInSearch(boolean checked) {
        return true;
    }

    public boolean getPetsInSearch() {
        return this.petsInSearch;
    }

    public void setRecombsInSearch(boolean recombsInSearch) {
        this.recombsInSearch = recombsInSearch;
    }

    public boolean checkRecombsInSearch(boolean checked) {
        return true;
    }

    public boolean getRecombsInSearch() {
        return this.recombsInSearch;
    }

}
