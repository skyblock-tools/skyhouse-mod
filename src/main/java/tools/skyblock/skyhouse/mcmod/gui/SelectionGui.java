package tools.skyblock.skyhouse.mcmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomButton;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomTextbox;
import tools.skyblock.skyhouse.mcmod.managers.ConfigManager;
import tools.skyblock.skyhouse.mcmod.models.SearchFilter;
import tools.skyblock.skyhouse.mcmod.util.Utils;
import tools.skyblock.skyhouse.mcmod.util.Constants;
import tools.skyblock.skyhouse.mcmod.util.Resources;

public class SelectionGui extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;

    private SearchFilter searchFilter = new SearchFilter();



    public SelectionGui() {
        createElements();
        initGui();
    }


    private void createElements() {
        ConfigManager config = SkyhouseMod.INSTANCE.configManager;
        inputs.add(
                new CustomTextbox(0, Minecraft.getMinecraft().fontRendererObj, guiLeft+(64-40), guiTop+150, 80, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(config.saveOptions ? String.valueOf(config.minProfit) : "100 000")
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMinProfit,
                                Constants.DEFAULT_MIN_PROFIT))
                        .withStateUpdater(Utils.createStringToIntCallback(config::setMinProfit, Constants.DEFAULT_MIN_PROFIT))
        );
        inputs.add(
                new CustomTextbox(1, Minecraft.getMinecraft().fontRendererObj, guiLeft+(64-40), guiTop+180, 80, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(config.saveOptions ? String.valueOf(config.maxPrice) : "10 000 000")
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMaxPrice, Constants.DEFAULT_MAX_PRICE))
                        .withStateUpdater(Utils.createStringToIntCallback(config::setMaxPrice, Constants.DEFAULT_MAX_PRICE))
        );
        buttons.add(
                new CustomButton(0, guiLeft+(128-40), guiTop+220, 80, 20, "Search")
                        .withExecutor(() -> SkyhouseMod.INSTANCE.overlayManager.search(searchFilter))
        );
    }

    @Override
    public void initGui() {
        tick();
        guiScale = Utils.getScaleFactor();
        inputs.get(0).xPosition = 64-40;
        inputs.get(0).yPosition = 150;
        inputs.get(1).xPosition = 64-40;
        inputs.get(1).yPosition = 180;
        buttons.get(0).xPosition = 128-40;
        buttons.get(0).yPosition = 220;
        for (GuiButton button : buttons) {
            if (button instanceof CustomButton) ((CustomButton) button).scales(guiScale);
        }
    }


    @Override
    public void tick() {
        super.tick();
        guiLeft = Utils.getGuiLeft();
        guiTop = Utils.getGuiTop();
    }


    @Override
    public void keyEvent() {
        for (GuiTextField field : inputs) {
            if (field.isFocused())
                field.textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.AH_OVERLAY_BACKGROUND);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();
//        drawTexturedModalRect(width-256-20, height/2-128, 0, 0, 256, 256);
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "AH Flip options", 120, 20, 0xffffff);

        drawString(Minecraft.getMinecraft().fontRendererObj, "Minimum profit",(64-40)+100, 150+5, 0xffffff);
        drawString(Minecraft.getMinecraft().fontRendererObj, "Maximum price",(64-40)+100, 180+5, 0xffffff);
        super.drawScreen(mouseX-guiLeft, mouseY-guiTop);
        GlStateManager.popMatrix();

        GlStateManager.enableDepth();
    }

    @Override
    public void click(int mouseX, int mouseY) {
        for (GuiTextField textField : inputs) {
            textField.setFocused(hover(mouseX-guiLeft, mouseY-guiTop, textField.xPosition, textField.yPosition, textField.width, textField.height, guiScale));
        }
        for (GuiButton button : buttons) {
            button.mousePressed(Minecraft.getMinecraft(), mouseX-guiLeft, mouseY-guiTop);
        }
    }
}
