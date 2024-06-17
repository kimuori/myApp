package com.example.myapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(
            ComposeView(this).apply{
                setContent{
                    Scaffold(
                        topBar = {
                            TopAppBar(title = { Text(text = "Todo List")})},

                        floatingActionButton = {
                            FloatingActionButton(onClick = {}){
                                Icon(
                                    modifier = Modifier.padding(10.dp, 10.dp),
                                    painter = painterResource(id = R.drawable.baseline_add_24),
                                    contentDescription = "add"
                                )
                            }
                        }

                    ){
                        innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .background(color = Color.Green)
                                .fillMaxSize()) {
                            ColumnView()
                        }
                    }
                }
            }
        )

    }
}

@Composable
private fun ColumnView(){
    for( i in 0..100 ) {
        TodoList()
    }
}

@Composable
private fun TodoList(){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.background(color = Color.Blue).fillMaxWidth()
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
