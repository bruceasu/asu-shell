package asu.shell;

import static asu.shell.sh.util.EnvManager.ASU_SHELL_COLUMNS;
import static asu.shell.sh.util.EnvManager.ASU_SHELL_LINES;
import static asu.shell.sh.util.EnvManager.ASU_SHELL_PAGE;

import asu.shell.gui.GuiConsole;
import asu.shell.sh.AsuShellSecurityManager;
import asu.shell.sh.CommandRunner;
import asu.shell.sh.Jobs;
import asu.shell.sh.util.EnvManager;
import asu.shell.sh.util.Util;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import me.asu.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    class Exitcall implements Runnable {
        @Override
        public void run() {
            exit(0);
        }
    }



    private static AsuShellSecurityManager security_manager = (AsuShellSecurityManager) EnvManager
            .getContextValue("security_manager");
    ;

    public static void exit(int i) {
        security_manager.okToExit();
        System.exit(i);
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.parseCmdLine(args);
        try {
            main.initialize();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        try {
            main.mainLoop();
            exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }

    }

    private String startPath = "";

    private GuiConsole guiConsole;

    private boolean useGUI = false;

    public void initialize() throws Exception {
        if (useGUI) {
            guiConsole = new GuiConsole();
            guiConsole.setExitCall(new Exitcall());
            // console.setVisible(true);
            EnvManager.addContext("console", guiConsole);
            Util.systemProperty(ASU_SHELL_PAGE, "false", false);
            Util.systemProperty(ASU_SHELL_COLUMNS, "120", true);
            Util.systemProperty(ASU_SHELL_LINES, "30", true);
        }

    }

    private void mainLoop() throws IOException {
        if (useGUI) {
            EventQueue.invokeLater(() -> guiConsole.setVisible(true));

        }
        while (true) {
            if (!useGUI) {
                prompt();
            }
            String cmd = readCommandLine();
            if (cmd != null) {
                cmd = StringUtils.strip(cmd);
                if (cmd.length() > 0
                        && ("exit".equals(cmd.toLowerCase()) || "quit".equals(cmd.toLowerCase()))) {
                    return;
                }

                if (cmd.length() > 0 && "clear".equals(cmd.toLowerCase())) {
                    if (useGUI) {
                        guiConsole.clear();
                        continue;
                    }
                }
            }

            CommandRunner commandRunner = parseCmd(cmd);

            if (commandRunner.isBg()) {
                Jobs.getInstance().add(commandRunner);
            } else {
                try {
                    commandRunner.execute();
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    private void parseCmdLine(String[] args) {
        // 启动命令行参数处理
        File f = new File(".");
        startPath = f.getAbsolutePath();
        EnvManager.addContext("startPath", startPath);
        EnvManager.addContext("cmdline_args", args);

        Options opt = new Options();
        opt.addOption("h", "help", false, "显示帮助信息。");
        opt.addOption("u", "usage", false, "显示帮助信息。");
        opt.addOption("g", "gui", false, "使用控制台版。");
        // opt.addOption("dsn", true, "The data source to use");
        DefaultParser parser = new DefaultParser();
        CommandLine cl;
        try {
            cl = parser.parse(opt, args);
            if (cl.hasOption('h') || cl.hasOption('u')) {
                HelpFormatter help = new HelpFormatter();
                help.setSyntaxPrefix("使用方法：");
                help.printHelp(" java " + this.getClass().getName() + " [options] ", Strings.dup(
                        '=', 60), opt, Strings.dup('-', 60));
                exit(0);
            }
            if (cl.hasOption('g')) {
                useGUI = true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EnvManager.addContext("useGUI", useGUI);
    }

    private CommandRunner parseCmd(String cmd) {
        // TODO: 分析命令。
        return new CommandRunner(cmd);
    }

    private void prompt() {
        System.out.print(EnvManager.getContextValue("asu.shell.prompt"));
    }

    private String readCommandLine() throws IOException {
        String str = null;
        do {
            str = readline();
        } while ((str == null) || (str.trim().length() == 0));

        return str.trim();
    }

    private String readline() throws IOException {
        char[] rcb = new char[1024];
        Reader reader = new InputStreamReader(System.in);
        int len = reader.read(rcb, 0, rcb.length);
        if (len < 0) {
            return null; // EOL
        }
        if (rcb[len - 1] == '\r') {
            len--; // remove CR at end;
        } else if (rcb[len - 1] == '\n') {
            len--; // remove LF at end;
            if (len > 0 && rcb[len - 1] == '\r') {
                len--; // remove the CR, if there is one
            }
        }
        char[] b = new char[len];
        if (len > 0) {
            System.arraycopy(rcb, 0, b, 0, len);
        }
        return new String(b);
    }

}
