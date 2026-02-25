package com.fungames.feature.timings.data

import com.fungames.core.data.db.ConfigDao
import com.fungames.core.data.db.StationDao
import com.fungames.feature.timings.domain.TimingsRepository
import com.fungames.feature.timings.domain.TrainTiming
import kotlin.math.roundToInt

class TimingsRepositoryImpl(
    private val stationDao: StationDao,
    private val configDao: ConfigDao
) : TimingsRepository {

    companion object {
        private const val METRO_AVG_SPEED_KMH = 35.0
        private const val DEFAULT_FREQUENCY_MINUTES = 10
    }

    override suspend fun getTimings(departureName: String, arrivalName: String): List<TrainTiming> {
        val departureStation = stationDao.getStationByName(departureName) ?: return emptyList()
        val arrivalStation = stationDao.getStationByName(arrivalName) ?: return emptyList()

        if (departureStation.lineId != arrivalStation.lineId) {
            return emptyList()
        }

        val allStationsOnLine = stationDao.getStationsByLine(departureStation.lineId)
            .sortedBy { it.sequence }

        val startSeq = minOf(departureStation.sequence, arrivalStation.sequence)
        val endSeq = maxOf(departureStation.sequence, arrivalStation.sequence)

        val stationsInRange = allStationsOnLine.filter { it.sequence in startSeq..endSeq }

        var totalDistanceKm = 0.0
        for (i in 0 until stationsInRange.size - 1) {
            val s1 = stationsInRange[i]
            val s2 = stationsInRange[i + 1]
            val stepDistance = configDao.getDistance(s1.id, s2.id) ?: 0.0
            totalDistanceKm += stepDistance
        }

        // Estimate duration in minutes
        val durationMinutes = ((totalDistanceKm / METRO_AVG_SPEED_KMH) * 60).roundToInt()
        val durationStr = if (durationMinutes < 60) "${durationMinutes}m" else "${durationMinutes / 60}h ${durationMinutes % 60}m"

        val timetable = configDao.getTimetableByMode(departureStation.mode)
        val frequency = timetable?.frequencyMinutes ?: DEFAULT_FREQUENCY_MINUTES

        return generateTimings(durationMinutes, durationStr, frequency)
    }

    private fun generateTimings(durationMinutes: Int, durationStr: String, frequency: Int): List<TrainTiming> {
        val timings = mutableListOf<TrainTiming>()
        val startHour = 8
        val startMinute = 0

        for (i in 0 until 5) {
            val depTotalMinutes = (startHour * 60) + startMinute + (i * frequency)
            val depHour = (depTotalMinutes / 60) % 24
            val depMin = depTotalMinutes % 60

            val arrTotalMinutes = depTotalMinutes + durationMinutes
            val arrHour = (arrTotalMinutes / 60) % 24
            val arrMin = arrTotalMinutes % 60

            val depTime = formatTime(depHour, depMin)
            val arrTime = formatTime(arrHour, arrMin)

            timings.add(
                TrainTiming(
                    trainNumber = "${1001 + i}",
                    trainName = "Metro Train ${i + 1}",
                    departureTime = depTime,
                    arrivalTime = arrTime,
                    duration = durationStr
                )
            )
        }
        return timings
    }

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
