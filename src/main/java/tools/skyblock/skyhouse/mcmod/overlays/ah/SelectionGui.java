package tools.skyblock.skyhouse.mcmod.overlays.ah;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.SkyhouseConfig;
import tools.skyblock.skyhouse.mcmod.config.annotations.Dropdown;
import tools.skyblock.skyhouse.mcmod.config.annotations.HiddenConfigOption;
import tools.skyblock.skyhouse.mcmod.config.gui.DropdownComponent;
import tools.skyblock.skyhouse.mcmod.gui.CustomGui;
import tools.skyblock.skyhouse.mcmod.gui.ConfigGui;
import tools.skyblock.skyhouse.mcmod.gui.components.CheckBox;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomButton;
import tools.skyblock.skyhouse.mcmod.gui.components.CustomTextbox;
import tools.skyblock.skyhouse.mcmod.gui.components.IconButton;
import tools.skyblock.skyhouse.mcmod.managers.ThemeManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SelectionGui extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;
    private List<HiddenConfigOption> labels = new ArrayList<>();

    private List<CheckBox> itemFilterCheckBoxes = new ArrayList<>();

    private List<IconButton> iconButtons = new ArrayList<>();

    private SearchFilter searchFilter = new SearchFilter();

    private final List<String> skyhousePlusOnlyTooltip = Lists.newArrayList(EnumChatFormatting.RED + "All greyed out filters require Skyhouse+,", EnumChatFormatting.RED + "click here to learn more.");

    private HashMap<String, CustomTextbox> textboxes = new HashMap<>();

    private DropdownComponent textboxDropdown;
    private DropdownComponent sortDropdown;
    private DropdownComponent typeDropdown;
    private String currentTextbox = "";

    public SelectionGui() {
        createElements();
        initGui();
    }

    private void createElements() {
        SkyhouseConfig config = SkyhouseMod.INSTANCE.getConfig();
        boolean save = config.ahOverlayConfig.saveOptions;
        int minProfit = save ? config.filterOptions.minProfit : Constants.DEFAULT_MIN_PROFIT;
        int maxPrice = save ? config.filterOptions.maxPrice : Constants.DEFAULT_MAX_PRICE;
        int houseQuantity = save  ? config.filterOptions.houseQuantity : Constants.DEFAULT_HOUSE_QUANTITY;
        String currentType = save && config.filterOptions.auctionType != null  ? config.filterOptions.auctionType : Constants.DEFAULT_AUCTION_TYPE_STRING;
        String currentSort = save && config.filterOptions.auctionSort != null ? config.filterOptions.auctionSort : Constants.DEFAULT_AUCTION_SORT_STRING;
        searchFilter
                .withMinProfit(minProfit)
                .withMaxPrice(maxPrice)
                .withHouseQuantity(houseQuantity);
        inputs.clear();
        buttons.clear();
        iconButtons.clear();
        textboxes.clear();

        int textboxX = 64-50, textboxY = 200;
        textboxes.put("Min Quantity",
                new CustomTextbox(1, Minecraft.getMinecraft().fontRendererObj, textboxX, textboxY, 90, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(String.valueOf(houseQuantity))
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withHouseQuantity, Constants.DEFAULT_HOUSE_QUANTITY))
                        .withStateUpdater(Utils.createStringToIntCallback(config.filterOptions::setHouseQuantity, Constants.DEFAULT_HOUSE_QUANTITY))
        );
        textboxes.put("Min Profit",
                new CustomTextbox(0, Minecraft.getMinecraft().fontRendererObj, textboxX, textboxY, 90, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(String.valueOf(minProfit))
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMinProfit,
                                Constants.DEFAULT_MIN_PROFIT))
                        .withStateUpdater(Utils.createStringToIntCallback(config.filterOptions::setMinProfit, Constants.DEFAULT_MIN_PROFIT))
        );
        textboxes.put("Max Price",
                new CustomTextbox(1, Minecraft.getMinecraft().fontRendererObj, textboxX, textboxY, 90, 20, CustomTextbox.DIGITS_ONLY)
                        .withDefaultText(String.valueOf(maxPrice))
                        .withStateUpdater(Utils.createStringToIntCallback(searchFilter::withMaxPrice, Constants.DEFAULT_MAX_PRICE))
                        .withStateUpdater(Utils.createStringToIntCallback(config.filterOptions::setMaxPrice, Constants.DEFAULT_MAX_PRICE))
        );
        currentTextbox = "Min Profit";
        textboxDropdown = new DropdownComponent(() -> textboxes.keySet().toArray(new String[0]), currentTextbox, (textbox) -> {
            currentTextbox = textbox;
        }, false, false);
        textboxDropdown.setCoords(64-50-1, 200-16-2);

        Dropdown typeDropdownAnnotation;
        Supplier<String[]> typeSupplier = null;
        try {
            typeDropdownAnnotation = SkyhouseMod.INSTANCE.getConfig().filterOptions.getClass().getDeclaredField("auctionType").getAnnotation(Dropdown.class);
            typeSupplier = typeDropdownAnnotation::options;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Consumer<String> typeUpdater = null;
        try {
            typeUpdater = Utils.fieldSetter(SkyhouseMod.INSTANCE.getConfig().filterOptions.getClass().getDeclaredField("auctionType"),
                    SkyhouseMod.INSTANCE.getConfig().filterOptions);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        typeDropdown = new DropdownComponent(typeSupplier, currentType, typeUpdater, true, false);
        typeDropdown.setCoords(256-21, 18-5+24+23);;

        Dropdown sortDropdownAnnotation;
        Supplier<String[]> sortSupplier = null;
        try {
            sortDropdownAnnotation = SkyhouseMod.INSTANCE.getConfig().filterOptions.getClass().getDeclaredField("auctionSort").getAnnotation(Dropdown.class);
            sortSupplier = sortDropdownAnnotation::options;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Consumer<String> sortUpdater = null;
        try {
            sortUpdater = Utils.fieldSetter(SkyhouseMod.INSTANCE.getConfig().filterOptions.getClass().getDeclaredField("auctionSort"),
                    SkyhouseMod.INSTANCE.getConfig().filterOptions);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        sortDropdown = new DropdownComponent(sortSupplier, currentSort, sortUpdater, true, true);
        sortDropdown.setCoords(256-21, 18-5+15);

        buttons.add(
                new CustomButton(0, (128-40), 236-10, 80, 20, "Search")
                        .withExecutor(() -> SkyhouseMod.INSTANCE.getOverlayManager().search(searchFilter))
        );

        iconButtons.add(new IconButton(1, 256-16-10-14-16, -24, 80, 0)
                        .withTooltip(EnumChatFormatting.RED + "Clear Auction Blacklist")
                        .withClickCallback(SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist::clear)
                        .withEnabledPredicate(() -> !SkyhouseMod.INSTANCE.getOverlayManager().auctionBlacklist.isEmpty()));

        iconButtons.add(new IconButton(1, 230, -32+8, 176, 16)
                .withTooltip(EnumChatFormatting.RED + "Reset Filter Preferences")
                .withClickCallback(SkyhouseMod.INSTANCE.getOverlayManager()::resetFilter)
                .withEnabledPredicate(() -> !SkyhouseMod.INSTANCE.getOverlayManager().isFilterDefault()));

        iconButtons.add(new IconButton(1, 8, -32+8, 194, 16)
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
        textboxDropdown.scales(guiScale);
        typeDropdown.scales(guiScale);
        for (IconButton button : iconButtons) {
            button.scales(guiScale);
        }
        int i = 0;
        int currentHeight = 16 + 16 + 10;
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
                itemFilterCheckBoxes.add(new CheckBox(i, 128 - 16 - 16 - 8 + 1, currentHeight - 8, updateChecker,
                        field.getBoolean(SkyhouseMod.INSTANCE.getConfig().filterOptions), updater));
            } catch (ReflectiveOperationException ignored) {}
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
            textboxes.get(currentTextbox).textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        ThemeManager.drawOverlayThemeFor("selectionGUI");

        Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "AH Flip Options", 128, 12-32, 0xffffff);


        int currentHeight = 16 + 16 + 10;
        for (HiddenConfigOption option : labels) {
            Utils.drawString(this, fontRendererObj, EnumChatFormatting.WHITE + option.value(), 64-50+2, currentHeight-4, 0xffffff);
            currentHeight += 24;
        }
        Utils.drawString(this, fontRendererObj, "Include:", 64-50+2, 18-2, 0xffffff);
        Utils.drawString(this, fontRendererObj, "Sort By", 256-16, 18-2, 0xffffff, true);
        Utils.drawString(this, fontRendererObj, "Type", 256-16, 18-5+24+11, 0xffffff, true);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2) {
            drawTexturedModalRect(106-16, 18-2-3, 96, 0, 16, 16);
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
        GlStateManager.disableAlpha();
        GlStateManager.enableLighting();
        drawTooltips(mouseX, mouseY);


    }

    private void drawComponents(int mouseX, int mouseY) {
        textboxes.get(currentTextbox).drawTextBox();
        for (IconButton button : iconButtons) {
            button.drawButton(Minecraft.getMinecraft(), mouseX - guiLeft, mouseY - guiTop);
        }
        for (GuiButton button : buttons) {
            button.drawButton(Minecraft.getMinecraft(), mouseX - guiLeft, mouseY - guiTop);
        }
        textboxDropdown.draw(mouseX - guiLeft, mouseY - guiTop);
        typeDropdown.draw(mouseX - guiLeft, mouseY - guiTop);
        sortDropdown.draw(mouseX - guiLeft, mouseY - guiTop);
    }

    private void drawTooltips(int mouseX, int mouseY) {
        for (IconButton button : iconButtons) {
            if (button.getTooltip().size() > 0 && button.isMouseOver() && button.enabled)
                drawHoveringText(button.getTooltip(), mouseX, mouseY);
        }
        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel < 2) {
            if (hover(mouseX - guiLeft, mouseY - guiTop, 105-16, 18-2-3, 16, 16, guiScale)) {
                drawHoveringText(skyhousePlusOnlyTooltip, mouseX, mouseY);
            }
        }
    }

    @Override
    public void click(int mouseX, int mouseY) {
        if (sortDropdown.mousePressed(mouseX - guiLeft, mouseY - guiTop) ||
            typeDropdown.mousePressed(mouseX - guiLeft, mouseY - guiTop) ||
            textboxDropdown.mousePressed(mouseX - guiLeft, mouseY - guiTop))
            return;
        CustomTextbox current = textboxes.get(currentTextbox);
        current.setFocused(hover(mouseX - guiLeft, mouseY - guiTop, current.xPosition, current.yPosition, current.width, current.height, guiScale));
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
            if (hover(mouseX - guiLeft, mouseY - guiTop, 105-16, 18-2-3, 16, 16, guiScale)) {
                try {
                    Desktop.getDesktop().browse(new URI(Constants.SKYHOUSE_PLUS_URL));
                } catch (URISyntaxException | IOException ignored) {
                }
            }
        }
    }
}
