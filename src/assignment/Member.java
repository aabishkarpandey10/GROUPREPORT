package assignment;

/**
 * Abstract base class representing a gym member.
 * Demonstrates abstraction and encapsulation principles.
 * Provides common attributes and behaviors for all member types.
 * 
 * @author [Your Group Members]
 * @version 1.0
 */
public abstract class Member {
    // Private attributes demonstrating encapsulation
    private String id;
    private String name;
    private int age;
    private double baseFee;
    private int performanceRating; // Range: 0-100
    private boolean achievedGoal;

    /**
     * Constructor to initialize a Member object.
     * 
     * @param id      Unique identifier for the member
     * @param name    Full name of the member
     * @param age     Age of the member
     * @param baseFee Base monthly fee before any discounts
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Member(String id, String name, int age, double baseFee) {
        validateId(id);
        validateName(name);
        validateAge(age);
        validateBaseFee(baseFee);

        this.id = id;
        this.name = name;
        this.age = age;
        this.baseFee = baseFee;
        this.performanceRating = 0;
        this.achievedGoal = false;
    }

    /**
     * Abstract method demonstrating polymorphism.
     * Each subclass must provide its own implementation for fee calculation.
     * 
     * @return The calculated monthly fee for this member
     */
    public abstract double calculateFee();

    /**
     * Applies a percentage-based discount to the base fee.
     * 
     * @param percent The discount percentage (0-100)
     */
    public void applyDiscountPercent(double percent) {
        if (percent <= 0 || percent > 100) {
            return;
        }
        double factor = 1.0 - (percent / 100.0);
        this.baseFee = this.baseFee * factor;
    }

    // Validation methods
    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be null or empty");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty");
        }
    }

    private void validateAge(int age) {
        if (age < 16 || age > 100) {
            throw new IllegalArgumentException("Age must be between 16 and 100");
        }
    }

    private void validateBaseFee(double baseFee) {
        if (baseFee < 0) {
            throw new IllegalArgumentException("Base fee cannot be negative");
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public int getPerformanceRating() {
        return performanceRating;
    }

    public boolean isAchievedGoal() {
        return achievedGoal;
    }

    // Setters with validation
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setAge(int age) {
        validateAge(age);
        this.age = age;
    }

    public void setBaseFee(double baseFee) {
        validateBaseFee(baseFee);
        this.baseFee = baseFee;
    }

    public void setPerformanceRating(int rating) {
        if (rating < 0 || rating > 100) {
            throw new IllegalArgumentException("Performance rating must be between 0 and 100");
        }
        this.performanceRating = rating;
    }

    public void setAchievedGoal(boolean achievedGoal) {
        this.achievedGoal = achievedGoal;
    }

    /**
     * Converts member data to CSV format for file storage.
     * 
     * @return CSV string representation of base member data
     */
    public String toCSVBase() {
        return String.join(",",
                sanitize(id),
                sanitize(name),
                Integer.toString(age),
                String.format("%.2f", baseFee),
                Integer.toString(performanceRating),
                Boolean.toString(achievedGoal));
    }

    /**
     * Sanitizes string data for CSV storage by removing commas.
     * 
     * @param s The string to sanitize
     * @return Sanitized string
     */
    protected String sanitize(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(",", ";").trim();
    }

    @Override
    public String toString() {
        return String.format("[ID: %s] %s | Age: %d | Type: %s | Performance: %d | Goal Achieved: %s | Base Fee: $%.2f",
                id, name, age, this.getClass().getSimpleName(),
                performanceRating, achievedGoal ? "Yes" : "No", baseFee);
    }
}
