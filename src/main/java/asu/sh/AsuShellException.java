package asu.sh;

import asu.sh.commands.Exit;
import asu.tools.Main;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

public class AsuShellException extends RuntimeException {

    private static final long serialVersionUID = 203580105970257538L;

    private Vector<Throwable> _exceptions;

    public void describe(PrintStream paramPrintStream) {
        if (this._exceptions == null) {
            printStackTrace(paramPrintStream);
        } else {
            Enumeration<Throwable> localEnumeration = this._exceptions.elements();
            while (localEnumeration.hasMoreElements()) {
                Throwable localThrowable = localEnumeration.nextElement();
                if ((localThrowable instanceof Exit.ReallyExit)) {
                    Main.exit(0);
                } else {
                    localThrowable.printStackTrace(paramPrintStream);
                }
            }
        }
    }

    public AsuShellException(String paramString) {
        super(paramString);
    }

    public AsuShellException(Vector<Throwable> paramVector) {
        super("Command execution failed.");
        this._exceptions = paramVector;
    }
}
