package assignment;

/**
 * Represents a regular gym member without personal training services.
 * Demonstrates inheritance by extending the Member class.
 * Regular members receive a 10% discount if they achieve their fitness goals.
 * 
 * @author [Your Group Members]
 * @version 1.0
 */
public class RegularMember extends Member {

    /**
     * Constructor for RegularMember.
     * 
     * @param id      Unique identifier
     * @param name    Member's full name
     * @param age     Member's age
     * @param baseFee Base monthly fee
     */
    public RegularMember(String id, String name, int age, double baseFee) {
        super(id, name, age, baseFee);
    }

    /**
     * Calculates the monthly fee for a regular member.
     * Demonstrates polymorphism - overrides the abstract method from Member class.
     * Applies 10% discount if fitness goal is achieved.
     * 
     * @return The calculated monthly fee
     */
    @Override
    public double calculateFee() {
        double fee = getBaseFee();

        // Apply 10% discount if goal is achieved
        if (isAchievedGoal()) {
            fee *= 0.90;
        }

        return fee;
    }

    /**
     * Converts member data to CSV format for file storage.
     * 
     * @return CSV string representation
     */
    public String toCSV() {
        return "REGULAR," + toCSVBase();
    }

    /**
     * Creates a RegularMember object from CSV data.
     * 
     * @param id                Member ID
     * @param name              Member name
     * @param age               Member age
     * @param baseFee           Base fee amount
     * @param performanceRating Performance rating (0-100)
     * @param achievedGoal      Whether goal was achieved
     * @return RegularMember object
     */
    public static RegularMember fromCSV(String id, String name, int age,
            double baseFee, int performanceRating,
            boolean achievedGoal) {
        RegularMember member = new RegularMember(id, name, age, baseFee);
        member.setPerformanceRating(performanceRating);
        member.setAchievedGoal(achievedGoal);
        return member;
    }

    @Override
    public String toString() {
        return super.toString() + " | Member Type: Regular";
    }
}
