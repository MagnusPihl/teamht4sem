/*
 * ThreadPool.java
 *
 * Created on 5. marts 2007, 20:57
 *
 * Based on examples in "Devoloping Games in Java" [Brackeen]
 * by Brackeen, David
 * www.brackeen.com
 * Listing 1.1 ThreadPool.java, page 16-19
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 5. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package obsolete;

import java.util.*;

/**
 *
 * @author LMK
 */
public class ThreadPool extends ThreadGroup {
    
    private boolean isAlive;
    private LinkedList taskQueue;
    private int threadID;
    private static int threadPoolID;
    
    /** 
     * Creates a new pool of threads.
     * 
     * @param number of threads in pool.
     */
    public ThreadPool(int threadCount) {
        super("ThreadPool-" + (threadPoolID++));
        super.setDaemon(true);
        this.isAlive = true;
        taskQueue = new LinkedList();
        for (int i = 0; i < threadCount; i++) {
            new PooledThread().start();
        }
    }
    
    /**
     * Run task when a thread becomes available.
     *
     * @param runnable task.
     */
    public synchronized void runTask(Runnable task) {
        if (!this.isAlive) {
            throw new IllegalStateException("You cannot start a new task on " +
                    "a ThreadPool that has closed.");
        }
        if (task != null) {
            this.taskQueue.add(task);
            notify();
        }
    }
    
    /**
     * Get next task.
     *
     * @return next taskt to run.
     */
    protected synchronized Runnable getTask() throws InterruptedException {
        while (this.taskQueue.size() == 0) {
            if (!isAlive) {
                return null;
            }
            wait();
        }
        return (Runnable)taskQueue.removeFirst();
    }
    
    /**
     * Forceably close ThreadPool, not allowing thread to finish.
     */
    public synchronized void close() {
        if (this.isAlive) {
            this.isAlive = false;
            this.taskQueue.clear();
            interrupt();
        }
    }
    /**
     * Wait for current tasks to finish, then close pool.
     */
    public void join() {
        synchronized (this) {
            this.isAlive = false;
            notifyAll();
        }
     
        Thread[] activeThreads = new Thread[super.activeCount()];
        int threadCount = super.enumerate(activeThreads);
        
        for (int i = 0 ; i < threadCount; i++) {
            try {
                activeThreads[i].join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        
        }
    }
    
    private class PooledThread extends Thread {
    
        public PooledThread() {
            super(ThreadPool.this, "PooleadThread-" + (threadID++));             
        }
        
        public void run() {
            while(!isInterrupted()) {
                Runnable task  = null;
                try {
                    task = getTask();
                } catch (InterruptedException ie) {}
                
                if (task == null) {
                    return;
                }
                
                try {
                    task.run();
                } catch (Throwable t) {
                    uncaughtException(this, t);
                }
            }
        }
    }
}
