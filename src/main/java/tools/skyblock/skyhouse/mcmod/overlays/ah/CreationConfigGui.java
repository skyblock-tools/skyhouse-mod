package tools.skyblock.skyhouse.mcmod.overlays.ah;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.annotations.HiddenConfigOption;
import tools.skyblock.skyhouse.mcmod.gui.CustomGui;
import tools.skyblock.skyhouse.mcmod.gui.ConfigGui;
import tools.skyblock.skyhouse.mcmod.gui.components.CheckBox;
import tools.skyblock.skyhouse.mcmod.gui.components.IconButton;
import tools.skyblock.skyhouse.mcmod.managers.ThemeManager;
import tools.skyblock.skyhouse.mcmod.util.Resources;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CreationConfigGui extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;
    private List<IconButton> extraPanelButtons = new ArrayList<>();
    private List<HiddenConfigOption> labels = new ArrayList<>();
    private List<CheckBox> buttons = new ArrayList<>();
    private List<Integer> disabled = new ArrayList<>();
    private static int page = 0;
    private int lastPageOptions;
    private int totalPages;
    private int shownOptions;

    public CreationConfigGui() {
        initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        ThemeManager.drawOverlayThemeFor("creationGUI");

        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        drawTexturedModalRect(230, -8-16, 194, 0, 16, 16);
        drawTexturedModalRect(8, -32+8, 194, 16, 16, 16);

        GlStateManager.popMatrix();


        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);

        Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "Page " + (page+1) + " of " + totalPages, 128, 12-32, 0xffffff);

        if (page != 0) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
            drawTexturedModalRect(12+25, 8-32, 0, 0, 16, 16);
        }
        if (page != totalPages-1) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
            drawTexturedModalRect(256-12-16-25, 8-32, 16, 0, 16, 16);
        }

        for (int i = page*6; i < buttons.subList(page * 6, page*6+shownOptions).size()+page*6; i++) {
            if (this.buttons.get(i).canCheck()) {
                this.buttons.get(i).drawButton(Minecraft.getMinecraft(), mouseX, mouseY, false);
            } else {
                this.buttons.get(i).drawButton(Minecraft.getMinecraft(), mouseX, mouseY, true);
                disabled.add(i);
            }
        }

        int currentHeight = 14;
        drawHorizontalLine(12, 256 - 12, currentHeight, 0xff595959);
        currentHeight += 16;
        int count = 0;
        for (HiddenConfigOption option : labels.subList(page*6, page*6+shownOptions)) {
            if (!disabled.contains(count)) {
                Utils.drawString(this, fontRendererObj, EnumChatFormatting.WHITE + option.value(), 16+16, currentHeight, 0xffffff);
            } else {
                drawString(fontRendererObj, EnumChatFormatting.GRAY + option.value(), 16+16, currentHeight, 0xffffff);
            }
            if (option.description().length != 0) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
                if (!disabled.contains(count)) drawTexturedModalRect(12, currentHeight-4, 160, 0, 16, 16);
                else drawTexturedModalRect(12, currentHeight-4, 160, 16, 16, 16);
            }
            currentHeight += 22;
            drawHorizontalLine(12, 256 - 12, currentHeight, 0xff595959);
            currentHeight += 16;
            count += 1;
        }
        disabled.clear();

        GlStateManager.popMatrix();
        GlStateManager.disableAlpha();

        currentHeight = 30;
        for (HiddenConfigOption option : labels.subList(page*6, page*6+shownOptions)) {
            if (hover(mouseX-guiLeft, mouseY-guiTop, 10, currentHeight-4, 16, 16, guiScale) && option.description().length != 0) {
                drawHoveringText(Arrays.asList(option.description()), mouseX, mouseY);
            }
            currentHeight += 38;
        }

        if (hover(mouseX-guiLeft, mouseY-guiTop, 8, -32+8, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + "Skyhouse"), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY - guiTop, 230, 8 - 32, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Settings"), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY - guiTop, 12 + 25, 8 - 32, 16, 16, guiScale) && page != 0) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY +  "Previous Page"), mouseX, mouseY);
        } else if (hover(mouseX - guiLeft, mouseY - guiTop, 256 - 12 - 16 - 25, 8 - 32, 16, 16, guiScale) && page != totalPages-1) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Next Page"), mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();

    }

    @Override
    public void initGui() {
        labels.clear();
        buttons.clear();
        buttonList.clear();
        guiScale = Utils.getScaleFactor();
        for (IconButton button : extraPanelButtons) {
            button.scales(guiScale);
        }
        int i = 0;
        int currentHeight = 26;
        for (Field field : SkyhouseMod.INSTANCE.getConfig().creationOptions.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(HiddenConfigOption.class)) continue;
            labels.add(field.getAnnotation(HiddenConfigOption.class));
            String methodSuffix = Character.toUpperCase(field.getName().charAt(0))
                    + field.getName().substring(1);
            Consumer<Boolean> updater = (checked) -> {
                try {
                    field.getDeclaringClass().getDeclaredMethod("set" + methodSuffix, boolean.class)
                            .invoke(SkyhouseMod.INSTANCE.getConfig().creationOptions, checked);
                } catch (ReflectiveOperationException e) {
                    try {
                        field.set(SkyhouseMod.INSTANCE.getConfig().creationOptions, checked);
                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    }
                }
            };
            Predicate<Boolean> updateChecker = (checked) -> {
                try {
                    return (boolean) field.getDeclaringClass().getDeclaredMethod("check" + methodSuffix, boolean.class)
                            .invoke(SkyhouseMod.INSTANCE.getConfig().creationOptions, checked);
                } catch (ReflectiveOperationException e) {
                    return true;
                }
            };
            try {
                buttons.add(new CheckBox(i, 256 - 16 - 16, currentHeight, updateChecker,
                        field.getBoolean(SkyhouseMod.INSTANCE.getConfig().creationOptions), updater));
            } catch (ReflectiveOperationException ignored) {
            }
            i++;
            if (i == 6) {
                currentHeight = 26;
            } else currentHeight += 38;
        }
        buttonList.addAll(buttons);
        tick();
    }

    public static void onGuiClose() {
        SkyhouseMod.INSTANCE.saveConfig();
    }

    @Override
    public void tick() {
        super.tick();
        guiLeft = Utils.getGuiLeft();
        guiTop = Utils.getGuiTop();
        totalPages = (int) Math.ceil((double) labels.size() / 6);
        lastPageOptions = labels.size() % 6 == 0 ?  6 : labels.size() % 6;
        if (page > totalPages - 1) page--;
        shownOptions = page == totalPages - 1 ? lastPageOptions : 6;
    }

    @Override
    public void click(int mouseX, int mouseY) {
        if (page != 0 && hover(mouseX - guiLeft, mouseY - guiTop, 12 + 25, 8 - 32, 16, 16, guiScale)) {
            page--;
        } else if (page != totalPages-1 && hover(mouseX - guiLeft, mouseY - guiTop, 256 - 12 - 16 - 25, 8 - 32, 16, 16, guiScale)) {
            page++;
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 8, -32+8, 16, 16, guiScale)) {
            SkyhouseMod.INSTANCE.getListener().openGui(new ConfigGui());
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 230, 8 - 32, 16, 16, guiScale)) {
            page = 0;
            SkyhouseMod.INSTANCE.getOverlayManager().toggleCreationConfig();
        } else {
            for (int i = page*6; i < buttons.subList(page * 6, page*6+shownOptions).size()+page*6; i++) {
                CheckBox button = this.buttons.get(i);
                if (hover(mouseX - guiLeft, mouseY - guiTop, button.xPosition, button.yPosition,button.width, button.height, guiScale)) {
                    button.pressed();
                }
            }
        }
    }

    @Override
    public void keyEvent() {

    }
}
