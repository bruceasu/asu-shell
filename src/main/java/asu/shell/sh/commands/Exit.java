package asu.shell.sh.commands;

import asu.shell.sh.AsuShellException;

@Cmd(value = "exit")
public class Exit extends Command {

    @Override
    public void execute(String... paramArrayOfString) {
        throw new ReallyExit();
    }

    @Override
    public void usage() {
        out().println("exit");
        out().println("    Exit JShell immediately.");
    }

    public static class ReallyExit extends AsuShellException {

        private static final long serialVersionUID = -6086645468457197L;

        public ReallyExit() {
            super((String) null);
        }
    }
}
