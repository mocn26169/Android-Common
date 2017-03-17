//package com.bourne.android_common.ServiceDemo;
//
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//
///**
// * Handy class for starting a new thread that has a looper. The looper can then be
// * used to create handler classes. Note that start() must still be called.
// */
//public class MyHandlerThread extends Thread {
//    /**
//     * 线程优先级
//     */
//    int mPriority;
//
//    int mTid = -1;
//
//    /**
//     * 当前线程持有的Looper对象
//     */
//    Looper mLooper;
//
//    public MyHandlerThread(String name) {
//        super(name);
//        mPriority = Process.THREAD_PRIORITY_DEFAULT;
//    }
//
//    /**
//     * Constructs a HandlerThread.
//     * @param name
//     * @param priority The priority to run the thread at. The value supplied must be from
//     * {@link android.os.Process} and not from java.lang.Thread.
//     */
//    public MyHandlerThread(String name, int priority) {
//        super(name);
//        mPriority = priority;
//    }
//
//    /**
//     *必要时可以自己去重写
//     */
//    protected void onLooperPrepared() {
//    }
//
//    @Override
//    public void run() {
//        mTid = Process.myTid();
//        Looper.prepare();
//        synchronized (this) {
//            //Looper对象将被创建
//            mLooper = Looper.myLooper();
//            //唤醒等待线程
//            notifyAll();
//        }
//        //设置进程优先级
//        Process.setThreadPriority(mPriority);
//        onLooperPrepared();
//        //开启looper循环语句
//        Looper.loop();
//        mTid = -1;
//    }
//
//    /**
//     * This method returns the Looper associated with this thread. If this thread not been started
//     * or for any reason is isAlive() returns false, this method will return null. If this thread
//     * has been started, this method will block until the looper has been initialized.
//     * @return The looper.
//     */
//    public Looper getLooper() {
//        //  判断当前线程是否启动了
//        if (!isAlive()) {
//            return null;
//        }
//
//
//        synchronized (this) {
//            while (isAlive() && mLooper == null) {
//                try {
//                    //等待唤醒
//                    wait();
//                } catch (InterruptedException e) {
//                }
//            }
//        }
//        return mLooper;
//    }
//
//    /**
//     * Quits the handler thread's looper.
//     * <p>
//     * Causes the handler thread's looper to terminate without processing any
//     * more messages in the message queue.
//     * </p><p>
//     * Any attempt to post messages to the queue after the looper is asked to quit will fail.
//     * For example, the {@link Handler#sendMessage(Message)} method will return false.
//     * </p><p class="note">
//     * Using this method may be unsafe because some messages may not be delivered
//     * before the looper terminates.  Consider using {@link #quitSafely} instead to ensure
//     * that all pending work is completed in an orderly manner.
//     * </p>
//     *
//     * @return True if the looper looper has been asked to quit or false if the
//     * thread had not yet started running.
//     *
//     * @see #quitSafely
//     */
//    public boolean quit() {
//        Looper looper = getLooper();
//        if (looper != null) {
//            looper.quit();
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Quits the handler thread's looper safely.
//     * <p>
//     * Causes the handler thread's looper to terminate as soon as all remaining messages
//     * in the message queue that are already due to be delivered have been handled.
//     * Pending delayed messages with due times in the future will not be delivered.
//     * </p><p>
//     * Any attempt to post messages to the queue after the looper is asked to quit will fail.
//     * For example, the {@link Handler#sendMessage(Message)} method will return false.
//     * </p><p>
//     * If the thread has not been started or has finished (that is if
//     * {@link #getLooper} returns null), then false is returned.
//     * Otherwise the looper is asked to quit and true is returned.
//     * </p>
//     *
//     * @return True if the looper looper has been asked to quit or false if the
//     * thread had not yet started running.
//     */
//    public boolean quitSafely() {
//        Looper looper = getLooper();
//        if (looper != null) {
//            looper.quitSafely();
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Returns the identifier of this thread. See Process.myTid().
//     */
//    public int getThreadId() {
//        return mTid;
//    }
//}
//
