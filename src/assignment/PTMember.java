package assignment;

/**
 * Represents a gym member with personal training services.
 * Demonstrates inheritance by extending the Member class.
 * PT members have additional trainer fees and receive tiered discounts based on
 * performance.
 * 
 * @author [Your Group Members]
 * @version 1.0
 */
public class PTMember extends Member {
    private double trainerFee;

    /**
     * Constructor for PTMember.
     * 
     * @param id         Unique identifier
     * @param name       Member's full name
     * @param age        Member's age
     * @param baseFee    Base monthly fee
     * @param trainerFee Additional personal trainer fee
     */
    public PTMember(String id, String name, int age, double baseFee, double trainerFee) {
        super(id, name, age, baseFee);
        validateTrainerFee(trainerFee);
        this.trainerFee = trainerFee;
    }

    /**
     * Validates trainer fee.
     * 
     * @param trainerFee The fee to validate
     * @throws IllegalArgumentException if fee is negative
     */
    private void validateTrainerFee(double trainerFee) {
        if (trainerFee < 0) {
            throw new IllegalArgumentException("Trainer fee cannot be negative");
        }
    }

    public double getTrainerFee() {
        return trainerFee;
    }

    public void setTrainerFee(double trainerFee) {
        validateTrainerFee(trainerFee);
        this.trainerFee = trainerFee;
    }

    /**
     * Calculates the monthly fee for a PT member.
     * Demonstrates polymorphism - overrides the abstract method from Member class.
     * 
     * Fee Calculation:
     * - Base fee + trainer fee
     * - Performance >= 90: 15% discount
     * - Performance >= 75: 8% discount
     * - Goal achieved: additional 5% discount
     * 
     * @return The calculated monthly fee
     */
    @Override
    public double calculateFee() {
        double fee = getBaseFee() + trainerFee;

        // Apply performance-based discounts
        if (getPerformanceRating() >= 90) {
            fee *= 0.85; // 15% discount
        } else if (getPerformanceRating() >= 75) {
            fee *= 0.92; // 8% discount
        }

        // Apply goal achievement discount
        if (isAchievedGoal()) {
            fee *= 0.95; // Additional 5% discount
        }

        return fee;
    }

    /**
     * Converts member data to CSV format for file storage.
     * 
     * @return CSV string representation
     */
    public String toCSV() {
        return "PT," + toCSVBase() + "," + String.format("%.2f", trainerFee);
    }

    /**
     * Creates a PTMember object from CSV data.
     * 
     * @param id                Member ID
     * @param name              Member name
     * @param age               Member age
     * @param baseFee           Base fee amount
     * @param performanceRating Performance rating (0-100)
     * @param achievedGoal      Whether goal was achieved
     * @param trainerFee        Personal trainer fee
     * @return PTMember object
     */
    public static PTMember fromCSVParts(String id, String name, int age,
            double baseFee, int performanceRating,
            boolean achievedGoal, double trainerFee) {
        PTMember member = new PTMember(id, name, age, baseFee, trainerFee);
        member.setPerformanceRating(performanceRating);
        member.setAchievedGoal(achievedGoal);
        return member;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Trainer Fee: $%.2f", trainerFee);
    }
}
