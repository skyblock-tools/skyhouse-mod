package tools.skyblock.skyhouse.mcmod.overlays.ah;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import scala.actors.threadpool.Arrays;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.SkyhouseConfig;
import tools.skyblock.skyhouse.mcmod.config.annotations.HiddenConfigOption;
import tools.skyblock.skyhouse.mcmod.gui.CustomGui;
import tools.skyblock.skyhouse.mcmod.gui.ConfigGui;
import tools.skyblock.skyhouse.mcmod.gui.components.CheckBox;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomButton;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomTextbox;
import tools.skyblock.skyhouse.mcmod.gui.components.IconButton;
import tools.skyblock.skyhouse.mcmod.models.SearchFilter;
import tools.skyblock.skyhouse.mcmod.util.Utils;
import tools.skyblock.skyhouse.mcmod.util.Constants;
import tools.skyblock.skyhouse.mcmod.util.Resources;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SelectionGui extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;
    private List<HiddenConfigOption> labels = new ArrayList<>();

    private List<CheckBox> itemFilterCheckBoxes = new ArrayList<>();

    private List<IconButton> iconButtons = new ArrayList<>();

    private SearchFilter searchFilter = new SearchFilter();

    private final List<String> skyhousePlusOnlyTooltip = Lists.newArrayList(EnumChatFormatting.RED + "The greyed out filters below require", EnumChatFormatting.RED + "Skyhouse+, click to learn more.");

    public SelectionGui() {
        createElements();
        initGui();
    }

    private void createElements() {
        SkyhouseConfig config = SkyhouseMod.INSTANCE.getConfig();
        boolean save = config.ahOverlayConfig.saveOptions;
        int minProfit = save ? config.filterOptions.minProfit : Constants.DEFAULT_MIN_PROFIT;
        int maxPrice = save ? config.filterOptions.maxPrice : Constants.DEFAULT_MAX_PRICE;
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
                        .withStateUpdater(Utils.createStringToIntCallback(config.filterOptions::setMinProfit, Constants.DEFAULT_MIN_PROFIT))
        );
        inputs.add(
                new CustomTextbox(1, Minecraft.getMinecraft().fontRendererObj, 14+10, 198, 90, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(String.valueOf(maxPrice))
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMaxPrice, Constants.DEFAULT_MAX_PRICE))
                        .withStateUpdater(Utils.createStringToIntCallback(config.filterOptions::setMaxPrice, Constants.DEFAULT_MAX_PRICE))
        );
        buttons.add(
                new CustomButton(0, (128-40), 236-10, 80, 20, "Search")
                        .withExecutor(() -> SkyhouseMod.INSTANCE.getOverlayManager().search(searchFilter))
        );

        iconButtons.add(new IconButton(1, 256-16-10-14-16, 12, 80, 0)
                        .withTooltip(EnumChatFormatting.RED + "Clear Auction Blacklist")
                        .withClickCallback(SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist::clear)
                        .withEnabledPredicate(() -> !SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.isEmpty()));

        iconButtons.add(new IconButton(1, 256-14-16, 12, 176, 16)
                .withTooltip(EnumChatFormatting.RED + "Reset Filter Preferences")
                .withClickCallback(SkyhouseMod.INSTANCE.getOverlayManager()::resetFilter)
                .withEnabledPredicate(() -> !SkyhouseMod.INSTANCE.getOverlayManager().isFilterDefault()));

        iconButtons.add(new IconButton(1, 14, 12, 194, 16)
                .withTooltip(EnumChatFormatting.GREEN + "Skyhouse")
                .withClickCallback(() -> SkyhouseMod.INSTANCE.getListener().openGui(new ConfigGui())));
    }

    @Override
    public void initGui() {
        tick();
        itemFilterCheckBoxes.clear();
        labels.clear();
        guiScale = Utils.getScaleFactor();
        for (GuiButton button : buttons) {
            if (button instanceof CustomButton) ((CustomButton) button).scales(guiScale);
        }
        for (IconButton button : iconButtons) {
            button.scales(guiScale);
        }
        int i = 0;
        int currentHeight = 72;
        for (Field field : SkyhouseConfig.FilterOptions.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(HiddenConfigOption.class)) continue;
            labels.add(field.getAnnotation(HiddenConfigOption.class));
            String methodSuffix = Character.toUpperCase(field.getName().charAt(0))
                    + field.getName().substring(1);
            Consumer<Boolean> updater = (checked) -> {
                try {
                    field.getDeclaringClass().getDeclaredMethod("set" + methodSuffix, boolean.class)
                            .invoke(SkyhouseMod.INSTANCE.getConfig().filterOptions, checked);
                    searchFilter.getClass().getDeclaredMethod("set" + methodSuffix, boolean.class)
                            .invoke(SkyhouseMod.INSTANCE.getConfig().filterOptions, checked);
                } catch (ReflectiveOperationException e) {
                    try {
                        field.set(SkyhouseMod.INSTANCE.getConfig().filterOptions, checked);
                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    }
                }
            };
            Predicate<Boolean> updateChecker = (checked) -> {
                try {
                    return (boolean) field.getDeclaringClass().getDeclaredMethod("check" + methodSuffix, boolean.class)
                            .invoke(SkyhouseMod.INSTANCE.getConfig().filterOptions, checked);
                } catch (ReflectiveOperationException e) {
                    return true;
                }
            };
            try {
                itemFilterCheckBoxes.add(new CheckBox(i, 256 - 16 - 16 - 8, currentHeight - 8, updateChecker,
                        field.getBoolean(SkyhouseMod.INSTANCE.getConfig().filterOptions), updater));
            } catch (ReflectiveOperationException ignored) {
            }
            i++;
            currentHeight += 24;
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
        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_COMPONENTS);
        drawTexturedModalRect(0, -32, 0, 45, 256, 32);

        Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "AH Flip Options", 128, 12-32, 0xffffff);

        drawHorizontalLine(12, 256-12, 34, 0xff595959);

        Utils.drawString(this, Minecraft.getMinecraft().fontRendererObj, "Minimum Profit", 256-90-(14+10), 192-6, 0xffffff);
        Utils.drawString(this, Minecraft.getMinecraft().fontRendererObj, "Maximum Price", 14+10, 192-6, 0xffffff);

        int currentHeight = 72;
        for (HiddenConfigOption option : labels) {
            Utils.drawString(this, fontRendererObj, EnumChatFormatting.WHITE + option.value(), 128+(64-50), currentHeight-4, 0xffffff);
            currentHeight += 24;
        }
        Utils.drawString(this, fontRendererObj, "Include:", 128+(64-50), 48-4, 0xffffff);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2) {
            drawTexturedModalRect(128 + 64 + 32 - 8, 48 - 7, 96, 0, 16, 16);
        }

        for (CheckBox checkBox : itemFilterCheckBoxes) {
            checkBox.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2);
        }

        for (GuiButton guiButton : buttons) {
            guiButton.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
        }

        for (IconButton button : iconButtons) {
            button.tick();
        }

        drawComponents(mouseX, mouseY);
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
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
        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2) {
            if (hover(mouseX - guiLeft, mouseY - guiTop, 128 + 64 + 32 - 8, 48 - 7, 16, 16, guiScale)) {
                drawHoveringText(skyhousePlusOnlyTooltip, mouseX, mouseY);
            }
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
        for (CheckBox button : itemFilterCheckBoxes) {
            if (hover(mouseX - guiLeft, mouseY - guiTop, button.xPosition, button.yPosition, button.width, button.height, guiScale)) {
                button.pressed();
            }
        }
        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2) {
            if (hover(mouseX - guiLeft, mouseY - guiTop, 128 + 64 + 32 - 8, 48 - 7, 16, 16, guiScale)) {
                try {
                    Desktop.getDesktop().browse(new URI(Constants.SKYHOUSE_PLUS_URL));
                } catch (URISyntaxException | IOException ignored) {
                }
            }
        }
    }
}
