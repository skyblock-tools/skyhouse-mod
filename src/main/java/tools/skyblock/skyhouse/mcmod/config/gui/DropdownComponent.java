package tools.skyblock.skyhouse.mcmod.config.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.managers.DataManager;
import tools.skyblock.skyhouse.mcmod.managers.ThemeManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DropdownComponent implements ConfigGuiComponent {

    private int xPos, yPos;
    private String selected;
    private Consumer<String> updater;
    private Supplier<String[]> optionsSupplier;
    private static final String[] themeBasePath = new String[]{"components", "dropdown"};

    boolean open = false;

    public DropdownComponent(String[] options, String selected, Consumer<String> updater) {
        this(() -> options, selected, updater);
    }

    public DropdownComponent(Supplier<String[]> optionsSupplier, String selected, Consumer<String> updater) {
        this.optionsSupplier = optionsSupplier;
        this.selected = selected;
        this.updater = updater;
        xPos = 0;
        yPos = 0;
    }

    private void tick() {
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        tick();

        int maxWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(selected);
        for (String str : optionsSupplier.get()) maxWidth = Math.max(maxWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(str));

        int xStart = xPos - maxWidth;


        Gui.drawRect(xStart, yPos, xStart + maxWidth + 16, yPos + 16,
                (mouseX > xStart && mouseX < xStart + maxWidth + 16 && mouseY > yPos && mouseY < yPos + 16) ?
                        ThemeManager.getColour(themeBasePath, "selected", "background:hover") :
                        ThemeManager.getColour(themeBasePath,"selected", "background:default"));

        int centreX = xStart + ((xStart + maxWidth + 16) - xStart) / 2;
        Minecraft.getMinecraft().fontRendererObj.drawString(selected, centreX - Minecraft.getMinecraft().fontRendererObj.getStringWidth(selected) / 2,
                yPos + 4,  (mouseX > xStart && mouseX < xStart + maxWidth + 16 && mouseY > yPos && mouseY < yPos + 16) ?
                        ThemeManager.getColour(themeBasePath, "selected", "text:hover") :
                        ThemeManager.getColour(themeBasePath, "selected", "text:default"));

        if (open) {
            boolean scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            for (int i = 0; i < optionsSupplier.get().length; i++) {

                Gui.drawRect(xStart, yPos + 16 * (i + 1), xStart + maxWidth + 16,yPos + 16 * (i + 2),
                        (mouseX > xStart && mouseX < xStart + maxWidth + 16 && mouseY > yPos + 16 * (i + 1) &&
                                mouseY < yPos + 16 * (i + 2)) ?
                                ThemeManager.getColour(themeBasePath, "options", "background:hover") :
                                ThemeManager.getColour(themeBasePath,"options", "background:default"));

                Gui.drawRect(xStart, yPos + 16 * (i + 1), xStart + maxWidth + 16, yPos + 16 * (i + 1) + 1,
                        ThemeManager.getColour(themeBasePath, "options", "separator"));
                Minecraft.getMinecraft().fontRendererObj.drawString(optionsSupplier.get()[i], centreX - Minecraft.getMinecraft().fontRendererObj.getStringWidth(optionsSupplier.get()[i]) / 2,
                        yPos + 4 + 16 * (i + 1), (mouseX > xStart && mouseX < xStart + maxWidth + 16 && mouseY > yPos + 16 * (i + 1) &&
                                mouseY < yPos + 16 * (i + 2)) ?
                                ThemeManager.getColour(themeBasePath, "options", "text:hover") :
                                ThemeManager.getColour(themeBasePath, "options", "text:default"));
            }
            Gui.drawRect(xStart, yPos + 16 * (optionsSupplier.get().length + 1), xStart + maxWidth + 16,
                    yPos + 16 * (optionsSupplier.get().length + 1) + 1,
                    ThemeManager.getColour(themeBasePath, "options", "separator"));


            if (scissor) GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }

    }

    @Override
    public void setCoords(int x, int y) {
        xPos = x;
        yPos = y;
    }

    @Override
    public void mousePressed(int mouseX, int mouseY) {
        int maxWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(selected);
        for (String str : optionsSupplier.get()) maxWidth = Math.max(maxWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(str));

        int xStart = xPos - maxWidth;
        if (mouseX > xStart && mouseX < xStart + maxWidth + 16 && mouseY > yPos && mouseY < yPos + 16) {
            open = !open;
        } else if (open) {
            for (int i = 0; i < optionsSupplier.get().length; i++) {
                if (mouseX < xStart + maxWidth + 16 && mouseY > yPos + 16 * (i + 1) && mouseY < yPos + 16 * (i + 2)) {
                    selected = optionsSupplier.get()[i];
                    updater.accept(selected);
                    open = false;
                    break;
                }
            }
            open = false;
        }


    }
}
