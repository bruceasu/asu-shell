package asu.shell.sh.util;

public class Assertion {

    public static void check(boolean paramBoolean) {
        if (!paramBoolean) {
            throw new RuntimeException("Assertion failed!");
        }
    }
}
