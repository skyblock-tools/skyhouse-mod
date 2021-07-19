package tools.skyblock.skyhouse.mcmod.overlays.ah;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.CustomGui;
import tools.skyblock.skyhouse.mcmod.gui.ConfigGui;
import tools.skyblock.skyhouse.mcmod.models.Auction;
import tools.skyblock.skyhouse.mcmod.models.SearchFilter;
import tools.skyblock.skyhouse.mcmod.util.Resources;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FlipListGui extends CustomGui {


    private SearchFilter filter;
    private List<Auction> auctions;
    private int page = 0;
    private int guiLeft, guiTop;
    private float guiScale;
    private int lastPageAuctions;
    private int totalPages;
    private int shownAucs;


    public FlipListGui(JsonArray json, SearchFilter searchFilter) {
        filter = searchFilter;
        auctions = new ArrayList<>();
        int i = 0;
        for (JsonElement el : json) {
            JsonObject item = el.getAsJsonObject();
            Auction auction = SkyhouseMod.serializeGson.fromJson(item, Auction.class);
            if (!SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.contains(auction.getUuid())){
                auctions.add(auction);
            }
        }
        processAuctions();
        initGui();
    }

    public void processAuctions() {
        for (Auction auction : auctions) {
            auction.process();
        }
    }

    @Override
    public void initGui() {
        tick();
        guiScale = Utils.getScaleFactor();
    }

    @Override
    public void tick() {
        super.tick();
        guiLeft = Utils.getGuiLeft();
        guiTop = Utils.getGuiTop();
        if (auctions.size() == 0) {
            totalPages = 0;
            page = 0;
            shownAucs = 0;
        } else {
            totalPages = (int) Math.ceil((double) auctions.size() / 4);
            lastPageAuctions = auctions.size() % 4 == 0 ?  4 : auctions.size() % 4;
            if (page > totalPages - 1) page--;
            shownAucs = page == totalPages - 1 ? lastPageAuctions : 4;
        }

    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2) {
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableAlpha();
            GlStateManager.color(1, 1, 1, 0.5f);
            drawCenteredString(Minecraft.getMinecraft().fontRendererObj, EnumChatFormatting.AQUA + "Get unlimited profit bin->bin flips, filters, and more" + EnumChatFormatting.RESET, width/2, 16, 0xffffff);
            drawCenteredString(Minecraft.getMinecraft().fontRendererObj, EnumChatFormatting.AQUA + "with Skyhouse+, learn more at" + EnumChatFormatting.RESET, width/2, 32, 0xffffff);

            String shPlusUrl = "https://skyblock.tools/skyhouse/skyhouse_plus";
            if (mouseX >= width/2-fontRendererObj.getStringWidth(shPlusUrl)/2 && mouseX <= width/2+fontRendererObj.getStringWidth(shPlusUrl)/2 && mouseY >= 48 && mouseY <= 48+8) {
                drawCenteredString(fontRendererObj, EnumChatFormatting.UNDERLINE + shPlusUrl + EnumChatFormatting.RESET, width/2, 48, 0xb8b8b8);
            } else drawCenteredString(fontRendererObj, shPlusUrl, width/2, 48, 0xb8b8b8);

            GlStateManager.disableAlpha();
            GlStateManager.color(1, 1, 1, 1);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.AH_OVERLAY_BACKGROUND);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_COMPONENTS);
        drawTexturedModalRect(0, -32, 0, 45, 256, 32);

        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        drawTexturedModalRect(230 - 16 - 10, -32 + 8, 176, 0, 16, 16);
        drawTexturedModalRect(230, -32 + 8, 144, 0, 16, 16);
        drawTexturedModalRect(8, -32 + 8, 194, 16, 16, 16);
        if (Utils.isAhCreationGui() && Utils.renderCreationOverlay()) {
            drawTexturedModalRect(8+22, -32+8+1, 32, 0, 16, 16);
        }
        Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "Flip List", 128, 12 - 32, 0xffffff);

        if (totalPages != 0)
            Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "Found " + auctions.size() + (auctions.size() > 1 ? " flips! Page " : " flip! Page ") + (page + 1) + " of " + totalPages, 128, 15, 0xffffff);
        else {
            Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "No Flips Found", 128, 120, 0xffffff);
            Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, ":(", 128, 136, 0xffffff);
        }

        if (page != 0) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
            drawTexturedModalRect(12, 10, 214, 0, 16, 16);
            drawTexturedModalRect(12 + 20, 10, 0, 0, 16, 16);
        }
        if (page != totalPages - 1 && totalPages != 0) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
            drawTexturedModalRect(256 - 12 - 16 - 20, 10, 16, 0, 16, 16);
            drawTexturedModalRect(256 - 12 - 16, 10, 230, 0, 16, 16);
        }
        int i = 0;
        for (Auction auction : auctions.subList(page * 4, page * 4 + shownAucs)) {
            ItemStack toRender = auction.getStack();

            if (auction.isRecomb()) {

                Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.RECOMB_ICON);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1f, 1f, 1f, 0.75f);
                GlStateManager.scale(0.0625, 0.0625, 0.0625);
                drawTexturedModalRect((20 + 135 + 16) * 16, (-19 + (i + 1) * 55 - 3) * 16, 0, 0, 256, 256);
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(2, 2, 2);
            GlStateManager.enableDepth();

            Utils.renderItem(toRender, (26) / 2, (-14 + 55 * ++i - 3) / 2);
            GlStateManager.disableDepth();
            GlStateManager.popMatrix();
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_COMPONENTS);
            drawTexturedModalRect(19, -20 + i * 55 - 3, 0, 0, 216, 45);
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
            drawTexturedModalRect(19 + 199, -19 + i * 55 - 3, 80, 0, 16, 16);
            drawTexturedModalRect(19 + 135, -19 + i * 55 - 3, 160, 0, 16, 16);
            String nameToDraw = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(auction.getStack().getDisplayName(), 150).trim();
            if (!nameToDraw.equals(auction.getStack().getDisplayName()))
                nameToDraw += (EnumChatFormatting.GRAY + "...");
            drawString(Minecraft.getMinecraft().fontRendererObj, nameToDraw, (67), (5 + 55 * i) - 3, 0xffffff);
            drawString(Minecraft.getMinecraft().fontRendererObj, NumberFormat.getNumberInstance(Locale.UK).format(auction.getProfit()) + " Profit",
                    67, -15 + 55 * i - 3, 0x00ff00);
        }
        i = 0;
        GlStateManager.popMatrix();
        for (Auction auction : auctions.subList(page * 4, page * 4 + shownAucs)) {
            ItemStack toRender = auction.getStack();
            if (hover(mouseX - guiLeft, mouseY - guiTop, 25, -16 + 55 * ++i - 2, 32, 32, guiScale))
                toRender.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
            if (hover(mouseX - guiLeft, mouseY - guiTop, 19 + 135, -19 + i * 55 - 3, 15, 15, guiScale))
                drawHoveringText(Arrays.asList(
                        EnumChatFormatting.GREEN + "Price: " + NumberFormat.getNumberInstance(Locale.UK).format(auction.getPrice()),
                        EnumChatFormatting.GREEN + "Resell: " + NumberFormat.getNumberInstance(Locale.UK).format(auction.getPrice() + auction.getProfit()),
                        EnumChatFormatting.GREEN + "Profit: " + NumberFormat.getNumberInstance(Locale.UK).format(auction.getProfit()) + "" + EnumChatFormatting.RESET
                ), mouseX, mouseY, Minecraft.getMinecraft().fontRendererObj);
        }
        super.drawScreen(mouseX, mouseY);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        if (hover(mouseX-guiLeft, mouseY-guiTop, 8, -32+8, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + "Skyhouse"), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 230-16-10, -32+8, 16, 16, guiScale)) {
            if (Utils.isAhCreationGui() && Utils.renderCreationOverlay()) drawHoveringText(Arrays.asList(EnumChatFormatting.RED + "Close Flip List"), mouseX, mouseY);
            else drawHoveringText(Arrays.asList(EnumChatFormatting.RED + "Back To Menu"), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 230, -32+8, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + "Refresh Auctions"), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 10+20, -32+8+1, 16, 16, guiScale) && Utils.isAhCreationGui() && Utils.renderCreationOverlay()) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + "Creation Overlay"), mouseX, mouseY);
        } else if (hover(mouseX - guiLeft, mouseY - guiTop, 12, 10, 16, 16, guiScale)  && page != 0) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "First Page"), mouseX, mouseY);
        } else if (hover(mouseX - guiLeft, mouseY - guiTop, 12+20, 10, 16, 16, guiScale)  && page != 0) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Previous Page"), mouseX, mouseY);
        } else if (hover(mouseX - guiLeft, mouseY - guiTop, 256-12-16-20, 10, 16, 16, guiScale) && page != totalPages-1 && totalPages != 0) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Next Page"), mouseX, mouseY);
        } else if (hover(mouseX - guiLeft, mouseY - guiTop, 256-12-16, 10, 16, 16, guiScale) && page != totalPages-1 && totalPages != 0) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Last Page"), mouseX, mouseY);
        }
        GlStateManager.disableDepth();


    }

    @Override
    public void click(int mouseX, int mouseY) {
        int start = 4 * page;
        for (int i = 1; i < shownAucs+1; i++) {
            if (hover(mouseX-guiLeft, mouseY-guiTop, 19+199, - 19 + i * 55, 16, 16, guiScale)) {
                SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.add(auctions.get(start + i - 1).getUuid());
                auctions.remove(start + i - 1);
            } else if (hover(mouseX-guiLeft, mouseY-guiTop, 19, - 20 + i * 55, 216, 45, guiScale)) {
                SkyhouseMod.INSTANCE.getListener().setLastAuction(start + i -1);
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/viewauction " + auctions.get(start + i - 1).getUuid());
            }
        }
        if (page != 0 && hover(mouseX-guiLeft, mouseY-guiTop, 12, 10, 16, 16, guiScale)) page = 0;
        else if (page != 0 && hover(mouseX-guiLeft, mouseY-guiTop, 12+20, 10, 16, 16, guiScale)) page--;
        else if (page != totalPages-1 && hover(mouseX-guiLeft, mouseY-guiTop, 256-12-16, 10, 16, 16, guiScale)) page = totalPages;
        else if (page != totalPages-1 && hover(mouseX-guiLeft, mouseY-guiTop, 256-12-16-20, 10, 16, 16, guiScale)) page++;
        else if (hover(mouseX-guiLeft, mouseY-guiTop, 8, -32+8, 16, 16, guiScale)) SkyhouseMod.INSTANCE.getListener().openGui(new ConfigGui());
        else if (hover(mouseX-guiLeft, mouseY-guiTop, 8+22, -32+8+1, 16, 16, guiScale) && Utils.isAhCreationGui() && Utils.renderCreationOverlay()) {
            SkyhouseMod.INSTANCE.getOverlayManager().toggleFlipListCreationGui();
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 230-16-10, -32+8, 16, 16, guiScale)) {
            SkyhouseMod.INSTANCE.getOverlayManager().close();
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 230, -32+8, 16, 16, guiScale)) {
            SkyhouseMod.INSTANCE.getOverlayManager().search(filter);
        }
        String shPlusUrl = "https://skyblock.tools/skyhouse/skyhouse_plus";
        if (mouseX >= width/2-fontRendererObj.getStringWidth(shPlusUrl)/2 && mouseX <= width/2+fontRendererObj.getStringWidth(shPlusUrl)/2 && mouseY >= 48 && mouseY <= 48+8) {
            try {
                Desktop.getDesktop().browse(new URI(shPlusUrl));
            } catch (URISyntaxException | IOException ignored) {
            }
        }
    }

    public void removeNotFoundAuction(int index) {
        SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.add(auctions.get(index).getUuid());
        auctions.remove(index);
    }

    @Override
    public void keyEvent() {

    }

}
