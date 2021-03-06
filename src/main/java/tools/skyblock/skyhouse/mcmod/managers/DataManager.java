package tools.skyblock.skyhouse.mcmod.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.config.SkyhouseConfig;
import tools.skyblock.skyhouse.mcmod.util.Constants;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataManager {

    public static JsonArray contributors = new JsonArray();
    public static JsonObject lowestBins = new JsonObject();
    public static JsonObject bazaarData = new JsonObject();
    public static JsonObject reforgeData = new JsonObject();
    public static JsonObject themes = new JsonObject();
    private static JsonObject storedThemes = new JsonObject();

    public static String[] themeNames() {
        String[] names = new String[themes.entrySet().size()];
        int i = 0;
        for (Map.Entry<String, JsonElement> entry : themes.entrySet()) {
            names[i++] = entry.getKey();
        }
        return names;
    }

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void scheduleFetch() {
        executor.scheduleAtFixedRate(DataManager::getLowestBinsFromMoulberryApi, 5, 120, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(DataManager::getBazaarDataFromApi, 5, 120, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(DataManager::getReforgeDataFromMoulberryGithub, 5, 300, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(DataManager::loadStaticSkyhouseData, 5, 300, TimeUnit.SECONDS);
    }


    @SuppressWarnings("SpellCheckingInspection")
    public static void getLowestBinsFromMoulberryApi() {
        getLowestBinsFromMoulberryApi(false);
    }

    // Moulberry's api here is only used for the CreationGui and BitsOverlay,
    // the actual flipper itself uses our api, which uses the hypixel api.
    @SuppressWarnings("SpellCheckingInspection")
    public static void getLowestBinsFromMoulberryApi(boolean sendMessage) {
        SkyhouseMod.LOGGER.debug("fetching lowest bin data");
        Utils.getJsonApiAsync(Utils.parseUrl("https://moulberry.codes/lowestbin.json"),
                data -> {
                    if (data != null) lowestBins = data;
                },
                e -> {
                    SkyhouseMod.LOGGER.debug("Error connecting to Moulberry's lowest bins api");
                    if (sendMessage)  {
                        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Error connecting to Moulberry's lowest bins api"));
                    }
                });
    }

    public static void getBazaarDataFromApi() {
        getBazaarDataFromApi(false);
    }

    public static void getBazaarDataFromApi(boolean sendMessage) {
        SkyhouseMod.LOGGER.debug("fetching bazaar data");
        Utils.getJsonApiAsync(Utils.parseUrl("https://api.hypixel.net/skyblock/bazaar"),
                data -> {
                    if (data != null) bazaarData = data;
                },
                e -> {
                    SkyhouseMod.LOGGER.debug("Error connecting to Hypixel api");
                    if (sendMessage)  {
                        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Error connecting to Hypixel api"));
                    }
                });
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static void getReforgeDataFromMoulberryGithub() {
        getReforgeDataFromMoulberryGithub(false);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static void getReforgeDataFromMoulberryGithub(boolean sendMessage) {
        SkyhouseMod.LOGGER.debug("fetching reforge data");
        Utils.getJsonApiAsync(Utils.parseUrl("https://raw.githubusercontent.com/Moulberry/NotEnoughUpdates-REPO/master/constants/reforgestones.json"),
                data -> {
                    if (data != null) reforgeData = data;
                },
                e -> {
                    SkyhouseMod.LOGGER.debug("Error connecting to Moulberry's Github");
                    if (sendMessage)  {
                        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Error connecting to Moulberry's Github"));
                    }
                });
    }

    public static void loadLocalData() {
        File themeFile = new File(SkyhouseMod.INSTANCE.getConfigDir(), "themes.json");
        if (themeFile.exists()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(themeFile), StandardCharsets.UTF_8)) {
                storedThemes = SkyhouseMod.gson.fromJson(reader, JsonObject.class);
            } catch (IOException ignored) {}
        }
    }

    public static void loadStaticSkyhouseData() {
        SkyhouseMod.LOGGER.debug("fetching skyhouse data");
        Utils.getJsonApiAsync(Utils.parseUrl(Constants.STATIC_BASE_URL + "/mod/contributors.json"), (object) -> {
            contributors = object.get("contributors").getAsJsonArray();
        });
        Utils.getJsonApiAsync(Utils.parseUrl(Constants.STATIC_BASE_URL + "/mod/themes.json"), (object) -> {
            themes = object;
            for (Map.Entry<String, JsonElement> entry : storedThemes.entrySet()) {
                themes.add(entry.getKey(), entry.getValue());
            }
        });
    }

}
