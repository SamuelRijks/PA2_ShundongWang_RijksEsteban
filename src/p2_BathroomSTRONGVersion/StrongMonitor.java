package p2_BathroomSTRONGVersion;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import p0_BathroomCommon.*;

public class StrongMonitor implements BathroomMonitor {

    Method writeMethod; // do not remove

    // Lock y condiciones
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condMen   = lock.newCondition();
    private final Condition condWomen = lock.newCondition();
    private final Condition condStall = lock.newCondition();
    
    // Variables para ordenar el acceso al anteroom por género:
    // Para hombres:
    private int menTicket = 0;   // emite tickets
    private int nextMen   = 0;   // siguiente ticket a atender
    // Para mujeres:
    private int womenTicket = 0;
    private int nextWomen   = 0;
    
    // Para ordenar la asignación de stall (dentro del grupo que ya accedió al anteroom)
    private int stallNext = 0;
    
    // Estado del anteroom: sólo personas de un mismo género pueden estar en él.
    // Si currentGender es null, el anteroom está vacío; de lo contrario, contendrá Gender.MAN o Gender.WOMAN.
    private Gender currentGender = null;
    private int countInAnteroom = 0; // número de personas en el anteroom
    
    // Representación de los 4 stalls: true indica que el stall está libre.
    private final boolean[] stallFree = new boolean[4];
    
    // Para almacenar el ticket emitido a cada persona al acceder al anteroom.
    // Se asume que Person.getId() devuelve un long único, en el rango [0, MAX_PERSONS).
    private static final int MAX_PERSONS = 100;
    private final int[] anteroomTicket = new int[MAX_PERSONS];
    
    public StrongMonitor(Analyser an) {
        try {
            writeMethod = an.getClass().getMethod("writeString", String.class);
        } catch(Exception ex) {
            System.exit(1);
        }
        // Inicializamos los stalls como libres.
        for (int i = 0; i < stallFree.length; i++) {
            stallFree[i] = true;
        }
    }
    
    private void injectTrace(String s) {
        try { 
            writeMethod.invoke(null, s);
        } catch(Exception ex) { 
            System.exit(1);
        }
    }
    
    // Al entrar en el baño se inyecta la traza.
    public void enter(Person p) {
        lock.lock();
        try {
            injectTrace("--> ENTERING bathroom " + p);
            // No se necesita más en esta fase.
        } finally {
            lock.unlock();
        }
    }
    
    // En accessAnteroom se garantiza que la persona, de acuerdo a su género,
    // acceda en orden de llegada.
    public void accessAnteroom(Person p) {
        lock.lock();
        try {
            // getGender devuelve un enum Gender (MAN o WOMAN)
            Gender gender = p.getGender();
            // Si el anteroom ya está ocupado por el otro género, la persona espera.
            while (currentGender != null && currentGender != gender) {
                if(gender == Gender.MAN)
                    condMen.awaitUninterruptibly();
                else
                    condWomen.awaitUninterruptibly();
            }
            // Si el anteroom está vacío, la persona fija el género y reinicia la ordenación.
            if (currentGender == null) {
                currentGender = gender;
                if(gender == Gender.MAN) {
                    menTicket = 0;
                    nextMen = 0;
                } else {
                    womenTicket = 0;
                    nextWomen = 0;
                }
                stallNext = 0; // reiniciamos también la asignación de stall
            }
            // Se emite un ticket según el género.
            int myTicket;
            if (gender == Gender.MAN) {
                myTicket = menTicket;
                menTicket++;
                // Espera hasta que sea su turno (FIFO entre hombres).
                while (myTicket != nextMen) {
                    condMen.awaitUninterruptibly();
                }
                nextMen++;
            } else { // género WOMAN
                myTicket = womenTicket;
                womenTicket++;
                while (myTicket != nextWomen) {
                    condWomen.awaitUninterruptibly();
                }
                nextWomen++;
            }
            // Se guarda el ticket para la asignación de stall.
            anteroomTicket[(int)p.getId()] = myTicket;
            countInAnteroom++;
            injectTrace("\t--> ACCESSING the anteroom " + p);
            // Se señala al siguiente de su mismo género (si lo hubiera).
            if (gender == Gender.MAN)
                condMen.signal();
            else
                condWomen.signal();
        } finally {
            lock.unlock();
        }
    }
    
    // getFreeStall garantiza que, entre las personas en el anteroom, se asigne el stall
    // respetando el orden de llegada (según el ticket emitido al acceder al anteroom).
    public void getFreeStall(Person p) {
        int stallNum = -1;
        lock.lock();
        try {
            int myTicket = anteroomTicket[(int)p.getId()];
            // Espera a que sea su turno para asignar stall.
            while (myTicket != stallNext) {
                condStall.awaitUninterruptibly();
            }
            // Espera a que exista un stall libre.
            while (!freeStallAvailable()) {
                condStall.awaitUninterruptibly();
            }
            // Se asigna el primer stall libre.
            for (int i = 0; i < stallFree.length; i++) {
                if (stallFree[i]) {
                    stallNum = i;
                    stallFree[i] = false;
                    break;
                }
            }
            p.assignStall(stallNum);
            injectTrace("\t\t " + p + " HAS TAKEN Stall [" + stallNum + "]");
            // Se actualiza el contador de orden para la asignación de stall.
            stallNext++;
            condStall.signal();
            // Al tomar un stall, la persona sale del anteroom.
            countInAnteroom--;
            // Si el anteroom queda vacío, se libera y se notifica a ambos géneros.
            if (countInAnteroom == 0) {
                currentGender = null;
                condMen.signal();
                condWomen.signal();
            }
        } finally {
            lock.unlock();
        }
    }
    
    // En exit se libera el stall ocupado y se inyecta la traza correspondiente.
    public void exit(Person p) {
        lock.lock();
        try {
            int stallNum = p.getAssignedStall();
            stallFree[stallNum] = true;
            injectTrace("<** LEAVING stall [" + stallNum + "] " + p);
            // Se señala a posibles bloqueados esperando un stall libre.
            condStall.signal();
        } finally {
            lock.unlock();
        }
    }
    
    // Método auxiliar: comprueba si existe algún stall libre.
    private boolean freeStallAvailable() {
        for (boolean free : stallFree) {
            if (free) return true;
        }
        return false;
    }
}
