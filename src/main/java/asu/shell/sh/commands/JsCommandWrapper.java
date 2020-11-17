package asu.shell.sh.commands;

import java.io.FileReader;
import java.util.Arrays;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JsCommandWrapper extends Command {

    private String cmd;

    ScriptEngineManager manager = new ScriptEngineManager();

    private final ScriptEngine engine = manager.getEngineByName("js");

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public JsCommandWrapper(String file) {
        // load the file
        try {
            FileReader reader = new FileReader(file);
            engine.eval(reader);
            reader.close();
            engine.eval("var cmd = getCmd();var name =  cmd.getName();");
            String name = (String) engine.get("name");
            this.setCmd(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Override
    public void execute(String... paramArrayOfString) throws Exception {
        engine.put("args", Arrays.asList(paramArrayOfString));
        engine.eval("var cmd = getCmd(); cmd.execute(args);");
    }

    @Override
    public void executeBg(String... paramArrayOfString) throws Exception {
        engine.put("args", Arrays.asList(paramArrayOfString));
        engine.eval("var cmd = getCmd(); cmd.executeBg(args);");
    }

    @Override
    public void usage() {
        try {
            engine.eval("var cmd = getCmd(); cmd.usage();");
        } catch (ScriptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
