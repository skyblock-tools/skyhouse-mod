package tools.skyblock.skyhouse.mcmod.managers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tools.skyblock.skyhouse.mcmod.config.Checkbox;
import tools.skyblock.skyhouse.mcmod.config.ConfigOption;
import tools.skyblock.skyhouse.mcmod.util.Constants;


public class ConfigManager {

    @Expose
    @SerializedName("show_overlay")
    @Checkbox
    @ConfigOption("Enable auction GUI overlay")
    public boolean showOverlay = false;

    @Expose
    @SerializedName("save_options")
    @Checkbox
    @ConfigOption("Save flip search options")
    public boolean saveOptions;

    @Expose
    @SerializedName("relative_gui")
    @Checkbox
    @ConfigOption("Relative GUI position")
    public boolean relativeGui = true;

    @Expose
    @SerializedName("max_price")
    public int maxPrice = Constants.DEFAULT_MAX_PRICE;

    @Expose
    @SerializedName("min_profit")
    public int minProfit = Constants.DEFAULT_MIN_PROFIT;

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

    public void processConfig() {
        if (!configOpened) {
            setRelativeGui(true);
//            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//            guiScale = 256f / sr.getScaledWidth();
//            guiTop = (sr.getScaledHeight() - Math.round(256f * guiScale)) / 2;
//            guiLeft = Math.round(sr.getScaledWidth() - 256f * (guiScale * sr.getScaledWidth()) / 255f);
//            configOpened = true;
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

    public void setShowOverlay(boolean showOverlay) {
        this.showOverlay = showOverlay;
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
//        return checked || Utils.canDrawOverlay();
        return true;
    }
}
