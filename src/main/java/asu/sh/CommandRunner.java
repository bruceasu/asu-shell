package asu.sh;

import asu.sh.commands.Command;
import asu.sh.commands.ShellCommandWrapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRunner.class);

    private final String commandLine;

    private String cmd;

    private String[] cmdOpts;

    private boolean bg;

    private ThreadRunner runner;

    private Map context = new HashMap();

    public void addContext(Map m) {
        if (m == null) {
            return;
        }
        context.putAll(m);
    }

    public void addContext(String key, Object value) {
        getContext().put(key, value);
    }

    public Map getContext() {
        return context;
    }

    public void setContext(Map context) {
        this.context = context;
    }

    public class ThreadRunner extends Thread {

        private volatile Thread myThread = Thread.currentThread();

        public ThreadRunner() {
        }

        @Override
        public void run() {
            if (myThread == null) {
                return; // stopped before started.
            }
            try {
                // all the run() method's code goes here
                executeBg();
                // do some work
                Thread.yield(); // let another thread have some time perhaps to
                // stop this one.
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Stopped by ifInterruptedStop()");
                }
                // do some more work
                myThread = null;
            } catch (Throwable t) {
                LOGGER.error("", t);
            }
        }

        public void kill() {
            Thread tmpBlinker = myThread;
            myThread = null;
            if (tmpBlinker != null) {
                tmpBlinker.interrupt();
            }

        }
    }

    public Thread getThread() {
        if (runner != null) {
            return runner;
        }
        runner = new ThreadRunner();

        return runner;
    }

    public void kill() {
        if (runner != null) {
            runner.kill();
        }
    }

    public boolean isBg() {
        return bg;
    }

    public void setBg(boolean bg) {
        this.bg = bg;
    }

    public CommandRunner(String cmd) {
        this.commandLine = cmd;
        praseCmd();
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void execute() throws Exception {
        Command cmd = getCommand();
        if (cmd != null) {
            History.getInstance().record(commandLine);
            cmd.addContext(getContext());
            cmd.execute(cmdOpts);
        }
    }

    public void executeBg() throws Exception {
        Command cmd = getCommand();
        if (cmd != null) {
            cmd.addContext(getContext());
            cmd.executeBg(cmdOpts);
        }
    }

    protected Command getCommand() {
        // to find the Command
        // find it at register first
        Command cmd = RegisterCommand.getCommand(this.cmd);
        if (cmd != null) {
            return cmd;
        }
        // consider as a shell cmd
        return new ShellCommandWrapper(this.cmd);
    }

    protected void praseCmd() {
        String cl = this.getCommandLine();

        if (cl == null || cl.trim().length() == 0) {
            return;
        }

        cl = StringUtils.strip(cl);
        if (cl.charAt(cl.length() - 1) == '&') {
            bg = true;
            cl = cl.substring(0, cl.length() - 1).trim();
        }

        String[] opts = cl.split("\\s+");
        if (opts.length == 1) {
            this.cmd = opts[0];
        } else {
            this.cmd = opts[0];
            this.cmdOpts = new String[opts.length - 1];
            System.arraycopy(opts, 1, this.cmdOpts, 0, this.cmdOpts.length);
        }
    }
}
