package p1_BathroomMILDVersion;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;
import p0_BathroomCommon.*;
import java.lang.reflect.*;

public class MildMonitor implements BathroomMonitor {

    private Method writeMethod; // do not remove
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition waitingAreaSpaceAvailable = lock.newCondition(); // 等待区容量条件
    private final Condition anteroomCondition = lock.newCondition(); // 前厅条件
    private final Condition stallCondition = lock.newCondition(); // 隔间条件
    private Gender currentGender = null; // 前厅当前性别
    private int anteroomCount = 0; // 前厅人数
    private int waitingAreaCount = 0; // 等待区人数（0-3）
    private int freeStalls = 4; // 可用隔间数
    private final boolean[] stalls = new boolean[4]; // true表示空闲

    public MildMonitor(Analyser an) {
        try {
            writeMethod = an.getClass().getMethod("writeString", String.class);
        } catch (Exception ex) {
            System.exit(1);
        }
        Arrays.fill(stalls, true); // 初始化隔间为全部空闲
    }

    private void injectTrace(String s) {
        try {
            writeMethod.invoke(null, s);
        } catch (Exception ex) {
            System.exit(1);
        }
    }

    public void enter(Person p) {
        lock.lock();
        try {
            while (waitingAreaCount >= 3) { // 限制等待区最多3人
                waitingAreaSpaceAvailable.await();
            }
            waitingAreaCount++;
            injectTrace("--> ENTERING bathroom " + p);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void accessAnteroom(Person p) {
        lock.lock();
        try {
            // 调试打印：进入前厅时的状态
            System.out.println("DEBUG: Thread " + p + " trying to access anteroom - currentGender: " + currentGender + 
                             ", anteroomCount: " + anteroomCount + ", threadGender: " + p.getGender());
            
            while (anteroomCount > 0 && currentGender != p.getGender()) { // 确保单性别
                // 调试打印：等待条件时的状态
                System.out.println("DEBUG: Thread " + p + " waiting - currentGender: " + currentGender + 
                                 ", anteroomCount: " + anteroomCount + ", threadGender: " + p.getGender());
                anteroomCondition.await();
            }
            waitingAreaCount--; // 从等待区离开
            if (anteroomCount == 0) {
                currentGender = p.getGender(); // 设置前厅性别
                // 调试打印：设置新性别时的状态
                System.out.println("DEBUG: Thread " + p + " setting new gender - currentGender: " + currentGender + 
                                 ", anteroomCount: " + anteroomCount);
            }
            anteroomCount++;
            // 调试打印：成功进入前厅后的状态
            System.out.println("DEBUG: Thread " + p + " entered anteroom - currentGender: " + currentGender + 
                             ", anteroomCount: " + anteroomCount + ", threadGender: " + p.getGender());
            
            injectTrace("\t--> ACCESSING the anteroom " + p);
            waitingAreaSpaceAvailable.signal(); // 通知等待进入等待区的人
            anteroomCondition.signalAll(); // 通知等待进入前厅的人
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void getFreeStall(Person p) {
        lock.lock();
        try {
            while (freeStalls == 0) {
                stallCondition.await();
            }
            int stallNumber = -1;
            for (int i = 0; i < stalls.length; i++) {
                if (stalls[i]) {
                    stallNumber = i;
                    break;
                }
            }
            stalls[stallNumber] = false;
            freeStalls--;
            p.assignStall(stallNumber);
            anteroomCount--;
            if (anteroomCount == 0) {
                currentGender = null; // 前厅清空
                anteroomCondition.signalAll();
            }
            injectTrace("\t\t " + p + " HAS TAKEN Stall [" + stallNumber + "]");
            stallCondition.signal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void exit(Person p) {
        lock.lock();
        try {
            int stallNum = p.getAssignedStall();
            stalls[stallNum] = true;
            freeStalls++;
            anteroomCount--; // 确保前厅人数减少
            if (anteroomCount == 0) {
                currentGender = null; // 仅在前厅完全清空时清空性别
                anteroomCondition.signalAll();
            }
            injectTrace("<** LEAVING stall [" + stallNum + "] " + p);
            stallCondition.signal();
        } finally {
            lock.unlock();
        }
    }
}
