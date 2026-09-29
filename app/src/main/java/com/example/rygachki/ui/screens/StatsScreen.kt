package com.example.rygachki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rygachki.R
import com.example.rygachki.data.CounterStore
import java.time.LocalDate
import java.time.YearMonth

private const val MONTHS_BEFORE = 5
private const val MONTHS_AFTER = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    counterStore: CounterStore,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val todayYearMonth = YearMonth.from(today)
    val startYearMonth = YearMonth.of(todayYearMonth.year - MONTHS_BEFORE, 1)
    val pageCount = (MONTHS_BEFORE + 1 + MONTHS_AFTER) * 12
    val todayPage = MONTHS_BEFORE * 12 + todayYearMonth.monthValue - 1

    val pagerState = rememberPagerState(initialPage = todayPage) { pageCount }
    val shownYearMonth = startYearMonth.plusMonths(pagerState.currentPage.toLong())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = shownYearMonth.year.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val monthNames = stringArrayResource(R.array.month_names)
        val monthName = monthNames[shownYearMonth.monthValue - 1]
        val monthTotal = counterStore.getMonthCount(shownYearMonth)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = monthName,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(
                    R.string.month_total,
                    monthName,
                    "$monthTotal ${pluralStringResource(R.plurals.ryg_word, monthTotal, monthTotal)}"
                ),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            WeekdayHeader()

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val yearMonth = startYearMonth.plusMonths(page.toLong())
                MonthGrid(
                    yearMonth = yearMonth,
                    today = today,
                    countOfDay = counterStore::getCount
                )
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        stringArrayResource(R.array.weekday_short).forEach { day ->
            Text(
                text = day,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MonthGrid(
    yearMonth: YearMonth,
    today: LocalDate,
    countOfDay: (LocalDate) -> Int
) {
    val leadingBlanks = yearMonth.atDay(1).dayOfWeek.value - 1
    val cells = MutableList<LocalDate?>(WEEKS_IN_GRID * DAYS_IN_WEEK) { null }
    for (day in 1..yearMonth.lengthOfMonth()) {
        cells[leadingBlanks + day - 1] = yearMonth.atDay(day)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        cells.chunked(DAYS_IN_WEEK).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    if (date == null) {
                        Box(modifier = Modifier.weight(1f))
                    } else {
                        DayCell(
                            day = date.dayOfMonth,
                            count = countOfDay(date),
                            isToday = date == today,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    count: Int,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    val hasCount = count > 0
    val background = if (isToday) {
        MaterialTheme.colorScheme.primaryContainer
    } else if (hasCount) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (hasCount || isToday) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Text(
            text = if (hasCount) count.toString() else stringResource(R.string.empty),
            fontSize = 13.sp,
            color = if (hasCount) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            }
        )
    }
}

private const val DAYS_IN_WEEK = 7
private const val WEEKS_IN_GRID = 6
