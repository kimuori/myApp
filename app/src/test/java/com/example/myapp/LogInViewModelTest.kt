package com.example.myapp

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith

/**
 * NOTE to the teacher:
 *
 * I think both tests fail due to the way how I misunderstood coding the ViewModel prior to
 * creating these tests. Without running this code, the manual testing on logging in with
 * my credentials seems to work via UI, and Logcat returns a successful 200 response with
 * User credentials. I think it helped narrow the issue that my viewModel needs work.
 *
 * If I had time to change my ViewModel to remove out the callback it is dependent on,
 * perhaps these tests can so somewhere. It feels very difficult to debug.
 * I feel that my knowledge to test feels very limited to troubleshoot to make sure
 * that my correct credential input for testing is indeed correct.
 *
 * Errors I commonly receive:
 *  Method getMainLooper in android.os.Looper not mocked.
 *  See https://developer.android.com/r/studio-ui/build/not-mocked for details.
 *
 */

@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class LogInViewModelTest {

    @Test
    fun `loginAccount() should be successful with the correct email and password credentials`() = runTest {
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

        //created to test system on testing
        val loginViewModel = LogInViewModel()

        //collects the emitted view states
        val viewStates = mutableListOf<LogInViewModel.ViewState>()
        loginViewModel.viewState.observeForever() {
            viewStates.add(it)
        }

        //NOTE: this method is reliant on context scope and dataStore for the callback
        loginViewModel.loginAccount("myTest@mail.com", "TestPass001") {
            //NOTE: This test failed even when I had a generated token activated via website at the same time
            //NOTE: The callback expects a lambda of a user and fetches info from dataStore--very difficult to debug with the current state of the ViewModel
            //user1.copy(291, "myTest@mail.com", "userA", "fa6ecb88-e2f9-451e-94ad-41146639e4f4")
            user -> user1
        }

        //assert that the returned values from the LogInViewModel object are correct.
        assertEquals("myTest@mail.com", loginViewModel.userState.value?.email)
        assertEquals(291, loginViewModel.userState.value?.id)
        assertEquals("userA", loginViewModel.userState.value?.name)
        assertEquals("fake-token100", loginViewModel.userState.value?.token)

        //assumes that the view model emitted the view states in correct order
        assertEquals(
            listOf(LogInViewModel.ViewState.Loading, LogInViewModel.ViewState.Success),
            viewStates
        )

        coVerify {
            userRequest1.loginUser(
                "fa04bfe8-c65a-417d-ad48-58f993bbc82b",
                LoginRequestBody("myTest@mail.com", "TestPass001")
            )
        }

    }

    @Test
    fun `loginAccount() should fail with the incorrect email and password credentials`(): Unit = kotlin.run {
        //mocking API Service for LogInRequest
        val userRequest2 = mockk<ApiService>() //mock data class User
        coEvery { userRequest2.loginUser(any(), any()) }

        //created to test system on testing
        val loginViewModel = LogInViewModel()

        //collects the emitted view states
        val viewStates = mutableListOf<LogInViewModel.ViewState>()
        loginViewModel.viewState.observeForever() {
            viewStates.add(it)
        }

        loginViewModel.loginAccount("wrongEmail@mail.com", "WrongPass") {
        }

        coEvery { userRequest2.loginUser(any(), any()) }

        assertNull(loginViewModel.userState.value)
        assertEquals(
            listOf(LogInViewModel.ViewState.Loading, LogInViewModel.ViewState.Error("Failed")),
            viewStates
        )
    }

}