package asu.sh;

import java.io.PrintStream;

/**
 * @author suk
 */
public class MultiLineBufferPrintStream extends PrintStream {

    private static final int BUFFER_SIZE = 500;

    private final StringBuffer buffer = new StringBuffer(BUFFER_SIZE);

    @Override
    public void flush() {
        flushToStream();
        super.flush();
    }

    @Override
    public void close() {
        flush();
        super.close();
    }

    @Override
    public boolean checkError() {
        flushToStream();
        return super.checkError();
    }

    @Override
    public void print(boolean paramBoolean) {
        String str = paramBoolean ? "true" : "false";
        this.buffer.append(str);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void print(char paramChar) {
        this.buffer.append(paramChar);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void print(int paramInt) {
        String str = Integer.toString(paramInt);
        this.buffer.append(str);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void print(long paramLong) {
        String str = Long.toString(paramLong);
        this.buffer.append(str);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void print(float paramFloat) {
        String str = Float.toString(paramFloat);
        this.buffer.append(str);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void print(double paramDouble) {
        String str = Double.toString(paramDouble);
        this.buffer.append(str);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void print(char[] paramArrayOfChar) {
        this.buffer.append(paramArrayOfChar);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void print(String paramString) {
        this.buffer.append(paramString);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void print(Object paramObject) {
        String str = paramObject.toString();
        this.buffer.append(str);
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println() {
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(boolean paramBoolean) {
        String str = paramBoolean ? "true" : "false";
        this.buffer.append(str);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(char paramChar) {
        this.buffer.append(paramChar);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(int paramInt) {
        String str = Integer.toString(paramInt);
        this.buffer.append(str);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(long paramLong) {
        String str = Long.toString(paramLong);
        this.buffer.append(str);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(float paramFloat) {
        String str = Float.toString(paramFloat);
        this.buffer.append(str);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(double paramDouble) {
        String str = Double.toString(paramDouble);
        this.buffer.append(str);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(char[] paramArrayOfChar) {
        this.buffer.append(paramArrayOfChar);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(String paramString) {
        this.buffer.append(paramString);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    @Override
    public void println(Object paramObject) {
        String str = paramObject.toString();
        this.buffer.append(str);
        this.buffer.append('\n');
        if (this.buffer.length() >= BUFFER_SIZE) {
            flushToStream();
        }

    }

    private void flushToStream() {
        super.print(this.buffer.toString());
        this.buffer.setLength(0);
    }

    public MultiLineBufferPrintStream(PrintStream paramPrintStream) {
        super(paramPrintStream);
    }
}
