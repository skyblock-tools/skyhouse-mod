package tools.skyblock.skyhouse.mcmod.overlays.crafting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tools.skyblock.skyhouse.mcmod.SkyhouseMod;
import tools.skyblock.skyhouse.mcmod.gui.CustomGui;
import tools.skyblock.skyhouse.mcmod.managers.ThemeManager;
import tools.skyblock.skyhouse.mcmod.util.Utils;

public class CraftFlipOverlay extends CustomGui {

    private int guiLeft, guiTop;
    private float guiScale;

    public CraftFlipOverlay() {
        initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.translate(guiLeft, guiTop, 0);
        GlStateManager.scale(guiScale, guiScale, guiScale);
        ThemeManager.drawOverlayThemeFor("craftFlipOverlay");

        Utils.drawCenteredString(this, Minecraft.getMinecraft().fontRendererObj, "Craft Flips", 128, 12 - 32, 0xffffff);
//        Utils.drawString(this, fontRendererObj, "hello there", 16, 16, 0xffffff);

        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();

        GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
        Container container = chest.inventorySlots;
//        for (Slot slot : container.inventorySlots) {
//            System.out.println(slot.slotNumber);
//            if (slot.getStack() != null) {
//                System.out.println(slot.getStack().getDisplayName());
//            } else System.out.println("empty");
//        }
        if (container.getSlot(49).getHasStack()) {
            ItemStack stack = container.getSlot(49).getStack();

            //TODO: figure out how to get the itemstack to render with transparency
            //TODO: go find a corner, curl up into a ball and cry if i fail to do that

//            GlStateManager.enableAlpha();
//            GlStateManager.enableBlend();
//            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//            GlStateManager.color(0.3f, 0.3f, 0.3f, 0.5f);

//            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
//            GL11.glEnable(GL11.GL_BLEND);
//            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

//            Utils.renderItemWithTransparencyEnabled(fontRendererObj, stack, container.getSlot(20).xDisplayPosition + chest.guiLeft, container.getSlot(20).yDisplayPosition + chest.guiTop, null);

//            GL11.glPopAttrib();

//            GlStateManager.disableAlpha();


//            ResourceLocation resource = item.delegate.getResourceName();
//            String reg = resource.getResourceDomain() + ":items/" + resource.getResourcePath();
//
//            System.out.println(reg);
//            container.getSlot(20).setBackgroundName(reg);
        }

        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        tick();
        guiScale = Utils.getScaleFactor();
    }

    @Override
    public void tick() {
        super.tick();
        guiLeft = Utils.getGuiLeft();
        guiTop = Utils.getGuiTop();
    }

    @Override
    public void click(int mouseX, int mouseY) {

    }

    @Override
    public void keyEvent() {

    }
}
