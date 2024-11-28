package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import ncnl.balayanexpensewise.beans.*;

import ncnl.balayanexpensewise.service.InboxService;
import ncnl.balayanexpensewise.service.LoggerService;
import ncnl.balayanexpensewise.utils.DisplayUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class CICSController implements Initializable {

    @FXML
    private TextField fieldCICSNameR1, fieldCICSAmountR1, fieldCICSPriceR1,
                      fieldCICSNameR2, fieldCICSAmountR2, fieldCICSPriceR2,
                      fieldCICSNameR3, fieldCICSAmountR3, fieldCICSPriceR3,
                      fieldCICSNameR4, fieldCICSAmountR4, fieldCICSPriceR4,
                      fieldCICSNameR5, fieldCICSAmountR5, fieldCICSPriceR5;


    @FXML
    private Label cicsNameR1, cicsAmountR1, cicsPriceR1, cicsBudgetR1,
                  cicsNameR2, cicsAmountR2, cicsPriceR2, cicsBudgetR2,
                  cicsNameR3, cicsAmountR3, cicsPriceR3, cicsBudgetR3,
                  cicsNameR4, cicsAmountR4, cicsPriceR4, cicsBudgetR4,
                  cicsNameR5, cicsAmountR5, cicsPriceR5, cicsBudgetR5,
                  budgetCICS;

    @FXML
    private PieChart chartCICS;

    @FXML
    private LineChart<String, Number> lineCICS;

    @FXML
    private BarChart<String, Number> barCICS;

    @FXML
    private JFXButton submitBtn, resetBtn;

    @FXML
    private ComboBox<String> comboBoxDateTime, comboBoxPeriod, comboBoxTransaction,
    categoryCICSR1, categoryCICSR2, categoryCICSR3, categoryCICSR4, categoryCICSR5;

    @FXML
    private ProgressBar progressBarCICS;

    private List<ExpenseRow> transactionRows;


    Calendar calendar = new Calendar();
    LoggerService loggerService = new LoggerService();
    InboxService inboxService = new InboxService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        comboBoxPeriod.getItems().addAll(calendar.getAllPeriods());
        comboBoxDateTime.getItems().addAll(calendar.getAllMonths());

        comboBoxTransaction.getItems().add("Expense");
        comboBoxTransaction.getItems().add("Service");

        comboBoxDateTime.setValue("January");
        comboBoxTransaction.setValue("Expense");
        comboBoxPeriod.setValue("Academic Year");

        List<ComboBox<String>> categoryBoxes = List.of(categoryCICSR1, categoryCICSR2, categoryCICSR3, categoryCICSR4, categoryCICSR5);
        DropBox.populateComboBoxes(categoryBoxes, List.of("Expense", "Service"));

        List<LabelRow> cicsRows = Arrays.asList(
                new LabelRow(cicsNameR1, cicsAmountR1, cicsPriceR1, cicsBudgetR1),
                new LabelRow(cicsNameR2, cicsAmountR2, cicsPriceR2, cicsBudgetR2),
                new LabelRow(cicsNameR3, cicsAmountR3, cicsPriceR3, cicsBudgetR3),
                new LabelRow(cicsNameR4, cicsAmountR4, cicsPriceR4, cicsBudgetR4),
                new LabelRow(cicsNameR5, cicsAmountR5, cicsPriceR5, cicsBudgetR5)
        );

        loadTransactionLogs(cicsRows);

        addLineDataToChart();
        addBarDataToChart();
        addDataToPieChart(chartCICS);

        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateData()),
                new KeyFrame(Duration.seconds(0), event -> loadTransactionLogs(cicsRows)),
                new KeyFrame(Duration.seconds(5)) // Trigger every 5 seconds
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

        transactionRows = new ArrayList<>();

        transactionRows.add(new ExpenseRow(fieldCICSNameR1, fieldCICSAmountR1,fieldCICSPriceR1, categoryCICSR1, "CICS"  ));
        transactionRows.add(new ExpenseRow(fieldCICSNameR2, fieldCICSAmountR2, fieldCICSPriceR2, categoryCICSR2 ,"CICS" ));
        transactionRows.add(new ExpenseRow(fieldCICSNameR3, fieldCICSAmountR3, fieldCICSPriceR3, categoryCICSR3 ,"CICS" ));
        transactionRows.add(new ExpenseRow(fieldCICSNameR4, fieldCICSAmountR4, fieldCICSPriceR4, categoryCICSR4 ,"CICS" ));
        transactionRows.add(new ExpenseRow(fieldCICSNameR5, fieldCICSAmountR5, fieldCICSPriceR5, categoryCICSR5 ,"CICS" ));

        transactionRows.forEach(row -> {
            row.getNameField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getQuantityField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getPriceField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getCategoryComboBox().valueProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
        });

        submitBtn.setDisable(true);

        progressBarCICS.setProgress(0);

    }


    private void validateProgressAndSubmit() {
        boolean hasCompleteRow = false;
        boolean hasPartialRow = false;

        for (ExpenseRow row : transactionRows) {
            boolean isRowComplete = isFieldFilled(row.getNameField())
                    && isFieldFilled(row.getQuantityField())
                    && isFieldFilled(row.getPriceField())
                    && isFieldFilled(row.getCategoryComboBox());
            boolean isRowPartial = isFieldFilled(row.getNameField())
                    || isFieldFilled(row.getQuantityField())
                    || isFieldFilled(row.getPriceField())
                    || isFieldFilled(row.getCategoryComboBox());

            boolean isPriceZero = isValidPrice(row.getPriceField().getText());
            boolean isQuantityZero = isValidQuantity(row.getQuantityField().getText());

            if (isPriceZero || isQuantityZero) {
                submitBtn.setDisable(true);
                return;
            }

            if (isRowComplete) {
                hasCompleteRow = true;
            } else if (isRowPartial) {
                hasPartialRow = true;
            }
        }

        if (hasPartialRow) {
            submitBtn.setDisable(true);
        } else {
            submitBtn.setDisable(!hasCompleteRow);
        }

        double totalFields = transactionRows.size() * 4;
        double filledFields = 0;

        for (ExpenseRow row : transactionRows) {
            boolean isRowComplete = isFieldFilled(row.getNameField())
                    && isFieldFilled(row.getQuantityField())
                    && isFieldFilled(row.getPriceField())
                    && isFieldFilled(row.getCategoryComboBox());
            if (isRowComplete) {
                filledFields += 6;
            } else {
                filledFields += (isFieldFilled(row.getNameField()) ? 1 : 0)
                        + (isFieldFilled(row.getQuantityField()) ? 1 : 0)
                        + (isFieldFilled(row.getPriceField()) ? 1 : 0)
                        + (isFieldFilled(row.getCategoryComboBox()) ? 1 : 0);
            }
        }

        double progress = filledFields / totalFields;
        progressBarCICS.setProgress(progress);
    }


    private void updateData() {
        lineCICS.getData().clear();
        addLineDataToChart();

        barCICS.getData().clear();
        addBarDataToChart();

        chartCICS.getData().clear();
        addDataToPieChart(chartCICS);

        budgetCICS.setText("₱ " + DisplayUtil.getBudget(Table.getTableName("CICS")));

    }

    private boolean isValidPrice(String priceText) {
        try {
            double price = Double.parseDouble(priceText);
            return price == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidQuantity(String quantityText) {
        try {
            double quantity = Double.parseDouble(quantityText);
            return quantity == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isFieldFilled(TextField field) {
        return field.getText() != null && !field.getText().trim().isEmpty();
    }

    private boolean isFieldFilled(ComboBox<?> comboBox) {
        return comboBox.getValue() != null && !comboBox.getValue().toString().isEmpty();
    }

    @FXML
    private void resetFields() {
        transactionRows.forEach(row -> {
            row.getNameField().clear();
            row.getQuantityField().clear();
            row.getPriceField().clear();
            row.getCategoryComboBox().setValue(null);
        });
        submitBtn.setDisable(true);
    }

    @FXML
    public void loadTransactionLogs(List<LabelRow> cicsRows) {
        List<TransactionLogger> transactionLogs = loggerService.getTransactionLogs("cics_transactions");
        Double totalPrice = 0.0;

        for (int i = 0; i < transactionLogs.size(); i++) {
            TransactionLogger transaction = transactionLogs.get(i);

            totalPrice += transaction.getAmount() * transaction.getPrice();

            LabelRow labelRow = cicsRows.get(i);

            transaction.updateLabels(
                    labelRow.getNameLabel(),
                    labelRow.getAmountLabel(),
                    labelRow.getPriceLabel(),
                    labelRow.getBudgetLabel()
            );
        }
    }

    private void addBarDataToChart() {
        String selectedMonth = comboBoxDateTime.getValue();


        BarChart.Series<String, Number> cicsSeries = new BarChart.Series<>();
        cicsSeries.setName("CICS");


        double cicsExpense = Double.parseDouble(DisplayUtil.getTotalExpenseByMonth(Table.getTableName("CICS"), selectedMonth));
        double cicsService = Double.parseDouble(DisplayUtil.getTotalServiceByMonth(Table.getTableName("CICS"), selectedMonth));

        cicsSeries.getData().add(new BarChart.Data<>("Expense", cicsExpense));
        cicsSeries.getData().add(new BarChart.Data<>("Service", cicsService));


        barCICS.getData().clear();
        barCICS.getData().add(cicsSeries);
    }


    private void addLineDataToChart() {
        String transactionCategory = comboBoxTransaction.getValue();
        String periodValue = comboBoxPeriod.getValue();

        XYChart.Series<String, Number> cicsExpenses = new XYChart.Series<>();

        List<Integer> expenseData;
        if ("Expense".equals(transactionCategory)) {
            expenseData = DisplayUtil.getYearlyExpensesFromDatabase(Table.getTableName("CICS"));
            cicsExpenses.setName("CICS Expense");
        } else {
            expenseData = DisplayUtil.getYearlyServicesFromDatabase(Table.getTableName("CICS"));
            cicsExpenses.setName("CICS Service");
        }

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int[] monthIndexes;

        if ("Academic Year".equalsIgnoreCase(periodValue)) {
            monthIndexes = new int[]{0, 11}; // Jan - Dec
        } else if ("1st Semester".equalsIgnoreCase(periodValue)) {
            monthIndexes = new int[]{7, 11}; // Aug - Dec
        } else if ("2nd Semester".equalsIgnoreCase(periodValue)) {
            monthIndexes = new int[]{0, 5}; // Jan - Jun
        } else {
            monthIndexes = new int[]{0, 11}; // Default to full year
        }

        for (int i = monthIndexes[0]; i <= monthIndexes[1]; i++) {
            int value = i < expenseData.size() ? expenseData.get(i) : 0;
            cicsExpenses.getData().add(new XYChart.Data<>(months[i], value));
        }

        lineCICS.getData().clear();
        lineCICS.getData().add(cicsExpenses);

    }

    /**
     * Adds data to the given PieChart based on the selected period value.
     *
     * @param pieChart the PieChart to be updated.
     */
    public void addDataToPieChart(PieChart pieChart) {
        String transactionCategory = comboBoxTransaction.getValue();
        String periodValue = comboBoxPeriod.getValue();

        List<Integer> cicsData;
        if ("Expense".equals(transactionCategory)) {
            cicsData = DisplayUtil.getYearlyExpensesFromDatabase(Table.getTableName("CICS"));
        } else {
            cicsData = DisplayUtil.getYearlyServicesFromDatabase(Table.getTableName("CICS"));
        }

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int startMonth, endMonth;

        switch (periodValue) {
            case "1st Semester":
                startMonth = 7; // August
                endMonth = 11; // December
                break;
            case "2nd Semester":
                startMonth = 0; // January
                endMonth = 5; // June
                break;
            case "Academic Year":
            default:
                startMonth = 0; // January
                endMonth = 11; // December
                break;
        }

        pieChart.getData().clear();

        for (int i = startMonth; i <= endMonth; i++) {
            int value = i < cicsData.size() ? cicsData.get(i) : 0;
            if (value > 0) {
                pieChart.getData().add(new PieChart.Data(months[i], value));
            }
        }
    }


    private boolean isRowValid(ExpenseRow row) {
        return isFieldFilled(row.getNameField())
                && isFieldFilled(row.getQuantityField())
                && isFieldFilled(row.getPriceField())
                && isFieldFilled(row.getCategoryComboBox());
    }

    @FXML
    public void handleSubmit(javafx.event.ActionEvent actionEvent) {
        transactionRows.forEach(row -> {
            if (isRowValid(row)) {
                String name = row.getNameField().getText();
                String department = "CICS";
                String category = row.getCategoryComboBox().getValue();
                int amount = Integer.parseInt(row.getQuantityField().getText());
                double price = Double.parseDouble(row.getPriceField().getText());



                Inbox transaction = new Inbox(name, department, category, amount, price);
                inboxService.addInboxRecord(transaction);


            }
        });
        resetFields();
    }

    public void clearFields(ActionEvent actionEvent) {
        resetFields();
    }

}
