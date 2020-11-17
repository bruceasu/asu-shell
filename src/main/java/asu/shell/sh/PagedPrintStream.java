package asu.shell.sh;

import static asu.shell.sh.util.EnvManager.ASU_SHELL_LINES;

import java.io.IOException;
import java.io.PrintStream;

public class PagedPrintStream extends PrintStream {

    private int linesPrinted;

    @Override
    public void println() {
        super.println();
        check_page_end();
    }

    @Override
    public void println(boolean paramBoolean) {
        super.println(paramBoolean);
        check_page_end();
    }

    @Override
    public void println(char paramChar) {
        super.println(paramChar);
        check_page_end();
    }

    @Override
    public void println(char[] paramArrayOfChar) {
        super.println(paramArrayOfChar);
        check_page_end();
    }

    @Override
    public void println(double paramDouble) {
        super.println(paramDouble);
        check_page_end();
    }

    @Override
    public void println(float paramFloat) {
        super.println(paramFloat);
        check_page_end();
    }

    @Override
    public void println(int paramInt) {
        super.println(paramInt);
        check_page_end();
    }

    @Override
    public void println(long paramLong) {
        super.println(paramLong);
        check_page_end();
    }

    @Override
    public void println(Object paramObject) {
        super.println(paramObject);
        check_page_end();
    }

    @Override
    public void println(String paramString) {
        super.println(paramString);
        check_page_end();
    }

    private void check_page_end() {
        String str = System.getProperty(ASU_SHELL_LINES);
        int i = str == null ? Integer.MAX_VALUE - 1: Integer.parseInt(str) -1;
        this.linesPrinted++;
        if (this.linesPrinted == i) {
            super.println("Press Enter for more.");
            super.flush();
            try {
                System.in.read();
            } catch (IOException localIOException) {
            }
            //super.println();
            this.linesPrinted = 0;
        }
    }

    public PagedPrintStream(PrintStream paramPrintStream) {
        super(paramPrintStream);
    }
}
