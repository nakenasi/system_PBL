import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Player_Hybrid extends Player {

    private static final int DP_START_TURN = 5;
    private HashSet<Integer>[] playedCards;
    private Map<Long, Double> memo;

    @Override
    public void initializeAll() {
        this.memo = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(int s) {
        this.playedCards = (HashSet<Integer>[]) new HashSet[2];
        this.playedCards[0] = new HashSet<Integer>();
        this.playedCards[1] = new HashSet<Integer>();
    }
    private void updateHistory(int s, int t) {
        this.playedCards[0].clear();
        this.playedCards[1].clear();

        for (int i = 0; i < t; i++) {
            int card0 = PlayGame.getCard(s, i, 0);
            int card1 = PlayGame.getCard(s, i, 1);

            this.playedCards[0].add(card0);
            this.playedCards[1].add(card1);
        }
    }

    private ArrayList<Integer> getRemainingHandFromState(int playerNum) {
        ArrayList<Integer> remainingHand = new ArrayList<>();
        HashSet<Integer> used = this.playedCards[playerNum];

        for (int card = 1; card <= 13; card++) {
            if (!used.contains(card)) {
                remainingHand.add(card);
            }
        }
        return remainingHand;
    }

    @Override
    public int decide(int s, int t, int pn) {

        if (t > 0) {
            updateHistory(s, t);
        }

        ArrayList<Integer> myHand = getRemainingHandFromState(pn);
        int oppPlayerNum = 1 - pn;
        ArrayList<Integer> oppHand = getRemainingHandFromState(oppPlayerNum);

        int cardToPlay;

        if (t < DP_START_TURN) {

            ArrayList<Integer> lowRiskCardsInHand = new ArrayList<>();
            ArrayList<Integer> highRiskCardsInHand = new ArrayList<>();
            for (int card : myHand) {
                if (card <= 6) {
                    lowRiskCardsInHand.add(card);
                } else {
                    highRiskCardsInHand.add(card);
                }
            }
            if (!lowRiskCardsInHand.isEmpty()) {
                cardToPlay = findBestCardByEV(lowRiskCardsInHand, oppHand);
            } else {
                cardToPlay = findBestCardByEV(highRiskCardsInHand, oppHand);
            }

        } else {
            if (t == DP_START_TURN) {
                this.memo.clear();
            }
            cardToPlay = findBestCardRecursive(myHand, oppHand);
        }

        return cardToPlay;
    }

    private int findBestCardByEV(ArrayList<Integer> myCardPool, ArrayList<Integer> oppHand) {

        int bestCard = -1;
        double maxEV = -Double.MAX_VALUE;
        for (int myCard : myCardPool) {
            double currentCardEV = 0.0;

            if (oppHand.isEmpty()) {
                currentCardEV = 0.0;
            } else {
                for (int oppCard : oppHand) {
                    currentCardEV += calculateScore(myCard, oppCard);
                }
                currentCardEV /= oppHand.size(); 
            }

            if (currentCardEV > maxEV) {
                maxEV = currentCardEV;
                bestCard = myCard;
            }
        }
        if (bestCard == -1 && !myCardPool.isEmpty()) {
            return myCardPool.get(0);
        }

        return bestCard;
    }

    private int findBestCardRecursive(ArrayList<Integer> myHand, ArrayList<Integer> oppHand) {

        int bestCard = -1;
        double maxOverallEV = -Double.MAX_VALUE;

        for (int myCard : myHand) {
            double currentTurnEV = 0.0;
            double futureTurnsEV = 0.0;

            if (oppHand.isEmpty()) {
                currentTurnEV = 0.0;
                futureTurnsEV = 0.0;
            } else {
                for (int oppCard : oppHand) {
                    currentTurnEV += calculateScore(myCard, oppCard);

                    ArrayList<Integer> nextMyHand = new ArrayList<>(myHand);
                    nextMyHand.remove(Integer.valueOf(myCard));

                    ArrayList<Integer> nextOppHand = new ArrayList<>(oppHand);
                    nextOppHand.remove(Integer.valueOf(oppCard));

                    futureTurnsEV += solveMaxEV(nextMyHand, nextOppHand);
                }
                currentTurnEV /= oppHand.size();
                futureTurnsEV /= oppHand.size();
            }

            double totalEV = currentTurnEV + futureTurnsEV;

            if (totalEV > maxOverallEV) {
                maxOverallEV = totalEV;
                bestCard = myCard;
            }
        }

        if (bestCard == -1 && !myHand.isEmpty()) {
            return myHand.get(0);
        }
        return bestCard;
    }

    private double solveMaxEV(ArrayList<Integer> myHand, ArrayList<Integer> oppHand) {
        if (myHand.isEmpty()) {
            return 0.0;
        }

        long stateKey = (getMask(myHand) << 16) | getMask(oppHand);

        if (memo.containsKey(stateKey)) {
            return memo.get(stateKey);
        }

        double bestFutureEV = -Double.MAX_VALUE;

        for (int myCard : myHand) {
            double currentTurnEV = 0.0;
            double futureTurnsEV = 0.0;

            if (oppHand.isEmpty()) {
                currentTurnEV = 0.0;
                futureTurnsEV = 0.0;
            } else {
                for (int oppCard : oppHand) {
                    currentTurnEV += calculateScore(myCard, oppCard);

                    ArrayList<Integer> nextMyHand = new ArrayList<>(myHand);
                    nextMyHand.remove(Integer.valueOf(myCard));

                    ArrayList<Integer> nextOppHand = new ArrayList<>(oppHand);
                    nextOppHand.remove(Integer.valueOf(oppCard));

                    futureTurnsEV += solveMaxEV(nextMyHand, nextOppHand);
                }
                currentTurnEV /= oppHand.size();
                futureTurnsEV /= oppHand.size();
            }

            double totalEV = currentTurnEV + futureTurnsEV;

            if (totalEV > bestFutureEV) {
                bestFutureEV = totalEV;
            }
        }

        memo.put(stateKey, bestFutureEV);
        return bestFutureEV;
    }

    private long getMask(ArrayList<Integer> hand) {
        long mask = 0;
        for (int card : hand) {
            mask |= (1L << (card - 1));
        }
        return mask;
    }

    private int calculateScore(int myCard, int oppCard) {
        int score = myCard + oppCard;

        if (myCard == oppCard) {
            return 0;
        }

        if (myCard == 1 && oppCard == 13) {
            return score;
        }
        if (myCard == 13 && oppCard == 1) {
            return -score;
        }

        if (myCard > oppCard) {
            return score;
        } else {
            return -score;
        }
    }
}