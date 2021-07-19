package tools.skyblock.skyhouse.mcmod.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public abstract class OverlayBase extends GuiScreen {


    public OverlayBase() {
        fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        render(mouseX, mouseY);
    }

    public abstract void render(int mouseX, int mouseY);
    public abstract boolean shouldRender();

    public boolean regenInstance() {
        return true;
    }

}
