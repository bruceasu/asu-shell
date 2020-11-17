package asu.shell.sh.commands;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import me.asu.util.Strings;

@Cmd(value = "native2ascii")
public class Native2ascii extends Command {

    Options opt = new Options();

    public Native2ascii() {
        // ative2ascii -encoding UTF-8 utf8.properties nonUtf8.properties
        opt.addOption("h", "help", false, "显示帮助信息。");
        opt.addOption("u", "usage", false, "显示帮助信息。");
        opt.addOption("s", true, "来源。");
        opt.addOption("r", false, "反转。");
    }

    @Override
    public void execute(String... paramArrayOfString) {
        BasicParser parser = new BasicParser();
        CommandLine cl;
        try {
            if (paramArrayOfString == null || paramArrayOfString.length == 0) {
                usage();
                return;
            }
            cl = parser.parse(opt, paramArrayOfString);
            if (cl.hasOption('h') || cl.hasOption('u')) {
                usage();
            } else if (cl.hasOption('r') && cl.hasOption('s')) {
                // System.err.println("未实现！！");
                String s = cl.getOptionValue('s');

                int len = s.length();
                int start = 0;
                StringBuilder builder = new StringBuilder();
                while (start < len) {
                    int i = s.indexOf("\\u", start);
                    if (i != -1 && i != start) {
                        String x = s.substring(start, i);
                        builder.append(x);
                        String h = s.substring(i + 2, i + 6);
                        int ch = Integer.parseInt(h, 16);
                        builder.append((char) ch);
                        start = i + 6;
                    } else if (i != -1 && i == start) {
                        String h = s.substring(i + 2, i + 6);
                        int ch = Integer.parseInt(h, 16);
                        builder.append((char) ch);
                        start = i + 6;
                    } else {
                        String x = s.substring(start);
                        builder.append(x);
                        start = len;
                    }
                }
                out().println(builder.toString());
            } else if (cl.hasOption('s')) {
                String s = cl.getOptionValue('s');
                if (StringUtils.isEmpty(s)) {
                    out().println("");
                }
                StringBuilder builder = new StringBuilder();
                for (int i = 0, j = s.length(); i < j; i++) {
                    int ch = s.charAt(i);
                    if (ch < 127) {
                        builder.append((char) ch);
                    } else {
                        String x = Strings.fillHex(ch, 4);
                        builder.append("\\u").append(x);
                    }
                }
                out().println(builder.toString());
            } else {
                usage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void usage() {
        HelpFormatter help = new HelpFormatter();
        help.setSyntaxPrefix("使用方法：");
        help.printHelp(" native2ascii [options] ", Strings.dup('=', 60), opt, Strings.dup('-', 60));
    }
}
