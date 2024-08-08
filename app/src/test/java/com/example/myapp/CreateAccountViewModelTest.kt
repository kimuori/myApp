package com.example.myapp

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith

/*
 * Errors I commonly receive:
 *  Method getMainLooper in android.os.Looper not mocked.
 *  See https://developer.android.com/r/studio-ui/build/not-mocked for details.
 */
@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class CreateAccountViewModelTest {
    @Test
    fun `createAccount() should be successful with initial creation of user`() = runTest {
        //mocking User
        val user1 = mockk<User>()
        every { user1.id } returns 780
        every { user1.email } returns "newTest1@mail.com"
        every { user1.name } returns "NewUserTest1"
        every { user1.token } returns "fake-token001"

        //mocking API Service for RegisterRequest
        val userCreationRequest1 = mockk<ApiService>() //mock data class User
        //for this API Service, we test createTodos
        coEvery {userCreationRequest1.registerUser(any(), any())} returns user1

        val createAccountViewModel = CreateAccountViewModel()

        val viewStates = mutableListOf<CreateAccountViewModel.ViewState>()
        createAccountViewModel.viewState.observeForever() {
            viewStates.add(it)
        }

        //NOTE: this method is reliant on context scope and dataStore for the callback
        createAccountViewModel.createAccount(
            "newTest1@mail.com",
            "NewUserTest1",
            "NewUserPass1"
        ) {
                user -> user1
        }

        //assert that the returned values from the CreateAccountViewModel object are correct.
        assertEquals("newTest1@mail.com", createAccountViewModel.userState.value?.email)
        assertEquals("NewUserTest1", createAccountViewModel.userState.value?.name)
        assertEquals("fake-token001", createAccountViewModel.userState.value?.token)
        assertEquals(780, createAccountViewModel.userState.value?.id) //removed because I am not sure how the API will react

        //assumes that the view model emitted the view states in correct order
        assertEquals(
            listOf(CreateAccountViewModel.ViewState.Loading, CreateAccountViewModel.ViewState.Success),
            viewStates
        )

        coVerify {
            userCreationRequest1.registerUser(
                "fa04bfe8-c65a-417d-ad48-58f993bbc82b",
                RegisterRequestBody("newTest1@mail.com", "NewUserTest1", "NewUserPass1"))
        }

    }

    @Test
    fun `createAccount() should have failed HTTP Response when incorrect API is not present`() = runTest {
        //mocking User
        val user2 = mockk<User>()
        every { user2.id } returns 781
        every { user2.email } returns "newTest2@mail.com"
        every { user2.name } returns "NewUserTest2"
        every { user2.token } returns "fake-token002"

        //mocking API Service for RegisterRequest
        val userFailRequest2 = mockk<ApiService>() //mock data class User
        coEvery { userFailRequest2.registerUser(any(), any()) }

        val createAccountViewModel = CreateAccountViewModel()

        val viewStates = mutableListOf<CreateAccountViewModel.ViewState>()
        createAccountViewModel.viewState.observeForever() {
            viewStates.add(it)
        }

        createAccountViewModel.createAccount(
            "781",
            "NewUserTest1",
            "NewUserPass1"
        ) {
                user -> user2
        }

        assertEquals("newTest2@mail.com", createAccountViewModel.userState.value?.email) //assert to forget a creation
        assertEquals("NewUserTest1", createAccountViewModel.userState.value?.name)
        assertEquals("fake-token001", createAccountViewModel.userState.value?.token)
        assertEquals(780, createAccountViewModel.userState.value?.id) //removed because I am not sure how the API will react

        //assumes that the view model emitted the view states in correct order
        assertEquals(
            listOf(CreateAccountViewModel.ViewState.Loading, CreateAccountViewModel.ViewState.Success),
            viewStates
        )

        coVerify {
            userFailRequest2.registerUser(
                "fake api",
                RegisterRequestBody("newTest2@mail.com", "NewUserTest1", "NewUserPass1"))
        }
    }
}
