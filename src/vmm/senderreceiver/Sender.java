package vmm.senderreceiver;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class Sender implements Runnable
{
    private DataService data;

    public Sender(DataService data)
    {
        this.data = data;
    }

    public void run()
    {
        String packets[] = { "First packet", "Second packet", "Third packet", "Fourth packet", "End" };

        for (String packet : packets)
        {
            // Thread.sleep() to mimic heavy server-side processing
            try
            {
                log( "Preparing next message");
                Thread.sleep( ThreadLocalRandom.current().nextInt( 1000, 5000));
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                log( "Thread interrupted" + e);
            }
            log( "Sending : " + packet);
            data.send( packet);
        }
    }

    public void log( String msg)
    {
        System.out.println( "[" + LocalDateTime.now() + " : "+ this.getClass().getSimpleName() + "] " + msg);
    }
}
