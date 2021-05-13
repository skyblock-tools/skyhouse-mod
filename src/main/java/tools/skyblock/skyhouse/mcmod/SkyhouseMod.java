package tools.skyblock.skyhouse.mcmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tools.skyblock.skyhouse.mcmod.commands.ConfigCommand;
import tools.skyblock.skyhouse.mcmod.commands.ReloadConfigCommand;
import tools.skyblock.skyhouse.mcmod.listeners.EventListener;
import tools.skyblock.skyhouse.mcmod.managers.OverlayManager;
import tools.skyblock.skyhouse.mcmod.managers.ConfigManager;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Mod(modid = SkyhouseMod.MODID, version = SkyhouseMod.VERSION, clientSideOnly = true)
public class SkyhouseMod {

    public static final String MODID = "skyhouse";
    public static final String VERSION = "1.0";
    public static final Gson serializeGson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();
    public static final Gson gson = new Gson();
    public static SkyhouseMod INSTANCE;
    private EventListener listener;
    private OverlayManager overlayManager;
    private ConfigManager configManager = null;
    private File configDir;
    private File configFile;

    public SkyhouseMod() {
        INSTANCE = this;
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configDir = new File(event.getModConfigurationDirectory(), "skyhouse");
        getConfigDir().mkdirs();
        configFile = new File(getConfigDir(), "config.json");
        loadConfig();
        listener = new EventListener();
        overlayManager = new OverlayManager();
        MinecraftForge.EVENT_BUS.register(listener);
        ClientCommandHandler.instance.registerCommand(new ConfigCommand());
        ClientCommandHandler.instance.registerCommand(new ReloadConfigCommand());
    }

    public void loadConfig() {
        if (configFile.exists())
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                configManager = gson.fromJson(reader, ConfigManager.class);
            } catch (IOException ignored) {}
        if (configManager == null) {
            configManager = new ConfigManager();
            saveConfig();
        }
    }

    public void saveConfig() {
        try {
            configFile.createNewFile();
            FileWriter writer = new FileWriter(configFile);
            writer.write(serializeGson.toJson(configManager));
            writer.close();
        } catch (IOException ignored) {}
    }

    public EventListener getListener() {
        return listener;
    }

    public OverlayManager getOverlayManager() {
        return overlayManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public File getConfigDir() {
        return configDir;
    }
}
