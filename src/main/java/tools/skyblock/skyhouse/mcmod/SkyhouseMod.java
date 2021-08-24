package tools.skyblock.skyhouse.mcmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.skyblock.skyhouse.mcmod.commands.*;
import tools.skyblock.skyhouse.mcmod.commands.data.*;
import tools.skyblock.skyhouse.mcmod.config.SkyhouseConfig;
import tools.skyblock.skyhouse.mcmod.listeners.EventListener;
import tools.skyblock.skyhouse.mcmod.managers.AuthenticationManager;
import tools.skyblock.skyhouse.mcmod.managers.DataManager;
import tools.skyblock.skyhouse.mcmod.managers.OverlayManager;

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
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static SkyhouseMod INSTANCE;
    private EventListener listener;
    private OverlayManager overlayManager;
    private AuthenticationManager authenticationManager;
    private File configDir;
    private File configFile;

    private SkyhouseConfig config;

    public SkyhouseMod() {
        INSTANCE = this;
    }

    private static class WrongForgeVersion extends RuntimeException {
        public WrongForgeVersion(String message) {
            super(message);
        }
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (ForgeVersion.getBuildVersion() != 2318)
            throw new WrongForgeVersion("[SKYHOUSE] This mod is incompatible with forge version " + ForgeVersion.getVersion() + ", Please upgrade to 11.15.1.2318 or remove this mod");
        configDir = new File(event.getModConfigurationDirectory(), "skyhouse");
        getConfigDir().mkdirs();
        configFile = new File(getConfigDir(), "config.json");
        loadConfig();
        authenticationManager = new AuthenticationManager();
        authenticationManager.loadCredentials();
        listener = new EventListener();
        overlayManager = new OverlayManager();
        DataManager.scheduleFetch();
        MinecraftForge.EVENT_BUS.register(listener);
        ClientCommandHandler.instance.registerCommand(new SkyhouseLoginCommand());
        ClientCommandHandler.instance.registerCommand(new ConfigCommand());
        ClientCommandHandler.instance.registerCommand(new ReloadConfigCommand());
        ClientCommandHandler.instance.registerCommand(new RefreshLowestBins());
        ClientCommandHandler.instance.registerCommand(new RefreshBazaarData());
        ClientCommandHandler.instance.registerCommand(new RefreshReforgeData());
        ClientCommandHandler.instance.registerCommand(new SetTokenCommand());
        ClientCommandHandler.instance.registerCommand(new AhOverlayPositionEditorCommand());
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveConfig));
    }

    public void loadConfig() {
        if (configFile.exists())
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                config = gson.fromJson(reader, SkyhouseConfig.class);
                config.processConfig();
            } catch (IOException ignored) {}
        if (config == null) {
            config = new SkyhouseConfig();
            config.processConfig();
            saveConfig();
        }
    }


    public void saveConfig() {
        try {
            configFile.createNewFile();
            FileWriter writer = new FileWriter(configFile);
            writer.write(serializeGson.toJson(config));
            writer.close();
        } catch (IOException ignored) {}
    }

    public EventListener getListener() {
        return listener;
    }

    public OverlayManager getOverlayManager() {
        return overlayManager;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public File getConfigDir() {
        return configDir;
    }

    public SkyhouseConfig getConfig() {
        return config;
    }
}
