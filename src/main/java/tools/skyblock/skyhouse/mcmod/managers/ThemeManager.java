package tools.skyblock.skyhouse.mcmod.managers;

import com.google.gson.JsonObject;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;

public class ThemeManager {

    public static String current;
    public static JsonObject themes;

    private static void tick() {
        themes = DataManager.themes;
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

        JsonObject head = themes.get(current).getAsJsonObject();

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

}
