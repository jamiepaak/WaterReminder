package com.example.myapplication.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
actual fun AdBanner(modifier: Modifier) {
    // iOS에서는 Google Mobile Ads SDK를 Swift/UIKit으로 구현해야 함
    // 현재는 플레이스홀더로 표시
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEEEEEE)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ad Banner (iOS)",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
