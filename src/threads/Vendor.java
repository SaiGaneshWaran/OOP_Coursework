package threads;

import core.AbstractTicketHandler;
import core.TicketPool;
import logging.Logger;

public class Vendor extends AbstractTicketHandler implements Runnable {
    private final int ticketReleaseRate;
    private final int totalTickets;
    private final String vendorId;
    private volatile boolean running = true;
    private static int currentTicketNumber = 0;

    public Vendor(TicketPool ticketPool, int ticketReleaseRate,
                  int totalTickets, String vendorId) {
        super(ticketPool);
        this.ticketReleaseRate = ticketReleaseRate;
        this.totalTickets = totalTickets;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        while (running && !TicketPool.areAllTicketsSold(totalTickets)) {
            synchronized(Vendor.class) {
                if (currentTicketNumber < totalTickets) {
                    currentTicketNumber++;
                    String ticket = "TKT-" + currentTicketNumber;
                    ticketPool.addTickets(ticket);
                    Logger.log(vendorId + " released ticket: " + ticket);
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

    public static void resetCounters() {
        currentTicketNumber = 0;
    }

    @Override
    public void handleTickets() {
        run();
    }
}


