package com.example.myapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(
            ComposeView(this).apply{
                setContent{
                    val sheetState = rememberModalBottomSheetState()
                    val scope = rememberCoroutineScope()
                    var showBottomSheet by remember { mutableStateOf(false)}
                    var textValueField by remember { mutableStateOf(TextFieldValue(""))
                    }
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text= stringResource(id = R.string.top_bar_header))}
                            )},

                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {showBottomSheet = true}
                            ){
                                Icon(
                                    modifier = Modifier.padding(12.dp, 12.dp),
                                    painter = painterResource(id = R.drawable.baseline_add_24),
                                    contentDescription = "add"
                                )
                            }
                        }

                    ){
                        innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(12.dp, 12.dp)
                                .background(color = Color.Green)
                                .verticalScroll(rememberScrollState()) //added scroll in case it is not working
                                .fillMaxSize()) {
                            ColumnView()
                        }

                        if (showBottomSheet){
                            ModalBottomSheet(
                                onDismissRequest = { showBottomSheet = false },
                                sheetState = sheetState
                            ) {
                                OutlinedTextField(
                                    value = textValueField,
                                    onValueChange = {textValueField = it},
                                    label = {Text(text= stringResource(id = R.string.text_field_value))},
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.outline_cancel_24dp),
                                            contentDescription = "clear")},
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp, 12.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Button(
                                    onClick = {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp, 12.dp)
                                ) {
                                    Text("Save")
                                }
                                OutlinedButton(
                                    onClick = {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                showBottomSheet = false
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp, 12.dp)
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        )

    }
}

@Composable
private fun ColumnView(){
    for( i in 0..10 ) {
        TodoList()
    }
}

@Composable
private fun TodoList(){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .background(color = Color.Blue)
            .fillMaxWidth()
    ){
        TodoCheck()
    }
}

@Composable
private fun TodoCheck(){
    var isChecked by remember { mutableStateOf(true) }
    Text(
        text = "Hello World.",
        color = Color.White,
        modifier = Modifier.padding(12.dp, 12.dp)
    )
    Checkbox(
        checked = isChecked,
        onCheckedChange = {isChecked = it}
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview (){
    ColumnView()
}
