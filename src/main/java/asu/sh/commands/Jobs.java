package asu.sh.commands;

@Cmd("jobs")
public class Jobs extends Command {

    @Override
    public void execute(String... args) {
        printJobs();
    }

    @Override
    public void usage() {
        out().println("jobs");
        out().println("    Prints currently running background jobs.");
    }
}
