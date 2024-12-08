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
    private final int totalTickets;

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
        while (running && ticketsBought < maxTicketsPerCustomer) {
            // First check if all tickets are sold
            if (TicketPool.areAllTicketsSold(totalTickets)) {
                Logger.log("All tickets have been sold. Simulation complete.");
                running = false;
                break;
            }

            synchronized (ticketPool) {
                if (ticketsBought <= totalTickets) {
                    String ticket = ticketPool.removeTicket();
                    if (ticket != null) {
                        ticketsBought++;
                        Logger.log(customerId + " purchased ticket: " + ticket);



                        if (ticketsBought >= maxTicketsPerCustomer) {
                            Logger.log(customerId + " reached maximum ticket limit");
                            running = false;
                            break;
                        }
                    }else {
                        // Only log waiting message if tickets are still available
                        if (!TicketPool.areAllTicketsSold(totalTickets)) {
                            Logger.log(customerId + " waiting for vendor to release tickets...");
                            try {
                                ticketPool.wait(retrievalRate);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }else{Logger.log("All tickets have been sold. Simulation complete.");
                    ticketPool.notifyAll();
                    running = false;
                    break;

                }
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