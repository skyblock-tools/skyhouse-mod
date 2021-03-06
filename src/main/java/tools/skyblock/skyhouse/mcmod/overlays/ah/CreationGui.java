package tools.skyblock.skyhouse.mcmod.overlays.ah;


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
import tools.skyblock.skyhouse.mcmod.gui.CustomGui;
import tools.skyblock.skyhouse.mcmod.gui.ConfigGui;
import tools.skyblock.skyhouse.mcmod.gui.components.IconButton;
import tools.skyblock.skyhouse.mcmod.managers.DataManager;
import tools.skyblock.skyhouse.mcmod.managers.ThemeManager;
import tools.skyblock.skyhouse.mcmod.util.Resources;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import static tools.skyblock.skyhouse.mcmod.util.Utils.formatNumber;
import static tools.skyblock.skyhouse.mcmod.util.Utils.getInternalNameFromNBT;

public class CreationGui extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;
    private List<IconButton> extraPanelButtons = new ArrayList<>();
    private static boolean isPreviewTooltip = false;

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
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        ThemeManager.drawAhOverlayThemeFor("creationGUI");

        Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.GUI_ICONS);
        drawTexturedModalRect(230, -8-16, 194, 0, 16, 16);

        drawTexturedModalRect(8, -32+8, 194, 16, 16, 16);
        if (SkyhouseMod.INSTANCE.getOverlayManager().hasFlips()) {
            drawTexturedModalRect(8+22, -32+8+1, 32, 0, 16, 16);
        }

        Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "Item Value Estimation", 128, 12-32, 0xffffff);

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

                if (DataManager.lowestBins == null) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(1, 1, 1);
                    drawCenteredString(fontRendererObj, EnumChatFormatting.RED + "Error connecting to", 128, 128-10-20, 0xffffff);
                    drawCenteredString(fontRendererObj, EnumChatFormatting.RED + "Moulberry's lowest bins API", 128, 128-10-6, 0xffffff);
                    drawCenteredString(fontRendererObj, EnumChatFormatting.RED + "D:", 128, 128-10+8, 0xffffff);
                    GlStateManager.popMatrix();
                } else if (!DataManager.lowestBins.has(internalName)) {
                    drawString(fontRendererObj, EnumChatFormatting.RED + "Could not value this item", 54, currentHeight, 0xffffff);
                } else {
                    double lowestBinValue = DataManager.lowestBins.get(internalName).getAsDouble();
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
                    Utils.drawString(this, fontRendererObj, "Additional", 14, currentHeight, 0xffffff);
                    Utils.drawString(this, fontRendererObj, "Value", 256 - 14 - fontRendererObj.getStringWidth("Value"), currentHeight, 0xffffff);

                    currentHeight += 20;


                    if (!isPet) {

                        //TODO: add everything that could modify the value of an item here

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeHpbs) {
                            NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");
                            final int amount = extraAttributes.getInteger("hot_potato_count");
                            if (amount > 0) {
                                Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Hot Potato Books: " + amount, 14, currentHeight, 0xffffff);
                                if (DataManager.bazaarData != null) {
                                    int hotPotatoPrice = DataManager.bazaarData.get("products").getAsJsonObject().get("HOT_POTATO_BOOK").getAsJsonObject().get("quick_status").getAsJsonObject().get("sellPrice").getAsInt();
                                    int hpbBonus = 0;
                                    if (amount <= 10) {
                                        hpbBonus = (amount * hotPotatoPrice);
                                    } else {
                                        int fumingPotatoPrice = DataManager.bazaarData.get("products").getAsJsonObject().get("FUMING_POTATO_BOOK").getAsJsonObject().get("quick_status").getAsJsonObject().get("sellPrice").getAsInt();
                                        hpbBonus = (10 * hotPotatoPrice) + ((amount - 10) * fumingPotatoPrice);
                                    }
                                    value += hpbBonus;
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(hpbBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(hpbBonus)), currentHeight, 0xffffff);
                                } else {
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.RED + "No Data", 256 - 14 - fontRendererObj.getStringWidth("No Data"), currentHeight, 0xffffff);
                                }
                                currentHeight += 15;
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeAow) {
                            NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");
                            final int amount = extraAttributes.getInteger("art_of_war_count");
                            if (amount > 0) {
                                if (DataManager.lowestBins.has("THE_ART_OF_WAR")) {
                                    int aowBonus = DataManager.lowestBins.get("THE_ART_OF_WAR").getAsInt() * amount;
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Art of War", 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(aowBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(aowBonus)), currentHeight, 0xffffff);
                                    value += aowBonus;
                                } else {
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "No Price Data for THE_ART_OF_WAR", 14, currentHeight, 0xffffff);
                                }
                                currentHeight += 15;
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeFrags) {
                            if (unmodifiedName.contains("\u269A")) {
                                final String fragType = Utils.fragType(internalName);
                                if (fragType != null) {
                                    if (DataManager.lowestBins.has(fragType)) {
                                        switch (fragType) {
                                            case "BONZO_FRAGMENT":
                                                Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Fragged (Bonzo)", 14, currentHeight, 0xffffff);
                                                break;
                                            case "SCARF_FRAGMENT":
                                                Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Fragged (Scarf)", 14, currentHeight, 0xffffff);
                                                break;
                                            case "LIVID_FRAGMENT":
                                                Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Fragged (Livid)", 14, currentHeight, 0xffffff);
                                                break;
                                        }
                                        int fragBonus = DataManager.lowestBins.get(fragType).getAsInt() * 8;
                                        value += fragBonus;
                                        drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(fragBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(fragBonus)), currentHeight, 0xffffff);
                                    } else {
                                        Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "No Price Data for " + fragType, 14, currentHeight, 0xffffff);
                                    }
                                } else {
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Fragged (Unknown)", 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+0", 256 - 14 - fontRendererObj.getStringWidth("+0"), currentHeight, 0xffffff);
                                }
                                currentHeight += 15;
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeMasterStars) {
                            NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");
                            final int masterStarCount = extraAttributes.getInteger("dungeon_item_level") - 5;
                            int masterStarBonus = 0;
                            if (masterStarCount > 0) {
                                ArrayList<String> noPriceFound = new ArrayList<>();
                                int count = masterStarCount;
                                while (count > 0) {
                                    if (count == 1) {
                                        if (DataManager.lowestBins.has("FIRST_MASTER_STAR")) {
                                            masterStarBonus += DataManager.lowestBins.get("FIRST_MASTER_STAR").getAsInt();
                                        } else {
                                            noPriceFound.add("FIRST_MASTER_STAR");
                                        }
                                    } else if (count == 2) {
                                        if (DataManager.lowestBins.has("SECOND_MASTER_STAR")) {
                                            masterStarBonus += DataManager.lowestBins.get("SECOND_MASTER_STAR").getAsInt();
                                        } else {
                                            noPriceFound.add("SECOND_MASTER_STAR");
                                        }
                                    } else if (count == 3) {
                                        if (DataManager.lowestBins.has("THIRD_MASTER_STAR")) {
                                            masterStarBonus += DataManager.lowestBins.get("THIRD_MASTER_STAR").getAsInt();
                                        } else {
                                            noPriceFound.add("THIRD_MASTER_STAR");
                                        }
                                    } else if (count == 4) {
                                        if (DataManager.lowestBins.has("FOURTH_MASTER_STAR")) {
                                            masterStarBonus += DataManager.lowestBins.get("FOURTH_MASTER_STAR").getAsInt();
                                        } else {
                                            noPriceFound.add("FOURTH_MASTER_STAR");
                                        }
                                    }
                                    count--;
                                }
                                value += masterStarBonus;
                                Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Master Stars: " + masterStarCount, 14, currentHeight, 0xffffff);
                                drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(masterStarBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(masterStarBonus)), currentHeight, 0xffffff);
                                currentHeight += 15;
                                if (!noPriceFound.isEmpty()) {
                                    for (String item : noPriceFound) {
                                        Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "- No Price Data For " + item, 14, currentHeight, 0xffffff);
                                        currentHeight += 15;
                                    }
                                }
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeRecombs) {
                            NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");
                            final int amount = extraAttributes.getInteger("rarity_upgrades");
                            if (amount > 0) {
                                int recombPrice = DataManager.bazaarData.get("products").getAsJsonObject().get("RECOMBOBULATOR_3000").getAsJsonObject().get("quick_status").getAsJsonObject().get("sellPrice").getAsInt();
                                value += recombPrice * amount;
                                Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Recombobulated", 14, currentHeight, 0xffffff);
                                drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(recombPrice), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(recombPrice)), currentHeight, 0xffffff);
                                currentHeight += 15;
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeItemSkins) {
                            NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");
                            if (extraAttributes.hasKey("skin")) {
                                String skin = extraAttributes.getString("skin");
                                String prettySkinName = skin.replaceAll("_", " ").toLowerCase();
                                StringBuilder capitalizedPrettySkinName = new StringBuilder();
                                for (String word : prettySkinName.split("\\s")) {
                                    if (word.length() >= 2) capitalizedPrettySkinName.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
                                    else capitalizedPrettySkinName.append(word.toUpperCase());
                                }
                                if (DataManager.lowestBins.has(skin)) {
                                    final int skinBonus = DataManager.lowestBins.get(skin).getAsInt();
                                    value += skinBonus;
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Skin: " + capitalizedPrettySkinName, 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(skinBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(skinBonus)), currentHeight, 0xffffff);
                                } else {
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "No Price Data for " + capitalizedPrettySkinName, 14, currentHeight, 0xffffff);
                                }
                                currentHeight += 15;
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeReforge) {
                            NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");
                            String reforge = extraAttributes.getString("modifier");
                            String reforgeInternalName = null;
                            int reforgeCost = -1;
                            String rarity = null;
                            for (Entry entry : DataManager.reforgeData.entrySet()) {
                                JsonObject reforgeJson = SkyhouseMod.gson.toJsonTree(entry.getValue()).getAsJsonObject();
                                if (reforgeJson.get("reforgeName").getAsString().toLowerCase().equals(reforge)) {
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
                                    if (rarity != null && reforgeJson.get("reforgeCosts").getAsJsonObject().has(rarity)) reforgeCost = reforgeJson.get("reforgeCosts").getAsJsonObject().get(rarity).getAsInt();
                                    break;
                                }
                            }

                            if (reforgeInternalName != null) {
                                StringBuilder capitalisedReforge = new StringBuilder();
                                for (String word : reforge.split("\\s")) {
                                    capitalisedReforge.append(word.substring(0, 1).toUpperCase() + word.substring(1)).append(" ");
                                }
                                capitalisedReforge = new StringBuilder(capitalisedReforge.toString().trim());
                                if (DataManager.lowestBins.has(reforgeInternalName)) {
                                    int reforgeBonus = DataManager.lowestBins.get(reforgeInternalName).getAsInt();
                                    value += reforgeBonus;
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Reforged: " + capitalisedReforge, 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(reforgeBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(reforgeBonus)), currentHeight, 0xffffff);
                                    currentHeight += 15;
                                    if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeReforgeCost && rarity != null) {
                                        value += reforgeCost;
                                        Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "- Reforge Cost: " + Character.toUpperCase(rarity.charAt(0)) + rarity.toLowerCase().substring(1), 14, currentHeight, 0xffffff);
                                        drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(reforgeCost), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(reforgeCost)), currentHeight, 0xffffff);
                                        currentHeight += 15;
                                    }
                                } else {
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "No Price Data for " + capitalisedReforge, 14, currentHeight, 0xffffff);
                                    currentHeight += 15;
                                }
                            }
                        }

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includeAmount) {
                            final int stackSize = stack.stackSize;
                            if (stackSize > 1) {
                                Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Amount", 14, 256 - 20 - 15, 0xffffff);
                                value *= stackSize;
                                drawString(fontRendererObj, EnumChatFormatting.GREEN + "x" + stackSize, 256 - 14 - fontRendererObj.getStringWidth("x" + stackSize), 256 - 20 - 15, 0xffffff);
                            }
                        }

                    } else {

                        //TODO: add everything that could modify the value of a pet here

                        JsonObject petInfo = Utils.getPetInfoFromNbt(nbt);

                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includePetItems) {
                            if (petInfo.has("heldItem")) {
                                String petItem = petInfo.get("heldItem").getAsString();
                                String prettyPetItemName = petItem.replaceAll("_", " ").toLowerCase().replace("pet item", "");
                                StringBuilder capitalizedPrettyPetItemName = new StringBuilder();
                                for (String word : prettyPetItemName.split("\\s")) {
                                    if (word.length() >= 2)
                                        capitalizedPrettyPetItemName.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
                                    else capitalizedPrettyPetItemName.append(word.toUpperCase());
                                }
                                capitalizedPrettyPetItemName = new StringBuilder(capitalizedPrettyPetItemName.toString().trim());
                                if (DataManager.lowestBins.has(petItem)) {
                                    final int petItemBonus = DataManager.lowestBins.get(petItem).getAsInt();
                                    value += petItemBonus;
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Pet Item: " + capitalizedPrettyPetItemName, 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(petItemBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(petItemBonus)), currentHeight, 0xffffff);
                                } else {
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "No Price Data for " + capitalizedPrettyPetItemName, 14, currentHeight, 0xffffff);
                                }
                                currentHeight += 15;
                            }
                        }



                        if (SkyhouseMod.INSTANCE.getConfig().creationOptions.includePetSkins) {
                            if (petInfo.has("skin")) {
                                String skin = "PET_SKIN_" + petInfo.get("skin").getAsString();
                                String prettyPetSkinName = skin.replaceAll("_", " ").toLowerCase().replace("pet skin", "");
                                StringBuilder capitalizedPrettyPetSkinName = new StringBuilder();
                                for (String word : prettyPetSkinName.split("\\s")) {
                                    if (word.length() >= 2) capitalizedPrettyPetSkinName.append(word.substring(0, 1).toUpperCase()).append(word.substring(1)).append(" ");
                                    else capitalizedPrettyPetSkinName.append(word.toUpperCase());
                                }
                                if (DataManager.lowestBins.has(skin)) {
                                    final int petSkinBonus = DataManager.lowestBins.get(skin).getAsInt();
                                    value += petSkinBonus;
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "Pet Skin: " + capitalizedPrettyPetSkinName, 14, currentHeight, 0xffffff);
                                    drawString(fontRendererObj, EnumChatFormatting.GREEN + "+" + formatNumber(petSkinBonus), 256 - 14 - fontRendererObj.getStringWidth("+" + formatNumber(petSkinBonus)), currentHeight, 0xffffff);
                                } else {
                                    Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "No Price Data for " + capitalizedPrettyPetSkinName, 14, currentHeight, 0xffffff);
                                }
                                currentHeight += 15;
                            }
                        }


                    }


                    if (currentHeight == 81) {
                        Utils.drawString(this, fontRendererObj, EnumChatFormatting.GRAY + "None", 14, currentHeight, 0xffffff);
                        drawString(fontRendererObj, EnumChatFormatting.GRAY + "0", 256-14-fontRendererObj.getStringWidth("0"), currentHeight, 0xffffff);
                    }

                    Utils.drawString(this, fontRendererObj, "Estimated Total Value: ", 14, 256-20, 0xffffff);
                    drawString(fontRendererObj, EnumChatFormatting.GREEN + formatNumber(value), 256-14-fontRendererObj.getStringWidth(formatNumber(value)), 256-20, 0xffffff);

                }
            }

        } else {
            Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "No Item Found", 128, 120, 0xffffff);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();

        GlStateManager.enableLighting();

        if (hover(mouseX-guiLeft, mouseY-guiTop, 8, -32+8, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + "Skyhouse"), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY - guiTop, 230, 8 - 32, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Settings" + EnumChatFormatting.RESET), mouseX, mouseY);
        } else if (hover(mouseX-guiLeft, mouseY-guiTop, 8+22, -32+8+1, 16, 16, guiScale) && SkyhouseMod.INSTANCE.getOverlayManager().hasFlips()) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GREEN + "Flip List"), mouseX, mouseY);
        } else if (isPet && hover(mouseX - guiLeft, mouseY - guiTop, 256 - 16 - 12, 35 - 24, 16, 16, guiScale)) {
            drawHoveringText(Arrays.asList(EnumChatFormatting.GRAY + "Pet Values are based off level 1 pets of that" + EnumChatFormatting.RESET,
                    EnumChatFormatting.GRAY + "rarity and do not take level into account." + EnumChatFormatting.RESET), mouseX, mouseY);
        }


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
