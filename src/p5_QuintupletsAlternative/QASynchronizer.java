package p5_QuintupletsAlternative;

import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import p3_QuintupletsCommon.*;

public class QASynchronizer implements QuintupletSynchronizer {
	private final Phaser phaser; // 用于屏障同步，替代三个信号量
	private final Semaphore semaphore; // 限制 tickadiddle 最多3人使用
	private int playedCount; // 唯一计数器，记录玩完人数

	public QASynchronizer() {
		phaser = new Phaser(5); // 注册5个参与者
		semaphore = new Semaphore(3); // 信号量，初始许可3
		playedCount = 0; // 初始化计数器
	}

	public void goPlayground(Quintuplet q) {
		QAnalyser.injectTrace(q + " is in the playground ready to play when all together");
		try {
			phaser.arriveAndAwaitAdvance(); // 阶段0：等待五人到达操场
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

	public void startPlaying(Quintuplet q) {
		try {
			semaphore.acquire(); // 获取许可，最多3人玩
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			QAnalyser.injectTrace("Interrupted while starting to play: " + q);
			return;
		}
		QAnalyser.injectTrace(q + " is having fun with the tickadiddle");
	}

	public void endPlaying(Quintuplet q) {
		QAnalyser.injectTrace(q + " is about to leave the tickadiddle");
		try {
			playedCount++; // 递增计数器，无需互斥保护（Phaser 同步足够）
			semaphore.release(); // 释放许可

			phaser.arriveAndAwaitAdvance(); // 阶段1：五人玩完，推进

		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

	public void goHomeForASnack(Quintuplet q) {
		try {
			phaser.arriveAndAwaitAdvance(); // 阶段2：五人到达
			QAnalyser.injectTrace(q + " is having a snack at home");
			playedCount--;

			phaser.arriveAndAwaitAdvance(); // 阶段3：五人完成吃点心
			if (playedCount == 0) {
				phaser.arriveAndAwaitAdvance(); // 阶段4：重置
			}
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}
}
