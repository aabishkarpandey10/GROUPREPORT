package assignment;

import java.util.*;

/**
 * Main application class for the Member Management System.
 * Provides a text-based user interface for managing gym members.
 * Demonstrates proper exception handling and input validation.
 * 
 * @author [Your Group Members]
 * @version 1.0
 */
public class MMSApp {
    private static MemberManager manager;
    private static Scanner scanner;
    private static final String FILE_PATH = "members.csv";

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║     GYM MEMBER MANAGEMENT SYSTEM (MMS)            ║");
        System.out.println("║              Version 1.0                           ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        manager = new MemberManager(FILE_PATH);
        scanner = new Scanner(System.in);

        // Initialize system
        manager.ensureSampleData();
        manager.loadFromFile();

        // Main application loop
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = getValidatedInput("Choose option (1-9): ");

            try {
                running = handleMenuChoice(choice);
            } catch (Exception e) {
                System.err.println("\n✗ Error: " + e.getMessage());
                System.out.println("Please try again.\n");
            }
        }

        // Cleanup
        scanner.close();
        System.out.println("\n👋 Thank you for using Member Management System!");
        System.out.println("Goodbye!\n");
    }

    /**
     * Displays the main menu options.
     */
    private static void displayMainMenu() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("           MEMBER MANAGEMENT SYSTEM");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  1. Load Records from File");
        System.out.println("  2. Add New Member");
        System.out.println("  3. Update Member Information");
        System.out.println("  4. Delete Member");
        System.out.println("  5. Query/Search Members");
        System.out.println("  6. Save Records to File");
        System.out.println("  7. Issue Letters & Discounts");
        System.out.println("  8. View All Members");
        System.out.println("  9. Exit System");
        System.out.println("═══════════════════════════════════════════════════");
    }

    /**
     * Handles user menu selection.
     * 
     * @param choice User's menu choice
     * @return true to continue running, false to exit
     */
    private static boolean handleMenuChoice(String choice) {
        switch (choice) {
            case "1":
                loadRecords();
                break;
            case "2":
                addNewMember();
                break;
            case "3":
                updateMember();
                break;
            case "4":
                deleteMember();
                break;
            case "5":
                queryMembers();
                break;
            case "6":
                saveRecords();
                break;
            case "7":
                lettersAndDiscountsMenu();
                break;
            case "8":
                manager.printAllMembers();
                break;
            case "9":
                return confirmExit();
            default:
                System.out.println("\n✗ Invalid option. Please choose 1-9.");
        }
        return true;
    }

    /**
     * Loads member records from file.
     */
    private static void loadRecords() {
        System.out.println("\n→ Loading records from file...");
        manager.loadFromFile();
    }

    /**
     * Adds a new member to the system with proper validation.
     */
    private static void addNewMember() {
        System.out.println("\n═══ ADD NEW MEMBER ═══");

        try {
            String id = getValidatedInput("Enter Member ID: ");

            if (manager.getById(id) != null) {
                System.out.println("✗ Error: Member with ID '" + id + "' already exists.");
                return;
            }

            String name = getValidatedInput("Enter Full Name: ");
            int age = getValidatedIntInput("Enter Age (16-100): ", 16, 100);
            double baseFee = getValidatedDoubleInput("Enter Base Fee ($): ", 0, 10000);

            System.out.println("\nMember Type:");
            System.out.println("  1. Regular Member");
            System.out.println("  2. PT Member (with Personal Trainer)");
            String type = getValidatedInput("Select type (1/2): ");

            Member member;

            if ("2".equals(type)) {
                double trainerFee = getValidatedDoubleInput("Enter Trainer Fee ($): ", 0, 5000);
                member = new PTMember(id, name, age, baseFee, trainerFee);
            } else {
                member = new RegularMember(id, name, age, baseFee);
            }

            // Set performance metrics
            int performance = getValidatedIntInput("Enter Performance Rating (0-100): ", 0, 100);
            member.setPerformanceRating(performance);

            String goalAchieved = getValidatedInput("Achieved Fitness Goal? (y/n): ");
            member.setAchievedGoal(goalAchieved.equalsIgnoreCase("y"));

            manager.addMember(member);

            String saveNow = getValidatedInput("\nSave to file now? (y/n): ");
            if (saveNow.equalsIgnoreCase("y")) {
                manager.saveToFile();
            }

        } catch (IllegalArgumentException e) {
            System.out.println("✗ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Error adding member: " + e.getMessage());
        }
    }

    /**
     * Updates an existing member's information.
     */
    private static void updateMember() {
        System.out.println("\n═══ UPDATE MEMBER ═══");

        String id = getValidatedInput("Enter Member ID: ");
        Member member = manager.getById(id);

        if (member == null) {
            System.out.println("✗ Member not found with ID: " + id);
            return;
        }

        System.out.println("\nCurrent Information:");
        System.out.println(member);
        System.out.println("\nWhat would you like to update?");
        System.out.println("  1. Name");
        System.out.println("  2. Age");
        System.out.println("  3. Base Fee");
        System.out.println("  4. Performance Rating");
        System.out.println("  5. Goal Achievement Status");
        if (member instanceof PTMember) {
            System.out.println("  6. Trainer Fee");
        }
        System.out.println("  0. Cancel");

        String choice = getValidatedInput("Select option: ");

        try {
            switch (choice) {
                case "1":
                    String newName = getValidatedInput("Enter new name: ");
                    member.setName(newName);
                    break;
                case "2":
                    int newAge = getValidatedIntInput("Enter new age (16-100): ", 16, 100);
                    member.setAge(newAge);
                    break;
                case "3":
                    double newFee = getValidatedDoubleInput("Enter new base fee: ", 0, 10000);
                    member.setBaseFee(newFee);
                    break;
                case "4":
                    int newRating = getValidatedIntInput("Enter new performance (0-100): ", 0, 100);
                    member.setPerformanceRating(newRating);
                    break;
                case "5":
                    String achieved = getValidatedInput("Goal achieved? (y/n): ");
                    member.setAchievedGoal(achieved.equalsIgnoreCase("y"));
                    break;
                case "6":
                    if (member instanceof PTMember) {
                        double newTrainerFee = getValidatedDoubleInput("Enter new trainer fee: ", 0, 5000);
                        ((PTMember) member).setTrainerFee(newTrainerFee);
                    }
                    break;
                case "0":
                    System.out.println("Update cancelled.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    return;
            }

            System.out.println("✓ Member updated successfully");

            String saveNow = getValidatedInput("Save changes to file? (y/n): ");
            if (saveNow.equalsIgnoreCase("y")) {
                manager.saveToFile();
            }

        } catch (Exception e) {
            System.out.println("✗ Error updating member: " + e.getMessage());
        }
    }

    /**
     * Deletes a member from the system.
     */
    private static void deleteMember() {
        System.out.println("\n═══ DELETE MEMBER ═══");

        String id = getValidatedInput("Enter Member ID to delete: ");
        Member member = manager.getById(id);

        if (member == null) {
            System.out.println("✗ Member not found with ID: " + id);
            return;
        }

        System.out.println("\nMember to be deleted:");
        System.out.println(member);

        String confirm = getValidatedInput("\nAre you sure you want to delete this member? (yes/no): ");

        if (confirm.equalsIgnoreCase("yes")) {
            if (manager.deleteById(id)) {
                String saveNow = getValidatedInput("Save changes to file? (y/n): ");
                if (saveNow.equalsIgnoreCase("y")) {
                    manager.saveToFile();
                }
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * Queries and searches for members.
     */
    private static void queryMembers() {
        System.out.println("\n═══ QUERY MEMBERS ═══");
        System.out.println("  1. Search by ID");
        System.out.println("  2. Search by Name");
        System.out.println("  3. Search by Performance Range");
        System.out.println("  0. Back to Main Menu");

        String choice = getValidatedInput("Select option: ");

        switch (choice) {
            case "1":
                queryById();
                break;
            case "2":
                queryByName();
                break;
            case "3":
                queryByPerformance();
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Queries a member by ID.
     */
    private static void queryById() {
        String id = getValidatedInput("\nEnter Member ID: ");
        Member member = manager.getById(id);

        if (member == null) {
            System.out.println("✗ No member found with ID: " + id);
        } else {
            System.out.println("\n" + member);
            System.out.printf("Monthly Fee: $%.2f\n", member.calculateFee());
        }
    }

    /**
     * Queries members by name.
     */
    private static void queryByName() {
        String query = getValidatedInput("\nEnter name to search: ");
        List<Member> results = manager.searchByName(query);

        displaySearchResults(results, "name search: " + query);
    }

    /**
     * Queries members by performance range.
     */
    private static void queryByPerformance() {
        int minRating = getValidatedIntInput("\nEnter minimum performance (0-100): ", 0, 100);
        int maxRating = getValidatedIntInput("Enter maximum performance (0-100): ", minRating, 100);

        List<Member> results = manager.searchByPerformance(minRating, maxRating);

        displaySearchResults(results, "performance range: " + minRating + "-" + maxRating);
    }

    /**
     * Displays search results.
     * 
     * @param results        List of members found
     * @param searchCriteria Description of search criteria
     */
    private static void displaySearchResults(List<Member> results, String searchCriteria) {
        if (results.isEmpty()) {
            System.out.println("\n✗ No members found for " + searchCriteria);
        } else {
            System.out.println("\n═══ SEARCH RESULTS (" + results.size() + " found) ═══");
            System.out.println("Criteria: " + searchCriteria);
            System.out.println("───────────────────────────────────────────────────");

            for (int i = 0; i < results.size(); i++) {
                Member m = results.get(i);
                System.out.printf("%d. %s\n", (i + 1), m);
                System.out.printf("   Monthly Fee: $%.2f\n", m.calculateFee());
            }
        }
    }

    /**
     * Saves records to file.
     */
    private static void saveRecords() {
        System.out.println("\n→ Saving records to file...");
        manager.saveToFile();
    }

    /**
     * Displays letters and discounts menu.
     */
    private static void lettersAndDiscountsMenu() {
        System.out.println("\n═══ LETTERS & DISCOUNTS ═══");
        System.out.println("  1. Issue Reminder Letters (Performance ≤ 50)");
        System.out.println("  2. Issue Appreciation Letters (Performance ≥ 80)");
        System.out.println("  3. Award Discounts (Performance ≥ 90, 10% discount)");
        System.out.println("  0. Back to Main Menu");

        String choice = getValidatedInput("Select option: ");

        switch (choice) {
            case "1":
                manager.issueReminderLetters(50);
                break;
            case "2":
                manager.issueAppreciationLetters(80);
                break;
            case "3":
                String confirm = getValidatedInput("\nAward 10% discount to high performers? (y/n): ");
                if (confirm.equalsIgnoreCase("y")) {
                    manager.awardDiscounts(90, 10.0);
                    String save = getValidatedInput("Save changes to file? (y/n): ");
                    if (save.equalsIgnoreCase("y")) {
                        manager.saveToFile();
                    }
                }
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Confirms exit from the application.
     * 
     * @return false to exit, true to continue
     */
    private static boolean confirmExit() {
        String confirm = getValidatedInput("\nAre you sure you want to exit? (y/n): ");
        return !confirm.equalsIgnoreCase("y");
    }

    /**
     * Gets validated string input from user.
     * 
     * @param prompt The prompt to display
     * @return User's input (non-empty)
     */
    private static String getValidatedInput(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("✗ Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());

        return input;
    }

    /**
     * Gets validated integer input within a range.
     * 
     * @param prompt The prompt to display
     * @param min    Minimum valid value
     * @param max    Maximum valid value
     * @return Valid integer input
     */
    private static int getValidatedIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);

                if (value < min || value > max) {
                    System.out.printf("✗ Value must be between %d and %d\n", min, max);
                    continue;
                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid number. Please enter an integer.");
            }
        }
    }

    /**
     * Gets validated double input within a range.
     * 
     * @param prompt The prompt to display
     * @param min    Minimum valid value
     * @param max    Maximum valid value
     * @return Valid double input
     */
    private static double getValidatedDoubleInput(String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                double value = Double.parseDouble(input);

                if (value < min || value > max) {
                    System.out.printf("✗ Value must be between %.2f and %.2f\n", min, max);
                    continue;
                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid number. Please enter a decimal value.");
            }
        }
    }
}
