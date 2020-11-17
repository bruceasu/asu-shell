package asu.shell.sh.commands;

@Cmd(value = "gc")
public class GC extends Command {

    @Override
    public void execute(String... paramArrayOfString) {
        System.out.print("call System.gc() ... ");
        System.gc();
        System.out.println("done! ");
    }

    @Override
    public void usage() {
        out().println("gc");
        out().println("    Run the garbage collector.");
    }
}
