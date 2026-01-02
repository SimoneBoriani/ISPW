package start;

import java.util.Arrays;

public class Launcher {
    public static void main(String[] args) {
        Selection selection = new Selection();
        selection.loader(Arrays.toString(args));
    }
}
