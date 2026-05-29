# home-agent — feature/home

## Agent Identity

You are the **home-agent**. You own everything under `feature/home/src/`. Your responsibilities: the home screen UI, nearest-station logic (Haversine distance + timetable lookup), location permission flow (expect/actual), and the action grid that dispatches navigation intents.

---

## Owned Files

```
feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/
├── presentation/
│   ├── HomeViewModel.kt           — ViewModel: state + intent dispatch + location logic
│   ├── HomePageIntent.kt          — sealed interface of all user actions
│   ├── HomePageUi.kt              — UI state data class + PageState enum
│   ├── NearestStation.kt          — data class for nearest station display card
│   ├── HomeScreen.kt              — root composable for the home tab
│   ├── HomePageComponents.kt      — sub-composables (action grid, station card, etc.)
│   └── HomeRoute.kt               — feature-local route entry point
├── navigation/
│   └── HomeNavigation.kt          — HomeRoutes sealed interface + homeGraph() extension
├── location/
│   └── LocationPermissionLauncher.kt  — expect fun rememberLocationPermissionLauncher(...)
├── di/
│   └── HomeModule.kt              — Koin module
└── Platform.kt                    — expect/actual stub

feature/home/src/androidMain/kotlin/com/metrowatch/kochi/home/
├── location/LocationPermissionLauncher.android.kt  — Android actual
└── Platform.android.kt

feature/home/src/iosMain/kotlin/com/metrowatch/kochi/home/
├── location/LocationPermissionLauncher.ios.kt      — iOS actual (CLLocationManager)
└── Platform.ios.kt
```

---

## Read Before Every Change

```
feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/presentation/HomeViewModel.kt
feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/presentation/HomePageIntent.kt
feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/presentation/HomePageUi.kt
feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/di/HomeModule.kt
```

---

## MVI Pattern

All user interactions flow through `HomePageIntent`. The composable calls `viewModel.userIntent(intent)`, which is the single entry point.

```kotlin
sealed interface HomePageIntent {
    data class ClickOnStation(val station: NearestStation) : HomePageIntent
    object ClickedOnLocation   : HomePageIntent
    object LocationFetching    : HomePageIntent
    data class LocationGranted(val lat: Double, val lon: Double) : HomePageIntent
    object LocationDenied      : HomePageIntent
    object ViewAllStations     : HomePageIntent
    object FareCalculation     : HomePageIntent
    object Timings             : HomePageIntent
    object Settings            : HomePageIntent
    object PlanTrip            : HomePageIntent
    object WaterMetroStations  : HomePageIntent
    object WaterMetroRoutes    : HomePageIntent
    object Parking             : HomePageIntent
    object MetroRoutes         : HomePageIntent
    object WaterMetroTiming    : HomePageIntent
    object WaterMetroFare      : HomePageIntent
}
```

**Adding a new intent:**
1. Add the case to `HomePageIntent`.
2. Handle it in `HomeViewModel.userIntent()` — emit to `_homeNavigationEffect` for navigation or update `_homeState` for UI changes.

---

## ViewModel State and Effects

```kotlin
class HomeViewModel(
    private val stationDao: StationDao,
    private val configDao: ConfigDao
) : ViewModel() {

    val homeState: StateFlow<HomePageUi>
    val homeNavigationEffect: SharedFlow<Route>

    fun userIntent(homeIntent: HomePageIntent)
}
```

**Rules:**
- Navigation side effects are emitted on `homeNavigationEffect: SharedFlow<Route>` — the composable collects these in a `LaunchedEffect`.
- `HomePageUi` is a flat data class; never nest `DomainState` inside it. Map to explicit boolean/string fields.
- `HomeViewModel` injects `StationDao` and `ConfigDao` directly (not a repository) — this is intentional for performance.

---

## HomePageUi

```kotlin
data class HomePageUi(
    val locationText: String,
    val locationLatitude: Double,
    val locationLongitude: Double,
    val nearestStation: NearestStation,
    val nearestStationAvailable: Boolean,
    val stationList: List<NearestStation>,
    val pageState: PageState   // Loading | Success | Error
)
```

Use `HomePageUi.initData()` for the initial empty state. Update with `.copy(...)`.

---

## Nearest Station Algorithm

```
1. stationDao.getAllStationsList()              → List<StationEntity>
2. stations.minByOrNull { haversineDistance(userLat, userLon, it.latitude, it.longitude) }
3. Format distance:
   if < 1 km  → "${(km * 1000).roundToInt()} m away"
   if >= 1 km → use (km * 10).roundToInt() / 10 pattern — NO String.format()
4. configDao.getTimetableByMode(nearest.mode)  → TimetableEntity?
5. Parse timetable startTime/endTime (12-hour "6:00 am" format)
6. Calculate next train: minutesSinceStart % frequencyMinutes → formatMinutesToTime()
   or return "Tomorrow" if past endTime
```

**Haversine is in `kotlin.math.*` only — safe for commonMain.**

---

## Location Permission

```kotlin
// commonMain
expect fun rememberLocationPermissionLauncher(
    onLocation: (lat: Double, lon: Double) -> Unit,
    onDenied: () -> Unit
): () -> Unit
```

- **Android actual:** `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission)` + `LocationManager.requestSingleUpdate`.
- **iOS actual:** `CLLocationManager` with `NSObject + CLLocationManagerDelegateProtocol`, calls `requestWhenInUseAuthorization()` then `requestLocation()`.

**Manifest/plist requirements (do not remove):**
- Android: `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION` in `composeApp/src/androidMain/AndroidManifest.xml`
- iOS: `NSLocationWhenInUseUsageDescription` in `iosApp/iosApp/Info.plist`

---

## Feature Navigation

```kotlin
sealed interface HomeRoutes {
    @Serializable data object HomePage : HomeRoutes
}

fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    composable<HomeRoutes.HomePage> {
        HomeRoute(navController)
    }
}
```

`HomeRoute` is the entry composable that sets up the ViewModel, collects `homeNavigationEffect`, and passes navigation calls back up to the root NavController.

---

## DI Module

```kotlin
// HomeModule.kt
val homeModule = module {
    viewModelOf(::HomeViewModel)
}
```

`HomeViewModel` receives `StationDao` and `ConfigDao` from Koin automatically (both are registered as `single` in `dataModule`).

---

## CommonMain Safety Rules

- `kotlin.math.*` only — no `java.math.*`.
- `(value * 10).roundToInt()` for one-decimal formatting — no `String.format`.
- `getLocalTime()` from `core/ui/Platform.kt` for current device time.
- No `android.*` imports in `commonMain`.
