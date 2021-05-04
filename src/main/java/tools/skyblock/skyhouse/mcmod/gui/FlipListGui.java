package tools.skyblock.skyhouse.mcmod.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.models.Auction;
import tools.skyblock.skyhouse.mcmod.util.Resources;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FlipListGui extends CustomGui {


    private List<Auction> auctions;
    private int page = 0;
    int guiLeft, guiTop;
    int lastPageAuctions;
    int totalPages;
    int shownAucs;


    public FlipListGui(JsonArray json) {
        auctions = new ArrayList<>();
        int i = 0;
        for (JsonElement el : json) {
            JsonObject item = el.getAsJsonObject();
            Auction auction = SkyhouseMod.serializeGson.fromJson(item, Auction.class);
            auctions.add(auction);
        }
        processAuctions();
    }


    public void processAuctions() {
        for (Auction auc : auctions) {
            auc.process();
        }
    }

    @Override
    public void tick() {
        super.tick();
        guiLeft = width-256-20;
        guiTop = height/2-128;
        if (auctions.size() == 0) {
            totalPages = 0;
            page = 0;
            shownAucs = 0;
        } else {
            lastPageAuctions = auctions.size() % 4 == 0 ? 4 : auctions.size() % 4;
            totalPages = (int) Math.ceil((double) auctions.size() / 4);

            shownAucs = page == totalPages - 1 ? lastPageAuctions : 4;
        }

    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.AH_OVERLAY_BACKGROUND);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();
        drawTexturedModalRect(width-256-20, height/2-128, 0, 0, 256, 256);
        GlStateManager.enableDepth();
        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "Found " + auctions.size() + " flips! Page " + (page+1) + " of " + totalPages, guiLeft+120, guiTop+20, 0xffffff);

        if (page != 0) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
            drawTexturedModalRect(guiLeft+12, guiTop+10, 0, 0, 16, 16);
        }
        if (page != totalPages-1) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
            drawTexturedModalRect(guiLeft+256-12-16, guiTop+10, 16, 0, 16, 16);
        }
        int i = 0;
        for (Auction auction : auctions.subList(page*4, page*4+shownAucs)) {
            ItemStack toRender = auction.getStack();
            GlStateManager.pushMatrix();
            GlStateManager.scale(2, 2, 2);
            Utils.renderItem(toRender, (guiLeft + 18) / 2, (guiTop - 15 + 55 * ++i) / 2);

            GlStateManager.popMatrix();
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.FLIP_FRAME);
            drawTexturedModalRect(guiLeft + 12, guiTop - 20 + i * 55, 0, 0, 216, 45);
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
            drawTexturedModalRect(guiLeft+12+135, guiTop - 19 + i * 55, 80, 0, 16, 16);
            drawString(Minecraft.getMinecraft().fontRendererObj, auction.getName(), (guiLeft + 57), (guiTop + 5 + 55 * i), 0xffffff);
            drawString(Minecraft.getMinecraft().fontRendererObj, NumberFormat.getNumberInstance(Locale.UK).format(auction.getProfit()),
                    guiLeft + 60, guiTop - 15 + 55 * i, 0xffffff);
        }
        i = 0;
        for (Auction auction : auctions.subList(page*4, page*4+shownAucs)) {
            ItemStack toRender = auction.getStack();
            if (hover(mouseX, mouseY, guiLeft+18, guiTop-15+55*++i, 32, 32))
                toRender.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
            if (hover(mouseX, mouseY, guiLeft+12+194, guiTop-20+i*55+2, 15, 15))
                drawHoveringText(Arrays.asList(
                        EnumChatFormatting.GREEN + "Price: " + NumberFormat.getNumberInstance(Locale.UK).format(auction.getPrice()),
                        EnumChatFormatting.GREEN + "Resell: " + NumberFormat.getNumberInstance(Locale.UK).format(auction.getPrice() + auction.getProfit()),
                        EnumChatFormatting.GREEN + "Profit: " + NumberFormat.getNumberInstance(Locale.UK).format(auction.getProfit()) + "" + EnumChatFormatting.RESET
                ), mouseX, mouseY, Minecraft.getMinecraft().fontRendererObj);
        }


        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void click(int mouseX, int mouseY) {
        int start = 4 * page;
        for (int i = 1; i < shownAucs+1; i++) {
            if (hover(mouseX, mouseY, guiLeft+12+135, guiTop - 19 + i * 55, 16, 16))
                auctions.remove(start + i - 1);
            else if (hover(mouseX, mouseY, guiLeft+12, guiTop - 20 + i * 55, 216, 45))
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/viewauction " + auctions.get(start + i - 1).getUuid());
        }
        if (page != 0 && hover(mouseX, mouseY, guiLeft+12, guiTop+10, 16, 16)) page--;
        else if (page != totalPages-1 && hover(mouseX, mouseY, guiLeft+256-12-16, guiTop+10, 16, 16)) page++;
    }

    @Override
    public void keyEvent() {

    }

}
