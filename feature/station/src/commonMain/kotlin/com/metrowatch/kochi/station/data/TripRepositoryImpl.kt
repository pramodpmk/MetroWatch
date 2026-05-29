package com.metrowatch.kochi.station.data

import com.metrowatch.kochi.data.db.ConfigDao
import com.metrowatch.kochi.data.db.StationDao
import com.metrowatch.kochi.data.db.TimetableEntity
import com.metrowatch.kochi.station.domain.TripDetails
import com.metrowatch.kochi.station.domain.TripRepository
import com.metrowatch.kochi.station.domain.TripTiming
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TripRepositoryImpl(
    private val stationDao: StationDao,
    private val configDao: ConfigDao
) : TripRepository {

    companion object {
        private const val METRO_AVG_SPEED_KMH = 35.0
        private const val DEFAULT_FREQUENCY_MINUTES = 10
        private const val DEFAULT_START_MINUTES = 6 * 60   // 06:00
        private const val DEFAULT_END_MINUTES = 22 * 60    // 22:00
    }

    override suspend fun getTripDetails(departureName: String, arrivalName: String): TripDetails? {
        val departureStation = stationDao.getStationByName(departureName) ?: return null
        val arrivalStation = stationDao.getStationByName(arrivalName) ?: return null

        if (departureStation.lineId != arrivalStation.lineId) return null

        val allStationsOnLine = stationDao.getStationsByLine(departureStation.lineId)
            .sortedBy { it.sequence }

        val startSeq = minOf(departureStation.sequence, arrivalStation.sequence)
        val endSeq = maxOf(departureStation.sequence, arrivalStation.sequence)
        val stationsInRange = allStationsOnLine.filter { it.sequence in startSeq..endSeq }

        var totalDistanceKm = 0.0
        for (i in 0 until stationsInRange.size - 1) {
            totalDistanceKm += configDao.getDistance(stationsInRange[i].id, stationsInRange[i + 1].id) ?: 0.0
        }

        val slabs = configDao.getFareSlabs()
        val timetables = configDao.getTimetablesByMode(departureStation.mode)

        return withContext(Dispatchers.Default) {
            val matchingSlab = slabs.find { totalDistanceKm >= it.minKm && totalDistanceKm <= it.maxKm }
                ?: slabs.find { it.maxKm == 0.0 && totalDistanceKm >= it.minKm }
                ?: slabs.lastOrNull()
            val fare = matchingSlab?.let { "₹${it.fare}" } ?: "N/A"

            val durationMinutes = ((totalDistanceKm / METRO_AVG_SPEED_KMH) * 60).roundToInt()
            val durationStr = formatDuration(durationMinutes)

            val timetable = selectTimetable(timetables, departureStation.mode)
            val timings = generateAllTimings(timetable, durationMinutes, durationStr)

            val intermediateStationCount = if (stationsInRange.size > 2) stationsInRange.size - 2 else 0
            val lineName = departureStation.lineId.ifBlank { "Blue line" }

            TripDetails(
                distance = "$totalDistanceKm km",
                fare = fare,
                numberOfStations = intermediateStationCount,
                lineName = lineName,
                timings = timings
            )
        }
    }

    private fun selectTimetable(timetables: List<TimetableEntity>, mode: String): TimetableEntity {
        val isSunday = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .dayOfWeek == DayOfWeek.SUNDAY

        val selected = if (isSunday) {
            timetables.find { it.dayType.contains("sun", ignoreCase = true) }
                ?: timetables.firstOrNull()
        } else {
            timetables.find { !it.dayType.contains("sun", ignoreCase = true) && !it.dayType.contains("holiday", ignoreCase = true) }
                ?: timetables.firstOrNull()
        }

        return selected ?: TimetableEntity(
            mode = mode,
            dayType = "weekday",
            startTime = "06:00",
            endTime = "22:00",
            frequencyMinutes = DEFAULT_FREQUENCY_MINUTES
        )
    }

    private fun generateAllTimings(
        timetable: TimetableEntity,
        durationMinutes: Int,
        durationStr: String
    ): List<TripTiming> {
        val startMinutes = parseTimeToMinutes(timetable.startTime) ?: DEFAULT_START_MINUTES
        val endMinutes = parseTimeToMinutes(timetable.endTime) ?: DEFAULT_END_MINUTES
        val frequency = timetable.frequencyMinutes.coerceAtLeast(1)

        val timings = mutableListOf<TripTiming>()
        var trainNum = 1001
        var depMinutes = startMinutes

        while (depMinutes + durationMinutes <= endMinutes) {
            val arrMinutes = depMinutes + durationMinutes
            timings.add(
                TripTiming(
                    trainNumber = "$trainNum",
                    departureTime = formatTime(depMinutes / 60, depMinutes % 60),
                    arrivalTime = formatTime(arrMinutes / 60, arrMinutes % 60),
                    duration = durationStr
                )
            )
            depMinutes += frequency
            trainNum++
        }
        return timings
    }

    private fun parseTimeToMinutes(time: String): Int? {
        return try {
            val parts = time.trim().split(":")
            parts[0].trim().toInt() * 60 + parts[1].trim().toInt()
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDuration(minutes: Int): String =
        if (minutes < 60) "${minutes}m" else "${minutes / 60}h ${minutes % 60}m"

    private fun formatTime(hour: Int, minute: Int): String {
        val ampm = if (hour < 12) "am" else "pm"
        val h = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "${h.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $ampm"
    }
}
