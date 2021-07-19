package tools.skyblock.skyhouse.mcmod.config.gui;

public interface ConfigGuiComponent {

    void draw(int mouseX, int mouseY);

    void setCoords(int x, int y);

    void mousePressed(int mouseX, int mouseY);

}
