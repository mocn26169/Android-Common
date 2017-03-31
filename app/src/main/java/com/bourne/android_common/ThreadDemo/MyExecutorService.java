//package java.util.concurrent;
//
//import java.util.Collection;
//import java.util.List;
//
///**
// Executor 提供了管理终止的方法，以及可为跟踪一个或多个异步任务执行状况而生成 Future 的方法。
//
// 可以关闭 ExecutorService，这将导致其拒绝新任务。提供两个方法来关闭 ExecutorService。shutdown() 方法在终止前允许执行以前提交的任务，而 shutdownNow() 方法阻止等待任务启动并试图停止当前正在执行的任务。在终止时，执行程序没有任务在执行，也没有任务在等待执行，并且无法提交新任务。应该关闭未使用的 ExecutorService 以允许回收其资源。
//
// 通过创建并返回一个可用于取消执行和/或等待完成的 Future，方法 submit 扩展了基本方法 Executor.execute(Java.lang.Runnable)。方法 invokeAny 和 invokeAll 是批量执行的最常用形式，它们执行任务 collection，然后等待至少一个，或全部任务完成（可使用 ExecutorCompletionService 类来编写这些方法的自定义变体）。
//
// Executors 类提供了用于此包中所提供的执行程序服务的工厂方法。
//
// 用法示例
//
// 下面给出了一个网络服务的简单结构，这里线程池中的线程作为传入的请求。它使用了预先配置的
// Executors.newFixedThreadPool(int) 工厂方法：
//
// class NetworkService implements Runnable {
// private final ServerSocket serverSocket;
// private final ExecutorService pool;
//
// public NetworkService(int port, int poolSize)
// throws IOException {
// serverSocket = new ServerSocket(port);
// pool = Executors.newFixedThreadPool(poolSize);
// }
//
// public void run() { // run the service
// try {
// for (;;) {
// pool.execute(new Handler(serverSocket.accept()));
// }
// } catch (IOException ex) {
// pool.shutdown();
// }
// }
// }
//
// class Handler implements Runnable {
// private final Socket socket;
// Handler(Socket socket) { this.socket = socket; }
// public void run() {
// // read and service request on socket
// }
// }
//
// 下列方法分两个阶段关闭 ExecutorService。第一阶段调用 shutdown 拒绝传入任务，然后调用 shutdownNow（如有必要）取消所有遗留的任务：
//
// void shutdownAndAwaitTermination(ExecutorService pool) {
// pool.shutdown(); // 禁止提交新任务
// try {
// // 等待一段时间，现有任务终止
// if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
// pool.shutdownNow(); // 取消当前正在执行的任务
// // 等待一段时间的任务回应被取消
// if (!pool.awaitTermination(60, TimeUnit.SECONDS))
// System.err.println("Pool did not terminate");
// }
// } catch (InterruptedException ie) {
// // （重新）如果当前线程也中断，则取消
// pool.shutdownNow();
// // 保留中断状态
// Thread.currentThread().interrupt();
// }
// }
//
// 内存一致性效果：线程中向 ExecutorService 提交 Runnable 或 Callable 任务之前的操作 happen-before 由该任务所提取的所有操作，后者依次 happen-before 通过 Future.get() 获取的结果。
// */
//public interface ExecutorService extends Executor {
//
//    /**
//     启动一次顺序关闭，执行以前提交的任务，但不接受新任务。如果已经关闭，则调用没有其他作用。
//     抛出：
//     SecurityException - 如果安全管理器存在并且关闭，
//     此 ExecutorService 可能操作某些不允许调用者修改的线程（因为它没有保持 RuntimePermission ("modifyThread")）
//     ，或者安全管理器的 checkAccess 方法拒绝访问。
//     */
//    void shutdown();
//
//    /**
//     试图停止所有正在执行的活动任务，暂停处理正在等待的任务，并返回等待执行的任务列表。
//     无法保证能够停止正在处理的活动执行任务，但是会尽力尝试。
//     例如，通过 Thread.interrupt() 来取消典型的实现，所以任何任务无法响应中断都可能永远无法终止。
//
//     返回：
//     从未开始执行的任务的列表
//     抛出：
//     SecurityException - 如果安全管理器存在并且关闭，
//     此 ExecutorService 可能操作某些不允许调用者修改的线程（因为它没有保持 RuntimePermission ("modifyThread")），
//     或者安全管理器的 checkAccess 方法拒绝访问。
//     */
//    List<Runnable> shutdownNow();
//
//    /**
//     如果此执行程序已关闭，则返回 true。
//     返回：
//     如果此执行程序已关闭，则返回 true
//     */
//    boolean isShutdown();
//
//    /**
//     如果关闭后所有任务都已完成，则返回 true。注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。
//     返回：
//     如果关闭后所有任务都已完成，则返回 true
//     */
//    boolean isTerminated();
//
//    /**
//     请求关闭、发生超时或者当前线程中断，无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行。
//     参数：
//     timeout - 最长等待时间
//     unit - timeout 参数的时间单位
//     返回：
//     如果此执行程序终止，则返回 true；如果终止前超时期满，则返回 false
//     抛出：
//     InterruptedException - 如果等待时发生中断
//     */
//    boolean awaitTermination(long timeout, TimeUnit unit)
//            throws InterruptedException;
//
//    /**
//     提交一个返回值的任务用于执行，返回一个表示任务的未决结果的 Future。
//     该 Future 的 get 方法在成功完成时将会返回该任务的结果。
//     如果想立即阻塞任务的等待，则可以使用 result = exec.submit(aCallable).get(); 形式的构造。
//
//     注：Executors 类包括了一组方法，可以转换某些其他常见的类似于闭包的对象，
//     例如，将 PrivilegedAction 转换为 Callable 形式，这样就可以提交它们了。
//
//     参数：
//     task - 要提交的任务
//     返回：
//     表示任务等待完成的 Future
//     抛出：
//     RejectedExecutionException - 如果任务无法安排执行
//     NullPointerException - 如果该任务为 null
//     */
//    <T> Future<T> submit(Callable<T> task);
//
//    /**
//     提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。
//     该 Future 的 get 方法在成功完成时将会返回给定的结果。
//     参数：
//     task - 要提交的任务
//     result - 返回的结果
//     返回：
//     表示任务等待完成的 Future
//     抛出：
//     RejectedExecutionException - 如果任务无法安排执行
//     NullPointerException - 如果该任务为 null
//     */
//    <T> Future<T> submit(Runnable task, T result);
//
//    /**
//     提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。该 Future 的 get 方法在 成功 完成时将会返回 null。
//     参数：
//     task - 要提交的任务
//     返回：
//     表示任务等待完成的 Future
//     抛出：
//     RejectedExecutionException - 如果任务无法安排执行
//     NullPointerException - 如果该任务为 null
//
//     */
//    Future<?> submit(Runnable task);
//
//    /**
//     执行给定的任务，当所有任务完成时，返回保持任务状态和结果的 Future 列表。返回列表的所有元素的 Future.isDone() 为 true。注意，可以正常地或通过抛出异常来终止 已完成 任务。如果正在进行此操作时修改了给定的 collection，则此方法的结果是不确定的。
//     参数：
//     tasks - 任务 collection
//     返回：
//     表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同，每个任务都已完成。
//     抛出：
//     InterruptedException - 如果等待时发生中断，在这种情况下取消尚未完成的任务。
//     NullPointerException - 如果任务或其任意元素为 null
//     RejectedExecutionException - 如果所有任务都无法安排执行
//     */
//    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
//            throws InterruptedException;
//
//    /**
//     执行给定的任务，当所有任务完成或超时期满时（无论哪个首先发生），返回保持任务状态和结果的 Future 列表。
//     返回列表的所有元素的 Future.isDone() 为 true。
//     一旦返回后，即取消尚未完成的任务。
//     注意，可以正常地或通过抛出异常来终止 已完成 任务。
//     如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。
//     参数：
//     tasks - 任务 collection
//     timeout - 最长等待时间
//     unit - timeout 参数的时间单位
//     返回：
//     表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同。
//     如果操作未超时，则已完成所有任务。
//     如果确实超时了，则某些任务尚未完成。
//     抛出：
//     InterruptedException - 如果等待时发生中断，在这种情况下取消尚未完成的任务
//     NullPointerException - 如果任务或其任意元素或 unit 为 null
//     RejectedExecutionException - 如果所有任务都无法安排执行
//     */
//    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
//                                  long timeout, TimeUnit unit)
//            throws InterruptedException;
//
//    /**
//     执行给定的任务，如果某个任务已成功完成（也就是未抛出异常），则返回其结果。
//     一旦正常或异常返回后，则取消尚未完成的任务。
//     如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。
//     参数：
//     tasks - 任务 collection
//     返回：
//     某个任务返回的结果
//     抛出：
//     InterruptedException - 如果等待时发生中断
//     NullPointerException - 如果任务或其任意元素为 null
//     IllegalArgumentException - 如果任务为空
//     ExecutionException - 如果没有任务成功完成
//     RejectedExecutionException - 如果任务无法安排执行
//     */
//    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
//            throws InterruptedException, ExecutionException;
//
//    /**
//     执行给定的任务，如果在给定的超时期满前某个任务已成功完成（也就是未抛出异常），则返回其结果。
//     一旦正常或异常返回后，则取消尚未完成的任务。
//     如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。
//     参数：
//     tasks - 任务 collection
//     timeout - 最长等待时间
//     unit - timeout 参数的时间单位
//     返回：
//     某个任务返回的结果
//     抛出：
//     InterruptedException - 如果等待时发生中断
//     NullPointerException - 如果任务或其任意元素或 unit 为 null
//     TimeoutException - 如果在所有任务成功完成之前给定的超时期满
//     ExecutionException - 如果没有任务成功完成
//     RejectedExecutionException - 如果任务无法安排执行
//     */
//    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
//                    long timeout, TimeUnit unit)
//            throws InterruptedException, ExecutionException, TimeoutException;
//}
