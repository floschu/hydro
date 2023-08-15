package at.florianschuster.hydro.model

import android.icu.text.MeasureFormat
import android.icu.util.MeasureUnit
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toLocalDateTime
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

val UserTimeZone: TimeZone
    get() = TimeZone.currentSystemDefault()

val NowInstant: Instant
    get() = Clock.System.now()

val TodayNow: LocalDateTime
    get() = NowInstant.toLocalDateTime(UserTimeZone)

val Today: LocalDate
    get() = TodayNow.date

val Now: LocalTime
    get() = TodayNow.time

operator fun LocalTime.plus(
    duration: Duration
): LocalTime {
    return LocalTime.fromMillisecondOfDay(
        toMillisecondOfDay() + duration.inWholeMilliseconds.toInt()
    )
}

private val usDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
private val euDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun LocalDate.format(
    locale: Locale = Locale.getDefault()
): String = when (locale) {
    Locale.US,
    Locale.UK -> toJavaLocalDate().format(usDateFormatter)

    else -> toJavaLocalDate().format(euDateFormatter)
}

private val usTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
private val euTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
fun LocalTime.format(
    locale: Locale = Locale.getDefault()
): String = when (locale) {
    Locale.US,
    Locale.UK -> toJavaLocalTime().format(usTimeFormatter)

    else -> toJavaLocalTime().format(euTimeFormatter)
}

fun Duration.format(
    locale: Locale = Locale.getDefault()
): String {
    return DurationFormat(locale).format(this, DurationFormat.DurationUnit.MINUTE)
}

// https://gist.github.com/Jeehut/78534c27b24d78f14a3cbd3eebead861
class DurationFormat(
    private val locale: Locale = Locale.getDefault()
) {

    enum class DurationUnit {
        DAY, HOUR, MINUTE, SECOND, MILLISECOND
    }

    fun format(
        duration: Duration,
        smallestUnit: DurationUnit = DurationUnit.SECOND
    ): String {
        val formattedStringComponents = mutableListOf<String>()
        var remainder = duration

        for (unit in DurationUnit.entries) {
            val component = calculateComponent(unit, remainder)

            remainder = when (unit) {
                DurationUnit.DAY -> remainder - component.days
                DurationUnit.HOUR -> remainder - component.hours
                DurationUnit.MINUTE -> remainder - component.minutes
                DurationUnit.SECOND -> remainder - component.seconds
                DurationUnit.MILLISECOND -> remainder - component.milliseconds
            }

            val unitDisplayName = unitDisplayName(unit)

            if (component > 0) {
                val formattedComponent = NumberFormat.getInstance(locale).format(component)
                formattedStringComponents.add("$formattedComponent$unitDisplayName")
            }

            if (unit == smallestUnit) {
                val formattedZero = NumberFormat.getInstance(locale).format(0)
                if (formattedStringComponents.isEmpty()) {
                    formattedStringComponents.add("$formattedZero$unitDisplayName")
                }
                break
            }
        }

        return formattedStringComponents.joinToString(" ")
    }

    private fun calculateComponent(unit: DurationUnit, remainder: Duration) = when (unit) {
        DurationUnit.DAY -> remainder.inWholeDays
        DurationUnit.HOUR -> remainder.inWholeHours
        DurationUnit.MINUTE -> remainder.inWholeMinutes
        DurationUnit.SECOND -> remainder.inWholeSeconds
        DurationUnit.MILLISECOND -> remainder.inWholeMilliseconds
    }

    private fun unitDisplayName(unit: DurationUnit): String {
        val measureFormat = MeasureFormat.getInstance(
            locale,
            MeasureFormat.FormatWidth.NARROW
        )
        return when (unit) {
            DurationUnit.DAY -> measureFormat.getUnitDisplayName(MeasureUnit.DAY)
            DurationUnit.HOUR -> measureFormat.getUnitDisplayName(MeasureUnit.HOUR)
            DurationUnit.MINUTE -> measureFormat.getUnitDisplayName(MeasureUnit.MINUTE)
            DurationUnit.SECOND -> measureFormat.getUnitDisplayName(MeasureUnit.SECOND)
            DurationUnit.MILLISECOND -> measureFormat.getUnitDisplayName(MeasureUnit.MILLISECOND)
        }
    }
}
