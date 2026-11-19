package processing.serial;

import java.io.*;

// A stand-in for the serial library of Processing. With no port, the
// original runs with no radar (the manual mode). A port that is a regular
// file is a scripted radar: its bytes are the bytes that arrive, and its end
// stops the program.
public class Serial {
    private final InputStream in;
    public static String[] list () { return new String[] {}; }
    public Serial (processing.core.PApplet p, String port, int baud) {
        File f = new File(port);
        if (!f.isFile()) {
            throw new RuntimeException("no serial port");
        }
        try {
            in = new BufferedInputStream(new FileInputStream(f));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public int read () {
        try {
            int c = in.read();
            if (c < 0) {
                System.out.println("serial : the line was closed");
                System.exit(1);
            }
            return c;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public int available () {
        try {
            return in.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public byte[] readBytes (int n) {
        byte[] b = new byte[n];
        for (int i=0; i<n; i++) {
            b[i] = (byte) read();
        }
        return b;
    }
    public void clear () {}
}
