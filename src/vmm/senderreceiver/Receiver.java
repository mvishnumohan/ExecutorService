package vmm.senderreceiver;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class Receiver implements Runnable
{
    private DataService load;

    // standard constructors

    public Receiver(DataService load)
    {
        this.load = load;
    }

    public void run()
    {
        String receivedMessage = null;
        do
        {
            receivedMessage = load.receive();
            log( "Received :" + receivedMessage);
            try
            {
                log( "Processing message :" + receivedMessage);
                Thread.sleep( ThreadLocalRandom.current().nextInt( 1000, 5000));
                log( "Processed message :" + receivedMessage);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                log( "Thread interrupted" + e);
            }
        }
        while (!"End".equals( receivedMessage));
    }

    public void log( String msg)
    {
        System.out.println( "[" + LocalDateTime.now() + " : "+ this.getClass().getSimpleName() + "] " + msg);
    }
}
