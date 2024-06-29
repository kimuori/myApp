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
        * tapping todo item check box must toggle the check box state
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
    * strings must be in strings.xml
    * check many times what I push to GitHub is what I expect
    * submit the the link to the Merge Request--any other links is a penalty
    * branch name is correct

    Reference:
    https://medium.com/geekculture/add-remove-in-lazycolumn-list-aka-recyclerview-jetpack-compose-7c4a2464fc9f
    https://stackoverflow.com/questions/68482228/how-to-clear-textfield-value-in-jetpack-compose
    https://stackoverflow.com/questions/71534415/composable-invocations-can-only-happen-from-the-context-of-a-composable-functio

 */
package com.example.myapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
                    var textValueField by remember { mutableStateOf(TextFieldValue("")) }
                    var showAlertDialog by remember {  mutableStateOf(false) }

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

                        //if FOA is clicked, show the BottomSheet
                        if (showBottomSheet){
                            ModalBottomSheet(
                                onDismissRequest = { showBottomSheet = false },
                                sheetState = sheetState
                            ) {

                                //To-do List text value field with trailing "x" icon
                                OutlinedTextField(
                                    value = textValueField,
                                    onValueChange = {
                                        textValueField = it
                                    },
                                    label = {Text(text= stringResource(id = R.string.outlinedtextfield_label))},

                                    /* TODO:
                                       The "x" button will clear the text field.
                                     */
                                    //"x" icon clear string functionality
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.outline_cancel_24dp),
                                            contentDescription = "clear text",
                                            //textValueField is now a blank string upon clicking
                                            modifier = Modifier.clickable { textValueField = TextFieldValue("") }
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp, 12.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))

                                //To-do List Save button
                                /* TODO:
                                   Tapping on the save button must add the to-do
                                   and update the list with the new to-do in an
                                   UNCOMPLETED state.
                                   **It must close the bottom sheet
                                   **If there is NO TEXT when the save button is tapped,
                                   UI must show error. (Can it be a pop up dialogue?)
                                */

                                Button(
                                    /*
                                       Checks on the condition if the OutlinedTextField is blank or isEmpty().
                                       Otherwise, it will add a new to-do list.
                                     */
                                    onClick = {
                                        if (textValueField.text == "" || textValueField.text.isEmpty() ) {
                                            showAlertDialog = true //show error
                                        } else {
                                            //temp
                                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                                if (!sheetState.isVisible) {
                                                    showBottomSheet = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp, 12.dp)
                                ) {
                                    Text(text= stringResource(id = R.string.bottomsheet_button_save))
                                }

                                /*
                                   If the OutlinedTextField is left blank AND save button is selected,
                                   this condition is flagged and will raise an Alert Dialog to notify
                                   the user for missing input.
                                 */

                                if (showAlertDialog){
                                    BlankAlertDialog( onCancel = { showAlertDialog = false })

                                }

                                //To-do List Cancel button
                                OutlinedButton(
                                    onClick = {
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            /*
                                              Tapping cancel button closes the bottom sheet, nothing is added.
                                              BottomSheet is dismissed.
                                             */
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
        .fillMaxSize()
    ){
        item{}
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
    var isChecked by remember { mutableStateOf(false) } //unchecked box by default

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

@Composable
fun BlankAlertDialog(onCancel: () -> Unit ){
    AlertDialog(
        onDismissRequest = {  },
        confirmButton = {
            TextButton(onClick = onCancel) {  Text(text = "Dismiss") }
        },
        title = { Text(text = "Error") },
        text = { Text(text = "Todo list cannot be left blank.")}
   )

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview (){
    ColumnView()
}
