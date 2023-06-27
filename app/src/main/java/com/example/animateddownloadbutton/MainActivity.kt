package com.example.animateddownloadbutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animateddownloadbutton.ui.theme.AnimatedDownloadButtonTheme
import com.example.animateddownloadbutton.ui.theme.LightGreen
import com.example.animateddownloadbutton.ui.theme.OceanBlue
import com.example.animateddownloadbutton.ui.theme.monsterrat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimatedDownloadButtonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedDownloadButton()
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedDownloadButton() {
    var downloadStarted by remember {
        mutableStateOf(false)
    }

    var downloadPercentage by remember {
        mutableStateOf(0)
    }

    val progress by
    animateFloatAsState(
        targetValue = if (downloadStarted) {
            (downloadPercentage / 100f) * 360f
        } else 0f,
        animationSpec = tween(easing = LinearEasing)
    )

    val downloadCompleted by remember {
        derivedStateOf { downloadPercentage >= 100 }
    }

    val scope = rememberCoroutineScope()

    val angle by animateFloatAsState(targetValue = if (downloadStarted) 360f else 0f)

    val downloadText by remember {
        derivedStateOf { if (downloadCompleted) "Open" else "Download" }
    }

    val borderColor by
    animateColorAsState(targetValue = if (downloadCompleted) LightGreen else OceanBlue)

    val icon by remember {
        derivedStateOf {
            if (downloadStarted) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_download
            }
        }
    }

    suspend fun startDownload() {
        for (i in 0..100) {
            delay(20)
            downloadPercentage += 1
        }
    }

    Row(
        Modifier
            .height(58.dp)
            .wrapContentWidth()
            .clickable {
                downloadStarted = !downloadStarted
                downloadPercentage = 0
                if (downloadStarted) {
                    scope.launch {
                        startDownload()
                    }
                }
            }
            .drawWithContent {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(size.width, size.width),
                    style = Stroke(width = 1.dp.toPx())
                )
                if (downloadStarted and downloadCompleted.not()) {
                    drawProgressDownloadCurve(progress = progress)
                }
                drawContent()
            }
            .padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(visible = downloadCompleted.not()) {
            Image(
                painter = painterResource(id = icon),
                modifier = Modifier
                    .size(48.dp)
                    .rotate(angle)
                    .background(OceanBlue, shape = CircleShape)
                    .padding(12.dp),
                contentDescription = ""
            )
        }

        AnimatedVisibility(visible = downloadStarted.not() or downloadCompleted) {
            Text(
                text = downloadText,
                fontFamily = monsterrat,
                color = Color.White,
                modifier = Modifier.padding(horizontal = if (downloadCompleted.not()) 28.dp else 38.dp),
                fontSize = 20.sp
            )
        }
    }
}

fun ContentDrawScope.drawProgressDownloadCurve(progress: Float){
    drawArc(
        color = Color.White,
        startAngle = -90f,
        sweepAngle = progress,
        useCenter = false,
        size = Size(size.width, size.height),
        style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)
    )
    val center = Offset(size.width / 2f, size.height / 2f)
    val beta = (progress - 90f) * (PI / 180f).toFloat()
    val r = size.width / 2f
    val a = cos(beta) * r
    val b = sin(beta) * r
    drawPoints(
        listOf(Offset(center.x + a, center.y + b)),
        pointMode = PointMode.Points,
        color = Color.White,
        strokeWidth = 10.dp.toPx(),
        cap = StrokeCap.Round
    )
}