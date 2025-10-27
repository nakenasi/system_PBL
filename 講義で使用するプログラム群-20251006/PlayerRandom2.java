import java.util.Random;

public class PlayerRandom2 extends Player {
    private Random rnd;

    public void initializeAll() {
        rnd = new Random();
    }

    public int decide(int s, int t, int pn) {
        int d;
        while(true) {
            d = rnd.nextInt(13)+1; 
            if(PlayGame.isValid(pn, d)) {
                return d;
            }
        }
    }
}
