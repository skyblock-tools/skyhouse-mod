package tools.skyblock.skyhouse.mcmod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.io.IOUtils;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Utils {

    public static final ExecutorService es = Executors.newFixedThreadPool(3);


    public static Integer parseInt(String string, int def) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
            return def;
        }
    }

    public static boolean isAhGui() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
            String title = ((ContainerChest) chest.inventorySlots).getLowerChestInventory().getDisplayName().getUnformattedText();
            return title.toLowerCase().contains("auction") || title.toLowerCase().contains("bid");
        }
        return false;
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
        try {
            return new URL(bobTheBuilder.toString());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static JsonObject getJsonApi(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(3_000);
        conn.setReadTimeout(15_000);
        conn.setRequestProperty("accept", "application/json");
        conn.setRequestProperty("user-agent", "forge/skyhouse");
        String res = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
        return SkyhouseMod.gson.fromJson(res, JsonObject.class);
    }

    public static void getJsonApiAsync(URL url, Consumer<JsonObject> cb) {
        getJsonApiAsync(url, cb, Throwable::printStackTrace);
    }
    public static void getJsonApiAsync(URL url, Consumer<JsonObject> cb, Consumer<IOException> errorHandler) {
        es.submit(() -> {
            try {
                cb.accept(getJsonApi(url));
            } catch (IOException e) {
                errorHandler.accept(e);
            }
        });
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
        return createConvertingCallback(x -> parseInt(x.replaceAll("\\s+", ""), def), target);
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

    public static boolean renderOverlay() {
        return isAhGui() && SkyhouseMod.INSTANCE.configManager.showOverlay;
    }

    public static int getGuiLeft() {
        int savedGuiLeft = SkyhouseMod.INSTANCE.configManager.guiLeft;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return SkyhouseMod.INSTANCE.configManager.relativeGui ? Math.round(sr.getScaledWidth() * (((float) savedGuiLeft) / 1000)) : savedGuiLeft;
    }
    public static int getGuiTop() {
        int savedGuiTop = SkyhouseMod.INSTANCE.configManager.guiTop;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return SkyhouseMod.INSTANCE.configManager.relativeGui ? Math.round(sr.getScaledHeight() * (((float) savedGuiTop) / 1000)) : savedGuiTop;
    }

    public static float getScaleFactor() {
        float savedSf = SkyhouseMod.INSTANCE.configManager.guiScale;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        return SkyhouseMod.INSTANCE.configManager.relativeGui ? (savedSf * sr.getScaledWidth()) / 255f : savedSf;

    }

}
