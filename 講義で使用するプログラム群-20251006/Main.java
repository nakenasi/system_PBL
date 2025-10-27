/**
 * 複数の対戦(match)を実行し、戦略の勝率を分析するためのクラス
 */
public class Main {
    public static void main(String[] args) {
        
        // --- 設定 ---
        // 分析のために実行する対戦回数 (1回 = 1000セット)
        int numMatches = 100; 
        // --- 設定おわり ---

        
        long totalSetWinsP0 = 0; 
        long totalSetWinsP1 = 0; 
        
        int matchWinsP0 = 0;     
        int matchWinsP1 = 0;    
        int matchDraws = 0;      
        PlayGame.setLogLevel(1); 

        // PlayGame.getMaxSet() は 1000 が返ると想定
        long totalSetsToPlay = (long)numMatches * PlayGame.getMaxSet();

        System.out.println("--- 戦略分析開始 ---");
        System.out.println("Player 0: Player_Hybrid (N手読み戦略)");
        System.out.println("Player 1: PlayerRandom (完全ランダム戦略)");
        System.out.println("対戦回数: " + numMatches + "回 (合計 " + totalSetsToPlay + " セット)");
        System.out.println("---------------------");

        // numMatches の回数だけ対戦(match)を繰り返す
        for (int i = 0; i < numMatches; i++) {
            Player p0 = new Player_Hybrid(); 
            Player p1 = new PlayerRandom();             
            int matchWinner = PlayGame.match(p0, p1);
            totalSetWinsP0 += PlayGame.getLastNumWin0();
            totalSetWinsP1 += PlayGame.getLastNumWin1();
            if (matchWinner == 0) {
                matchWinsP0++;
            } else if (matchWinner == 1) {
                matchWinsP1++;
            } else {
                matchDraws++;
            }
            
            // 10% ごとに進捗を表示
            if ((i + 1) % (numMatches / 10) == 0 && (i + 1) != numMatches) {
                System.out.println("... " + (i + 1) + "回 実行完了 (" + ((i+1.0)/numMatches * 100.0) + "%)");
            }
        }

        System.out.println("... " + numMatches + "回 実行完了 (100.0%)");
        System.out.println("\n--- 分析結果 ---");

        System.out.println("【対戦(Match)勝敗】 (" + numMatches + "回実行)");
        System.out.printf("Player 0 (Hybrid) 勝利: %d 回 (%.1f%%)\n", 
            matchWinsP0, (double)matchWinsP0 / numMatches * 100.0);
        System.out.printf("Player 1 (Random) 勝利: %d 回 (%.1f%%)\n", 
            matchWinsP1, (double)matchWinsP1 / numMatches * 100.0);
        System.out.printf("引き分け:               %d 回 (%.1f%%)\n", 
            matchDraws, (double)matchDraws / numMatches * 100.0);
        
        long totalSetsWon = totalSetWinsP0 + totalSetWinsP1;
        
        System.out.println("\n【累計セット勝利数】 (全 " + totalSetsToPlay + " セット中)");
        System.out.printf("Player 0 (Hybrid) 勝利セット: %d (勝率 %.2f%%)\n", 
            totalSetWinsP0, (double)totalSetWinsP0 / totalSetsToPlay * 100.0);
        System.out.printf("Player 1 (Random) 勝利セット: %d (勝率 %.2f%%)\n", 
            totalSetWinsP1, (double)totalSetWinsP1 / totalSetsToPlay * 100.0);
        System.out.printf("引き分けセット:               %d (発生率 %.2f%%)\n",
            (totalSetsToPlay - totalSetsWon), (double)(totalSetsToPlay - totalSetsWon) / totalSetsToPlay * 100.0);


        double avgWinsP0 = (double)totalSetWinsP0 / numMatches;
        double avgWinsP1 = (double)totalSetWinsP1 / numMatches;

        System.out.println("\n【1対戦(1000セット)あたりの平均勝利セット数】");
        System.out.printf("Player 0 (Hybrid): %.2f セット\n", avgWinsP0);
        System.out.printf("Player 1 (Random): %.2f セット\n", avgWinsP1);
    }
}