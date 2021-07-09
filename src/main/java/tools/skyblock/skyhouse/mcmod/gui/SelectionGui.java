package tools.skyblock.skyhouse.mcmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.ConfigOption;
import tools.skyblock.skyhouse.mcmod.config.FilterOption;
import tools.skyblock.skyhouse.mcmod.gui.components.CheckBox;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomButton;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomTextbox;
import tools.skyblock.skyhouse.mcmod.gui.components.IconButton;
import tools.skyblock.skyhouse.mcmod.managers.ConfigManager;
import tools.skyblock.skyhouse.mcmod.models.SearchFilter;
import tools.skyblock.skyhouse.mcmod.util.Utils;
import tools.skyblock.skyhouse.mcmod.util.Constants;
import tools.skyblock.skyhouse.mcmod.util.Resources;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SelectionGui extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;
    private List<ConfigOption> labels = new ArrayList<>();

    private List<CheckBox> checkBoxes = new ArrayList<>();

    private List<IconButton> iconButtons = new ArrayList<>();

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
        iconButtons.clear();
        inputs.add(
                new CustomTextbox(0, Minecraft.getMinecraft().fontRendererObj, 128+(64-50), 198, 90, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(String.valueOf(minProfit))
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMinProfit,
                                Constants.DEFAULT_MIN_PROFIT))
                        .withStateUpdater(Utils.createStringToIntCallback(config::setMinProfit, Constants.DEFAULT_MIN_PROFIT))
        );
        inputs.add(
                new CustomTextbox(1, Minecraft.getMinecraft().fontRendererObj, 14+10, 198, 90, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(String.valueOf(maxPrice))
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMaxPrice, Constants.DEFAULT_MAX_PRICE))
                        .withStateUpdater(Utils.createStringToIntCallback(config::setMaxPrice, Constants.DEFAULT_MAX_PRICE))
        );
        buttons.add(
                new CustomButton(0, (128-40), 236-10, 80, 20, "Search")
                        .withExecutor(() -> SkyhouseMod.INSTANCE.getOverlayManager().search(searchFilter))
        );

        iconButtons.add(new IconButton(1, 256-16-10-14-16, 12, 80, 0)
                        .withTooltip(EnumChatFormatting.RED + "Clear Auction Blacklist")
                        .withClickCallback(() -> SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.clear())
                        .withEnabledPredicate(() -> !SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.isEmpty()));

        iconButtons.add(new IconButton(1, 256-14-16, 12, 80, 0)
                .withTooltip(EnumChatFormatting.RED + "Reset Filter Preferences")
                .withClickCallback(() -> SkyhouseMod.INSTANCE.getOverlayManager().resetFilter())
                .withEnabledPredicate(() -> !SkyhouseMod.INSTANCE.getOverlayManager().isFilterDefault()));

        iconButtons.add(new IconButton(1, 14, 12, 194, 0)
                .withTooltip(EnumChatFormatting.GREEN + "Skyhouse Settings")
                .withClickCallback(() -> SkyhouseMod.INSTANCE.getListener().openGui(new ConfigGui())));
    }

    @Override
    public void initGui() {
        tick();
        checkBoxes.clear();
        labels.clear();
        guiScale = Utils.getScaleFactor();
        for (GuiButton button : buttons) {
            if (button instanceof CustomButton) ((CustomButton) button).scales(guiScale);
        }
        for (IconButton button : iconButtons) {
            button.scales(guiScale);
        }
        int i = 0;
        int currentHeight = 168;
        for (Field field : ConfigManager.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigOption.class)) continue;
            if (field.isAnnotationPresent(FilterOption.class)) labels.add(field.getAnnotation(ConfigOption.class));
            String methodSuffix = Character.toUpperCase(field.getName().charAt(0))
                    + field.getName().substring(1);
            if (field.isAnnotationPresent(FilterOption.class)) {
                Consumer<Boolean> updater = (checked) -> {
                    try {
                        field.getDeclaringClass().getDeclaredMethod("set" + methodSuffix, boolean.class)
                                .invoke(SkyhouseMod.INSTANCE.getConfigManager(), checked);
                    } catch (ReflectiveOperationException e) {
                        try {
                            field.set(SkyhouseMod.INSTANCE.getConfigManager(), checked);
                        } catch (IllegalAccessException illegalAccessException) {
                            illegalAccessException.printStackTrace();
                        }
                    }
                };
                Predicate<Boolean> updateChecker = (checked) -> {
                    try {
                        return (boolean) field.getDeclaringClass().getDeclaredMethod("check" + methodSuffix, boolean.class)
                                .invoke(SkyhouseMod.INSTANCE.getConfigManager(), checked);
                    } catch (ReflectiveOperationException e) {
                        return true;
                    }
                };
                try {
                    checkBoxes.add(new CheckBox(i, 256-16-16-8, currentHeight-8, updateChecker,
                            field.getBoolean(SkyhouseMod.INSTANCE.getConfigManager()), updater));
                } catch (ReflectiveOperationException ignored) {}
                i++;
                currentHeight -= 29;
            }
        }
        buttonList.addAll(buttons);
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
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_COMPONENTS);
        drawTexturedModalRect(0, -32, 0, 45, 256, 32);

        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "AH Flip Options", 128, 12-32, 0xffffff);

        drawHorizontalLine(12, 256-12, 34, 0xff595959);

        drawString(Minecraft.getMinecraft().fontRendererObj, "Minimum Profit", 256-90-(14+10), 192-6, 0xffffff);
        drawString(Minecraft.getMinecraft().fontRendererObj, "Maximum Price", 14+10, 192-6, 0xffffff);

        int currentHeight = 196;
        for (ConfigOption option : labels) {
            currentHeight -= 29;
            drawString(fontRendererObj, EnumChatFormatting.WHITE + option.value(), 128+(64-50), currentHeight-4, 0xffffff);
        }

        for (CheckBox checkBox : checkBoxes) {
            checkBox.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2);
        }

        for (GuiButton guiButton : buttons) {
            guiButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        for (IconButton button : iconButtons) {
            button.tick();
        }

        drawComponents(mouseX, mouseY);
        GlStateManager.popMatrix();
        drawTooltips(mouseX, mouseY);


        GlStateManager.enableDepth();
    }

    private void drawComponents(int mouseX, int mouseY) {
        for (GuiTextField textField : inputs) {
            textField.drawTextBox();
        }
        for (IconButton button : iconButtons) {
            button.drawButton(Minecraft.getMinecraft(), mouseX - guiLeft, mouseY - guiTop);
        }
        for (GuiButton button : buttons) {
            button.drawButton(Minecraft.getMinecraft(), mouseX - guiLeft, mouseY - guiTop);
        }
    }

    private void drawTooltips(int mouseX, int mouseY) {
        for (IconButton button : iconButtons) {
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
        for (IconButton button : iconButtons) {
            if (button.isMouseOver() && button.enabled && button.getClickCallback() != null)
                button.getClickCallback().run();
        }
        for (CheckBox button : checkBoxes) {
            if (hover(mouseX - guiLeft, mouseY - guiTop, button.xPosition, button.yPosition, button.width, button.height, guiScale) &&
            SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel > 1) {
                button.pressed();
            }
        }
    }
}
