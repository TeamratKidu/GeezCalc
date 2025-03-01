package com.example.calcgeez

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private lateinit var resultText: TextView
    private lateinit var arabicResultText: TextView
    private var firstNumber = 0.0
    private var operation = ""
    private var newNumber = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.resultText)
        arabicResultText = findViewById(R.id.arabicResultText)

        // Number buttons
        val numberButtons = arrayOf(
            findViewById<Button>(R.id.btn0),
            findViewById<Button>(R.id.btn1),
            findViewById<Button>(R.id.btn2),
            findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4),
            findViewById<Button>(R.id.btn5),
            findViewById<Button>(R.id.btn6),
            findViewById<Button>(R.id.btn7),
            findViewById<Button>(R.id.btn8),
            findViewById<Button>(R.id.btn9)
        )

        // Set click listeners for number buttons
        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                onNumberClick(index.toString())
            }
        }

        // Operation buttons
        findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperationClick("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperationClick("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperationClick("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperationClick("/") }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsClick() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearClick() }
    }

    private fun convertToGeez(number: Int): String {
        return when (number) {
            0 -> "አልቦ"
            1 -> "፩"
            2 -> "፪"
            3 -> "፫"
            4 -> "፬"
            5 -> "፭"
            6 -> "፮"
            7 -> "፯"
            8 -> "፰"
            9 -> "፱"
            else -> number.toString()
        }
    }

    private fun convertFromGeez(geezNumber: String): String {
        return when (geezNumber) {
            "አልቦ" -> "0"
            "፩" -> "1"
            "፪" -> "2"
            "፫" -> "3"
            "፬" -> "4"
            "፭" -> "5"
            "፮" -> "6"
            "፯" -> "7"
            "፰" -> "8"
            "፱" -> "9"
            else -> geezNumber
        }
    }

    private fun updateDisplays(geezText: String) {
        resultText.text = geezText
        // Convert Geez to Arabic for the secondary display
        val arabicText = if (geezText == "አልቦ") {
            "0"
        } else {
            geezText.map {
                if (it.toString() == "አ" || it.toString() == "ል" || it.toString() == "ቦ") "0"
                else convertFromGeez(it.toString())
            }.joinToString("")
        }
        arabicResultText.text = arabicText
    }

    private fun onNumberClick(number: String) {
        if (newNumber) {
            updateDisplays(convertToGeez(number.toInt()))
            newNumber = false
        } else {
            if (resultText.text == "አልቦ") {
                updateDisplays(convertToGeez(number.toInt()))
            } else {
                val currentText = resultText.text.toString()
                val arabicNumber = currentText.map {
                    if (it.toString() == "አ" || it.toString() == "ል" || it.toString() == "ቦ") "0"
                    else convertFromGeez(it.toString())
                }.joinToString("")
                val newGeezText = (arabicNumber + number).map {
                    convertToGeez(it.toString().toInt())
                }.joinToString("")
                updateDisplays(newGeezText)
            }
        }
    }

    private fun onOperationClick(op: String) {
        val currentText = resultText.text.toString()
        val arabicNumber = if (currentText == "አልቦ") {
            "0"
        } else {
            currentText.map {
                if (it.toString() == "አ" || it.toString() == "ል" || it.toString() == "ቦ") "0"
                else convertFromGeez(it.toString())
            }.joinToString("")
        }
        firstNumber = arabicNumber.toDouble()
        operation = op
        newNumber = true
    }

    private fun numberToWords(number: Double): String {
        val intPart = number.toLong()
        val decimalPart = (number - intPart) * 100

        val intWords = when {
            intPart == 0L -> "zero"
            intPart < 0 -> "negative " + numberToWordsPositive(-intPart)
            else -> numberToWordsPositive(intPart)
        }

        return if (decimalPart == 0.0) {
            intWords
        } else {
            "$intWords point ${numberToWordsPositive(decimalPart.toLong())}"
        }
    }

    private fun numberToWordsPositive(number: Long): String {
        val units = arrayOf("", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
        val tens = arrayOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")

        return when {
            number == 0L -> ""
            number < 20 -> units[number.toInt()]
            number < 100 -> {
                val ten = tens[(number / 10).toInt()]
                val unit = units[(number % 10).toInt()]
                if (unit.isEmpty()) ten else "$ten-$unit"
            }
            number < 1000 -> {
                val hundred = units[(number / 100).toInt()]
                val rest = numberToWordsPositive(number % 100)
                if (rest.isEmpty()) "$hundred hundred"
                else "$hundred hundred and $rest"
            }
            number < 1000000 -> {
                val thousand = numberToWordsPositive(number / 1000)
                val rest = numberToWordsPositive(number % 1000)
                if (rest.isEmpty()) "$thousand thousand"
                else "$thousand thousand $rest"
            }
            else -> "number too large"
        }
    }

    private fun onEqualsClick() {
        val currentText = resultText.text.toString()
        val arabicNumber = if (currentText == "አልቦ") {
            "0"
        } else {
            currentText.map {
                if (it.toString() == "አ" || it.toString() == "ል" || it.toString() == "ቦ") "0"
                else convertFromGeez(it.toString())
            }.joinToString("")
        }
        val secondNumber = arabicNumber.toDouble()
        val result = when (operation) {
            "+" -> firstNumber + secondNumber
            "-" -> firstNumber - secondNumber
            "*" -> firstNumber * secondNumber
            "/" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.POSITIVE_INFINITY
            else -> secondNumber
        }

        // Convert result to Geez numbers
        val resultString = if (result.toLong().toDouble() == result) {
            result.toLong().toString()
        } else {
            result.toString()
        }

        val geezResult = if (resultString == "0") {
            "አልቦ"
        } else {
            resultString.map {
                if (it.isDigit()) convertToGeez(it.toString().toInt()) else it.toString()
            }.joinToString("")
        }

        updateDisplays(geezResult)

        // Show toast with English words
        val operationSymbol = when(operation) {
            "+" -> "plus"
            "-" -> "minus"
            "*" -> "times"
            "/" -> "divided by"
            else -> ""
        }

        val message = "${numberToWords(firstNumber)} $operationSymbol ${numberToWords(secondNumber)} equals ${numberToWords(result)}"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        newNumber = true
    }

    private fun onClearClick() {
        updateDisplays("አልቦ")
        firstNumber = 0.0
        operation = ""
        newNumber = true
    }
} 