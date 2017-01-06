package com.kritter.utils.nosql.common;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SignalingNotificationObject<T> {
    private T object;
    private boolean done;

    private final Lock lock;
    private final Condition doneCondition;

    public SignalingNotificationObject() {
        this.done = false;
        this.lock = new ReentrantLock();
        this.doneCondition = lock.newCondition();
    }

    public T get() {
        lock.lock();
        try {
            if(!done) {
                doneCondition.await();
            }
            return object;
        } catch (InterruptedException ie) {
            return null;
        }finally {
            lock.unlock();
        }
    }

    public void put(T object) {
        lock.lock();
        try {
            this.object = object;
            done = true;
            doneCondition.signal();
        } finally {
            lock.unlock();
        }
    }
}
