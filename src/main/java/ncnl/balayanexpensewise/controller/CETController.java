package ncnl.balayanexpensewise.controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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

public class CETController implements Initializable {

    @FXML
    private TextField fieldCETNameR1, fieldCETAmountR1, fieldCETPriceR1,
            fieldCETNameR2, fieldCETAmountR2, fieldCETPriceR2,
            fieldCETNameR3, fieldCETAmountR3, fieldCETPriceR3,
            fieldCETNameR4, fieldCETAmountR4, fieldCETPriceR4,
            fieldCETNameR5, fieldCETAmountR5, fieldCETPriceR5;


    @FXML
    private Label cetNameR1, cetAmountR1, cetPriceR1, cetBudgetR1,
            cetNameR2, cetAmountR2, cetPriceR2, cetBudgetR2,
            cetNameR3, cetAmountR3, cetPriceR3, cetBudgetR3,
            cetNameR4, cetAmountR4, cetPriceR4, cetBudgetR4,
            cetNameR5, cetAmountR5, cetPriceR5, cetBudgetR5,
            budgetCET;


    @FXML
    private PieChart chartCET;

    @FXML
    private LineChart<String, Number> lineCET;

    @FXML
    private BarChart<String, Number> barCET;

    @FXML
    private JFXButton submitBtn, resetBtn;

    @FXML
    private ComboBox<String> comboBoxDateTime, comboBoxPeriod, comboBoxTransaction,
            categoryCETR1, categoryCETR2, categoryCETR3, categoryCETR4, categoryCETR5;

    @FXML
    private ProgressBar progressBarCET;

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

        List<ComboBox<String>> categoryBoxes = List.of(categoryCETR1, categoryCETR2, categoryCETR3, categoryCETR4, categoryCETR5);
        DropBox.populateComboBoxes(categoryBoxes, List.of("Expense", "Service"));


        List<LabelRow> cetRows = Arrays.asList(
                new LabelRow(cetNameR1, cetAmountR1, cetPriceR1, cetBudgetR1),
                new LabelRow(cetNameR2, cetAmountR2, cetPriceR2, cetBudgetR2),
                new LabelRow(cetNameR3, cetAmountR3, cetPriceR3, cetBudgetR3),
                new LabelRow(cetNameR4, cetAmountR4, cetPriceR4, cetBudgetR4),
                new LabelRow(cetNameR5, cetAmountR5, cetPriceR5, cetBudgetR5)
        );


        transactionRows = new ArrayList<>();

        transactionRows.add(new ExpenseRow(fieldCETNameR1, fieldCETAmountR1, fieldCETPriceR1, categoryCETR1, "CET"));
        transactionRows.add(new ExpenseRow(fieldCETNameR2, fieldCETAmountR2, fieldCETPriceR2, categoryCETR2, "CET"));
        transactionRows.add(new ExpenseRow(fieldCETNameR3, fieldCETAmountR3, fieldCETPriceR3, categoryCETR3, "CET"));
        transactionRows.add(new ExpenseRow(fieldCETNameR4, fieldCETAmountR4, fieldCETPriceR4, categoryCETR4, "CET"));
        transactionRows.add(new ExpenseRow(fieldCETNameR5, fieldCETAmountR5, fieldCETPriceR5, categoryCETR5, "CET"));

        transactionRows.forEach(row -> {
            row.getNameField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getQuantityField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getPriceField().textProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
            row.getCategoryComboBox().valueProperty().addListener((observable, oldValue, newValue) -> validateProgressAndSubmit());
        });

        loadTransactionLogs(cetRows);

        addLineDataToChart();
        addBarDataToChart();
        addDataToPieChart(chartCET);

        Timeline updateTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> updateData()),
                new KeyFrame(Duration.seconds(0), event -> loadTransactionLogs(cetRows)),
                new KeyFrame(Duration.seconds(5)) // Trigger every 5 seconds
        );
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

        submitBtn.setDisable(true);

        progressBarCET.setProgress(0);

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
        progressBarCET.setProgress(progress);
    }

    private void updateData() {
        lineCET.getData().clear();
        addLineDataToChart();

        barCET.getData().clear();
        addBarDataToChart();

        chartCET.getData().clear();
        addDataToPieChart(chartCET);

        budgetCET.setText("₱ " + DisplayUtil.getBudget(Table.getTableName("CET")));


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
    public void loadTransactionLogs(List<LabelRow> cetRows) {
        List<TransactionLogger> transactionLogs = loggerService.getTransactionLogs("cet_transactions");
        Double totalPrice = 0.0;

        for (int i = 0; i < transactionLogs.size(); i++) {
            TransactionLogger transaction = transactionLogs.get(i);

            totalPrice += transaction.getAmount() * transaction.getPrice();

            LabelRow labelRow = cetRows.get(i);

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


        BarChart.Series<String, Number> cetSeries = new BarChart.Series<>();
        cetSeries.setName("CET");


        double cicsExpense = Double.parseDouble(DisplayUtil.getTotalExpenseByMonth(Table.getTableName("CET"), selectedMonth));
        double cicsService = Double.parseDouble(DisplayUtil.getTotalServiceByMonth(Table.getTableName("CET"), selectedMonth));

        cetSeries.getData().add(new BarChart.Data<>("Expense", cicsExpense));
        cetSeries.getData().add(new BarChart.Data<>("Service", cicsService));


        barCET.getData().clear();
        barCET.getData().add(cetSeries);
    }


    private void addLineDataToChart() {
        String transactionCategory = comboBoxTransaction.getValue();
        String periodValue = comboBoxPeriod.getValue();

        XYChart.Series<String, Number> cetLineSeries = new XYChart.Series<>();

        List<Integer> expenseData;
        if ("Expense".equals(transactionCategory)) {
            expenseData = DisplayUtil.getYearlyExpensesFromDatabase(Table.getTableName("CET"));
            cetLineSeries.setName("CET Expense");
        } else {
            expenseData = DisplayUtil.getYearlyServicesFromDatabase(Table.getTableName("CET"));
            cetLineSeries.setName("CET Service");
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
            monthIndexes = new int[]{0, 11};
        }

        for (int i = monthIndexes[0]; i <= monthIndexes[1]; i++) {
            int value = i < expenseData.size() ? expenseData.get(i) : 0;
            cetLineSeries.getData().add(new XYChart.Data<>(months[i], value));
        }

        lineCET.getData().clear();
        lineCET.getData().add(cetLineSeries);

    }
    /**
     * Updates the PieChart data based on the selected period.
     *
     * @param periodValue the selected period value from the ComboBox (e.g., "Academic Year", "1st Semester", "2nd Semester").
     * @param yearlyPieChart the PieChart to be updated.
     */
    /**
     * Adds data to the given PieChart based on the selected period value.
     *
     * @param pieChart the PieChart to be updated.
     */
    public void addDataToPieChart(PieChart pieChart) {
        String transactionCategory = comboBoxTransaction.getValue();
        String periodValue = comboBoxPeriod.getValue();

        List<Integer> cetData;
        if ("Expense".equals(transactionCategory)) {
            cetData = DisplayUtil.getYearlyExpensesFromDatabase(Table.getTableName("CET"));
        } else {
            cetData = DisplayUtil.getYearlyServicesFromDatabase(Table.getTableName("CET"));
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
            int value = i < cetData.size() ? cetData.get(i) : 0;
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
                String department = "CET";
                String category = row.getCategoryComboBox().getValue();
                int amount = Integer.parseInt(row.getQuantityField().getText());
                double price = Double.parseDouble(row.getPriceField().getText());



                Inbox transaction = new Inbox(name, department, category,amount, price);
                inboxService.addInboxRecord(transaction);

            }
        });
        resetFields();
    }

    public void clearFields(ActionEvent actionEvent) {
        resetFields();
    }
}


