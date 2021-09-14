package tools.skyblock.skyhouse.mcmod.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.client.ClientCommandHandler;
import tools.skyblock.skyhouse.mcmod.util.Resources;

public class CommandComponent extends GuiButton implements ConfigGuiComponent {

    private String command;

    public CommandComponent(int buttonId, int x, int y, String buttonText, String command) {
        super(buttonId, x, y, buttonText);
        width = 16;
        height = 16;
        this.command = command;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);

        int strWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(displayString);
        int strWidthTrimmed = strWidth - Math.min(16, strWidth);
        double sections = Math.ceil(strWidthTrimmed / 16f);
        xPosition = (int) (xPosition - (sections + 1) * 16);
        width = (int) (32 + sections * 16);

        drawTexturedModalRect(xPosition, yPosition, 80, 16, 16, 16);
        int i;
        for (i = 1; i <= sections; i++) {
            drawTexturedModalRect(xPosition + 16 * i, yPosition, 96, 16, 16, 16);
        }
        drawTexturedModalRect(xPosition + 16 * i, yPosition, 112, 16, 16, 16);
        Minecraft.getMinecraft().fontRendererObj.drawString(displayString, (int) (xPosition + (32 + 16 * sections) / 2 - strWidth / 2), yPosition + 4, 0xffffffff);
    }

    @Override
    public void setCoords(int x, int y) {
        xPosition = x;
        yPosition = y;
    }

    @Override
    public boolean mousePressed(int mouseX, int mouseY) {
        boolean ret = super.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
        if (ret)
            ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, "/" + command);
        return ret;
    }
}
