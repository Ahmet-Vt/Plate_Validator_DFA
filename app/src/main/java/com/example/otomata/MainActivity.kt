package com.example.otomata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.otomata.ui.theme.OtomataTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OtomataTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlateValidatorScreen()
                }
            }
        }
    }
}

@Composable
fun PlateValidatorScreen() {
    var plateInput by remember { mutableStateOf("") }
    var stateHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    var isValidating by remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf<Boolean?>(null) }
    var currentStateIndex by remember { mutableStateOf(-1) }

    // Scroll kontrolü için state
    val listState = rememberLazyListState()

    // Her yeni durum eklendiğinde listenin en altına otomatik kaydır
    LaunchedEffect(stateHistory.size) {
        if (stateHistory.isNotEmpty()) {
            listState.animateScrollToItem(stateHistory.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Plaka Doğrulama Otomatı",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Plaka girişi
        OutlinedTextField(
            value = plateInput,
            onValueChange = {
                plateInput = it.uppercase()
                validationResult = null
                stateHistory = emptyList()
                currentStateIndex = -1
            },
            label = { Text("Plaka Giriniz") },
            placeholder = { Text("Örn: 34 A 1234") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isValidating,
            singleLine = true
        )

        // Doğrula butonu
        Button(
            onClick = {
                if (plateInput.isNotBlank()) {
                    isValidating = true
                    validationResult = null
                    stateHistory = emptyList()
                    currentStateIndex = -1
                }
            },
            enabled = !isValidating && plateInput.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Doğrula")
        }

        // Durum geçişlerini göster (Recycler View mantığıyla LazyColumn)
        if (stateHistory.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Durum Geçişleri",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        itemsIndexed(stateHistory) { index, state ->
                            StateTransitionItem(
                                state = state,
                                isActive = index == currentStateIndex,
                                isError = state == "ErrorState"
                            )
                        }
                    }
                }
            }
        }

        // Sonuç gösterimi
        AnimatedVisibility(
            visible = validationResult != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            validationResult?.let { isValid ->
                ResultCard(isValid = isValid)
            }
        }
    }

    // Doğrulama animasyonu
    LaunchedEffect(isValidating) {
        if (isValidating) {
            stateHistory = listOf("QStart")
            currentStateIndex = 0
            delay(500)

            val tracker = TrackingPlateValidator()
            tracker.validate(plateInput.uppercase())
            val allStates = tracker.getStateHistory()

            for (i in 1 until allStates.size) {
                stateHistory = allStates.subList(0, i + 1)
                currentStateIndex = i
                delay(400)

                if (allStates[i] == "ErrorState") break
            }

            delay(500)
            val finalValidator = PlateValidator()
            validationResult = finalValidator.validate(plateInput)
            isValidating = false
        }
    }
}

@Composable
fun StateTransitionItem(
    state: String,
    isActive: Boolean,
    isError: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val backgroundColor = when {
        isError -> Color(0xFFEF5350)
        isActive -> Color(0xFF66BB6A)
        else -> Color(0xFFBDBDBD)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .background(
                color = backgroundColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = backgroundColor,
                    shape = CircleShape
                )
        )

        Text(
            text = state,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isActive) 16.sp else 14.sp,
            color = if (isError) Color(0xFFB71C1C) else Color.Unspecified
        )
    }
}

@Composable
fun ResultCard(isValid: Boolean) {
    val backgroundColor = if (isValid) Color(0xFF4CAF50) else Color(0xFFF44336)
    val iconText = if (isValid) "✓" else "✗"
    val resultText = if (isValid) "Plaka Geçerli!" else "Plaka Geçersiz!"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = iconText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Text(
                text = resultText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// PlateValidator'ı extend edip durum geçişlerini takip eden sınıf
open class TrackingPlateValidator : PlateValidator() {
    private val stateHistory = mutableListOf<String>()

    override fun changeState(newState: PlateState) {
        super.changeState(newState)
        stateHistory.add(newState::class.simpleName ?: "Unknown")
    }

    fun getStateHistory(): List<String> {
        return listOf("QStart") + stateHistory
    }
}