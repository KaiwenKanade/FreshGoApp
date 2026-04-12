package com.example.freshgoapp.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freshgoapp.ui.theme.PrimaryFigmaGreen

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    // Scaffold memberikan struktur yang rapi (Top, Content, Bottom)
    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indikator Titik (Page Indicator)
                Row(
                    Modifier
                        .height(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { iteration ->
                        val color = if (pagerState.currentPage == iteration) PrimaryFigmaGreen else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Lanjut
                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryFigmaGreen)
                ) {
                    Text(
                        text = if (pagerState.currentPage == 2) "Mulai Sekarang" else "Lanjut",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header FreshGo tetap di atas
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "FreshGo",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )

            // Pager Konten di Tengah
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                OnboardingPage(page)
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Int) {
    val titles = listOf("Pantau Kesegaran", "Kurangi Food Waste", "Belanja Pintar")
    val subs = listOf(
        "Catat semua bahan makananmu\ndi dalam kulkas digital.",
        "Dapatkan peringatan otomatis\nsebelum bahan makanan basi.",
        "Atur daftar belanjaanmu dengan\nefisien dan hemat waktu."
    )
    val emojis = listOf("🥬", "⏰", "🛒")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Pastikan konten ke tengah secara horizontal
        verticalArrangement = Arrangement.Center // Pastikan konten ke tengah secara vertical
    ) {
        // Area Ilustrasi (Bentuk Kotak seperti Figma)
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emojis[page], fontSize = 100.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Judul (Rata Tengah)
        Text(
            text = titles[page],
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle (Rata Tengah)
        Text(
            text = subs[page],
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}