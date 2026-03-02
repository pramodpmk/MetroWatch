package com.fungames.core.station.data

import com.fungames.core.data.db.ConfigDao
import com.fungames.core.data.db.StationDao
import com.fungames.core.station.domain.TripDetails
import com.fungames.core.station.domain.TripRepository
import com.fungames.core.station.domain.TripTiming
import kotlin.math.roundToInt

class TripRepositoryImpl(
    private val stationDao: StationDao,
    private val configDao: ConfigDao
) : TripRepository {

    companion object {
        private const val METRO_AVG_SPEED_KMH = 35.0
        private const val DEFAULT_FREQUENCY_MINUTES = 10
    }

    override suspend fun getTripDetails(departureName: String, arrivalName: String): TripDetails? {
        val departureStation = stationDao.getStationByName(departureName) ?: return null
        val arrivalStation = stationDao.getStationByName(arrivalName) ?: return null

        if (departureStation.lineId != arrivalStation.lineId) {
            return null
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

        // Calculate fare
        val slabs = configDao.getFareSlabs()
        val matchingSlab = slabs.find { totalDistanceKm >= it.minKm && totalDistanceKm <= it.maxKm }
            ?: slabs.find { it.maxKm == 0.0 && totalDistanceKm >= it.minKm }
            ?: slabs.lastOrNull()

        val fare = matchingSlab?.let { "₹${it.fare}" } ?: "N/A"

        // Calculate timings
        val durationMinutes = ((totalDistanceKm / METRO_AVG_SPEED_KMH) * 60).roundToInt()
        val durationStr = if (durationMinutes < 60) "${durationMinutes}m" else "${durationMinutes / 60}h ${durationMinutes % 60}m"

        val timetable = configDao.getTimetableByMode(departureStation.mode)
        val frequency = timetable?.frequencyMinutes ?: DEFAULT_FREQUENCY_MINUTES

        val timings = generateTimings(durationMinutes, durationStr, frequency)

        // Intermediate stations (exclude departure and arrival)
        val intermediateStationCount = if (stationsInRange.size > 2) stationsInRange.size - 2 else 0

        // Line name
        val lineName = departureStation.lineId.ifBlank { "Blue line" }

        return TripDetails(
            distance = "$totalDistanceKm km",
            fare = fare,
            numberOfStations = intermediateStationCount,
            lineName = lineName,
            timings = timings
        )
    }

    private fun generateTimings(durationMinutes: Int, durationStr: String, frequency: Int): List<TripTiming> {
        val timings = mutableListOf<TripTiming>()
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
                TripTiming(
                    trainNumber = "${1001 + i}",
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
