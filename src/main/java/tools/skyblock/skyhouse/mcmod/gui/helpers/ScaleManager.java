package tools.skyblock.skyhouse.mcmod.gui.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ScaleManager {

    private int refWidth;
    private int refHeight;
    private ScaledResolution sr;

    public void setSr() {
        sr = new ScaledResolution(Minecraft.getMinecraft());
    }

    public ScaleManager(int refWidth, int refHeight) {
        this.refWidth = refWidth;
        this.refHeight = refHeight;
        setSr();
    }

    public int scaleX(int x) {
        return Math.round(((float) x / (float) refWidth) * sr.getScaledWidth());
    }
    public int scaleY(int y) {
        return Math.round(((float) y / (float) refHeight) * sr.getScaledHeight());
    }


}
