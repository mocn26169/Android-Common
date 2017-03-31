///*
// * Written by Doug Lea with assistance from members of JCP JSR-166
// * Expert Group and released to the public domain, as explained at
// * http://creativecommons.org/publicdomain/zero/1.0/
// */
//
//package java.util.concurrent;
//
///**
// * An {@link ExecutorService} that can schedule commands to run after a given
// * delay, or to execute periodically.
// *
// * <p>The {@code schedule} methods create tasks with various delays
// * and return a task object that can be used to cancel or check
// * execution. The {@code scheduleAtFixedRate} and
// * {@code scheduleWithFixedDelay} methods create and execute tasks
// * that run periodically until cancelled.
// *
// * <p>Commands submitted using the {@link Executor#execute(Runnable)}
// * and {@link ExecutorService} {@code submit} methods are scheduled
// * with a requested delay of zero. Zero and negative delays (but not
// * periods) are also allowed in {@code schedule} methods, and are
// * treated as requests for immediate execution.
// *
// * <p>All {@code schedule} methods accept <em>relative</em> delays and
// * periods as arguments, not absolute times or dates. It is a simple
// * matter to transform an absolute time represented as a {@link
// * java.util.Date} to the required form. For example, to schedule at
// * a certain future {@code date}, you can use: {@code schedule(task,
// * date.getTime() - System.currentTimeMillis(),
// * TimeUnit.MILLISECONDS)}. Beware however that expiration of a
// * relative delay need not coincide with the current {@code Date} at
// * which the task is enabled due to network time synchronization
// * protocols, clock drift, or other factors.
// *
// * <p>The {@link Executors} class provides convenient factory methods for
// * the ScheduledExecutorService implementations provided in this package.
// *
// * <h3>Usage Example</h3>
// *
// * Here is a class with a method that sets up a ScheduledExecutorService
// * to beep every ten seconds for an hour:
// *
// * <pre> {@code
// * import static java.util.concurrent.TimeUnit.*;
// * class BeeperControl {
// *   private final ScheduledExecutorService scheduler =
// *     Executors.newScheduledThreadPool(1);
// *
// *   public void beepForAnHour() {
// *     final Runnable beeper = new Runnable() {
// *       public void run() { System.out.println("beep"); }
// *     };
// *     final ScheduledFuture<?> beeperHandle =
// *       scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
// *     scheduler.schedule(new Runnable() {
// *       public void run() { beeperHandle.cancel(true); }
// *     }, 60 * 60, SECONDS);
// *   }
// * }}</pre>
// *
// * @since 1.5
// * @author Doug Lea
// */
//public interface ScheduledExecutorService extends ExecutorService {
//
//    /**
//     创建并执行在给定延迟后启用的一次性操作。
//     参数：
//     command - 要执行的任务
//     delay - 从现在开始延迟执行的时间
//     unit - 延迟参数的时间单位
//     返回：
//     表示挂起任务完成的 ScheduledFuture，并且其 get() 方法在完成后将返回 null
//     抛出：
//     RejectedExecutionException - 如果无法安排执行该任务
//     NullPointerException - 如果 command 为 null
//     */
//    public ScheduledFuture<?> schedule(Runnable command,
//                                       long delay, TimeUnit unit);
//
//    /**
//     创建并执行在给定延迟后启用的 ScheduledFuture。
//     参数：
//     callable - 要执行的功能
//     delay - 从现在开始延迟执行的时间
//     unit - 延迟参数的时间单位
//     返回：
//     可用于提取结果或取消的 ScheduledFuture
//     抛出：
//     RejectedExecutionException - 如果无法安排执行该任务
//     NullPointerException - 如果 callable 为 null
//     */
//    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
//                                           long delay, TimeUnit unit);
//
//    /**
//     创建并执行一个在给定初始延迟后首次启用的定期操作，后续操作具有给定的周期；
//     也就是将在 initialDelay 后开始执行，然后在 initialDelay+period 后执行，接着在 initialDelay + 2 * period 后执行，依此类推。
//     如果任务的任何一个执行遇到异常，则后续执行都会被取消。否则，只能通过执行程序的取消或终止方法来终止该任务。
//     如果此任务的任何一个执行要花费比其周期更长的时间，则将推迟后续执行，但不会同时执行。
//     参数：
//     command - 要执行的任务
//     initialDelay - 首次执行的延迟时间
//     period - 连续执行之间的周期
//     unit - initialDelay 和 period 参数的时间单位
//     返回：
//     表示挂起任务完成的 ScheduledFuture，并且其 get() 方法在取消后将抛出异常
//     抛出：
//     RejectedExecutionException - 如果无法安排执行该任务
//     NullPointerException - 如果 command 为 null
//     IllegalArgumentException - 如果 period 小于等于 0
//
//     */
//    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
//                                                  long initialDelay,
//                                                  long period,
//                                                  TimeUnit unit);
//
//    /**
//     创建并执行一个在给定初始延迟后首次启用的定期操作，
//     随后，在每一次执行终止和下一次执行开始之间都存在给定的延迟。
//     如果任务的任一执行遇到异常，就会取消后续执行。
//     否则，只能通过执行程序的取消或终止方法来终止该任务。
//     参数：
//     command - 要执行的任务
//     initialDelay - 首次执行的延迟时间
//     delay - 一次执行终止和下一次执行开始之间的延迟
//     unit - initialDelay 和 delay 参数的时间单位
//     返回：
//     表示挂起任务完成的 ScheduledFuture，并且其 get() 方法在取消后将抛出异常
//     抛出：
//     RejectedExecutionException - 如果无法安排执行该任务
//     NullPointerException - 如果 command 为 null。
//     IllegalArgumentException - 如果 delay 小于等于 0
//     */
//    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
//                                                     long initialDelay,
//                                                     long delay,
//                                                     TimeUnit unit);
//
//}
