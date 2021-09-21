package tools.skyblock.skyhouse.mcmod.config.gui;

public interface ConfigGuiComponent {

    void draw(int mouseX, int mouseY);

    void setCoords(int x, int y);

    boolean mousePressed(int mouseX, int mouseY);

    default int priority() {
        return 0;
    }

}
