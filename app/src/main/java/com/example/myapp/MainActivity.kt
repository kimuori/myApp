/*
    Requirements:
    Asg 1
        * One screen
        * main screen has a top bar, it will have the name of the app
        * to add a new todo item:
            ** bottom sheet pops up by clicking a floating action button with the "+" icon

            ** user will be able to:
                - the "x" button will clear the text field
                - the save button will be saved at the bottom of the list. The bottom sheet is dismissed.
                - the cancel button does not save to the list. The bottom sheet is dismissed.

         * main screen has a list of todo items:
            ** todo item will have a text which will be the todo item name
            ** todo item will have a checkbox which can be toggle
                - check = item completed
                - uncheck = item uncompleted

    Compose:
        * save button must be filled
        * cancel button must be unfilled, have an outline
        * textfield must be an outline text field
        * todo items must have 12dp horizontal padding

    Functional UI:
        * tapping todo item check box must togle the check box state
        * tapping floating action button brings up bottom sheet
        * typing text must update the contents of the text field
        * tapping on the save button must add the todo and
          update the list with the new todo in an UNCOMPLETED state.
              - it must close the bottom sheet
              - if there is NO TEXT when the button is tapped, UI must show error.
                  NOTE: Can it be a pop up dialogue?
        * tapping cancel button closes the bottom sheet, nothing is added
        * tapping the "x" icon clears the text field

    NOTES:
    * allowed to use place holder data and data structures
    * strings must be in strangs.xml
    * check many times what I push to GitHub is what I expect
    * submit the the link to the Merge Request--any other links is a penalty
    * branch name is correct

    Reference:
    https://medium.com/geekculture/add-remove-in-lazycolumn-list-aka-recyclerview-jetpack-compose-7c4a2464fc9f



 */
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
import androidx.compose.foundation.lazy.LazyColumn
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
                                    Text(text= stringResource(id = R.string.topbar_header))}
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
                        ColumnView()
                        //ColumnTodoListView()

                        if (showBottomSheet){
                            ModalBottomSheet(
                                onDismissRequest = { showBottomSheet = false },
                                sheetState = sheetState
                            ) {
                                OutlinedTextField(
                                    value = textValueField,
                                    onValueChange = {textValueField = it},
                                    label = {Text(text= stringResource(id = R.string.outlinedtextfield_label))},
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
                                    Text(text= stringResource(id = R.string.bottomsheet_button_save))
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
                                    Text(text= stringResource(id = R.string.bottomsheet_outlinedbutton_cancel))
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
    Column(
        modifier = Modifier
            .padding(12.dp, 12.dp)
            .background(color = Color.Green)
            .verticalScroll(rememberScrollState()) //added scroll in case it is not working
            .fillMaxSize()) {
        for( i in 0..10 ) {
            TodoList()
        }
    }

}


@Composable
private fun ColumnTodoListView(){
    LazyColumn(modifier = Modifier
        .padding(12.dp, 12.dp)
        .background(color = Color.Green)
        .verticalScroll(rememberScrollState()) //added scroll in case it is not working
        .fillMaxSize()) {
        item {  }
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
