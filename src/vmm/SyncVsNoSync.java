package vmm;

public class SyncVsNoSync
{
    public static void main( String[] args)
    {
        printUsingNonSynchronizedLogger();
        //printUsingSynchronizedLogger();
    }

    public static void printUsingSynchronizedLogger()
    {
        Logger sLogger = new SynchronizedLogger();
        new TestThread( sLogger, "one").start();
        new TestThread( sLogger, "two").start();
        new TestThread( sLogger, "three").start();
    }

    public static void printUsingNonSynchronizedLogger()
    {
        Logger logger = new Logger();
        new TestThread( logger, "one").start();
        new TestThread( logger, "two").start();
        new TestThread( logger, "three").start();
    }

}

class Logger
{
    public void print( String msg)
    {
        System.out.print( "[" + msg);
        try
        {
            Thread.sleep( 100);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println( "]");
    }
}

class SynchronizedLogger extends Logger
{
    public synchronized void print( String msg)
    {
        super.print( msg);
    }
}

class TestThread extends Thread
{
    String msg;
    Logger fobj;

    TestThread(Logger fp, String str)
    {
        fobj = fp;
        msg = str;
    }

    public void run()
    {
        fobj.print( msg);
    }
}
