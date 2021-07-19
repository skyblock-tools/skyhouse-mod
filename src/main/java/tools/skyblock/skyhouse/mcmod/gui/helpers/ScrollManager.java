package tools.skyblock.skyhouse.mcmod.gui.helpers;

import java.util.function.Supplier;

public class ScrollManager {

    private int boxBottom, boxTop;
    private int scrollStart;
    private int mouseOffset;
    private Supplier<Integer> height;
    private boolean scrolling;
    
    public int[] getScrollInfo() {
        float shown = Math.min((float) (boxBottom - boxTop) / (float) height.get(), 1);
        int scrollBarSize = Math.round(shown * ((boxBottom - 2) - (boxTop + 2)));
        int scrollBarStart = Math.round(((float) scrollStart / ((float) boxBottom -
                height.get() - boxTop)) * (((boxBottom - 2) - (boxTop + 2)) - scrollBarSize));
        return new int[]{scrollBarSize, scrollBarStart};
    }
    
    public void tick(int mouseX, int mouseY) {
        if (scrolling) {
            float shown = (float) (boxBottom - boxTop) / (float) height.get();
            int scrollbarMaxsize = (boxBottom - 2) - (boxTop + 2);

            int newTop = mouseY - mouseOffset;
            scrollStart = Math.round(((newTop - boxTop) / ((scrollbarMaxsize) - (shown * scrollbarMaxsize))) * ((float) boxBottom -
                    height.get() - boxTop));
            clampScroll();
        }
    }
    
    public void clampScroll() {
        if (scrollStart + boxTop + height.get() < boxBottom)
            scrollStart = boxBottom - height.get() - boxTop;
        scrollStart = Math.min(0, scrollStart);
    }

    public int getBoxBottom() {
        return boxBottom;
    }

    public ScrollManager withBoxBottom(int boxBottom) {
        this.boxBottom = boxBottom;
        return this;
    }

    public int getBoxTop() {
        return boxTop;
    }

    public ScrollManager withBoxTop(int boxTop) {
        this.boxTop = boxTop;
        return this;
    }

    public int getScrollStart() {
        return scrollStart;
    }

    public ScrollManager withScrollStart(int scrollStart) {
        this.scrollStart = scrollStart;
        return this;
    }

    public int getMouseOffset() {
        return mouseOffset;
    }

    public ScrollManager withMouseOffset(int mouseOffset) {
        this.mouseOffset = mouseOffset;
        return this;
    }

    public Supplier<Integer> getHeight() {
        return height;
    }

    public ScrollManager withHeight(Supplier<Integer> height) {
        this.height = height;
        return this;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public ScrollManager withScrolling(boolean scrolling) {
        this.scrolling = scrolling;
        return this;
    }
}
