package tools.skyblock.skyhouse.mcmod.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import tools.skyblock.skyhouse.mcmod.util.Resources;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CheckBox extends GuiButton {


    private Predicate<Boolean> toggleCheck;
    private Consumer<Boolean> updater;
    public boolean checked;


    public CheckBox(int buttonId, int x, int y, Predicate<Boolean> canCheck, boolean checked, Consumer<Boolean> updater) {
        super(buttonId, x, y, "");
        width = 16;
        height = 16;
        toggleCheck = canCheck;
        this.checked = checked;
        this.updater = updater;
    }
    public void setCoords(int x, int y) {
        xPosition = x;
        yPosition = y;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY) && toggleCheck.test(checked)) {
            checked = !checked;
            updater.accept(checked);
            return true;
        }
        return false;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            enabled = toggleCheck.test(checked);
            mc.getTextureManager().bindTexture(Resources.GUI_ICONS);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.mouseDragged(mc, mouseX, mouseY);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, checked?64:48, 0, 16, 16);

        }
    }
}
