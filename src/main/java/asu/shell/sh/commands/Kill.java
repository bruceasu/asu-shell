package asu.shell.sh.commands;

@Cmd("killjob")
public class Kill extends Command {

    @Override
    public void execute(String... paramArrayOfString) throws Exception {
        int[] arrayOfInt = new int[paramArrayOfString.length];
        for (int i = 0; i < paramArrayOfString.length; i++) {
            arrayOfInt[i] = Integer.parseInt(paramArrayOfString[i]);
        }
        killJobs(arrayOfInt);
    }

    @Override
    public void usage() {
        out().println("killjob ...");
        out()
                .println(
                        "    Terminates execution of the specified jobs.\n"
                                + "    The job numbers are obtained by running the\n"
                                + "    jobs Cmd.");
    }
}
