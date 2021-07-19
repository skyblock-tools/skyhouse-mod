package tools.skyblock.skyhouse.mcmod.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import tools.skyblock.skyhouse.mcmod.util.Resources;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class BooleanComponent extends GuiButton implements ConfigGuiComponent {


    private Predicate<Boolean> toggleCheck;
    private Consumer<Boolean> updater;
    public Supplier<Boolean> checked;


    public BooleanComponent(int buttonId, int x, int y, Predicate<Boolean> canCheck, Supplier<Boolean> checked, Consumer<Boolean> updater) {
        super(buttonId, x, y, "");
        width = 16;
        height = 16;
        toggleCheck = canCheck;
        this.checked = checked;
        this.updater = updater;
    }

    @Override
    public void setCoords(int x, int y) {
        xPosition = x;
        yPosition = y;
    }

    @Override
    public void mousePressed(int mouseX, int mouseY) {
        mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY) && toggleCheck.test(checked.get())) {
            updater.accept(!checked.get());
            return true;
        }
        return false;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        drawButton(Minecraft.getMinecraft(), mouseX, mouseY, !toggleCheck.test(checked.get()));
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, boolean greyed) {
        if (this.visible) {
            enabled = toggleCheck.test(checked.get());
            mc.getTextureManager().bindTexture(Resources.GUI_ICONS);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.mouseDragged(mc, mouseX, mouseY);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, checked.get() ? 64 : 48, greyed ? 16 : 0, 16, 16);
        }
    }
}

