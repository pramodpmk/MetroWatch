# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [v2] — 2026-06-11

### Added
- Added a Water Metro home tab ("Boat") with entry points to Water Metro stations, routes, timings, and fares.
- Added the ability to pick "from" and "to" stations for trip planning directly from the home screen.
- Added a Water Metro general fares screen listing standard route fares.
- Added multi-language station name matching (English, Malayalam, Hindi) to station lookup.
- Added a station lookup by ID (`StationDao.getStationById`).
- Added richer fare details (stop count, estimated travel time, line ID) to the fare calculator result.
- Added a new app logo, launcher icon, and splash screen branding.

### Changed
- Rebranded the app package identity from `com.fungames.reminderapp` to `com.metrowatch.kochi` across all modules.
- Redesigned the home screen, station list, station picker, plan trip, fare calculator, timing table, timing detail, metro routes, and parking screens, with initial dark-mode optimization.
- Reworked the trip/fare/timing repository layer so business logic (distance, fare-slab, and timetable calculations) lives in the use-case classes instead of the repository implementations.
- Station detail navigation now carries the specific station ID instead of opening a generic detail screen.
- Distance values in fare and trip results are now rounded to one decimal place.

### Fixed
- Fixed train timing calculations that assumed every train started at the rider's own departure station, producing incorrect arrival/departure times for non-terminus stations; timings now account for the train's actual line-terminus origin.
- Fixed a location detection failure that could occur on a freshly installed Android app by switching to Google's Fused Location Provider.
- Fixed a malformed Gradle module include string in `settings.gradle.kts`.

### Removed
- Removed `StationDao.deleteAllStations()` and `StationDao.getStationIdByName()`.
- Removed the single-method `TripRepository.getTripDetails`, `FareRepository.getFareDetails`, and `TimingsRepository.getTimings` interfaces in favor of granular data-access methods.

### Security
- No security changes this release.

### Performance
- No performance changes this release.

<!-- Previous entries below this line -->
