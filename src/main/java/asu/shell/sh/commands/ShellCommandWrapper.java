package asu.shell.sh.commands;

import java.io.BufferedReader;
import java.io.IOException;
import me.asu.runner.Shell.BufferedReaderHandler;
import me.asu.runner.Shell.ExitCodeException;
import me.asu.runner.Shell.ShellCommandExecutor2;

public class ShellCommandWrapper extends Command {


    private final String cmd;

    public ShellCommandWrapper(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public void execute(String... args) throws Exception {
        ShellCommandExecutor2 executor = createExecutor(args);
        try {

            executor.execute();
        } catch (ExitCodeException e) {
            int exitCode = e.getExitCode();
            String message = e.getMessage();
            System.err.println("exit code: " + exitCode);
            System.err.println(message);
        }catch (Exception e) {
            String message = e.getMessage();
            System.err.println(message);
        }

    }

    private ShellCommandExecutor2 createExecutor(String[] args)
    {
        ShellCommandExecutor2 executor;
        if (args == null) {
            executor = new ShellCommandExecutor2(cmd);
        } else {
            String[] cmds = new String[args.length + 1];
            cmds[0] = cmd;
            System.arraycopy(args, 0, cmds, 1, args.length);
            executor = new ShellCommandExecutor2(cmds);
        }
        return executor;
    }

    @Override
    public void executeBg(String... args) throws Exception {
        BufferedReaderHandler handler = new BufferedReaderHandler() {
            @Override
            public void handle(BufferedReader reader) throws IOException
            {
                String line;
                while(null != (line = reader.readLine())) {
                    // throw away.
                }
            }
        };

        ShellCommandExecutor2 executor = createExecutor(args);
        executor.setHandler(handler);
        executor.execute();
    }

}
