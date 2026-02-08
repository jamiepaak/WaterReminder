package com.example.myapplication.feature.water.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.feature.water.presentation.WaterHomeEvent
import com.example.myapplication.feature.water.presentation.WaterHomeScreenModel
import com.example.myapplication.ui.component.AdBanner
import com.example.myapplication.ui.component.LoadingIndicator

class WaterHomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<WaterHomeScreenModel>()
        val state by screenModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("물 마시기") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF2196F3),
                        titleContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = { screenModel.onEvent(WaterHomeEvent.NavigateToSettings) }) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "설정",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { screenModel.onEvent(WaterHomeEvent.ShowAddDialog) },
                    containerColor = Color(0xFF2196F3)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "물 추가", tint = Color.White)
                }
            }
        ) { paddingValues ->
            if (state.isLoading) {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 오늘의 진행 상황
                    item {
                        state.todaySummary?.let { summary ->
                            TodayProgressCard(
                                summary = summary,
                                cupSize = state.goal.cupSizeMl,
                                onAddCup = { screenModel.onEvent(WaterHomeEvent.AddWater(state.goal.cupSizeMl)) }
                            )
                        }
                    }

                    // 빠른 추가 버튼들
                    item {
                        QuickAddSection(
                            cupSize = state.goal.cupSizeMl,
                            onAddWater = { amount -> screenModel.onEvent(WaterHomeEvent.AddWater(amount)) }
                        )
                    }

                    // 주간 통계
                    item {
                        WeeklyStatsCard(weeklySummary = state.weeklySummary)
                    }

                    // 오늘 기록
                    if (state.todayIntakes.isNotEmpty()) {
                        item {
                            Text(
                                text = "오늘의 기록",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(state.todayIntakes) { intake ->
                            IntakeItem(
                                intake = intake,
                                onDelete = { screenModel.onEvent(WaterHomeEvent.DeleteIntake(intake.id)) }
                            )
                        }
                    }

                    // 광고 배너
                    item {
                        AdBanner()
                    }
                }
            }

            // 커스텀 양 추가 다이얼로그
            if (state.showAddDialog) {
                AddWaterDialog(
                    customAmount = state.customAmount,
                    onAmountChange = { screenModel.onEvent(WaterHomeEvent.UpdateCustomAmount(it)) },
                    onConfirm = { screenModel.onEvent(WaterHomeEvent.AddCustomAmount) },
                    onDismiss = { screenModel.onEvent(WaterHomeEvent.HideAddDialog) }
                )
            }
        }
    }
}

@Composable
private fun TodayProgressCard(
    summary: DailyWaterSummary,
    cupSize: Int,
    onAddCup: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 원형 프로그레스
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = summary.progressPercent,
                    label = "progress"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 20.dp.toPx()
                    val diameter = size.minDimension - strokeWidth

                    // 배경 원
                    drawArc(
                        color = Color(0xFFBBDEFB),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // 프로그레스 원
                    drawArc(
                        color = Color(0xFF2196F3),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${summary.totalAmount}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = "/ ${summary.goalAmount} ml",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64B5F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (summary.isGoalAchieved) {
                Text(
                    text = "🎉 목표 달성!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            } else {
                Text(
                    text = "${summary.remainingAmount}ml 더 마셔요",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddCup,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.WaterDrop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("한 잔 마시기 (${cupSize}ml)")
            }
        }
    }
}

@Composable
private fun QuickAddSection(
    cupSize: Int,
    onAddWater: (Int) -> Unit
) {
    Column {
        Text(
            text = "빠른 추가",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(100, 200, 300, 500).forEach { amount ->
                OutlinedButton(
                    onClick = { onAddWater(amount) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("${amount}ml")
                }
            }
        }
    }
}

@Composable
private fun WeeklyStatsCard(weeklySummary: List<DailyWaterSummary>) {
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
                text = "이번 주 기록",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weeklySummary.forEach { summary ->
                    DayColumn(summary = summary)
                }
            }
        }
    }
}

@Composable
private fun DayColumn(summary: DailyWaterSummary) {
    val dayNames = listOf("일", "월", "화", "수", "목", "금", "토")
    val dayOfWeek = summary.date.dayOfWeek.ordinal

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 프로그레스 바
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((80 * summary.progressPercent).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (summary.isGoalAchieved) Color(0xFF4CAF50)
                        else Color(0xFF2196F3)
                    )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = dayNames.getOrElse(dayOfWeek) { "" },
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun IntakeItem(
    intake: com.example.myapplication.domain.model.WaterIntake,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.WaterDrop,
                contentDescription = null,
                tint = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${intake.amount}ml",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${intake.drankAt.hour}:${intake.drankAt.minute.toString().padStart(2, '0')}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = onDelete) {
                Text("삭제", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddWaterDialog(
    customAmount: String,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("물 추가") },
        text = {
            OutlinedTextField(
                value = customAmount,
                onValueChange = onAmountChange,
                label = { Text("양 (ml)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

