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
            while (anteroomCount > 0 && currentGender != p.getGender()) { // 确保单性别
                anteroomCondition.await();
            }
            waitingAreaCount--; // 从等待区离开
            if (anteroomCount == 0) {
                currentGender = p.getGender(); // 仅在前厅空时设置性别
            }
            anteroomCount++; // 增加前厅人数
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
            while (freeStalls == 0) { // 等待空隔间
                stallCondition.await();
            }
            int stallNumber = -1;
            for (int i = 0; i < stalls.length; i++) {
                if (stalls[i]) {
                    stallNumber = i;
                    break;
                }
            }
            stalls[stallNumber] = false; // 占用隔间
            freeStalls--;
            p.assignStall(stallNumber);
            anteroomCount--; // 离开前厅
            if (anteroomCount < 0) { // 防止计数器为负
                System.err.println("ERROR: anteroomCount became negative: " + anteroomCount + " for thread " + p);
                anteroomCount = 0; // 修正计数器
            }
            if (anteroomCount == 0) {
                currentGender = null; // 前厅清空
                anteroomCondition.signalAll(); // 通知等待进入前厅的人
            }
            injectTrace("\t\t " + p + " HAS TAKEN Stall [" + stallNumber + "]");
            stallCondition.signal(); // 通知等待隔间的人
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
            stalls[stallNum] = true; // 释放隔间
            freeStalls++;
            injectTrace("<** LEAVING stall [" + stallNum + "] " + p);
            stallCondition.signal(); // 通知等待隔间的人
        } finally {
            lock.unlock();
        }
    }
}
