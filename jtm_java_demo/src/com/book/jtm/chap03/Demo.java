/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.book.jtm.chap03;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demo {

    public static void main(String[] args) {
        MyArrayBlockingQueue<Integer> aQueue = new MyArrayBlockingQueue<Integer>();
        aQueue.put(3);
        aQueue.put(24);
        for (int i = 0; i < 5; i++) {
            System.out.println(aQueue.take());
        }
    }

    class synchronizedDemo {
        public synchronized void syncMethd() {

        }

        public void syncThis() {
            synchronized (this) {
                // 代码
            }
        }

        public void syncClassMethod() {
            synchronized (synchronizedDemo.class) {
                // 做xxx
            }
        }
    }

    public synchronized static void syncStaticMethod() {
        //
    }

    static class MyArrayBlockingQueue<T> {
        // 数据数组
        private final T[] items;
        // 锁
        private final Lock lock = new ReentrantLock();
        // 满
        private Condition notFull = lock.newCondition();
        // 空
        private Condition notEmpty = lock.newCondition();
        // 头部索引
        private int head;
        // 尾部索引
        private int tail;
        // 数据的个数
        private int count;

        public MyArrayBlockingQueue(int maxSize) {
            items = (T[]) new Object[maxSize];
        }

        public MyArrayBlockingQueue() {
            this(10);
        }

        public void put(T t) {
            lock.lock();
            try {
                while (count == getCapacity()) {
                    System.out.println("数据已满，等待");
                    notFull.await();
                }
                items[tail] = t;
                if (++tail == getCapacity()) {
                    tail = 0;
                }
                ++count;
                notEmpty.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public T take() {
            lock.lock();
            try {
                while (count == 0) {
                    System.out.println("还没有数据，请等待");
                    notEmpty.await();
                }
                T ret = items[head];
                items[head] = null;
                if (++head == getCapacity()) {
                    head = 0;
                }
                --count;
                notFull.signalAll();
                return ret;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            return null;
        }

        public int getCapacity() {
            return items.length;
        }

        public int size() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }

    }
}
