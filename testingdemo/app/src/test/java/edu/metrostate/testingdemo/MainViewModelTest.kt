package edu.metrostate.testingdemo

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Please not that you will need both of these extensions if you are testing coroutines and
 * view models with LiveData. They can be found in the testing package.
 *
 * Also, review the libs.versions.toml and the app build.gradle.kts files to make sure you have
 * the proper test dependencies set up in your project.
 */
@ExtendWith(CoroutinesTestExtension::class, InstantExecutorExtension::class)
class MainViewModelTest {

    @Test
    fun `login() should be successful with correct credentials`() = runTest {

        /**
         * Create the mock User and define the mock responses
         */
        val user = mockk<User>()
        every { user.name } returns "User Name"
        every { user.id } returns "ID"
        every { user.email } returns "user@name.com"

        /**
         * Create the mock ApiService and mock the login() response.
         */
        val api = mockk<ApiService>()
        coEvery { api.login(any(), any()) } returns user

        /**
         * Create the system under test. In this case MainViewModel.
         */
        val viewModel = MainViewModel(api)

        /**
         * Collect the emitted view states for later verification
         */
        val viewStates = mutableListOf<MainViewModel.ViewState>()
        viewModel.viewState.observeForever {
            viewStates.add(it)
        }

        viewModel.login("username", "password1")

        /**
         * Assert that the returned values from the view model User object are correct.
         */
        assertEquals("User Name", viewModel.userState.value?.name)
        assertEquals("ID", viewModel.userState.value?.id)
        assertEquals("user@name.com", viewModel.userState.value?.email)

        /**
         * Assert that the view model emitted the view states in the correct order.
         */
        assertEquals(
            listOf(MainViewModel.ViewState.Loading, MainViewModel.ViewState.Success),
            viewStates
        )

        /**
         * Verify that the api service login function was called with the correct arguments.
         */
        coVerify { api.login("username", "password1") }
    }

    @Test
    fun `login() should fail with incorrect credentials`() = runTest {
        val api = mockk<ApiService>()
        coEvery { api.login(any(), any()) } throws RuntimeException("")

        val viewModel = MainViewModel(api)

        val viewStates = mutableListOf<MainViewModel.ViewState>()
        viewModel.viewState.observeForever {
            viewStates.add(it)
        }

        viewModel.login("wrongUsername", "wrongPassword")

        coEvery { api.login("wrongUsername", "wrongPassword") }

        assertNull(viewModel.userState.value)
        assertEquals(
            listOf(MainViewModel.ViewState.Loading, MainViewModel.ViewState.Error("Failed")),
            viewStates
        )
    }
}