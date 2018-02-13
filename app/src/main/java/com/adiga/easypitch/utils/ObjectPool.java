package com.adiga.easypitch.utils;

import android.support.v4.util.SparseArrayCompat;


/**
 * Created by dbajj on 2018-02-12.
 */

public abstract class ObjectPool<T> {

    private SparseArrayCompat<T> free;
    private SparseArrayCompat<T> lent;

    private int maxCapacity;

    public ObjectPool(int initialCapacity, int maxCapacity) {
        initialize(initialCapacity);
        this.maxCapacity = maxCapacity;
    }

    public ObjectPool(int maxCapacity) {
        this(maxCapacity/2,maxCapacity);
    }

    public T acquire() {
        T t = null;

        synchronized (free) {
            int freeSize = free.size();
            for(int i = 0; i < freeSize; i++) {
                int key = free.keyAt(i);
                t = free.get(key);

                if(t != null) {
                    this.lent.put(key,t);
                    this.free.remove(key);
                    return t;
                }
            }

            if(t == null && lent.size()+free.size() < maxCapacity) {
                t = create();
                lent.put(lent.size()+freeSize,t);
            }
        }

        return t;
    }

    public void release(T t) {
        if (t == null) {
            return;
        }

        int index = lent.indexOfValue(t);
        restore(t);
        this.free.put(lent.keyAt(index),t);
        this.lent.removeAt(index);
    }

    protected abstract T create();

    protected void restore(T t) {

    }

    private void initialize(final int initialCapacity) {
        lent = new SparseArrayCompat<>();
        free = new SparseArrayCompat<>();

        for(int i = 0; i < initialCapacity; i++) {
            free.put(i, create());
        }
    }

    public int spaceRemaining() {
        return free.size();
    }

    public int spaceUsed() {
        return lent.size();
    }

}
