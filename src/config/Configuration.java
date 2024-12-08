package config;
import com.google.gson.Gson;
import java.io.*;
public class Configuration {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;
    private int numberOfVendors;    // Add this
    private int numberOfCustomers;  // Add this
    private int maxTicketsPerCustomer; // Add this

    public Configuration(int totalTickets, int ticketReleaseRate, int
            customerRetrievalRate, int maxTicketCapacity,int numberOfVendors, int numberOfCustomers,
                         int maxTicketsPerCustomer) {
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
        this.numberOfVendors = numberOfVendors;
        this.numberOfCustomers = numberOfCustomers;
        this.maxTicketsPerCustomer = maxTicketsPerCustomer;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }
    public int getNumberOfVendors() { return numberOfVendors; }
    public int getNumberOfCustomers() { return numberOfCustomers; }
    public int getMaxTicketsPerCustomer() { return maxTicketsPerCustomer; }

    public static Configuration loadFromFile(String filePath) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Configuration.class);
        }
    }

    public void saveToFile(String filePath) throws IOException {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(this, writer);
        }

    }

}