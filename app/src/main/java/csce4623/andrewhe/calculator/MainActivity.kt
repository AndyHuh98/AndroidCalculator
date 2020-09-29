package csce4623.andrewhe.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {
    val logger: Logger = Logger.getLogger("MainActivity")
    lateinit var txtResult: TextView

    private var currentNumber: Double = 0.0
    private var runningTotal: Double = 0.0

    var lastPressOpBtn: Boolean = false
    var lastPressEqualsBtn: Boolean = false

    private var operation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtResult = findViewById(R.id.calculatorResult)
    }

    // called whenever we just pressed a number
    fun onNumButtonPress(v: View) {
        logger.info("onNumButtonPress()")
        val button = v as Button
        val number = button.text.toString()

        var currentValue: String = txtResult.text.toString()
        // If in initial state, operator or equals button pressed, then the currentValue is equal to the number clicked
        // else append new digit to the current "number" string
        currentValue = if (currentValue == "0" || currentValue == "" || lastPressOpBtn || lastPressEqualsBtn) number else StringBuilder().append(currentValue).append(number).toString()

        // Setting currentNumber strings and txtView display to the current value
        currentNumber = currentValue.toDouble()
        txtResult.text = currentValue

        // if equals button was pressed, we reset. here we just need to reset currentOperation (the rest is reset further down)
        if (lastPressEqualsBtn) {
            operation = ""
        }

        lastPressOpBtn = false
        lastPressEqualsBtn = false
    }

    // appends decimalpoint to the string
    fun onDecimalButtonPress(v: View) {
        logger.info("onDecimalButtonPress()")
        var currentValue: String = txtResult.text.toString()

        // If order of presses goes OPERATOR PRESS -> DECIMAL or EQUALS -> DECIMAL, we know we have to append 0 infront
        if (lastPressOpBtn || lastPressEqualsBtn) {
            currentValue = StringBuilder().append("0").append(".").toString()

            if (lastPressEqualsBtn) operation = ""
            currentNumber = 0.0
        } else {
            // else append decimal point to number string already typed, if it doesn't already have a decimal point
            if (!currentValue.contains(".")) {
                currentValue = StringBuilder().append(currentValue).append(".").toString()
            }
        }

        txtResult.setText(currentValue)

        lastPressOpBtn = false
        lastPressEqualsBtn = false
    }

    // called whenever an operator is pressed. the majority of its logic is handled in calculate method
    fun onOperatorButtonPress(v: View) {
        logger.info("onOperatorButtonPress()")
        val button = v as Button
        val operator = button.text.toString()

        if (!lastPressOpBtn && !lastPressEqualsBtn) {
            calculate()
        }

        // stores the pressed operator for evaluation purposes
        operation = operator

        lastPressOpBtn = true
        lastPressEqualsBtn = false
    }

    // called whenever the "clear" button is pressed. simply resets everything to default state
    fun onClearPress(v: View) {
        logger.info("onClearPress()")
        currentNumber = 0.0
        runningTotal = 0.0
        operation = ""

        txtResult.text = currentNumber.toInt().toString()

        lastPressOpBtn = false
        lastPressEqualsBtn = false
    }

    // called when user wants to flip the sign of the number
    fun onPosNegPress(v: View) {
        logger.info("onPosNegPress()")
        // takes current value from text view window and if it is non-zero,
        val currentValue: String = txtResult.text.toString()

        currentNumber = currentValue.toDouble()
        if (currentNumber == 0.0) return

        currentNumber *= -1

        val format = if (runningTotal < 1000000000) DecimalFormat("#.###########") else DecimalFormat("0.#####E0")
        format.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)

        txtResult.setText(format.format(currentNumber))

        if (lastPressEqualsBtn) {
            operation = ""
        }

        lastPressEqualsBtn = false
        lastPressOpBtn = false
    }

    // called when user wants to backspace an input
    fun onBackspacePress(v: View) {
        logger.info("onBackspacePress()")
        // if last press was op, there's no number to backspace
        if (lastPressOpBtn || lastPressEqualsBtn) return

        var currentResult: String = txtResult.text.toString()

        currentResult = if (currentResult.length > 1) currentResult.substring(0, currentResult.length - 1) else "0"

        txtResult.text = currentResult

        // handle leftover negative character if number is negative
        currentNumber = if (currentResult != "-") currentResult.toDouble() else 0.0

        if (lastPressEqualsBtn) {
            operation = ""
        }

        lastPressOpBtn = false
        lastPressEqualsBtn = false
    }

    // Called whenever user wants to calculate final result using equals button
    fun onEqualsBtnPress(v: View) {
        logger.info("onEqualsBtnPress()")
        // set the currentNumber equal to the runningTotal in case another operation is pressed after equals button calculation
        if (lastPressOpBtn) {
            currentNumber = runningTotal
        }

        calculate()

        val format = if (runningTotal < 100000000) DecimalFormat("#.###########") else DecimalFormat("0.#####E0")
        format.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)

        txtResult.setText(format.format(runningTotal))

        // resets states to default
        operation = ""
        lastPressOpBtn = false
        lastPressEqualsBtn = true
    }

    // private method used by operatorPress and equalsPress methods to calculate the result of a user's input
    private fun calculate() {
        // when statement is basically switch statement in Kotlin
        when (operation) {
            // take advantage of big decimal class to handle float point arithmetic
            "+" -> runningTotal = (runningTotal.toBigDecimal() + currentNumber.toBigDecimal()).toDouble()
            "-" -> runningTotal = (runningTotal.toBigDecimal() - currentNumber.toBigDecimal()).toDouble()
            "*" -> runningTotal = (runningTotal.toBigDecimal() * currentNumber.toBigDecimal()).toDouble()
            "/" -> runningTotal = (runningTotal.toBigDecimal() / currentNumber.toBigDecimal()).toDouble()
            "" -> {
                runningTotal = currentNumber
            }
        }

        val format = if (runningTotal < 1000000000) DecimalFormat("#.###########") else DecimalFormat("0.#####E0")
        format.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)

        txtResult.setText(format.format(runningTotal))
    }
}