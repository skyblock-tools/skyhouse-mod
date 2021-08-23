package tools.skyblock.skyhouse.mcmod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import scala.unchecked;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Utils {

    public static final ExecutorService es = Executors.newFixedThreadPool(3);
    private static final DecimalFormat df = new DecimalFormat("###,###,###");
    private static final List<String> bonzoFraggedItems = Arrays.asList("BONZO_STAFF", "STARRED_BONZO_STAFF", "BONZO_MASK", "STARRED_BONZO_MASK");
    private static final List<String> scarfFraggedItems = Arrays.asList("STONE_BLADE", "STARRED_STONE_BLADE", "ADAPTIVE_HELMET", "STARRED_ADAPTIVE_HELMET", "ADAPTIVE_CHESTPLATE",
                                                        "STARRED_ADAPTIVE_CHESTPLATE", "ADAPTIVE_LEGGINGS", "STARRED_ADAPTIVE_LEGGINGS", "ADAPTIVE_BOOTS", "STARRED_ADAPTIVE_BOOTS");
    private static final List<String> lividFraggedItems = Arrays.asList("LAST_BREATH", "STARRED_LAST_BREATH", "SHADOW_ASSASSIN_HELMET", "STARRED_SHADOW_ASSASSIN_HELMET",
                                                        "SHADOW_ASSASSIN_CHESTPLATE", "STARRED_SHADOW_ASSASSIN_CHESTPLATE", "SHADOW_ASSASSIN_LEGGINGS", "STARRED_SHADOW_ASSASSIN_LEGGINGS",
                                                        "SHADOW_ASSASSIN_BOOTS", "STARRED_SHADOW_ASSASSIN_BOOTS", "SHADOW_FURY", "STARRED_SHADOW_FURY");


    public static Integer parseInt(String string, int def) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    public static String invGuiName() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
            return ((ContainerChest) chest.inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText();
        }
        return null;
    }

    private static String stripStartColourCodes(String text) {
        List<Character> allowedChars = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');
        boolean hasColourCode = false;
        if (text.length() >= 2) {
            hasColourCode = text.charAt(0) == '\u00a7' && allowedChars.contains(text.charAt(1));
        }
        while (hasColourCode) {
            text = text.substring(2);
            if (text.length() >= 2) {
                if (!(text.charAt(0) == '\u00a7' && allowedChars.contains(text.charAt(1)))) {
                    hasColourCode = false;
                }
            }
        }
        return text;
    }

    public static void drawString(GuiScreen gui, FontRenderer fontRendererObject, String text, int x, int y, int colour) {
        if (SkyhouseMod.INSTANCE.getConfig().generalConfig.fullChromaMode) {
            text = stripStartColourCodes(text);
            text = "\u00a7z" + text + EnumChatFormatting.RESET;
        }
        gui.drawString(fontRendererObject, text, x, y, colour);
    }

    public static void drawCenteredString(GuiScreen gui, FontRenderer fontRendererObject, String text, int x, int y, int colour) {
        if (SkyhouseMod.INSTANCE.getConfig().generalConfig.fullChromaMode) {
            text = stripStartColourCodes(text);
            text = "\u00a7z" + text + EnumChatFormatting.RESET;
        }
        gui.drawCenteredString(fontRendererObject, text, x, y, colour);
    }

    public static void drawStringWithShadow(FontRenderer fontRendererObject, String text, int x, int y, int colour) {
        if (SkyhouseMod.INSTANCE.getConfig().generalConfig.fullChromaMode) {
            text = stripStartColourCodes(text);
            text = "\u00a7z" + text + EnumChatFormatting.RESET;
        }
        fontRendererObject.drawStringWithShadow(text, x, y, colour);
    }

    public static void drawButton(GuiButton button, Minecraft mc, int mouseX, int mouseY) {
        if (SkyhouseMod.INSTANCE.getConfig().generalConfig.fullChromaMode) {
            button.displayString = "\u00a7z" + stripStartColourCodes(button.displayString) + EnumChatFormatting.RESET;
        }
        button.drawButton(mc, mouseX, mouseY);
    }

    public static boolean isAhGui() {
        String title = invGuiName();
        return title != null && (title.toLowerCase().contains("auction") || title.toLowerCase().contains("bid"));
    }

    public static boolean isAhCreationGui() {
        String title = invGuiName();
        return title != null && (title.toLowerCase().contains("create") && title.toLowerCase().contains("auction"));
    }

    public static URL parseUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ignored) {
            return null;
        }
    }


    public static URL getUrl(String url, JsonObject query) {
        StringBuilder bobTheBuilder = new StringBuilder(url);
        bobTheBuilder.append("?");
        for (Map.Entry<String, JsonElement> item : query.entrySet()) {
            bobTheBuilder.append(item.getKey())
                    .append("=")
                    .append(item.getValue())
                    .append("&");
        }
        return parseUrl(bobTheBuilder.toString());
    }

    public static JsonObject getJsonApi(URL url, String[]... headers) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(3_000);
        conn.setReadTimeout(15_000);
        conn.setRequestProperty("accept", "application/json");
        conn.setRequestProperty("user-agent", "forge/skyhouse");
        for (String[] header : headers) {
            conn.setRequestProperty(header[0], header[1]);
        }
        String res = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
        return SkyhouseMod.gson.fromJson(res, JsonObject.class);
    }

    public static void getJsonApiAsync(URL url, Consumer<JsonObject> cb) {
        getJsonApiAsync(url, cb, Throwable::printStackTrace);
    }


    public static void getJsonApiAsync(URL url, Consumer<JsonObject> cb, Consumer<IOException> errorHandler, String[]... headers) {
        es.submit(() -> {
            try {
                cb.accept(getJsonApi(url, headers));
            } catch (IOException e) {
                errorHandler.accept(e);
            }
        });
    }



    public static String formatNumber(double value) {
        return df.format(value);
    }

    public static String fragType(String itemName) {
        if (bonzoFraggedItems.contains(itemName)) {
            return "BONZO_FRAGMENT";
        }
        if (scarfFraggedItems.contains(itemName)) {
            return "SCARF_FRAGMENT";
        }
        if (lividFraggedItems.contains(itemName)) {
            return "LIVID_FRAGMENT";
        }
        return null;
    }

    public static void renderItem(ItemStack itemStack, int x, int y) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.zLevel = -145;
        renderItem.renderItemAndEffectIntoGUI(itemStack, x, y);
        renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, itemStack, x, y, null);
        renderItem.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
    }

    public static <O, T> Consumer<O> createConvertingCallback(Converter<O, T> converter, Consumer<T> target) {
        return ((O x) -> target.accept(converter.convert(x)));
    }

    public static Consumer<String> createStringToIntCallback(Consumer<Integer> target, int def) {
        Consumer<String> consumer = createConvertingCallback(x -> parseInt(x.replaceAll("\\s+", ""), def), target);
        return consumer;
    }

    public static String[] getLoreFromNBT(NBTTagCompound tag) {
        String[] lore = new String[0];
        NBTTagCompound display = tag.getCompoundTag("display");

        if (display.hasKey("Lore", 9)) {
            NBTTagList list = display.getTagList("Lore", 8);
            lore = new String[list.tagCount()];
            for (int i = 0; i < list.tagCount(); i++) {
                lore[i] = list.getStringTagAt(i);
            }
        }
        return lore;
    }

    public static JsonObject getPetInfoFromNbt(NBTTagCompound nbt) {
        String str = "";
        NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");
        if (extraAttributes.hasKey("petInfo", 8)) {
            str = extraAttributes.getString("petInfo");
        }
        JsonObject petInfo = SkyhouseMod.serializeGson.fromJson(str, JsonObject.class).getAsJsonObject();
        return petInfo;
    }

    /*
    This was copied and pasted from https://github.com/Mouberry/NotEnoughUpdates
    to work with his lowest bin api (with a few changes to work here)
     */
    public static String getInternalNameFromNBT(NBTTagCompound tag) {
        String internalName = null;
        if(tag != null && tag.hasKey("ExtraAttributes", 10)) {
            NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");

            if(ea.hasKey("id", 8)) {
                internalName = ea.getString("id").replaceAll(":", "-");
            } else {
                return null;
            }

            if("PET".equals(internalName)) {
                String petInfo = ea.getString("petInfo");
                if(petInfo.length() > 0) {
                    JsonObject petInfoObject = SkyhouseMod.gson.fromJson(petInfo, JsonObject.class);
                    internalName = petInfoObject.get("type").getAsString();
                    String tier = petInfoObject.get("tier").getAsString();
                    switch(tier) {
                        case "COMMON":
                            internalName += ";0"; break;
                        case "UNCOMMON":
                            internalName += ";1"; break;
                        case "RARE":
                            internalName += ";2"; break;
                        case "EPIC":
                            internalName += ";3"; break;
                        case "LEGENDARY":
                            internalName += ";4"; break;
                        case "MYTHIC":
                            internalName += ";5"; break;
                    }
                }
            }
            if("ENCHANTED_BOOK".equals(internalName)) {
                NBTTagCompound enchants = ea.getCompoundTag("enchantments");

                for(String enchname : enchants.getKeySet()) {
                    internalName = enchname.toUpperCase() + ";" + enchants.getInteger(enchname);
                    break;
                }
            }
        }

        return internalName;
    }

    public static JsonObject nbtToJson(NBTTagCompound tag) {
        if (tag.getKeySet().size() == 0) return null;

        int id = tag.getShort("id");
        int damage = tag.getShort("Damage");
        int count = tag.getShort("Count");
        tag = tag.getCompoundTag("tag");

        if (id == 141) id = 391;


        NBTTagCompound display = tag.getCompoundTag("display");
        String[] lore = getLoreFromNBT(tag);

        Item mcItem = Item.getItemById(id);
        String itemid = "null";
        if (mcItem != null) {
            itemid = mcItem.getRegistryName();
        }
        String name = display.getString("Name");

        JsonObject item = new JsonObject();
        item.addProperty("itemid", itemid);
        item.addProperty("displayname", name);

        if (tag.hasKey("ExtraAttributes", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");

            byte[] bytes = null;
            for (String key : ea.getKeySet()) {
                if (key.endsWith("_backpack_data") || key.equals("new_year_cake_bag_data")) {
                    bytes = ea.getByteArray(key);
                    break;
                }
            }
            if (bytes != null) {
                JsonArray bytesArr = new JsonArray();
                for (byte b : bytes) {
                    bytesArr.add(new JsonPrimitive(b));
                }
                item.add("item_contents", bytesArr);
            }
            if (ea.hasKey("dungeon_item_level")) {
                item.addProperty("dungeon_item_level", ea.getInteger("dungeon_item_level"));
            }
        }

        if (lore != null && lore.length > 0) {
            JsonArray jsonLore = new JsonArray();
            for (String line : lore) {
                jsonLore.add(new JsonPrimitive(line));
            }
            item.add("lore", jsonLore);
        }

        item.addProperty("damage", damage);
        if (count > 1) item.addProperty("count", count);
        item.addProperty("nbttag", tag.toString());

        return item;
    }

    public static JsonObject decodeItemBytes(String itemBytes) {
        try {
            NBTTagCompound tag = CompressedStreamTools.readCompressed(new ByteArrayInputStream(Base64.getDecoder().decode(itemBytes)));
            return nbtToJson(tag.getTagList("i", Constants.NBT.TAG_COMPOUND).getCompoundTagAt(0));
        } catch (IOException ignored) {
            return null;
        }
    }

    public static ItemStack jsonToItem(JsonObject json) {
        if(json == null) return new ItemStack(Items.painting, 1, 10);

        ItemStack stack = new ItemStack(Item.itemRegistry.getObject(
                new ResourceLocation(json.get("itemid").getAsString())));

        if(json.has("count")) {
            stack.stackSize = json.get("count").getAsInt();
        }

        if(stack.getItem() == null) {
            stack = new ItemStack(Item.getItemFromBlock(Blocks.stone), 0, 255);
        } else {
            if(json.has("damage")) {
                stack.setItemDamage(json.get("damage").getAsInt());
            }

            if(json.has("nbttag")) {
                try {
                    NBTTagCompound tag = JsonToNBT.getTagFromJson(json.get("nbttag").getAsString());
                    stack.setTagCompound(tag);
                } catch(NBTException ignored) {
                }
            }
            if(json.has("lore")) {
                NBTTagCompound display = new NBTTagCompound();
                if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("display")) {
                    display = stack.getTagCompound().getCompoundTag("display");
                }
                display.setTag("Lore", getLore(json.get("lore").getAsJsonArray()));
                NBTTagCompound tag = stack.getTagCompound() != null ? stack.getTagCompound() : new NBTTagCompound();
                tag.setTag("display", display);
                stack.setTagCompound(tag);
            }
        }
        return stack;
    }


    public static NBTTagList getLore(JsonArray lore) {
        NBTTagList list = new NBTTagList();
        for (JsonElement lineObj : lore) {
            String line = lineObj.getAsString();
            list.appendTag(new NBTTagString(line));
        }
        return list;
    }

    public static boolean renderFlippingOverlay() {
        return SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.showFlippingOverlay;
    }

    public static boolean renderCreationOverlay() {
        return SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.showCreationOverlay;
    }

    public static int getGuiLeft() {
        int savedGuiLeft = SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.guiLeft;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.relativeGui ? Math.round(sr.getScaledWidth() * (((float) savedGuiLeft) / 1000)) : savedGuiLeft;
    }
    public static int getGuiTop() {
        int savedGuiTop = SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.guiTop;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.relativeGui ? Math.round(sr.getScaledHeight() * (((float) savedGuiTop) / 1000)) : savedGuiTop;
    }

    public static float getScaleFactor() {
        float savedSf = SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.guiScale;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.relativeGui ? (savedSf * sr.getScaledWidth()) / 255f : savedSf;

    }

    public static int multiplyAlphaARGB(int argb, float multiplier) {
        float alpha = (float) (argb >> 24 & 255);
        int newAlpha = (int) (alpha * multiplier);
        int colour = argb & 0xFFFFFF;
        return colour | (newAlpha << 24);
    }

    public static void scissor(int x, int y, int width, int height) {
        ScaledResolution sr = ((GuiIngameForge) Minecraft.getMinecraft().ingameGUI).getResolution();
        int sf = sr.getScaleFactor();
        int translatedY = sr.getScaledHeight() - y - height;
        GL11.glScissor(x*sf, translatedY*sf, width*sf, height*sf);
    }

    public static void drawStringCentred(String text, int x, int y, int colour) {
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2, y, colour);
    }

    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> fieldGetter(Field field, Object owner) {
        return () -> {
            try {
                return (T) field.get(owner);
            } catch (IllegalAccessException e) {
                return null;
            }
        };
    }

    public static <T> Consumer<T> fieldSetter(Field field, Object owner) {
        return (T value) -> {
            try {
                field.set(owner, value);
            } catch (IllegalAccessException ignored) {
            }
        };
    }

    public static List<String> jsonArrayToStringList(JsonArray arr) {
        List<String> list = new ArrayList<>();
        for (JsonElement el : arr) {
            list.add(el.getAsString());
        }
        return list;
    }

    public static void browseTo(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ignored) {};
    }

    public static String[] wrapText(String text, int width) {
        return wrapText(text, EnumChatFormatting.WHITE, width);
    }

    public static String[] wrapText(String text, EnumChatFormatting colour, int width) {
        String[] words = text.split(" ");
        StringBuilder builder = new StringBuilder(colour + "");
        List<String> output = new ArrayList<>();
        for (String word : words) {
            if (builder.length() == 0 || Minecraft.getMinecraft().fontRendererObj.getStringWidth(builder.toString()) +
                    Minecraft.getMinecraft().fontRendererObj.getStringWidth(word) < width) builder.append(word).append(" ");
            else {
                output.add(builder.toString());
                builder = new StringBuilder(colour + "").append(word).append(" ");
            }
        }
        if (builder.length() != 0) output.add(builder.toString());
        return output.toArray(new String[0]);
    }

    public static Method methodByName(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getMethod(methodName);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object obj, Object... args) {
        try {
            return (T) method.invoke(obj, args);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

}
