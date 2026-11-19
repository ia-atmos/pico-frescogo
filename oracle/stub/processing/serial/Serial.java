package processing.serial;

// A stand-in for the serial library of Processing, with no port: the
// original then runs with no radar (the manual mode).
public class Serial {
    public static String[] list () { return new String[] {}; }
    public Serial (processing.core.PApplet p, String port, int baud) {
        throw new RuntimeException("no serial port");
    }
    public int    read      ()      { return -1; }
    public int    available ()      { return 0; }
    public byte[] readBytes (int n) { return new byte[n]; }
    public void   clear     ()      {}
}
