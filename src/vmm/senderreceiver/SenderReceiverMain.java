package vmm.senderreceiver;

public class SenderReceiverMain
{

    public static void main( String[] args)
    {
        DataService data = new DataService();
        Thread sender = new Thread( new Sender( data));
        Thread receiver = new Thread( new Receiver( data));

        sender.start();
        receiver.start();
    }

}
