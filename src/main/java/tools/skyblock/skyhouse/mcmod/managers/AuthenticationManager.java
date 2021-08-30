package tools.skyblock.skyhouse.mcmod.managers;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.util.Constants;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;

public class AuthenticationManager {

    private String accessToken = "";
    private String refreshToken = "";

    /*
    -1 = placeholder to show no successful api request has been made
    0 = no perms to flipper
    1 = no sh+
    2+ = sh+
    */
    public int privLevel = -1;

    private long lastCredUpdate = 0;


    public void authenticateJsonApiAsync(URL url, Consumer<JsonObject> cb, Consumer<IOException> errorHandler) {
        Utils.getJsonApiAsync(url, (json) -> {
            if (json.get("refresh_session").getAsBoolean())
                refreshCredentials();
            cb.accept(json);
        }, errorHandler, new String[]{"Authorization", "Bearer " + accessToken});
    }

    public void tryRefreshCredentials() {
        if (lastCredUpdate + Constants.TRY_UPDATE_CRED_DELAY < System.currentTimeMillis())
            refreshCredentials();
    }

    public void refreshCredentials() {
        Utils.getJsonApiAsync(Utils.parseUrl(Constants.API_BASE_URL + "/auth/token/create?modtoken=" + refreshToken), (json) -> {
            accessToken = json.get("access_token").getAsString();
            privLevel = json.get("privilege_level").getAsInt();
            lastCredUpdate = System.currentTimeMillis();
            if (privLevel < 2) {
                SkyhouseMod.INSTANCE.getConfig().resetPremiumFeatures();
            }
        }, (err) -> {
            if (err.getMessage().contains("401") || err.getMessage().contains("403")) {
                SkyhouseMod.LOGGER.debug("Could not authenticate with the API, please refresh your access token and try again");
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not authenticate with the API, please refresh your access token and try again" + EnumChatFormatting.RESET));
            }
        });
    }

    public void loadCredentials() {
        try {
            File file = new File(SkyhouseMod.INSTANCE.getConfigDir(), "very-secret-do-not-share.txt");
            boolean loadFromJar = true;
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                refreshToken = reader.readLine();
                loadFromJar = refreshToken == null;
                reader.close();
            } if (loadFromJar) {
                Properties props = new Properties();
                InputStream stream = getClass().getClassLoader().getResourceAsStream("credentials.properties");
                if (stream == null) {
                    refreshToken = System.getenv("refresh_token");
                } else {
                    props.load(stream);
                    refreshToken = props.getProperty("refresh_token");
                }
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(refreshToken);
                    writer.close();
                }
            }
            refreshCredentials();
        } catch (IOException ignored) {

        }
    }

}
