package asu.sh.commands;

import asu.sh.BgExecutor;
import java.io.BufferedReader;
import java.io.IOException;
import me.asu.runner.Shell.BufferedReaderHandler;
import me.asu.runner.Shell.ShellCommandExecutor2;

public class ShellCommandWrapper extends Command {


    private final String cmd;

    public ShellCommandWrapper(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public void execute(String... args) throws Exception {
        ShellCommandExecutor2 executor = createExecutor(args);
        executor.execute();
        int exitCode = executor.getExitCode();

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
        BgExecutor.execute(()->{
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
            try {
                executor.execute();
            } catch (IOException e) {
                // ignore
            }
        });

    }

}
