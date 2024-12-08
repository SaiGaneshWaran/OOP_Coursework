package threads;

import core.AbstractTicketHandler;
import core.TicketPool;
import logging.Logger;

public class Customer extends AbstractTicketHandler implements Runnable {
    private volatile boolean running = true;
    private final int retrievalRate;
    private final int maxTicketsPerCustomer;
    private final String customerId;
    private int ticketsBought = 0;
    private static int totalTicketsBought = 0;  // Static counter for all tickets
    private final int totalTickets;  // Add total tickets field

    public Customer(TicketPool ticketPool, int retrievalRate,
                    int maxTicketsPerCustomer, String customerId, int totalTickets) {
        super(ticketPool);
        this.retrievalRate = retrievalRate;
        this.maxTicketsPerCustomer = maxTicketsPerCustomer;
        this.customerId = customerId;
        this.totalTickets = totalTickets;
    }

    @Override
    public void run() {
        while (running && ticketsBought < maxTicketsPerCustomer && totalTicketsBought < totalTickets) {
            String ticket = ticketPool.removeTicket();
            if (ticket != null) {
                synchronized(Customer.class) {  // Synchronize ticket count
                    if (totalTicketsBought < totalTickets) {
                        ticketsBought++;
                        totalTicketsBought++;
                        Logger.log(customerId + " purchased ticket: " + ticket);

                        if (ticketsBought >= maxTicketsPerCustomer) {
                            Logger.log(customerId + " reached maximum ticket limit (" +
                                    maxTicketsPerCustomer + ")");
                            break;
                        }
                        if (totalTicketsBought >= totalTickets) {
                            Logger.log("All tickets have been purchased");
                            break;
                        }
                    }
                }
            } else {
                Logger.log(customerId + " waiting for tickets...");
            }

            try {
                Thread.sleep(retrievalRate);
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