# timings-agent — feature/timings

## Agent Identity

You are the **timings-agent**. You own everything under `feature/timings/src/`. Your responsibilities: metro train timetable display, timing detail calculation per station stop, and water metro timetable screen.

---

## Owned Files

```
feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/
├── domain/
│   ├── TrainTiming.kt                  — domain model for a timetable entry
│   ├── TimingsRepository.kt            — interface
│   └── CalculateTimingsUseCase.kt      — computes timings between two stations
├── data/
│   └── TimingsRepositoryImpl.kt        — queries ConfigDao
├── presentation/
│   ├── TimingTableScreen.kt            — main timetable list screen
│   ├── TimingTableViewModel.kt
│   ├── WaterMetroTimingScreen.kt       — water metro timetable screen
│   └── detail/
│       ├── TimingDetailScreen.kt       — per-station arrival times
│       └── TimingDetailViewModel.kt
├── navigation/
│   └── TimingsNavigation.kt            — TimingRoutes + timingsGraph() extension
├── di/
│   └── TimingsModule.kt
└── Platform.kt
```

---

## Read Before Every Change

```
feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/domain/TimingsRepository.kt
feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/domain/CalculateTimingsUseCase.kt
feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/domain/TrainTiming.kt
feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/di/TimingsModule.kt
feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/navigation/TimingsNavigation.kt
```

---

## Domain Layer

### TrainTiming

The domain model returned by `CalculateTimingsUseCase`. Contains timetable start/end/frequency and the computed next departure from the given station.

### TimingsRepository Interface

```kotlin
interface TimingsRepository {
    suspend fun getTimetableByMode(mode: String): TimetableEntity?
    suspend fun getTimetablesByMode(mode: String): List<TimetableEntity>
}
```

`TimingsRepositoryImpl` injects `ConfigDao` and delegates directly to it.

### CalculateTimingsUseCase

Uses distance between two stations divided by average speed (35 km/h) to compute arrival times at each intermediate station. Accepts departure + arrival station IDs as constructor parameters.

**Timing calculation rules:**
- Distance from `ConfigDao.getDistance(fromId, toId)`.
- Travel time (minutes) = `(distanceKm / 35.0) * 60`.
- First departure from `TimetableEntity.startTime` (12-hour format, e.g. `"6:00 am"`).
- Parse 12-hour time to minutes-since-midnight — same `parseTimeToMinutes` logic as in `HomeViewModel`.
- Format output as `"h:mm am/pm"` — same `formatMinutesToTime` logic.
- Do not use `String.format` — not KMP-safe in commonMain.

---

## Feature Navigation

```kotlin
sealed interface TimingRoutes {
    @Serializable data object Timings         : TimingRoutes
    @Serializable data object TimingDetail    : TimingRoutes
    @Serializable data object WaterMetroTimings: TimingRoutes
}

fun NavGraphBuilder.timingsGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit
)
```

`AppGraph.kt` bridges:
- `Route.Timings` → `TimingRoutes.Timings`
- `Route.WaterMetroTimings` → `TimingRoutes.WaterMetroTimings`

`TimingDetail` is navigated internally within the timings graph (no global `Route.*` needed).

---

## DI Module (exact)

```kotlin
val timingsModule = module {
    factoryOf(::TimingsRepositoryImpl) bind TimingsRepository::class
    factoryOf(::CalculateTimingsUseCase)
    viewModelOf(::TimingTableViewModel)
    viewModelOf(::TimingDetailViewModel)
}
```

**All registrations are `factoryOf`** — timings calculations are stateless and per-use. ViewModels use `viewModelOf` for Koin-Compose lifecycle.

---

## ViewModel Pattern

```kotlin
class TimingTableViewModel(
    private val timingsRepository: TimingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimingTableUiState())
    val uiState: StateFlow<TimingTableUiState> = _uiState.asStateFlow()
}
```

No navigation side effects from `TimingTableViewModel` — users navigate forward (to detail) via the NavController passed to the composable.

---

## Adding a New Timing Screen

1. Add the screen composable + ViewModel here.
2. Add a `TimingRoutes.NewScreen` entry to `TimingsNavigation.kt`.
3. Add `composable<TimingRoutes.NewScreen> { ... }` in `timingsGraph()`.
4. If globally reachable: add `Route.NewTimingScreen` to `core/navigation/Routes.kt` (notify `navigation-agent`) and a redirect entry in `AppGraph.kt` (notify `app-agent`).
5. Register ViewModel in `TimingsModule.kt`.

---

## CommonMain Safety Rules

- No `String.format` for time formatting. Use string concatenation + `padStart(2, '0')`.
- All time parsing via `Regex("\\s+")` split on the 12-hour time string.
- Distance queries via `ConfigDao.getDistance()` — handles bidirectional lookup internally.
