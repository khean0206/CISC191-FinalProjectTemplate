package edu.sdccd.cisc191;

import java.io.Serializable;

public class GPAResponse implements Serializable {
    private double gpa;
    private String message;

    public GPAResponse(double gpa, String message) {
        this.gpa = gpa;
        this.message = message;
    }

    public double getGpa() {
        return gpa;
    }

    public String getMessage() {
        return message;
    }
}
