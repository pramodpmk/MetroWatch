package com.metrowatch.kochi.station.domain

import com.metrowatch.kochi.data.db.TimetableEntity
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt

class PlanTripUseCase(
    private val tripRepository: TripRepository
) {
    companion object {
        private const val METRO_AVG_SPEED_KMH = 35.0
        private const val DEFAULT_FREQUENCY_MINUTES = 10
        private const val DEFAULT_START_MINUTES = 6 * 60
        private const val DEFAULT_END_MINUTES = 22 * 60
    }

    operator fun invoke(departure: String, arrival: String): Flow<DomainState<TripDetails>> = flow {
        emit(DomainState.Loading)
        try {
            val departureStation = tripRepository.getStationByName(departure)
                ?: run {
                    emit(DomainState.Error("Station '$departure' not found"))
                    return@flow
                }
            val arrivalStation = tripRepository.getStationByName(arrival)
                ?: run {
                    emit(DomainState.Error("Station '$arrival' not found"))
                    return@flow
                }

            if (departureStation.lineId != arrivalStation.lineId) {
                emit(DomainState.Error("Stations are on different lines"))
                return@flow
            }

            val allStationsOnLine = tripRepository.getStationsByLine(departureStation.lineId)

            // Direction determines which terminus trains originate from
            val isAscending = departureStation.sequence < arrivalStation.sequence
            val terminus = if (isAscending) allStationsOnLine.first() else allStationsOnLine.last()

            // Offset: travel time from terminus to departure station
            val termToDepStations = allStationsOnLine.filter {
                it.sequence in minOf(terminus.sequence, departureStation.sequence)..maxOf(terminus.sequence, departureStation.sequence)
            }
            var offsetDistanceKm = 0.0
            for (i in 0 until termToDepStations.size - 1) {
                offsetDistanceKm += tripRepository.getDistance(termToDepStations[i].id, termToDepStations[i + 1].id) ?: 0.0
            }
            val offsetMinutes = ((offsetDistanceKm / METRO_AVG_SPEED_KMH) * 60).roundToInt()

            // Journey distance: departure to arrival
            val startSeq = minOf(departureStation.sequence, arrivalStation.sequence)
            val endSeq = maxOf(departureStation.sequence, arrivalStation.sequence)
            val stationsInRange = allStationsOnLine.filter { it.sequence in startSeq..endSeq }
            var totalDistanceKm = 0.0
            for (i in 0 until stationsInRange.size - 1) {
                totalDistanceKm += tripRepository.getDistance(stationsInRange[i].id, stationsInRange[i + 1].id) ?: 0.0
            }

            val slabs = tripRepository.getFareSlabs()
            val matchingSlab = slabs.find { totalDistanceKm >= it.minKm && totalDistanceKm <= it.maxKm }
                ?: slabs.find { it.maxKm == 0.0 && totalDistanceKm >= it.minKm }
                ?: slabs.lastOrNull()
            val fare = matchingSlab?.let { "₹${it.fare}" } ?: "N/A"

            val durationMinutes = ((totalDistanceKm / METRO_AVG_SPEED_KMH) * 60).roundToInt()
            val timetables = tripRepository.getTimetablesByMode(departureStation.mode)
            val timetable = selectTimetable(timetables, departureStation.mode)
            val timings = generateAllTimings(timetable, offsetMinutes, durationMinutes, formatDuration(durationMinutes))

            val intermediateStations = if (stationsInRange.size > 2) stationsInRange.size - 2 else 0
            val distanceNumber = ((totalDistanceKm * 10).roundToInt()) / 10.0
            emit(DomainState.Success(
                TripDetails(
                    distance = "${distanceNumber} km",
                    fare = fare,
                    numberOfStations = intermediateStations,
                    lineName = departureStation.lineId.ifBlank { "Blue line" },
                    timings = timings
                )
            ))
        } catch (e: Exception) {
            emit(DomainState.Error(e.message ?: "An unknown error occurred", e))
        }
    }.flowOn(Dispatchers.IO)

    private fun selectTimetable(timetables: List<TimetableEntity>, mode: String): TimetableEntity {
        val isSunday = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .dayOfWeek == DayOfWeek.SUNDAY

        val selected = if (isSunday) {
            timetables.find { it.dayType.contains("sun", ignoreCase = true) }
                ?: timetables.firstOrNull()
        } else {
            timetables.find {
                !it.dayType.contains("sun", ignoreCase = true) &&
                !it.dayType.contains("holiday", ignoreCase = true)
            } ?: timetables.firstOrNull()
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
        offsetMinutes: Int,
        durationMinutes: Int,
        durationStr: String
    ): List<TripTiming> {
        val startMinutes = parseTimeToMinutes(timetable.startTime) ?: DEFAULT_START_MINUTES
        val endMinutes = parseTimeToMinutes(timetable.endTime) ?: DEFAULT_END_MINUTES
        val frequency = timetable.frequencyMinutes.coerceAtLeast(1)

        val timings = mutableListOf<TripTiming>()
        var trainNum = 1001
        var trainStart = startMinutes

        while (trainStart <= endMinutes) {
            val depMinutes = trainStart + offsetMinutes
            val arrMinutes = depMinutes + durationMinutes
            timings.add(
                TripTiming(
                    trainNumber = "$trainNum",
                    departureTime = formatTime(depMinutes / 60, depMinutes % 60),
                    arrivalTime = formatTime(arrMinutes / 60, arrMinutes % 60),
                    duration = durationStr
                )
            )
            trainStart += frequency
            trainNum++
        }
        return timings
    }

    private fun parseTimeToMinutes(time: String): Int? {
        val parts = time.trim().split(":")
        if (parts.size < 2) return null
        val hour = parts[0].trim().toIntOrNull() ?: return null
        val minute = parts[1].trim().toIntOrNull() ?: return null
        return hour * 60 + minute
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
