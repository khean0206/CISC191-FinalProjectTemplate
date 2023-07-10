package edu.sdccd.cisc191;

import java.io.Serializable;

public class GPARequest implements Serializable {
    private double credits;
    private double gradePoints;

    public GPARequest(double credits, double gradePoints) {
        this.credits = credits;
        this.gradePoints = gradePoints;
    }

    public double getCredits() {
        return credits;
    }

    public double getGradePoints() {
        return gradePoints;
    }
}
