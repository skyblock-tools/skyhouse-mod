package tools.skyblock.skyhouse.mcmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.util.Resources;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.io.IOException;

public class AhOverlayPositionEditor extends GuiScreen {

    private int guiLeft, guiTop;
    private int offsetX, offsetY;
    private float guiScale;
    private boolean isDragging;
    private boolean save = true;
    private ResourceLocation guiChest = new ResourceLocation("textures/gui/container/generic_54.png");

    @Override
    public void initGui() {
        guiLeft = Utils.getGuiLeft();
        guiTop = Utils.getGuiTop();
        guiScale = Utils.getScaleFactor();
        buttonList.add(new GuiButton(0, 10, height-90, "Reset Layout"));
        buttonList.add(new GuiButton(1, 10, height-60, "Save"));
        buttonList.add(new GuiButton(2, 10, height-30, "Cancel"));
        super.initGui();
    }

    public void calculateOffset(int mouseX, int mouseY) {
        offsetX = guiLeft - mouseX;
        offsetY = guiTop - mouseY;
    }

    public void tick(int mouseX, int mouseY) {
        int lastGuiLeft = guiLeft, lastGuiTop = guiTop;
        guiLeft = Math.round(Math.max(Math.min(isDragging ? mouseX + offsetX : guiLeft, width - 256 * guiScale), 0));
        guiTop = Math.round(Math.max(Math.min(isDragging ? mouseY + offsetY : guiTop, height - 256 * guiScale), 0));

        if (wouldRenderOutOfBoundsX(guiLeft, guiScale)) {
            guiLeft = lastGuiLeft;
            calculateOffset(mouseX, mouseY);
        }
        if (wouldRenderOutOfBoundsY(guiTop, guiScale)) {
            guiTop = lastGuiTop;
            calculateOffset(mouseX, mouseY);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        // chest background
        Minecraft.getMinecraft().getTextureManager().bindTexture(guiChest);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(0.3f, 0.3f, 0.3f, 0.5f);
        drawTexturedModalRect((width-176)/2, (height-222)/2, 0, 0, 176, 222);
        String label = "Auction House GUI";
        Utils.drawStringWithShadow(fontRendererObj, label, (width-176)/2+(164-fontRendererObj.getStringWidth(label))/2, (height-222)/2+128, 0xAAAAAA | 127 << 24);
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
        // main editor
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        tick(mouseX, mouseY);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.AH_OVERLAY_BACKGROUND);
        drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_COMPONENTS);
        drawTexturedModalRect(0, -32, 0, 45, 256, 32);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        drawTexturedModalRect(96, 164, 0, 32, 64, 64);
        drawTexturedModalRect(96, 32, 64, 32, 64, 64);
        drawCenteredString(fontRendererObj, "Drag To Move", 128, 124, 0x00ff00);
        GlStateManager.popMatrix();
        for (GuiButton guiButton : this.buttonList) {
            Utils.drawButton(guiButton, this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        float oldGuiScale = guiScale;
        if (mouseX > guiLeft + 96 * guiScale && mouseX < guiLeft + (96 + 64) * guiScale && mouseY > guiTop + 164 * guiScale && mouseY < guiTop + (164 + 64) * guiScale) {
            guiScale = Math.max(guiScale - 0.1f, 0.5f);
        } else if (mouseX > guiLeft + 96 * guiScale && mouseX < guiLeft + (96 + 64) * guiScale && mouseY > guiTop + 32 * guiScale && mouseY < guiTop + (32 + 64 * guiScale)) {
            guiScale = Math.min(guiScale + 0.1f, 2);
            if (wouldRenderOutOfBoundsX(guiLeft, guiScale) || wouldRenderOutOfBoundsY(guiTop, guiScale)) guiScale = oldGuiScale;
        }
        if (mouseX > guiLeft && mouseX < guiLeft + 256 * guiScale && mouseY > guiTop - 32 * guiScale && mouseY < guiTop + 256 * guiScale) {
            isDragging = true;
            calculateOffset(mouseX, mouseY);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (isDragging) {
            isDragging = false;
            guiLeft = mouseX + offsetX;
            guiTop = mouseY + offsetY;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                guiScale = 1;
                guiLeft = 1;
                guiTop = 33;
                break;
            case 1:
                save();
                SkyhouseMod.INSTANCE.getListener().openGui(new ConfigGui());
                break;
            case 2:
                save = false;
                SkyhouseMod.INSTANCE.getListener().openGui(new ConfigGui());
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        save();
    }

    private void save() {
        if (save) {
            boolean relative = SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.relativeGui;
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.guiLeft = relative ? Math.round(((float) (guiLeft == 0 ? 1 : guiLeft) / ((float) sr.getScaledWidth())) * 1000) : guiLeft;
            SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.guiTop = relative ? Math.round(((float) (guiTop == 0 ? 1 : guiTop) / ((float) sr.getScaledHeight())) * 1000) : guiTop;
            SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.guiScale = relative ? (255f * guiScale) / sr.getScaledWidth() : guiScale;
            SkyhouseMod.INSTANCE.saveConfig();
        }
    }

    private boolean wouldRenderOutOfBoundsX(int x, float sf) {
        return (x == 0 || x >= width - 1 - 256 * sf);
    }

    private boolean wouldRenderOutOfBoundsY(int y, float sf) {
        return (y <= 32 * sf || y >= height - 1 - 256 * sf);
    }

}
