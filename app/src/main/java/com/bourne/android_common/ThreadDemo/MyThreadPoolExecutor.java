//package java.util.concurrent;
//
//import java.util.concurrent.AbstractExecutorService;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.RejectedExecutionException;
//import java.util.concurrent.RejectedExecutionHandler;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Condition;
//import java.util.ArrayList;
//import java.util.ConcurrentModificationException;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.AbstractQueuedSynchronizer;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// 使用ThreadPoolExecutor
//
// private final int CORE_POOL_SIZE = 4;//核心线程数
// private final int MAX_POOL_SIZE = 5;//最大线程数
// private final long KEEP_ALIVE_TIME = 10;//空闲线程超时时间
// private final int BLOCK_SIZE = 2;//阻塞队列大小
//
// ThreadPoolExecutor executorPool = new ThreadPoolExecutor(
// CORE_POOL_SIZE,
// MAX_POOL_SIZE, KEEP_ALIVE_TIME,TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(BLOCK_SIZE),Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
// executorPool.allowCoreThreadTimeOut(true);
//
// executorPool.execute(new WorkerThread(""));
// 官方文档说明
//
// 一个 ExecutorService，它使用可能的几个池线程之一执行每个提交的任务，通常使用 Executors 工厂方法配置。
//
// 线程池可以解决两个不同问题：
//
// 由于减少了每个任务调用的开销，它们通常可以在执行大量异步任务时提供增强的性能。
//
// 提供绑定和管理资源（包括执行任务集时使用的线程）的方法。每个 ThreadPoolExecutor 还维护着一些基本的统计数据，如完成的任务数。
//
// 强烈建议程序员使用较为方便的 Executors 工厂方法 Executors.newCachedThreadPool()（无界线程池，可以进行自动线程回收）、Executors.newFixedThreadPool(int)（固定大小线程池）和 Executors.newSingleThreadExecutor()（单个后台线程），它们均为大多数使用场景预定义了设置
//
// 也可以可自定义：
//
// 核心和最大池大小
//
// ThreadPoolExecutor 将根据 corePoolSize（参见 getCorePoolSize()）和 maximumPoolSize（参见 getMaximumPoolSize()）设置的边界自动调整池大小。
//
// 当新任务在方法 execute(Java.lang.Runnable) 中提交时，如果运行的线程少于 corePoolSize，则创建新线程来处理请求，即使其他辅助线程是空闲的。
//
// 如果运行的线程多于 corePoolSize 而少于 maximumPoolSize，则仅当队列满时才创建新线程。
//
// 如果设置的 corePoolSize 和 maximumPoolSize 相同，则创建了固定大小的线程池。
//
// 如果将 maximumPoolSize 设置为基本的无界值（如 Integer.MAX_VALUE），则允许池适应任意数量的并发任务。
//
// 在大多数情况下，核心和最大池大小仅基于构造来设置，不过也可以使用 setCorePoolSize(int) 和 setMaximumPoolSize(int) 进行动态更改。
//
// 按需构造
//
// 默认情况下，即使核心线程最初只是在新任务到达时才创建和启动的，也可以使用方法 prestartCoreThread() 或 prestartAllCoreThreads() 对其进行动态重写。如果构造带有非空队列的池，则可能希望预先启动线程。
//
// 创建新线程
//
// 使用 ThreadFactory 创建新线程。如果没有另外说明，则在同一个 ThreadGroup 中一律使用 Executors.defaultThreadFactory() 创建线程，并且这些线程具有相同的 NORM_PRIORITY 优先级和非守护进程状态。
//
// 通过提供不同的 ThreadFactory，可以改变线程的名称、线程组、优先级、守护进程状态，等等。
//
// 如果从 newThread 返回 null 时 ThreadFactory 未能创建线程，则执行程序将继续运行，但不能执行任何任务。
//
// 保持活动时间
//
// 如果池中当前有多于 corePoolSize 的线程，则这些多出的线程在空闲时间超过 keepAliveTime 时将会终止（参见 getKeepAliveTime(java.util.concurrent.TimeUnit)）。
//
// 这提供了当池处于非活动状态时减少资源消耗的方法。如果池后来变得更为活动，则可以创建新的线程。
//
// 也可以使用方法 setKeepAliveTime(long, java.util.concurrent.TimeUnit) 动态地更改此参数。
//
// 使用 Long.MAX_VALUE TimeUnit.NANOSECONDS 的值在关闭前有效地从以前的终止状态禁用空闲线程。
//
// 默认情况下，保持活动策略只在有多于 corePoolSizeThreads 的线程时应用。
//
// 但是只要 keepAliveTime 值非 0，allowCoreThreadTimeOut(boolean) 方法也可将此超时策略应用于核心线程。
//
// 一个小例子：
//
// 要做异步任务执行队列，具体需求如下：
//
// 有个线程池用于执行任务
//
// 有个有界队列，用于缓存未执行的任务
//
// 没有任务执行时，我希望线程池中的线程停掉
//
// 这看似是个很正常的需求，但是用JDK1.5(我的工作地方JDK还是1.5的)实现，真得很困难的。
//
// ThreadPoolExecutor中线程池有corePoolSize 和 maximumPoolSize两个参数。JDK1.5中线程池至少保持corePoolSize的线程，所以为了满足上面的需求，corePoolSize必须被设置为0。但是，JDK1.5中队列不满的话，是不会创建大于corePoolSize大小的线程数的。也就是，corePoolSize为0时，队列满了，才会创建新的线程，这显然不满足我的需求。
//
// 今天看JDK1.6的文档时，发现ThreadPoolExecutor多了一个allowCoreThreadTimeOut方法。这个方法是允许线程数低于corePoolSize时，线程也因为空闲而终止。有了这个方法，实现上面的需求就非常简单了。将corePoolSize 和 maximumPoolSize设置为相同的大小，allowCoreThreadTimeOut设置为true，加上一个有界队列，OK了。
//
// 排队
//
// 所有 BlockingQueue 都可用于传输和保持提交的任务。可以使用此队列与池大小进行交互：
//
// 如果运行的线程少于 corePoolSize，则 Executor 始终首选添加新的线程，而不进行排队。
//
// 如果运行的线程等于或多于 corePoolSize，则 Executor 始终首选将请求加入队列，而不添加新的线程。
//
// 比如说，corePoolSize为4，maximumPoolSize为5，使用了new LinkedBlockingDeque()，那么就一直不会创建第5个线程。但是将new LinkedBlockingDeque()换成new LinkedBlockingDeque(1)，当任务小于等于5时，不会创建第5个线程；当任务为大于或等于6个时，会创建第5个线程。
//
// 如果无法将请求加入队列，则创建新的线程，除非创建此线程超出 maximumPoolSize，在这种情况下，任务将被拒绝。
//
// 排队有三种通用策略：
//
// 1、直接提交。工作队列的默认选项是 SynchronousQueue，它将任务直接提交给线程而不保持它们。在此，如果不存在可用于立即运行任务的线程，则试图把任务加入队列将失败，因此会构造一个新的线程。此策略可以避免在处理可能具有内部依赖性的请求集时出现锁。直接提交通常要求无界 maximumPoolSizes 以避免拒绝新提交的任务。当命令以超过队列所能处理的平均数连续到达时，此策略允许无界线程具有增长的可能性。
//
// 2、无界队列。使用无界队列（例如，不具有预定义容量的 LinkedBlockingQueue）将导致在所有 corePoolSize 线程都忙时新任务在队列中等待。这样，创建的线程就不会超过 corePoolSize。（因此，maximumPoolSize 的值也就无效了。）当每个任务完全独立于其他任务，即任务执行互不影响时，适合于使用无界队列；例如，在 Web 页服务器中。这种排队可用于处理瞬态突发请求，当命令以超过队列所能处理的平均数连续到达时，此策略允许无界线程具有增长的可能性。
//
// 3、有界队列。当使用有限的 maximumPoolSizes 时，有界队列（如 ArrayBlockingQueue）有助于防止资源耗尽，但是可能较难调整和控制。队列大小和最大池大小可能需要相互折衷：使用大型队列和小型池可以最大限度地降低 CPU 使用率、操作系统资源和上下文切换开销，但是可能导致人工降低吞吐量。如果任务频繁阻塞（例如，如果它们是 I/O 边界），则系统可能为超过您许可的更多线程安排时间。使用小型队列通常要求较大的池大小，CPU 使用率较高，但是可能遇到不可接受的调度开销，这样也会降低吞吐量。
//
// 被拒绝的任务
//
// 当 Executor 已经关闭，并且 Executor 将有限边界用于最大线程和工作队列容量，且已经饱和时，在方法 execute(java.lang.Runnable) 中提交的新任务将被拒绝。
//
// 在以上两种情况下，execute 方法都将调用其 RejectedExecutionHandler 的 RejectedExecutionHandler.rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor) 方法。
//
// 下面提供了四种预定义的处理程序策略：
//
// 1、在默认的 ThreadPoolExecutor.AbortPolicy 中，处理程序遭到拒绝将抛出运行时 RejectedExecutionException。
// 在 ThreadPoolExecutor.CallerRunsPolicy 中，线程调用运行该任务的 execute 本身。此策略提供简单的反馈控制机制，能够减缓新任务的提交速度。
//
// 2、在 ThreadPoolExecutor.DiscardPolicy 中，不能执行的任务将被删除。
//
// 3、在 ThreadPoolExecutor.DiscardOldestPolicy 中，如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程）。
//
// 4、定义和使用其他种类的 RejectedExecutionHandler 类也是可能的，但这样做需要非常小心，尤其是当策略仅用于特定容量或排队策略时。
//
// 钩子 (hook) 方法
//
// 此类提供 protected 可重写的 beforeExecute(java.lang.Thread, java.lang.Runnable) 和 afterExecute(java.lang.Runnable, java.lang.Throwable) 方法，这两种方法分别在执行每个任务之前和之后调用。它们可用于操纵执行环境；例如，重新初始化 ThreadLocal、搜集统计信息或添加日志条目。此外，还可以重写方法 terminated() 来执行 Executor 完全终止后需要完成的所有特殊处理。
//
// 如果钩子 (hook) 或回调方法抛出异常，则内部辅助线程将依次失败并突然终止。
//
// 队列维护
//
// 方法 getQueue() 允许出于监控和调试目的而访问工作队列。强烈反对出于其他任何目的而使用此方法。remove(java.lang.Runnable) 和 purge() 这两种方法可用于在取消大量已排队任务时帮助进行存储回收。
//
// 终止
//
// 程序 AND 不再引用的池没有剩余线程会自动 shutdown。如果希望确保回收取消引用的池（即使用户忘记调用 shutdown()），则必须安排未使用的线程最终终止：设置适当保持活动时间，使用 0 核心线程的下边界和/或设置 allowCoreThreadTimeOut(boolean)。
//
// 扩展示例。此类的大多数扩展可以重写一个或多个受保护的钩子 (hook) 方法。例如，下面是一个添加了简单的暂停/恢复功能的子类：
//
// class PausableThreadPoolExecutor extends ThreadPoolExecutor {
// private boolean isPaused;
// private ReentrantLock pauseLock = new ReentrantLock();
// private Condition unpaused = pauseLock.newCondition();
//
// public PausableThreadPoolExecutor(...) { super(...); }
//
// protected void beforeExecute(Thread t, Runnable r) {
// super.beforeExecute(t, r);
// pauseLock.lock();
// try {
// while (isPaused) unpaused.await();
// } catch(InterruptedException ie) {
// t.interrupt();
// } finally {
// pauseLock.unlock();
// }
// }
//
// public void pause() {
// pauseLock.lock();
// try {
// isPaused = true;
// } finally {
// pauseLock.unlock();
// }
// }
//
// public void resume() {
// pauseLock.lock();
// try {
// isPaused = false;
// unpaused.signalAll();
// } finally {
// pauseLock.unlock();
// }
// }
// }
// */
//public class ThreadPoolExecutor extends AbstractExecutorService {
//    /**
//     主池控制状态ctl是一个原子整数包装两个概念领域
//
//     workerCount,指示线程的有效数量
//     runState,    指示是否运行，关闭等
//
//     为了将它们打包成一个int，我们将workerCount限制为（2 ^ 29）-1（约5亿）个线程，而不是（2 ^ 31）-1（20亿），否则可以表示。
//     如果将来这是一个问题，变量可以改为AtomicLong，并且shift / mask常数下面被调整。
//     但直到需要出现，这个代码使用一个int来更快更简单。
//
//     workerCount是允许启动并且不允许停止的工作人员的数量。
//     该值可能与实际线程的实际数量暂时不同，例如当ThreadFactory在被询问时无法创建线程，并且退出线程在终止之前仍然执行记录时。
//     用户可见的池大小被报告为工作集的当前大小。
//
//     runState提供主要的生命周期控制，取值：
//
//     RUNNING:  接受新任务并处理排队的任务
//     SHUTDOWN: 不接受新任务，而是处理排队的任务
//     STOP:    不接受新任务，不处理排队任务，
//             并中断进行中的任务
//     TIDYING:  所有任务已经终止，workerCount为零，线程转换到状态TIDYING将运行terminate（）hook方法
//     TERMINATED: terminated（）已完成
//
//     这些值之间的数字顺序很重要，以便进行有序的比较。 runState随着时间的推移单调增加，但不需要击中每个状态。 过渡是：
//     *
//     * RUNNING -> SHUTDOWN
//     *    关于shutdown（）的调用，可能隐含在finalize（）
//     * (RUNNING or SHUTDOWN) -> STOP
//     *   调用shutdownNow（）
//     * SHUTDOWN -> TIDYING
//     *    队列和池都为空时
//     * STOP -> TIDYING
//     *  当池是空的
//     * TIDYING -> TERMINATED
//     *   当terminate（）hook 方法完成时
//     *
//     在等待Termination（）等待的线程将在状态达到TERMINATED时返回。
//     *
//     检测从SHUTDOWN到TIDYING的转换比您想要的不那么简单，因为在SHUTDOWN状态下，队列可能在非空之后变为空，反之亦然，
//     但是我们只能在看到它为空之后终止，我们看到workerCount 是0（有时需要重新检查 - 见下文）。
//     */
//    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
//    private static final int COUNT_BITS = Integer.SIZE - 3;
//    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
//
//    // runState存储在高位中
//    private static final int RUNNING    = -1 << COUNT_BITS;
//    private static final int SHUTDOWN   =  0 << COUNT_BITS;
//    private static final int STOP       =  1 << COUNT_BITS;
//    private static final int TIDYING    =  2 << COUNT_BITS;
//    private static final int TERMINATED =  3 << COUNT_BITS;
//
//    // 包装和包装ctl
//    private static int runStateOf(int c)     { return c & ~CAPACITY; }
//    private static int workerCountOf(int c)  { return c & CAPACITY; }
//    private static int ctlOf(int rs, int wc) { return rs | wc; }
//
//    /*
//    不需要打包ctl的位字段访问器。 这些取决于位布局，而workerCount从不负面
//     */
//
//    private static boolean runStateLessThan(int c, int s) {
//        return c < s;
//    }
//
//    private static boolean runStateAtLeast(int c, int s) {
//        return c >= s;
//    }
//
//    private static boolean isRunning(int c) {
//        return c < SHUTDOWN;
//    }
//
//    /**
//     * 尝试CAS增加ctl的workerCount字段。
//     */
//    private boolean compareAndIncrementWorkerCount(int expect) {
//        return ctl.compareAndSet(expect, expect + 1);
//    }
//
//    /**
//     * 尝试CAS减去ctl的workerCount字段。
//     */
//    private boolean compareAndDecrementWorkerCount(int expect) {
//        return ctl.compareAndSet(expect, expect - 1);
//    }
//
//    /**
//     减去ctl的workerCount字段。 这仅在线程突然终止时才调用（请参阅processWorkerExit）。 在getTask中执行其他递减。
//     */
//    private void decrementWorkerCount() {
//        do {} while (! compareAndDecrementWorkerCount(ctl.get()));
//    }
//
//    /**
//     用于保存任务并切换到工作线程的队列。
//     我们不要求这个工作Queue.poll（）返回null必然意味着workQueue.isEmpty（），
//     所以仅仅依靠isEmpty来查看队列是否为空（例如，当决定是否从SHUTDOWN转换为TIDYING时，我们必须做） 。
//     这适用于特殊用途的队列，例如DelayQueues，即使在延迟到期时可以稍后返回非空值，poll（）被允许返回null。
//     */
//    private final BlockingQueue<Runnable> workQueue;
//
//    /**
//     锁定在访问工人集合和相关的簿记时。
//     尽管我们可以使用某种并发的集合，但是通常最好使用锁。
//     原因在于这个序列化了interruptIdleWorkers，这样可以避免不必要的中断风暴，尤其是在关机期间。
//     否则退出线程将同时中断尚未中断的线程。
//     它还简化了一些关于maximumPoolSize的相关统计信息的记录。
//     我们还在shutdown和shutdownNow上保存了mainLock，以确保工作者设置稳定，同时单独检查中断和实际中断的权限。
//     */
//    private final ReentrantLock mainLock = new ReentrantLock();
//
//    /**
//     集合包含池中的所有工作线程。 只能在拿着mainLock时才能访问。
//     */
//    private final HashSet<Worker> workers = new HashSet<>();
//
//    /**
//     * 等待条件支持等待终止。
//     */
//    private final Condition termination = mainLock.newCondition();
//
//    /**
//     * 达到最大池大小。 只能在mainLock下使用。
//     */
//    private int largestPoolSize;
//
//    /**
//     计数器完成任务。 仅在终止工作线程时更新。 只能在mainLock下使用。
//     */
//    private long completedTaskCount;
//
//    /*
//    所有用户控制参数被声明为挥发性，以便持续的操作基于最新值，
//    但不需要锁定，因为没有内部不变量取决于它们与其他操作同步地改变。
//     */
//
//    /**
//     工厂新线程。 所有线程都使用此工厂（通过方法addWorker）创建。
//     所有呼叫者必须准备好使addWorker失败，这可能反映了限制线程数量的系统或用户的策略。
//     即使不将其视为错误，创建线程的失败可能会导致新任务被拒绝或现有的任务仍然停留在队列中。
//     *
//     我们进一步保留池不变量，即使面临诸如OutOfMemoryError这样的错误，可能在尝试创建线程时抛出。
//     由于需要在Thread.start中分配本机堆栈，所以这些错误是相当普遍的，用户需要执行清理池关闭来清理。
//     可能有足够的内存可用于清理代码完成，而不会遇到另一个OutOfMemoryError。
//     */
//    private volatile ThreadFactory threadFactory;
//
//    /**
//     * 处理程序在执行饱和或关闭时调用。
//     */
//    private volatile RejectedExecutionHandler handler;
//
//    /**
//     等待工作的空闲线程以超时为单位。
//     当存在超过corePoolSize或allowCoreThreadTimeOut时，线程使用此超时。
//     否则，他们会永远等待新的工作。
//     */
//    private volatile long keepAliveTime;
//
//    /**
//     如果为false（默认），即使空闲时，内核线程仍然保持活动状态。 如果是真的，核心线程使用keepAliveTime超时等待工作。
//     */
//    private volatile boolean allowCoreThreadTimeOut;
//
//    /**
//     核心池大小是保留活动的最小工作数（并且不允许超时等），除非设置allowCoreThreadTimeOut，在这种情况下，最小值为零。
//     */
//    private volatile int corePoolSize;
//
//    /**
//     最大池大小。
//     请注意，实际最大值在内部由CAPACITY限制。
//     */
//    private volatile int maximumPoolSize;
//
//    /**
//     * 默认拒绝的执行处理程序。
//     */
//    private static final RejectedExecutionHandler defaultHandler =
//            new AbortPolicy();
//
//    /**
//     呼叫者关闭和关闭所需的权限。
//     我们另外要求（参见checkShutdownAccess），调用者有权实际中断工作集中的线程（由Thread.interrupt管理，Thread依赖于ThreadGroup.checkAccess，后者依赖于SecurityManager.checkAccess）。
//     只有通过这些检查，才会尝试关闭。
//     *
//     Thread.interrupt的所有实际调用（请参阅interruptIdleWorkers和interruptWorkers）忽略SecurityExceptions，
//     这意味着尝试的中断默默地失败 在关闭的情况下，除非SecurityManager具有不一致的策略，
//     否则它们不应该失败，有时允许访问线程，有时不允许访问线程。
//     在这种情况下，无法实际中断线程可能会禁用或延迟完全终止。
//     interruptIdleWorkers的其他用途是建议，实际中断的失败只会延迟对配置更改的响应，因此不会被特别处理。
//     */
//    private static final RuntimePermission shutdownPerm =
//            new RuntimePermission("modifyThread");
//
//    /**
//     类工作者主要维护线程运行任务的中断控制状态，以及其他较小的簿记。
//     该类机会地扩展了AbstractQueuedSynchronizer，以简化获取和释放围绕每个任务执行的锁。
//     这样可以防止意图唤醒等待任务的工作线程的中断，从而中断正在运行的任务。
//     我们实现一个简单的非可重入互斥锁而不是使用ReentrantLock，因为我们不希望工作任务在调用像setCorePoolSize这样的池控制方法时重新获取锁。
//     另外，为了在线程实际开始运行任务之前抑制中断，我们将锁定状态初始化为负值，并在启动时（在runWorker中）清除它。
//     */
//    private final class Worker
//            extends AbstractQueuedSynchronizer
//            implements Runnable
//    {
//        /**
//         这个类永远不会被序列化，但是我们提供了一个serialVersionUID来抑制javac警告。
//         */
//        private static final long serialVersionUID = 6138294804551838833L;
//
//        /** 线程这个工作正在运行。如果工厂出现故障，则为空 */
//        final Thread thread;
//        /** 运行的初始任务。 可能为空 */
//        Runnable firstTask;
//        /** 每线程任务计数器 */
//        volatile long completedTasks;
//
//        /**
//         从ThreadFactory创建给定的第一个任务和线程。
//        参数：firstTask  第一个任务（如果没有，则为null）
//         */
//        Worker(Runnable firstTask) {
//            setState(-1); // 禁止中断直到runWorker
//            this.firstTask = firstTask;
//            this.thread = getThreadFactory().newThread(this);
//        }
//
//        /** 将主运行循环委托给外部的runWorker。 */
//        public void run() {
//            runWorker(this);
//        }
//
//        //锁定方法
//        //
//        //值0表示解锁状态。
//        //值为1表示锁定状态。
//
//        protected boolean isHeldExclusively() {
//            return getState() != 0;
//        }
//
//        protected boolean tryAcquire(int unused) {
//            if (compareAndSetState(0, 1)) {
//                setExclusiveOwnerThread(Thread.currentThread());
//                return true;
//            }
//            return false;
//        }
//
//        protected boolean tryRelease(int unused) {
//            setExclusiveOwnerThread(null);
//            setState(0);
//            return true;
//        }
//
//        public void lock()        { acquire(1); }
//        public boolean tryLock()  { return tryAcquire(1); }
//        public void unlock()      { release(1); }
//        public boolean isLocked() { return isHeldExclusively(); }
//
//        void interruptIfStarted() {
//            Thread t;
//            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
//                try {
//                    t.interrupt();
//                } catch (SecurityException ignore) {
//                }
//            }
//        }
//    }
//
//    /*
//     * 设置控制状态的方法
//     */
//
//    /**
//     将runState转换为给定目标，或者如果已经至少给定目标，则将其保留。
//     *
//     * 参数： targetState
//     所需的状态，SHUTDOWN或STOP（但不是TIDYING或TERMINATED） - 使用tryTerminate）
//     */
//    private void advanceRunState(int targetState) {
//        // assert targetState == SHUTDOWN || targetState == STOP;
//        for (;;) {
//            int c = ctl.get();
//            if (runStateAtLeast(c, targetState) ||
//                    ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))))
//                break;
//        }
//    }
//
//    /**
//     *如果（SHUTDOWN和池和队列为空）或（STOP和池为空），则转换到TERMINATED状态。
//     *  如果否则可以终止，但是workerCount不为零，则会中断一个空闲的工作人员以确保关闭信号传播。
//     *  必须在可能终止可能的任何操作之后调用此方法 - 在关闭期间减少工作人员计数或从队列中删除任务。
//     *  该方法是非私有的，允许从ScheduledThreadPoolExecutor访问。
//     */
//    final void tryTerminate() {
//        for (;;) {
//            int c = ctl.get();
//            if (isRunning(c) ||
//                    runStateAtLeast(c, TIDYING) ||
//                    (runStateOf(c) == SHUTDOWN && ! workQueue.isEmpty()))
//                return;
//            if (workerCountOf(c) != 0) { // Eligible to terminate
//                interruptIdleWorkers(ONLY_ONE);
//                return;
//            }
//
//            final ReentrantLock mainLock = this.mainLock;
//            mainLock.lock();
//            try {
//                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
//                    try {
//                        terminated();
//                    } finally {
//                        ctl.set(ctlOf(TERMINATED, 0));
//                        termination.signalAll();
//                    }
//                    return;
//                }
//            } finally {
//                mainLock.unlock();
//            }
//            // else 重试失败的CAS
//        }
//    }
//
//    /*
//     * 控制工作线程中断的方法
//     */
//
//    /**
//     如果有安全管理器，请确保调用者有权关闭线程（请参阅shutdownPerm）。
//     如果这样通过，另外确保呼叫者被允许中断每个工作线程。
//     即使第一次检查通过，如果SecurityManager特别处理某些线程，这可能不是真的。
//     */
//    private void checkShutdownAccess() {
//        SecurityManager security = System.getSecurityManager();
//        if (security != null) {
//            security.checkPermission(shutdownPerm);
//            final ReentrantLock mainLock = this.mainLock;
//            mainLock.lock();
//            try {
//                for (Worker w : workers)
//                    security.checkAccess(w.thread);
//            } finally {
//                mainLock.unlock();
//            }
//        }
//    }
//
//    /**
//     中断所有线程，即使活动。 忽略SecurityExceptions（在这种情况下，一些线程可能保持不间断）。
//     */
//    private void interruptWorkers() {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            for (Worker w : workers)
//                w.interruptIfStarted();
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     中断可能正在等待任务的线程（由未被锁定指示），以便它们可以检查终止或配置更改。
//     忽略SecurityExceptions（在这种情况下，一些线程可能保持不间断）。
//     *
//     * 参数 onlyOne
//     如果是ture，最多中断一个工人。 这仅在尝试终止时才从tryTerminate调用，但是还有其他工作人员。
//     在这种情况下，大多数等待工作人员被中断以传播关闭信号，以防所有线程正在等待。
//     中断任意任意线程可确保从关机开始以来新到达的工作人员也将最终退出。
//     为了确保最终终止，只需要中断只有一个空闲的工作人员就足够了，但是shutdown（）会中断所有闲置的工作人员，以便冗余的工作人员立即退出，而不是
//     等待一个分散的任务完成。
//     */
//    private void interruptIdleWorkers(boolean onlyOne) {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            for (Worker w : workers) {
//                Thread t = w.thread;
//                if (!t.isInterrupted() && w.tryLock()) {
//                    try {
//                        t.interrupt();
//                    } catch (SecurityException ignore) {
//                    } finally {
//                        w.unlock();
//                    }
//                }
//                if (onlyOne)
//                    break;
//            }
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     通常形式的interruptIdleWorkers，以避免不必要记住布尔参数的含义。
//     */
//    private void interruptIdleWorkers() {
//        interruptIdleWorkers(false);
//    }
//
//    private static final boolean ONLY_ONE = true;
//
//    /*
//     其他实用程序，其中大部分也导出到ScheduledThreadPoolExecutor
//     */
//
//    /**
//     调用给定命令的被拒绝的执行处理程序。
//     包保护供ScheduledThreadPoolExecutor使用。
//     */
//    final void reject(Runnable command) {
//        handler.rejectedExecution(command, this);
//    }
//
//    /**
//     执行关闭调用后的运行状态转换后进一步清理。
//     这里没有操作，但由ScheduledThreadPoolExecutor用于取消延迟的任务。
//     */
//    void onShutdown() {
//    }
//
//    /**
//     *由ScheduledThreadPoolExecutor需要进行状态检查，以便在关机期间启用运行任务。
//     *
//     * 参数shutdownOK true
//      如果SHUTDOWN应该返回true
//     */
//    final boolean isRunningOrShutdown(boolean shutdownOK) {
//        int rs = runStateOf(ctl.get());
//        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
//    }
//
//    /**
//     将任务队列排入新列表，通常使用drainTo。
//     但是，如果队列是DelayQueue或任何其他类型的队列，poll或drainTo可能无法删除某些元素，则会逐个删除它们。
//     */
//    private List<Runnable> drainQueue() {
//        BlockingQueue<Runnable> q = workQueue;
//        ArrayList<Runnable> taskList = new ArrayList<>();
//        q.drainTo(taskList);
//        if (!q.isEmpty()) {
//            for (Runnable r : q.toArray(new Runnable[0])) {
//                if (q.remove(r))
//                    taskList.add(r);
//            }
//        }
//        return taskList;
//    }
//
//    /*
//     *工作人员创建，运行和清理的方法
//     */
//
//    /**
//     检查是否可以针对当前池状态和给定绑定（核心或最大值）添加新的工作者。
//     如果是这样，则相应地调整工作人员数量，并且如果可能，则创建并启动新的工作人员，运行firstTask作为其第一个任务。
//     如果池停止或有资格关闭，此方法返回false。
//     如果线程工厂在询问时无法创建线程，那么它也返回false。 如果线程创建失败，
//     由于线程工厂返回null或由于异常（通常在Thread.start（）中通常为OutOfMemoryError），因此我们回滚干净。
//     *
//     * 参数 firstTask
//     新线程应该首先运行的任务（如果没有，则为null）。
//     当有少于corePoolSize线程（在这种情况下我们总是启动一个），或当队列已满（在这种情况下，我们必须绕过队列）时，工作者将创建一个初始的第一个任务（方法execute（））来绕过排队， 。
//     最初空闲线程通常通过prestartCoreThread创建或替换其他垂死的工作人员。
//     *
//     * 参数 core
//     如果真的使用corePoolSize作为绑定，否则maximumPoolSize。
//     （这里使用一个布尔指示器，而不是一个值，以确保在检查其他池状态后读取新值）。
//
//     * 返回 true 成功的时候
//     */
//    private boolean addWorker(Runnable firstTask, boolean core) {
//        retry:
//        for (;;) {
//            int c = ctl.get();
//            int rs = runStateOf(c);
//
//            // 检查队列是否只在必要时为空
//            if (rs >= SHUTDOWN &&
//                    ! (rs == SHUTDOWN &&
//                            firstTask == null &&
//                            ! workQueue.isEmpty()))
//                return false;
//
//            for (;;) {
//                int wc = workerCountOf(c);
//                if (wc >= CAPACITY ||
//                        wc >= (core ? corePoolSize : maximumPoolSize))
//                    return false;
//                if (compareAndIncrementWorkerCount(c))
//                    break retry;
//                c = ctl.get();  // 重读 ctl
//                if (runStateOf(c) != rs)
//                    continue retry;
//                // 其他CAS由于workerCount更改而失败; 重试内循环
//            }
//        }
//
//        boolean workerStarted = false;
//        boolean workerAdded = false;
//        Worker w = null;
//        try {
//            w = new Worker(firstTask);
//            final Thread t = w.thread;
//            if (t != null) {
//                final ReentrantLock mainLock = this.mainLock;
//                mainLock.lock();
//                try {
//                // 重新检查，同时保持锁定。
//                // ThreadFactory故障或如果在锁获取之前关闭。
//                    int rs = runStateOf(ctl.get());
//
//                    if (rs < SHUTDOWN ||
//                            (rs == SHUTDOWN && firstTask == null)) {
//                        if (t.isAlive()) // 预先检查t是否可以启动
//                            throw new IllegalThreadStateException();
//                        workers.add(w);
//                        int s = workers.size();
//                        if (s > largestPoolSize)
//                            largestPoolSize = s;
//                        workerAdded = true;
//                    }
//                } finally {
//                    mainLock.unlock();
//                }
//                if (workerAdded) {
//                    t.start();
//                    workerStarted = true;
//                }
//            }
//        } finally {
//            if (! workerStarted)
//                addWorkerFailed(w);
//        }
//        return workerStarted;
//    }
//
//    /**
//     回滚工作线程创建。 如果工人退出工作人员，如果工作人员退出工作人员，重新检查终止，以防该工人的工作终止
//     */
//    private void addWorkerFailed(Worker w) {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            if (w != null)
//                workers.remove(w);
//            decrementWorkerCount();
//            tryTerminate();
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     为垂死的工人执行清理和簿记。
//     仅从工作线程调用。
//     除非已完成设置，否则假设workerCount已经被调整以解除退出。
//     这个方法从工作集中删除线程如果由于用户任务异常退出，或者如果少于corePoolSize工作正在运行或队列不为空，
//     但没有工作人员，则可能会终止池或替换该工作程序。
//     *
//     * 参数 w
//     * the worker
//     * 参数completedAbruptly
//     * 如果工作人员因用户异常而死亡
//     */
//    private void processWorkerExit(Worker w, boolean completedAbruptly) {
//        if (completedAbruptly) // 如果突然，那么workerCount没有被调整
//            decrementWorkerCount();
//
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            completedTaskCount += w.completedTasks;
//            workers.remove(w);
//        } finally {
//            mainLock.unlock();
//        }
//
//        tryTerminate();
//
//        int c = ctl.get();
//        if (runStateLessThan(c, STOP)) {
//            if (!completedAbruptly) {
//                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
//                if (min == 0 && ! workQueue.isEmpty())
//                    min = 1;
//                if (workerCountOf(c) >= min)
//                    return; // replacement not needed
//            }
//            addWorker(null, false);
//        }
//    }
//
//    /**
//     根据当前配置设置执行阻止或定时等待任务，或者如果此工作方必须退出，则返回null：
//     * 1. 有超过maximumPoolSize的工作人员（由于
//        调用setMaximumPoolSize）。
//     * 2. 线程池停止.
//     * 3. 池关闭，队列为空
//     * 4. 该工作人员超时等待任务，
//     * 超时工作人员在定时等待之前和之后都会终止（即{@code allowCoreThreadTimeOut || workerCount> corePoolSize}），
//     * 如果队列不为空， 这个工作人员不是池中的最后一个线程。
//     *
//     * 返回 任务，如果工作人员必须退出，则为null，在这种情况下，workerCount将被递减
//     */
//    private Runnable getTask() {
//        boolean timedOut = false; // Did the last poll() time out?
//
//        for (;;) {
//            int c = ctl.get();
//            int rs = runStateOf(c);
//
//            // 检查队列是否只在必要时为空。
//            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
//                decrementWorkerCount();
//                return null;
//            }
//
//            int wc = workerCountOf(c);
//
//            //工人是否被淘汰？
//            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
//
//            if ((wc > maximumPoolSize || (timed && timedOut))
//                    && (wc > 1 || workQueue.isEmpty())) {
//                if (compareAndDecrementWorkerCount(c))
//                    return null;
//                continue;
//            }
//
//            try {
//                Runnable r = timed ?
//                        workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
//                        workQueue.take();
//                if (r != null)
//                    return r;
//                timedOut = true;
//            } catch (InterruptedException retry) {
//                timedOut = false;
//            }
//        }
//    }
//
//    /**
//     主要工人运行循环。 重复从队列中获取任务并执行它们，同时应对若干问题：
//
//      1. 我们可以从初始任务开始，在这种情况下，我们不需要得到第一个任务。
//     否则，只要池正在运行，我们从getTask获取任务。
//     如果它返回null，那么由于更改池状态或配置参数，该工作器将退出。
//      其他退出会导致外部代码中的异常抛出，在这种情况下，已完成持有，通常会导致processWorkerExit替换此线程。
//
//      2.在运行任何任务之前，在执行任务时获取锁以防止其他池中断，然后我们确保除非池停止，否则该线程不会设置其中断。
//
//     3.每个任务运行之前都是调用beforeExecute，这可能会引发一个异常，在这种情况下，
//      我们会导致线程死机（断开循环，并且已完成），而不处理任务。
//
//     4. 假设beforeExecute正常完成，我们运行该任务，收集其抛出的任何异常以发送到afterExecute。
//     我们分别处理RuntimeException，Error（这两个规范保证我们陷阱）和任意Throwables。
//    因为我们不能在Runnable.run内重新抛出Throwables，
//      所以我们把它们包裹在错误的路上（到线程的UncaughtExceptionHandler）。
//    任何抛出的异常也保守地导致线程死亡。
//
//    5. 在task.run完成后，我们调用afterExecute，这也可能会引发异常，这也会导致线程死机。 根据JLS Sec 14.20，这个异常是即使task.run抛出也将生效的例外。
//
//     异常机制的净效果是afterExecute和线程的UncaughtExceptionHandler具有我们可以提供用户代码遇到的任何问题的准确信息。
//     *
//     * 参数 w
//     * the worker
//     */
//    final void runWorker(Worker w) {
//        Thread wt = Thread.currentThread();
//        Runnable task = w.firstTask;
//        w.firstTask = null;
//        w.unlock(); // 允许中断
//        boolean completedAbruptly = true;
//        try {
//            while (task != null || (task = getTask()) != null) {
//                w.lock();
////                如果池停止，请确保线程中断; 如果没有，确保线程不中断。
////                这需要在第二种情况下重新检查以在清除中断时处理shutdownNow比赛
//                if ((runStateAtLeast(ctl.get(), STOP) ||
//                        (Thread.interrupted() &&
//                                runStateAtLeast(ctl.get(), STOP))) &&
//                        !wt.isInterrupted())
//                    wt.interrupt();
//                try {
//                    beforeExecute(wt, task);
//                    Throwable thrown = null;
//                    try {
//                        task.run();
//                    } catch (RuntimeException x) {
//                        thrown = x; throw x;
//                    } catch (Error x) {
//                        thrown = x; throw x;
//                    } catch (Throwable x) {
//                        thrown = x; throw new Error(x);
//                    } finally {
//                        afterExecute(task, thrown);
//                    }
//                } finally {
//                    task = null;
//                    w.completedTasks++;
//                    w.unlock();
//                }
//            }
//            completedAbruptly = false;
//        } finally {
//            processWorkerExit(w, completedAbruptly);
//        }
//    }
//
//    // 公共构造函数和方法
//
//    /**
//     用给定的初始参数和默认的线程工厂及被拒绝的执行处理程序创建新的 ThreadPoolExecutor。使用 Executors 工厂方法之一比使用此通用构造方法方便得多。
//     参数：
//     corePoolSize - 池中所保存的线程数，包括空闲线程。
//     maximumPoolSize - 池中允许的最大线程数。
//     keepAliveTime - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
//     unit - keepAliveTime 参数的时间单位。
//     workQueue - 执行前用于保持任务的队列。此队列仅保持由 execute 方法提交的 Runnable 任务。
//     抛出：
//     IllegalArgumentException - 如果 corePoolSize 或 keepAliveTime 小于 0，或者 maximumPoolSize 小于等于 0，或者 corePoolSize 大于 maximumPoolSize。
//     NullPointerException - 如果 workQueue 为 null
//     */
//    public ThreadPoolExecutor(int corePoolSize,
//                              int maximumPoolSize,
//                              long keepAliveTime,
//                              TimeUnit unit,
//                              BlockingQueue<Runnable> workQueue) {
//        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
//                Executors.defaultThreadFactory(), defaultHandler);
//    }
//
//    /**
//     用给定的初始参数和默认被拒绝的执行处理程序创建新的 ThreadPoolExecutor。
//     参数：
//     corePoolSize - 池中所保存的线程数，包括空闲线程。
//     maximumPoolSize - 池中允许的最大线程数。
//     keepAliveTime - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
//     unit - keepAliveTime 参数的时间单位。
//     workQueue - 执行前用于保持任务的队列。此队列仅保持由 execute 方法提交的 Runnable 任务。
//     threadFactory - 执行程序创建新线程时使用的工厂。
//     抛出：
//     IllegalArgumentException - 如果 corePoolSize 或 keepAliveTime 小于 0，或者 maximumPoolSize 小于等于 0，或者 corePoolSize 大于 maximumPoolSize。
//     NullPointerException - 如果 workQueue 或 threadFactory 为 null。
//     */
//    public ThreadPoolExecutor(int corePoolSize,
//                              int maximumPoolSize,
//                              long keepAliveTime,
//                              TimeUnit unit,
//                              BlockingQueue<Runnable> workQueue,
//                              ThreadFactory threadFactory) {
//        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
//                threadFactory, defaultHandler);
//    }
//
//    /**
//     用给定的初始参数和默认的线程工厂创建新的 ThreadPoolExecutor。
//     参数：
//     corePoolSize - 池中所保存的线程数，包括空闲线程。
//     maximumPoolSize - 池中允许的最大线程数。
//     keepAliveTime - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
//     unit - keepAliveTime 参数的时间单位。
//     workQueue - 执行前用于保持任务的队列。此队列仅由保持 execute 方法提交的 Runnable 任务。
//     handler - 由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序。
//     抛出：
//     IllegalArgumentException - 如果 corePoolSize 或 keepAliveTime 小于 0，或者 maximumPoolSize 小于等于 0，或者 corePoolSize 大于 maximumPoolSize。
//     NullPointerException - 如果 workQueue 或 handler 为 null。
//     */
//    public ThreadPoolExecutor(int corePoolSize,
//                              int maximumPoolSize,
//                              long keepAliveTime,
//                              TimeUnit unit,
//                              BlockingQueue<Runnable> workQueue,
//                              RejectedExecutionHandler handler) {
//        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
//                Executors.defaultThreadFactory(), handler);
//    }
//
//    /**
//     用给定的初始参数创建新的 ThreadPoolExecutor。
//     参数：
//     corePoolSize - 池中所保存的线程数，包括空闲线程。
//     maximumPoolSize - 池中允许的最大线程数。
//     keepAliveTime - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
//     unit - keepAliveTime 参数的时间单位。
//     workQueue - 执行前用于保持任务的队列。此队列仅保持由 execute 方法提交的 Runnable 任务。
//     threadFactory - 执行程序创建新线程时使用的工厂。
//     handler - 由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序。
//     抛出：
//     IllegalArgumentException - 如果 corePoolSize 或 keepAliveTime 小于 0，或者 maximumPoolSize 小于等于 0，或者 corePoolSize 大于 maximumPoolSize。
//     NullPointerException - 如果 workQueue、threadFactory 或 handler 为 null。
//     */
//    public ThreadPoolExecutor(int corePoolSize,
//                              int maximumPoolSize,
//                              long keepAliveTime,
//                              TimeUnit unit,
//                              BlockingQueue<Runnable> workQueue,
//                              ThreadFactory threadFactory,
//                              RejectedExecutionHandler handler) {
//        if (corePoolSize < 0 ||
//                maximumPoolSize <= 0 ||
//                maximumPoolSize < corePoolSize ||
//                keepAliveTime < 0)
//            throw new IllegalArgumentException();
//        if (workQueue == null || threadFactory == null || handler == null)
//            throw new NullPointerException();
//        this.corePoolSize = corePoolSize;
//        this.maximumPoolSize = maximumPoolSize;
//        this.workQueue = workQueue;
//        this.keepAliveTime = unit.toNanos(keepAliveTime);
//        this.threadFactory = threadFactory;
//        this.handler = handler;
//    }
//
//    /**
//     在将来某个时间执行给定任务。可以在新线程中或者在现有池线程中执行该任务。 如果无法将任务提交执行，或者因为此执行程序已关闭，或者因为已达到其容量，则该任务由当前 RejectedExecutionHandler 处理。
//     参数：
//     command - 要执行的任务。
//     抛出：
//     RejectedExecutionException - 如果无法接收要执行的任务，则由 RejectedExecutionHandler 决定是否抛出 RejectedExecutionException
//     NullPointerException - 如果命令为 null
//     */
//    public void execute(Runnable command) {
//        if (command == null)
//            throw new NullPointerException();
//        /*
//         * 继续进行3个步骤：
//         *
//         * 1.如果少于corePoolSize线程正在运行，尝试使用给定命令作为其第一个任务启动一个新线程。
//          * 对addWorker的调用原子地检查runState和workerCount，
//          * 从而防止当不应该通过返回false来添加线程的虚假警报。
//         *
//         * 2. 如果一个任务可以成功排队，那么我们还需要仔细检查一下是否应该添加一个线程（因为自从上一次检查以来已经存在了一个线程），或者是从进入该方法后该池关闭了。
//         * 所以我们重新检查状态，如有必要，如果停止，则回滚入队，或者如果没有，则启动新线程。
//         *
//         * 3. 如果我们无法排队任务，那么我们尝试添加一个新线程。 如果失败，我们知道我们被关闭或饱和，所以拒绝任务。
//         */
//        int c = ctl.get();
//        if (workerCountOf(c) < corePoolSize) {
//            if (addWorker(command, true))
//                return;
//            c = ctl.get();
//        }
//        if (isRunning(c) && workQueue.offer(command)) {
//            int recheck = ctl.get();
//            if (! isRunning(recheck) && remove(command))
//                reject(command);
//            else if (workerCountOf(recheck) == 0)
//                addWorker(null, false);
//        }
//        else if (!addWorker(command, false))
//            reject(command);
//    }
//
//    /**
//     按过去执行已提交任务的顺序发起一个有序的关闭，但是不接受新任务。如果已经关闭，则调用没有其他作用。
//     抛出：
//     SecurityException - 如果安全管理器存在并且关闭此 ExecutorService 可能操作某些不允许调用者修改的线程（因为它没有 RuntimePermission("modifyThread")），或者安全管理器的 checkAccess 方法拒绝访问。
//     */
//    // android-note: Removed @throws SecurityException
//    public void shutdown() {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            checkShutdownAccess();
//            advanceRunState(SHUTDOWN);
//            interruptIdleWorkers();
//            onShutdown(); // hook for ScheduledThreadPoolExecutor
//        } finally {
//            mainLock.unlock();
//        }
//        tryTerminate();
//    }
//
//    /**
//     尝试停止所有的活动执行任务、暂停等待任务的处理，并返回等待执行的任务列表。在从此方法返回的任务队列中排空（移除）这些任务。
//     并不保证能够停止正在处理的活动执行任务，但是会尽力尝试。 此实现通过 Thread.interrupt() 取消任务，所以无法响应中断的任何任务可能永远无法终止。
//
//     返回：
//     从未开始执行的任务的列表。
//     抛出：
//     SecurityException - 如果安全管理器存在并且关闭此 ExecutorService 可能操作某些不允许调用者修改的线程（因为它没有 RuntimePermission("modifyThread")），或者安全管理器的 checkAccess 方法拒绝访问。
//     */
//    // android-note: Removed @throws SecurityException
//    public List<Runnable> shutdownNow() {
//        List<Runnable> tasks;
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            checkShutdownAccess();
//            advanceRunState(STOP);
//            interruptWorkers();
//            tasks = drainQueue();
//        } finally {
//            mainLock.unlock();
//        }
//        tryTerminate();
//        return tasks;
//    }
//
//    /**
//     如果此执行程序已关闭，则返回 true。
//     返回：
//     如果此执行程序已关闭，则返回 true
//     */
//    public boolean isShutdown() {
//        return ! isRunning(ctl.get());
//    }
//
//    /**
//     如果此执行程序处于在 shutdown 或 shutdownNow 之后正在终止但尚未完全终止的过程中，则返回 true。
//     此方法可能对调试很有用。
//     关闭之后很长一段时间才报告返回的 true，这可能表示提交的任务已经被忽略或取消中断，导致此执行程序无法正确终止。
//     返回：
//     如果正在终止但尚未完成，则返回 true
//     */
//    public boolean isTerminating() {
//        int c = ctl.get();
//        return ! isRunning(c) && runStateLessThan(c, TERMINATED);
//    }
//
//    /*
//    如果关闭后所有任务都已完成，则返回 true。注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。
//    返回：
//    如果关闭后所有任务都已完成，则返回 true
//     */
//    public boolean isTerminated() {
//        return runStateAtLeast(ctl.get(), TERMINATED);
//    }
//
//    /**
//     * 请求关闭、发生超时或者当前线程中断，无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行。
//     参数：
//     timeout - 最长等待时间
//     unit - timeout 参数的时间单位
//     返回：
//     如果此执行程序终止，则返回 true；如果终止前超时期满，则返回 false
//     抛出：
//     InterruptedException - 如果等待时发生中断
//     */
//    public boolean awaitTermination(long timeout, TimeUnit unit)
//            throws InterruptedException {
//        long nanos = unit.toNanos(timeout);
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            while (!runStateAtLeast(ctl.get(), TERMINATED)) {
//                if (nanos <= 0L)
//                    return false;
//                nanos = termination.awaitNanos(nanos);
//            }
//            return true;
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     当不再引用此执行程序时，调用 shutdown。
//     */
//    protected void finalize() {
//        shutdown();
//    }
//
//    /**
//     设置用于创建新线程的线程工厂。
//     参数：
//     threadFactory - 新线程工厂
//     抛出：
//     NullPointerException - 如果 threadFactory 为 null
//     另请参见：
//     getThreadFactory()
//     */
//    public void setThreadFactory(ThreadFactory threadFactory) {
//        if (threadFactory == null)
//            throw new NullPointerException();
//        this.threadFactory = threadFactory;
//    }
//
//    /**
//     返回用于创建新线程的线程工厂。
//     返回：
//     当前线程工厂
//     另请参见：
//     setThreadFactory(java.util.concurrent.ThreadFactory)
//     */
//    public ThreadFactory getThreadFactory() {
//        return threadFactory;
//    }
//
//    /**
//     设置用于未执行任务的新处理程序。
//     参数：
//     handler - 新处理程序
//     抛出：
//     NullPointerException - 如果处理程序为 null
//     另请参见：
//     getRejectedExecutionHandler()
//     */
//    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
//        if (handler == null)
//            throw new NullPointerException();
//        this.handler = handler;
//    }
//
//    /**
//     返回用于未执行任务的当前处理程序。
//     返回：
//     当前处理程序
//     另请参见：
//     setRejectedExecutionHandler(java.util.concurrent.RejectedExecutionHandler)
//     */
//    public RejectedExecutionHandler getRejectedExecutionHandler() {
//        return handler;
//    }
//
//    /**
//     设置核心线程数。此操作将重写构造方法中设置的任何值。如果新值小于当前值，则多余的现有线程将在下一次空闲时终止。如果较大，则在需要时启动新线程来执行这些排队的任务。
//     参数：
//     corePoolSize - 新核心大小
//     抛出：
//     IllegalArgumentException - 如果 corePoolSize 小于 0
//     另请参见：
//     getCorePoolSize()
//     */
//    // Android-changed:
////    当{@code corePoolSize}大于{@linkplain #getMaximumPoolSize（）最大池大小}时，转换IAE的代码。
////    这是由于常用的第三方库中的代码有问题，它们的作用如下：
//    //
//    // exec.setCorePoolSize(N);
//    // exec.setMaxPoolSize(N);
//    public void setCorePoolSize(int corePoolSize) {
//        if (corePoolSize < 0)
//            throw new IllegalArgumentException();
//        int delta = corePoolSize - this.corePoolSize;
//        this.corePoolSize = corePoolSize;
//        if (workerCountOf(ctl.get()) > corePoolSize)
//            interruptIdleWorkers();
//        else if (delta > 0) {
////            我们不知道有多少新线程是“需要的”。
////            作为启发式，预备好足够的新员工（达到新的核心大小）来处理队列中当前的任务数量，但如果队列变空则停止。
//            int k = Math.min(delta, workQueue.size());
//            while (k-- > 0 && addWorker(null, true)) {
//                if (workQueue.isEmpty())
//                    break;
//            }
//        }
//    }
//
//    /**
//     返回核心线程数。
//     返回：
//     核心线程数
//     另请参见：
//     setCorePoolSize(int)
//     */
//    public int getCorePoolSize() {
//        return corePoolSize;
//    }
//
//    /**
//     启动核心线程，使其处于等待工作的空闲状态。仅当执行新任务时，此操作才重写默认的启动核心线程策略。如果已启动所有核心线程，此方法将返回 false。
//     返回：
//     如果启动了线程，则返回 true
//     */
//    public boolean prestartCoreThread() {
//        return workerCountOf(ctl.get()) < corePoolSize &&
//                addWorker(null, true);
//    }
//
//    /**
//     与prestartCoreThread相同，除了排列至少有一个线程启动，即使corePoolSize为0
//     */
//    void ensurePrestart() {
//        int wc = workerCountOf(ctl.get());
//        if (wc < corePoolSize)
//            addWorker(null, true);
//        else if (wc == 0)
//            addWorker(null, false);
//    }
//
//    /**
//     启动所有核心线程，使其处于等待工作的空闲状态。仅当执行新任务时，此操作才重写默认的启动核心线程策略。
//     返回：
//     已启动的线程数
//     */
//    public int prestartAllCoreThreads() {
//        int n = 0;
//        while (addWorker(null, true))
//            ++n;
//        return n;
//    }
//
//    /**
//     如果此池允许核心线程超时和终止，如果在 keepAlive 时间内没有任务到达，新任务到达时正在替换（如果需要），则返回 true。
//     当返回 true 时，适用于非核心线程的相同的保持活动策略也同样适用于核心线程。
//     当返回 false（默认值）时，由于没有传入任务，核心线程不会终止。
//     返回：
//     如果允许核心线程超时，则返回 true；否则返回 false
//     从以下版本开始：
//     1.6
//     */
//    public boolean allowsCoreThreadTimeOut() {
//        return allowCoreThreadTimeOut;
//    }
//
//    /**
//     如果在保持活动时间内没有任务到达，新任务到达时正在替换（如果需要），则设置控制核心线程是超时还是终止的策略。
//     当为 false（默认值）时，由于没有传入任务，核心线程将永远不会中止。当为 true 时，适用于非核心线程的相同的保持活动策略也同样适用于核心线程。
//     为了避免连续线程替换，保持活动时间在设置为 true 时必须大于 0。
//     通常应该在主动使用该池前调用此方法。
//     参数：
//     value - 如果应该超时，则为 true；否则为 false
//     抛出：
//     IllegalArgumentException - 如果 value 为 true 并且当前保持活动时间不大于 0。
//     从以下版本开始：
//     */
//    public void allowCoreThreadTimeOut(boolean value) {
//        if (value && keepAliveTime <= 0)
//            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
//        if (value != allowCoreThreadTimeOut) {
//            allowCoreThreadTimeOut = value;
//            if (value)
//                interruptIdleWorkers();
//        }
//    }
//
//    /**
//     设置允许的最大线程数。此操作将重写构造方法中设置的任何值。如果新值小于当前值，则多余的现有线程将在下一次空闲时终止。
//     参数：
//     maximumPoolSize - 新的最大值
//     抛出：
//     IllegalArgumentException - 如果新的最大值小于等于 0，或者小于核心池大小
//     另请参见：
//     getMaximumPoolSize()
//     */
//    public void setMaximumPoolSize(int maximumPoolSize) {
//        if (maximumPoolSize <= 0 || maximumPoolSize < corePoolSize)
//            throw new IllegalArgumentException();
//        this.maximumPoolSize = maximumPoolSize;
//        if (workerCountOf(ctl.get()) > maximumPoolSize)
//            interruptIdleWorkers();
//    }
//
//    /**
//     返回允许的最大线程数。
//     返回：
//     允许的最大线程数
//     另请参见：
//     setMaximumPoolSize(int)
//     */
//    public int getMaximumPoolSize() {
//        return maximumPoolSize;
//    }
//
//    /**
//     设置线程在终止前可以保持空闲的时间限制。如果池中的当前线程数多于核心线程数，在不处理任务的情况下等待这一时间段之后，多余的线程将被终止。
//     此操作将重写构造方法中设置的任何值。
//     参数：
//     time - 等待的时间。时间值 0 将导致执行任务后多余的线程立即终止。
//     unit - 时间参数的时间单位
//     抛出：
//     IllegalArgumentException - 如果时间小于 0，或者时间为 0 和 allowsCoreThreadTimeOut
//     另请参见：
//     */
//    public void setKeepAliveTime(long time, TimeUnit unit) {
//        if (time < 0)
//            throw new IllegalArgumentException();
//        if (time == 0 && allowsCoreThreadTimeOut())
//            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
//        long keepAliveTime = unit.toNanos(time);
//        long delta = keepAliveTime - this.keepAliveTime;
//        this.keepAliveTime = keepAliveTime;
//        if (delta < 0)
//            interruptIdleWorkers();
//    }
//
//    /**
//     返回线程保持活动的时间，该时间就是超过核心池大小的线程可以在终止前保持空闲的时间值。
//     参数：
//     unit - 所需的结果时间单位
//     返回：
//     时间限制
//     另请参见：
//     setKeepAliveTime(long, java.util.concurrent.TimeUnit)
//
//     */
//    public long getKeepAliveTime(TimeUnit unit) {
//        return unit.convert(keepAliveTime, TimeUnit.NANOSECONDS);
//    }
//
//    /* User-level queue utilities */
//
//    /**
//     返回此执行程序使用的任务队列。对任务队列的访问主要用于调试和监控。
//     此队列可能正处于活动使用状态中。
//     获取任务队列不妨碍已加入队列的任务的执行。
//     返回：
//     任务队列
//     */
//    public BlockingQueue<Runnable> getQueue() {
//        return workQueue;
//    }
//
//    /**
//     从执行程序的内部队列中移除此任务（如果存在），从而如果尚未开始，则其不再运行。
//     此方法可用作取消方案的一部分。它可能无法移除在放置到内部队列之前已经转换为其他形式的任务。例如，使用 submit 输入的任务可能被转换为维护 Future 状态的形式。但是，在此情况下，purge() 方法可用于移除那些已被取消的 Future。
//
//     参数：
//     task - 要移除的任务
//     返回：
//     如果已经移除任务，则返回 true
//     */
//    public boolean remove(Runnable task) {
//        boolean removed = workQueue.remove(task);
//        tryTerminate(); // In case SHUTDOWN and now empty
//        return removed;
//    }
//
//    /**
//     尝试从工作队列移除所有已取消的 Future 任务。
//     此方法可用作存储回收操作，它对功能没有任何影响。
//     取消的任务不会再次执行，但是它们可能在工作队列中累积，直到 worker 线程主动将其移除。
//     调用此方法将试图立即移除它们。
//     但是，如果出现其他线程的干预，那么此方法移除任务将失败。
//
//     */
//    public void purge() {
//        final BlockingQueue<Runnable> q = workQueue;
//        try {
//            Iterator<Runnable> it = q.iterator();
//            while (it.hasNext()) {
//                Runnable r = it.next();
//                if (r instanceof Future<?> && ((Future<?>)r).isCancelled())
//                    it.remove();
//            }
//        } catch (ConcurrentModificationException fallThrough) {
//            // 如果在穿越过程中遇到干扰，请采取缓慢的路径。
//            // 复制进行遍历并调用取消的条目。慢路径更可能是O（N * N）。
//            for (Object r : q.toArray())
//                if (r instanceof Future<?> && ((Future<?>)r).isCancelled())
//                    q.remove(r);
//        }
//
//        tryTerminate(); // In case SHUTDOWN and now empty
//    }
//
//    /* Statistics */
//
//    /**
//     返回池中的当前线程数。
//     返回：
//     线程数。
//     */
//    public int getPoolSize() {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            // Remove rare and surprising possibility of
//            // isTerminated() && getPoolSize() > 0
//            return runStateAtLeast(ctl.get(), TIDYING) ? 0
//                    : workers.size();
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     返回主动执行任务的近似线程数。
//     返回：
//     线程数。
//     */
//    public int getActiveCount() {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            int n = 0;
//            for (Worker w : workers)
//                if (w.isLocked())
//                    ++n;
//            return n;
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     返回曾经同时位于池中的最大线程数。
//     返回：
//     线程数。
//
//     */
//    public int getLargestPoolSize() {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            return largestPoolSize;
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     返回曾计划执行的近似任务总数。因为在计算期间任务和线程的状态可能动态改变，所以返回值只是一个近似值。
//     返回：
//     任务数
//     */
//    public long getTaskCount() {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            long n = completedTaskCount;
//            for (Worker w : workers) {
//                n += w.completedTasks;
//                if (w.isLocked())
//                    ++n;
//            }
//            return n + workQueue.size();
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     返回已完成执行的近似任务总数。因为在计算期间任务和线程的状态可能动态改变，所以返回值只是一个近似值，但是该值在整个连续调用过程中不会减少。
//     返回：
//     任务数。
//     */
//    public long getCompletedTaskCount() {
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            long n = completedTaskCount;
//            for (Worker w : workers)
//                n += w.completedTasks;
//            return n;
//        } finally {
//            mainLock.unlock();
//        }
//    }
//
//    /**
//     * 返回标识此池的字符串及其状态，包括运行状态和估计的工作人员和任务计数的指示。
//     *
//     * @return 一个标识这个池的字符串，以及它的状态
//     */
//    public String toString() {
//        long ncompleted;
//        int nworkers, nactive;
//        final ReentrantLock mainLock = this.mainLock;
//        mainLock.lock();
//        try {
//            ncompleted = completedTaskCount;
//            nactive = 0;
//            nworkers = workers.size();
//            for (Worker w : workers) {
//                ncompleted += w.completedTasks;
//                if (w.isLocked())
//                    ++nactive;
//            }
//        } finally {
//            mainLock.unlock();
//        }
//        int c = ctl.get();
//        String runState =
//                runStateLessThan(c, SHUTDOWN) ? "Running" :
//                        runStateAtLeast(c, TERMINATED) ? "Terminated" :
//                                "Shutting down";
//        return super.toString() +
//                "[" + runState +
//                ", pool size = " + nworkers +
//                ", active threads = " + nactive +
//                ", queued tasks = " + workQueue.size() +
//                ", completed tasks = " + ncompleted +
//                "]";
//    }
//
//    /* Extension hooks */
//
//    /**
//     在执行给定线程中的给定 Runnable 之前调用的方法。此方法由将执行任务 r 的线程 t 调用，并且可用于重新初始化 ThreadLocals 或者执行日志记录。
//     此实现不执行任何操作，但可在子类中定制。注：为了正确嵌套多个重写操作，此方法结束时，子类通常应该调用 super.beforeExecute。
//
//     参数：
//     t - 将运行任务 r 的线程。
//     r - 将执行的任务。
//     */
//    protected void beforeExecute(Thread t, Runnable r) { }
//
//    /**
//     基于完成执行给定 Runnable 所调用的方法。此方法由执行任务的线程调用。如果非 null，则 Throwable 是导致执行突然终止的未捕获 RuntimeException 或 Error。
//     注：当操作显示地或者通过 submit 之类的方法包含在任务内时（如 FutureTask），这些任务对象捕获和维护计算异常，因此它们不会导致突然终止，内部异常不会 传递给此方法。
//
//     此实现不执行任何操作，但可在子类中定制。注：为了正确嵌套多个重写操作，此方法开始时，子类通常应该调用 super.afterExecute。
//
//     参数：
//     r - 已经完成的 runnable 线程。
//     t - 导致终止的异常；如果执行正常结束，则为 null。
//     *
//     * <p><b>Note:</b>
//     * 当操作被明确地包含在任务（如{@link FutureTask}）中或通过诸如{@code submit}的方法包含在内）时，
//     * 这些任务对象捕获并维护计算异常，因此它们不会引起突然终止，
//     * 并且内部异常 是<em>不</ em>传递给此方法。
//     * 如果您希望在此方法中捕获两种故障，您可以进一步探测这种情况，如在此示例子类中，
//     * 如果任务已中止，则会打印直接原因或底层异常：
//     *
//     * <pre> {@code
//     * class ExtendedExecutor extends ThreadPoolExecutor {
//     *   // ...
//     *   protected void afterExecute(Runnable r, Throwable t) {
//     *     super.afterExecute(r, t);
//     *     if (t == null
//     *         && r instanceof Future<?>
//     *         && ((Future<?>)r).isDone()) {
//     *       try {
//     *         Object result = ((Future<?>) r).get();
//     *       } catch (CancellationException ce) {
//     *         t = ce;
//     *       } catch (ExecutionException ee) {
//     *         t = ee.getCause();
//     *       } catch (InterruptedException ie) {
//     *         // ignore/reset
//     *         Thread.currentThread().interrupt();
//     *       }
//     *     }
//     *     if (t != null)
//     *       System.out.println(t);
//     *   }
//     * }}</pre>
//     *
//     * @param r 已经完成的runnable
//     * @param t 导致终止的异常，如果执行正常完成，则为null
//     */
//    protected void afterExecute(Runnable r, Throwable t) { }
//
//    /**
//     当 Executor 已经终止时调用的方法。默认实现不执行任何操作。注：为了正确嵌套多个重写操作，子类通常应该在此方法中调用 super.afterExecute。
//     */
//    protected void terminated() { }
//
//    /* Predefined RejectedExecutionHandlers */
//
//    /**
//     用于被拒绝任务的处理程序，它直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务。
//     */
//    public static class CallerRunsPolicy implements RejectedExecutionHandler {
//        /**
//         * 创建一个{@code CallerRunsPolicy}.
//         */
//        public CallerRunsPolicy() { }
//
//        /**
//         * 执行调用者线程中的任务r，除非执行程序已经被关闭，否则任务被丢弃。
//         *
//         * @param r 请求执行的可运行任务
//         * @param e 执行者试图执行这个任务
//         */
//        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
//            if (!e.isShutdown()) {
//                r.run();
//            }
//        }
//    }
//
//    /**
//     用于被拒绝任务的处理程序，它将抛出 RejectedExecutionException.
//     */
//    public static class AbortPolicy implements RejectedExecutionHandler {
//        /**
//         * Creates an {@code AbortPolicy}.
//         */
//        public AbortPolicy() { }
//
//        /**
//         * 总是抛出RejectedExecutionException。
//         *
//         * @param r 请求执行的可运行任务
//         * @param e 执行者试图执行这个任务
//         * @throws RejectedExecutionException always
//         */
//        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
//            throw new RejectedExecutionException("Task " + r.toString() +
//                    " rejected from " +
//                    e.toString());
//        }
//    }
//
//    /**
//     用于被拒绝任务的处理程序，默认情况下它将丢弃被拒绝的任务。
//     */
//    public static class DiscardPolicy implements RejectedExecutionHandler {
//        /**
//         * 创建一个 {@code DiscardPolicy}.
//         */
//        public DiscardPolicy() { }
//
//        /**
//         * 什么也没有，具有丢弃任务r的效果。
//         *
//         * @param r 请求执行的可运行任务
//         * @param e 执行者试图执行这个任务
//         */
//        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
//        }
//    }
//
//    /**
//     用于被拒绝任务的处理程序，它放弃最旧的未处理请求，然后重试 execute；如果执行程序已关闭，则会丢弃该任务。
//     */
//    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
//        /**
//         * 为给定的执行者创建{@code DiscardOldestPolicy}。
//         */
//        public DiscardOldestPolicy() { }
//
//        /**
//         * 获取并忽略执行者否则将执行的下一个任务，如果一个可以立即可用，
//         * 然后重试任务r的执行，除非执行程序关闭，否则任务r被替换为丢弃。
//         *
//         * @param r 请求执行的可运行任务
//         * @param e 执行者试图执行这个任务
//         */
//        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
//            if (!e.isShutdown()) {
//                e.getQueue().poll();
//                e.execute(r);
//            }
//        }
//    }
//}