abstract class Player {
    /**
     * 対戦前の初期化(勝負全体の前に呼び出される)
     */
    public void initializeAll() {
    }

    /**
     * 各セット前の初期化
     * @param s 現在のセット番号
     */
    public void initialize(int s) {
    }

    /**
     * 各セット後の後始末
     * @param s 現在のセット番号
     */
    public void cleanup(int s) {
    }

    /**
     * 全勝負終了後の後始末
     */
    public void cleanupAll() {
    }

    /**
     * 次の1手を決める
     * @param s 現在のセット番号(0-)
     * @param t 現在のターン番号(0-12)
     * @param pn 現在のプレイヤー番号(0,1)
     * @return 次の1手(1-13) 現在のセット内で重複が無いように
     */
    public int decide(int s, int t, int pn) {
        // 第sセット，ターンtのプレイヤーpnの手(1-13)
        return 0;
    }
}
