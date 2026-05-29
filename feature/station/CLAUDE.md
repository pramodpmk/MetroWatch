# station-agent — feature/station

## Agent Identity

You are the **station-agent**. You own everything under `feature/station/src/`. Your responsibilities cover the largest feature scope in the project: station list/detail, station picker (used by fare and trip planning), water metro, metro routes, parking, contacts/emergency, and trip planning.

---

## Owned Files

```
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/
├── domain/
│   ├── Station.kt                     — domain model
│   ├── StationRepository.kt           — interface
│   ├── StationListUseCase.kt
│   ├── TripRepository.kt              — interface
│   ├── TripDetails.kt                 — domain model for trip result
│   ├── PlanTripUseCase.kt
│   ├── WaterMetro.kt                  — water metro domain models
│   ├── WaterMetroStationsUseCase.kt
│   ├── WaterMetroRoutesUseCase.kt
│   ├── MetroRoute.kt                  — metro route domain model
│   ├── MetroRoutesUseCase.kt
│   ├── Parking.kt                     — parking domain models
│   ├── ParkingInfoUseCase.kt
│   ├── Contact.kt                     — contact domain model
│   └── ContactsUseCase.kt
├── data/
│   ├── StationRepositoryImpl.kt
│   └── TripRepositoryImpl.kt
├── presentation/
│   ├── StationViewModel.kt            — station list + search
│   ├── list/
│   │   ├── StationListScreen.kt
│   │   ├── StationListRoute.kt
│   │   └── StationListUi.kt
│   ├── detail/
│   │   └── StationDetailRoute.kt
│   ├── picker/
│   │   ├── StationPickerScreen.kt
│   │   ├── StationPickerRoute.kt
│   │   ├── StationPickerUi.kt
│   │   └── StationPickerViewModel.kt
│   ├── plantrip/
│   │   ├── PlanTripScreen.kt
│   │   ├── PlanTripRoute.kt
│   │   └── PlanTripViewModel.kt
│   ├── watermetro/
│   │   ├── WaterMetroStationsScreen.kt
│   │   ├── WaterMetroStationsViewModel.kt
│   │   ├── WaterMetroRoutesScreen.kt
│   │   └── WaterMetroRoutesViewModel.kt
│   ├── metroroutes/
│   │   ├── MetroRoutesScreen.kt
│   │   └── MetroRoutesViewModel.kt
│   ├── parking/
│   │   ├── ParkingScreen.kt
│   │   └── ParkingViewModel.kt
│   └── contact/
│       ├── ContactsScreen.kt
│       ├── ContactsRoute.kt
│       └── ContactsViewModel.kt
├── navigation/
│   └── StationNavigation.kt           — StationRoutes + stationsGraph() extension
└── di/
    └── StationModule.kt
```

---

## Read Before Every Change

For list/search tasks:
```
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/presentation/StationViewModel.kt
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/presentation/list/StationListUi.kt
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/domain/StationRepository.kt
```

For trip planning tasks:
```
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/domain/PlanTripUseCase.kt
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/domain/TripDetails.kt
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/data/TripRepositoryImpl.kt
```

Always read:
```
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/di/StationModule.kt
feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/navigation/StationNavigation.kt
```

---

## Domain Layer

### Station Model

```kotlin
data class Station(
    val id: String,
    val nameEn: String,
    val nameMl: String?,
    val nameHi: String?,
    val latitude: Double,
    val longitude: Double,
    val lineId: String,
    val sequence: Int,
    val mode: String,
    val wheelchairAccessible: Boolean
)
```

### StationRepository Interface

```kotlin
interface StationRepository {
    fun getAllStations(): Flow<List<Station>>
    suspend fun getAllStationsList(): List<Station>
    suspend fun getStationById(id: String): Station?
}
```

`StationRepositoryImpl` maps `StationEntity` → `Station`.

### TripRepository Interface

```kotlin
interface TripRepository {
    suspend fun getDistance(fromId: String, toId: String): Double?
    suspend fun getFareForDistance(distanceKm: Double): Double?
}
```

`TripRepositoryImpl` is a **factory** (not singleton) — it's instantiated per-use inside `PlanTripUseCase`.

---

## Station Search (StationViewModel)

```kotlin
data class StationListUi(
    val stationList: List<Station>,
    val filteredList: List<Station>,
    val searchQuery: String,
    val isLoading: Boolean,
    val error: String?
)
```

Search filters across `nameEn`, `nameMl`, `nameHi` — all case-insensitive. Updates `filteredList` reactively as `searchQuery` changes.

---

## Station Picker

The station picker is a reusable selection screen. Any feature that needs station selection navigates to `Route.StationPicker` and receives the result via `LocalNavigationResults`.

**Sender (e.g., FareViewModel navigating to picker):**
```kotlin
_navigationEffect.emit(Route.StationPicker)
```

**Receiver (e.g., FareScreen after picker returns):**
```kotlin
val nav = LocalNavigationResults.current
LaunchedEffect(nav.version) {
    val station = nav.consume<Station>(NavigationKeys.STATION_PICKER_RESULT) ?: return@LaunchedEffect
    // use station
}
```

**Picker sets result before popping:**
```kotlin
nav.set(NavigationKeys.STATION_PICKER_RESULT, selectedStation)
navController.popBackStack()
```

---

## Feature Navigation

```kotlin
sealed interface StationRoutes {
    @Serializable data object StationList      : StationRoutes
    @Serializable data class  StationDetails(val stationId: String) : StationRoutes
    @Serializable data object StationPicker    : StationRoutes
    @Serializable data object Contacts         : StationRoutes
    @Serializable data object PlanTrip         : StationRoutes
    @Serializable data object WaterMetroStations: StationRoutes
    @Serializable data object WaterMetroRoutes : StationRoutes
    @Serializable data object MetroRoutes      : StationRoutes
    @Serializable data object Parking          : StationRoutes
}

fun NavGraphBuilder.stationsGraph(
    navController: NavHostController,
    onStationPickerResult: (() -> Unit)? = null
)
```

`AppGraph.kt` bridges `Route.*` → `StationRoutes.*` via `LaunchedEffect` redirects.

**Adding a new screen:**
1. Add to `StationRoutes` sealed interface.
2. Add `composable<StationRoutes.NewScreen> { ... }` in `stationsGraph()`.
3. Add `Route.NewScreen` to `core/navigation/Routes.kt` (notify `navigation-agent`).
4. Add the `composable<Route.NewScreen>` redirect entry in `AppGraph.kt` (notify `app-agent`).
5. Register ViewModel in `StationModule.kt`.

---

## DI Module (exact)

```kotlin
val stationModule = module {
    singleOf(::StationRepositoryImpl) bind StationRepository::class
    singleOf(::StationListUseCase)
    singleOf(::WaterMetroStationsUseCase)
    singleOf(::WaterMetroRoutesUseCase)
    singleOf(::MetroRoutesUseCase)
    singleOf(::ParkingInfoUseCase)
    singleOf(::ContactsUseCase)
    viewModelOf(::StationViewModel)
    viewModelOf(::StationPickerViewModel)
    factoryOf(::TripRepositoryImpl) bind TripRepository::class
    factoryOf(::PlanTripUseCase)
    viewModelOf(::PlanTripViewModel)
    viewModelOf(::ParkingViewModel)
    viewModelOf(::WaterMetroStationsViewModel)
    viewModelOf(::WaterMetroRoutesViewModel)
    viewModelOf(::MetroRoutesViewModel)
    viewModelOf(::ContactsViewModel)
}
```

**Singleton vs factory:**
- `StationRepositoryImpl` + all use cases except `PlanTripUseCase` → `singleOf` (shared state, cached data).
- `TripRepositoryImpl` + `PlanTripUseCase` → `factoryOf` (per-trip, no shared state needed).
- ViewModels → `viewModelOf` (Koin-Compose lifecycle management).

---

## ViewModel Pattern for This Module

```kotlin
class FooViewModel(private val fooUseCase: FooUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            fooUseCase().collect { state ->
                when (state) {
                    is DomainState.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is DomainState.Success -> _uiState.update { it.copy(data = state.data, isLoading = false) }
                    is DomainState.Error   -> _uiState.update { it.copy(error = state.message, isLoading = false) }
                }
            }
        }
    }
}
```
