package com.example.myapp

/*
    Assignment 2

    [UI]
    Create a Login UI and Create Account UI.
    * The main title is "To-do"

    **  Login:
            * email
            * password
            **Log in button
            **"Dont have an account?" text link

    **  Create Account
            * name
            * email
            *password
            **Create Account button
            **"Log In" text link

    [HTTP REQUESTS & APP BEHAVIOR]
    The main screen of the application should GET the list of the logged in user’s todos from the server.
        When tapping the check box to “complete” a to-do item, the app should PUT that to the server.
        When creating a new to-do from the bottom sheet, the app should POST that new to-do to the server.

        After creating a new to-do, make sure to GET the list of the logged in user’s todos again so that the new todo can be displayed.

        If you quit the application and log in again, the todos for the user should be fetched and displayed.
        If you quit the application and log in with a different user, the different user’s todos should be fetched and displayed.

    [ARCHITECTURE]
    The LogInViewModel and the CreateAccountViewModel will need to get the token and the user id
    from either the log in response or the create account response and store them in a
    place where OTHER parts of the application can make use of it.
        **Use SharedPreferences which is a built-in part of the Android SDK.

        The API is the Data layer, no need database.
        **Store current list of To-do in the To-doListModel
        **list should be replaced when GETting the list of the user’s todos.

    [ERROR HANDLING]
    For every error, display an error dialogue.

    [REQUIREMENTS]
        * use MVVM
        * Use Kotlin Coroutine in your Retrofit functions
            ** ViewModel super class provides viewModelScope for calling/creating coroutines

    Reference:
    [Assignment 1]
    https://medium.com/geekculture/add-remove-in-lazycolumn-list-aka-recyclerview-jetpack-compose-7c4a2464fc9f
    https://stackoverflow.com/questions/68482228/how-to-clear-textfield-value-in-jetpack-compose
    https://stackoverflow.com/questions/71534415/composable-invocations-can-only-happen-from-the-context-of-a-composable-functio

 */

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : AppCompatActivity() {

    private val createAccountViewModel : CreateAccountViewModel = CreateAccountViewModel()
    private val loginViewModel : LogInViewModel = LogInViewModel()
    private val todoListViewModel : TodoListViewModel = TodoListViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(
            ComposeView(this).apply{
                setContent{
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "one" ){
                        composable( route = "one"){
                            CreateAccountScreen(navController)
                        }

                        composable( route = "two"){
                            LogInScreen(navController)
                        }

                        composable (route = "three"){
                            TodoListScreen()
                        }

                    }

                }
            }
        )

    }
}

@Composable
fun CreateAccountScreen (
    navController: NavController
){
    var newUsername by remember { mutableStateOf(TextFieldValue(""))}
    var newEmailValueField by remember { mutableStateOf(TextFieldValue(""))}
    var newPasswordValueField by remember { mutableStateOf(TextFieldValue(""))}

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,) {
        Text(
            text = stringResource(id = R.string.screenCreateAccount_title),
            fontSize = 48.sp,
        )

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedTextField(
            value = newUsername,
            onValueChange = {
                newUsername = it
            },
            label = {
                Text( text = stringResource(id = R.string.screenCreateAccount_outlinedtextfield_name) )
            },

            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        )

        OutlinedTextField(
            value = newEmailValueField,
            onValueChange = {
                newEmailValueField = it
            },
            label = {
                Text( text = stringResource(id = R.string.screenCreateAccount_outlinedtextfield_email) )
            },

            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        )

        OutlinedTextField(
            value = newPasswordValueField,
            onValueChange = {
                newPasswordValueField = it
            },
            label = {
                Text(
                    text = stringResource(id = R.string.screenCreateAccount_outlinedtextfield_password),
                )},

            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        )

        //navigating buttons
        Button(
            onClick = { navController.navigate("three") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        ) {
            Text( text= stringResource(id = R.string.screenCreateAccount_button_create) )

        }

        Spacer(modifier = Modifier.size(12.dp))

        TextButton(
            onClick = { navController.navigate("two") },

        ) {
            Text( text = stringResource(id = R.string.screenCreateAccount_textbutton_login) )
        }
    }
}

@Composable
fun LogInScreen (
    navController : NavController
){
    var emailValueField by remember { mutableStateOf(TextFieldValue(""))}
    var passwordValueField by remember { mutableStateOf(TextFieldValue(""))}

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,) {
        Text(
            text = stringResource(id = R.string.screenLogIn_title),
            fontSize = 48.sp,
        )

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedTextField(
            value = emailValueField,
            onValueChange = {
                emailValueField = it
            },
            label = {
                Text( text = stringResource(id = R.string.screenLogIn_outlinedtextfield_email) )
            },

            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        )

        OutlinedTextField(
            value = passwordValueField,
            onValueChange = {
                passwordValueField = it
            },
            label = {
                Text( text = stringResource(id = R.string.screenLogIn_outlinedtextfield_password) )
            },

            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        )

        //navigating buttons
        Button(
            onClick = { navController.navigate("three") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp)
        ) {
            Text( text= stringResource(id = R.string.screenLogIn_button_login) )

        }

        Spacer(modifier = Modifier.size(12.dp))

        TextButton(
            onClick = { navController.navigate("one") },

        ) {
            Text(stringResource(id = R.string.screenLogIn_textbutton_create))
        }
    }
}

//attributes that a to-do list has: a to-do string and a (un)checked box
data class TodoCheckList(val todoCheckText: String?, val isChecked: Boolean)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen (){
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false)}
    var textValueField by remember { mutableStateOf(TextFieldValue("")) } //to-do list text
    var showAlertDialog by remember {  mutableStateOf(false) } //for UI error
    val theList = remember {mutableListOf<TodoCheckList>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text( text = stringResource(id = R.string.topbar_header))
                }
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
        //innerPadding ->
        ColumnTodoListView(theList)

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
                    label = {
                        Text( text= stringResource(id = R.string.outlinedtextfield_label) )
                    },

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

                //To-do List Save button and BottomSheet dismissal for UI Error
                Button(
                    /*
                       Checks on the condition if the OutlinedTextField is blank or isEmpty().
                       Otherwise, it will add a new to-do list.
                     */
                    onClick = {
                        if (textValueField.text.isBlank() || textValueField.text.isEmpty()) {
                            showAlertDialog = true //show error
                        } else {
                            /*
                                Save button adds the to-do and updates the list with
                                the new to-do. Finally, the bottom sheet closes.
                             */
                            theList.add(TodoCheckList(textValueField.text, false))

                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }

                            //finally, clear the text as the bottomsheet dismisses
                            textValueField = TextFieldValue("")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp, 12.dp)
                ) {
                    Text(
                        text= stringResource(id = R.string.bottomsheet_button_save),
                    )

                }

                /*
                   If the OutlinedTextField is left blank AND save button is selected,
                   this condition is flagged and will raise an Alert Dialog to notify
                   the user for missing input.
                 */
                if (showAlertDialog){
                    //hide the BottomSheet after clicking the confirmButton
                    AlertDialog(
                        onDismissRequest = {  },
                        confirmButton = {
                            TextButton(onClick = {
                                showAlertDialog = false
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            } ) {
                                Text( text = stringResource(id = R.string.alertdialog_dismisstext) )
                            }
                        },

                        title = { Text(text = stringResource(id = R.string.alertdialog_title))},
                        text = { Text(text = stringResource(id = R.string.alertdialog_text))}
                    )
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
                    Text( text= stringResource(id = R.string.bottomsheet_outlinedbutton_cancel) )
                }
            }
        }
    }
}

@Composable
private fun ColumnTodoListView(
    theList: MutableList<TodoCheckList>
) {
    LazyColumn(modifier = Modifier
        .padding(start = 12.dp, top = 113.dp, end = 12.dp, bottom = 0.dp)
        .background(color = Color.Green)
        .fillMaxSize()

    ){
        items(theList){
                items ->
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .background(color = Color.Blue)
                    .fillMaxWidth()
            ){
                var isChecked by remember { mutableStateOf(items.isChecked) } //unchecked box by default
                Text(
                    text = items.todoCheckText?: "",
                    color = Color.White,
                    modifier = Modifier.padding(12.dp, 12.dp)
                )
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it}
                )
            }
        }
    }
}