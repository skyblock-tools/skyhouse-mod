package tools.skyblock.skyhouse.mcmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomButton;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomTextbox;
import tools.skyblock.skyhouse.mcmod.gui.components.IconButton;
import tools.skyblock.skyhouse.mcmod.managers.ConfigManager;
import tools.skyblock.skyhouse.mcmod.models.SearchFilter;
import tools.skyblock.skyhouse.mcmod.util.Utils;
import tools.skyblock.skyhouse.mcmod.util.Constants;
import tools.skyblock.skyhouse.mcmod.util.Resources;

import java.util.ArrayList;
import java.util.List;

public class SelectionGui extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;

    private List<IconButton> extraPanelButtons = new ArrayList<>();

    private SearchFilter searchFilter = new SearchFilter();

    public SelectionGui() {
        createElements();
        initGui();
    }

    private void createElements() {
        ConfigManager config = SkyhouseMod.INSTANCE.getConfigManager();
        boolean save = config.saveOptions;
        int minProfit = save ? config.minProfit : Constants.DEFAULT_MIN_PROFIT;
        int maxPrice = save ? config.maxPrice : Constants.DEFAULT_MAX_PRICE;
        searchFilter
                .withMinProfit(minProfit)
                .withMaxPrice(maxPrice);
        inputs.clear();
        buttons.clear();
        inputs.add(
                new CustomTextbox(0, Minecraft.getMinecraft().fontRendererObj, (64-50), 150, 74, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(String.valueOf(minProfit))
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMinProfit,
                                Constants.DEFAULT_MIN_PROFIT))
                        .withStateUpdater(Utils.createStringToIntCallback(config::setMinProfit, Constants.DEFAULT_MIN_PROFIT))
        );
        inputs.add(
                new CustomTextbox(1, Minecraft.getMinecraft().fontRendererObj, (64-50), 190, 74, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(String.valueOf(maxPrice))
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMaxPrice, Constants.DEFAULT_MAX_PRICE))
                        .withStateUpdater(Utils.createStringToIntCallback(config::setMaxPrice, Constants.DEFAULT_MAX_PRICE))
        );
        buttons.add(
                new CustomButton(0, (128-40), 220, 80, 20, "Search")
                        .withExecutor(() -> SkyhouseMod.INSTANCE.getOverlayManager().search(searchFilter))
        );

        extraPanelButtons.add(new IconButton(1, 10, 256+8, 80, 0)
                        .withTooltip(EnumChatFormatting.RED + "Clear auction blacklist")
                        .withClickCallback(() -> SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.clear())
                        .withEnabledPredicate(() -> !SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.isEmpty()));
    }

    @Override
    public void initGui() {
        tick();
        guiScale = Utils.getScaleFactor();
        for (GuiButton button : buttons) {
            if (button instanceof CustomButton) ((CustomButton) button).scales(guiScale);
        }
        for (IconButton button : extraPanelButtons)
            button.scales(guiScale);
    }


    @Override
    public void tick() {
        super.tick();
        guiLeft = Utils.getGuiLeft();
        guiTop = Utils.getGuiTop();
    }


    @Override
    public void keyEvent() {
        if (Keyboard.getEventKeyState())
        for (GuiTextField field : inputs) {
            field.textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.AH_OVERLAY_BACKGROUND);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        boolean drawExtraPanel = false;
        for (IconButton button : extraPanelButtons) {
            button.tick();
            if (button.enabled) {
                drawExtraPanel = true;
                break;
            }
        }
        if (drawExtraPanel) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_COMPONENTS);
            drawTexturedModalRect(0, 256, 0, 45, 256, 32);
        }
        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "AH Flip options", 120, 20, 0xffffff);

        drawString(Minecraft.getMinecraft().fontRendererObj, "Minimum profit",(64-50), 150-12, 0xffffff);
        drawString(Minecraft.getMinecraft().fontRendererObj, "Maximum price",(64-50), 180-2, 0xffffff);

        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        if (!SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.isEmpty()) drawTexturedModalRect(10, 256+8, 80, 0, 16, 16);

        drawComponents(mouseX, mouseY);
        GlStateManager.popMatrix();
        drawTooltips(mouseX, mouseY);


        GlStateManager.enableDepth();
    }

    private void drawComponents(int mouseX, int mouseY) {
        for (GuiTextField textField : inputs)
            textField.drawTextBox();
        for (IconButton button : extraPanelButtons)
            button.drawButton(Minecraft.getMinecraft(), mouseX - guiLeft, mouseY - guiTop);
        for (GuiButton button : buttons)
            button.drawButton(Minecraft.getMinecraft(), mouseX - guiLeft, mouseY - guiTop);
    }
    private void drawTooltips(int mouseX, int mouseY) {
        for (IconButton button : extraPanelButtons) {
            if (button.getTooltip().size() > 0 && button.isMouseOver() && button.enabled)
                drawHoveringText(button.getTooltip(), mouseX, mouseY);
        }
    }

    @Override
    public void click(int mouseX, int mouseY) {
        for (GuiTextField textField : inputs) {
            textField.setFocused(hover(mouseX-guiLeft, mouseY-guiTop, textField.xPosition, textField.yPosition, textField.width, textField.height, guiScale));
        }
        for (GuiButton button : buttons) {
            if (button.mousePressed(Minecraft.getMinecraft(), mouseX-guiLeft, mouseY-guiTop))
                button.playPressSound(Minecraft.getMinecraft().getSoundHandler());
        }
        for (IconButton button : extraPanelButtons) {
            if (button.isMouseOver() && button.enabled && button.getClickCallback() != null)
                button.getClickCallback().run();
        }
    }
}
