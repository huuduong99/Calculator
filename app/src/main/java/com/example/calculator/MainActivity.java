package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Integer[] buttonIds = new Integer[]{R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot,
            R.id.btnEqual, R.id.btnPlus, R.id.btnSub, R.id.btnMul, R.id.btnDiv, R.id.btnPercent, R.id.btnOppositeNumber, R.id.btnAC};
    char[] numberChars = new char[]{'0','1','2','3','4','5','6','7','8','9'};
    TextView tvResult;
    String typingNumber = "";
    boolean negative = false;
    boolean isAC = true;
    List<Double> numbers = new ArrayList<>();
    List<Character> mathOperations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Listen click button
        for (int i = 0; i < buttonIds.length; i++) {
            findViewById(buttonIds[i]).setOnClickListener(this);
        }
        tvResult = findViewById(R.id.tvResult);
    }

    public void PressNumber(char ch){
        AddNumber(ch);
        EnableAllButtons();
        UpdateTypingNumber();
        UpdateTextAC();
        CheckPreviousStep();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {

            case R.id.btn0:
                if (!isZero()) {
                    PressNumber('0');
                }
                break;
            case R.id.btn1:
            case R.id.btn2:
            case R.id.btn3:
            case R.id.btn4:
            case R.id.btn5:
            case R.id.btn6:
            case R.id.btn7:
            case R.id.btn8:
            case R.id.btn9:
                int index = java.util.Arrays.asList(buttonIds).indexOf(id);
                PressNumber(numberChars[index]);
                break;

            case R.id.btnDot:
                boolean hasDot = false;
                if (typingNumber.length() > 0) {
                    for (int i = 0; i < typingNumber.length(); i++) {
                        if (typingNumber.charAt(i) == '.') {
                            hasDot = true;
                            break;
                        }
                    }
                    if (!hasDot) {
                        typingNumber += ".";
                        UpdateTypingNumber();
                    }
                }
                break;

            case R.id.btnEqual:
            {
                AddLastNumber();
                AddOperation('=');
                EnableAllButtons();
                boolean result = Calculate();
                if (result) {
                    ShowResult();
                    EnableAC();
                }
                else
                    ResetAll();
                break;
            }
            case R.id.btnPlus: {
                AddLastNumber();
                boolean result = Calculate();
                if (result) {
                    ShowResult();
                    AddOperation('+');
                    HighlightButton(R.id.btnPlus);
                } else
                    ResetAll();
                break;
            }
            case R.id.btnSub:
                AddLastNumber();
                boolean result = Calculate();
                if(result) {
                    ShowResult();
                    AddOperation('-');
                    HighlightButton(R.id.btnSub);
                } else
                    ResetAll();
                break;

            case R.id.btnMul:
                AddLastNumber();
                AddOperation('x');
                EnableAllButtons();
                HighlightButton(R.id.btnMul);
                break;

            case R.id.btnDiv:
                AddLastNumber();
                AddOperation('/');
                HighlightButton(R.id.btnDiv);
                break;

            case R.id.btnPercent:
                AddLastNumber();
                percentOfNumber();
                break;

            case R.id.btnOppositeNumber:

                if (typingNumber != "" || mathOperations.size() == numbers.size()) {
                    negative = !negative;
                    UpdateTypingNumber();
                } else {
                    Double number = numbers.get(numbers.size() - 1);
                    numbers.set(numbers.size() - 1, -1 * number);
                    tvResult.setText(numbers.get(numbers.size() - 1).toString());
                }
                break;

            case R.id.btnAC: {
                if (!isAC) {
                    typingNumber = "";
                    tvResult.setText("0");
                    EnableAC();
                    UndoLastOperation();
                } else {
                    ResetAll();
                }
                break;
            }

            default:
                break;
        }
    }

    public void EnableAC(){
        isAC = true;
        ((Button) findViewById(R.id.btnAC)).setText("AC");
    }

    public void ResetAll(){
        numbers.clear();
        mathOperations.clear();
        typingNumber = "";
        negative = false;
        EnableAC();
        EnableAllButtons();
        tvResult.setText("0");
    }

    private void UpdateTypingNumber() {
        tvResult.setText(((negative ? "-" : "") + typingNumber));
    }

    public void AddLastNumber() {
        if (typingNumber != "") {
            numbers.add((negative ? -1 : 1) * Double.valueOf(typingNumber));
            typingNumber = "";
            negative = false;
        }
    }

    public void AddOperation(char a) {
        if (numbers.size() > 0) {
            if (mathOperations.size() == numbers.size()) {
                mathOperations.remove(mathOperations.size() - 1);
            }

            if (a != '=') {
                mathOperations.add(a);
            }
        }
    }

    public void HighlightButton(int id) {
        EnableAllButtons();
        findViewById(id).setEnabled(false);
    }

    public void EnableAllButtons() {
        findViewById(R.id.btnPlus).setEnabled(true);
        findViewById(R.id.btnSub).setEnabled(true);
        findViewById(R.id.btnMul).setEnabled(true);
        findViewById(R.id.btnDiv).setEnabled(true);
    }

    public boolean Calculate() {
        if (numbers.size() > 1) {
            for (int i = 0; i < mathOperations.size(); i++) {
                Double value = null;
                if (mathOperations.get(i) == 'x') {
                    value = numbers.get(i) * numbers.get(i + 1);
                } else if (mathOperations.get(i) == '/') {
                    if (numbers.get(i + 1) != 0) {
                        value = numbers.get(i) / numbers.get(i + 1);
                    } else {
                        Toast.makeText(MainActivity.this, "Cannot be divided by zero", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                if (value != null) {
                    numbers.set(i, value);
                    numbers.remove(i + 1);
                    mathOperations.remove(i);
                    i--;
                }
            }

            Double value = numbers.get(0);
            for (int i = 0; i < mathOperations.size(); i++) {
                if (mathOperations.get(i) == '+') {
                    value += numbers.get(i + 1);
                } else if (mathOperations.get(i) == '-') {
                    value -= numbers.get(i + 1);
                }
            }
            numbers.clear();
            mathOperations.clear();
            numbers.add(Double.valueOf(value));
        }
        return true;
    }

    public void ShowResult(){
        if(numbers.size() == 1)
            SetResultValue(numbers.get(0));
    }

    public void SetResultValue (double value){
        DecimalFormat df = new DecimalFormat("#.#########");
        String formatted = df.format(value);
        tvResult.setText(formatted);
    }

    public void CheckPreviousStep() {
        if (mathOperations.size() == 0 && numbers.size() > 0) {
            numbers.clear();
        }
    }

    public void percentOfNumber() {
        if (numbers.size() > 0 && numbers.size() == mathOperations.size() + 1) {

            numbers.set(numbers.size() - 1, numbers.get(numbers.size() - 1) * 0.01);
        } else {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }

        if (numbers.size() > 0) {
            SetResultValue(numbers.get(numbers.size() - 1));
        }
    }

    public void AddNumber(char ch) {
        if (!IsOverNumber()) {
            typingNumber += ch;
        } else {
            Toast.makeText(MainActivity.this, typingNumber.length() > 14 ? "Maximum number of digits is 15" : "Maximum of digits after decimal point is 9", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean IsOverNumber() {
        int length = 0;
        boolean hasDot = false;
        if (typingNumber.length() > 14)
            return true;

        for (int i = typingNumber.length() - 1; i > 0; i--) {
            if (typingNumber.charAt(i) != '.')
                length++;
            else {
                hasDot = true;
                break;
            }
        }
        return hasDot && length > 8;
    }

    public void UpdateTextAC() {
        ((Button) findViewById(R.id.btnAC)).setText("C");
        isAC = false;
    }

    public void UndoLastOperation() {
        if (mathOperations.size() > 0) {
            switch ((mathOperations.get(mathOperations.size() - 1))) {
                case '+':
                    findViewById(R.id.btnPlus).setEnabled(false);
                    break;

                case '-':
                    findViewById(R.id.btnSub).setEnabled(false);
                    break;

                case '*':
                    findViewById(R.id.btnMul).setEnabled(false);
                    break;

                case '/':
                    findViewById(R.id.btnDiv).setEnabled(false);
                    break;
            }
        }
    }

    public boolean isZero()
    {
        return(typingNumber.length()==1 && typingNumber.charAt(0)=='0');
    }
}
