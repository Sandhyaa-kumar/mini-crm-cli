package crm;

import java.util.ArrayList;
import java.util.List;

public class LeadService {
    private List<Lead> leads = new ArrayList<>();

    // Add a new lead
    public void addLead(String name, String email, String phone, String followUpDate, String priority) {
        Lead newLead = new Lead(name, email, phone, followUpDate, priority);
        leads.add(newLead);
        System.out.println(" Lead added successfully!");
    }

    // View all leads
    public void viewLeads() {
        if (leads.isEmpty()) {
            System.out.println(" No leads found.");
            return;
        }
        for (Lead lead : leads) {
            System.out.println(lead);
        }
    }

    // Search lead by email or phone
    public void searchLead(String key) {
        boolean found = false;
        for (Lead lead : leads) {
            if (lead.getEmail().equalsIgnoreCase(key) || lead.getPhone().equals(key)) {
                System.out.println(" Lead Found:\n" + lead);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println(" Lead not found with given email/phone.");
        }
    }

    // Get list of leads for daily follow-up
    public void viewTodayFollowUps(String todayDate) {
        boolean any = false;
        for (Lead lead : leads) {
            if (lead.getFollowUpDate().equals(todayDate)) {
                System.out.println(" Follow-up Today: " + lead);
                any = true;
            }
        }
        if (!any) {
            System.out.println(" No follow-ups for today!");
        }
    }
}
