package core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;

public class TicketPool implements TicketOperation {
    private final int maxCapacity;
    private final List<String> tickets = Collections.synchronizedList(new LinkedList<>());
    private static int totalTicketsSold = 0;
    private final int totalTickets;

    public TicketPool(int maxCapacity, int totalTickets) {
        this.maxCapacity = maxCapacity;
        this.totalTickets = totalTickets;
    }

    @Override
    public synchronized void addTickets(String ticket) {
        if (tickets.size() < maxCapacity) {
            tickets.add(ticket);
            Logger.log("Ticket added to pool: " + ticket);
            notifyAll();
        }
    }

    @Override
    public synchronized String removeTicket() {
        if (!tickets.isEmpty() && totalTicketsSold < totalTickets) {
            String ticket = tickets.remove(0);
            totalTicketsSold++;
            return ticket;
        }
        return null;
    }

    public int getTicketCount() {
        return tickets.size();
    }

    public static boolean areAllTicketsSold(int totalTickets) {
        return totalTicketsSold >= totalTickets;
    }

    public static void resetCounters() {
        totalTicketsSold = 0;
    }
    public static int getTotalTicketsSold() {
        return totalTicketsSold;
    }
}