package p5_QuintupletsAlternative;

import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import p3_QuintupletsCommon.*;

public class QASynchronizer implements QuintupletSynchronizer {
    // Phaser para sincronizar a los 5 quintillizos en cada fase de la rutina.
    private final Phaser phaser;
    // Semaphore con naturaleza de multiplex: limita a 3 el uso del tickadiddle.
    private final Semaphore semaphore;

    public QASynchronizer() {
        // Se registran 5 participantes en el Phaser.
        phaser = new Phaser(5);
        // Se inicializa el semaphore con 3 permisos.
        semaphore = new Semaphore(3);
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
            // Adquiere un permiso; máximo 3 quintillizos podrán jugar simultáneamente.
            semaphore.acquire();
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
			semaphore.release(); // 释放许可

			phaser.arriveAndAwaitAdvance(); // 阶段1：五人玩完，推进

		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

    public void goHomeForASnack(Quintuplet q) {
    	try {
            // Fase 2: Espera a que todos estén listos para ir a la merienda.
            phaser.arriveAndAwaitAdvance();
            QAnalyser.injectTrace(q + " is having a snack at home");
            // Fase 3: Espera a que todos hayan terminado la merienda.
            phaser.arriveAndAwaitAdvance();
    	} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}
    	

    
}
