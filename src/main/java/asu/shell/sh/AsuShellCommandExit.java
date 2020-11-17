package asu.shell.sh;

public class AsuShellCommandExit extends SecurityException {

    private static final long serialVersionUID = -2114250263511428215L;
    private int _exit_code;

    public int exitCode() {
        return this._exit_code;
    }

    public AsuShellCommandExit(int paramInt) {
        super("System.exit(" + paramInt + ")");
        this._exit_code = paramInt;
    }
}
