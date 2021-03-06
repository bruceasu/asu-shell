package asu.shell.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class LoopedStreams {

    private final PipedOutputStream pipedOS = new PipedOutputStream();

    private boolean keepRunning = true;

    private final ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream() {
        @Override
        public void close() {
            keepRunning = false;
            try {
                super.close();
                pipedOS.close();
            } catch (IOException e) {
                System.exit(1);
            }
        }
    };

    private final PipedInputStream pipedIS = new PipedInputStream() {
        @Override
        public void close() {
            keepRunning = false;
            try {
                super.close();
            } catch (IOException e) {
                System.exit(1);
            }
        }
    };

    public LoopedStreams() throws IOException {
        pipedOS.connect(pipedIS);
        startByteArrayReaderThread();
    } // LoopedStreams()

    public InputStream getInputStream() {
        return pipedIS;
    } // getInputStream()

    public OutputStream getOutputStream() {
        return byteArrayOS;
    } // getOutputStream()

    private void startByteArrayReaderThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (keepRunning) {
                    // check the number of bytes in the stream
                    if (byteArrayOS.size() > 0) {
                        byte[] buffer = null;
                        synchronized (byteArrayOS) {
                            buffer = byteArrayOS.toByteArray();
                            byteArrayOS.reset(); // clear the buffer
                        }
                        try {
                            // send the data to PipedOutputStream
                            pipedOS.write(buffer, 0, buffer.length);
                        } catch (IOException e) {
                            // record the err or other things
                            // for simple, we just exit here
                            System.exit(1);
                        }
                    } else
                    // no data to read, then the thread go to sleep
                    {
                        try {
                            // check the ByteArrayOutputStream for new data
                            // every one second
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            keepRunning = false;
                        }
                    }
                }
            }
        }).start();
    } // startByteArrayReaderThread()
} // LoopedStreams
