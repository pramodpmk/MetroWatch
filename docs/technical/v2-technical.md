# Sprint v2 — Technical Release Notes

## Architecture Changes

The dominant structural change this sprint is a full package rename from `com.fungames.*` (and the Android `applicationId`/`namespace` `com.fungames.reminderapp`) to `com.metrowatch.kochi.*` (`applicationId`/`namespace` `com.metrowatch.kochi`), applied consistently across every module: `composeApp`, `core/data`, `core/domain`, `core/navigation`, `core/ui`, `feature/home`, `feature/station`, `feature/timings`, `feature/fare`, `feature/settings`. The iOS framework `baseName` was changed from `"ComposeApp"` to `"Metro Watch"`.

A project-wide `CLAUDE.md` (root) plus one `CLAUDE.md` per module (`composeApp`, `core/data`, `core/domain`, `core/navigation`, `core/ui`, `feature/fare`, `feature/home`, `feature/settings`, `feature/station`, `feature/timings`) were added, documenting an agentic-workflow module registry and per-module conventions for future automated/AI-assisted work. These are documentation-only and carry no runtime behavior.

### Module Changes

- **`core/navigation`**: `Route` (in `Routes.kt`) is now itself annotated `@Serializable` at the sealed-interface level. New graph-container routes were added: `Route.HomeGraph`, `Route.StationGraph`, `Route.TimingsGraph`, `Route.FareGraph`, `Route.SettingsGraph` — all `data object`. New leaf routes: `Route.WaterMetroTimings`, `Route.WaterMetroFare`, `Route.WaterMetroHome`. `HomeDestination` now extends `Route` and gained `HomeDestination.Home`.
- **`core/navigation.BottomTab`**: gained `WATER_METRO("Boat")`, inserted between `HOME` and `CONTACTS`.
- **`core/navigation.result.NavigationKeys`**: moved from `com.fungames.core.navigation.result` to `com.metrowatch.kochi.navigation.result` and gained `STATION_PICKER_ID_RESULT`, `PLAN_TRIP_FROM_NAME`, `PLAN_TRIP_FROM_ID`, `PLAN_TRIP_TO_NAME`, `PLAN_TRIP_TO_ID` — supporting the new home-screen trip-planning station picker.
- **`feature/station`, `feature/fare`, `feature/timings`**: each feature's `Repository` interface (`TripRepository`, `FareRepository`, `TimingsRepository`) was rewritten from a single domain-shaped method (e.g. `getTripDetails(departureName, arrivalName): TripDetails?`) to a set of granular data-access methods (`getStationByName`, `getStationsByLine`, `getDistance`, `getFareSlabs`, `getTimetablesByMode`). The corresponding `*RepositoryImpl` classes are now thin adapters over `StationDao`/`ConfigDao`, and the business logic (distance summation, fare-slab matching, timetable generation) that used to live in the `Impl` classes now lives in the `UseCase` classes (`PlanTripUseCase`, `CalculateFareUseCase`, `CalculateTimingsUseCase`). This matches the `domain-agent` convention documented in the new root `CLAUDE.md`.
- **`feature/home`**: gained `WaterMetroHomeViewModel`, `WaterMetroHomePageIntent`, `WaterMetroHomeRoute`, `WaterMetroHomeScreen` for the new Water Metro home tab. `HomePageComponents.kt` grew from 302 to 581 lines as part of the redesign.
- **`feature/fare`**: gained `GeneralFare` (domain model), `GeneralFareUiState`, `WaterMetroFareRoute`, `WaterMetroFareScreen` (rendered via `WaterMetroTimingScreen` composable in the same file).

### New Abstractions

- `TripRepository`, `FareRepository`, `TimingsRepository` — redefined as DAO-facing interfaces (see above) rather than single-call domain interfaces.
- `GeneralFare` / `GeneralFareUiState` (feature/fare) — model for the new static fare-list screen.
- `WaterMetroHomePageIntent` (feature/home) — MVI intent set for the new Water Metro home tab (`Stations`, `Routes`, `Timing`, `Fare`).
- `HomePageIntent` gained `WaterMetroTiming`, `WaterMetroFare`, `SelectFromStation`, `SelectToStation`, `StationPickedForTrip(name, id)`.

### Removed Abstractions

- `NavigationKeys` in its old location (`com.fungames.core.navigation.result`) was deleted; callers must use the new `com.metrowatch.kochi.navigation.result.NavigationKeys`.
- `StationDao.deleteAllStations()` was removed with no direct replacement visible in the diff; bulk station insert continues to use `@Insert(onConflict = OnConflictStrategy.REPLACE)`.
- The old single-method `TripRepository.getTripDetails()`, `FareRepository.getFareDetails()`, `TimingsRepository.getTimings()` interfaces were removed in favor of the granular interfaces described above — any external callers of these exact signatures need to migrate to the use-case classes, which retain equivalent public entry points (`PlanTripUseCase.invoke(departure, arrival)`, `CalculateFareUseCase.invoke(departure, arrival)`, `CalculateTimingsUseCase.invoke(departure, arrival)`).

## API Changes

### New Endpoints / Methods

- `StationDao.getStationById(id: String): StationEntity?` — new lookup by primary key.
- `StationDao.getStationByName(name: String): StationEntity?` — signature unchanged, but the query now matches `nameEn = :name OR nameMl = :name OR nameHi = :name LIMIT 1` (previously `nameEn` only), adding multi-language name lookup.
- `TripRepository` / `FareRepository` / `TimingsRepository`: `getStationByName`, `getStationsByLine`, `getDistance`, `getFareSlabs` (Trip/Fare only), `getTimetablesByMode` (Trip/Timings only).
- `Route.StationDetail(val stationId: String)` — now a parameterized `data class` (see Modified Endpoints below).

### Modified Endpoints / Methods

- **Breaking:** `Route.StationDetail` changed from `data object Route.StationDetail : Route` to `data class Route.StationDetail(val stationId: String) : Route`. Any navigation call site constructing `Route.StationDetail` without an ID will no longer compile; `HomeViewModel.onIntent` was updated to pass `homeIntent.station.stationId`.
- `StationDao.getStationIdByName(name)` was removed and replaced by the broader `getStationByName` multi-field lookup; callers that only needed the ID now call `getStationByName(name)?.id`.
- `PlanTripUseCase.invoke` and `CalculateTimingsUseCase.invoke`: internal timetable generation now takes an additional `offsetMinutes` parameter (terminus-to-departure travel time) — see Data Flow Changes.
- `CalculateFareUseCase.invoke`: `FareDetails` result now includes `departureCode`, `arrivalCode`, `stops`, `estimatedTimeMin`, `lineId` in addition to the previous `distance`/`fare` fields. Distance is now rounded to one decimal place via `(distanceKm * 10).roundToInt() / 10.0` instead of interpolating the raw `Double` into a string.
- `DataModule`'s `Json` configuration replaced `isLenient = true` with `coerceInputValues = true`.
- `DataModule` now registers `SyncRepository` via `singleOf(::SyncRepository)` instead of an explicit `single { SyncRepository(get(), get(), get()) }`, and registers `AppDatabase` via `single { getDatabase(get()) }` without an explicit `<AppDatabase>` type parameter (relies on inference).

### Deprecated / Removed APIs

- `StationDao.deleteAllStations()` — removed.
- `StationDao.getStationIdByName()` — removed; use `getStationByName(name)?.id`.
- Old `TripRepository.getTripDetails`, `FareRepository.getFareDetails`, `TimingsRepository.getTimings` — removed; use the corresponding `UseCase` classes, which now own the business logic previously implemented in these methods.

## Database Changes

### New Tables / Collections

No new tables were added. `AppDatabase`'s entity list is unchanged in membership (`ConfigVersionEntity`, `ContactEntity`, `DistanceEntity`, `FareSlabEntity`, `ParkingInfoEntity`, `ParkingPassEntity`, `ParkingRateEntity`, `StationEntity`, `TimetableEntity`, `WaterMetroRouteEntity`, `WaterMetroStationEntity`) but was reordered alphabetically in the `@Database` annotation.

### Modified Schemas

**`AppDatabase.version` was reset from `3` to `1`.** A new Room schema export was added at `core/data/schemas/com.metrowatch.kochi.data.db.AppDatabase/1.json` (identity hash `936d98bc0f5eb1fa0276b3de0b603f4f`), and a new constant `DB_FILE_NAME = "metro_watch.db"` was added to `AppDatabase.kt`. This is consistent with the app's package/applicationId rename — Room treats this as a fresh database file under a new name, so no migration from the old (`com.fungames.reminderapp`) database is required or provided. Any device that previously had the old package installed will get a brand-new, empty database under the new package.

### New Migrations

None. There is currently no `Migration` object defined for `AppDatabase`; schema version starts at `1` with no upgrade path defined yet for a future version `2`.

### Query Changes

- `StationDao.getStationByName`: now `SELECT * FROM stations WHERE nameEn = :name OR nameMl = :name OR nameHi = :name LIMIT 1` (previously `nameEn = :name` only).
- `StationDao.getStationIdByName` and `StationDao.deleteAllStations` queries were removed.
- `StationDao.getStationById` added: `SELECT * FROM stations WHERE id = :id`.

## Data Flow Changes

**Timing/trip calculation now models the train's terminus origin.** Previously, `CalculateTimingsUseCase` (feature/timings) and the trip-timing logic in `TripRepositoryImpl` (feature/station) generated a train schedule starting at the rider's own departure station's timetable window (e.g. `06:00`–`22:00`), which is only correct if the departure station happens to be the line's terminus. The corrected logic:

1. Determines direction (`isAscending`) by comparing `departureStation.sequence` and `arrivalStation.sequence`.
2. Picks the actual line terminus (`allStationsOnLine.first()` or `.last()`) that trains in that direction originate from.
3. Sums per-station distances from the terminus to the departure station to compute `offsetDistanceKm`, converted to `offsetMinutes` at `35 km/h` average speed.
4. Generates trains starting at the terminus's timetable window (`trainStart` from `startMinutes` to `endMinutes` at `frequencyMinutes` intervals), applying `depMinutes = trainStart + offsetMinutes` before adding the journey's own `durationMinutes` to get `arrMinutes`.

The same offset logic was duplicated into `PlanTripUseCase` (feature/station), which previously lived only in `TripRepositoryImpl` and has now been moved/rewritten in the use case with the same terminus-offset correction.

**Home → Plan Trip station selection**: `HomeViewModel` gained `SelectFromStation`/`SelectToStation` intents that set `isPickingFromStation` on `HomeUiState` and emit `Route.StationPicker`; the picker's result comes back through `HomePageIntent.StationPickedForTrip(name, id)`, which writes to `planFromStation`/`planFromId` or `planToStation`/`planToId` on `HomeUiState` depending on which side was being picked.

## Security Changes

None identified in this diff.

## Dependency Changes

| Package | Change | Reason / Impact |
|---|---|---|
| `com.android.tools:desugar_jdk_libs` | Added at `2.1.4` | Enables `isCoreLibraryDesugaringEnabled = true` in `composeApp` and `feature:station`. `feature/station/lint.xml` explicitly suppresses the `NewApi` lint check for `TripRepositoryImpl.kt`, noting `kotlinx.datetime.DayOfWeek` is a typealias for `java.time.DayOfWeek` on the JVM, which needs API 26+ without desugaring (project `minSdk` is 24). |
| `compose.materialIconsExtended` | Added to `composeApp` common dependencies | Used by the redesigned screens (icon usage expanded, e.g. `Icons.Default.CurrencyBitcoin`, `Icons.Default.Schedule` in the new fare screen). |

No existing dependency versions were bumped in `gradle/libs.versions.toml` beyond the new `desugar-jdk-libs` alias addition.

## Performance Changes

None measured or targeted this sprint. Note: `gradle.properties` changed `org.gradle.configuration-cache` from `true` to `false`, which is a build-time regression risk (slower incremental Gradle builds), not a runtime performance change.

## Testing Changes

No new test files or test coverage were added for the feature work in this sprint (Water Metro home tab, station detail navigation, timing correction, fare enrichment). `FareRepositoryTest.kt` was updated only for the package rename and constructor signature change (moved from `com.fungames.fare` to `com.metrowatch.kochi.fare`, updated to the new `FareRepositoryImpl` constructor). Boilerplate `ExampleUnitTest`/`ExampleInstrumentedTest` files across modules were renamed/moved for the package rename only.

## Build / CI / CD Changes

- `composeApp/build.gradle.kts`: added `compose.materialIconsExtended`; enabled `isCoreLibraryDesugaringEnabled` and added `coreLibraryDesugaring(libs.desugar.jdk.libs)`; removed an unused `TargetFormat` import; `namespace`/`applicationId` changed to `com.metrowatch.kochi`; iOS framework `baseName` changed to `"Metro Watch"`.
- `feature/station/build.gradle.kts`: `namespace` changed to `com.metrowatch.kochi.station`; added `coreLibraryDesugaring(libs.desugar.jdk.libs)`.
- `gradle.properties`: `org.gradle.configuration-cache` set to `false` (was `true`); added `org.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64`.
- `settings.gradle.kts`: fixed `include("feature:fare")` (missing leading colon — worked by coincidence) to `include(":feature:fare")`, consistent with the other `include(":...")` declarations.
- `feature/station/lint.xml`: new file suppressing the `NewApi` lint rule for `TripRepositoryImpl.kt` (see Dependency Changes).

## Code Quality Changes

- Business logic that lived inside `*RepositoryImpl.getXxxDetails()` methods for trip, fare, and timing calculations was moved into their respective `UseCase` classes, with `RepositoryImpl` classes reduced to direct DAO pass-throughs. This centralizes the "distance summation → fare slab / timetable lookup → format result" logic in one layer per feature and matches the project's now-documented `domain-agent`/use-case pattern.
- Error messages in `PlanTripUseCase`, `CalculateFareUseCase`, and `CalculateTimingsUseCase` became more specific (e.g. `"Station '$departure' not found"`, `"Stations are on different lines"`) instead of a single generic `"Could not calculate ... for the selected stations"`.
- `parseTimeToMinutes` in the timing/trip use cases was rewritten from a `try/catch`-wrapped parse to a null-safe `toIntOrNull()` chain.

## Known Technical Debt Introduced

- `AppDatabase` has no `Migration` defined from its new version `1` baseline. The next schema change will need its first real migration path designed from scratch.
- `gradle.properties` now hardcodes a local machine path (`org.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64`). This is environment-specific and will likely break builds for other developers or CI runners that don't have a JDK at that exact path.
- `FareViewModel`'s Water Metro general fare list (`GeneralFare` entries: Kakkanad–Vyttila, Vyttila–Kakkanad, High Court–Fort Kochi, Fort Kochi–High Court) is hardcoded in the `ViewModel`'s `init` block rather than sourced from `ConfigDao`/`FareRepository`, unlike the rest of the fare/timing data which is DB-backed.
- Timetable-offset generation logic (terminus lookup, offset calculation, `generateAllTimings`) is now duplicated near-verbatim between `feature/timings/domain/CalculateTimingsUseCase.kt` and `feature/station/domain/PlanTripUseCase.kt`, with no shared abstraction.

## Engineering Notes for Reviewers

- Verify that all call sites constructing `Route.StationDetail` were updated for the new `stationId` constructor parameter — this is a breaking, non-optional signature change on a navigation route.
- Confirm the Room database name change (`metro_watch.db`, version reset to `1`) is intentional and acceptable for this release — existing installs under the old package (`com.fungames.reminderapp`) are unaffected since Android treats it as a different app, but any existing installs already migrated to `com.metrowatch.kochi` in a prior internal build would lose local data with no migration.
- The terminus-offset timing correction duplicates logic across two modules (`feature/timings` and `feature/station`); check that both copies stay in sync if the algorithm changes again, and consider whether this belongs in a shared module.
- Check whether `org.gradle.java.home` in `gradle.properties` should be removed before this reaches other developers' machines or CI.
- QA should verify Plan Trip and Timing Table results at both terminus stations and interior stations in both directions, since the offset calculation is the direct fix for the timing bug this sprint addressed.
- QA should verify first-run location detection on a clean Android install (uninstall/reinstall, not just app data clear) to confirm the Fused Location Provider change resolves the original issue.

---

*Generated automatically by release-doc-generator.*
*Base: v1 → HEAD*
