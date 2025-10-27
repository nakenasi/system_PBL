import java.util.Random;

public class PlayerRandom extends Player {
    private Random rnd;
    private int card[];

    public void initializeAll() {
        rnd = new Random();
    }

    public void initialize(int s) {
        card = new int[13];
        for(int i=0; i<13; i++) {
            card[i] = i+1;
        }

        for(int i=0; i<100; i++) {
            int x = rnd.nextInt(13);
            int y = rnd.nextInt(13);
            int t = card[y];
            card[y] = card[x];
            card[x] = t;
        }
    }

    public int decide(int s, int t, int pn) {
        return card[t];
    }
}
