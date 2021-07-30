package tools.skyblock.skyhouse.mcmod.overlays.price;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.managers.DataManager;
import tools.skyblock.skyhouse.mcmod.overlays.OverlayBase;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class BitsOverlay extends OverlayBase {

    public BitsOverlay() {
//        SkyhouseMod.INSTANCE.getOverlayManager().overlays.put(getClass(), this);
    }

    public void render(int mouseX, int mouseY) {
        if (!shouldRender()) return;

        List<ItemStack> items = getItems();
        HashMap<Integer, String> toDisplay = new HashMap<>();
        int drawI = 0;
        for (int i = 0; i < 56; i++) {
            if (items.get(i) == null || items.get(i).getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane)) continue;
            List<String> lore = Arrays.asList(Utils.getLoreFromNBT(items.get(i).getTagCompound()));
            if (lore.contains("\u00a77Cost")) {
                String iName = Utils.getInternalNameFromNBT(items.get(i).getTagCompound());
                if (DataManager.lowestBins.has(iName)) {
                    int bitCost = Integer.parseInt(lore.get(lore.indexOf("\u00a77Cost")+1).toLowerCase()
                            .replace("\u00a7b", "")
                            .replace(",", "")
                            .replace("bits", "")
                            .trim());
                    int lbin = DataManager.lowestBins.get(iName).getAsInt();
                    toDisplay.put(lbin/bitCost, items.get(i).getDisplayName() + " : \u00a76\u00a7l" + NumberFormat.getInstance(Locale.UK).format(lbin/bitCost) + " coins/bit");
                }
            }
        }
        for (Integer price : toDisplay.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
            drawString(fontRendererObj, toDisplay.get(price), 20, 10*drawI+20, 0xffffff);
            drawI++;
        }
    }

    private static List<ItemStack> getItems() {
        GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
        return chest.inventorySlots.getInventory();
    }

    @Override
    public boolean shouldRender() {
        if (!SkyhouseMod.INSTANCE.getConfig().priceOverlayConfig.showBitsOverlay) return false;
        String title = Utils.invGuiName();
        if (title == null || !title.equalsIgnoreCase("community shop")) return false;
        List<ItemStack> items = getItems();
        return items.get(13) != null && items.get(13).getItem() == Item.getItemFromBlock(Blocks.stained_glass_pane) && items.get(13).getMetadata() == 5;
    }

}