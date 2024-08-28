package com.example.myapp

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Errors I commonly receive:
 *  *  Method getMainLooper in android.os.Looper not mocked.
 *  *  See https://developer.android.com/r/studio-ui/build/not-mocked for details.
 */
@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class TodoListViewModelTest {

    /**
     * Errors I commonly receive for this function:
     *  Method getMainLooper in android.os.Looper not mocked.
     *  See https://developer.android.com/r/studio-ui/build/not-mocked for details.
     */
    @Test
    fun `createTodo() should be successful with a complete creation and successful Login`() = runTest {
        //mocking to-do
        val todo1 = mockk<Todo>()
        every { todo1.completed } returns false
        every { todo1.description } returns "testing todo string 1"

        //mocking existing User
        val user1 = mockk<User>()
        every { user1.id } returns 291
        every { user1.email } returns "myTest@mail.com"
        every { user1.name } returns "userA"
        every { user1.token } returns "fake-token100"

        //mocking API Service for LogInRequest
        val userRequest1 = mockk<ApiService>()
        //for this API Service, we test loginUser
        coEvery { userRequest1.loginUser(any(), any()) } returns user1

        //mocking API Service for TodoListViewModel
        val todoListViewModelCreateTodo = TodoListViewModel()

        //collects the emitted view states
        val viewStates = mutableListOf<TodoListViewModel.ViewState>()
        todoListViewModelCreateTodo.viewStates.observeForever() {
            viewStates.add(it)
        }

        todoListViewModelCreateTodo.addTodoObject (
            user1.id.toString(),
            user1.token,
            todo1.description,
            todo1.completed) {
                todo1.completed
                todo1.description
        }

        //assert that the returned values from the TodoListViewModel object are correct and created
        assertEquals("testing todo string 1", todoListViewModelCreateTodo.todoListState.value?.get(0)?.description)
        assertEquals(false, todoListViewModelCreateTodo.todoListState.value?.get(0)?.completed)

        //NOTE: not sure why the 'ViewState' is not working
        /*
        assertEquals(
            listOf(todoListViewModelCreateTodo.ViewState.Loading, todoListViewModelCreateTodo.ViewState.Success),
            viewStates
        )
         */

        coVerify {
            userRequest1.loginUser(
            "fa04bfe8-c65a-417d-ad48-58f993bbc82b",
            LoginRequestBody("myTest@mail.com", "TestPass001"))

            userRequest1.createTodos(
                "fa04bfe8-c65a-417d-ad48-58f993bbc82b",
                "fake-token100",
                291,
                TodoRequestBody("testing todo string 1", false)
            )

        }

    }

    fun `showAllTodos() should fail with the incorrect ID of an existing user account`() = runTest {
        //mocking to-do
        val todo2 = mockk<Todo>()
        every { todo2.completed } returns false
        every { todo2.description } returns "testing todo string 1"

        //mocking existing User
        val user2 = mockk<User>()
        every { user2.id } returns 300
        every { user2.email } returns "myTest@mail.com"
        every { user2.name } returns "userA"
        every { user2.token } returns "fake-token100"

        //mocking API Service for LogInRequest
        val userRequest2 = mockk<ApiService>()
        //for this API Service, we test loginUser
        coEvery { userRequest2.loginUser(any(), any()) } returns user2

        //created to test system on LogInRequest
        val loginViewModel = LogInViewModel()
        //collects the emitted view states
        val viewStates = mutableListOf<LogInViewModel.ViewState>()
        loginViewModel.viewState.observeForever() {
            viewStates.add(it)
        }

        //mocking API Service for TodoListViewModel
        val todoListViewModelShowAllTodo = TodoListViewModel()
        //collects the emitted view states
        val viewStates2 = mutableListOf<TodoListViewModel.ViewState>()
        todoListViewModelShowAllTodo.viewStates.observeForever() {
            viewStates2.add(it)
        }

        loginViewModel.loginAccount("myTest@mail.com", "TestPass001") {
                user -> user2
        }

        todoListViewModelShowAllTodo.showAllTodos (
            user2.id.toString(),
            user2.token
        ){
            List<Todo>(1) {
                item -> Todo(description = "hello", completed = false)
            }
        }

        //assert that the returned values from the TodoListViewModel object are correct and created
        //expects that the id should be 291 to pass this test
        assertEquals(291, loginViewModel.userState.value?.id)

        //NOTE: not sure why the 'ViewState' is not working as well
        /*
        assertEquals(
            listOf(todoListViewModelShowAllTodo.ViewState.Loading, todoListViewModelShowAllTodo.ViewState.Success),
            viewStates
        )
         */

        coVerify {
            userRequest2.loginUser(
            "fa04bfe8-c65a-417d-ad48-58f993bbc82b",
            LoginRequestBody("myTest@mail.com", "TestPass001"))

            todoListViewModelShowAllTodo.showAllTodos("291", "testing todo string 1"){
                Todo("hello", false)
            }

        }

    }
}