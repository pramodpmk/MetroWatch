# fare-agent — feature/fare

## Agent Identity

You are the **fare-agent**. You own everything under `feature/fare/src/`. Your responsibilities: metro fare calculator (station-to-station), water metro fare screen, and the general fares display.

---

## Owned Files

```
feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/
├── domain/
│   ├── FareDetails.kt               — result model: distance + fare
│   ├── GeneralFare.kt               — simple fare entry (route name + price)
│   ├── FareRepository.kt            — interface
│   └── CalculateFareUseCase.kt      — looks up distance then matches fare slab
├── data/
│   └── FareRepositoryImpl.kt        — queries ConfigDao
├── presentation/
│   ├── FareViewModel.kt             — state for both calculator and general fares
│   ├── GeneralFareUiState.kt        — UI state for general fares list
│   ├── CalculateFareScreen.kt       — station-to-station calculator UI
│   ├── CalculateFareRoute.kt        — route entry composable
│   ├── WaterMetroFareScreen.kt      — water metro fares UI
│   └── WaterMetroFareRoute.kt       — route entry composable
├── navigation/
│   └── FareNavigation.kt            — FareRoutes + fareGraph() extension
├── di/
│   └── FareModule.kt
└── Platform.kt
```

---

## Read Before Every Change

```
feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/presentation/FareViewModel.kt
feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/domain/FareRepository.kt
feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/domain/CalculateFareUseCase.kt
feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/di/FareModule.kt
feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/navigation/FareNavigation.kt
```

---

## Domain Layer

### FareDetails

```kotlin
data class FareDetails(
    val distanceKm: Double,
    val fare: Double
)
```

### FareRepository Interface

```kotlin
interface FareRepository {
    suspend fun getDistance(fromId: String, toId: String): Double?
    suspend fun getFareForDistance(distanceKm: Double): Double?
}
```

`FareRepositoryImpl`:
- `getDistance()` → `ConfigDao.getDistance(fromId, toId)` (handles bidirectionality).
- `getFareForDistance()` → `ConfigDao.getFareSlabs()` → lower-bound lookup: find the slab where `minKm <= distanceKm <= maxKm`.

### CalculateFareUseCase

Accepts `fromStationId` and `toStationId` as constructor parameters (injected by Koin per-call). Returns `Flow<DomainState<FareDetails>>`.

---

## FareViewModel UI State

```kotlin
data class FareUiState(
    val departureStation: String = "",
    val arrivalStation: String = "",
    val departureStationId: String = "",
    val arrivalStationId: String = "",
    val distance: String = "",
    val fare: String = "",
    val showDetails: Boolean = false,
    val isLoading: Boolean = false,
    val isPickingDeparture: Boolean = true   // true = next picker result goes to departure
)
```

`GeneralFareUiState` holds a list of `GeneralFare` entries with `isLoading`.

---

## Station Picker Integration

`FareViewModel` navigates to the station picker when either station field is tapped:

```kotlin
is FareIntent.SelectDeparture -> {
    _uiState.update { it.copy(isPickingDeparture = true) }
    _navigationEffect.emit(Route.StationPicker)
}
is FareIntent.SelectArrival -> {
    _uiState.update { it.copy(isPickingDeparture = false) }
    _navigationEffect.emit(Route.StationPicker)
}
```

The `CalculateFareScreen` composable listens for the result:

```kotlin
val nav = LocalNavigationResults.current
LaunchedEffect(nav.version) {
    val station = nav.consume<Station>(NavigationKeys.STATION_PICKER_RESULT) ?: return@LaunchedEffect
    viewModel.onIntent(
        if (uiState.isPickingDeparture) FareIntent.DepartureSelected(station)
        else FareIntent.ArrivalSelected(station)
    )
}
```

---

## Feature Navigation

```kotlin
sealed interface FareRoutes {
    @Serializable data object CalculateFare      : FareRoutes
    @Serializable data object WaterMetroFareRoute: FareRoutes
}

fun NavGraphBuilder.fareGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit
)
```

`AppGraph.kt` bridges:
- `Route.FareCalculation` → `FareRoutes.CalculateFare`
- `Route.WaterMetroFare` → `FareRoutes.WaterMetroFareRoute`

`onNavigate` is the callback for outbound navigation (e.g., to `Route.StationPicker`).

---

## DI Module (exact)

```kotlin
val fareModule = module {
    factoryOf(::FareRepositoryImpl) bind FareRepository::class
    factoryOf(::CalculateFareUseCase)
    viewModelOf(::FareViewModel)
}
```

All three are `factory` — fare calculations are stateless per-call. `viewModelOf` for Koin-Compose lifecycle.

---

## Adding a New Fare Screen

1. Add composable + ViewModel.
2. Add `FareRoutes.NewScreen` to `FareNavigation.kt`.
3. Add `composable<FareRoutes.NewScreen> { ... }` in `fareGraph()`.
4. If globally reachable: add `Route.NewFareScreen` to `core/navigation/Routes.kt` and a redirect in `AppGraph.kt`.
5. Register ViewModel in `FareModule.kt`.

---

## Test File

`feature/fare/src/androidHostTest/.../FareRepositoryTest.kt` — unit tests for fare calculation. Update when changing fare lookup logic.
