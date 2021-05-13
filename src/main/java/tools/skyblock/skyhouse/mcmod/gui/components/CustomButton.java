package tools.skyblock.skyhouse.mcmod.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class CustomButton extends GuiButton {


    private ResourceLocation resourceLocation = new ResourceLocation("textures/gui/widgets.png");
    private int hoverX, hoverY;
    private int textureX, textureY;
    private boolean customDraw = false;
    private Runnable clickAction = null;
    private float guiScale = 1;

    public CustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);

    }

    public CustomButton scales(float sf) {
        guiScale = sf;
        return this;
    }

    public CustomButton(ResourceLocation resource, int buttonId, int x, int y, int widthIn, int heightIn, int textureX, int textureY, String buttonText) {
        this(buttonId, x, y, widthIn, heightIn, buttonText);
        resourceLocation = resource;
        this.textureX = textureX;
        this.textureY = textureY;
        hoverX = textureX;
        hoverY = textureY;
        customDraw = true;
    }

    public void onHover(int x, int y) {
        hoverX = x;
        hoverY = y;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        drawCustomButton(mc, mouseX, mouseY);
    }

    public void drawCustomButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(resourceLocation);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = isScaledHover(mouseX, mouseY);
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            if (customDraw)
                this.drawTexturedModalRect(this.xPosition, this.yPosition, hovered ? hoverX : textureX, hovered ? hoverX : textureX, this.width / 2, this.height);
            else {
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            }
                this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (packedFGColour != 0) {
                j = packedFGColour;
            } else if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered && "textures/gui/widgets.png".equals(resourceLocation.getResourcePath())) {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
    }

    public CustomButton withExecutor(Runnable cb) {
        clickAction = cb;
        return this;
    }

    private boolean mousePressed(int mouseX, int mouseY) {
        return enabled && visible && mouseX >= this.xPosition * guiScale && mouseY >= this.yPosition * guiScale &&
                mouseX < (this.xPosition + this.width) * guiScale && mouseY < (this.yPosition + this.height) * guiScale;
    }

    protected boolean isScaledHover(int mouseX, int mouseY) {
        return mouseX >= this.xPosition * guiScale && mouseY >= this.yPosition * guiScale &&
                mouseX < (this.xPosition + this.width) * guiScale && mouseY < (this.yPosition + this.height) * guiScale;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (mousePressed(mouseX, mouseY) && clickAction != null) {
            clickAction.run();
            return true;
        }
        return false;
    }
}
