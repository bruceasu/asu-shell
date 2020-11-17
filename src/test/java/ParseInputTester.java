import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ParseInputTester
{

    public static void main(String[] args)
    {
        // cp "a b.txt"  "c/\""
        String line = "  \tcp \"a b.txt\"  \"c/\\\"\" >>sdf 2>&1 &";
        System.out.println("line = " + line);
        String[] opts = parse(line);
        System.out.println(Arrays.toString(opts));
    }

    static String[] parse(String line)
    {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        List<String> tokens = new LinkedList<>();
        char[] chars = line.toCharArray();
        StringBuilder buffer = new StringBuilder();
        boolean quoteStart = false;
        boolean escapeStart = false;
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch == '\"') {
                if (escapeStart) {
                    buffer.append('\"');
                    escapeStart = false;
                } else if(quoteStart) {
                    // finished
                    tokens.add(buffer.toString());
                    buffer.setLength(0);
                    quoteStart = false;
                } else {
                    quoteStart = true;
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    }
                }
            } else if (ch == '\\') {
                if (escapeStart) {
                    buffer.append('\\');
                    escapeStart = false;
                } else {
                    escapeStart = true;
                }
            } else if (ch == 't') {
                if (escapeStart) {
                    buffer.append('\t');
                    escapeStart = false;
                } else {
                    buffer.append(ch);
                }
            } else if (ch == 'n') {
                if (escapeStart) {
                    buffer.append('\n');
                    escapeStart = false;
                } else {
                    buffer.append(ch);
                }
            } else if (ch == ' ') {
                if (quoteStart) {
                    buffer.append(' ');
                } else if (escapeStart) {
                    buffer.append(' ');
                    escapeStart = false;
                } else {
                    // finish
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    } else {
                        // skip
                    }
                }
            } else if (ch == '\t') {
                if (quoteStart) {
                    buffer.append('\t');
                } else if (escapeStart) {
                    buffer.append('\t');
                    escapeStart = false;
                } else {
                    // finish
                    if (buffer.length() > 0) {
                        tokens.add(buffer.toString());
                        buffer.setLength(0);
                    } else {
                        // skip
                    }
                }
            } else {
                buffer.append(ch);
            }
        }
        if (buffer.length() > 0) {
            tokens.add(buffer.toString());
            buffer.setLength(0);
        }

        return tokens.toArray(new String[tokens.size()]);
    }
}
