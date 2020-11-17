package asu.shell.sh.commands;

import asu.shell.sh.RegisterCommand;
import me.asu.util.Strings;

@Cmd("help")
public class Help extends Command {

    private String _command;

    @Override
    public void execute(String... paramArrayOfString) throws ClassNotFoundException,
                                                             IllegalAccessException {
        process_args(paramArrayOfString);
        print_command_usage();
    }

    @Override
    public void usage() {
        out().println("help Cmd");
        out().println("    Provides usage information for builtin JShell\n    commands.");
        String[] list = RegisterCommand.getCommandList();
        out().println("comnands: ");
        for (int i = 0, j = list.length; i < j; i++) {
            if (i == 0) {
                out().println(Strings.dup("=", 80));
            } else if (i % 8 == 0) {
                out().println("");
            }
            out().print(list[i]);
            out().print('\t');

        }
        out().println("\n" + Strings.dup("-", 80));
    }

    private void process_args(String... paramArrayOfString) {
        this._command = ((paramArrayOfString == null || paramArrayOfString.length == 0) ? "help"
                                                                                        : paramArrayOfString[0]);
    }

    private void print_command_usage() {
        try {
            Command localCommand = RegisterCommand.getCommand(this._command);
            localCommand.usage();
        } catch (Exception e) {
            System.out.println(this._command + " is not a JShell Cmd.");
        }
    }
}

/*
 * Location: /home/suk/workspace/asu-tools/lib/asu.shell.jar Qualified Name:
 * asu.shell.Cmd.help JD-Core Version: 0.6.0
 */
