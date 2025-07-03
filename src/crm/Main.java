package crm;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        db.connect();

        Scanner scanner = new Scanner(System.in);
        LeadService leadService = new LeadService();

        while (true) {
            System.out.println("\n📋 MINI CLI CRM - MENU");
            System.out.println("1. Add Lead");
            System.out.println("2. View All Leads");
            System.out.println("3. Search Lead (Email/Phone)");
            System.out.println("4. View Today Follow-ups");
            System.out.println("5. Convert Lead to Client");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
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
                    System.out.print("Enter Lead Status (New/Interested/Hot): ");
                    String status = scanner.nextLine();
                    leadService.addLead(name, email, phone, followUpDate, status);
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
                    System.out.print("Enter today's date (YYYY-MM-DD): ");
                    String today = scanner.nextLine();
                    leadService.viewTodayFollowUps(today);
                    break;

                case 5:
                    System.out.print("Enter Lead Email or Phone to convert: ");
                    String convertKey = scanner.nextLine();
                    System.out.print("Enter today's date (YYYY-MM-DD): ");
                    String todayDate = scanner.nextLine();
                    leadService.convertLeadToClient(convertKey, todayDate);
                    break;

                case 6:
                    System.out.println("👋 Exiting... Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("❗ Invalid choice! Try again.");
            }
        }
    }
}
