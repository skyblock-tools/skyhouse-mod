package tools.skyblock.skyhouse.mcmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.managers.ThemeManager;
import tools.skyblock.skyhouse.mcmod.util.Resources;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.io.IOException;

public class CraftFlipGui extends GuiScreen {

    private int guiLeft, guiTop;
    private int offsetX, offsetY;
    private float guiScale;
    private boolean isDragging;
    private boolean save = true;
    private ResourceLocation guiChest = new ResourceLocation("textures/gui/container/crafting_table.png");

    @Override
    public void initGui() {
        guiLeft = Utils.getGuiLeft();
        guiTop = Utils.getGuiTop();
        guiScale = Utils.getScaleFactor();
//        buttonList.add(new GuiButton(0, 10, height-90, "button"));
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
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(0.3f, 0.3f, 0.3f, 1);
                drawTexturedModalRect(((width / 2) - (120/2)), (((height / 2) - (57/2)) * row), 27, 14, 176 - 56, 222 - 128 - 37);
            }
        }
//        String label = "Auction House GUI";
//        Utils.drawStringWithShadow(fontRendererObj, label, (width-176)/2+(164-fontRendererObj.getStringWidth(label))/2, (height-222)/2+128, 0xAAAAAA | 127 << 24);
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
//        for (GuiButton guiButton : this.buttonList) {
//            Utils.drawButton(guiButton, this.mc, mouseX, mouseY);
//        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

//    @Override
//    protected void actionPerformed(GuiButton button) {
//        switch (button.id) {
//            case 0:
//                // do stuff for button with id 0
//        }
//    }

    private boolean wouldRenderOutOfBoundsX(int x, float sf) {
        return (x == 0 || x >= width - 1 - 256 * sf);
    }

    private boolean wouldRenderOutOfBoundsY(int y, float sf) {
        return (y <= 32 * sf || y >= height - 1 - 256 * sf);
    }

}
