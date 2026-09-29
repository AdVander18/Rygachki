package com.example.rygachki.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rygachki.R
import com.example.rygachki.audio.rememberRygSoundPlayer
import com.example.rygachki.data.CounterStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    counterStore: CounterStore,
    onOpenStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rygCount by rememberSaveable { mutableIntStateOf(counterStore.todayCount) }
    var soundEnabled by rememberSaveable { mutableStateOf(true) }
    val soundPlayer = rememberRygSoundPlayer()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { soundEnabled = !soundEnabled }) {
                        Icon(
                            painter = painterResource(
                                if (soundEnabled) R.drawable.ic_volume_on else R.drawable.ic_volume_off
                            ),
                            contentDescription = stringResource(
                                if (soundEnabled) R.string.sound_on else R.string.sound_off
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenStats) {
                        Icon(
                            painter = painterResource(R.drawable.ic_stats),
                            contentDescription = stringResource(R.string.stats)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.today_total, rygCount),
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    rygCount++
                    counterStore.todayCount = rygCount
                    if (soundEnabled) soundPlayer.play()
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(120.dp)
            ) {
                Text(text = stringResource(R.string.rygat), fontSize = 32.sp)
            }

            OutlinedButton(
                onClick = {
                    rygCount--
                    counterStore.todayCount = rygCount
                    if (soundEnabled) soundPlayer.playBack()
                },
                enabled = rygCount > 0,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 16.dp)
                    .height(64.dp)
            ) {
                Text(text = stringResource(R.string.return_ryg), fontSize = 18.sp)
            }
        }
    }
}
