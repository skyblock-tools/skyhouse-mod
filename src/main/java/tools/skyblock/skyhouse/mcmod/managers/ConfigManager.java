package tools.skyblock.skyhouse.mcmod.managers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import tools.skyblock.skyhouse.mcmod.config.Checkbox;
import tools.skyblock.skyhouse.mcmod.config.ConfigOption;
import tools.skyblock.skyhouse.mcmod.util.Constants;
import tools.skyblock.skyhouse.mcmod.util.Utils;


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
    @SerializedName("max_price")
    public int maxPrice = Constants.DEFAULT_MAX_PRICE;

    @Expose
    @SerializedName("min_profit")
    public int minProfit = Constants.DEFAULT_MIN_PROFIT;

    public void processConfig() {

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

    public boolean checkShowOverlay(boolean checked) {
        return checked || Utils.canDrawOverlay();
    }
}
