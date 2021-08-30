package tools.skyblock.skyhouse.mcmod.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;
import tools.skyblock.skyhouse.mcmod.util.Utils;

public class Auction {

    @SerializedName("item_name")
    @Expose
    private String name;

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("price")
    @Expose
    private int price;

    @SerializedName("resell_price")
    @Expose
    private int resellPrice;

    @SerializedName("profit")
    @Expose
    private int profit;

    @SerializedName("item_bytes")
    @Expose
    private String nbt;

    @SerializedName("recomb")
    @Expose
    private boolean recomb;

    @SerializedName("pet")
    @Expose
    private boolean pet;

    @SerializedName("soul")
    @Expose
    private boolean soul;

    @SerializedName("skin")
    @Expose
    private boolean skin;

    private ItemStack stack;

    public void process() {
        stack = Utils.jsonToItem(Utils.decodeItemBytes(nbt));
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public int getPrice() {
        return price;
    }

    public int getResellPrice() {
        return resellPrice;
    }

    public int getProfit() {
        return profit;
    }

    public String getNbt() {
        return nbt;
    }

    public boolean isRecomb() {
        return recomb;
    }

    public boolean isPet() {
        return pet;
    }

    public boolean isSoul() {
        return soul;
    }

    public boolean isSkin() {
        return skin;
    }

    public ItemStack getStack() {
        return stack;
    }

}
