
package ui;

import config.Configuration;
import core.TicketPool;
import logging.Logger;
import threads.Customer;
import threads.Vendor;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class CommandLineInterface {
    private static Configuration config;
    private static TicketPool ticketPool;
    private static List<Thread> vendorThreads;
    private static List<Thread> customerThreads;
    private static List<Vendor> vendors;
    private static List<Customer> customers;
    private static boolean isRunning = true;

    public static void start() {
        Scanner scanner = new Scanner(System.in);
        while (isRunning) {
            displayMenu();
            try {
                int choice = scanner.nextInt();
                handleChoice(choice);
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number between 1-5");
                scanner.nextLine();
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Ticket Management System ===");
        System.out.println("1. Configure System");
        System.out.println("2. Start Simulation");
        System.out.println("3. Stop Simulation");
        System.out.println("4. Show Status");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }

    private static void handleChoice(int choice) {
        switch (choice) {
            case 1 -> configureSystem();
            case 2 -> startSimulation();
            case 3 -> stopSimulation();
            case 4 -> showStatus();
            case 5 -> exit();
            default -> System.out.println("Invalid choice! Please enter 1-5");
        }
    }

    private static void configureSystem() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Total Tickets: ");
            int totalTickets = getValidInput(scanner);

            System.out.print("Enter Number of Vendors: ");
            int numberOfVendors = getValidInput(scanner);

            System.out.print("Enter Number of Customers: ");
            int numberOfCustomers = getValidInput(scanner);

            int maxTicketsPerCustomer;
            while (true) {
                System.out.print("Enter Maximum Tickets Per Customer: ");
                maxTicketsPerCustomer = getValidInput(scanner);

                int totalPossiblePurchases = maxTicketsPerCustomer * numberOfCustomers;
                if (totalPossiblePurchases < totalTickets) {
                    System.out.println("Warning: Total possible purchases (" +
                            totalPossiblePurchases + ") is less than total tickets (" +
                            totalTickets + ")");
                    System.out.print("Do you want to adjust the maximum tickets per customer? (y/n): ");
                    String response = scanner.next();
                    scanner.nextLine();
                    if (response.toLowerCase().startsWith("y")) {
                        continue;
                    }
                }
                break;
            }

            System.out.print("Enter Ticket Release Rate (ms): ");
            int releaseRate = getValidInput(scanner);

            System.out.print("Enter Customer Retrieval Rate (ms): ");
            int retrievalRate = getValidInput(scanner);

            int maxCapacity;
            while (true) {
                System.out.print("Enter Max Ticket Capacity: ");
                maxCapacity = getValidInput(scanner);
                if (maxCapacity > totalTickets) {
                    System.out.println("Error: Max Capacity cannot be greater than Total Tickets!");
                    continue;
                }
                break;
            }

            config = new Configuration(totalTickets, releaseRate, retrievalRate,
                    maxCapacity, numberOfVendors, numberOfCustomers,
                    maxTicketsPerCustomer);
            Logger.log("System configured successfully");

        } catch (Exception e) {
            Logger.log("Configuration error: " + e.getMessage());
        }
    }

    private static void startSimulation() {
        if (config == null) {
            System.out.println("Please configure the system first!");
            return;
        }

        try {
            ticketPool = new TicketPool(config.getMaxTicketCapacity());
            vendorThreads = new ArrayList<>();
            customerThreads = new ArrayList<>();
            vendors = new ArrayList<>();
            customers = new ArrayList<>();

            // Create vendors
            for (int i = 1; i <= config.getNumberOfVendors(); i++) {
                Vendor vendor = new Vendor(ticketPool, config.getTicketReleaseRate(),
                        config.getTotalTickets(), "Vendor-" + i);
                vendors.add(vendor);
                Thread thread = new Thread(vendor);
                vendorThreads.add(thread);
            }

            // Create customers
            for (int i = 1; i <= config.getNumberOfCustomers(); i++) {
                Customer customer = new Customer(ticketPool, config.getCustomerRetrievalRate(),
                        config.getMaxTicketsPerCustomer(),
                        "Customer-" + i,
                        config.getTotalTickets());
                customers.add(customer);
                Thread thread = new Thread(customer);
                customerThreads.add(thread);
            }

            Logger.log("Simulation started");

            // Start all threads
            vendorThreads.forEach(Thread::start);
            customerThreads.forEach(Thread::start);

            // Wait for completion
            for (Thread t : vendorThreads) t.join();
            for (Thread t : customerThreads) t.join();

            Logger.log("Simulation completed");

        } catch (Exception e) {
            Logger.log("Error in simulation: " + e.getMessage());
        }
    }

    private static void stopSimulation() {
        if (vendors != null) vendors.forEach(Vendor::stop);
        if (customers != null) customers.forEach(Customer::stop);
        Logger.log("Simulation stopped");
    }

    private static void showStatus() {
        if (config == null) {
            System.out.println("\nSystem Status: Not Configured");
            return;
        }

        System.out.println("\nSystem Status:");
        System.out.println("Configuration:");
        System.out.println("- Total Tickets: " + config.getTotalTickets());
        System.out.println("- Number of Vendors: " + config.getNumberOfVendors());
        System.out.println("- Number of Customers: " + config.getNumberOfCustomers());
        System.out.println("- Max Tickets Per Customer: " + config.getMaxTicketsPerCustomer());
        System.out.println("- Max Capacity: " + config.getMaxTicketCapacity());
        System.out.println("- Release Rate: " + config.getTicketReleaseRate() + "ms");
        System.out.println("- Retrieval Rate: " + config.getCustomerRetrievalRate() + "ms");

        if (ticketPool != null) {
            System.out.println("\nTicket Status:");
            System.out.println("- Available Tickets: " + ticketPool.getTicketCount());
            System.out.println("- Simulation Running: " +
                    (vendorThreads != null && vendorThreads.stream().anyMatch(Thread::isAlive)));
        }
    }

    private static void exit() {
        stopSimulation();
        isRunning = false;
        System.out.println("Shutting down system...");
        Logger.log("System terminated");
    }

    private static int getValidInput(Scanner scanner) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value <= 0) {
                    System.out.println("Value must be positive. Try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
