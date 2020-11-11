package asu.sh;

import asu.sh.commands.Cmd;
import asu.sh.commands.Command;
import asu.sh.commands.JsCommandWrapper;
import asu.sh.commands.ShellCommandWrapper;
import asu.sh.util.EnvManager;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import me.asu.util.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterCommand {

    private static final Logger log = LoggerFactory.getLogger(RegisterCommand.class);

    private static Lock lock = new ReentrantLock();

    private static Map<String, Command> commands = new HashMap<String, Command>();

    static {
        initial();
    }

    public static void register(String key, Command cmd) {
        lock.lock();
        commands.put(key, cmd);
        lock.unlock();
    }

    public static void unregister(String key) {
        lock.lock();
        if (commands.containsKey(key)) {
            commands.remove(key);
        }
        lock.unlock();
    }

    public static Command getCommand(String key) {
        return commands.get(key);
    }

    public static String[] getCommandList() {
        String[] keys = commands.keySet().toArray(new String[0]);
        return keys;
    }

    public static void removeAllCommands() {
        lock.lock();
        commands.clear();
        lock.unlock();
    }

    public static void reload() {
        // URL[] externalURLs = new URL[]{ new URL(
        // "file:../TestHotDeployImpl/bin/" )};
        // cl = new URLClassLoader(externalURLs);
        // catClass = cl.loadClass( "com.unmi.CatImpl" );
        // FIXME: 没有重新加载类。
        removeAllCommands();
        initial();
    }

    public static void initial() {
        initJavaCommands();
        initScriptCommands();
        initShellCommands();
        initLinuxToolsInWindowsCommands();
    }

    private static void initLinuxToolsInWindowsCommands()
    {
        String os = (String) EnvManager.getContextValue("os.name");
        if (os != null && os.contains("Windows")) {
            // load the linux programs
            File f = new File("linuxtools");
            if (f.isDirectory()) {
                File[] shfiles = f.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(".exe")
                                || !pathname.getName().contains(".");
                    }

                });

                if (shfiles != null) {
                    for (File file : shfiles) {
                        try {
                            ShellCommandWrapper cmd = new ShellCommandWrapper(file.getAbsolutePath());
                            String name = file.getName();
                            int lastdot = name.lastIndexOf('.');
                            if (lastdot != -1) {
                                name = name.substring(0, lastdot);
                            }
                            register(name, cmd);
                        } catch (Exception e) {
                            log.error("", e);
                        }
                    }
                }
            }
        }
    }

    private static void initShellCommands()
    {
        File f = new File("shell");
        if (f.isDirectory()) {
            File[] shfiles = f.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".sh")
                            || pathname.getName().endsWith(".bat");
                }

            });

            if (shfiles != null) {
                for (File file : shfiles) {
                    try {
                        ShellCommandWrapper cmd = new ShellCommandWrapper(file.getAbsolutePath());
                        String name = file.getName();
                        int lastdot = name.lastIndexOf('.');
                        if (lastdot != -1) {
                            name = name.substring(0, lastdot);
                        }
                        register(name, cmd);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }

            }

        }
    }

    private static void initScriptCommands()
    {
        // load the scripts
        File f = new File("script");
        if (f.isDirectory()) {
            File[] jsfiles = f.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".js");
                }

            });

            if (jsfiles != null) {
                for (File js : jsfiles) {
                    try {
                        JsCommandWrapper cmd = new JsCommandWrapper(js.getAbsolutePath());
                        String name = cmd.getCmd();
                        register(name, cmd);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }

            }

        }
    }

    private static void initJavaCommands()
    {
        Set<Class<?>> cmds = ClassUtils.getClasses("asu.sh.commands");
        if (!cmds.isEmpty() ) {
            List<Class<?>> collect = cmds.stream()
                                         .filter(clazz -> clazz.isAnnotationPresent(Cmd.class))
                                         .collect(Collectors.toList());

            if (!collect.isEmpty()) {
                for (Class<?> clazz : cmds) {
                    Cmd a = clazz.getAnnotation(Cmd.class);
                    if (a == null) continue;
                    String name = a.value();
                    if (StringUtils.isEmpty(name)) {
                        name = clazz.getSimpleName().toLowerCase();
                    }
                    try {
                        Command cmd = (Command) ClassUtils.newInstance(clazz);
                        register(name, cmd);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }
    }
}
