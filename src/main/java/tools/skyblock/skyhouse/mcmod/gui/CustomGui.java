package tools.skyblock.skyhouse.mcmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomGui extends GuiScreen {

    protected ScaledResolution sr;
    protected List<GuiTextField> inputs = new ArrayList<>();
    protected List<GuiButton> buttons = new ArrayList<>();

    public CustomGui() {
        fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    }

    public void tick() {
        sr = new ScaledResolution(Minecraft.getMinecraft());
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawScreen(mouseX, mouseY);
    }

    public void drawScreen(int mouseX, int mouseY) {
        for (GuiTextField textField : inputs) {
            textField.drawTextBox();
        }
        for (GuiButton button : buttons) {
            button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
        }
    }

    public abstract void click(int mouseX, int mouseY);

    public abstract void keyEvent();


    protected boolean hover(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    protected boolean hover(int mouseX, int mouseY, int x, int y, int width, int height, float guiScale) {
        return mouseX > x * guiScale && mouseX < (x + width)*guiScale && mouseY > y * guiScale && mouseY < (y + height)*guiScale;
    }


    public void drawHoveringText(List<String> textLines, int x, int y) {
        super.drawHoveringText(textLines, x, y, Minecraft.getMinecraft().fontRendererObj);
    }
}
