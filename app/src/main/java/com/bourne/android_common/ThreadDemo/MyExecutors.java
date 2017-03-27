package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.util.SecurityConstants;

public class Executors {

    /**
     创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程。
     在任意点，在大多数 nThreads 线程会处于处理任务的活动状态。
     如果在所有线程处于活动状态时提交附加任务，则在有可用线程之前，附加任务将在队列中等待。
     如果在关闭前的执行期间由于失败而导致任何线程终止，那么一个新线程将代替它执行后续的任务（如果需要）。
     在某个线程被显式地关闭之前，池中的线程将一直存在。
     参数：
     nThreads - 池中的线程数
     返回：
     新创建的线程池
     抛出：
     IllegalArgumentException - 如果 nThreads <= 0
     */
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     创建一个维护足够的线程以支持给定的并行级别的线程池，并且可以使用多个队列来减少争用。
     并行级别对应于主动参与或可以从事任务处理的最大线程数。
     线程的实际数量可以动态增长和收缩。 工作窃取池不保证执行提交的任务的顺序。
     *
     */
    public static ExecutorService newWorkStealingPool(int parallelism) {
        return new ForkJoinPool
                (parallelism,
                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                        null, true);
    }

    /**
     使用{@linkplain Runtime＃availableProcessors可用处理器}的数量作为目标并行级别创建一个工作窃取线程池。
     *
     */
    public static ExecutorService newWorkStealingPool() {
        return new ForkJoinPool
                (Runtime.getRuntime().availableProcessors(),
                        ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                        null, true);
    }

    /**
     创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程，在需要时使用提供的 ThreadFactory 创建新线程。
     在任意点，在大多数 nThreads 线程会处于处理任务的活动状态。
     如果在所有线程处于活动状态时提交附加任务，则在有可用线程之前，附加任务将在队列中等待。
     如果在关闭前的执行期间由于失败而导致任何线程终止，那么一个新线程将代替它执行后续的任务（如果需要）。
     在某个线程被显式地关闭之前，池中的线程将一直存在。
     参数：
     nThreads - 池中的线程数
     threadFactory - 创建新线程时使用的工厂
     返回：
     新创建的线程池
     抛出：
     NullPointerException - 如果 threadFactory 为 null
     IllegalArgumentException - 如果 nThreads <= 0
     */
    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
    }

    /**
     创建一个使用单个 worker 线程的 Executor，以无界队列方式来运行该线程。
     （注意，如果因为在关闭前的执行期间出现失败而终止了此单个线程，那么如果需要，一个新线程将代替它执行后续的任务）。
     可保证顺序地执行各个任务，并且在任意给定的时间不会有多个线程是活动的。
     与其他等效的 newFixedThreadPool(1) 不同，可保证无需重新配置此方法所返回的执行程序即可使用其他的线程。
     返回：
     新创建的单线程 Executor
     */
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
                (new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>()));
    }

    /**
     创建一个使用单个 worker 线程的 Executor，以无界队列方式来运行该线程，并在需要时使用提供的 ThreadFactory 创建新线程。
     与其他等效的 newFixedThreadPool(1, threadFactory) 不同，可保证无需重新配置此方法所返回的执行程序即可使用其他的线程。
     参数：
     threadFactory - 创建新线程时使用的工厂
     返回：
     新创建的单线程 Executor
     抛出：
     NullPointerException - 如果 threadFactory 为 null
     */
    public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return new FinalizableDelegatedExecutorService
                (new ThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        threadFactory));
    }

    /**
     创建一个可根据需要创建新线程的线程池，但是在以前构造的线程可用时将重用它们。
     对于执行很多短期异步任务的程序而言，这些线程池通常可提高程序性能。
     调用 execute 将重用以前构造的线程（如果线程可用）。如果现有线程没有可用的，则创建一个新线程并添加到池中。
     终止并从缓存中移除那些已有 60 秒钟未被使用的线程。
     因此，长时间保持空闲的线程池不会使用任何资源。注意，可以使用 ThreadPoolExecutor 构造方法创建具有类似属性但细节不同（例如超时参数）的线程池。
     返回：
     新创建的线程池
     */
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    /**
     创建一个可根据需要创建新线程的线程池，但是在以前构造的线程可用时将重用它们，并在需要时使用提供的 ThreadFactory 创建新线程。
     参数：
     threadFactory - 创建新线程时使用的工厂
     返回：
     新创建的线程池
     抛出：
     NullPointerException - 如果 threadFactory 为 null
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                threadFactory);
    }

    /**
     创建一个单线程执行程序，它可安排在给定延迟后运行命令或者定期地执行。
     （注意，如果因为在关闭前的执行期间出现失败而终止了此单个线程，那么如果需要，一个新线程会代替它执行后续的任务）。
     可保证顺序地执行各个任务，并且在任意给定的时间不会有多个线程是活动的。
     与其他等效的 newScheduledThreadPool(1) 不同，可保证无需重新配置此方法所返回的执行程序即可使用其他的线程。
     返回：
     新创建的安排执行程序
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return new DelegatedScheduledExecutorService
                (new ScheduledThreadPoolExecutor(1));
    }

    /**
     创建一个单线程执行程序，它可安排在给定延迟后运行命令或者定期地执行。
     （注意，如果因为在关闭前的执行期间出现失败而终止了此单个线程，那么如果需要，一个新线程会代替它执行后续的任务）。
     可保证顺序地执行各个任务，并且在任意给定的时间不会有多个线程是活动的。
     与其他等效的 newScheduledThreadPool(1, threadFactory) 不同，可保证无需重新配置此方法所返回的执行程序即可使用其他的线程。
     参数：
     threadFactory - 创建新线程时使用的工厂
     返回：
     新创建的安排执行程序
     抛出：
     NullPointerException - 如果 threadFactory 为 null
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory threadFactory) {
        return new DelegatedScheduledExecutorService
                (new ScheduledThreadPoolExecutor(1, threadFactory));
    }

    /**
     创建一个线程池，它可安排在给定延迟后运行命令或者定期地执行。
     参数：
     corePoolSize - 池中所保存的线程数，即使线程是空闲的也包括在内。
     返回：
     新创建的安排线程池
     抛出：
     NullPointerException - 如果 threadFactory 为 null
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }

    /**
     创建一个线程池，它可安排在给定延迟后运行命令或者定期地执行。
     参数：
     corePoolSize - 池中所保存的线程数，即使线程是空闲的也包括在内
     threadFactory - 执行程序创建新线程时使用的工厂
     返回：
     新创建的安排线程池
     抛出：
     IllegalArgumentException - 如果 corePoolSize < 0
     NullPointerException - 如果 threadFactory 为 null
     */
    public static ScheduledExecutorService newScheduledThreadPool(
            int corePoolSize, ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }

    /**
     返回一个将所有已定义的 ExecutorService 方法委托给指定执行程序的对象，但是使用强制转换可能无法访问其他方法。
     这提供了一种可安全地“冻结”配置并且不允许调整给定具体实现的方法。
     参数：
     executor - 底层实现
     返回：
     一个 ExecutorService 实例
     抛出：
     NullPointerException - 如果 executor 为 null
     */
    public static ExecutorService unconfigurableExecutorService(ExecutorService executor) {
        if (executor == null)
            throw new NullPointerException();
        return new DelegatedExecutorService(executor);
    }

    /**
     返回一个将所有已定义的 ExecutorService 方法委托给指定执行程序的对象，但是使用强制转换可能无法访问其他方法。
     这提供了一种可安全地“冻结”配置并且不允许调整给定具体实现的方法。
     参数：
     executor - 底层实现
     返回：
     一个 ScheduledExecutorService 实例
     抛出：
     NullPointerException - 如果 executor 为 null
     */
    public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService executor) {
        if (executor == null)
            throw new NullPointerException();
        return new DelegatedScheduledExecutorService(executor);
    }

    /**
     返回用于创建新线程的默认线程工厂。此工厂创建同一 ThreadGroup 中 Executor 使用的所有新线程。
     如果有 SecurityManager，则它使用 System.getSecurityManager() 组来调用此 defaultThreadFactory 方法，其他情况则使用线程组。
     每个新线程都作为非守护程序而创建，并且具有设置为 Thread.NORM_PRIORITY 中较小者的优先级以及线程组中允许的最大优先级。
     新线程具有可通过 pool-N-thread-M 的 Thread.getName() 来访问的名称，其中 N 是此工厂的序列号，M 是此工厂所创建线程的序列号。
     返回：
     线程工厂
     */
    public static ThreadFactory defaultThreadFactory() {
        return new DefaultThreadFactory();
    }

    /**
     * 过时了，不再使用
     *
     * 返回用于创建新线程的线程工厂，这些新线程与当前线程具有相同的权限。
     * 此工厂创建具有与 defaultThreadFactory() 相同设置的线程，
     * 新线程的 AccessControlContext 和 contextClassLoader 的其他设置与调用此 privilegedThreadFactory 方法的线程相同。可以在 AccessController.doPrivileged(java.security.PrivilegedAction) 操作中创建一个新 privilegedThreadFactory，
     * 设置当前线程的访问控制上下文，以便创建具有该操作中保持的所选权限的线程。
     注意，虽然运行在此类线程中的任务具有与当前线程相同的访问控制和类加载器，但是它们无需具有相同的 ThreadLocal 或 InheritableThreadLocal 值。
     如有必要，使用 ThreadPoolExecutor.beforeExecute(java.lang.Thread, java.lang.Runnable) 在 ThreadPoolExecutor 子类中运行任何任务前，可以设置或重置线程局部变量的特定值。
     另外，如果必须初始化 worker 线程，以具有与某些其他指定线程相同的 InheritableThreadLocal 设置，则可以在线程等待和服务创建请求的环境中创建自定义的 ThreadFactory，而不是继承其值。

     返回：
     线程工厂
     抛出：
     AccessControlException - 如果当前访问控制上下文没有获取和设置上下文类加载器的权限。
     */
    public static ThreadFactory privilegedThreadFactory() {
        return new PrivilegedThreadFactory();
    }

    /**
     返回 Callable 对象，调用它时可运行给定的任务并返回给定的结果。这在把需要 Callable 的方法应用到其他无结果的操作时很有用。
     参数：
     task - 要运行的任务
     result - 返回的结果
     返回：
     一个 callable 对象
     抛出：
     NullPointerException - 如果 task 为 null
     */
    public static <T> Callable<T> callable(Runnable task, T result) {
        if (task == null)
            throw new NullPointerException();
        return new RunnableAdapter<T>(task, result);
    }

    /**
     返回 Callable 对象，调用它时可运行给定的任务并返回 null。
     参数：
     task - 要运行的任务
     返回：
     一个 callable 对象
     抛出：
     NullPointerException - 如果 task 为 null
     */
    public static Callable<Object> callable(Runnable task) {
        if (task == null)
            throw new NullPointerException();
        return new RunnableAdapter<Object>(task, null);
    }

    /**
     返回 Callable 对象，调用它时可运行给定特权的操作并返回其结果。
     参数：
     action - 要运行的特权操作
     返回：
     一个 callable 对象
     抛出：
     NullPointerException - 如果 action 为 null
     */
    public static Callable<Object> callable(final PrivilegedAction<?> action) {
        if (action == null)
            throw new NullPointerException();
        return new Callable<Object>() {
            public Object call() { return action.run(); }};
    }

    /**
     返回 Callable 对象，调用它时可运行给定特权的异常操作并返回其结果。
     参数：
     action - 要运行的特权异常操作
     返回：
     一个 callable 对象
     抛出：
     NullPointerException - 如果 action 为 null
     */
    public static Callable<Object> callable(final PrivilegedExceptionAction<?> action) {
        if (action == null)
            throw new NullPointerException();
        return new Callable<Object>() {
            public Object call() throws Exception { return action.run(); }};
    }

    /**
     *过时了，不在使用
     *
     * 返回 Callable 对象，调用它时可在当前的访问控制上下文中执行给定的 callable 对象。
     * 通常应该在 AccessController.doPrivileged(java.security.PrivilegedAction) 操作中调用此方法，以便创建 callable 对象，并且如有可能，则在该操作中保持的所选权限设置下执行此对象；
     * 如果无法调用，则抛出相关的 AccessControlException。
     参数：
     callable - 底层任务
     返回：
     一个 callable 对象
     抛出：
     NullPointerException - 如果 callable 为 null
     */
    public static <T> Callable<T> privilegedCallable(Callable<T> callable) {
        if (callable == null)
            throw new NullPointerException();
        return new PrivilegedCallable<T>(callable);
    }

    /**
     * 过时了，不在使用
     *
     * 返回 Callable 对象，调用它时可在当前的访问控制上下文中，使用当前上下文类加载器作为上下文类加载器来执行给定的 callable 对象。
     * 通常应该在 AccessController.doPrivileged(java.security.PrivilegedAction) 操作中调用此方法，以创建 callable 对象，并且如有可能，
     * 则在该操作中保持的所选权限设置下执行此对象；如果无法调用，则抛出相关的 AccessControlException。
     参数：
     callable - 底层任务
     返回：
     一个 callable 对象
     抛出：
     NullPointerException - 如果 callable 为 null
     AccessControlException - 如果当前的访问控制上下文没有设置和获得上下文类加载器的权限。
     */
    public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> callable) {
        if (callable == null)
            throw new NullPointerException();
        return new PrivilegedCallableUsingCurrentClassLoader<T>(callable);
    }

    // 支持公共方式的非公共类

    /**
     * 运行给定任务并返回给定结果的Callable
     */
    private static final class RunnableAdapter<T> implements Callable<T> {
        private final Runnable task;
        private final T result;
        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }
        public T call() {
            task.run();
            return result;
        }
    }

    /**
     * 可以在已建立的访问控制设置下运行的callable
     */
    private static final class PrivilegedCallable<T> implements Callable<T> {
        final Callable<T> task;
        final AccessControlContext acc;

        PrivilegedCallable(Callable<T> task) {
            this.task = task;
            this.acc = AccessController.getContext();
        }

        public T call() throws Exception {
            try {
                return AccessController.doPrivileged(
                        new PrivilegedExceptionAction<T>() {
                            public T run() throws Exception {
                                return task.call();
                            }
                        }, acc);
            } catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
    }

    /**
     * 一个可在所建立的访问控制设置和当前ClassLoader下运行的callable
     */
    private static final class PrivilegedCallableUsingCurrentClassLoader<T>
            implements Callable<T> {
        final Callable<T> task;
        final AccessControlContext acc;
        final ClassLoader ccl;

        PrivilegedCallableUsingCurrentClassLoader(Callable<T> task) {
            // BEGIN android-removed
            // SecurityManager sm = System.getSecurityManager();
            // if (sm != null) {
         //   从这个类调用getContextClassLoader不会触发安全检查，但是我们检查我们的调用者是否拥有此权限。
            //     sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);

//            是否将setContextClassLoader证明是必要的，如果权限不可用，我们会快速失败
            //     sm.checkPermission(new RuntimePermission("setContextClassLoader"));
            // }
            // END android-removed
            this.task = task;
            this.acc = AccessController.getContext();
            this.ccl = Thread.currentThread().getContextClassLoader();
        }

        public T call() throws Exception {
            try {
                return AccessController.doPrivileged(
                        new PrivilegedExceptionAction<T>() {
                            public T run() throws Exception {
                                Thread t = Thread.currentThread();
                                ClassLoader cl = t.getContextClassLoader();
                                if (ccl == cl) {
                                    return task.call();
                                } else {
                                    t.setContextClassLoader(ccl);
                                    try {
                                        return task.call();
                                    } finally {
                                        t.setContextClassLoader(cl);
                                    }
                                }
                            }
                        }, acc);
            } catch (PrivilegedActionException e) {
                throw e.getException();
            }
        }
    }

    /**
     * 默认线程工厂
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    /**
     * 线程工厂捕获访问控制上下文和类加载器。
     */
    private static class PrivilegedThreadFactory extends DefaultThreadFactory {
        final AccessControlContext acc;
        final ClassLoader ccl;

        PrivilegedThreadFactory() {
            super();
            // BEGIN android-removed
            // SecurityManager sm = System.getSecurityManager();
            // if (sm != null) {
//            从这个类调用getContextClassLoader不会触发安全检查，但是我们检查我们的调用者是否拥有此权限
            //     sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);

            //     // 失败快
            //     sm.checkPermission(new RuntimePermission("setContextClassLoader"));
            // }
            // END android-removed
            this.acc = AccessController.getContext();
            this.ccl = Thread.currentThread().getContextClassLoader();
        }

        public Thread newThread(final Runnable r) {
            return super.newThread(new Runnable() {
                public void run() {
                    AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        public Void run() {
                            Thread.currentThread().setContextClassLoader(ccl);
                            r.run();
                            return null;
                        }
                    }, acc);
                }
            });
        }
    }

    /**
     一个只暴露ExecutorService实现的ExecutorService方法的包装类。
     */
    private static class DelegatedExecutorService
            extends AbstractExecutorService {
        private final ExecutorService e;
        DelegatedExecutorService(ExecutorService executor) { e = executor; }
        public void execute(Runnable command) { e.execute(command); }
        public void shutdown() { e.shutdown(); }
        public List<Runnable> shutdownNow() { return e.shutdownNow(); }
        public boolean isShutdown() { return e.isShutdown(); }
        public boolean isTerminated() { return e.isTerminated(); }
        public boolean awaitTermination(long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.awaitTermination(timeout, unit);
        }
        public Future<?> submit(Runnable task) {
            return e.submit(task);
        }
        public <T> Future<T> submit(Callable<T> task) {
            return e.submit(task);
        }
        public <T> Future<T> submit(Runnable task, T result) {
            return e.submit(task, result);
        }
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
                throws InterruptedException {
            return e.invokeAll(tasks);
        }
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                             long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.invokeAll(tasks, timeout, unit);
        }
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
                throws InterruptedException, ExecutionException {
            return e.invokeAny(tasks);
        }
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                               long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny(tasks, timeout, unit);
        }
    }

    private static class FinalizableDelegatedExecutorService
            extends DelegatedExecutorService {
        FinalizableDelegatedExecutorService(ExecutorService executor) {
            super(executor);
        }
        protected void finalize() {
            super.shutdown();
        }
    }

    /**
     仅公开ScheduledExecutorService实现的ScheduledExecutorService方法的包装类。
     */
    private static class DelegatedScheduledExecutorService
            extends DelegatedExecutorService
            implements ScheduledExecutorService {
        private final ScheduledExecutorService e;
        DelegatedScheduledExecutorService(ScheduledExecutorService executor) {
            super(executor);
            e = executor;
        }
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return e.schedule(command, delay, unit);
        }
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return e.schedule(callable, delay, unit);
        }
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return e.scheduleAtFixedRate(command, initialDelay, period, unit);
        }
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return e.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }

    /** 不能被实例化 */
    private Executors() {}
}
