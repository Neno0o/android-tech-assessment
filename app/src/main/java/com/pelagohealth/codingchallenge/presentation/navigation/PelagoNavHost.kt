package com.pelagohealth.codingchallenge.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pelagohealth.codingchallenge.presentation.history.HistoryScreen
import com.pelagohealth.codingchallenge.presentation.history.HistoryViewModel
import com.pelagohealth.codingchallenge.presentation.main.MainScreen
import com.pelagohealth.codingchallenge.presentation.main.MainViewModel

@Composable
fun PelagoNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            val viewModel = hiltViewModel<MainViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            MainScreen(
                state = state,
                onFetchNewFact = viewModel::loadFact,
                onShowHistory = {
                    navController.navigate("history")
                },
                modifier = modifier,
            )
        }

        composable(route = "history") {
            val viewModel = hiltViewModel<HistoryViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            HistoryScreen(
                state = state,
                onBack = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }
    }
}

