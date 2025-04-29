package com.personalprojects.expensetracker;

/**
 * 
 *@author Darren Baker
 * 1/10/24
 * 
 */

import com.personalprojects.expensetracker.dao.ExpenseDAO;
import com.personalprojects.expensetracker.model.Expense;

import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Pos;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.util.StringConverter;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;

public class ExpenseTracker extends Application {
    private final ExpenseDAO dao = new ExpenseDAO();
    private final TableView<Expense> table = new TableView<>();
    private final ObservableList<Expense> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        
        BorderPane root = new BorderPane();
        //Form controls
        //Create category dropdown
        Label catLabel = new Label("Category");
        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll("Food","Entertainment","Auto", "Subscriptions", "Bills", 
                "Other");
        VBox catContainer = new VBox(5, catLabel, catBox);
        
        //Create Name and Amount text field
        Label nameLabel = new Label("Name");
        TextField nameFld = new TextField();
        VBox nameContainer = new VBox(5, nameLabel, nameFld);
        
        Label amtLabel = new Label("Amount");
        TextField amtFld  = new TextField();
        
        // Currency Formatter, enforces numeric pattern and adds $ to text
        StringConverter<Double> currencyConverter = new StringConverter<>() {
            @Override
            public String toString(Double value) {
            if (value == null) return "";
            return String.format("$%.2f", value);
        }
        @Override
        public Double fromString(String text) {
            if (text == null || text.isBlank()) return 0.0;
            // remove any non-digit and non-dot chars (e.g. “$”)
            String numeric = text.replaceAll("[^\\d.]", "");
            if (numeric.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(numeric);
        }
        };
        
        UnaryOperator<TextFormatter.Change> filter = change -> {
        String newText = change.getControlNewText();
            if (newText.matches("\\$?\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        };
        
        TextFormatter<Double> currencyFormatter =
            new TextFormatter<>(currencyConverter, 0.00, filter);
        amtFld.setTextFormatter(currencyFormatter);
        VBox amtContainer = new VBox(5, amtLabel, amtFld);
        
        Label dateLabel = new Label("Date");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        VBox dateContainer = new VBox(5, dateLabel, datePicker);
        
        //Create action buttons
        Button addBtn = new Button("Add");
        Button deleteBtn = new Button("Delete");
        
        //Only allow delete button press when an item is selected
        deleteBtn.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldsel, 
                newSel) -> {deleteBtn.setDisable(newSel == null);
                
        });
        
        //Build form HBox
        HBox form = new HBox(10, catContainer, nameContainer, amtContainer, 
                dateContainer, addBtn, deleteBtn);
        form.setPadding(new Insets(10));
        root.setTop(form);

        //Build table
        TableColumn<Expense,String> c1 = new TableColumn<>("Category");
        c1.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getCategory()));
        
        TableColumn<Expense,String> c2 = new TableColumn<>("Name");
        c2.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getName()));
        
        TableColumn<Expense,Number> c3 = new TableColumn<>("Amount");
        c3.setCellValueFactory(p -> new ReadOnlyDoubleWrapper(p.getValue().getAmount()));
        
        //Display $ before amount value in table
        c3.setCellFactory(column -> new TableCell<Expense, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                }
                else {
                    setText(String.format("$%.2f", value.doubleValue()));
                }
            }
        });
        
        TableColumn<Expense,String> c4 = new TableColumn<>("Date");
        c4.setCellValueFactory(p -> 
            new ReadOnlyStringWrapper(p.getValue().getDate().toString()));

        table.getColumns().addAll(c1,c2,c3,c4);
        table.setItems(data);
        root.setCenter(table);
        
        //Event handlers for buttons
        
        //Add expense item to table
        addBtn.setOnAction(ev -> {
            try {
                /*TextFormatter<Double> tf = (TextFormatter<Double>) amtFld.getTextFormatter();
                double amount = tf.getValue();*/
                double amount = currencyFormatter.getValue();
                Expense e = new Expense(
                  catBox.getValue(),
                  nameFld.getText(),
                  amount,
                  datePicker.getValue()
                );
                dao.addExpense(e);
                refreshTable();
                nameFld.clear();
                currencyFormatter.setValue(0.0); //Reset value text to $0.00
            } catch (SQLException ex) {
                showError(ex);
            }
        });
        
        //Delete expense item from table
        deleteBtn.setOnAction(ev -> {
            Expense selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Remove ''" + selected.getName() + "'' of " + selected.getAmount() +
                    "?", ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText("CONFIRM DELETE");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    dao.deleteExpense(selected.getId());
                    refreshTable();
                }
                catch (SQLException e) {
                    showError(e);
                }
            }
        });
        
        //Initial loadup
        refreshTable();
        stage.setScene(new Scene(root, 800, 800));
        stage.setTitle("Expense Tracker");
        stage.show();
    }
    
    //Refreshes table to show newly added data
    private void refreshTable() {
        try {
            data.setAll(dao.getAllExpenses());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    //Simple method for displaying error to user when an exception is thrown
    private void showError(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
        a.showAndWait();
    }

    //Launch app
    public static void main(String[] args) {
        launch();
    }
}
