package tools.skyblock.skyhouse.mcmod.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.managers.ThemeManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DropdownComponent implements ConfigGuiComponent {

    private int xPos, yPos;
    private String selected;
    private Consumer<String> updater;
    private Supplier<String[]> optionsSupplier;
    private static final String[] themeBasePath = new String[]{"components", "dropdown"};
    private boolean fromRight;
    private float guiScale = 1;
    private boolean premium;

    boolean open = false;

    public DropdownComponent(String[] options, String selected, Consumer<String> updater, boolean fromRight, boolean premium) {
        this(() -> options, selected, updater, fromRight, premium);
    }

    public void scales(float sf) {
        guiScale = sf;
    }

    public DropdownComponent(Supplier<String[]> optionsSupplier, String selected, Consumer<String> updater, boolean fromRight, boolean premium) {
        this.optionsSupplier = optionsSupplier;
        this.selected = selected;
        this.updater = updater;
        this.fromRight = fromRight;
        this.premium = premium;
        xPos = 0;
        yPos = 0;
    }

    private void tick() {
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        tick();

        char arrow = open ? '\u25B2' : '\u25BC';
        int arrowWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(" " + arrow);
        int maxWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(selected) + arrowWidth;
        for (String str : optionsSupplier.get())
            maxWidth = Math.max(maxWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(str) + arrowWidth);

        int xStart = fromRight ? xPos - maxWidth : xPos;
        int centreX = xStart + ((xStart + maxWidth + 16 - arrowWidth) - xStart) / 2;

        if (!(premium && SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2)) {
            Gui.drawRect(xStart, yPos, xStart + maxWidth + 8, yPos + 16,
                    (mouseX > xStart && mouseX < xStart + maxWidth + 16 && mouseY > yPos && mouseY < yPos + 16) ?
                            ThemeManager.getColour(themeBasePath, "selected", "background:hover") :
                            ThemeManager.getColour(themeBasePath, "selected", "background:default"));

            Minecraft.getMinecraft().fontRendererObj.drawString(selected, xStart + 4,
                    yPos + 4, (mouseX > xStart && mouseX < xStart + maxWidth + 8 && mouseY > yPos && mouseY < yPos + 16) ?
                            ThemeManager.getColour(themeBasePath, "selected", "text:hover") :
                            ThemeManager.getColour(themeBasePath, "selected", "text:default"));

            Minecraft.getMinecraft().fontRendererObj.drawString(String.valueOf(arrow), xStart + maxWidth, yPos + 4, (mouseX > xStart && mouseX < xStart + maxWidth + 8 && mouseY > yPos && mouseY < yPos + 16) ?
                    ThemeManager.getColour(themeBasePath, "selected", "text:hover") :
                    ThemeManager.getColour(themeBasePath, "selected", "text:default"));
        } else {
            Gui.drawRect(xStart, yPos, xStart + maxWidth + 8, yPos + 16, 0xff6d6d6d);

            Minecraft.getMinecraft().fontRendererObj.drawString(selected, xStart + 4, yPos + 4, 0xbdbdbd);

            Minecraft.getMinecraft().fontRendererObj.drawString(String.valueOf(arrow), xStart + maxWidth, yPos + 4, 0xbdbdbd);
        }

        if (open) {
            boolean scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            for (int i = 0; i < optionsSupplier.get().length; i++) {

                Gui.drawRect(xStart, yPos + 16 * (i + 1), xStart + maxWidth + 8, yPos + 16 * (i + 2),
                        (mouseX > xStart * guiScale && mouseX < (xStart + maxWidth + 8) * guiScale && mouseY > (yPos + 16 * (i + 1)) * guiScale &&
                                mouseY < (yPos + 16 * (i + 2)) * guiScale) ?
                                ThemeManager.getColour(themeBasePath, "options", "background:hover") :
                                ThemeManager.getColour(themeBasePath, "options", "background:default"));

                Gui.drawRect(xStart, yPos + 16 * (i + 1), xStart + maxWidth + 8, yPos + 16 * (i + 1) + 1,
                        ThemeManager.getColour(themeBasePath, "options", "separator"));
                Minecraft.getMinecraft().fontRendererObj.drawString(optionsSupplier.get()[i], xStart + 4,
                        yPos + 4 + 16 * (i + 1), (mouseX > xStart * guiScale && mouseX < (xStart + maxWidth + 8) * guiScale && mouseY > (yPos + 16 * (i + 1)) * guiScale &&
                                mouseY < (yPos + 16 * (i + 2)) * guiScale) ?
                                ThemeManager.getColour(themeBasePath, "options", "text:hover") :
                                ThemeManager.getColour(themeBasePath, "options", "text:default"));
            }
            Gui.drawRect(xStart, yPos + 16 * (optionsSupplier.get().length + 1), xStart + maxWidth + 8,
                    yPos + 16 * (optionsSupplier.get().length + 1) + 1,
                    ThemeManager.getColour(themeBasePath, "options", "separator"));


            if (scissor) GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GlStateManager.color(1, 1, 1, 1);
        }

    }

    @Override
    public void setCoords(int x, int y) {
        xPos = x;
        yPos = y;
    }

    @Override
    public boolean mousePressed(int mouseX, int mouseY) {
        if (premium && SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2) return false;
        boolean ret = false;
        char arrow = open ? '\u25B2' : '\u25BC';
        int maxWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(selected + ' ' + arrow);
        for (String str : optionsSupplier.get())
            maxWidth = Math.max(maxWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(str)
                    + Minecraft.getMinecraft().fontRendererObj.getStringWidth(" " + arrow));

        int xStart = fromRight ? xPos - maxWidth : xPos;
        if (mouseX > xStart * guiScale && mouseX < (xStart + maxWidth + 8) * guiScale && mouseY > yPos * guiScale && mouseY < (yPos + 16) * guiScale) {
            open = !open;
            ret = true;
        } else if (open) {
            for (int i = 0; i < optionsSupplier.get().length; i++) {
                if (mouseX > xStart * guiScale && mouseX < (xStart + maxWidth + 8) * guiScale && mouseY > (yPos + 16 * (i + 1)) * guiScale && mouseY < (yPos + 16 * (i + 2)) * guiScale) {
                    selected = optionsSupplier.get()[i];
                    updater.accept(selected);
                    open = false;
                    break;
                }
            }
            open = false;
            ret = true;
        }
        return ret;

    }

    @Override
    public int priority() {
        return 1;
    }
}
