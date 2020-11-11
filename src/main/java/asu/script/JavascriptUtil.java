package asu.script;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Various Javascript code utilities.
 * The escape classes were taken from jakarta-commons-lang which in turn
 * borrowed from Turbine and other projects. The list of authors below is almost
 * certainly far too long, but I'm not sure who really wrote these methods.
 *
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 * @author Henri Yandell
 * @author Alexander Day Chaffee
 * @author Antony Riley
 * @author Helge Tesgaard
 * @author Sean Brown
 * @author Gary Gregory
 * @author Phil Steitz
 * @author Pete Gieser
 */
public class JavascriptUtil {

    /**
     * <p>Escapes the characters in a <code>String</code> using JavaScript String rules.</p>
     * <p>Escapes any values it finds into their JavaScript String form.
     * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
     *
     * <p>So a tab becomes the characters <code>'\\'</code> and
     * <code>'t'</code>.</p>
     *
     * <p>The only difference between Java strings and JavaScript strings
     * is that in JavaScript, a single quote must be escaped.</p>
     *
     * <p>Example:
     * <pre>
     * input string: He didn't say, "Stop!"
     * output string: He didn\'t say, \"Stop!\"
     * </pre>
     * </p>
     *
     * @param str String to escape values in, may be null
     * @return String with escaped values, <code>null</code> if null string input
     */
    public static String escapeJavaScript(String str) {
        if (str == null) {
            return null;
        }

        StringBuffer writer = new StringBuffer(str.length() * 2);

        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                writer.append("\\u");
                writer.append(hex(ch));
            } else if (ch > 0xff) {
                writer.append("\\u0");
                writer.append(hex(ch));
            } else if (ch > 0x7f) {
                writer.append("\\u00");
                writer.append(hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b':
                        writer.append('\\');
                        writer.append('b');
                        break;
                    case '\n':
                        writer.append('\\');
                        writer.append('n');
                        break;
                    case '\t':
                        writer.append('\\');
                        writer.append('t');
                        break;
                    case '\f':
                        writer.append('\\');
                        writer.append('f');
                        break;
                    case '\r':
                        writer.append('\\');
                        writer.append('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            writer.append("\\u00");
                            writer.append(hex(ch));
                        } else {
                            writer.append("\\u000");
                            writer.append(hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        // If we wanted to escape for Java strings then we would
                        // not need this next line.
                        writer.append('\\');
                        writer.append('\'');
                        break;
                    case '"':
                        writer.append('\\');
                        writer.append('"');
                        break;
                    case '\\':
                        writer.append('\\');
                        writer.append('\\');
                        break;
                    default:
                        writer.append(ch);
                        break;
                }
            }
        }

        return writer.toString();
    }

    /**
     * <p>Returns an upper case hexadecimal <code>String</code> for the given
     * character.</p>
     *
     * @param ch The character to convert.
     * @return An upper case hexadecimal <code>String</code>
     */
    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }

    /**
     * <p>Unescapes any JavaScript literals found in the <code>String</code>.</p>
     * <p>For example, it will turn a sequence of <code>'\'</code> and <code>'n'</code>
     * into a newline character, unless the <code>'\'</code> is preceded by another
     * <code>'\'</code>.</p>
     *
     * @param str the <code>String</code> to unescape, may be null
     * @return A new unescaped <code>String</code>, <code>null</code> if null string input
     */
    public static String unescapeJavaScript(String str) {
        if (str == null) {
            return null;
        }

        StringBuffer writer = new StringBuffer(str.length());
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;

        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        writer.append((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException(
                                "Unable to parse unicode value: " + unicode + " cause: " + nfe);
                    }
                }
                continue;
            }

            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        writer.append('\\');
                        break;
                    case '\'':
                        writer.append('\'');
                        break;
                    case '\"':
                        writer.append('"');
                        break;
                    case 'r':
                        writer.append('\r');
                        break;
                    case 'f':
                        writer.append('\f');
                        break;
                    case 't':
                        writer.append('\t');
                        break;
                    case 'n':
                        writer.append('\n');
                        break;
                    case 'b':
                        writer.append('\b');
                        break;
                    case 'u':
                        // uh-oh, we're in unicode country....
                        inUnicode = true;
                        break;
                    default:
                        writer.append(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            writer.append(ch);
        }

        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            writer.append('\\');
        }

        return writer.toString();
    }

    /**
     * Check to see if the given word is reserved or a bad idea in any known
     * version of JavaScript.
     *
     * @param name The word to check
     * @return false if the word is not reserved
     */
    public static boolean isReservedWord(String name) {
        return reserved.contains(name);
    }

    /**
     * The array of javascript reserved words
     */
    private static final String[] RESERVED_ARRAY = new String[] {
                    // Reserved and used at ECMAScript 4
                    "as",
                    "break",
                    "case",
                    "catch",
                    "class",
                    "const",
                    "continue",
                    "default",
                    "delete",
                    "do",
                    "else",
                    "export",
                    "extends",
                    "false",
                    "finally",
                    "for",
                    "function",
                    "if",
                    "import",
                    "in",
                    "instanceof",
                    "is",
                    "namespace",
                    "new",
                    "null",
                    "package",
                    "private",
                    "public",
                    "return",
                    "super",
                    "switch",
                    "this",
                    "throw",
                    "true",
                    "try",
                    "typeof",
                    "use",
                    "var",
                    "void",
                    "while",
                    "with",
                    // Reserved for future use at ECMAScript 4
                    "abstract",
                    "debugger",
                    "enum",
                    "goto",
                    "implements",
                    "interface",
                    "native",
                    "protected",
                    "synchronized",
                    "throws",
                    "transient",
                    "volatile",
                    // Reserved in ECMAScript 3, unreserved at 4 best to avoid anyway
                    "boolean",
                    "byte",
                    "char",
                    "double",
                    "final",
                    "float",
                    "int",
                    "long",
                    "short",
                    "static",

                    // I have seen the following list as 'best avoided for function names'
                    // but it seems way to all encompassing, so I'm not going to include it
        /*
        "alert", "anchor", "area", "arguments", "array", "assign", "blur",
        "boolean", "button", "callee", "caller", "captureevents", "checkbox",
        "clearinterval", "cleartimeout", "close", "closed", "confirm",
        "constructor", "date", "defaultstatus", "document", "element", "escape",
        "eval", "fileupload", "find", "focus", "form", "frame", "frames",
        "getclass", "hidden", "history", "home", "image", "infinity",
        "innerheight", "isfinite", "innerwidth", "isnan", "java", "javaarray",
        "javaclass", "javaobject", "javapackage", "length", "link", "location",
        "locationbar", "math", "menubar", "mimetype", "moveby", "moveto",
        "name", "nan", "navigate", "navigator", "netscape", "number", "object",
        "onblur", "onerror", "onfocus", "onload", "onunload", "open", "opener",
        "option", "outerheight", "outerwidth", "packages", "pagexoffset",
        "pageyoffset", "parent", "parsefloat", "parseint", "password",
        "personalbar", "plugin", "print", "prompt", "prototype", "radio", "ref",
        "regexp", "releaseevents", "reset", "resizeby", "resizeto",
        "routeevent", "scroll", "scrollbars", "scrollby", "scrollto", "select",
        "self", "setinterval", "settimeout", "status", "statusbar", "stop",
        "string", "submit", "sun", "taint",  "text", "textarea", "toolbar",
        "top", "tostring", "unescape", "untaint", "unwatch", "valueof", "watch",
        "window",
        */
            };

    /**
     * 编码带有中文名称Url。
     *
     * @param url url中的中文
     */
    public static String encodeUrl(String url) {
        return encodeUrl(url, "UTF-8");
    }

    /**
     * 编码带有中文名称Url。
     *
     * @param url url中的中文
     * @param targetCode 目标字符
     */
    public static String encodeUrl(String url, String targetCode) {
        String encodeUrl = "";
        if (url == null || url.trim() == "") {
            return "";
        }
        //编码并转换空格
        try {
            encodeUrl = URLEncoder.encode(url, targetCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeUrl;
    }

    public static String decodeUrl(String url, String targetCode) {
        String decodeUrl = "";
        if (url == null || url.trim().equals("")) {
            return "";
        }
        //编码并转换空格
        try {
            decodeUrl = URLDecoder.decode(url, targetCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decodeUrl;
    }

    public static String getUnicode(String str) {
        if (str == null || str.trim().equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (char ch : str.toCharArray()) {
            builder.append("\\u").append(Integer.toHexString(ch));
        }
        return builder.toString();
    }

    public static String getStringUnicode(String str) {
        throw new RuntimeException("未实现！");
    }

    /**
     * The list of reserved words
     */
    private static SortedSet<String> reserved = new TreeSet<String>();

    /**
     * For easy access ...
     */
    static {
        // The Javascript reserved words array so we don't generate illegal javascript
        reserved.addAll(Arrays.asList(RESERVED_ARRAY));
    }

    public static void main(String[] args) {
        String x = "是否好运！";
        System.out.println(getUnicode(x));
    }
}