public class PlayGame {
    private static Player player0;
    private static Player player1;
    private static int[][][] selectedCard;
    private static int[][] score;
    private static int currentSet = 0;
    private static int currentTurn = 0;
    private static int MaxSet = 1000;
    private static int MaxTurn = 13;
    private static int logLevel = 4;

    private static int lastScore0 = -1;
    private static int lastScore1 = -1;
    private static boolean lastWin0 = false;
    private static boolean lastWin1 = false;
    private static int lastNumWin0 = 0;
    private static int lastNumWin1 = 0;

    /**
     * 対戦を行う(対戦時にはこれを呼び出す)
     * @param po0 プレイヤー0
     * @param po1 プレイヤー1
     * @return 勝ったプレイヤー番号 (引き分けなら -1)
     */
    public static int match(Player po0, Player po1) {
        selectedCard = new int[MaxSet][MaxTurn][2];
        score = new int[MaxSet][2];
        currentSet = 0;
        currentTurn = 0;

        player0 = po0;
        player1 = po1;
        player0.initializeAll();
        player1.initializeAll();

        for(currentSet = 0; currentSet < MaxSet; currentSet++) {
            oneSet();
        }

        player0.cleanupAll();
        player1.cleanupAll();

        // make a summary
        int score0 = 0;
        int score1 = 0;
        int win0 = 0;
        int win1 = 0;
        for(int s = 0; s< MaxSet; s++) {
            score0 = score0 + score[s][0];
            score1 = score1 + score[s][1];
            if(score[s][0] > score[s][1]) {
                win0++;
            } else if(score[s][1] > score[s][0]) {
                win1++;
            }
        }
        if(logLevel>1) System.out.printf("Total score:  0= %5d (win= %3d)  1= %5d (win= %3d)\n", score0, win0, score1, win1);

        lastScore0 = score0;
        lastScore1 = score1;
        lastNumWin0 = win0;
        lastNumWin1 = win1;
        if(win0 > win1) {
            lastWin0 = true;
            lastWin1 = false;
            return 0;
        } else if(win0 < win1) {
            lastWin0 = false;
            lastWin1 = true;
            return 1;
        } else if(score0 > score1) {
            lastWin0 = true;
            lastWin1 = false;
            return 0;
        } else if(score0 < score1) {
            lastWin0 = false;
            lastWin1 = true;
            return 1;
        } else {
            lastWin0 = false;
            lastWin1 = false;
            return -1;
        }
    }

    /**
     * 1セット分の対戦を行う
     */
    public static void oneSet() {
        player0.initialize(currentSet);
        player1.initialize(currentSet);

        int score0 = 0;
        int score1 = 0;

        for(currentTurn = 0; currentTurn < MaxTurn; currentTurn++) {
            // decide cards
            int card0 = player0.decide(currentSet, currentTurn, 0);
            int card1 = player1.decide(currentSet, currentTurn, 1);
            selectedCard[currentSet][currentTurn][0] = card0;
            selectedCard[currentSet][currentTurn][1] = card1;

            // check faul
            if(isValid(0, card0)) {
                if(isValid(1, card1)) {
                    // no faul
                    int sA = score(card0, card1);
                    int sB = score(card1, card0);
                    score0 = score0 + sA;
                    score1 = score1 + sB;
                    if(logLevel>3) System.out.printf("Set %4d    Turn %3d    card0 %2d ( %2d )  card1 %2d ( %2d )\n",
                            currentSet, currentTurn, card0, sA, card1, sB);
                } else {
                    // player1のみ反則負け
                    if(logLevel>3) System.out.printf("Set %4d    Turn %3d    card0 %2d ( -- )  card1 %2d ( -- ) : Faul B\n",
                            currentSet, currentTurn, card0, card1);
                    score0 = 14 * MaxTurn;
                    score1 = 0;
                    break;
                }
            } else {
                if(isValid(1, card1)) {
                    // player0のみ反則負け
                    if(logLevel>3) System.out.printf("Set %4d    Turn %3d    card0 %2d ( -- )  card1 %2d ( -- ) : Faul A\n",
                            currentSet, currentTurn, card0, card1);
                    score0 = 0;
                    score1 = 14*MaxTurn;
                    break;
                } else {
                    // 両方反則負け
                    if(logLevel>3) System.out.printf("Set %4d    Turn %3d    card0 %2d ( -- )  card1 %2d ( -- ) : Faul A&B\n",
                            currentSet, currentTurn, card0, card1);
                    score0 = 0;
                    score1 = 0;
                    break;
                }
            }

        }

        score[currentSet][0] = score0;
        score[currentSet][1] = score1;
        if(logLevel>2) System.out.printf("--- Score0: %3d   Score1: %3d\n", score0, score1);

        player0.cleanup(currentSet);
        player1.cleanup(currentSet);
    }

    /**
     * 今の時点で，正しい手かどうかを調べる(値の範囲・重複をチェックする)
     * @param pn プレイヤー番号(0 or 1)
     * @param c 手(1-13)
     * @return
     */
    public static boolean isValid(int pn, int c) {
        if(pn!=0 && pn!=1) {
            return false;
        } else if(c<1 || c>13) {
            return false;
        } else {
            for(int i=0; i<currentTurn; i++) {
                if(selectedCard[currentSet][i][pn] == c) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 2枚のカードの勝敗を調べる
     * @param a プレイヤーAの手
     * @param b プレイヤーBの手
     * @return プレイヤーAの得点
     */
    public static int score(int a, int b) {
        if(a==13 && b==1) {
            // lose
            return 0;
        } else if(a==1 && b==13) {
            // win
            return a+b;
        } else if(a>b) {
            // win
            return a+b;
        } else {
            // lose or draw
            return 0;
        }
    }

    /**
     * 過去の手を返す
     * @param s セット番号
     * @param t ターン番号
     * @param pn プレイヤー番号(0:プレイヤー0  1:プレイヤー1)
     * @return 手(1-13) 不正な引数に対しては -1 を返す．
     */
    public static int getCard(int s, int t, int pn) {
        if(s<0 || s>= MaxSet || t<0 || t>=MaxTurn || pn<0 || pn>1) {
            return -1;
        } else if(s>=0 && s< currentSet) {
            // 過去の対戦
            return selectedCard[s][t][pn];
        } else if(s== currentSet && t<currentTurn) {
            // 今の対戦
            return selectedCard[s][t][pn];
        } else {
            return -1;
        }
    }

    /**
     * 過去の得点を返す
     * @param s セット番号
     * @param pn プレイヤー番号(0:プレイヤー0  1:プレイヤー1)
     * @return 当該セットでの総得点　不正な引数に対しては -1 を返す．
     */
    public static int getScore(int s, int pn) {
        if(s<0 || s>= currentSet || pn<0 || pn>1) {
            return -1;
        } else {
            return score[s][pn];
        }
    }

    /**
     * プレイヤー番号を返す
     * @param po プレイヤーのオブジェクト
     * @return プレイヤー番号(0:プレイヤー0  1:プレイヤー1  -1:不正な引数)
     */
    public static int getPlayerNumber(Player po) {
        if(po == player0) {
            return 0;
        } else if(po == player1) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 現在のセット番号を返す
     * @return 現在のセット番号(0〜)
     */
    public static int getCurrentSet() {
        return currentSet;
    }

    /**
     * 現在のターン番号を返す
     * @return 現在のターン番号(0〜)
     */
    public static int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * 最大セット番号を返す
     * @return 最大セット番号(セット番号は0以上，これ未満)
     */
    public static int getMaxSet() {
        return MaxSet;
    }

    /**
     * 最大ターン番号を返す
     * @return 最大ターン番号(ターン番号は0以上，これ未満)
     */
    public static int getMaxTurn() {
        return MaxTurn;
    }

    /**
     * 最後のセットのプレイヤー0の総得点
     * @return 最後のセットのプレイヤー0の総得点
     */
    public static int getLastScore0() {
        return lastScore0;
    }

    /**
     * 最後のセットのプレイヤー1の総得点
     * @return 最後のセットのプレイヤー1の総得点
     */
    public static int getLastScore1() {
        return lastScore1;
    }

    /**
     * 最後のセットでプレイヤー0が勝ったか
     * @return 最後のセットでプレイヤー0が勝ったか (true:勝った  false:負け or 引き分け)
     */
    public static boolean isLastWin0() {
        return lastWin0;
    }

    /**
     * 最後のセットでプレイヤー1が勝ったか
     * @return 最後のセットでプレイヤー1が勝ったか (true:勝った  false:負け or 引き分け)
     */
    public static boolean isLastWin1() {
        return lastWin1;
    }

    /**
     * 最後のセットのプレイヤー0の勝ち数
     * @return 最後のセットのプレイヤー0の勝ち数
     */
    public static int getLastNumWin0() {
        return lastNumWin0;
    }

    /**
     * 最後のセットのプレイヤー1の勝ち数
     * @return 最後のセットのプレイヤー1の勝ち数
     */
    public static int getLastNumWin1() {
        return lastNumWin1;
    }

    /**
     * ログレベルの設定
     * @param logLevel ログレベル (1:サイレント  2:最終結果のみ  3:セットごとの結果  4:ターンごとの結果)
     */
    public static void setLogLevel(int logLevel) {
        PlayGame.logLevel = logLevel;
    }

    /**
     * 最大セット数の指定
     * @param maxSet 最大セット数
     */
    public static void setMaxSet(int maxSet) {
        MaxSet = maxSet;
    }
}
