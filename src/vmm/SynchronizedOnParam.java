package vmm;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class SynchronizedOnParam
{
    private static final String LOGGER_PREFIX = SynchronizedOnParam.class.getSimpleName();
    static Map<Integer, Object> locks = new ConcurrentHashMap<>();

    public static void main( String[] args) throws Throwable
    {
        testExecutor();
    }

    public static boolean foo( int o)
    {
        System.out.println( "Requested:" + o + "," + o % 3 + "(" + LocalDateTime.now() + ")");
        synchronized (locks.computeIfAbsent( o % 3, k -> new Object()))
        {
            System.out.println( "Processing:" + o + "," + o % 3 + "(" + LocalDateTime.now() + ")");
            try
            {
                TimeUnit.SECONDS.sleep( 1);
            }
            catch (InterruptedException e)
            {

            }
            System.out.println( "Completed:" + o + "," + o % 3 + "(" + LocalDateTime.now() + ")");
        }
        return true;
    }

    public static void testExecutor() throws Throwable
    {
        List<Callable<Boolean>> callableTasks = new ArrayList<>();
        for (int i = 1; i < 20; i++)
        {
            int o = i;
            Callable<Boolean> callable = () -> {
                return foo( o);
            };
            callableTasks.add( callable);

        }
        execute( callableTasks, 10, () -> {
            return false;
        });
    }

    /**
     * Execute callables in thread pool and waits till completed unless an
     * interrupt is given via the interruptionStatus or an execution exception
     * occurred.
     *
     * @param callableTasks the callable tasks
     * @param poolSize the pool size
     * @param interruptionStatus the interruption status supplier
     * @throws Exception the exception
     */
    public static void execute( List<Callable<Boolean>> callableTasks, int poolSize,
            BooleanSupplier interruptionStatus) throws Exception
    {
        LocalTime start = LocalTime.now();
        System.out.println( String.format( "[%s]: Execution Started. To do: %s, StartTime: %s.",
                LOGGER_PREFIX, callableTasks.size(), start));
        ExecutorService pool = Executors
                .newFixedThreadPool( callableTasks.size() > poolSize ? poolSize : callableTasks.size());
        final CompletionService<Boolean> service = new ExecutorCompletionService<>( pool);
        for (Callable<Boolean> task : callableTasks)
        {
            service.submit( task);
        }
        pool.shutdown();
        int completedCount = 0;
        while (completedCount != callableTasks.size() && !pool.isTerminated()
                && !interruptionStatus.getAsBoolean())
        {
            try
            {
                service.take().get();
                completedCount++;
            }
            catch (ExecutionException e)
            {
                pool.shutdownNow();
                throw new Exception( e.getCause());
            }
            catch (InterruptedException e)
            {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (interruptionStatus.getAsBoolean())
        {
            System.out.println(
                    String.format( "[%s]: Execution interrupted by external request.", LOGGER_PREFIX));
            pool.shutdownNow();
        }
        System.out
                .println( String.format( "[%s]: Shutdown Execution. Completed: %s out of %s, Started at: %s.",
                        LOGGER_PREFIX, completedCount, callableTasks.size(), start));
    }

}
