package tools.skyblock.skyhouse.mcmod.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.SkyhouseConfig;
import tools.skyblock.skyhouse.mcmod.config.annotations.CommandButton;
import tools.skyblock.skyhouse.mcmod.config.annotations.ConfigCategory;
import tools.skyblock.skyhouse.mcmod.config.annotations.ConfigOption;
import tools.skyblock.skyhouse.mcmod.config.gui.BooleanComponent;
import tools.skyblock.skyhouse.mcmod.config.gui.CommandComponent;
import tools.skyblock.skyhouse.mcmod.config.gui.ConfigGuiComponent;
import tools.skyblock.skyhouse.mcmod.gui.helpers.ScaleManager;
import tools.skyblock.skyhouse.mcmod.gui.helpers.ScrollManager;
import tools.skyblock.skyhouse.mcmod.gui.helpers.SimpleAnimationStateManager;
import tools.skyblock.skyhouse.mcmod.managers.DataManager;
import tools.skyblock.skyhouse.mcmod.util.Resources;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigGui extends GuiScreen {

    private ScaleManager scaleManager = new ScaleManager(1024, 1024);
    private int guiWidth, guiHeight;
    private int guiLeft, guiTop;
    private int categoryBoxLeft, categoryBoxRight, categoryBoxTop, categoryBoxBottom;
    private int optionsBoxLeft, optionsBoxRight, optionsBoxTop, optionsBoxBottom;
    private List<Field> categories = new ArrayList<>();
    private Field openCategory = SkyhouseMod.INSTANCE.getConfig().openCategory;
    private int optionsHeightTotal = 0;
    private List<String> tooltip = null;

    List<ConfigGuiComponent> components = new ArrayList<>();
    List<ConfigOption> options = new ArrayList<>();

    ScaledResolution sr;

    // colours
    private static final int bgColour = 0x884d4a69;
    private static final int mainBoxColour = 0x994c497a;
    private static final int optBoxColour = 0xcc302e59;
    private static final int lightShadeColour = 0xdd27254f;
    private static final int darkShadeColour = 0xdd222240;

    private SimpleAnimationStateManager openWidthAnimation = SimpleAnimationStateManager.builder()
            .withCurrent(0)
            .withTarget(512)
            .withStep(25)
            .build()
            .start();

    private SimpleAnimationStateManager openHeightAnimation = SimpleAnimationStateManager.builder()
            .withCurrent(32)
            .withTarget(620)
            .withStep(25)
            .build();

    private SimpleAnimationStateManager fadeInAnimation = SimpleAnimationStateManager.builder()
            .withCurrent(0)
            .withTarget(255)
            .withStep(10)
            .build();

    private SimpleAnimationStateManager optionsSlideIn = SimpleAnimationStateManager.builder()
            .withCurrent(0)
            .withTarget(100)
            .withStep(5)
            .build();

    private ScrollManager categoryScroll = new ScrollManager().withHeight(this::getCategoryHeight),
            optionsScroll = new ScrollManager().withHeight(this::getOptionsHeight);

    public ConfigGui() {
        for (Field field : SkyhouseConfig.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigCategory.class)) categories.add(field);
        }
        if (openCategory != null) openNewCategory();
    }

    @Override
    public void initGui() {
        scaleManager.setSr();
        sr = new ScaledResolution(Minecraft.getMinecraft());
        categoryScroll.withScrollStart(0);
        optionsScroll.withScrollStart(0);
    }

    private void tick() {
        if (!openWidthAnimation.ended())
            openWidthAnimation.tick();
        else {
            if (!openHeightAnimation.started()) openHeightAnimation.start();
            openHeightAnimation.tick();
        }
        if (openHeightAnimation.ended()) {
            if (!fadeInAnimation.started()) fadeInAnimation.start();
            fadeInAnimation.tick();
        }
        int scale = sr.getScaleFactor();
        guiWidth = Math.round(Math.min(scaleManager.scaleX(openWidthAnimation.current / 4) * scale, width / 2.5f));
        guiHeight = Math.round(Math.min(scaleManager.scaleY(openHeightAnimation.current / 4) * scale, height / 2.5f));
        if (openWidthAnimation.ended())
            if (guiWidth < 128) {
                while (scaleManager.scaleX(openWidthAnimation.target / 4) * scale < Math.round(width / 2.5f)) {
                    openWidthAnimation.target++;
                }
            }
        if (openHeightAnimation.ended())
            if (guiHeight < 128) {
                while (scaleManager.scaleY(openHeightAnimation.target / 4) * scale < Math.round(height / 2.5f))
                    openHeightAnimation.target++;
            }

        guiLeft = width / 2 - guiWidth;
        guiTop = height / 2 - guiHeight;

        categoryScroll
                .withBoxTop(categoryBoxTop = height / 2 - guiHeight / 2 + 6)
                .withBoxBottom(categoryBoxBottom = height / 2 + guiHeight - 6);
        categoryBoxLeft = guiLeft + 6;
        categoryBoxRight = guiLeft + guiWidth / 2 - 10;

        optionsScroll
                .withBoxTop(optionsBoxTop = height / 2 - guiHeight / 2 + 6)
                .withBoxBottom(optionsBoxBottom = height / 2 + guiHeight - 6);
        optionsBoxLeft = guiLeft + guiWidth / 2 + 10;
        optionsBoxRight = width / 2 + guiWidth - 12;

    }

    private void scrollTick(int mouseX, int mouseY) {
        categoryScroll.tick(mouseX, mouseY);

        optionsScroll.tick(mouseX, mouseY);
    }


    private void openNewCategory() {
        int i = 0;
        components.clear();
        options.clear();
        optionsSlideIn.reset();
        if (openCategory == null) return;
        try {
            Object subCategory = openCategory.get(SkyhouseMod.INSTANCE.getConfig());
            for (Field field : subCategory.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(ConfigOption.class)) continue;
                options.add(field.getAnnotation(ConfigOption.class));
                String methodSuffix = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);

                Method getterMethod, setterMethod, checkerMethod;
                try {
                    getterMethod = subCategory.getClass().getDeclaredMethod("get" + methodSuffix);
                } catch (NoSuchMethodException e) {
                    getterMethod = null;
                }
                try {
                    setterMethod = subCategory.getClass().getDeclaredMethod("set" + methodSuffix, boolean.class);
                } catch (NoSuchMethodException e) {
                    setterMethod = null;
                }
                try {
                    checkerMethod = subCategory.getClass().getDeclaredMethod("check" + methodSuffix, boolean.class);
                } catch (NoSuchMethodException e) {
                    checkerMethod = null;
                }
                Method finalGetterMethod = getterMethod, finalSetterMethod = setterMethod, finalCheckerMethod = checkerMethod;

                if (field.isAnnotationPresent(CommandButton.class)) {
                    CommandButton commandButton = field.getAnnotation(CommandButton.class);
                    components.add(new CommandComponent(0, width / 2 + guiWidth - 20,
                            categoryBoxTop + 24 * i++, commandButton.label(), commandButton.value()));
                } else if (field.getType().equals(boolean.class)) {
                    components.add(new BooleanComponent(0, width / 2 + guiWidth - 20,
                            categoryBoxTop + 24 * i++,
                            checkerMethod != null ? (checked) -> {
                                try {
                                    return (boolean) finalCheckerMethod.invoke(subCategory, checked);
                                } catch (ReflectiveOperationException ignored) {
                                    return false;
                                }
                            } : (__) -> true,

                            getterMethod != null ? () -> {
                                try {
                                    return (boolean) finalGetterMethod.invoke(subCategory);
                                } catch (ReflectiveOperationException ignored) {
                                    return false;
                                }
                            } : Utils.fieldGetter(field, subCategory),
                            setterMethod != null ? (checked) -> {
                                try {
                                    finalSetterMethod.invoke(subCategory, checked);
                                } catch (ReflectiveOperationException ignored) {
                                }
                            } : Utils.fieldSetter(field, subCategory)
                    ));
                }
            }
        } catch (ReflectiveOperationException ignored) {
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        tick();
        scrollTick(mouseX, mouseY);
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawBackground();
        if (openHeightAnimation.ended()) {
            drawSections();
            drawCategories();
            if (fadeInAnimation.ended()) {
                if (!optionsSlideIn.started()) optionsSlideIn.start();
                if (openCategory != null) {
                    drawCategory(mouseX, mouseY);
                } else {
                    drawCredits(mouseX, mouseY);
                }
            }
        }
        if (tooltip != null) drawHoveringText(tooltip, mouseX, mouseY);
        tooltip = null;
    }

    private void drawBackground() {
        drawRect(width / 2 - guiWidth, height / 2 - guiHeight, width / 2 + guiWidth, height / 2 + guiHeight, bgColour);
    }

    private void drawSections() {
        // top box
        int blockAlpha = Utils.multiplyAlphaARGB(mainBoxColour, fadeInAnimation.current / 255f);
        drawRect(guiLeft + 4, guiTop + 2, width / 2 + guiWidth - 2, guiTop + guiHeight / 2 - 2, blockAlpha);
        drawRect(guiLeft + 2, guiTop + 2, guiLeft + 4, guiTop + guiHeight / 2 - 2, lightShadeColour);
        drawRect(guiLeft + 2, guiTop + guiHeight / 2 - 2, width / 2 + guiWidth - 2, guiTop + guiHeight / 2, lightShadeColour);

        // left hand box
        drawRect(guiLeft + 4, height / 2 - guiHeight / 2 + 4, guiLeft + guiWidth / 2, height / 2 + guiHeight - 4, blockAlpha);
        drawRect(guiLeft + 2, height / 2 - guiHeight / 2 + 4, guiLeft + 4, height / 2 + guiHeight - 4, lightShadeColour);
        drawRect(guiLeft + 2, height / 2 + guiHeight - 4, guiLeft + guiWidth / 2, height / 2 + guiHeight - 2, lightShadeColour);

        // main box
        drawRect(guiLeft + guiWidth / 2 + 4, height / 2 - guiHeight / 2 + 4, width / 2 + guiWidth - 2, height / 2 + guiHeight - 4, blockAlpha);
        drawRect(guiLeft + guiWidth / 2 + 2, height / 2 - guiHeight / 2 + 4, guiLeft + guiWidth / 2 + 4, height / 2 + guiHeight - 4, lightShadeColour);
        drawRect(guiLeft + guiWidth / 2 + 2, height / 2 + guiHeight - 2, width / 2 + guiWidth - 2, height / 2 + guiHeight - 4, lightShadeColour);

        GlStateManager.pushMatrix();
        GlStateManager.translate(width / 2 - fontRendererObj.getStringWidth("Skyhouse"),
                guiTop + 2 + 4 + 6, 0);
        GlStateManager.scale(2, 2, 2);
        fontRendererObj.drawString("Skyhouse", 0, 0, Utils.multiplyAlphaARGB(0xff2185ff, fadeInAnimation.current / 255f));
        GlStateManager.popMatrix();
        if (openCategory != null) {
            ConfigCategory category = openCategory.getAnnotation(ConfigCategory.class);
            Utils.drawStringCentred(EnumChatFormatting.BOLD + "Selected Category: " + category.name() +
                    EnumChatFormatting.RESET, width / 2, guiTop + 2 + 24 + 9, 0xff498ee3);
            Utils.drawStringCentred(category.description(), width / 2, guiTop + 2 + 36 + 9, 0xff498ee3);
        } else {
            Utils.drawStringCentred("by the skyblock.tools team",
                    width / 2, guiTop + 2 + 24 + 10, 0xff2185ff);
        }

    }

    private void drawCategories() {
        Utils.scissor(categoryBoxLeft, categoryBoxTop, categoryBoxRight - categoryBoxLeft, categoryBoxBottom - categoryBoxTop);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        // draw categories
        int i = 0;
        for (Field field : categories) {
            if (!field.isAnnotationPresent(ConfigCategory.class)) continue;
            drawRect(categoryBoxLeft, categoryScroll.getScrollStart() + categoryBoxTop + 28 * i, categoryBoxRight, categoryScroll.getScrollStart() + categoryBoxTop + 28 * ++i - 4, optBoxColour);
            String name = field.getAnnotation(ConfigCategory.class).name();
            fontRendererObj.drawString(name, categoryBoxLeft + ((categoryBoxRight - categoryBoxLeft) / 2 - fontRendererObj.getStringWidth(name) / 2),
                    categoryScroll.getScrollStart() + categoryBoxTop + 28 * i - 20, field.equals(openCategory) ? 0xff0BB5FF : 0xffffffff);
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // draw scrollbar
        drawRect(categoryBoxRight + 2, categoryBoxTop, categoryBoxRight + 8, categoryBoxBottom, optBoxColour);

        int[] scrollInfo = categoryScroll.getScrollInfo();
        drawRect(categoryBoxRight + 4, categoryBoxTop + 2 + scrollInfo[1], categoryBoxRight + 6, categoryBoxTop + 2 + scrollInfo[1] + scrollInfo[0], darkShadeColour);

    }

    private void drawCategory(int mouseX, int mouseY) {

        int[] scrollInfo = optionsScroll.getScrollInfo();
        drawRect(width / 2 + guiWidth - 10, height / 2 - guiHeight / 2 + 6, width / 2 + guiWidth - 4, height / 2 + guiHeight - 6, optBoxColour);
        drawRect(width / 2 + guiWidth - 8, optionsBoxTop + 2 + scrollInfo[1], width / 2 + guiWidth - 6, optionsBoxTop + 2 + scrollInfo[1] + scrollInfo[0], darkShadeColour);

        optionsSlideIn.tick();
        int slideProgress = Math.round(((float) optionsSlideIn.current / (float) optionsSlideIn.target) * (optionsBoxRight - optionsBoxLeft));
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        drawTexturedModalRect(width/2, height/8, 160, 0, 16, 16);
        Utils.scissor(optionsBoxLeft - 2, optionsBoxTop, optionsBoxRight - optionsBoxLeft, optionsBoxBottom - optionsBoxTop);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        optionsBoxLeft = optionsBoxLeft - (optionsBoxRight - optionsBoxLeft) + slideProgress;
        optionsBoxRight = optionsBoxRight - (optionsBoxRight - (guiLeft + guiWidth / 2 + 10)) + slideProgress;

        GlStateManager.disableLighting();
        int i = 0;
        for (ConfigGuiComponent component : components) {
            GlStateManager.disableLighting();

            drawRect(optionsBoxLeft, optionsScroll.getScrollStart() + optionsBoxTop + 48 * i, optionsBoxRight, optionsScroll.getScrollStart() + optionsBoxTop + 48 * (i + 1) - 8, optBoxColour);
            drawRect(optionsBoxLeft - 2, optionsScroll.getScrollStart() + optionsBoxTop + 48 * i, optionsBoxLeft, optionsScroll.getScrollStart() + optionsBoxTop + 48 * (i + 1) - 8, darkShadeColour);
            drawRect(optionsBoxLeft - 2, optionsScroll.getScrollStart() + optionsBoxTop + 48 * (i + 1) - 8, optionsBoxRight, optionsScroll.getScrollStart() + optionsBoxTop + 48 * (i + 1) - 6, darkShadeColour);
            Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);

            GlStateManager.pushMatrix();
            GlStateManager.enableLighting();
            drawTexturedModalRect(optionsBoxLeft + 4, optionsScroll.getScrollStart() + optionsBoxTop + 48 * i + 12, 160, 0, 16, 16);
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();

            fontRendererObj.drawString(options.get(i).value(), optionsBoxLeft + 24, optionsScroll.getScrollStart() + optionsBoxTop + 48 * i + 16, 0xffffffff);

            component.setCoords(optionsBoxRight - 30, optionsScroll.getScrollStart() + optionsBoxTop + 48 * i + 12);
            component.draw(mouseX, mouseY);
            if (mouseX > optionsBoxLeft + 4 && mouseX < optionsBoxLeft + 20 && mouseY > optionsScroll.getScrollStart() + optionsBoxTop + 48 * i + 12 && mouseY < optionsScroll.getScrollStart() + optionsBoxTop + 48 * i + 28 && optionsSlideIn.ended()) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                tooltip = Arrays.asList(options.get(i).description());
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }
            i++;
            GlStateManager.enableLighting();
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);


    }

    private void drawCredits(int mouseX, int mouseY) {

        int[] scrollInfo = optionsScroll.getScrollInfo();
        drawRect(width / 2 + guiWidth - 10, height / 2 - guiHeight / 2 + 6, width / 2 + guiWidth - 4, height / 2 + guiHeight - 6, optBoxColour);
        drawRect(width / 2 + guiWidth - 8, optionsBoxTop + 2 + scrollInfo[1], width / 2 + guiWidth - 6, optionsBoxTop + 2 + scrollInfo[1] + scrollInfo[0], darkShadeColour);


        optionsSlideIn.tick();
        int slideProgress = Math.round(((float) optionsSlideIn.current / (float) optionsSlideIn.target) * (optionsBoxRight - optionsBoxLeft));
        Utils.scissor(optionsBoxLeft - 2, optionsBoxTop, optionsBoxRight - optionsBoxLeft, optionsBoxBottom - optionsBoxTop);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        optionsBoxLeft = optionsBoxLeft - (optionsBoxRight - optionsBoxLeft) + slideProgress;
        optionsBoxRight = optionsBoxRight - (optionsBoxRight - (guiLeft + guiWidth / 2 + 10)) + slideProgress;

        drawRect(optionsBoxLeft, optionsScroll.getScrollStart() + optionsBoxTop, optionsBoxRight, optionsScroll.getScrollStart() + optionsBoxTop + getOptionsHeight() - 2, optBoxColour);

        drawRect(optionsBoxLeft - 2, optionsScroll.getScrollStart() + optionsBoxTop, optionsBoxLeft, optionsScroll.getScrollStart() + optionsBoxTop + getOptionsHeight() - 2, darkShadeColour);
        drawRect(optionsBoxLeft - 2, optionsScroll.getScrollStart() + optionsBoxTop + getOptionsHeight() - 2, optionsBoxRight, optionsScroll.getScrollStart() + optionsBoxTop + getOptionsHeight(), darkShadeColour);

        int stringWidth = 0;

        for (JsonElement el : DataManager.contributors) {
            stringWidth = Math.max(stringWidth, fontRendererObj.getStringWidth(el.getAsJsonObject().get("name").getAsString()));
        }
//        optionsScroll.getScrollStart() + optionsBoxTop + 8 + 4
        int shpBoxCentreX = (optionsBoxLeft + stringWidth + 10) + ((optionsBoxRight - 4) - (optionsBoxLeft + stringWidth + 16)) / 2;
        Utils.drawStringCentred("Skyhouse+", shpBoxCentreX, optionsScroll.getScrollStart() + optionsBoxTop + 14, 0xff00ff00);

        optionsHeightTotal = 28;
        int i = 0;
        if (SkyhouseMod.INSTANCE.getAuthenticationManager().privLevel > 1) {
            optionsHeightTotal += 8;
            String[] text = Utils.wrapText("You have a Skyhouse+ subscription", EnumChatFormatting.GREEN, (optionsBoxRight - 16) - (optionsBoxLeft + stringWidth + 16));
            for (i = 0; i < text.length; i++)
                fontRendererObj.drawString(text[i], optionsBoxLeft + stringWidth + 24, optionsScroll.getScrollStart() + optionsBoxTop + 28 + 12 * i, 0xff00ff00);
        } else {
            String[] text = Utils.wrapText("You do not have a Skyhouse+ subscription. Get unlimited profit bin->bin flips, filters, and more with Skyhouse+",
                    EnumChatFormatting.RED, (optionsBoxRight - 10) - (optionsBoxLeft + stringWidth + 16));
            for (i = 0; i < text.length; i++) {
                optionsHeightTotal += 8;
                fontRendererObj.drawString(text[i], optionsBoxLeft + stringWidth + 24 - 6, optionsScroll.getScrollStart() + optionsBoxTop + 28 + 12 * i, 0xffffffff);
            }
        }
        drawRect(optionsBoxLeft + stringWidth + 10, optionsScroll.getScrollStart() + optionsBoxTop + 8, optionsBoxLeft + stringWidth + 18 - 6, optionsScroll.getScrollStart() + optionsBoxTop + 28 + 12 * i, darkShadeColour);
        drawRect(optionsBoxLeft + stringWidth + 10, optionsScroll.getScrollStart() + optionsBoxTop + 28 + 12 * i, optionsBoxRight - 4 - 6, optionsScroll.getScrollStart() + optionsBoxTop + 28 + 12 * i + 2, darkShadeColour);

        drawRect(optionsBoxLeft + stringWidth + 10, optionsScroll.getScrollStart() + optionsBoxTop + 8, optionsBoxRight - 4 - 6, optionsScroll.getScrollStart() + optionsBoxTop + 10, lightShadeColour);
        drawRect(optionsBoxRight - 6 - 6, optionsScroll.getScrollStart() + optionsBoxTop + 10, optionsBoxRight - 4 - 6, optionsScroll.getScrollStart() + optionsBoxTop + 28 + 12 * i, lightShadeColour);

        i = 0;

        for (JsonElement el : DataManager.contributors) {
            GlStateManager.disableLighting();
            JsonObject obj = el.getAsJsonObject();
            EnumChatFormatting colour = EnumChatFormatting.getValueByName(obj.get("colour").getAsString());
            String text = obj.get("name").getAsString();

            if (mouseY > optionsBoxTop && mouseY < optionsBoxBottom && mouseX > 4 + optionsBoxLeft && mouseX < 6 + optionsBoxLeft + fontRendererObj.getStringWidth(text) &&
                    mouseY > optionsScroll.getScrollStart() + optionsBoxTop - 1 + 16 * (i + 1) - 6 && mouseY < optionsScroll.getScrollStart() + optionsBoxTop - 4 + 16 * (i + 1) + 6 && optionsSlideIn.ended()) {
                fontRendererObj.drawString(colour + "" + EnumChatFormatting.UNDERLINE + text + EnumChatFormatting.RESET,
                        optionsBoxLeft + 6, optionsScroll.getScrollStart() + optionsBoxTop - 6 + 16 * ++i, 0xffffffff);
                JsonArray note = obj.get("note").getAsJsonArray();
                if (note.size() > 0) tooltip = Utils.jsonArrayToStringList(note);
            } else
                fontRendererObj.drawString(colour + text + EnumChatFormatting.RESET,
                        optionsBoxLeft + 6, optionsScroll.getScrollStart() + optionsBoxTop - 6 + 16 * ++i, 0xffffffff);
            GlStateManager.enableLighting();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseX = Mouse.getX() * width / Minecraft.getMinecraft().displayWidth;
        int mouseY = height - Mouse.getY() * height / Minecraft.getMinecraft().displayHeight - 1;
        if (mouseX > categoryBoxLeft && mouseX < categoryBoxRight && mouseY > categoryBoxTop && mouseY < categoryBoxBottom) {
            categoryScroll.withScrollStart(categoryScroll.getScrollStart() + Mouse.getEventDWheel() / 5);
            categoryScroll.clampScroll();
        }
        if (mouseX > optionsBoxLeft && mouseX < optionsBoxRight && mouseY > optionsBoxTop && mouseY < optionsBoxBottom) {
            optionsScroll.withScrollStart(optionsScroll.getScrollStart() + Mouse.getEventDWheel() / 5);
            optionsScroll.clampScroll();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int[] categoryScrollInfo = categoryScroll.getScrollInfo();
        if (mouseX > categoryBoxRight + 2 && mouseX < categoryBoxRight + 8 &&
                mouseY > categoryBoxTop + 2 + categoryScrollInfo[1] &&
                mouseY < categoryBoxTop + 2 + categoryScrollInfo[1] + categoryScrollInfo[0]) {
            categoryScroll.withScrolling(true).withMouseOffset(mouseY - (categoryBoxTop + 2 + categoryScrollInfo[1]));
        }

        int[] optionsScrollInfo = optionsScroll.getScrollInfo();
        if (mouseX > optionsBoxRight + 4 && mouseX < optionsBoxRight + 10 &&
                mouseY > optionsBoxTop + 2 + optionsScrollInfo[1] &&
                mouseY < optionsBoxTop + 2 + optionsScrollInfo[1] + optionsScrollInfo[0]) {
            optionsScroll.withScrolling(true).withMouseOffset(mouseY - (optionsBoxTop + 2 + optionsScrollInfo[1]));
        }

        if (mouseX > categoryBoxLeft && mouseX < categoryBoxRight && mouseY > categoryBoxTop && mouseY < categoryBoxBottom) {
            int i = 0;
            for (Field field : categories) {
                if (!field.isAnnotationPresent(ConfigCategory.class)) continue;
                if (mouseY > categoryScroll.getScrollStart() + categoryBoxTop + 28 * i && mouseY < categoryScroll.getScrollStart() + categoryBoxTop + 28 * ++i - 4) {
                    openCategory = field.equals(openCategory) ? null : field;
                    openNewCategory();
                    break;
                }
            }
        }

        if (openCategory == null) {
            int i = 0;
            for (JsonElement el : DataManager.contributors) {
                if (optionsBoxTop + 16 + 16 * (i + 1) + 4 > optionsBoxBottom) break;
                JsonObject obj = el.getAsJsonObject();
                i++;
                if (mouseX > 4 + optionsBoxLeft && mouseX < 6 + optionsBoxLeft + fontRendererObj.getStringWidth(obj.get("name").getAsString())
                        && mouseY > optionsScroll.getScrollStart() + optionsBoxTop - 1 + 16 * i - 6
                        && mouseY < optionsScroll.getScrollStart() + optionsBoxTop - 4 + 16 * i + 6) {
                    String url = obj.get("url").getAsString();
                    if (!url.isEmpty()) {
                        Utils.browseTo(url);
                    }
                }
            }
        }

        for (ConfigGuiComponent component : components) {
            component.mousePressed(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        categoryScroll.withScrolling(false);
        optionsScroll.withScrolling(false);
    }

    private int getCategoryHeight() {
        return categories.size() * 28 - 4;
    }

    private int getOptionsHeight() {
        return openCategory == null ? Math.max(optionsHeightTotal, Math.max((DataManager.contributors.size() + 1) * 16 + 16, optionsBoxBottom - optionsBoxTop)) : options.size() * 48 - 8;
    }


    @Override
    public void onGuiClosed() {
        SkyhouseMod.INSTANCE.saveConfig();
        SkyhouseMod.INSTANCE.getConfig().openCategory = openCategory;
    }
}
