package edu.sdccd.cisc191;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GpaCalculatorServerTest {

    public static void main(String[] args) {
        GpaCalculatorServer server = new GpaCalculatorServer();
        Thread serverThread = new Thread(() -> server.start());
        serverThread.start();

        try {
            // Test scenarios
            testGpaCalculation(4, 3.7); // Test GPA calculation
            testGpaCalculation(3, 3.2); // Test GPA calculation
            testGpaReset(); // Test GPA reset
            testGpaCalculation(2, 3.9); // Test GPA calculation after reset
            testGpaError(); // Test GPA calculation error
        } finally {
            // Stop the server
            serverThread.interrupt();
        }
    }

    private static void testGpaCalculation(double credits, double gradePoints) {
        try (Socket clientSocket = new Socket("localhost", 8080);
             ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {

            GPARequest request = new GPARequest(credits, gradePoints);
            outputStream.writeObject(request);
            outputStream.flush();

            GPAResponse response = (GPAResponse) inputStream.readObject();
            System.out.println("GPA Calculation - Credits: " + credits + ", Grade Points: " + gradePoints);
            System.out.println("GPA: " + response.getGpa());
            System.out.println("Message: " + response.getMessage());
            System.out.println();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void testGpaReset() {
        try (Socket clientSocket = new Socket("localhost", 8080);
             ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {

            GPARequest request = new GPARequest(0, 0);
            outputStream.writeObject(request);
            outputStream.flush();

            GPAResponse response = (GPAResponse) inputStream.readObject();
            System.out.println("GPA Reset");
            System.out.println("GPA: " + response.getGpa());
            System.out.println("Message: " + response.getMessage());
            System.out.println();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void testGpaError() {
        try (Socket clientSocket = new Socket("localhost", 8080);
             ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {

            // Invalid input: gradePoints cannot be negative
            GPARequest request = new GPARequest(4, -1);
            outputStream.writeObject(request);
            outputStream.flush();

            GPAResponse response = (GPAResponse) inputStream.readObject();
            System.out.println("GPA Calculation Error - Credits: " + request.getCredits() + ", Grade Points: " + request.getGradePoints());
            System.out.println("GPA: " + response.getGpa());
            System.out.println("Message: " + response.getMessage());
            System.out.println();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
