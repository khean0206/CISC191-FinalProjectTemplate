package edu.sdccd.cisc191;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;

public class GpaCalculatorClient extends Application {

    private TextField creditField;
    private TextField gradeField;
    private TextArea resultArea;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private double totalCredits;
    private double totalGradePoints;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GPA Calculator");

        GridPane gridPane = createGridPane();
        Scene scene = new Scene(gridPane, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label creditLabel = new Label("Credit Hours:");
        GridPane.setConstraints(creditLabel, 0, 0);
        creditField = new TextField();
        GridPane.setConstraints(creditField, 1, 0);

        Label gradeLabel = new Label("Grade Points:");
        GridPane.setConstraints(gradeLabel, 0, 1);
        gradeField = new TextField();
        GridPane.setConstraints(gradeField, 1, 1);

        Button calculateButton = new Button("Calculate");
        GridPane.setConstraints(calculateButton, 0, 2);
        calculateButton.setOnAction(e -> calculateGpa());

        Button resetButton = new Button("Reset");
        GridPane.setConstraints(resetButton, 1, 2);
        resetButton.setOnAction(e -> resetData());

        resultArea = new TextArea();
        resultArea.setEditable(false);
        GridPane.setConstraints(resultArea, 0, 3, 2, 1);

        gridPane.getChildren().addAll(creditLabel, creditField, gradeLabel, gradeField, calculateButton, resetButton, resultArea);
        return gridPane;
    }

    private void calculateGpa() {
        try {
            double credits = Double.parseDouble(creditField.getText());
            double gradePoints = Double.parseDouble(gradeField.getText());

            GPARequest request = new GPARequest(credits, gradePoints);

            Socket clientSocket = new Socket("localhost", 8080);
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            outputStream.writeObject(request);
            outputStream.flush();

            GPAResponse response = (GPAResponse) inputStream.readObject();

            double gpa = response.getGpa();
            String message = response.getMessage();

            totalCredits += credits; // Update total credits
            totalGradePoints += gradePoints; // Update total grade points

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            resultArea.setText("GPA: " + decimalFormat.format(gpa) + "\n" + message);

            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (NumberFormatException | IOException | ClassNotFoundException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("An error occurred. Please try again.");
            errorAlert.showAndWait();
        }
    }

    private void resetData() {
        creditField.setText("");
        gradeField.setText("");
        resultArea.setText("");

        totalCredits = 0;
        totalGradePoints = 0;
    }
}
