package com.rickardosatrioabout.asesment1_mobpro1

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.rickardosatrioabout.asesment1_mobpro1.navigation.Screen
import com.rickardosatrioabout.asesment1_mobpro1.navigation.SetupNavGraph
import com.rickardosatrioabout.asesment1_mobpro1.ui.theme.Asesment1_Mobpro1Theme
import java.util.*

private fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Asesment1_Mobpro1Theme {
                SetupNavGraph()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: androidx.navigation.NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.About.route)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.history),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        CenteredLargeText(Modifier.padding(innerPadding))
    }
}

@Composable
fun CenteredLargeText(modifier: Modifier = Modifier) {
    var angka1 by rememberSaveable { mutableStateOf("") }
    var angka1Error by rememberSaveable { mutableStateOf(false) }

    var angka2 by rememberSaveable { mutableStateOf("") }
    var angka2Error by rememberSaveable { mutableStateOf(false) }

    var selectedOperations by rememberSaveable { mutableStateOf(setOf<Int>()) }
    var resultText by rememberSaveable { mutableStateOf("") }
    var resultValue by rememberSaveable { mutableStateOf("") }

    var noOperationSelected by rememberSaveable { mutableStateOf(false) }

    val operations = listOf(
        OperationItem(R.string.add, R.drawable.tambah),
        OperationItem(R.string.subtract, R.drawable.kurang),
        OperationItem(R.string.multiply, R.drawable.kali),
        OperationItem(R.string.divide, R.drawable.bagi)
    )

    val context = LocalContext.current

    fun calculate() {
        if (selectedOperations.isEmpty()) {
            noOperationSelected = true
            resultText = ""
            resultValue = ""
            return
        } else {
            noOperationSelected = false
        }

        val num1 = angka1.toDoubleOrNull() ?: 0.0
        val num2 = angka2.toDoubleOrNull() ?: 0.0

        val results = mutableListOf<String>()

        selectedOperations.forEach { operation ->
            val operationResult = when (operation) {
                R.string.add -> num1 + num2
                R.string.subtract -> num1 - num2
                R.string.multiply -> num1 * num2
                R.string.divide -> if (num2 != 0.0) num1 / num2 else Double.NaN
                else -> Double.NaN
            }

            val operationLabel = when (operation) {
                R.string.add -> context.getString(R.string.result_add)
                R.string.subtract -> context.getString(R.string.result_subtract)
                R.string.multiply -> context.getString(R.string.result_multiply)
                R.string.divide -> context.getString(R.string.result_divide)
                else -> ""
            }

            if (!operationResult.isNaN()) {
                val formattedResult = if (operationResult % 1 == 0.0) {
                    operationResult.toInt().toString()
                } else {
                    String.format(Locale.US, "%.2f", operationResult)
                }
                results.add("$operationLabel $formattedResult")
            } else {
                results.add("$operationLabel ${context.getString(R.string.error)}")
            }
        }

        resultText = context.getString(R.string.calculation_results)
        resultValue = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.instruction),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = angka1,
            onValueChange = { angka1 = it },
            label = { Text(text = stringResource(R.string.value1)) },
            trailingIcon = { IconPicker(angka1Error) },
            supportingText = { ErrorHint(angka1Error) },
            isError = angka1Error,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = angka2,
            onValueChange = { angka2 = it },
            label = { Text(text = stringResource(R.string.value2)) },
            trailingIcon = { IconPicker(angka2Error) },
            supportingText = { ErrorHint(angka2Error) },
            isError = angka2Error,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            operations.forEach { operation ->
                OperationCheckbox(
                    operation = operation,
                    isSelected = selectedOperations.contains(operation.labelRes),
                    onSelectionChange = { isChecked ->
                        selectedOperations = if (isChecked) {
                            selectedOperations + operation.labelRes
                        } else {
                            selectedOperations - operation.labelRes
                        }
                        noOperationSelected = false
                    },
                    isError = noOperationSelected
                )
            }
        }

        if (noOperationSelected) {
            Text(
                text = stringResource(R.string.no_operation_selected),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Button(
            onClick = {
                angka1Error = (angka1 == "")
                angka2Error = (angka2 == "")
                if (angka1Error || angka2Error) return@Button
                else {
                    calculate()
                }
            },
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(text = stringResource(R.string.count))
        }

        if (resultText.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    selectedOperations.forEach { operation ->
                        val num1 = angka1.toDoubleOrNull() ?: 0.0
                        val num2 = angka2.toDoubleOrNull() ?: 0.0

                        val operationResult = when (operation) {
                            R.string.add -> num1 + num2
                            R.string.subtract -> num1 - num2
                            R.string.multiply -> num1 * num2
                            R.string.divide -> if (num2 != 0.0) num1 / num2 else Double.NaN
                            else -> Double.NaN
                        }

                        val operationLabel = when (operation) {
                            R.string.add -> context.getString(R.string.result_add)
                            R.string.subtract -> context.getString(R.string.result_subtract)
                            R.string.multiply -> context.getString(R.string.result_multiply)
                            R.string.divide -> context.getString(R.string.result_divide)
                            else -> ""
                        }

                        if (!operationResult.isNaN()) {
                            val formattedResult = if (operationResult % 1 == 0.0) {
                                operationResult.toInt().toString()
                            } else {
                                String.format(Locale.US, "%.2f", operationResult)
                            }
                            Text(
                                text = "$operationLabel $formattedResult",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                            )
                        } else {
                            Text(
                                text = "$operationLabel ${context.getString(R.string.error)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val message = buildString {
                            append("${context.getString(R.string.value1_share)} $angka1\n")
                            append("${context.getString(R.string.value2_share)} $angka2\n\n")
                            append(resultText)
                            append("\n")
                            selectedOperations.forEach { operation ->
                                val num1 = angka1.toDoubleOrNull() ?: 0.0
                                val num2 = angka2.toDoubleOrNull() ?: 0.0
                                val operationResult = when (operation) {
                                    R.string.add -> num1 + num2
                                    R.string.subtract -> num1 - num2
                                    R.string.multiply -> num1 * num2
                                    R.string.divide -> if (num2 != 0.0) num1 / num2 else Double.NaN
                                    else -> Double.NaN
                                }
                                val operationLabel = when (operation) {
                                    R.string.add -> context.getString(R.string.result_add)
                                    R.string.subtract -> context.getString(R.string.result_subtract)
                                    R.string.multiply -> context.getString(R.string.result_multiply)
                                    R.string.divide -> context.getString(R.string.result_divide)
                                    else -> ""
                                }
                                if (!operationResult.isNaN()) {
                                    val formattedResult = if (operationResult % 1 == 0.0) {
                                        operationResult.toInt().toString()
                                    } else {
                                        String.format(Locale.US, "%.2f", operationResult)
                                    }
                                    append("$operationLabel $formattedResult\n")
                                } else {
                                    append("$operationLabel ${context.getString(R.string.error)}\n")
                                }
                            }
                        }
                        shareData(context, message)
                    },
                    modifier = Modifier.padding(top = 24.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(R.string.share))
                }
            }
        }
    }
}

@Composable
fun OperationCheckbox(
    operation: OperationItem,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    isError: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange,
                modifier = Modifier.size(48.dp),
                interactionSource = remember { MutableInteractionSource() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    checkmarkColor = MaterialTheme.colorScheme.primary
                )
            )
            if (isSelected) {
                Icon(
                    painter = painterResource(id = operation.iconRes),
                    contentDescription = stringResource(id = operation.labelRes),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Text(
            text = stringResource(id = operation.labelRes),
            color = if (isError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

data class OperationItem(
    val labelRes: Int,
    val iconRes: Int
)

@Composable
fun IconPicker(isError: Boolean) {
    if (isError) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun ErrorHint(isError: Boolean) {
    if (isError) {
        Text(
            text = stringResource(R.string.input_invalid),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Asesment1_Mobpro1Theme {
        MainScreen(rememberNavController())
    }
}
