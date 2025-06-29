package crm;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Create Scanner
        LeadService leadService = new LeadService(); // Create service object

        while (true) {
            // Menu
            System.out.println("\n MINI CLI CRM - MENU");
            System.out.println("1. Add Lead");
            System.out.println("2. View All Leads");
            System.out.println("3. Search Lead (Email/Phone)");
            System.out.println("4. View Today Follow-ups");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt(); // Get menu option
            scanner.nextLine(); // Consume leftover newline

            switch (choice) {
                case 1:
                    // Input for adding lead
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter Phone: ");
                    String phone = scanner.nextLine();
                    System.out.print("Enter Follow-up Date (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    System.out.print("Enter Priority (High/Medium/Low): ");
                    String priority = scanner.nextLine();
                    leadService.addLead(name, email, phone, date, priority);
                    break;

                case 2:
                    leadService.viewLeads(); // View all
                    break;

                case 3:
                    System.out.print("Enter Email or Phone to search: ");
                    String key = scanner.nextLine();
                    leadService.searchLead(key); // Search
                    break;

                case 4:
                    System.out.print("Enter today's date (YYYY-MM-DD): ");
                    String today = scanner.nextLine();
                    leadService.viewTodayFollowUps(today); // Follow-ups
                    break;

                case 5:
                    System.out.println(" Exiting... Goodbye!");
                    scanner.close(); // Close scanner properly
                    return;

                default:
                    System.out.println(" Invalid choice! Try again.");
            }
        }
    }
}
