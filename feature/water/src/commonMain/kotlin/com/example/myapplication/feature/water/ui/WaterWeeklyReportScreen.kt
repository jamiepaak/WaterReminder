package com.example.myapplication.feature.water.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.myapplication.domain.model.DailyWaterSummary
import com.example.myapplication.feature.water.presentation.WaterWeeklyReportEffect
import com.example.myapplication.feature.water.presentation.WaterWeeklyReportEvent
import com.example.myapplication.feature.water.presentation.WaterWeeklyReportScreenModel
import com.example.myapplication.feature.water.presentation.WaterWeeklyReportState
import com.example.myapplication.ui.component.LoadingIndicator

class WaterWeeklyReportScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<WaterWeeklyReportScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            screenModel.effect.collect { effect ->
                when (effect) {
                    is WaterWeeklyReportEffect.NavigateBack -> navigator.pop()
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("주간 리포트") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF2196F3),
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { screenModel.onEvent(WaterWeeklyReportEvent.OnBackClick) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (state.isLoading) {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            } else {
                WeeklyReportContent(
                    state = state,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun WeeklyReportContent(
    state: WaterWeeklyReportState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 요약 카드
        item {
            WeeklySummaryCard(state)
        }

        // 주간 바 차트 (상세)
        item {
            DetailedWeeklyChart(state.weeklySummary)
        }

        // 일별 상세 기록
        item {
            Text(
                text = "일별 상세",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(state.weeklySummary.reversed()) { summary ->
            DailyDetailCard(summary)
        }
    }
}

@Composable
private fun WeeklySummaryCard(state: WaterWeeklyReportState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "이번 주 요약",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "총 섭취량",
                    value = "${state.totalWeeklyIntake}ml",
                    color = Color(0xFF2196F3)
                )
                StatItem(
                    label = "일 평균",
                    value = "${state.averageDailyIntake}ml",
                    color = Color(0xFF42A5F5)
                )
                StatItem(
                    label = "달성 일수",
                    value = "${state.goalAchievedDays}/7일",
                    color = if (state.goalAchievedDays >= 5) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 달성률 바
            Text(
                text = "달성률 ${(state.achievementRate * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1565C0)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFBBDEFB))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.achievementRate)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (state.achievementRate >= 0.7f) Color(0xFF4CAF50)
                            else Color(0xFFFF9800)
                        )
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575)
        )
    }
}

@Composable
private fun DetailedWeeklyChart(weeklySummary: List<DailyWaterSummary>) {
    val maxAmount = weeklySummary.maxOfOrNull { it.goalAmount.coerceAtLeast(it.totalAmount) }?.coerceAtLeast(1) ?: 1
    val dayNames = listOf("월", "화", "수", "목", "금", "토", "일")

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
                text = "주간 기록",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklySummary.forEach { summary ->
                    val dayOfWeek = summary.date.dayOfWeek.ordinal
                    val barHeight = if (maxAmount > 0)
                        (summary.totalAmount.toFloat() / maxAmount) * 120f
                    else 0f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // 섭취량 라벨
                        Text(
                            text = "${summary.totalAmount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 바
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(barHeight.dp.coerceAtLeast(2.dp))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    if (summary.isGoalAchieved) Color(0xFF4CAF50)
                                    else Color(0xFF2196F3)
                                )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 요일
                        Text(
                            text = dayNames.getOrElse(dayOfWeek) { "" },
                            style = MaterialTheme.typography.labelSmall
                        )

                        // 달성 아이콘
                        if (summary.isGoalAchieved) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "달성",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyDetailCard(summary: DailyWaterSummary) {
    val dayNames = listOf("월", "화", "수", "목", "금", "토", "일")
    val dayOfWeek = dayNames.getOrElse(summary.date.dayOfWeek.ordinal) { "" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (summary.isGoalAchieved) Color(0xFFE8F5E9)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 날짜
            Column(modifier = Modifier.width(60.dp)) {
                Text(
                    text = "${summary.date.monthNumber}/${summary.date.dayOfMonth}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${dayOfWeek}요일",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 프로그레스 바
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${summary.totalAmount}ml",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "/ ${summary.goalAmount}ml",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(summary.progressPercent)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (summary.isGoalAchieved) Color(0xFF4CAF50)
                                else Color(0xFF2196F3)
                            )
                    )
                }

                Text(
                    text = "${(summary.progressPercent * 100).toInt()}% · ${summary.intakeCount}회",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF757575)
                )
            }

            // 달성 여부
            if (summary.isGoalAchieved) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "목표 달성",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
