package tools.skyblock.skyhouse.mcmod.gui.components;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomTextbox extends GuiTextField {

    public static final int DIGITS_ONLY = 0b1;

    private List<Consumer<String>> stateUpdaters = new ArrayList<>();

    private int opt;


    public CustomTextbox(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, int options) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        opt = options;
    }
    public CustomTextbox(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        this(componentId, fontrendererObj, x, y, par5Width, par6Height, 0);
    }

    public CustomTextbox withDefaultText(String text) {
        setText(text);
        return this;
    }

    public CustomTextbox withStateUpdater(Consumer<String> cb) {
        stateUpdaters.add(cb);
        return this;
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        if ((opt & DIGITS_ONLY) != 0 && (Character.isLetter(typedChar))) return false;
        boolean success = super.textboxKeyTyped(typedChar, keyCode);
        if (success && stateUpdaters != null)
            for (Consumer<String> updater : stateUpdaters)
                updater.accept(getText());
        return success;
    }
}
