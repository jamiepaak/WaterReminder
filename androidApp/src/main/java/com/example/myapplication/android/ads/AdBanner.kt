package com.example.myapplication.android.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // 테스트 광고 ID - 출시 전에 실제 ID로 변경 필요
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

object AdMobIds {
    // 테스트 광고 ID들 - 출시 전에 실제 ID로 변경 필요
    const val BANNER_TEST = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_TEST = "ca-app-pub-3940256099942544/1033173712"

    // 실제 광고 ID (AdMob 콘솔에서 발급)
    // const val BANNER = "ca-app-pub-YOUR_APP_ID/YOUR_AD_UNIT_ID"
}
