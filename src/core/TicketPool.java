package core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
public class TicketPool implements TicketOperation {
    private final int maxCapacity;

    public TicketPool(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    private final List<String> tickets = Collections.synchronizedList(new LinkedList<>());
    @Override
    public synchronized void addTickets(String ticket) {
        if (tickets.size() < maxCapacity) {
            tickets.add(ticket);
        }
    }
    @Override
    public synchronized String removeTicket() {
        return tickets.isEmpty() ? null : tickets.remove(0);
    }

    public int getTicketCount() {
        return tickets.size();
    }
    public int getMaxCapacity() {
        return maxCapacity;}
}