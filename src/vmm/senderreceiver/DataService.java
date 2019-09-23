package vmm.senderreceiver;

import java.time.LocalDateTime;

public class DataService
{
    private String packet;

    // True if receiver should wait
    // False if sender should wait
    private boolean transfer = true;

    public synchronized void send( String packet)
    {
        while (!transfer)
        {
            try
            {
                log( "Send on hold - waiting for consumption.");
                wait();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                log( "Thread interrupted" + e);
            }
        }
        transfer = false;

        this.packet = packet;
        log( "Packet arrived - notifing receiver.");
        notifyAll();
    }

    public synchronized String receive()
    {
        while (transfer)
        {
            try
            {
                log( "Receive on hold - waiting for packet.");
                wait();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                log( "Thread interrupted" + e);
            }
        }
        transfer = true;
        log( "Packet consumed - notifing sender.");
        notifyAll();
        return packet;
    }

    public void log( String msg)
    {
        System.out.println( "[" + LocalDateTime.now() + " : "+ this.getClass().getSimpleName() + "] " + msg);
    }
}
