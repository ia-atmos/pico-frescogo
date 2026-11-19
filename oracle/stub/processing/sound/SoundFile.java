package processing.sound;

// A mute stand-in for the sound library of Processing, to run the original
// with no audio device. Each `play` prints a line of trace, with the time of
// the sketch.
public class SoundFile {
    public static java.util.function.IntSupplier clock = () -> 0;
    private final String name;
    public SoundFile (processing.core.PApplet p, String path) {
        this.name = path.substring(path.lastIndexOf('/') + 1);
    }
    public void play () {
        System.out.println(String.format("[%06d] snd %s", clock.getAsInt(), name));
    }
}
