package com.example.myapplication.feature.water.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.myapplication.feature.water.presentation.WaterSettingsEffect
import com.example.myapplication.feature.water.presentation.WaterSettingsEvent
import com.example.myapplication.feature.water.presentation.WaterSettingsScreenModel
import com.example.myapplication.ui.component.LoadingIndicator

class WaterSettingsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<WaterSettingsScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            screenModel.effect.collect { effect ->
                when (effect) {
                    is WaterSettingsEffect.NavigateBack -> navigator.pop()
                    is WaterSettingsEffect.ShowMessage -> {
                        // Show snackbar
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("설정") },
                    navigationIcon = {
                        IconButton(onClick = { screenModel.onEvent(WaterSettingsEvent.OnBackClick) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF2196F3),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            if (state.isLoading) {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 목표량 설정
                    SettingsCard(title = "일일 목표량") {
                        Text(
                            text = "${state.goal.dailyGoalMl} ml",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )

                        Slider(
                            value = state.goal.dailyGoalMl.toFloat(),
                            onValueChange = {
                                screenModel.onEvent(WaterSettingsEvent.UpdateDailyGoal(it.toInt()))
                            },
                            valueRange = 1000f..4000f,
                            steps = 29
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("1000ml", style = MaterialTheme.typography.labelSmall)
                            Text("4000ml", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // 컵 사이즈 설정
                    SettingsCard(title = "기본 컵 사이즈") {
                        Text(
                            text = "${state.goal.cupSizeMl} ml",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )

                        Slider(
                            value = state.goal.cupSizeMl.toFloat(),
                            onValueChange = {
                                screenModel.onEvent(WaterSettingsEvent.UpdateCupSize(it.toInt()))
                            },
                            valueRange = 100f..500f,
                            steps = 7
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("100ml", style = MaterialTheme.typography.labelSmall)
                            Text("500ml", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // 알림 설정
                    SettingsCard(title = "알림 설정") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("알림 받기")
                            Switch(
                                checked = state.goal.reminderEnabled,
                                onCheckedChange = {
                                    screenModel.onEvent(WaterSettingsEvent.UpdateReminderEnabled(it))
                                }
                            )
                        }

                        if (state.goal.reminderEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text("알림 간격: ${state.goal.reminderIntervalMinutes}분")
                            Slider(
                                value = state.goal.reminderIntervalMinutes.toFloat(),
                                onValueChange = {
                                    screenModel.onEvent(WaterSettingsEvent.UpdateReminderInterval(it.toInt()))
                                },
                                valueRange = 30f..180f,
                                steps = 4
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("알림 시작 시간: ${state.goal.reminderStartHour}시")
                            Slider(
                                value = state.goal.reminderStartHour.toFloat(),
                                onValueChange = {
                                    screenModel.onEvent(WaterSettingsEvent.UpdateReminderStartHour(it.toInt()))
                                },
                                valueRange = 6f..12f,
                                steps = 5
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("알림 종료 시간: ${state.goal.reminderEndHour}시")
                            Slider(
                                value = state.goal.reminderEndHour.toFloat(),
                                onValueChange = {
                                    screenModel.onEvent(WaterSettingsEvent.UpdateReminderEndHour(it.toInt()))
                                },
                                valueRange = 18f..24f,
                                steps = 5
                            )
                        }
                    }

                    Button(
                        onClick = { screenModel.onEvent(WaterSettingsEvent.SaveSettings) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("저장")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}
