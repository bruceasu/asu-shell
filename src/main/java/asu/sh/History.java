package asu.sh;

import asu.sh.util.Assertion;
import asu.sh.util.Util;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class History {

    private static History instance = new History();

    private AtomicInteger idCounter = new AtomicInteger(0);

    private final Hashtable<Integer, Record> commands = new Hashtable<Integer, Record>();

    public static History getInstance() {
        return instance;
    }

    public void record(String commandLine) {
        Record localRecord = new Record(idCounter.getAndIncrement(), commandLine);
        this.commands.put(new Integer(localRecord.getId()), localRecord);
        trimHistory();
    }

    public void printLast(int paramInt, PrintStream paramPrintStream) {
        if ((paramInt == 0) || (paramInt > this.commands.size())) {
            paramInt = this.commands.size();
        }
        int i = this.idCounter.get() - paramInt;
        int j = this.idCounter.get() - 1;
        for (int k = i; k <= j; k++) {
            Integer localInteger = new Integer(k);
            Record localRecord = this.commands.get(localInteger);
            paramPrintStream.println(localRecord.getId() + ": " + localRecord.getCommandLine());
        }
    }

    public String[] getHistArray() {
        List<String> list = new ArrayList();
        int i = 0;
        int j = this.idCounter.get();
        for (int k = i; k < j; k++) {
            Integer localInteger = new Integer(k);
            Record localRecord = this.commands.get(localInteger);
            list.add(localRecord.getCommandLine());
        }

        return list.toArray(new String[0]);
    }

    public int lastCommandId() {
        return this.idCounter.get() - 1;
    }

    public String commandLine(int paramInt) {
        Record localRecord = this.commands.get(new Integer(paramInt));
        return localRecord == null ? null : localRecord.getCommandLine();
    }

    private void trimHistory() {
        int i = Integer.parseInt(Util.systemProperty("asu.shell.history_size"));

        int j = this.idCounter.get() - this.commands.size();
        int k = this.commands.size() - i;
        int m = j + k - 1;
        for (int n = j; n <= m; n++) {
            Integer localInteger = new Integer(n);
            Assertion.check(this.commands.remove(localInteger) != null);
        }
    }

    static class Record {

        private final int id;

        private final String commandLine;

        public int getId() {
            return this.id;
        }

        public String getCommandLine() {
            return this.commandLine;
        }

        public Record(int paramInt, String paramString) {
            this.id = paramInt;
            this.commandLine = paramString;
        }
    }
}
