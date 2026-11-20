import dev.nocdhotbarfix.Nocd;
import java.util.ArrayList;
import java.util.List;

public class NocdTest {
    static final int[] CLS = new int[512];
    static final List<String> released = new ArrayList<>();
    static final Nocd.Adapter A = new Nocd.Adapter() {
        public int classify(int key, int scancode) { return CLS[key]; }
        public void release(int key, int scancode, Object token) { released.add("" + key); }
    };

    static void check(boolean ok, String name) {
        if (!ok) throw new AssertionError(name);
    }

    static void reset() {
        for (int i = 0; i < CLS.length; i++) { CLS[i] = 0; Nocd.token[i] = null; }
        released.clear();
    }

    static void press(int key, int cls) { CLS[key] = cls; Nocd.press(key, 0, null, A); }

    public static void main(String[] args) {
        reset();
        press(10, Nocd.HOTBAR);
        press(11, Nocd.HOTBAR);
        check(released.equals(List.of("10")), "hotbar cancels hotbar");
        check(Nocd.token[10] == null && Nocd.token[11] != null, "no hotbar restore");

        int[] classes = {Nocd.HOTBAR, Nocd.FWD, Nocd.BACK, Nocd.LEFT, Nocd.RIGHT, Nocd.ACTION, Nocd.NONE};
        for (int p : classes) for (int h : classes) {
            if (p == h) continue;
            if ((p == Nocd.FWD && h == Nocd.BACK) || (p == Nocd.BACK && h == Nocd.FWD)
                || (p == Nocd.LEFT && h == Nocd.RIGHT) || (p == Nocd.RIGHT && h == Nocd.LEFT)
                || (p == Nocd.NONE && h == Nocd.NONE)) continue;
            reset();
            press(20, h);
            press(21, p);
            check(released.isEmpty(), "isolated " + p + "->" + h);
        }

        reset();
        press(30, Nocd.FWD);
        press(31, Nocd.RIGHT);
        check(released.isEmpty(), "adjacent movement kept");
        press(32, Nocd.BACK);
        check(released.equals(List.of("30")), "opposite movement cancels");

        reset();
        press(40, Nocd.ACTION);
        press(41, Nocd.NONE);
        check(released.isEmpty(), "action immune");
        press(42, Nocd.HOTBAR);
        check(Nocd.token[40] != null, "hotbar cannot cancel action");

        reset();
        press(50, Nocd.NONE);
        press(51, Nocd.NONE);
        check(released.equals(List.of("50")), "ordinary cancels ordinary");
        Nocd.unpress(51);
        check(Nocd.token[50] == null, "no restore on release");

        System.out.println("Nocd cancel table OK");
    }
}
