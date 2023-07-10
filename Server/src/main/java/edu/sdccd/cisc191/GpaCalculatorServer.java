package edu.sdccd.cisc191;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;

public class GpaCalculatorServer {

    private double totalCredits;
    private double totalGradePoints;

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("GPA Calculator Server is running on port 8080...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

                Object object = inputStream.readObject();
                if (object instanceof GPARequest) {
                    GPARequest request = (GPARequest) object;
                    double credits = request.getCredits();
                    double gradePoints = request.getGradePoints();

                    if (credits == 0 && gradePoints == 0) {
                        resetGpa();
                        GPAResponse response = new GPAResponse(0, "GPA reset successfully");
                        outputStream.writeObject(response);
                    } else {
                        totalCredits += credits;
                        totalGradePoints += gradePoints;

                        double gpa = totalGradePoints / totalCredits;
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");

                        GPAResponse response = new GPAResponse(gpa, "GPA calculated successfully");
                        outputStream.writeObject(response);
                    }
                    outputStream.flush();
                }

                inputStream.close();
                outputStream.close();
                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void resetGpa() {
        totalCredits = 0;
        totalGradePoints = 0;
    }

    public static void main(String[] args) {
        GpaCalculatorServer server = new GpaCalculatorServer();
        server.start();
    }
}
