package com.example.myapp

/*
    Assignment 2

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

    Reference:
    [Assignment 1]
    https://medium.com/geekculture/add-remove-in-lazycolumn-list-aka-recyclerview-jetpack-compose-7c4a2464fc9f
    https://stackoverflow.com/questions/68482228/how-to-clear-textfield-value-in-jetpack-compose
    https://stackoverflow.com/questions/71534415/composable-invocations-can-only-happen-from-the-context-of-a-composable-functio

    [Assignment 2]
    https://medium.com/@aleslam12345/use-retrofit-with-kotlin-81cb938dfd10
    https://developer.android.com/codelabs/android-preferences-datastore
    https://www.youtube.com/watch?v=tYZ2pGS95K4

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : AppCompatActivity() {
    private val createAccountViewModel : CreateAccountViewModel = CreateAccountViewModel()
    private val loginViewModel : LogInViewModel = LogInViewModel()
    private val todoListViewModel : TodoListViewModel = TodoListViewModel()

    //private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(
            ComposeView(this).apply{
                setContent{
                    //the context, scope, and dataStore work together for data persistence
                    //to store bearer token and user_id
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()
                    val dataStore = PreferencesManager(context)

                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "one" ){
                        composable( route = "one"){
                            CreateAccountScreen(navController, createAccountViewModel, scope, dataStore)
                        }

                        composable( route = "two"){
                            LogInScreen(navController, loginViewModel, scope, dataStore)
                        }

                        composable (route = "three"){
                            TodoListScreen(todoListViewModel, scope, dataStore)
                        }

                    }

                }
            }
        )

    }

}

@Composable
fun CreateAccountScreen (
    navController: NavController,
    createAccountViewModel: CreateAccountViewModel,
    scope: CoroutineScope,
    dataStore: PreferencesManager,
){
    var newUsernameValueField by remember { mutableStateOf(TextFieldValue(""))}
    var newEmailValueField by remember { mutableStateOf(TextFieldValue(""))}
    var newPasswordValueField by remember { mutableStateOf(TextFieldValue("")) }

    var errorAlertText by remember { mutableStateOf("") }
    var showAlertDialog by remember {mutableStateOf(false)}

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            confirmButton = {
                TextButton(
                    onClick = { showAlertDialog = false }
                ) {
                    Text(stringResource(id = R.string.alertdialog_dismisstext))
                }
            },
            title = { Text(text = stringResource(id = R.string.alertdialog_title))},
            text = { Text (errorAlertText)}
        )
    }

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
            value = newUsernameValueField,
            onValueChange = {
                newUsernameValueField = it
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
            onClick = {
                when {
                    //when multiple fields are missing
                    newUsernameValueField.text.isBlank() && newEmailValueField.text.isBlank() && newPasswordValueField.text.isBlank() -> {
                        errorAlertText = "Multiple fields are missing."
                        showAlertDialog = true
                    }
                    ((newUsernameValueField.text.isBlank() && newEmailValueField.text.isBlank()) ||
                    (newEmailValueField.text.isBlank() && newPasswordValueField.text.isBlank()) ||
                    newPasswordValueField.text.isBlank() && newUsernameValueField.text.isBlank()) -> {
                        errorAlertText = "Multiple fields are missing."
                        showAlertDialog = true
                    }
                    //when name text field is missing
                    newUsernameValueField.text.isBlank() -> {
                        errorAlertText = "Name is required."
                        showAlertDialog = true
                    }
                    //when email text field is missing
                    newEmailValueField.text.isBlank() -> {
                        errorAlertText = "Email is required."
                        showAlertDialog = true
                    }
                    //when password text field is missing
                    newPasswordValueField.text.isBlank() -> {
                        errorAlertText = "Password is required."
                        showAlertDialog = true
                    }
                    else ->
                        createAccountViewModel.createAccount(
                            newEmailValueField.text,
                            newUsernameValueField.text,
                            newPasswordValueField.text
                        ){ user ->
                            navController.navigate("three") //temp
                            if (user != null) {

                                //coroutine scope on storing Response Body via dataStore
                                scope.launch {
                                    dataStore.saveEmail(newEmailValueField.text)
                                    dataStore.savePassword(newPasswordValueField.text)
                                    dataStore.saveToken(user.token)
                                    //NOTE: the response body for "id" originally responds in Int. Parsed into String.
                                    dataStore.saveUserId(user.id.toString())
                                }
                                navController.navigate("three")
                            } else {
                                errorAlertText = "Login failed. Please try again."
                            }
                    }
                }
                //navController.navigate("three") //temp
            },
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
    navController: NavController,
    loginViewModel: LogInViewModel,
    scope: CoroutineScope,
    dataStore: PreferencesManager
){
    var emailValueField by remember { mutableStateOf(TextFieldValue(""))}
    var passwordValueField by remember { mutableStateOf(TextFieldValue(""))}

    var errorAlertText by remember { mutableStateOf("") }
    var showAlertDialog by remember {mutableStateOf(false)}

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            confirmButton = {
                TextButton(
                    onClick = { showAlertDialog = false }
                ) {
                    Text(stringResource(id = R.string.alertdialog_dismisstext))
                }
            },
            title = { Text(text = stringResource(id = R.string.alertdialog_title))},
            text = { Text (errorAlertText) }
        )
    }

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
            onClick = {
                when {
                    //when email text field is missing
                    emailValueField.text.isBlank() -> {
                        errorAlertText = "Email is required."
                        showAlertDialog = true
                    }
                    //when password text field is missing
                    passwordValueField.text.isBlank() -> {
                        errorAlertText = "Password is required."
                        showAlertDialog = true
                    }
                    //when multiple text fields are missing
                    (emailValueField.text.isBlank() && emailValueField.text.isBlank()) ->{
                        errorAlertText = "Multiple fields are missing."
                        showAlertDialog = true
                    }
                    else -> {
                        //navController.navigate("three") //temp
                        loginViewModel.loginAccount(
                            emailValueField.text,
                            passwordValueField.text)
                        { user ->
                            //if the User account exits
                            if (user != null) {
                                /*
                                    NOTE: based on observation, Logcat will log a response body
                                    of the User object when request is successful.
                                    Example:
                                    User(id=###, email=myTest@mail.com, name=userA, token=xxx...)
                                 */

                                //coroutine scope on storing Response Body via dataStore
                                scope.launch {
                                    dataStore.saveEmail(emailValueField.text)
                                    dataStore.savePassword(passwordValueField.text)
                                    dataStore.saveToken(user.token)
                                    //NOTE: the response body for "id" originally responds in Int. Parsed into String.
                                    dataStore.saveUserId(user.id.toString())
                                }
                                navController.navigate("three")

                            } else {
                                /*
                                    NOTE: based on observation, login fails if the request
                                    is not successful, despite that the User account object exists
                                    prior to this development.

                                    Adding an internet permission below to the AndroidManifest may have helped.
                                        <uses-permission android:name="android.permission.INTERNET"/>
                                        -   Successful requests to log in happened after this addition.

                                    NOTE: Error login fail UI alert is not reached if
                                    placing incorrect email and incorrect password, resulting in error 422
                                 */
                                errorAlertText = "Login failed. Please try again."

                            }
                        }

                    }
                }

                //navController.navigate("three")
            },
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    todoListViewModel: TodoListViewModel,
    scope: CoroutineScope,
    dataStore: PreferencesManager
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false)}
    var textValueField by remember { mutableStateOf(TextFieldValue("")) } //to-do list text
    var showAlertDialog by remember {  mutableStateOf(false) } //for UI error
    val theList = remember { mutableListOf<Todo>() }

    val savedUserId = dataStore.getUserId
    val savedBearToken = dataStore.getToken

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
        ColumnTodoListView(theList, todoListViewModel, dataStore)

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
                        when {
                            textValueField.text.isBlank() || textValueField.text.isEmpty() -> {
                                showAlertDialog = true //show error
                            } else -> {
                                todoListViewModel.addTodoObject(
                                    userId = savedUserId.toString(),
                                    bearerToken = savedBearToken.toString(),
                                    description = textValueField.text,
                                    completed = false
                                ) {
                                    theList.add(Todo(textValueField.text, false))
                                }
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }

                                //finally, clear the text as the bottomsheet dismisses
                                textValueField = TextFieldValue("")
                            }
                        }
                        /*
                        if (textValueField.text.isBlank() || textValueField.text.isEmpty()) {
                            showAlertDialog = true //show error
                        } else {
                            /*
                                Save button adds the to-do and updates the list with
                                the new to-do. Finally, the bottom sheet closes.
                             */
                            //todoListViewModel.addTodoObject(dataStore())
                            theList.add(Todo(textValueField.text, false))

                           c
                        }

                         */
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
    theList: MutableList<Todo>,
    todoListViewModel: TodoListViewModel,
    dataStore: PreferencesManager
) {
    val savedUserId = dataStore.getUserId
    val savedBearToken = dataStore.getToken

    LazyColumn(modifier = Modifier
        .padding(start = 12.dp, top = 113.dp, end = 12.dp, bottom = 0.dp)
        .background(color = Color.Green)
        .fillMaxSize()

    ){
        todoListViewModel.showAllTodos(
            userId = savedUserId.toString(),
            bearerToken = savedBearToken.toString()
        ){
            //shows List<To-do> in LazyList
            items(theList){
                    items ->
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .background(color = Color.Blue)
                        .fillMaxWidth()
                ){
                    var isChecked by remember { mutableStateOf(items.completed) } //unchecked box by default
                    Text(
                        text = items.description?: "",
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
        /*
            items(theList){
                items ->
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .background(color = Color.Blue)
                        .fillMaxWidth()
                ){
                    var isChecked by remember { mutableStateOf(items.completed) } //unchecked box by default
                    Text(
                        text = items.description?: "",
                        color = Color.White,
                        modifier = Modifier.padding(12.dp, 12.dp)
                    )
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it}
                    )
                }
            }
         */

    }
}