package pkg.calculator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.*;
import java.math.*;

public class UiController implements Initializable {
    /**
     * **************** Data/Properties******************************
     */
    @FXML
    private Label resultLine0;
    @FXML
    private Label resultLine1;
    @FXML
    private Label resultLine2;

    int MAXDIGITS = 11;
    NumberFormat formatter = new DecimalFormat();

    int currentLine = 0;

    // Boolean isLeadingZero = false;
    Boolean[] hasPeriod = new Boolean[2];
    Boolean isPositive = true;
    Boolean isCalculationCompleted = false;
    String numberPressed = new String();

    Label[] resultLines = new Label[3];
    double[] numbers = new double[3];
    int[] countDigitsInLine = new int[2];
    String operator = "";
    String lastOperator = "";

    private ModelCalculate modelCalculate = new ModelCalculate();

    /**
     * **************** Methods******************************
     */
    @FXML
    private void btnNumber(ActionEvent event) {
        if (isCalculationCompleted) {
            isCalculationCompleted = false;
            for (double d : numbers) d = 0.0;
            for (int i : countDigitsInLine) i = 0;
            for (Label l : resultLines) l.setText("");
            resetCurrentLine(0);
        }
        numberPressed = ((Button) event.getSource()).getText();
        if (countDigitsInLine[currentLine] < MAXDIGITS) {
            if (countDigitsInLine[currentLine] == 0) {
                if (numberPressed.equals(".")) {
                    hasPeriod[currentLine] = true;
                    resultLines[currentLine].setText(resultLines[currentLine].getText() + "0.");
                    countDigitsInLine[currentLine] = 2;
                } else {
                    appendDigitToCurrentLine();
                }

            } else if (countDigitsInLine[currentLine] == 1) {
                if (hasOnlyZero()) {
                    resultLines[currentLine].setText(numberPressed);
                    return;
                } else {
                    if (numberPressed.equals(".")) {
                        hasPeriod[currentLine] = true;
                    }
                    appendDigitToCurrentLine();
                    return;
                }
            }//if 2nd digit
            else {//countDigitsInLine[currentLine] from 2 to 12
                if (numberPressed.equals(".")) {
                    if (!hasPeriod[currentLine]) {
                        hasPeriod[currentLine] = true;
                        appendDigitToCurrentLine();
                    }
                } else {
                    appendDigitToCurrentLine();
                }
            }
        }//if countDigitsInLine[currentLine] NLT 12
        else if (!hasPeriod[currentLine]) {
            if (numberPressed.equals(".")) {
                hasPeriod[currentLine] = true;
                return;
            } else {
                if (currentLine == 0) {
                    numbers[currentLine] = 10.0 * Double.parseDouble(resultLines[currentLine].getText());
                    countDigitsInLine[currentLine]++;
                    formatter = new DecimalFormat("0.000000E0");
                    resultLines[currentLine].setText(formatter.format(numbers[currentLine]));
                } else {
                    numbers[currentLine] = 10.0 * Double.parseDouble(resultLines[currentLine].getText().substring(2));
                    countDigitsInLine[currentLine]++;
                    formatter = new DecimalFormat("0.000000E0");
                    resultLines[currentLine].setText(operator + " " + formatter.format(numbers[currentLine]));
                }
            }
        }
    }

    private Boolean hasPeriod() {
        return resultLines[currentLine].getText().matches("[.]+");
    }

    private Boolean hasOnlyZero() {
        if (currentLine == 0) {
            return resultLines[0].getText().matches("[0]+");
        } else {
            return resultLines[1].getText().substring(2).matches("[0]+");
        }
    }

    private void appendDigitToCurrentLine() {
        resultLines[currentLine].setText(resultLines[currentLine].getText() + numberPressed);
        countDigitsInLine[currentLine]++;
    }

    @FXML
    private void btnSignSwitch(ActionEvent event) {
        if (!isCalculationCompleted) {
            if (isPositive) {
                isPositive = false;
                if (currentLine == 0) {
                    resultLines[currentLine].setText("-" + resultLines[currentLine].getText());
                } else {//currentLine == 1
                    resultLines[currentLine].setText(operator + " -" + resultLines[currentLine].getText().substring(2));
                }
            } else {
                isPositive = true;
                if (currentLine == 0) {
                    resultLines[currentLine].setText(resultLines[currentLine].getText().substring(1));
                } else {//currentLine == 1
                    resultLines[currentLine].setText(operator + " " + resultLines[currentLine].getText().substring(3));
                }
            }
        }
    }

    @FXML
    private void btnClear(ActionEvent event) {
        if (isCalculationCompleted) {
            for (Label i : resultLines) {
                i.setText("");
            }
            resetCurrentLine(0);
        } else {
            if (currentLine == 1) {
                if (resultLines[1].getText().matches(".*\\d+.*")) {
                    resetCurrentLine(1);
                    resultLines[1].setText(operator + " ");
                } else {
                    resetCurrentLine(1);
                    currentLine = 0;
                }
            } else {//currentLine == 0
                resetCurrentLine(0);
            }
        }
    }

    private void resetCurrentLine(int lineNumber) {
        currentLine = lineNumber;
        resultLines[currentLine].setText("");
        countDigitsInLine[currentLine] = 0;
        hasPeriod[currentLine] = false;
        isPositive = true;
    }

    @FXML
    private void btnOperator(ActionEvent event) {
        if (resultLines[1].getText().matches(".*\\d+.*")) {//2nd number already completed
            checkAndRemoveNegativeZero();
            lastOperator = operator;
            operator = ((Button) event.getSource()).getText();
            numbers[0] = Double.parseDouble(resultLines[0].getText());
            numbers[1] = Double.parseDouble(resultLines[1].getText().substring(1));
            numbers[2] = modelCalculate.calculate(numbers[0], numbers[1], lastOperator);
            resultLines[2].setText(formatNumber(numbers[2]));

            try {//pause for 0.3 seconds
                Thread.sleep(300);

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            resultLines[0].setText(resultLines[2].getText());
            resetCurrentLine(1);
            resultLines[1].setText(operator + " ");
            resultLines[2].setText("");
            isCalculationCompleted = false;
        } else if (resultLines[0].getText().matches(".*\\d+.*")) {
            if (resultLines[0].getText().matches("[-0.]+") && resultLines[0].getText().startsWith("-")) {//Check that a String entered contains only 0's -'s and -'s
                resultLines[0].setText(resultLines[0].getText().substring(1));
                countDigitsInLine[0]--;
            }
            resetCurrentLine(1);
            operator = ((Button) event.getSource()).getText();
            resultLines[1].setText(operator + " ");
            isCalculationCompleted = false;
        }
    }

    private void checkAndRemoveNegativeZero() {
        if (resultLines[1].getText().substring(2).matches("[-0.]+") && resultLines[1].getText().substring(2).startsWith("-")) {//Check that 2nd number entered contains only 0's -'s and -'s
            resultLines[1].setText(operator + " " + resultLines[1].getText().substring(3));
            countDigitsInLine[1]--;
        }
    }

    @FXML
    public void btnEqual(ActionEvent event)  {
        if (isCalculationCompleted) {
            numbers[0] = numbers[2];
            resultLines[0].setText(resultLines[2].getText());
            //numbers[1] = Double.parseDouble(resultLines[1].getText().substring(2));
            numbers[2] = modelCalculate.calculate(numbers[0], numbers[1], operator);
            resultLines[2].setText(formatNumber(numbers[2]));
        } else if (resultLines[1].getText().matches(".*\\d+.*")) {
            isCalculationCompleted = true;
            checkAndRemoveNegativeZero();
            numbers[0] = Double.parseDouble(resultLines[0].getText());
            numbers[1] = Double.parseDouble(resultLines[1].getText().substring(2));
            numbers[2] = modelCalculate.calculate(numbers[0], numbers[1], operator);
            resultLines[2].setText(formatNumber(numbers[2]));
        }
    }

    private String formatNumber(double numberInput) {
        if (Math.abs(numberInput) >= Math.pow(10.0, MAXDIGITS)) {
            formatter = new DecimalFormat("0.0######E0");
            return formatter.format(numberInput);
        } else if (Math.abs(numberInput) >= 0.1) {
            BigDecimal bd = new BigDecimal(numberInput);
            bd = bd.round(new MathContext(MAXDIGITS));
            formatter = new DecimalFormat("#0.#########");
            return formatter.format(bd.doubleValue());
        } else {
            formatter = new DecimalFormat("#0.#########");
            String str = formatter.format(numberInput);
            if (str.matches("[-0.]+") && str.startsWith("-")){
                return "0";
            }else return str;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        // TODO
        resultLines[0] = resultLine0;
        resultLines[1] = resultLine1;
        resultLines[2] = resultLine2;
        countDigitsInLine[0] = 0;
        countDigitsInLine[1] = 0;
    }

}

