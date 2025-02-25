package p5_QuintupletsAlternative;

import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import p3_QuintupletsCommon.*;

public class QASynchronizer implements QuintupletSynchronizer {
    private final Phaser phaser;        // 用于屏障同步
    private final Semaphore semaphore;  // 限制tickadiddle最多3人使用
    private int playedCount;            // 计数器，记录玩完的人数

    public QASynchronizer() {
        phaser = new Phaser(5);         // 注册5个参与者（五胞胎）
        semaphore = new Semaphore(3);   // 信号量，初始许可3，控制tickadiddle使用
        playedCount = 0;                // 初始化计数器
    }

    public void goPlayground(Quintuplet q) {
        QAnalyser.injectTrace(q + " is in the playground ready to play when all together");
        phaser.arriveAndAwaitAdvance(); // 到达并等待所有5人到齐，推进阶段
    }

    public void startPlaying(Quintuplet q) {
        try {
            semaphore.acquire(); // 获取许可，最多3人玩
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            QAnalyser.injectTrace("Interrupted while starting to play: " + q);
            return; // 如果中断，提前返回
        }
        QAnalyser.injectTrace(q + " is having fun with the tickadiddle");
    }

   /* public void endPlaying(Quintuplet q) {
        QAnalyser.injectTrace(q + " is about to leave the tickadiddle");
        int countBefore = playedCount;    // 记录递增前的值
        playedCount++;                  // 玩完人数加1
        if (countBefore == 5) {
        	phaser.arriveAndAwaitAdvance();  // 确保所有孩子都完成后推进         // 当5人都玩完时推进阶段
        }
        semaphore.release();            // 释放许可
    } */
    public void endPlaying(Quintuplet q) {
        QAnalyser.injectTrace(q + " is about to leave the tickadiddle");
        //System.out.println(q + " is leaving the tickadiddle. Played count before: " + playedCount);
        int countBefore = playedCount;    // 记录递增前的值
        playedCount++;                  // 玩完人数加1
        System.out.println("Played count after increment: " + playedCount);

        if (countBefore == 4) {  // when the last player leaves
            System.out.println("All 5 are done playing. Advancing the Phaser.");
            phaser.arriveAndAwaitAdvance();  // 当5人都玩完时推进阶段
        }
        semaphore.release();            // 释放许可
        //System.out.println(q + " released a spot for playing.");
    }

    public void goHomeForASnack(Quintuplet q) {
        int currentPhase = phaser.getPhase(); // 记录当前阶段
        System.out.println(q + " is checking if all players are done. Current phase: " + currentPhase);
        
        System.out.println("Current played count: " + playedCount);
        
        if (playedCount != 5) {               // 等待所有5人玩完
            System.out.println(q + " is waiting for all players to finish playing.");
            phaser.awaitAdvance(currentPhase); // 等待阶段推进到下一阶段（无InterruptedException）
        }
		//if (playedCount == 5) { // 所有人都玩完后重置计数器
			//phaser.arriveAndAwaitAdvance();  // 当5人都玩完时推进阶段
		//}
       
        QAnalyser.injectTrace(q + " is having a snack at home");
        System.out.println(q + " is having a snack at home.");
        playedCount--;                        // 吃完点心后计数器减1
        System.out.println("Played count after decrement: " + playedCount);

        if (playedCount == 0) {               // 所有人都吃完点心后重置
            System.out.println("All are done with snacks. Advancing the Phaser.");
            phaser.arriveAndAwaitAdvance(); // 推进阶段并等待，为下一轮准备
        }
    }
}
