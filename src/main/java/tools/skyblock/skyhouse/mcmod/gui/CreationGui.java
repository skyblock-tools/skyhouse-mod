package tools.skyblock.skyhouse.mcmod.gui;


import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.components.IconButton;
import tools.skyblock.skyhouse.mcmod.util.Resources;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tools.skyblock.skyhouse.mcmod.util.Utils.formatNumber;
import static tools.skyblock.skyhouse.mcmod.util.Utils.getInternalNameFromNBT;

public class CreationGui extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;
    private final Pattern REGEX_PATTERN_FOR_HOT_POTATO_BOOKS_BONUS_FOR_ITEM_VALUE_CALCULATION = Pattern.compile(EnumChatFormatting.YELLOW + "\\(\\+(\\d+)\\)");
    private final Pattern REGEX_PATTERN_FOR_ART_OF_WAR_BONUS_FOR_ITEM_VALUE_CALCULATION = Pattern.compile(EnumChatFormatting.GOLD + "\\[\\+(\\d+)\\]");
    private final Pattern REGEX_PATTERN_FOR_PET_ITEM_BONUS_FOR_ITEM_VALUE_CALCULATION = Pattern.compile(EnumChatFormatting.GOLD + "Held Item: \u00a7[a-f\\d](.+)");
    private List<IconButton> extraPanelButtons = new ArrayList<>();
    private static boolean isPreviewTooltip = false;

    ItemStack toRender = null;

    public CreationGui() {
        initGui();
    }

    public static List<String> processTooltip(List<String> tooltip) {
        List<String> list;
        if (isPreviewTooltip) {
            list = new ArrayList<>();
            int count = 0;
            int colourLocation = -1;
            String colourLine = null;
            for (String line : tooltip) {
                if (line.contains("Color")) {
                    colourLocation = count;
                    colourLine = line;
                } else if (!((colourLocation == -1 ? count <= 1 : count <= 2) || line.contains("Click to pickup!")) || (colourLocation != -1 && count-2 == colourLocation)) {
                    list.add(line);
                    if (colourLocation != -1 && count-2 == colourLocation) list.add(colourLine);
                } else if (line.contains("Click to pickup!")) list.remove(list.size()-1);
                count++;
            }
        } else {
            list = new ArrayList<>(tooltip);
        }
        return list;
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

        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        drawTexturedModalRect(230, -8-16, 194, 0, 16, 16);
        drawTexturedModalRect(8, -32+8, 194, 0, 16, 16);
        if (SkyhouseMod.INSTANCE.getOverlayManager().hasFlips()) {
            drawTexturedModalRect(8+22, -32+8+1, 32, 0, 16, 16);
        }

        drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "Item Value Estimation", 128, 12-32, 0xffffff);

        GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
        ContainerChest container = (ContainerChest) chest.inventorySlots;
        IInventory lower = container.getLowerChestInventory();

        ItemStack stack = lower.getStackInSlot(13);

        boolean isPet = false;

        if (stack != null && !stack.serializeNBT().getString("id").equals("minecraft:stone_button")) {
            NBTTagCompound nbt = stack.getTagCompound();
            String[] lore = Utils.getLoreFromNBT(nbt);
            if (lore.length > 1) {

                GlStateManager.pushMatrix();
                GlStateManager.scale(2, 2, 2);
                GlStateManager.enableDepth();
                Utils.renderItem(stack, 7, 7);
                GlStateManager.disableDepth();
                GlStateManager.popMatrix();
                Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_COMPONENTS);
                drawTexturedModalRect(12, 14, 5, 6, 37, 33);

                if (hover(mouseX-guiLeft, mouseY-guiTop, 14, 14, 32, 32, guiScale)) {
                    isPreviewTooltip = true;
                    stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

                } else if (isPreviewTooltip) isPreviewTooltip = false;

                int currentHeight = 15;
                String unmodifiedName = lore[1];
                isPet = unmodifiedName.contains("[Lvl ");
                String nameToDraw = "";
                if (!isPet) {
                    nameToDraw = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(lore[1], 256-54-15).trim();
                } else {
                    nameToDraw = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(lore[1], 256-54-15-18).trim();
                }
                if (!nameToDraw.equals(lore[1])) nameToDraw += (EnumChatFormatting.GRAY + "...");
                drawString(fontRendererObj, nameToDraw + EnumChatFormatting.RESET, 54, currentHeight, 0xffffff);
                currentHeight += 22;
                String internalName = getInternalNameFromNBT(nbt);

                if (SkyhouseMod.INSTANCE.lowestBins == null) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(1, 1, 1);
                    drawCenteredString(fontRendererObj, EnumChatFormatting.RED + "Error connecting to", 128, 128-10-20, 0xffffff);
                    drawCenteredString(fontRendererObj, EnumChatFormatting.RED + "Moulberry's lowest bins API", 128, 128-10-6, 0xffffff);
                    drawCenteredString(fontRendererObj, EnumChatFormatting.RED + "D:", 128, 128-10+8, 0xffffff);
                    GlStateManager.popMatrix();
                } else if (!SkyhouseMod.INSTANCE.lowestBins.has(internalName)) {
                    drawString(fontRendererObj, EnumChatFormatting.RED + "Could not value this item", 54, currentHeight, 0xffffff);
                } else {
                    double lowestBinValue = SkyhouseMod.INSTANCE.lowestBins.get(internalName).getAsDouble();
                    double value = lowestBinValue;

                    if (isPet) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
                        drawTexturedModalRect(256-16-12, 35-24, 160, 0, 16, 16);
                    }

                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "Lowest Bin: ", 54, currentHeight, 0xffffff);
                    drawString(fontRendererObj, EnumChatFormatting.GREEN + formatNumber(lowestBinValue), 256 - 14 - fontRendererObj.getStringWidth(formatNumber(value)), currentHeight, 0xffffff);

                    currentHeight += 16;
                    drawHorizontalLine(12, 256 - 12, currentHeight, 0xff595959);

                    currentHeight += 8;
                    drawString(fontRendererObj, "Additional", 14, currentHeight, 0xffffff);
                    drawString(fontRendererObj, "Value", 256 - 14 - fontRendererObj.getStringWidth("Value"), currentHeight, 0xffffff);

                    currentHeight += 20;


                    if (!isPet) {

                        //TODO: add everything that could modify the value of an item here

                        if (SkyhouseMod.INSTANCE.getConfigManager().getIncludeHpbs()) {
                            for (String entry : lore) {
                                final Matcher matcher = REGEX_PATTERN_FOR_HOT_POTATO_BOOKS_BONUS_FOR_ITEM_VALUE_CALCULATION.matcher(entry);
                                if (!matcher.find()) continue;
                                final int amount = Integer.parseInt(matcher.group(1)) / 2;
                                drawString(fontRendererObj, EnumChatFormatting.GRAY + "Hot Potato Books: " + amount, 14, currentHeight, 0xffffff);
                                if (SkyhouseMod.INSTANCE.bazaarData != null) {
                                    int hotPotatoPrice = SkyhouseMod.INSTANCE.bazaarData.get("products").getAsJsonObject().get("HOT_POTATO_BOOK").getAsJsonObject().get("quick_status").getAsJsonObject().get("buyPrice").getAsInt();
                                    int hpbBonus = 0;
                                    if (amount <= 10) {
                                        hpbBonus = (amount * hotPotatoPrice);
                                    } else {
                                        int fumingPotatoPrice = SkyhouseMod.INSTANCE.bazaarData.get("products").getAsJsonObject().get("FUMING_POTATO_BOOK").getAsJsonObject().get("quick_status").getAsJsonObject().get("buyPrice").getAsInt();
                                        hpbBonus = (10 * hotPotatoPrice) + (amount * fumingPotatoPrice);
                                    }
                                    value += hpbBonus;
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(hpbBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(hpbBonus)), currentHeight, 0xffffff);
                                    currentHeight += 15;
                                } else {
                                    drawString(fontRendererObj, EnumChatFormatting.RED + "No Data", 256 - 14 - fontRendererObj.getStringWidth("No Data"), currentHeight, 0xffffff);
                                    currentHeight += 15;
                                }
                                break;
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfigManager().getIncludeAow()) {
                            for (String entry : lore) {
                                final Matcher matcher = REGEX_PATTERN_FOR_ART_OF_WAR_BONUS_FOR_ITEM_VALUE_CALCULATION.matcher(entry);
                                if (!matcher.find()) continue;
                                int aowBonus = SkyhouseMod.INSTANCE.lowestBins.get("THE_ART_OF_WAR").getAsInt();
                                drawString(fontRendererObj, EnumChatFormatting.GRAY + "Art of War", 14, currentHeight, 0xffffff);
                                drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(aowBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(aowBonus)), currentHeight, 0xffffff);
                                value += aowBonus;
                                currentHeight += 15;

                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfigManager().getIncludeFrags()) {
                            if (unmodifiedName.contains("\u269A")) {
                                final String fragType = Utils.fragType(internalName);
                                if (fragType != null) {
                                    int fragBonus = SkyhouseMod.INSTANCE.lowestBins.get(fragType).getAsInt() * 8;
                                    value += fragBonus;
                                    switch (fragType) {
                                        case "BONZO_FRAGMENT":
                                            drawString(fontRendererObj, EnumChatFormatting.GRAY + "Fragged (Bonzo)", 14, currentHeight, 0xffffff);
                                            break;
                                        case "SCARF_FRAGMENT":
                                            drawString(fontRendererObj, EnumChatFormatting.GRAY + "Fragged (Scarf)", 14, currentHeight, 0xffffff);
                                            break;
                                        case "LIVID_FRAGMENT":
                                            drawString(fontRendererObj, EnumChatFormatting.GRAY + "Fragged (Livid)", 14, currentHeight, 0xffffff);
                                            break;
                                    }
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(fragBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(fragBonus)), currentHeight, 0xffffff);
                                } else {
                                    drawString(fontRendererObj, EnumChatFormatting.GRAY + "Fragged (Unknown)", 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+0", 256 - 14 - fontRendererObj.getStringWidth("+0"), currentHeight, 0xffffff);
                                }
                                currentHeight += 15;
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfigManager().getIncludeMasterStars()) {
                            final char[] nameChars = unmodifiedName.toCharArray();
                            int masterStarCount = 0;
                            int masterStarBonus = 0;
                            for (int i = 0; i < unmodifiedName.length(); i++) {
                                if (nameChars[i] == '\u272A' && nameChars[i - 1] == 'c') {
                                    masterStarCount += 1;
                                }
                            }
                            if (masterStarCount > 0) {
                                int count = masterStarCount;
                                while (count > 0) {
                                    if (count == 1) {
                                        masterStarBonus += SkyhouseMod.INSTANCE.lowestBins.get("FIRST_MASTER_STAR").getAsInt();
                                    } else if (count == 2) {
                                        masterStarBonus += SkyhouseMod.INSTANCE.lowestBins.get("SECOND_MASTER_STAR").getAsInt();
                                    } else if (count == 3) {
                                        masterStarBonus += SkyhouseMod.INSTANCE.lowestBins.get("THIRD_MASTER_STAR").getAsInt();
                                    } else if (count == 4) {
                                        masterStarBonus += SkyhouseMod.INSTANCE.lowestBins.get("FOURTH_MASTER_STAR").getAsInt();
                                    }
                                    count--;
                                }
                                value += masterStarBonus;
                                drawString(fontRendererObj, EnumChatFormatting.GRAY + "Master Stars: " + masterStarCount, 14, currentHeight, 0xffffff);
                                drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(masterStarBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(masterStarBonus)), currentHeight, 0xffffff);
                                currentHeight += 15;
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfigManager().getIncludeRecombs()) {
                            for (String line : lore) {
                                if (line.contains(EnumChatFormatting.OBFUSCATED.toString())) {
                                    int recombPrice = SkyhouseMod.INSTANCE.bazaarData.get("products").getAsJsonObject().get("RECOMBOBULATOR_3000").getAsJsonObject().get("quick_status").getAsJsonObject().get("buyPrice").getAsInt();
                                    value += recombPrice;
                                    drawString(fontRendererObj, EnumChatFormatting.GRAY + "Recombobulated", 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(recombPrice), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(recombPrice)), currentHeight, 0xffffff);
                                    currentHeight += 15;
                                }
                            }
                        }


                        if (SkyhouseMod.INSTANCE.getConfigManager().getIncludeAmount()) {
                            final int stackSize = stack.stackSize;
                            if (stackSize > 1) {
                                drawString(fontRendererObj, EnumChatFormatting.GRAY + "Amount", 14, 256 - 20 - 15, 0xffffff);
                                value *= stackSize;
                                drawString(fontRendererObj, EnumChatFormatting.GREEN + "x" + stackSize, 256 - 14 - fontRendererObj.getStringWidth("x" + stackSize), 256 - 20 - 15, 0xffffff);
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfigManager().getIncludeReforge()) {
                            final String nameWithoutFrags = unmodifiedName.replaceFirst("\u269A", "");
                            final String[] splitName = nameWithoutFrags.split("\\s");
                            String reforge = splitName[0].replaceAll("\u00a7[0-9a-f]", "");
                            if (reforge.equals("Wise") && splitName[1].equals("dragon")) reforge = "null";
                            if (reforge.equals("Very")) reforge = "Very Wise";
                            String reforgeInternalName = null;
                            int reforgeCost = -1;
                            String rarity = null;
                            for (Entry entry : SkyhouseMod.INSTANCE.reforgeData.entrySet()) {
                                JsonObject reforgeJson = SkyhouseMod.gson.toJsonTree(entry.getValue()).getAsJsonObject();
                                if (reforgeJson.get("reforgeName").getAsString().equals(reforge)) {
                                    reforgeInternalName = reforgeJson.get("internalName").getAsString();
                                    for (String line : lore) {
                                        if (line.contains("VERY SPECIAL")) {
                                            rarity = "VERY_SPECIAL";
                                            break;
                                        } else if (line.contains("SPECIAL")) {
                                            rarity = "SPECIAL";
                                            break;
                                        } else if (line.contains("MYTHIC")) {
                                            rarity = "MYTHIC";
                                            break;
                                        } else if (line.contains("LEGENDARY")) {
                                            rarity = "LEGENDARY";
                                            break;
                                        } else if (line.contains("EPIC")) {
                                            rarity = "EPIC";
                                            break;
                                        } else if (line.contains("RARE")) {
                                            rarity = "RARE";
                                            break;
                                        } else if (line.contains("UNCOMMON")) {
                                            rarity = "UNCOMMON";
                                            break;
                                        } else if (line.contains("COMMON")) {
                                            rarity = "COMMON";
                                            break;
                                        }
                                    }
                                    if (rarity != null) reforgeCost = reforgeJson.get("reforgeCosts").getAsJsonObject().get(rarity).getAsInt();
                                    break;
                                }
                            }
                            if (reforgeInternalName != null) {
                                int reforgeBonus = SkyhouseMod.INSTANCE.lowestBins.get(reforgeInternalName).getAsInt();
                                value += reforgeBonus;
                                drawString(fontRendererObj, EnumChatFormatting.GRAY + "Reforged: " + reforge, 14, currentHeight, 0xffffff);
                                drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(reforgeBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(reforgeBonus)), currentHeight, 0xffffff);
                                currentHeight += 15;
                                if (SkyhouseMod.INSTANCE.getConfigManager().getIncludeReforgeCost() && rarity != null) {
                                    value += reforgeCost;
                                    drawString(fontRendererObj, EnumChatFormatting.GRAY + "- Reforge Cost: " + Character.toUpperCase(rarity.charAt(0)) + rarity.toLowerCase().substring(1), 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(reforgeCost), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(reforgeCost)), currentHeight, 0xffffff);
                                    currentHeight += 15;
                                }
                            }
                        }

                    } else {

                        //TODO: add everything that could modify the value of a pet here

                        if (SkyhouseMod.INSTANCE.getConfigManager().getIncludePetItems()) {
                            for (String entry : lore) {
                                final Matcher matcher = REGEX_PATTERN_FOR_PET_ITEM_BONUS_FOR_ITEM_VALUE_CALCULATION.matcher(entry);
                                if (!matcher.find()) continue;
                                final String possibleInternalName = matcher.group(1).toUpperCase().replaceAll("\\s", "_");
                                int petItemBonus = -1;
                                try {
                                    petItemBonus = SkyhouseMod.INSTANCE.lowestBins.get(possibleInternalName).getAsInt();
                                } catch (NullPointerException bonusIsNull) {
                                    petItemBonus = SkyhouseMod.INSTANCE.lowestBins.get("PET_ITEM_" + possibleInternalName).getAsInt();
                                }
                                value += petItemBonus;
                                drawString(fontRendererObj, EnumChatFormatting.GRAY + "Pet Item: " + matcher.group(1), 14, currentHeight, 0xffffff);
                                drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(petItemBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(petItemBonus)), currentHeight, 0xffffff);
                                currentHeight += 15;
                            }
                        }



                    }


                    if (currentHeight == 81) {
                        drawString(fontRendererObj, EnumChatFormatting.GRAY + "None", 14, currentHeight, 0xffffff);
                        drawString(fontRendererObj, EnumChatFormatting.GRAY + "0", 256-14-fontRendererObj.getStringWidth("0"), currentHeight, 0xffffff);
                    }

                    drawString(fontRendererObj, "Estimated Total Value: ", 14, 256-20, 0xffffff);
                    drawString(fontRendererObj, EnumChatFormatting.GREEN + formatNumber(value), 256-14-fontRendererObj.getStringWidth(formatNumber(value)), 256-20, 0xffffff);

                }
            }

        } else {
            drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "No Item Found", 128, 120, 0xffffff);
        }

        GlStateManager.popMatrix();

        if (hover(mouseX-guiLeft, mouseY-guiTop, 8, -32+8, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + "Skyhouse Settings"), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY - guiTop, 230, 8 - 32, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Settings" + EnumChatFormatting.RESET), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 8+22, -32+8+1, 16, 16, guiScale) && SkyhouseMod.INSTANCE.getOverlayManager().hasFlips()) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + "Flip List"), mouseX, mouseY);
        } else if (isPet && hover(mouseX - guiLeft, mouseY - guiTop, 256 - 16 - 12, 35 - 24, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Pet Values are based off level 1 pets of that" + EnumChatFormatting.RESET,
                    EnumChatFormatting.GRAY + "rarity and do not take level into account." + EnumChatFormatting.RESET), mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();

    }

    @Override
    public void initGui() {
        tick();
        guiScale = Utils.getScaleFactor();
        for (IconButton button : extraPanelButtons) {
            button.scales(guiScale);
        }
    }

    @Override
    public void tick() {
        super.tick();
        guiLeft = Utils.getGuiLeft();
        guiTop = Utils.getGuiTop();
    }

    @Override
    public void click(int mouseX, int mouseY) {
        if (hover(mouseX-guiLeft, mouseY-guiTop, 8, -32+8, 16, 16, guiScale)) {
            SkyhouseMod.INSTANCE.getListener().openGui(new ConfigGui());
        } else if (hover(mouseX-guiLeft, mouseY - guiTop, 230, 8 - 32, 16, 16, guiScale)) {
            SkyhouseMod.INSTANCE.getOverlayManager().toggleCreationConfig();
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 8+22, -32+8+1, 16, 16, guiScale) && SkyhouseMod.INSTANCE.getOverlayManager().hasFlips()) {
            SkyhouseMod.INSTANCE.getOverlayManager().toggleFlipListCreationGui();
        }
    }

    @Override
    public void keyEvent() {

    }
}
