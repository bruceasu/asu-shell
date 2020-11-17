package asu.shell.sh.commands;

import me.asu.util.Strings;

@Cmd("history")
public class History extends Command {

    @Override
    public void execute(String... paramArrayOfString) {
        out().println(Strings.dup("=", 80));
        out().println("历史：");
        out().println(Strings.dup("-", 80));
        if (paramArrayOfString == null || paramArrayOfString.length == 0) {
            printHistory(0);
        } else {
            printHistory(Integer.parseInt(paramArrayOfString[0]));
        }
        out().println(Strings.dup("-", 80));
    }

    @Override
    public void usage() {
        out().println("history [n]");
        out().println(
                "    Prints the last n commands. If n is omitted,\n"
                        + "    all recorded commands are printed. In both cases,\n"
                        + "    the number of commands printed is limited by the\n"
                        + "    value of asu.shell.history_size.");
    }
}
