package com.bourne.android_common.ServiceDemo;


/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.TimeUnit;package android.os;

import android.annotation.MainThread;
import android.annotation.WorkerThread;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>AsyncTask enables proper and easy use of the UI thread. This class allows you
 * to perform background operations and publish results on the UI thread without
 * having to manipulate threads and/or handlers.</p>
 * <p>
 * AsyncTask允许正确和容易地使用UI线程。 此类允许您在UI线程上执行后台操作和发布结果，而无需操作线程和/或处理程序。
 * <p>AsyncTask is designed to be a helper class around {@link Thread} and {@link Handler}
 * and does not constitute a generic threading framework. AsyncTasks should ideally be
 * used for short operations (a few seconds at the most.) If you need to keep threads
 * running for long periods of time, it is highly recommended you use the various APIs
 * provided by the <code>java.util.concurrent</code> package such as {@link Executor},
 * {@link ThreadPoolExecutor} and {@link FutureTask}.</p>
 * <p>
 * AsyncTask被设计为一个围绕{@link Thread}和{@link Handler}的帮助类，并不构成一个通用的线程框架。
 * AsyncTasks应该理想地用于短操作（最多几秒钟）。
 * 如果您需要保持线程长时间运行，强烈建议您使用java.util.concurrent包提供的各种API，例如 {@link Executor}，{@link ThreadPoolExecutor}和{@link FutureTask}。
 * <p>
 * <p>An asynchronous task is defined by a computation that runs on a background thread and
 * whose result is published on the UI thread. An asynchronous task is defined by 3 generic
 * types, called <code>Params</code>, <code>Progress</code> and <code>Result</code>,
 * and 4 steps, called <code>onPreExecute</code>, <code>doInBackground</code>,
 * <code>onProgressUpdate</code> and <code>onPostExecute</code>.</p>
 * 异步任务由在后台线程上运行并且其结果在UI线程上发布的计算定义。
 * 异步任务由3种通用类型定义，称为Params，Progress和Result，以及4个步骤，称为onPreExecute，doInBackground，onProgressUpdate和onPostExecute。
 * <p>
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * 开发人员指南
 * <p>For more information about using tasks and threads, read the
 * <a href="{@docRoot}guide/components/processes-and-threads.html">Processes and
 * Threads</a> developer guide.</p>
 * 有关使用任务和线程的详细信息，请阅读<a href="{@docRoot}guide/components/processes-and-threads.html">流程和主题</a>开发人员指南。
 * </div>
 * <p>
 * <h2>Usage</h2>
 * 用法
 * <p>AsyncTask must be subclassed to be used. The subclass will override at least
 * one method ({@link #doInBackground}), and most often will override a
 * second one ({@link #onPostExecute}.)</p>
 * <p>
 * AsyncTask必须被子类化才能使用。 子类将覆盖至少一个方法（{@link #doInBackground}），并且通常会覆盖第二个方法（{@link #onPostExecute}）。
 * <p>Here is an example of subclassing:</p>
 * 这里是一个子类化的例子
 * <pre class="prettyprint">
 * private class DownloadFilesTask extends AsyncTask&lt;URL, Integer, Long&gt; {
 * protected Long doInBackground(URL... urls) {
 * int count = urls.length;
 * long totalSize = 0;
 * for (int i = 0; i < count; i++) {
 * totalSize += Downloader.downloadFile(urls[i]);
 * publishProgress((int) ((i / (float) count) * 100));
 * // Escape early if cancel() is called
 * if (isCancelled()) break;
 * }
 * return totalSize;
 * }
 * <p>
 * protected void onProgressUpdate(Integer... progress) {
 * setProgressPercent(progress[0]);
 * }
 * <p>
 * protected void onPostExecute(Long result) {
 * showDialog("Downloaded " + result + " bytes");
 * }
 * }
 * </pre>
 * <p>
 * <p>Once created, a task is executed very simply:</p>
 * 一旦创建，任务被执行非常简单:
 * <pre class="prettyprint">
 * new DownloadFilesTask().execute(url1, url2, url3);
 * </pre>
 * <p>
 * <h2>AsyncTask's generic types</h2>
 * AsyncTask的泛型类型
 * <p>The three types used by an asynchronous task are the following:</p>
 * 异步任务使用的三种类型如下:
 * <ol>
 * <li><code>Params</code>, the type of the parameters sent to the task upon 在执行时发送到任务的参数的类型。
 * execution.</li>
 * <li><code>Progress</code>, the type of the progress units published during 在其期间发布的进度单位的类型
 * the background computation.</li>
 * <li><code>Result</code>, the type of the result of the background 后台的结果的类型
 * computation.</li>
 * </ol>
 * <p>Not all types are always used by an asynchronous task. To mark a type as unused,
 * simply use the type {@link Void}:</p>
 * 并非所有类型都由异步任务使用。 要将类型标记为未使用，只需使用类型{@link Void}：
 * <pre>
 * private class MyTask extends AsyncTask&lt;Void, Void, Void&gt; { ... }
 * </pre>
 * <p>
 * <h2>The 4 steps</h2>
 * <p>When an asynchronous task is executed, the task goes through 4 steps:</p>
 * 当执行异步任务时，任务通过4个步骤：
 * <ol>
 * <li>{@link #onPreExecute()}, invoked on the UI thread before the task
 * is executed. This step is normally used to setup the task, for instance by
 * showing a progress bar in the user interface.</li>
 * {@link #onPreExecute（）}，在UI线程上执行任务之前调用。 此步骤通常用于设置任务，例如通过在用户界面中显示进度条。
 * <li>{@link #doInBackground}, invoked on the background thread
 * immediately after {@link #onPreExecute()} finishes executing. This step is used
 * to perform background computation that can take a long time. The parameters
 * of the asynchronous task are passed to this step. The result of the computation must
 * be returned by this step and will be passed back to the last step. This step
 * can also use {@link #publishProgress} to publish one or more units
 * of progress. These values are published on the UI thread, in the
 * {@link #onProgressUpdate} step.</li>
 * <p>
 * {@link #doInBackground}，在{@link #onPreExecute（）}完成执行后立即在后台线程上调用。
 * 此步骤用于执行可能需要很长时间的后台计算。
 * 异步任务的参数传递到此步骤。
 * 计算的结果必须由此步骤返回，并且将返回到最后一步。
 * 此步骤还可以使用{@link #publishProgress}发布一个或多个进度单位。 这些值在UI线程上的{@link #onProgressUpdate}步骤中发布。
 * <p>
 * <li>{@link #onProgressUpdate}, invoked on the UI thread after a
 * call to {@link #publishProgress}. The timing of the execution is
 * undefined. This method is used to display any form of progress in the user
 * interface while the background computation is still executing. For instance,
 * it can be used to animate a progress bar or show logs in a text field.</li>
 * <li>{@link #onPostExecute}, invoked on the UI thread after the background
 * computation finishes. The result of the background computation is passed to
 * this step as a parameter.</li>
 * </ol>
 * {@link #onProgressUpdate}，在调用{@link #publishProgress}后在UI线程上调用。
 * 执行的时间未定义。
 * 此方法用于在后台计算仍在执行时在用户界面中显示任何形式的进度。
 * 例如，它可用于动画进度条或在文本字段中显示日志。
 * {@ link #onPostExecute}，在后台计算完成后在UI线程上调用。
 * 后台计算的结果作为参数传递到该步骤。
 * <p>
 * <h2>Cancelling a task</h2>
 * 取消任务
 * <p>A task can be cancelled at any time by invoking {@link #cancel(boolean)}. Invoking
 * this method will cause subsequent calls to {@link #isCancelled()} to return true.
 * After invoking this method, {@link #onCancelled(Object)}, instead of
 * {@link #onPostExecute(Object)} will be invoked after {@link #doInBackground(Object[])}
 * returns. To ensure that a task is cancelled as quickly as possible, you should always
 * check the return value of {@link #isCancelled()} periodically from
 * {@link #doInBackground(Object[])}, if possible (inside a loop for instance.)</p>
 * <p>
 * 任务可以通过调用{@link #cancel（boolean）}随时取消。
 * <p>
 * 调用此方法将导致对{@link #isCancelled（）}的后续调用返回true。
 * <p>
 * 调用此方法后，将在{@link #doInBackground（Object []）}返回后调用{@link #onCancelled（Object）}，而不是{@link #onPostExecute（Object）}。
 * <p>
 * 为了确保尽快取消任务，您应该始终从{@link #doInBackground（Object []）}定期检查{@link #isCancelled（）}的返回值，如果可能的话 。）
 * <h2>Threading rules</h2>
 * 线程规则
 * <p>There are a few threading rules that must be followed for this class to
 * work properly:</p>
 * 有几个线程规则，必须遵循这个类才能正常工作：
 * <ul>
 * <li>The AsyncTask class must be loaded on the UI thread. This is done
 * automatically as of {@link android.os.Build.VERSION_CODES#JELLY_BEAN}.</li>
 * <li>The task instance must be created on the UI thread.</li>
 * <li>{@link #execute} must be invoked on the UI thread.</li>
 * <li>Do not call {@link #onPreExecute()}, {@link #onPostExecute},
 * {@link #doInBackground}, {@link #onProgressUpdate} manually.</li>
 * <li>The task can be executed only once (an exception will be thrown if
 * a second execution is attempted.)</li>
 * </ul>
 * <p>
 * AsyncTask类必须在UI线程上加载。 这是从{@link android.os.Build.VERSION_CODES＃JELLY_BEAN}自动完成的。
 * <p>
 * 任务实例必须在UI线程上创建。
 * <p>
 * {@link #execute}必须在UI线程上调用。
 * <p>
 * 请勿手动调用{@link #onPreExecute（）}，{@link #onPostExecute}，{@link #doInBackground}，{@link #onProgressUpdate}。
 * 该任务只能执行一次（如果第二次抛出异常 尝试执行）
 * <p>
 * <p>
 * <h2>Memory observability</h2>
 * 内存可观察性
 * <p>AsyncTask guarantees that all callback calls are synchronized in such a way that the following
 * operations are safe without explicit synchronizations.</p>
 * AsyncTask保证所有回调调用都以这样的方式同步，使得以下操作在没有显式同步的情况下是安全的。
 * <ul>
 * <li>Set member fields in the constructor or {@link #onPreExecute}, and refer to them
 * in {@link #doInBackground}.
 * <li>Set member fields in {@link #doInBackground}, and refer to them in
 * {@link #onProgressUpdate} and {@link #onPostExecute}.
 * </ul>
 * 在构造函数或{@link #onPreExecute}中设置成员字段，并在{@link #doInBackground}中引用它们。
 * <p>
 * 在{@link #doInBackground}中设置成员字段，并在{@link #onProgressUpdate}和{@link #onPostExecute}中引用它们。
 * <p>
 * <h2>Order of execution</h2>
 * 执行顺序
 * <p>When first introduced, AsyncTasks were executed serially on a single background
 * thread. Starting with {@link android.os.Build.VERSION_CODES#DONUT}, this was changed
 * to a pool of threads allowing multiple tasks to operate in parallel. Starting with
 * {@link android.os.Build.VERSION_CODES#HONEYCOMB}, tasks are executed on a single
 * thread to avoid common application errors caused by parallel execution.</p>
 * <p>If you truly want parallel execution, you can invoke
 * {@link #executeOnExecutor(java.util.concurrent.Executor, Object[])} with
 * {@link #THREAD_POOL_EXECUTOR}.</p>
 * <p>
 * 当第一次引入时，AsyncTasks在单个后台线程上连续执行。
 * <p>
 * 从{@link android.os.Build.VERSION_CODES＃DONUT}开始，这被更改为允许多个任务并行操作的线程池。
 * <p>
 * 从{@link android.os.Build.VERSION_CODES＃HONEYCOMB}开始，任务在单个线程上执行，以避免由并行执行引起的常见应用程序错误。
 * <p>
 * 如果您真的需要并行执行，可以使用{@link #THREAD_POOL_EXECUTOR}调用{@link #executeOnExecutor（java.util.concurrent.Executor，Object []）}。
 */
public abstract class AsyncTask<Params, Progress, Result> {
    private static final String LOG_TAG = "AsyncTask";

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    //   我们想在核心池中至少有2个线程，最多4个线程，更喜欢有1个小于CPU计数的CPU，以避免CPU背景饱和
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    /**
     * An {@link Executor}  that can be used to execute tasks in parallel.Executor
     * 一个{@link Executor}，可以用来并行执行任务
     */
    public static final Executor THREAD_POOL_EXECUTOR;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    /**
     * An {@link Executor} that executes tasks one at a time in serial
     * order.  This serialization is global to a particular process.
     * 一个{@link Executor}，它以串行顺序一次执行一个任务。 此序列化对于特定进程是全局的。
     */
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;

    //默认是单线程
    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;

    private static InternalHandler sHandler;

    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;

    private volatile Status mStatus = Status.PENDING;

    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

    /**
     * Indicates the current status of the task. Each status will be set only once
     * during the lifetime of a task.
     * 表示任务的当前状态。 在任务的生命周期中每个状态将只设置一次
     */
    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         * 表示任务尚未执行
         */
        PENDING,
        /**
         * Indicates that the task is running.
         * 表示任务正在运行
         */
        RUNNING,
        /**
         * Indicates that {@link AsyncTask#onPostExecute} has finished.
         * 表示任务已经结束
         */
        FINISHED,
    }

    private static Handler getHandler() {
        synchronized (AsyncTask.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    /**
     * @hide
     */
    public static void setDefaultExecutor(Executor exec) {
        sDefaultExecutor = exec;
    }

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     * 创建新的异步任务。 此构造函数必须在UI线程上调用
     */
    public AsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);
                Result result = null;
                try {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    //noinspection unchecked
                    //noinspection未选中
                    result = doInBackground(mParams);
                    Binder.flushPendingCommands();
                } catch (Throwable tr) {
                    mCancelled.set(true);
                    throw tr;
                } finally {
                    postResult(result);
                }
                return result;
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {
                    android.util.Log.w(LOG_TAG, e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occurred while executing doInBackground()",
                            e.getCause());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }

    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    private Result postResult(Result result) {
        @SuppressWarnings("unchecked")
        Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }

    /**
     * Returns the current status of this task.
     * 返回此任务的当前状态
     *
     * @return The current status.当前状态
     */
    public final Status getStatus() {
        return mStatus;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * 此方法在后台线程上执行计算。 指定的参数是由此任务的调用者传递给{@link #execute}的参数。
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     * 此方法可以调用{@link #publishProgress}在UI线程上发布更新。
     *
     * @param params The parameters of the task.任务的参数
     * @return A result, defined by the subclass of this task.由此任务的子类定义的结果
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @WorkerThread
    protected abstract Result doInBackground(Params... params);

    /**
     * Runs on the UI thread before {@link #doInBackground}.在{@link #doInBackground}之前,在UI线程上运行
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @MainThread
    protected void onPreExecute() {
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p>
     * <p>This method won't be invoked if the task was cancelled.</p>
     * <p>
     * 在{@link #doInBackground}之后的UI线程上运行。 指定的结果是{@link #doInBackground}返回的值。如果任务被取消，则不会调用此方法。
     *
     * @param result The result of the operation computed by {@link #doInBackground}.由{@link #doInBackground}计算的运算结果。
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @SuppressWarnings({"UnusedDeclaration"})
    @MainThread
    protected void onPostExecute(Result result) {
    }

    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     * 在调用{@link #publishProgress}之后，在UI线程上运行。
     * 指定的值是传递给{@link #publishProgress}的值。
     *
     * @param values The values indicating progress.指示进度的值。
     * @see #publishProgress
     * @see #doInBackground
     */
    @SuppressWarnings({"UnusedDeclaration"})
    @MainThread
    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     * 在调用{@link #cancel（boolean）}并且{@link #doInBackground（Object []）}完成后，在UI线程上运行。
     * <p>
     * <p>The default implementation simply invokes {@link #onCancelled()} and
     * ignores the result. If you write your own implementation, do not call
     * <code>super.onCancelled(result)</code>.</p>
     * 默认实现简单地调用{@link #onCancelled（）}和
     * 忽略结果。 如果你编写自己的实现，不要调用super.onCancelled（result）
     *
     * @param result The result, if any, computed in
     *               {@link #doInBackground(Object[])}, can be null
     *               在{@link #doInBackground（Object []）}中计算的结果（如果有）可以为null
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    @SuppressWarnings({"UnusedParameters"})
    @MainThread
    protected void onCancelled(Result result) {
        onCancelled();
    }

    /**
     * <p>Applications should preferably override {@link #onCancelled(Object)}.
     * This method is invoked by the default implementation of
     * {@link #onCancelled(Object)}.</p>
     * <p>
     * 应用程序应优先覆盖{@link #onCancelled（Object）}。此方法由{@link #onCancelled（Object）}的默认实现调用。
     * <p>
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     * <p>
     * 在调用{@link #cancel（boolean）}并且{@link #doInBackground（Object []）}完成后，在UI线程上运行。
     *
     * @see #onCancelled(Object)
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    @MainThread
    protected void onCancelled() {
    }

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed
     * normally. If you are calling {@link #cancel(boolean)} on the task,
     * the value returned by this method should be checked periodically from
     * {@link #doInBackground(Object[])} to end the task as soon as possible.
     * <p>
     * 如果此任务在正常完成之前被取消，则返回true。
     * 如果您在任务上调用{@link #cancel（boolean）}，
     * 则应该从{@link #doInBackground（Object []）}定期检查此方法返回的值，以尽快结束任务。
     *
     * @return <tt>true</tt> if task was cancelled before it completed
     * <tt> true </ tt>如果任务在完成之前被取消
     * @see #cancel(boolean)
     */
    public final boolean isCancelled() {
        return mCancelled.get();
    }

    /**
     * <p>Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when <tt>cancel</tt> is called,
     * this task should never run. If the task has already started,
     * then the <tt>mayInterruptIfRunning</tt> parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.</p>
     * <p>
     * 尝试取消此任务的执行。
     * 如果任务已完成，已取消或由于某些其他原因无法取消，则此尝试将失败。
     * 如果成功，并且此任务在调用cancel时未启动，则此任务不应运行。
     * 如果任务已经启动，则mayInterruptIfRunning参数确定是否应该中断执行此任务的线程以尝试停止任务。
     * <p>
     * <p>Calling this method will result in {@link #onCancelled(Object)} being
     * invoked on the UI thread after {@link #doInBackground(Object[])}
     * returns. Calling this method guarantees that {@link #onPostExecute(Object)}
     * is never invoked. After invoking this method, you should check the
     * value returned by {@link #isCancelled()} periodically from
     * {@link #doInBackground(Object[])} to finish the task as early as
     * possible.</p>
     * <p>
     * 调用此方法将导致{@link #onCancelled（Object）}在{@link #doInBackground（Object []）}返回后在UI线程上被调用。
     * 调用此方法可确保不会调用{@link #onPostExecute（Object）}。
     * 调用此方法后，您应该从{@link #doInBackground（Object []）}定期检查{@link #isCancelled（）}返回的值，以尽快完成任务。
     *
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
     *                              task should be interrupted; otherwise, in-progress tasks are allowed
     *                              to complete.
     *                              true 如果执行此任务的线程应该中断; 否则，正在进行的任务被允许完成。
     * @return <tt>false</tt> if the task could not be cancelled,
     * typically because it has already completed normally;
     * false如果任务无法取消，通常是因为它已经正常完成;
     * <tt>true</tt> otherwise true 除此以外
     * @see #isCancelled()
     * @see #onCancelled(Object)
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     * 如果需要等待计算完成，然后检索其结果。
     *
     * @return The computed result. 计算结果
     * @throws CancellationException If the computation was cancelled.如果计算被取消
     * @throws ExecutionException    If the computation threw an exception.如果计算抛出异常。
     * @throws InterruptedException  If the current thread was interrupted
     *                               while waiting.如果当前线程在等待期间中断。
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result.
     * 如果必要，等待最多给定时间以便计算完成，然后检索其结果。
     *
     * @param timeout Time to wait before cancelling the operation.在取消操作之前等待的时间。
     * @param unit    The time unit for the timeout.超时的时间单位。
     * @return The computed result.计算结果。
     * @throws CancellationException If the computation was cancelled.如果计算被取消。
     * @throws ExecutionException    If the computation threw an exception.如果计算抛出异常。
     * @throws InterruptedException  If the current thread was interrupted
     *                               while waiting.如果当前线程在等待期间中断。
     * @throws TimeoutException      If the wait timed out.如果等待超时。
     */
    public final Result get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    /**
     * Executes the task with the specified parameters. The task returns
     * itself (this) so that the caller can keep a reference to it.
     * <p>
     * 使用指定的参数执行任务。 任务返回它本身（这），以便调用者可以保留对它的引用。
     * <p>Note: this function schedules the task on a queue for a single background
     * thread or pool of threads depending on the platform version.  When first
     * introduced, AsyncTasks were executed serially on a single background thread.
     * Starting with {@link android.os.Build.VERSION_CODES#DONUT}, this was changed
     * to a pool of threads allowing multiple tasks to operate in parallel. Starting
     * {@link android.os.Build.VERSION_CODES#HONEYCOMB}, tasks are back to being
     * executed on a single thread to avoid common application errors caused
     * by parallel execution.  If you truly want parallel execution, you can use
     * the {@link #executeOnExecutor} version of this method
     * with {@link #THREAD_POOL_EXECUTOR}; however, see commentary there for warnings
     * on its use.
     * <p>
     * 注意：此功能根据平台版本为单个后台线程或线程池调度队列上的任务。
     * 当第一次引入时，AsyncTasks在单个后台线程上连续执行。
     * 从{@link android.os.Build.VERSION_CODES＃DONUT}开始，这被更改为允许多个任务并行操作的线程池。
     * 启动{@link android.os.Build.VERSION_CODES＃HONEYCOMB}，任务将回到在单个线程上执行，以避免由并行执行引起的常见应用程序错误。
     * 如果您真的需要并行执行，可以使用{@link #executeOnExecutor}版本的此方法与{@link #THREAD_POOL_EXECUTOR};
     * 但是，看到那里的评论有关使用警告。
     * <p>This method must be invoked on the UI thread.
     * 此方法必须在UI线程上调用
     *
     * @param params The parameters of the task.任务的参数
     * @return This instance of AsyncTask.这个AsyncTask实例。
     * @throws IllegalStateException If {@link #getStatus()} returns either
     *                               {@link AsyncTask.Status#RUNNING} or {@link AsyncTask.Status#FINISHED}.
     *                               如果{@link #getStatus（）}返回{@link AsyncTask.Status＃RUNNING}或{@link AsyncTask.Status＃FINISHED}。
     * @see #executeOnExecutor(java.util.concurrent.Executor, Object[])
     * @see #execute(Runnable)
     */
    @MainThread
    public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

    /**
     * Executes the task with the specified parameters. The task returns
     * itself (this) so that the caller can keep a reference to it.
     * <p>
     * 使用指定的参数执行任务。 任务返回它本身（这），以便调用者可以保留对它的引用。
     * <p>This method is typically used with {@link #THREAD_POOL_EXECUTOR} to
     * allow multiple tasks to run in parallel on a pool of threads managed by
     * AsyncTask, however you can also use your own {@link Executor} for custom
     * behavior.
     * 此方法通常与{@link #THREAD_POOL_EXECUTOR}一起使用，
     * 以允许多个任务在由AsyncTask管理的线程池中并行运行，
     * 但您也可以使用自己的{@link Executor}进行自定义行为。
     * <p>
     * <p><em>Warning:</em> Allowing multiple tasks to run in parallel from
     * a thread pool is generally <em>not</em> what one wants, because the order
     * of their operation is not defined.  For example, if these tasks are used
     * to modify any state in common (such as writing a file due to a button click),
     * there are no guarantees on the order of the modifications.
     * Without careful work it is possible in rare cases for the newer version
     * of the data to be over-written by an older one, leading to obscure data
     * loss and stability issues.  Such changes are best
     * executed in serial; to guarantee such work is serialized regardless of
     * platform version you can use this function with {@link #SERIAL_EXECUTOR}.
     * <p>
     * 警告：</ em>允许多个任务从线程池并行运行通常<em>不是</ em>想要的，因为它们的操作顺序未定义。
     * 例如，如果这些任务用于修改任何公共状态（例如由于按钮单击而写入文件），则不能保证修改的顺序。
     * 没有仔细的工作，在极少数情况下，较旧版本的数据可能会被旧版本的数据覆盖，导致模糊的数据丢失和稳定性问题。
     * 这种变化最好串行执行; 以确保此类工作序列化，无论平台版本，您可以使用此功能与{@link #SERIAL_EXECUTOR}。
     * <p>This method must be invoked on the UI thread.
     * 此方法必须在UI线程上调用。
     *
     * @param exec   The executor to use.  {@link #THREAD_POOL_EXECUTOR} is available as a
     *               convenient process-wide thread pool for tasks that are loosely coupled.
     *               执行者使用。 {@link #THREAD_POOL_EXECUTOR}可用作一个方便的进程级线程池，用于松散耦合的任务。
     * @param params The parameters of the task.任务的参数。
     * @return This instance of AsyncTask.这个AsyncTask实例。
     * @throws IllegalStateException If {@link #getStatus()} returns either
     *                               {@link AsyncTask.Status#RUNNING} or {@link AsyncTask.Status#FINISHED}.
     *                               如果{@link #getStatus（）}返回{@link AsyncTask.Status＃RUNNING}或{@link AsyncTask.Status＃FINISHED}。
     * @see #execute(Object[])
     */
    @MainThread
    public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
                                                                       Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        mStatus = Status.RUNNING;

        onPreExecute();

        mWorker.mParams = params;
        exec.execute(mFuture);

        return this;
    }

    /**
     * Convenience version of {@link #execute(Object...)} for use with
     * a simple Runnable object. See {@link #execute(Object[])} for more
     * information on the order of execution.
     * 便利版本的{@link #execute（Object ...）}用于一个简单的Runnable对象。
     * 有关执行顺序的更多信息，请参阅{@link #execute（Object []）}。
     *
     * @see #execute(Object[])
     * @see #executeOnExecutor(java.util.concurrent.Executor, Object[])
     */
    @MainThread
    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }

    /**
     * This method can be invoked from {@link #doInBackground} to
     * publish updates on the UI thread while the background computation is
     * still running. Each call to this method will trigger the execution of
     * {@link #onProgressUpdate} on the UI thread.
     * <p>
     * 此方法可以从{@link #doInBackground}调用以在后台计算仍在运行时在UI线程上发布更新。
     * 每次调用此方法将在UI线程上触发{@link #onProgressUpdate}的执行。
     * {@link #onProgressUpdate} will not be called if the task has been
     * canceled.
     * 如果任务已取消，则不会调用{@link #onProgressUpdate}。
     *
     * @param values The progress values to update the UI with.values用于更新UI的进度值
     * @see #onProgressUpdate
     * @see #doInBackground
     */
    @WorkerThread
    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            getHandler().obtainMessage(MESSAGE_POST_PROGRESS,
                    new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        mStatus = Status.FINISHED;
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result 只有一个结果
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    private static class AsyncTaskResult<Data> {
        final AsyncTask mTask;
        final Data[] mData;

        AsyncTaskResult(AsyncTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }
}
