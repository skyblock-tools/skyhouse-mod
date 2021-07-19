package tools.skyblock.skyhouse.mcmod.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.overlays.ah.CreationGui;
import tools.skyblock.skyhouse.mcmod.overlays.ah.FlipListGui;
import tools.skyblock.skyhouse.mcmod.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static tools.skyblock.skyhouse.mcmod.util.Utils.*;

public class EventListener {

    private List<String> tooltipToRender = null;

    private int lastAuctionIndex = -1;

    private GuiScreen toOpen;

    public void openGui(GuiScreen guiScreen) {
        toOpen = guiScreen;
    }


    public void setLastAuction(int lastAuctionIndex) {
        this.lastAuctionIndex = lastAuctionIndex;
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
        toOpen = null;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGuiBackgroundDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        SkyhouseMod.INSTANCE.getOverlayManager().drawOverlays(event.mouseX, event.mouseY);
        if ((Utils.renderCreationOverlay() && isAhCreationGui()) || (Utils.renderFlippingOverlay() && Utils.isAhGui())) {
            SkyhouseMod.INSTANCE.getOverlayManager().drawScreen(event.mouseX, event.mouseY);
        }
        if (tooltipToRender != null) {
            if ((Utils.isAhGui() && SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.showFlippingOverlay) || (Utils.isAhCreationGui() &&
                    SkyhouseMod.INSTANCE.getConfig().ahOverlayConfig.showCreationOverlay))
                SkyhouseMod.INSTANCE.getOverlayManager().drawHoveringText(tooltipToRender, event.mouseX, event.mouseY);
            tooltipToRender = null;
        }
    }

    @SubscribeEvent
    public void onChatMessageRecieved(ClientChatReceivedEvent event) {
        if (isAhGui() && SkyhouseMod.INSTANCE.getOverlayManager().isFlipList() && event.type == 0) {
            ChatComponentText text = (ChatComponentText) event.message;
            if (text.getUnformattedText().equals("This auction wasn't found!")) {
                ((FlipListGui) SkyhouseMod.INSTANCE.getOverlayManager().getGui()).removeNotFoundAuction(lastAuctionIndex);
            }
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
        } else if (Utils.isAhGui() && Utils.renderFlippingOverlay()) tooltipToRender = new ArrayList<>(event.toolTip);
        else return;
        event.toolTip.clear();
    }

}
