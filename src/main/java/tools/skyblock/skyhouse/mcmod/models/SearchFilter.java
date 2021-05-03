package tools.skyblock.skyhouse.mcmod.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import tools.skyblock.skyhouse.mcmod.util.Constants;

public class SearchFilter {

    @SerializedName("minProfit")
    @Expose
    public int minProfit = Constants.DEFAULT_MIN_PROFIT;

    @SerializedName("maxPrice")
    @Expose
    public int maxPrice = Constants.DEFAULT_MAX_PRICE;

    public SearchFilter withMinProfit(int minProfit) {
        this.minProfit = minProfit;
        return this;
    }

    public SearchFilter withMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }
}
