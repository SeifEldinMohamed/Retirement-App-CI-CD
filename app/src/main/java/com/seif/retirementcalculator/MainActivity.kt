package com.seif.retirementcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.seif.retirementcalculator.ui.theme.RetirementCalculatorTheme
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppCenter.start(application, BuildConfig.APP_CENTER_KEY, Analytics::class.java, Crashes::class.java)
            RetirementCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    RetirementCalculatorScreen()
                }
            }
        }
    }
}



@Composable
fun RetirementCalculatorScreen() {
    // State variables for the input fields and result
    var monthlySavings by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var currentAge by remember { mutableStateOf("") }
    var retirementAge by remember { mutableStateOf("") }
    var currentSavings by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    // Layout for the retirement calculator
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // TextFields for input
        TextField(
            value = monthlySavings,
            onValueChange = { monthlySavings = it },
            label = { Text("Monthly Savings") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = interestRate,
            onValueChange = { interestRate = it },
            label = { Text("Interest Rate (%)") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = currentAge,
            onValueChange = { currentAge = it },
            label = { Text("Your Current Age") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = retirementAge,
            onValueChange = { retirementAge = it },
            label = { Text("Planned Retirement Age") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = currentSavings,
            onValueChange = { currentSavings = it },
            label = { Text("Current Savings") },
            modifier = Modifier.fillMaxWidth()
        )

        // Calculate button
        Button(
            onClick = {
                val properties = mapOf(
                    "monthlySavings" to monthlySavings,
                    "interestRate" to interestRate,
                    "currentAge" to currentAge,
                    "retirementAge" to retirementAge,
                    "currentSavings" to currentSavings
                )

                if((interestRate.toIntOrNull() ?: 0) <= 0)
                    Analytics.trackEvent("wrong_interest", properties)
                if ((retirementAge.toIntOrNull() ?: 0) <= (currentAge.toIntOrNull() ?: 0))
                    Analytics.trackEvent("wrong_age", properties)

                result = calculateRetirement(
                    monthlySavings.toDoubleOrNull() ?: 0.0,
                    interestRate.toDoubleOrNull() ?: 0.0,
                    currentAge.toIntOrNull() ?: 0,
                    retirementAge.toIntOrNull() ?: 0,
                    currentSavings.toDoubleOrNull() ?: 0.0
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Calculate")
        }

        // Display result
        Text(
            text = result,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Function to calculate retirement savings
fun calculateRetirement(
    monthlySavings: Double,
    interestRate: Double,
    currentAge: Int,
    retirementAge: Int,
    currentSavings: Double
): String {
    // Example logic for retirement savings calculation
    val yearsUntilRetirement = retirementAge - currentAge
    val totalMonths = yearsUntilRetirement * 12
    val monthlyInterest = (interestRate / 100) / 12

    var futureValue = currentSavings
    for (i in 1..totalMonths) {
        futureValue = (futureValue + monthlySavings) * (1 + monthlyInterest)
    }

    return "Estimated Savings at Retirement: $${"%.2f".format(futureValue)}"
}

@Preview
@Composable
fun PreviewRetirementCalculatorScreen() {
    RetirementCalculatorTheme {
        RetirementCalculatorScreen()
    }
}
