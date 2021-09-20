package tools.skyblock.skyhouse.mcmod.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;

import net.minecraft.client.gui.Gui;

public class ThemeManager {

    public static String current = SkyhouseMod.INSTANCE.getConfig().generalConfig.theme;

    private static void tick() {
        current = SkyhouseMod.INSTANCE.getConfig().generalConfig.theme;
    }

    public static int getColour(String[] base, String... path) {
        String[] props = new String[base.length + path.length];
        System.arraycopy(base, 0, props, 0, base.length);
        System.arraycopy(path, 0, props, base.length, path.length);
        return getColour(props);
    }

    public static int getColour(String... props) {
        tick();

        JsonObject head = DataManager.themes.get(current).getAsJsonObject();

        for (int i = 0; i < props.length; i++) {
            String prop = props[i];
            if (head.has(prop)) {
                if (i == props.length - 1)
                    return head.get(prop).getAsInt();
                else
                    head = head.get(prop).getAsJsonObject();
            } else {
                String[] sections = prop.split(":");
                if (!head.has(sections[0])) return 0;
                if (i == props.length - 1)
                    return head.get(sections[0]).getAsInt();
                else
                    head = head.get(sections[0]).getAsJsonObject();
            }
        }
        return 0;
    }
    public static void drawAhOverlayThemeFor(String gui) {
        JsonObject currentData = DataManager.themes.get(current).getAsJsonObject().get(gui).getAsJsonObject();
        if (currentData.has("ref")) {
            gui = currentData.get("ref").getAsString();
        }
        Gui.drawRect(2, 2, 254, 254, ThemeManager.getColour(gui, "background"));
        Gui.drawRect(2, -30, 254, -2, ThemeManager.getColour(gui, "background"));

        if (DataManager.themes.get(current).getAsJsonObject().has(gui)) {
            JsonArray lines = DataManager.themes.get(current).getAsJsonObject().get(gui).getAsJsonObject().get("lines").getAsJsonArray();
            for (JsonElement el : lines) {
                JsonObject lineInfo = el.getAsJsonObject();
                Gui.drawRect(lineInfo.get("left").getAsInt(), lineInfo.get("top").getAsInt(), lineInfo.get("right").getAsInt(),
                        lineInfo.get("bottom").getAsInt(), lineInfo.get("colour").getAsInt());
            }
        }
        GlStateManager.color(1, 1, 1, 1);

    }

}
