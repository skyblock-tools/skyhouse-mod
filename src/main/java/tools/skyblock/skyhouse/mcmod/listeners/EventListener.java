package tools.skyblock.skyhouse.mcmod.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.CreationGui;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static tools.skyblock.skyhouse.mcmod.util.Utils.isAhCreationGui;
import static tools.skyblock.skyhouse.mcmod.util.Utils.isAhGui;

public class EventListener {

    private List<String> tooltipToRender = null;

    public int ticksUntilRefreshBins = 0;
    public int ticksUntilRefreshBaz = 0;
    public int ticksUntilRefreshReforges = 0;
    public boolean binsManuallyRefreshed = false;
    public boolean bazaarManuallyRefreshed = false;
    public boolean reforgesManuallyRefreshed = false;

    private GuiScreen toOpen;

    public void openGui(GuiScreen guiScreen) {
        toOpen = guiScreen;
    }
    public void closeGui() {
        toOpen = null;
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event) {
        ticksUntilRefreshBins++;
        ticksUntilRefreshBaz++;
        if (ticksUntilRefreshBins == 4800) {
            ticksUntilRefreshBins = 0;
            if (binsManuallyRefreshed) binsManuallyRefreshed = false;
            Utils.getLowestBinsFromMoulberryApi();
        }
        if (ticksUntilRefreshBaz == 4800) {
            ticksUntilRefreshBaz = 0;
            if (bazaarManuallyRefreshed) bazaarManuallyRefreshed = false;
            Utils.getBazaarDataFromApi();
        }
        if (ticksUntilRefreshBaz == 12000) {
            ticksUntilRefreshReforges = 0;
            if (reforgesManuallyRefreshed) reforgesManuallyRefreshed = false;
            Utils.getReforgeDataFromMoulberryGithub();
        }
    }

    @SubscribeEvent
    public void onGuiScreenMouse(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (isAhGui())
            SkyhouseMod.INSTANCE.getOverlayManager().mouseAction();
    }

    @SubscribeEvent
    public void onGuiScreenKeyboard(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (isAhGui())
            SkyhouseMod.INSTANCE.getOverlayManager().keyTyped();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (toOpen != null) Minecraft.getMinecraft().displayGuiScreen(toOpen);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGuiBackgroundDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (Utils.renderCreationOverlay()) {
            if (isAhCreationGui()) {
                SkyhouseMod.INSTANCE.getOverlayManager().drawScreen(event.mouseX, event.mouseY);
            }
        }
        if (Utils.renderFlippingOverlay()) {
            if (isAhGui()) {
                SkyhouseMod.INSTANCE.getOverlayManager().drawScreen(event.mouseX, event.mouseY);
            }
        }
        if (tooltipToRender != null) {
            SkyhouseMod.INSTANCE.getOverlayManager().drawHoveringText(tooltipToRender, event.mouseX, event.mouseY);
            tooltipToRender = null;
        }
    }

    @SubscribeEvent
    public void onGuiResize(GuiScreenEvent.InitGuiEvent event) {
        if (Utils.renderFlippingOverlay())
            SkyhouseMod.INSTANCE.getOverlayManager().initGui();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (Utils.renderCreationOverlay() && Utils.isAhCreationGui()) {
            tooltipToRender = CreationGui.processTooltip(event.toolTip);
        } else tooltipToRender = new ArrayList<>(event.toolTip);
        event.toolTip.clear();
    }

}
