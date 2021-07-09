package tools.skyblock.skyhouse.mcmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.config.Checkbox;
import tools.skyblock.skyhouse.mcmod.config.ConfigOption;
import tools.skyblock.skyhouse.mcmod.gui.components.CheckBox;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.managers.ConfigManager;
import tools.skyblock.skyhouse.mcmod.util.Resources;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfigGui extends GuiScreen {

    private int guiLeft, guiTop;
    private List<GuiButton> buttons = new ArrayList<>();
    private List<ConfigOption> labels = new ArrayList<>();
    private int editGuiButtonId;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        tick();
        GlStateManager.pushMatrix();
        GlStateManager.scale(2, 2, 2);
        String title = "Skyhouse";
        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel >= 2) title += "+";
        drawCenteredString(fontRendererObj, title, width/4, height/16, 0x188cd5);
        GlStateManager.popMatrix();
        int i = 0;
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (ConfigOption opt : labels) {
            drawString(fontRendererObj, opt.value(), width/3, height/4 + (height/8 * i++), 0xffffff);
            if (opt.description().length != 0 ) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
                drawTexturedModalRect(width/3-20, height/4-4 + (height/8 * (i-1)), 160, 0, 16, 16);
            }
        }
        i = 0;
        for (ConfigOption opt : labels) {
            if (mouseX > width/3-20 && mouseX < width/3-20+16 && mouseY > height/4 + (height/8 * (++i-1)) && mouseY < height/4 + (height/8 * (i-1)) + 16 && opt.description().length != 0) {
                drawHoveringText(Arrays.asList(opt.description()), mouseX, mouseY);
            }
        }
        int linkEnd = fontRendererObj.getStringWidth("https://skyblock.tools/skyhouse/flipper") + 10;
        if (mouseX >= 10 && mouseX <= linkEnd && mouseY >= height-20 && mouseY <= height-12) {
            drawString(fontRendererObj, EnumChatFormatting.UNDERLINE + "https://skyblock.tools/skyhouse/flipper" + EnumChatFormatting.RESET, 10, height-20, 0xb8b8b8);
            drawHoveringText(Arrays.asList("\u00a77Click to follow the link"), mouseX, mouseY);
        } else drawString(fontRendererObj, "https://skyblock.tools/skyhouse/flipper", 10, height-20, 0xb8b8b8);
    }

    public void tick() {

    }


    @Override
    public void initGui() {
        buttonList.clear();
        buttons.clear();
        labels.clear();
        int i = 0;
        for (Field field : ConfigManager.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigOption.class)) continue;
            if (field.isAnnotationPresent(Checkbox.class)) labels.add(field.getAnnotation(ConfigOption.class));
            String methodSuffix = Character.toUpperCase(field.getName().charAt(0))
                    + field.getName().substring(1);
            if (field.isAnnotationPresent(Checkbox.class)) {
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
                    buttons.add(new CheckBox(i, width - width /3, height /4 -4 + (height / 8 * i++), updateChecker,
                            field.getBoolean(SkyhouseMod.INSTANCE.getConfigManager()), updater));
                } catch (ReflectiveOperationException ignored) {}

            }
        }
        buttons.add(new GuiButton(editGuiButtonId = i, width-200-10, height-12-20, "Edit GUI Location"));
        buttonList.addAll(buttons);
    }

    @Override
    public void onGuiClosed() {
        SkyhouseMod.INSTANCE.getListener().closeGui();
        SkyhouseMod.INSTANCE.saveConfig();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (GuiButton button : buttons) {
            if (button.id == editGuiButtonId && button.mousePressed(mc, mouseX, mouseY)) SkyhouseMod.INSTANCE.getListener().openGui(new GuiEditGui());
            else button.mousePressed(mc, mouseX, mouseY);
        }
        int linkEnd = fontRendererObj.getStringWidth("https://skyblock.tools/skyhouse/flipper") + 10;
        if (mouseX >= 10 && mouseX <= linkEnd && mouseY >= height-20 && mouseY <= height-12) {
            try {
                //Desktop.getDesktop().browse(new URI("https://skyblock.tools/skyhouse/flipper"));
                Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
            }
            catch (URISyntaxException | IOException e) {
                System.out.println("error pog");
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == editGuiButtonId) {
            SkyhouseMod.INSTANCE.getListener().openGui(new GuiEditGui());
        }
    }
}
