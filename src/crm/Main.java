package crm;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DBConnection.connect();

        Scanner scanner = new Scanner(System.in);
        LeadService leadService = new LeadService();
        ClientService clientService = new ClientService();
        SummaryService summaryService = new SummaryService();

        while (true) {
            System.out.println("\nMINI CLI CRM - MAIN MENU");
            System.out.println("------------------------------");
            System.out.println("1. Lead Management");
            System.out.println("2. Client Management");
            System.out.println("3. Reports & Analytics");
            System.out.println("4. Auto-Snooze Missed Follow-ups");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int mainChoice;
            try {
                mainChoice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (mainChoice) {
                case 1:
                    leadSubMenu(scanner, leadService);
                    break;
                case 2:
                    clientSubMenu(scanner, clientService);
                    break;
                case 3:
                    reportSubMenu(scanner, summaryService);
                    break;
                case 4:
                    leadService.autoSnoozeMissedFollowUps();
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void leadSubMenu(Scanner scanner, LeadService leadService) {
        while (true) {
            System.out.println("\nLEAD MANAGEMENT MENU");
            System.out.println("------------------------------");
            System.out.println("1. Add Lead");
            System.out.println("2. View All Leads");
            System.out.println("3. Search Lead (Email/Phone)");
            System.out.println("4. View Today's Follow-ups");
            System.out.println("5. Convert Lead to Client");
            System.out.println("6. Update Lead Details");
            System.out.println("7. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter Phone: ");
                    String phone = scanner.nextLine();
                    System.out.print("Enter Follow-up Date (YYYY-MM-DD): ");
                    String followUpDate = scanner.nextLine();
                    leadService.addLead(name, email, phone, followUpDate);
                    break;
                case 2:
                    leadService.viewLeads();
                    break;
                case 3:
                    System.out.print("Enter Email or Phone to search: ");
                    String key = scanner.nextLine();
                    leadService.searchLead(key);
                    break;
                case 4:
                    leadService.viewTodayFollowUps();
                    break;
                case 5:
                    System.out.print("Enter Lead Email or Phone to convert: ");
                    String convertKey = scanner.nextLine();
                    leadService.convertLeadToClient(convertKey);
                    break;
                case 6:
                    leadService.updateLead(scanner);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void clientSubMenu(Scanner scanner, ClientService clientService) {
        while (true) {
            System.out.println("\nCLIENT MANAGEMENT MENU");
            System.out.println("------------------------------");
            System.out.println("1. View All Clients");
            System.out.println("2. Search Client (Email/Phone)");
            System.out.println("3. Delete Client");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    clientService.viewAllClients();
                    break;
                case 2:
                    System.out.print("Enter Email or Phone to search: ");
                    String key = scanner.nextLine();
                    clientService.searchClient(key);
                    break;
                case 3:
                    System.out.print("Enter Email or Phone to delete: ");
                    String delKey = scanner.nextLine();
                    clientService.deleteClient(delKey);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void reportSubMenu(Scanner scanner, SummaryService summaryService) {
        while (true) {
            System.out.println("\nREPORTS & ANALYTICS MENU");
            System.out.println("------------------------------");
            System.out.println("1. Daily CRM Summary");
            System.out.println("2. Lead Status Breakdown");
            System.out.println("3. Clients Converted in Last 7 Days");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            LocalDate today = LocalDate.now();

            switch (choice) {
                case 1:
                    summaryService.displayDailySummary(today);
                    break;
                case 2:
                    summaryService.leadStatusBreakdown();
                    break;
                case 3:
                    summaryService.clientsConvertedLast7Days();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
