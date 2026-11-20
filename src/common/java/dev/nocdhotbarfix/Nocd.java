package dev.nocdhotbarfix;

/** NOCD cancellation, without restoring a cancelled key when the new key is released. */
public final class Nocd {
    public static final int NONE = 0, FWD = 1, BACK = 2, LEFT = 3, RIGHT = 4, HOTBAR = 5, ACTION = 6;

    public static final Object[] token = new Object[512];
    private static final Object HELD = new Object();
    private static final int[] scan = new int[512];
    private static final int[] kind = new int[512];

    private Nocd() {}

    public interface Adapter {
        int classify(int key, int scancode);
        void release(int key, int scancode, Object token);
    }

    public static void press(int key, int scancode, Object t, Adapter adapter) {
        if (key < 0 || key >= token.length) return;
        int c = adapter.classify(key, scancode);
        for (int i = 0; i < token.length; i++) {
            if (token[i] == null || i == key) continue;
            if (cancels(c, kind[i])) {
                Object old = token[i];
                int s = scan[i];
                token[i] = null;
                adapter.release(i, s, old);
            }
        }
        token[key] = t == null ? HELD : t;
        scan[key] = scancode;
        kind[key] = c;
    }

    public static void unpress(int key) {
        if (key >= 0 && key < token.length) token[key] = null;
    }

    private static boolean cancels(int p, int h) {
        if (p == ACTION || h == ACTION) return false;
        if (p == HOTBAR || h == HOTBAR) return p == HOTBAR && h == HOTBAR;
        if (p >= FWD && p <= RIGHT) {
            return h >= FWD && h <= RIGHT
                && ((p == FWD && h == BACK) || (p == BACK && h == FWD)
                 || (p == LEFT && h == RIGHT) || (p == RIGHT && h == LEFT));
        }
        return p == NONE && h == NONE;
    }
}
