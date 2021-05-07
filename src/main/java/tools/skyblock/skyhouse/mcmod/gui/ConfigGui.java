package tools.skyblock.skyhouse.mcmod.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import tools.skyblock.skyhouse.mcmod.config.Checkbox;
import tools.skyblock.skyhouse.mcmod.config.ConfigOption;
import tools.skyblock.skyhouse.mcmod.gui.components.CheckBox;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.managers.ConfigManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfigGui extends GuiScreen {

    private int guiLeft, guiTop;
    private List<GuiButton> buttons = new ArrayList<>();
    private List<ConfigOption> lables = new ArrayList<>();
    private int editGuiButtonId;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        tick();
        GlStateManager.pushMatrix();
        GlStateManager.scale(2, 2, 2);
        drawCenteredString(fontRendererObj, "Skyhouse Auction Flipping", width/4, height/16, 0x188cd5);
        GlStateManager.popMatrix();
        int i = 0;
        for (ConfigOption opt : lables) {
            drawString(fontRendererObj, opt.value(), width/3, height/4 + (height/8 * i++), 0xffffff);
        }
        drawString(fontRendererObj, "https://skyblock.tools/skyhouse/flipper", 10, height-20, 0xb8b8b8);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void tick() {

    }


    @Override
    public void initGui() {
        buttonList.clear();
        buttons.clear();
        lables.clear();
        int i = 0;
        for (Field field : ConfigManager.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigOption.class)) continue;
            lables.add(field.getAnnotation(ConfigOption.class));
            String methodSuffix = Character.toUpperCase(field.getName().charAt(0))
                    + field.getName().substring(1);
            if (field.isAnnotationPresent(Checkbox.class)) {
                Consumer<Boolean> updater = (checked) -> {
                    try {
                        field.getDeclaringClass().getDeclaredMethod("set"
                                        + methodSuffix,
                                boolean.class
                        ).invoke(SkyhouseMod.INSTANCE.configManager, checked);
                    } catch (ReflectiveOperationException e) {
                        try {
                            field.set(SkyhouseMod.INSTANCE.configManager, checked);
                        } catch (IllegalAccessException illegalAccessException) {
                            illegalAccessException.printStackTrace();
                        }
                    }
                };                Predicate<Boolean> updateChecker = (checked) -> {
                    try {
                        return (boolean) field.getDeclaringClass().getDeclaredMethod("check"
                                        + methodSuffix,
                                boolean.class
                        ).invoke(SkyhouseMod.INSTANCE.configManager, checked);
                    } catch (ReflectiveOperationException e) {
                        return true;
                    }
                };
                try {
                    buttons.add(new CheckBox(i, width - width /3, height /4 -4 + (height / 8 * i++), updateChecker,
                            field.getBoolean(SkyhouseMod.INSTANCE.configManager), updater));
                } catch (ReflectiveOperationException ignored) {}

            }
        }
        buttons.add(new GuiButton(editGuiButtonId = i, width-200-20, height-20-20, "edit gui location"));
        buttonList.addAll(buttons);
    }

    @Override
    public void onGuiClosed() {
        SkyhouseMod.INSTANCE.listener.closeGui();
        SkyhouseMod.INSTANCE.saveConfig();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (GuiButton button : buttons) {
            if (button.id == editGuiButtonId && button.mousePressed(mc, mouseX, mouseY)) SkyhouseMod.INSTANCE.listener.openGui(new GuiEditGui());
            else button.mousePressed(mc, mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == editGuiButtonId) {
            SkyhouseMod.INSTANCE.listener.openGui(new GuiEditGui());
        }
    }
}
