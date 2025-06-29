package crm;
import java.util.Scanner;
public class Main {
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
         int choice;

        do {
            System.out.println("\n===== MINI CRM - MAIN MENU =====");
            System.out.println("1. Add Lead");
            System.out.println("2. View Leads");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Add Lead (coming soon)");
                    break;
                case 2:
                    System.out.println("View Leads (coming soon)");
                    break;
                case 3:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 3);

        sc.close();
    }
}
    

