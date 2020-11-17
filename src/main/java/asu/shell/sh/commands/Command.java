package asu.shell.sh.commands;


import static asu.shell.sh.util.EnvManager.ASU_SHELL_PAGE;

import asu.shell.sh.History;
import asu.shell.sh.Jobs;
import asu.shell.sh.PagedPrintStream;
import asu.shell.sh.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class Command {

    private       InputStream      in          = null;
    private       PrintStream      out         = null;
    private       PrintStream      err         = null;
    private       boolean          redirectIn  = false;
    private       boolean          redirectOut = false;
    private final ThreadLocal<Map> context     = new ThreadLocal();

    public Command() {
        this.context.set(new HashMap());
    }

    private Map getContext() {
        Map m = (Map) this.context.get();
        if (m == null) {
            m = new HashMap();
            this.context.set(m);
        }
        return m;
    }

    public void addContext(String key, Object value) {
        getContext().put(key, value);
    }

    public void addContext(Map map) {
        getContext().putAll(map);
    }

    public Object getContextValue(String key) {
        return getContext().get(key);
    }

    public abstract void execute(String... paramVarArgs)
            throws Exception;

    public void executeBg(String... paramArrayOfString)
            throws Exception {
        execute(paramArrayOfString);
    }

    public void usage() {
        out().println("help not available for " + rmPackName(getClass().getName()));
    }

    protected final InputStream in() {
        if (this.in == null) {
            this.in = System.in;
        }
        return this.in;
    }

    protected final PrintStream out() {
        if (this.out == null) {
            this.out = System.out;
            if (System.getProperty(ASU_SHELL_PAGE).equals("true")) {
                this.out = new PagedPrintStream(this.out);
            }
        }
        return this.out;
    }

    protected final PrintStream err() {
        if (this.err == null) {
            this.err = System.err;
            if (System.getProperty(ASU_SHELL_PAGE).equals("true")) {
                this.err = new PagedPrintStream(this.err);
            }
        }
        return this.err;
    }

    protected void checkForInterruption()
            throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    public static Properties properties() {
        return System.getProperties();
    }

    public static String property(String paramString) {
        return System.getProperty(paramString);
    }

    public static void property(String paramString1, String paramString2) {
        System.getProperties().put(paramString1, paramString2);
    }

    public static void removeProperty(String paramString) {
        System.getProperties().remove(paramString);
    }

    public final void finish() {
        closeIn();
        closeOut();
        closeErr();
    }

    public static void connectOutputToInput(Command paramObject1, Command paramObject2)
            throws IOException {
        PipedOutputStream localPipedOutputStream = new PipedOutputStream();
        PipedInputStream localPipedInputStream = new PipedInputStream(localPipedOutputStream);
        paramObject1.out = new PrintStream(localPipedOutputStream);
        paramObject1.redirectOut = true;
        paramObject2.in = localPipedInputStream;
        paramObject2.redirectIn = true;
    }

    protected void printHistory(int n) {
        History.getInstance().printLast(n, out());
    }

    protected void printJobs() {
        Jobs.getInstance().print(out());
    }

    protected void killJobs(int[] paramArrayOfInt) {
        Jobs.getInstance().kill(paramArrayOfInt);
    }

    protected boolean isFlag(String paramString) {
        return paramString.charAt(0) == Util.systemProperty("asu.shell.flag").charAt(0);
    }

    protected String flag(String paramString) {
        return Util.systemProperty("asu.shell.flag") + paramString;
    }

    private void closeIn() {
        if (this.in != null) {
            try {
                if (this.redirectIn) {
                    this.in.close();
                }
            } catch (IOException localIOException) {
            }
        }
    }

    private void closeOut() {
        if (this.out != null) {
            this.out.flush();
            if (this.redirectOut) {
                this.out.close();
            }
        }
    }

    private void closeErr() {
        if (this.err != null) {
            this.err.flush();
        }
    }

    private String rmPackName(String paramString) {
        int i = paramString.lastIndexOf(".");
        return i < 0 ? paramString : paramString.substring(i + 1);
    }
}
