package tools.skyblock.skyhouse.mcmod.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class EventListener {

    private List<String> tooltipToRender = null;

    private GuiScreen toOpen;

    public void openGui(GuiScreen guiScreen) {
        toOpen = guiScreen;
    }
    public void closeGui() {
        toOpen = null;
    }


    @SubscribeEvent
    public void onGuiScreenMouse(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Utils.isAhGui())
            SkyhouseMod.INSTANCE.getOverlayManager().mouseAction();
    }

    @SubscribeEvent
    public void onGuiScreenKeyboard(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (Utils.isAhGui())
            SkyhouseMod.INSTANCE.getOverlayManager().keyTyped();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (toOpen != null) Minecraft.getMinecraft().displayGuiScreen(toOpen);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGuiBackgroundDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (Utils.renderOverlay()) {
            SkyhouseMod.INSTANCE.getOverlayManager().drawScreen(event.mouseX, event.mouseY);
            if (tooltipToRender != null) {
                SkyhouseMod.INSTANCE.getOverlayManager().drawHoveringText(tooltipToRender, event.mouseX, event.mouseY);
                tooltipToRender = null;
            }
        }
    }

    @SubscribeEvent
    public void onGuiResize(GuiScreenEvent.InitGuiEvent event) {
        if (Utils.renderOverlay())
            SkyhouseMod.INSTANCE.getOverlayManager().initGui();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (Utils.renderOverlay()) {
            tooltipToRender = new ArrayList<>(event.toolTip);
            event.toolTip.clear();
        }
    }

}
