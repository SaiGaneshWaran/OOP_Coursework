package threads;
import core.AbstractTicketHandler;
import core.TicketPool;
import logging.Logger;

public class Vendor extends AbstractTicketHandler implements Runnable {
    private final int ticketReleaseRate;
    private final int totalTickets;
    private final String vendorId;
    private volatile boolean running = true;
    private static int totalTicketsReleased = 0;  // Static counter for all tickets

    public Vendor(TicketPool ticketPool, int ticketReleaseRate, int totalTickets, String vendorId) {
        super(ticketPool);
        this.ticketReleaseRate = ticketReleaseRate;
        this.totalTickets = totalTickets;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        while (running && totalTicketsReleased < totalTickets) {
            synchronized(Vendor.class) {  // Synchronize ticket count
                if (totalTicketsReleased < totalTickets) {
                    String ticket = "TKT-" + (totalTicketsReleased + 1);
                    ticketPool.addTickets(ticket);
                    Logger.log(vendorId + " released ticket: " + ticket);
                    totalTicketsReleased++;
                } else {
                    break;
                }
            }

            try {
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                break;
            }
        }
        running = false;
    }


    public void stop() {
        running = false;
    }
    @Override
    public void handleTickets() {
        run();
    }
}