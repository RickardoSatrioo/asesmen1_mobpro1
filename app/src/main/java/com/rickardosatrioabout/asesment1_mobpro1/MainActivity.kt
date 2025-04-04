package com.rickardosatrioabout.asesment1_mobpro1

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rickardosatrioabout.asesment1_mobpro1.navigation.Screen
import com.rickardosatrioabout.asesment1_mobpro1.navigation.SetupNavGraph
import com.rickardosatrioabout.asesment1_mobpro1.ui.theme.Asesment1_Mobpro1Theme

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
fun MainScreen(navController: NavController) {
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
    var angka2 by rememberSaveable { mutableStateOf("") }
    var selectedOperation by rememberSaveable { mutableStateOf(R.string.add) }
    var resultText by rememberSaveable { mutableStateOf("") }
    var resultValue by rememberSaveable { mutableStateOf("") }


    val operations = listOf(
        R.string.add,
        R.string.subtract,
        R.string.multiply,
        R.string.divide
    )

    val context = LocalContext.current

    fun calculate() {
        val num1 = angka1.toDoubleOrNull() ?: 0.0
        val num2 = angka2.toDoubleOrNull() ?: 0.0

        val operationResult = when (selectedOperation) {
            R.string.add -> num1 + num2
            R.string.subtract -> num1 - num2
            R.string.multiply -> num1 * num2
            R.string.divide -> if (num2 != 0.0) num1 / num2 else Double.NaN
            else -> Double.NaN
        }

        if (operationResult.isNaN()) {
            resultText = context.getString(R.string.divisionError)
            resultValue = ""
        } else {
            resultText = context.getString(R.string.result)
            resultValue = if (operationResult % 1 == 0.0) {
                operationResult.toInt().toString()
            } else {
                operationResult.toString()
            }
        }
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
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = angka1,
            onValueChange = { angka1 = it },
            label = { Text(text = stringResource(R.string.value1)) },
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    RadioButton(
                        selected = (selectedOperation == operation),
                        onClick = { selectedOperation = operation },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(id = operation),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Button(
            onClick = { calculate() },
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(text = stringResource(R.string.count))
        }

        if (resultText.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                if (resultValue.isNotEmpty()) {
                    Text(
                        text = resultValue,
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(
                    onClick = {
                        shareData(
                            context = context,
                            message = context.getString(R.string.share_template, resultValue)
                        )
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(R.string.share))
                }
            }
        }
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