package tools.skyblock.skyhouse.mcmod.managers;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import tools.skyblock.skyhouse.mcmod.gui.CustomGui;
import tools.skyblock.skyhouse.mcmod.models.SearchFilter;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.FlipListGui;
import tools.skyblock.skyhouse.mcmod.gui.SelectionGui;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.net.SocketTimeoutException;
import java.util.List;

public class OverlayManager {

    private CustomGui gui;
    private JsonArray flips = null;


    private CustomGui ensureInstance() {
        if (gui == null) gui = new SelectionGui();
        if (flips != null && gui instanceof SelectionGui) gui = new FlipListGui(flips);
        return gui;
    }

    public void close() {
        gui = null;
        flips = null;
    }

    public void keyTyped() {
        ensureInstance().keyEvent();
    }

    public void mouseAction() {
        ensureInstance();
        if (!Mouse.getEventButtonState()) return;
        int mouseX = Mouse.getX() * gui.width / Minecraft.getMinecraft().displayWidth;
        int mouseY = gui.height - Mouse.getY() * gui.height / Minecraft.getMinecraft().displayHeight - 1;
        ensureInstance().click(mouseX, mouseY);
    }

    public void drawScreen(int mouseX, int mouseY) {
        ensureInstance().tick();
        gui.drawScreen(mouseX, mouseY);
    }
    public void initGui() {
        ensureInstance().tick();
        gui.initGui();
    }


    public void search(SearchFilter filter) {
        Utils.getJsonApiAsync(Utils.getUrl("https://api-jiri-v1.rose.sh/api/flip/auctions?showRawData=yesPlease",//"https://api.skyblock.tools/skyhouse/api/flip/auctions",
                SkyhouseMod.gson.fromJson(SkyhouseMod.serializeGson.toJson(filter), JsonObject.class)),
                data -> flips = data.get("flips").getAsJsonArray(), e -> {
            if (e instanceof SocketTimeoutException) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not connect to API!"));
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
            else if (e.getMessage().contains("403") || e.getMessage().contains("401")) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Forbidden to access the API!"));
                Minecraft.getMinecraft().displayGuiScreen(null);
            }
            else e.printStackTrace();
        });
    }


    public void drawHoveringText(List<String> text, int x, int y) {
        ensureInstance().drawHoveringText(text, x, y);
    }

}
