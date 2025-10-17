package assignment;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages all member operations including CRUD operations and file I/O.
 * Utilizes ArrayList for efficient random access and iteration.
 * Demonstrates exception handling for file operations and data validation.
 * 
 * @author [Your Group Members]
 * @version 1.0
 */
public class MemberManager {
    // Using ArrayList for efficient random access and simple operations
    private List<Member> members;
    private String filePath;

    /**
     * Constructor initializes the member manager with a file path.
     * 
     * @param filePath Path to the CSV file for data persistence
     */
    public MemberManager(String filePath) {
        this.members = new ArrayList<>();
        this.filePath = filePath;
    }

    /**
     * Adds a new member to the system.
     * 
     * @param member The member to add
     * @throws IllegalArgumentException if member is null or ID already exists
     */
    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Cannot add null member");
        }

        if (getById(member.getId()) != null) {
            throw new IllegalArgumentException("Member with ID " + member.getId() + " already exists");
        }

        members.add(member);
        System.out.println("✓ Member added successfully: " + member.getName());
    }

    /**
     * Retrieves a member by their unique ID.
     * 
     * @param id The member ID to search for
     * @return The member object if found, null otherwise
     */
    public Member getById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (Member member : members) {
            if (member.getId().equalsIgnoreCase(id.trim())) {
                return member;
            }
        }
        return null;
    }

    /**
     * Deletes a member by their ID.
     * 
     * @param id The ID of the member to delete
     * @return true if member was deleted, false otherwise
     */
    public boolean deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        boolean removed = members.removeIf(m -> m.getId().equalsIgnoreCase(id.trim()));
        if (removed) {
            System.out.println("✓ Member deleted successfully");
        }
        return removed;
    }

    /**
     * Searches for members by name (partial match, case-insensitive).
     * 
     * @param query The search query
     * @return List of matching members
     */
    public List<Member> searchByName(String query) {
        List<Member> results = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase().trim();
        for (Member member : members) {
            if (member.getName().toLowerCase().contains(lowerQuery)) {
                results.add(member);
            }
        }
        return results;
    }

    /**
     * Searches for members within a performance rating range.
     * 
     * @param minRating Minimum performance rating (inclusive)
     * @param maxRating Maximum performance rating (inclusive)
     * @return List of matching members
     */
    public List<Member> searchByPerformance(int minRating, int maxRating) {
        List<Member> results = new ArrayList<>();

        if (minRating < 0 || maxRating > 100 || minRating > maxRating) {
            System.out.println("Invalid performance range");
            return results;
        }

        for (Member member : members) {
            int rating = member.getPerformanceRating();
            if (rating >= minRating && rating <= maxRating) {
                results.add(member);
            }
        }
        return results;
    }

    /**
     * Prints all members in the system with their calculated fees.
     */
    public void printAllMembers() {
        if (members.isEmpty()) {
            System.out.println("\n⚠ No members in the system.");
            return;
        }

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("                    ALL MEMBERS (" + members.size() + ")");
        System.out.println("═══════════════════════════════════════════════════════════");

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            System.out.printf("%d. %s\n", (i + 1), member);
            System.out.printf("   Monthly Fee: $%.2f\n", member.calculateFee());
            System.out.println("───────────────────────────────────────────────────────────");
        }
    }

    /**
     * Saves all member data to the CSV file.
     * Demonstrates exception handling for file operations.
     */
    public void saveToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath))) {
            // Write header
            writer.write("Type,ID,Name,Age,BaseFee,PerformanceRating,AchievedGoal,TrainerFee");
            writer.newLine();

            // Write member data
            for (Member member : members) {
                if (member instanceof PTMember) {
                    writer.write(((PTMember) member).toCSV());
                } else {
                    writer.write(((RegularMember) member).toCSV());
                }
                writer.newLine();
            }

            System.out.println("✓ Data saved successfully to " + filePath);
        } catch (IOException e) {
            System.err.println("✗ Error saving file: " + e.getMessage());
            System.err.println("Please check file permissions and disk space.");
        } catch (Exception e) {
            System.err.println("✗ Unexpected error during save: " + e.getMessage());
        }
    }

    /**
     * Loads member data from the CSV file.
     * Demonstrates exception handling for file operations and data parsing.
     */
    public void loadFromFile() {
        members.clear();
        Path path = Path.of(filePath);

        if (!Files.exists(path)) {
            System.out.println("⚠ File not found: " + filePath);
            System.out.println("Creating new empty file...");
            ensureSampleData();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip empty lines and header
                if (line.trim().isEmpty() || lineNumber == 1) {
                    continue;
                }

                try {
                    parseMemberFromCSV(line);
                } catch (Exception e) {
                    System.err.println("⚠ Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            }

            System.out.println("✓ Loaded " + members.size() + " members from file");
        } catch (IOException e) {
            System.err.println("✗ Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Unexpected error during load: " + e.getMessage());
        }
    }

    /**
     * Parses a CSV line and creates the appropriate Member object.
     * 
     * @param line CSV line to parse
     * @throws IllegalArgumentException if line format is invalid
     */
    private void parseMemberFromCSV(String line) {
        String[] parts = line.split(",");

        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid CSV format - insufficient fields");
        }

        String type = parts[0].trim();
        String id = parts[1].trim();
        String name = parts[2].trim();
        int age = Integer.parseInt(parts[3].trim());
        double baseFee = Double.parseDouble(parts[4].trim());
        int performanceRating = Integer.parseInt(parts[5].trim());
        boolean achievedGoal = Boolean.parseBoolean(parts[6].trim());

        Member member;

        if (type.equalsIgnoreCase("PT")) {
            if (parts.length < 8) {
                throw new IllegalArgumentException("PT member missing trainer fee");
            }
            double trainerFee = Double.parseDouble(parts[7].trim());
            member = PTMember.fromCSVParts(id, name, age, baseFee,
                    performanceRating, achievedGoal, trainerFee);
        } else {
            member = RegularMember.fromCSV(id, name, age, baseFee,
                    performanceRating, achievedGoal);
        }

        members.add(member);
    }

    /**
     * Issues reminder letters to members with low performance ratings.
     * 
     * @param maxRating Maximum performance rating to receive reminder
     */
    public void issueReminderLetters(int maxRating) {
        System.out.println("\n ISSUING REMINDER LETTERS ");
        int count = 0;

        for (Member member : members) {
            if (member.getPerformanceRating() <= maxRating) {
                System.out.printf("📧 Reminder sent to: %s (Rating: %d)\n",
                        member.getName(), member.getPerformanceRating());
                count++;
            }
        }

        System.out.println("Total reminders sent: " + count);
    }

    /**
     * Issues appreciation letters to members with high performance ratings.
     * 
     * @param minRating Minimum performance rating to receive appreciation
     */
    public void issueAppreciationLetters(int minRating) {
        System.out.println("\n ISSUING APPRECIATION LETTERS");
        int count = 0;

        for (Member member : members) {
            if (member.getPerformanceRating() >= minRating) {
                System.out.printf("🎉 Appreciation sent to: %s (Rating: %d)\n",
                        member.getName(), member.getPerformanceRating());
                count++;
            }
        }

        System.out.println("Total appreciations sent: " + count);
    }

    /**
     * Awards discounts to high-performing members.
     * 
     * @param minRating       Minimum performance rating to receive discount
     * @param discountPercent Discount percentage to apply
     */
    public void awardDiscounts(int minRating, double discountPercent) {
        System.out.println("\n═══ AWARDING DISCOUNTS ═══");
        int count = 0;

        for (Member member : members) {
            if (member.getPerformanceRating() >= minRating) {
                member.applyDiscountPercent(discountPercent);
                System.out.printf("💰 %.1f%% discount awarded to: %s\n",
                        discountPercent, member.getName());
                count++;
            }
        }

        System.out.println("Total discounts awarded: " + count);
    }

    /**
     * Creates a sample data file with predefined members if file doesn't exist.
     */
    public void ensureSampleData() {
        Path path = Path.of(filePath);

        if (Files.exists(path)) {
            return;
        }

        try {
            // Create sample data with header
            List<String> sampleData = Arrays.asList(
                    "Type,ID,Name,Age,BaseFee,PerformanceRating,AchievedGoal,TrainerFee");

            Files.write(path, sampleData);
            System.out.println("✓ Sample data file created: " + filePath);
        } catch (IOException e) {
            System.err.println("✗ Error creating sample file: " + e.getMessage());
        }
    }

    /**
     * Returns the total number of members.
     * 
     * @return Member count
     */
    public int getMemberCount() {
        return members.size();
    }
}
