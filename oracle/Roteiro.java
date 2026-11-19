// The original, driven by a script.
//
// `placar` is the class of the sketch, as it comes in the release package of
// v4.1.0 (`lib/placar.jar`), not changed. This subclass runs it as the tests
// run the port: frames of 16 ms of a clock of its own, keys pressed at given
// frames, and screenshots at given frames, read from the same variables:
//
//      PICO_ATM_FRAMES=n:16        runs `n` frames, and exits
//      PICO_ATM_KEYS=f:dn:A,...    presses (dn) or releases (up) keys
//      PICO_ATM_SHOT=f:x.png,...   saves the screen
//
// The names of the keys are those of SDL, as in pico. In a frame, the keys
// come first, with the time of the previous frame, and then `draw`.
// `delay` only advances the clock.

import java.util.*;
import processing.core.*;
import processing.event.KeyEvent;

public class Roteiro extends placar {
    int     frames  = 0;
    int     frame   = 0;
    int     clock   = 0;
    int     offset  = 0;
    boolean dentro  = false;
    boolean ctrl, shift, caps;

    List<String[]> keys  = new ArrayList<String[]>();
    List<String[]> shots = new ArrayList<String[]>();

    public static void main (String[] args) {
        // the sketch folder (`data/`, `relatorios/`) is the current directory
        String dir = System.getProperty("user.dir");
        PApplet.runSketch(new String[] { "--sketch-path=" + dir, "Roteiro" }, new Roteiro());
    }

    static List<String[]> script (String var) {
        List<String[]> ret = new ArrayList<String[]>();
        String s = System.getenv(var);
        if (s != null && !s.isEmpty()) {
            for (String item : s.split(",")) {
                ret.add(item.split(":", var.equals("PICO_ATM_KEYS") ? 3 : 2));
            }
        }
        return ret;
    }

    public void setup () {
        processing.sound.SoundFile.clock = () -> millis();
        frames = Integer.parseInt(System.getenv("PICO_ATM_FRAMES").split(":")[0]);
        keys   = script("PICO_ATM_KEYS");
        shots  = script("PICO_ATM_SHOT");
        super.setup();
        frameRate(1000);
    }

    public int millis () {
        return clock + offset;
    }

    public void delay (int ms) {
        offset += ms;
    }

    void tecla (boolean dn, String k) {
        switch (k) {
            case "Left Ctrl":  case "Right Ctrl":  ctrl  = dn; break;
            case "Left Shift": case "Right Shift": shift = dn; break;
            case "CapsLock":   if (dn) { caps = !caps; }       break;
        }
        char key  = CODED;
        int  code = 0;
        if (k.length() == 1) {
            char c = k.charAt(0);
            code = c;
            if (c >= 'A' && c <= 'Z') {
                key = ctrl ? (char)(c-64) : ((shift != caps) ? c : (char)(c+32));
            } else if (c == '-') {
                key = shift ? '_' : '-';
            } else {
                key = c;
            }
        } else {
            switch (k) {
                case "Left":         code = 37; break;
                case "Up":           code = 38; break;
                case "Right":        code = 39; break;
                case "Down":         code = 40; break;
                case "Backspace":    code = 8;  key = BACKSPACE; break;
                case "Return":       code = 10; key = ENTER;     break;
                case "Keypad Enter": code = 10; key = ENTER;     break;
                case "Escape":       code = 27; key = ESC;       break;
                case "Left Ctrl":  case "Right Ctrl":  code = 17; break;
                case "Left Shift": case "Right Shift": code = 16; break;
                case "CapsLock":     code = 20; break;
            }
        }
        int mods = (ctrl ? KeyEvent.CTRL : 0) | (shift ? KeyEvent.SHIFT : 0);
        handleKeyEvent(new KeyEvent(null, millis(), dn ? KeyEvent.PRESS : KeyEvent.RELEASE, mods, key, code));
    }

    public void draw () {
        if (dentro) {           // `go_termino` calls `draw`
            super.draw();
            return;
        }
        dentro = true;
        frame++;
        clock = 16 * (frame-1);
        for (String[] k : keys) {
            if (Integer.parseInt(k[0]) == frame) {
                tecla(k[1].equals("dn"), k[2]);
            }
        }
        clock = 16 * frame;
        super.draw();
        for (String[] s : shots) {
            if (Integer.parseInt(s[0]) == frame) {
                saveFrame(s[1]);
            }
        }
        dentro = false;
        if (frame >= frames) {
            exit();
        }
    }
}
