package tools.skyblock.skyhouse.mcmod.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.SkyhouseConfig;
import tools.skyblock.skyhouse.mcmod.config.annotations.ItemFilter;
import tools.skyblock.skyhouse.mcmod.util.Constants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SearchFilter {

    @SerializedName("min_profit")
    @Expose
    public int minProfit = Constants.DEFAULT_MIN_PROFIT;

    @SerializedName("max_price")
    @Expose
    public int maxPrice = Constants.DEFAULT_MAX_PRICE;

    @SerializedName("min_quantity")
    @Expose
    public int houseQuantity = Constants.DEFAULT_HOUSE_QUANTITY;

    @SerializedName("type")
    @Expose
    public int auctionType = Constants.DEFAULT_AUCTION_TYPE_VALUE;

    @SerializedName("sort")
    @Expose
    public int auctionSort = Constants.DEFAULT_AUCTION_SORT_VALUE;

    @SerializedName("item_filter")
    @Expose
    public int itemFilter = 0;

    @SerializedName("serve_nbt")
    @Expose
    public boolean serve_nbt = true;

    public SearchFilter withMinProfit(int minProfit) {
        this.minProfit = minProfit;
        return this;
    }

    public SearchFilter withMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public SearchFilter withHouseQuantity(int houseQuantity) {
        this.houseQuantity = houseQuantity;
        return this;
    }

    public SearchFilter withAuctionType() {
        switch (SkyhouseMod.INSTANCE.getConfig().filterOptions.auctionType) {
            case "All Auctions": this.auctionType = 3; break;
            case "Auction Only": this.auctionType = 2; break;
            case "Bin Only": this.auctionType = 1; break;
        }
        return this;
    }

    public SearchFilter withAuctionSort() {
        switch (SkyhouseMod.INSTANCE.getConfig().filterOptions.auctionSort) {
            case "Highest Profit": this.auctionSort = 1; break;
            case "Lowest Price": this.auctionSort = 3; break;
            case "Profit Proportion": this.auctionSort = 2; break;
            case "AH Quantity": this.auctionSort = 4; break;
        }
        return this;
    }

    public List<Boolean> getItemFilters() {
        List<Boolean> itemFilters = new ArrayList<>();
        for (Field field : SkyhouseConfig.FilterOptions.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ItemFilter.class)) continue;
            try {
                itemFilters.add(field.getBoolean(SkyhouseMod.INSTANCE.getConfig().filterOptions));
            } catch (ReflectiveOperationException ignored) {}
        }
        return itemFilters;
    }

    public SearchFilter withItemFilter() {
        List<Boolean> itemFilters = getItemFilters();
        this.itemFilter = 0;
        for (int i = 0; i < itemFilters.size(); i++) {
            if (!itemFilters.get(i)) {
                this.itemFilter |= (int) Math.pow(2, i);
            }
        }
        return this;
    }
}
