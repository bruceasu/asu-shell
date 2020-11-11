package asu.sh.commands;

import asu.sh.RegisterCommand;
import me.asu.util.DateUtils;

@Cmd("reload")
public class ReloadCmds extends Command {

    @Override
    public void execute(String... args) throws ClassNotFoundException, IllegalAccessException {
        try {
            RegisterCommand.reload();
            out().println("重新加载成功。 " + DateUtils.now());
        } catch (Exception e) {
            System.err.println("重新加载失败。 " + e.getMessage() + DateUtils.now());
            e.printStackTrace();
        }
    }

    @Override
    public void usage() {
        out().println("reload");
        out().println("    reload the commands.");
    }

}
