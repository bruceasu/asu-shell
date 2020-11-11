package asu.sh;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Jobs {

    private final ExecutorService serivce = Executors.newCachedThreadPool();

    private static Jobs instance = new Jobs();

    private final Vector<CommandRunner> jobs = new Vector<CommandRunner>();

    public static Jobs getInstance() {
        return instance;
    }

    public void add(CommandRunner paramCommandRunner) {
        this.jobs.addElement(paramCommandRunner);
        serivce.execute(paramCommandRunner.getThread());
    }

    public CommandRunner job(long paramInt) {
        Enumeration<CommandRunner> localEnumeration = this.jobs.elements();
        while (localEnumeration.hasMoreElements()) {
            CommandRunner localCommandRunner = localEnumeration.nextElement();

            if (localCommandRunner.getThread().getId() == paramInt) {
                return localCommandRunner;
            }
        }
        return null;
    }

    public void remove(CommandRunner paramCommandRunner) {
        if (paramCommandRunner != null) {
            this.jobs.removeElement(paramCommandRunner);
        }
    }

    public void print(PrintStream paramPrintStream) {
        Enumeration<CommandRunner> localEnumeration = this.jobs.elements();
        while (localEnumeration.hasMoreElements()) {
            CommandRunner localCommandRunner = localEnumeration.nextElement();
            boolean done = false;
            if (!localCommandRunner.getThread().isAlive()) {
                this.remove(localCommandRunner);
                done = true;
            }
            paramPrintStream.print(localCommandRunner.getThread().getId());
            paramPrintStream.print(": ");
            paramPrintStream.println(localCommandRunner.getCommandLine() + (done ? " done " : ""));

        }
    }

    public void kill(int[] paramArrayOfInt) {
        for (int i = 0; i < paramArrayOfInt.length; i++) {
            int j = paramArrayOfInt[i];
            CommandRunner localCommandRunner = find(j);
            if (localCommandRunner == null) {
                continue;
            }
            remove(localCommandRunner);
            localCommandRunner.kill();
        }
    }

    private CommandRunner find(long paramInt) {
        Enumeration<CommandRunner> localEnumeration = this.jobs.elements();
        while (localEnumeration.hasMoreElements()) {
            CommandRunner localCommandRunner = localEnumeration.nextElement();

            if (localCommandRunner.getThread().getId() == paramInt) {
                return localCommandRunner;
            }
        }
        return null;
    }
}