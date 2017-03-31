//package java.util.concurrent;
//
//import static java.util.concurrent.TimeUnit.NANOSECONDS;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//
///**
// 提供 ExecutorService 执行方法的默认实现。此类使用 newTaskFor 返回的 RunnableFuture 实现 submit、invokeAny 和 invokeAll 方法，默认情况下，RunnableFuture 是此包中提供的 FutureTask 类。例如，submit(Runnable) 的实现创建了一个关联 RunnableFuture 类，该类将被执行并返回。子类可以重写 newTaskFor 方法，以返回 FutureTask 之外的 RunnableFuture 实现。
//
// 扩展示例。以下是一个类的简要介绍，该类定制 ThreadPoolExecutor 使用 CustomTask 类替代默认 FutureTask：
//
// public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
//
// static class CustomTask implements RunnableFuture {…}
//
// protected RunnableFuture newTaskFor(Callable c) {
// return new CustomTask(c);
// }
// protected RunnableFuture newTaskFor(Runnable r, V v) {
// return new CustomTask(r, v);
// }
// // … add constructors, etc.
// }
//
// 从以下版本开始：
// 1.5
// */
//public abstract class AbstractExecutorService implements ExecutorService {
//
//    /**
//     * 为给定可运行任务和默认值返回一个 RunnableFuture。
//     * 参数：
//     * runnable - 将被包装的可运行任务
//     * value - 用于所返回的将来任务的默认值
//     * 返回：
//     * 一个 RunnableFuture，在运行的时候，它将运行底层可运行任务，作为 Future 任务，它将生成给定值作为其结果，并为底层任务提供取消操作。
//     * 从以下版本开始：
//     * 1.6
//     */
//    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
//        return new FutureTask<T>(runnable, value);
//    }
//
//    /**
//     * 为给定可调用任务返回一个 RunnableFuture。
//     * 参数：
//     * callable - 将包装的可调用任务
//     * 返回：
//     * 一个 RunnableFuture，在运行的时候，它将调用底层可调用任务，作为 Future 任务，它将生成可调用的结果作为其结果，并为底层任务提供取消操作。
//     * 从以下版本开始：
//     * 1.6
//     */
//    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
//        return new FutureTask<T>(callable);
//    }
//
//    /**
//     * 从接口 ExecutorService 复制的描述
//     * 提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。该 Future 的 get 方法在 成功 完成时将会返回 null。
//     * 指定者：
//     * 接口 ExecutorService 中的 submit
//     * 参数：
//     * task - 要提交的任务
//     * 返回：
//     * 表示任务等待完成的 Future
//     */
//    public Future<?> submit(Runnable task) {
//        if (task == null) throw new NullPointerException();
//        RunnableFuture<Void> ftask = newTaskFor(task, null);
//        execute(ftask);
//        return ftask;
//    }
//
//    /**
//     * 从接口 ExecutorService 复制的描述
//     * 提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。该 Future 的 get 方法在成功完成时将会返回给定的结果。
//     * 指定者：
//     * 接口 ExecutorService 中的 submit
//     * 参数：
//     * task - 要提交的任务
//     * result - 返回的结果
//     * 返回：
//     * 表示任务等待完成的 Future
//     */
//    public <T> Future<T> submit(Runnable task, T result) {
//        if (task == null) throw new NullPointerException();
//        RunnableFuture<T> ftask = newTaskFor(task, result);
//        execute(ftask);
//        return ftask;
//    }
//
//    /**
//     * 从接口 ExecutorService 复制的描述
//     * 提交一个返回值的任务用于执行，返回一个表示任务的未决结果的 Future。该 Future 的 get 方法在成功完成时将会返回该任务的结果。
//     * 如果想立即阻塞任务的等待，则可以使用 result = exec.submit(aCallable).get(); 形式的构造。
//     * <p>
//     * 注：Executors 类包括了一组方法，可以转换某些其他常见的类似于闭包的对象，例如，将 PrivilegedAction 转换为 Callable 形式，这样就可以提交它们了。
//     * <p>
//     * 指定者：
//     * 接口 ExecutorService 中的 submit
//     * 参数：
//     * task - 要提交的任务
//     * 返回：
//     * 表示任务等待完成的 Future
//     */
//    public <T> Future<T> submit(Callable<T> task) {
//        if (task == null) throw new NullPointerException();
//        RunnableFuture<T> ftask = newTaskFor(task);
//        execute(ftask);
//        return ftask;
//    }
//
//    /**
//     * invokeAny的主要机制
//     */
//    private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks,
//                              boolean timed, long nanos)
//            throws InterruptedException, ExecutionException, TimeoutException {
//        if (tasks == null)
//            throw new NullPointerException();
//        int ntasks = tasks.size();
//        if (ntasks == 0)
//            throw new IllegalArgumentException();
//        ArrayList<Future<T>> futures = new ArrayList<>(ntasks);
//        ExecutorCompletionService<T> ecs =
//                new ExecutorCompletionService<T>(this);
//
//    // 为了提高效率，特别是在并行性有限的执行者中，检查先前提交的任务是否在提交更多的任务之前完成。
//    // 这种交错加上异常机制占主环路的凌乱。
//
//        try {
//            //记录异常，如果我们不能获得任何结果，我们可以抛出我们得到的最后一个异常。
//            ExecutionException ee = null;
//            final long deadline = timed ? System.nanoTime() + nanos : 0L;
//            Iterator<? extends Callable<T>> it = tasks.iterator();
//
//            // 肯定开始一个任务; 其余的是渐进式的
//            futures.add(ecs.submit(it.next()));
//            --ntasks;
//            int active = 1;
//
//            for (; ; ) {
//                Future<T> f = ecs.poll();
//                if (f == null) {
//                    if (ntasks > 0) {
//                        --ntasks;
//                        futures.add(ecs.submit(it.next()));
//                        ++active;
//                    } else if (active == 0)
//                        break;
//                    else if (timed) {
//                        f = ecs.poll(nanos, NANOSECONDS);
//                        if (f == null)
//                            throw new TimeoutException();
//                        nanos = deadline - System.nanoTime();
//                    } else
//                        f = ecs.take();
//                }
//                if (f != null) {
//                    --active;
//                    try {
//                        return f.get();
//                    } catch (ExecutionException eex) {
//                        ee = eex;
//                    } catch (RuntimeException rex) {
//                        ee = new ExecutionException(rex);
//                    }
//                }
//            }
//
//            if (ee == null)
//                ee = new ExecutionException();
//            throw ee;
//
//        } finally {
//            cancelAll(futures);
//        }
//    }
//
//    /**
//     * 从接口 ExecutorService 复制的描述
//     * 执行给定的任务，如果某个任务已成功完成（也就是未抛出异常），则返回其结果。
//     * 一旦正常或异常返回后，则取消尚未完成的任务。
//     * 如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。
//     * 指定者：
//     * 接口 ExecutorService 中的 invokeAny
//     * 参数：
//     * tasks - 任务 collection
//     * 返回：
//     * 某个任务返回的结果
//     * 抛出：
//     * InterruptedException - 如果等待时发生中断
//     * ExecutionException - 如果没有任务成功完成
//     */
//    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
//            throws InterruptedException, ExecutionException {
//        try {
//            return doInvokeAny(tasks, false, 0);
//        } catch (TimeoutException cannotHappen) {
//            assert false;
//            return null;
//        }
//    }
//
//    /**
//     * 从接口 ExecutorService 复制的描述
//     * 执行给定的任务，如果在给定的超时期满前某个任务已成功完成（也就是未抛出异常），则返回其结果。
//     * 一旦正常或异常返回后，则取消尚未完成的任务。
//     * 如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。
//     * 指定者：
//     * 接口 ExecutorService 中的 invokeAny
//     * 参数：
//     * tasks - 任务 collection
//     * timeout - 最长等待时间
//     * unit - timeout 参数的时间单位
//     * 返回：
//     * 某个任务返回的结果
//     * 抛出：
//     * InterruptedException - 如果等待时发生中断
//     * ExecutionException - 如果没有任务成功完成
//     * TimeoutException - 如果在所有任务成功完成之前给定的超时期满
//     */
//    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
//                           long timeout, TimeUnit unit)
//            throws InterruptedException, ExecutionException, TimeoutException {
//        return doInvokeAny(tasks, true, unit.toNanos(timeout));
//    }
//
//    /**
//     * 从接口 ExecutorService 复制的描述
//     * 执行给定的任务，当所有任务完成时，返回保持任务状态和结果的 Future 列表。
//     * 返回列表的所有元素的 Future.isDone() 为 true。注意，可以正常地或通过抛出异常来终止 已完成 任务。
//     * 如果正在进行此操作时修改了给定的 collection，则此方法的结果是不确定的。
//     * 指定者：
//     * 接口 ExecutorService 中的 invokeAll
//     * 参数：
//     * tasks - 任务 collection
//     * 返回：
//     * 表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同，每个任务都已完成。
//     * 抛出：
//     * InterruptedException - 如果等待时发生中断，在这种情况下取消尚未完成的任务。
//     */
//    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
//            throws InterruptedException {
//        if (tasks == null)
//            throw new NullPointerException();
//        ArrayList<Future<T>> futures = new ArrayList<>(tasks.size());
//        try {
//            for (Callable<T> t : tasks) {
//                RunnableFuture<T> f = newTaskFor(t);
//                futures.add(f);
//                execute(f);
//            }
//            for (int i = 0, size = futures.size(); i < size; i++) {
//                Future<T> f = futures.get(i);
//                if (!f.isDone()) {
//                    try {
//                        f.get();
//                    } catch (CancellationException ignore) {
//                    } catch (ExecutionException ignore) {
//                    }
//                }
//            }
//            return futures;
//        } catch (Throwable t) {
//            cancelAll(futures);
//            throw t;
//        }
//    }
//
//    /**
//     * 从接口 ExecutorService 复制的描述
//     * 执行给定的任务，当所有任务完成或超时期满时（无论哪个首先发生），返回保持任务状态和结果的 Future 列表。
//     * 返回列表的所有元素的 Future.isDone() 为 true。一旦返回后，即取消尚未完成的任务。
//     * 注意，可以正常地或通过抛出异常来终止 已完成 任务。如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。
//     * 指定者：
//     * 接口 ExecutorService 中的 invokeAll
//     * 参数：
//     * tasks - 任务 collection
//     * timeout - 最长等待时间
//     * unit - timeout 参数的时间单位
//     * 返回：
//     * 表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同。
//     * 如果操作未超时，则已完成所有任务。
//     * 如果确实超时了，则某些任务尚未完成。
//     * 抛出：
//     * InterruptedException - 如果等待时发生中断，在这种情况下取消尚未完成的任务
//     */
//    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
//                                         long timeout, TimeUnit unit)
//            throws InterruptedException {
//        if (tasks == null)
//            throw new NullPointerException();
//        final long nanos = unit.toNanos(timeout);
//        final long deadline = System.nanoTime() + nanos;
//        ArrayList<Future<T>> futures = new ArrayList<>(tasks.size());
//        int j = 0;
//        timedOut:
//        try {
//            for (Callable<T> t : tasks)
//                futures.add(newTaskFor(t));
//
//            final int size = futures.size();
//
////            如果执行者没有任何/多个并行性，则进行交织时间检查和调用。
//            for (int i = 0; i < size; i++) {
//                if (((i == 0) ? nanos : deadline - System.nanoTime()) <= 0L)
//                    break timedOut;
//                execute((Runnable) futures.get(i));
//            }
//
//            for (; j < size; j++) {
//                Future<T> f = futures.get(j);
//                if (!f.isDone()) {
//                    try {
//                        f.get(deadline - System.nanoTime(), NANOSECONDS);
//                    } catch (CancellationException ignore) {
//                    } catch (ExecutionException ignore) {
//                    } catch (TimeoutException timedOut) {
//                        break timedOut;
//                    }
//                }
//            }
//            return futures;
//        } catch (Throwable t) {
//            cancelAll(futures);
//            throw t;
//        }
//        //在所有任务完成之前先停止; 取消剩余
//        cancelAll(futures, j);
//        return futures;
//    }
//
//    private static <T> void cancelAll(ArrayList<Future<T>> futures) {
//        cancelAll(futures, 0);
//    }
//
//    /**
//     * 取消所有索引至少是j的futures
//     */
//    private static <T> void cancelAll(ArrayList<Future<T>> futures, int j) {
//        for (int size = futures.size(); j < size; j++)
//            futures.get(j).cancel(true);
//    }
//}
