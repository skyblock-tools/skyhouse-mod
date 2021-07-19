package tools.skyblock.skyhouse.mcmod.gui.helpers;

public class SimpleAnimationStateManager {

    public int target = 0;
    public int current = 0;
    public int step = 0;

    private int start;

    private boolean increase = true;

    private long lastUpdate = -1;
    private int delta;

    public SimpleAnimationStateManager start() {
        lastUpdate = System.currentTimeMillis();
        start = current;
        return this;
    }

    public void tick() {
        delta = (int) (System.currentTimeMillis() - lastUpdate);
        if (delta / 10 < 1) return;
        lastUpdate = System.currentTimeMillis();
        current = max(current + step * delta / 10, target);
    }

    public boolean started() {
        return lastUpdate != -1;
    }

    public boolean ended() {
        return current == target;
    }

    public int max(int a, int b) {
       return increase ? Math.min(a, b) : Math.max(a, b);
    }

    public void reset() {
        lastUpdate = -1;
        current = start;
    }

    public static SimpleAnimationStateManagerBuilder builder() {
        return new SimpleAnimationStateManagerBuilder();
    }


    public static class SimpleAnimationStateManagerBuilder {

        private SimpleAnimationStateManagerBuilder() {

        }

        private SimpleAnimationStateManager parent = new SimpleAnimationStateManager();

        public SimpleAnimationStateManagerBuilder withTarget(int target) {
            parent.target = target;
            return this;
        }

        public SimpleAnimationStateManagerBuilder withCurrent(int current) {
            parent.current = current;
            return this;
        }

        public SimpleAnimationStateManagerBuilder withStep(int step) {
            parent.step = step;
            return this;
        }

        public SimpleAnimationStateManagerBuilder withIncrease(boolean increase) {
            parent.increase = increase;
            return this;
        }

        public SimpleAnimationStateManager build() {
            return parent;
        }

    }

}
