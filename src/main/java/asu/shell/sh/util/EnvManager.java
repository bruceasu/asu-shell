package asu.shell.sh.util;

import asu.shell.sh.AsuShellSecurityManager;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class EnvManager {

    private static final Map context = new HashMap();

    public static final String ASU_SHELL_PAGE = "asu.shell.page";

    public static final String ASU_SHELL_PROMPT = "asu.shell.prompt";

    public static final String ASU_SHELL_COLUMNS = "asu.shell.columns";

    public static final String ASU_SHELL_LINES = "asu.shell.lines";

    public static final String ASU_SHELL_DIR = "asu.shell.dir";

    public static final String ASU_SHELL_HISTORY_SIZE = "asu.shell.history_size";

    public static final String ASU_SHELL_FLAG = "asu.shell.flag";

    static {
        Util.systemProperty(ASU_SHELL_PROMPT, "asu-toolkits >  ", true);
        Util.systemProperty(ASU_SHELL_PAGE, "true", true);
        Util.systemProperty(ASU_SHELL_COLUMNS, "80", true);
        Util.systemProperty(ASU_SHELL_LINES, "25", true);
        Util.systemProperty(ASU_SHELL_FLAG, "-", true);
        Util.systemProperty(ASU_SHELL_DIR, System.getProperty("user.dir"), true);
        Util.systemProperty(ASU_SHELL_HISTORY_SIZE, "100", true);

        EnvManager.addContext(System.getProperties());
        EnvManager.addContext(System.getenv());

        AsuShellSecurityManager security_manager = new AsuShellSecurityManager();
        EnvManager.addContext("security_manager", security_manager);

    }

    public static String getPS() {
        return (String) getContextValue(ASU_SHELL_PROMPT);
    }

    public static Map getContext() {
        return context;
    }

    public static Object getContextValue(String key) {
        return context.get(key);
    }

    public static void addContext(String key, Object value) {
        context.put(key, value);
    }

    public static void addContext(Map m) {
        context.putAll(m);
    }
}
