package p4_QuintupletsSemaphores;

import java.util.concurrent.Semaphore;
import p3_QuintupletsCommon.*;

public class QSSynchronizer implements QuintupletSynchronizer {
    private final Semaphore mutex = new Semaphore(1); // Mutex para acceso al contador
    private final Semaphore multiplex = new Semaphore(3); // Permite hasta 3 jugando
    private final Semaphore turnstile1 = new Semaphore(0); // Turnstile para sincronización inicial
    private final Semaphore turnstile2 = new Semaphore(0); // Turnstile para sincronización final
    private int count = 0;

    public void goPlayground(Quintuplet q) {
        QAnalyser.injectTrace(q + " is in the playground ready to play when all together");
        
        try {
            mutex.acquire();
            count++;
            if (count == 5) {
                turnstile1.release(5); // Libera a todos cuando estén en el playground
            }
            mutex.release();
            turnstile1.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startPlaying(Quintuplet q) {
        try {
            multiplex.acquire(); // Hasta 3 pueden jugar simultáneamente
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        QAnalyser.injectTrace(q + " is having fun with the tickadiddle");
    }

    public void endPlaying(Quintuplet q) {
        QAnalyser.injectTrace(q + " is about to leave the tickadiddle");
        
        multiplex.release(); // Libera espacio para que otro pueda jugar
        
        try {
            mutex.acquire();
            count--;
            if (count == 0) {
                turnstile2.release(5); // Libera a todos para ir a casa cuando el último sale
            }
            mutex.release();
            turnstile2.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void goHomeForASnack(Quintuplet q) {
        QAnalyser.injectTrace(q + " is having a snack at home");
    }
}
