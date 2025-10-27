import java.util.Random;

public class PlayerRandomError extends Player {
    private Random rnd;

    public void initializeAll() {
        rnd = new Random();
    }

    public int decide(int s, int t, int pn) {
        return rnd.nextInt(13)+1;
    }
}
