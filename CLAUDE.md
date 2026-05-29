# CLAUDE.md — MetroWatch Solution Architect

## Architect Role

This file governs the agentic workflow for MetroWatch. When a user prompt arrives:

1. **Classify** the request by affected layer(s) using the Module Registry below.
2. **Spawn scoped agents** for each affected module — run independent modules in parallel.
3. **Sequence** agents with cross-module dependencies (domain → data → navigation → feature → composeApp).
4. **Verify** integration after all agents complete: compile with `./gradlew :composeApp:assembleDebug`.

Never implement directly when a scoped agent exists. Route all work through the registered agents.

---

## Module Registry

### `data-agent` — core/data

**Scope:** `core/data/src/`
**Triggers:** new Room entities/DAOs, API DTOs, database migrations, sync logic, Ktor client changes.

**Before starting:** Read
- `core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/AppDatabase.kt`
- `core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/Entities.kt`
- `core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/ConfigModels.kt`
- `core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/di/DataModule.kt`

**Patterns to enforce:**
- All entities annotated with `@Entity(tableName = "snake_case_name")` in `Entities.kt`.
- All DAO queries in `ConfigDao` — bulk insert/delete inside a `@Transaction` when atomically replacing config.
- Database version increment required when adding/altering tables; add `Migration` object.
- API DTOs go in `ConfigModels.kt` annotated with `@Serializable`; use `@SerialName` for any key that differs from field name.
- Ktor `HttpClient` is configured once in `DataModule`; inject `HttpClient` — never create a new one.
- `SyncRepository` calls `ConfigApi.getVersion()` → compare with `ConfigDao.getVersion()` → fetch full config if changed → call `ConfigDao.updateConfig()` in one transaction.

**DI registration (`DataModule.kt`):**
```kotlin
single { getDatabase(get()) }
single { get<AppDatabase>().configDao() }
single { get<AppDatabase>().stationDao() }
singleOf(::SyncRepository)
```

**Existing Room entities:**
`stations`, `distances`, `fare_slabs`, `timetables`, `config_version`,
`water_metro_routes`, `water_metro_stations`,
`parking_rates`, `parking_passes`, `parking_info`, `contacts`

**Handoff:** After DAO/entity changes, notify `domain-agent` of new query method signatures and `feature-agent`(s) that consume the DAO.

---

### `domain-agent` — core/domain

**Scope:** `core/domain/src/`
**Triggers:** new base patterns, changes to `DomainState`, new repository interfaces that features will implement.

**Before starting:** Read
- `core/domain/src/commonMain/kotlin/com/metrowatch/kochi/domain/DomainState.kt`
- `core/domain/src/commonMain/kotlin/com/metrowatch/kochi/domain/BaseUseCase.kt`

**Patterns to enforce:**
- `DomainState<T>` is a sealed interface: `Loading`, `Success(data: T)`, `Error(message, throwable?)` — do not add states.
- Every use case extends `BaseUseCase` and returns `Flow<DomainState<T>>`.
- Repository interfaces belong in the feature module that owns them (e.g., `StationRepository` lives in `feature/station`), not in `core/domain`. `core/domain` holds only the abstract base classes.
- `BaseUseCase.apiCall {}` wraps suspend blocks and converts exceptions to `DomainState.Error`.

**No DI registration** — this module has no Koin bindings.

---

### `navigation-agent` — core/navigation

**Scope:** `core/navigation/src/`
**Triggers:** new screens requiring a route, changes to navigation result keys, new bottom tabs.

**Before starting:** Read
- `core/navigation/src/commonMain/kotlin/com/metrowatch/kochi/navigation/Routes.kt`
- `core/navigation/src/commonMain/kotlin/com/metrowatch/kochi/navigation/NavigationResults.kt`

**Patterns to enforce:**
- All routes are nested inside `sealed interface Route` in `Routes.kt` and annotated with `@Serializable`.
- Data-carrying routes use `data class Route.FooDetail(val id: String) : Route`.
- Static routes use `data object Route.Foo : Route`.
- Graph container routes (`HomeGraph`, `StationGraph`, etc.) are `data object`.
- Navigation results (picker → caller) use `LocalNavigationResults` with a string key defined in `NavigationKeys.kt`.
- `BottomTab` enum in `BottomTab.kt` — only add a tab here if a new bottom-nav entry is needed.

**No DI registration.**

**Handoff:** After adding a `Route`, notify `app-agent` to wire it in `AppGraph.kt`.

---

### `ui-agent` — core/ui

**Scope:** `core/ui/src/`
**Triggers:** new shared composables, theme colour/typography changes, new platform-specific UI utilities.

**Before starting:** Read
- `core/ui/src/commonMain/kotlin/com/metrowatch/kochi/ui/theme/Theme.kt`
- `core/ui/src/commonMain/kotlin/com/metrowatch/kochi/ui/components/` (scan directory)

**Patterns to enforce:**
- Brand colour is `BrandBlue = Color(0xFF039076)` (teal-green). Never hardcode colour values in feature modules — reference the colour tokens from `core/ui/theme/Color.kt`.
- `BrandToolBar` is the canonical app bar — reuse it, do not create a one-off toolbar in a feature screen.
- New shared composables go in `components/`; platform-specific ones use `expect/actual` with `.android.kt` / `.ios.kt` siblings.
- Typography uses `MaterialTheme.typography` entries mapped in `Type.kt`.

**No DI registration.**

---

### `home-agent` — feature/home

**Scope:** `feature/home/src/`
**Triggers:** home screen UI changes, nearest-station logic, location permission flow, home action grid.

**Before starting:** Read
- `feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/HomeViewModel.kt`
- `feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/HomePageIntent.kt`
- `feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/HomePageUi.kt`
- `feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/HomeScreen.kt`
- `feature/home/src/commonMain/kotlin/com/metrowatch/kochi/home/di/HomeModule.kt`

**Patterns to enforce:**
- MVI: user actions modelled as `HomePageIntent` sealed interface; `HomeViewModel.onIntent(intent)` is the single entry point.
- Navigation side effects emitted on `homeNavigationEffect: SharedFlow<Route>`.
- Location permission: `expect fun rememberLocationPermissionLauncher(onLocation, onDenied): () -> Unit` — platform actuals in `androidMain` / `iosMain`.
- Haversine distance uses only `kotlin.math.*` — safe in commonMain.
- Time parsing uses 12-hour format string; format output as `"HH:mm"` or `"Tomorrow"`.
- Do not use `String.format` in commonMain — use `(value * 10).roundToInt()` pattern for 1-decimal numbers.

**DI registration (`HomeModule.kt`):**
```kotlin
viewModelOf(::HomeViewModel)
```

**iOS permission:** `NSLocationWhenInUseUsageDescription` must be present in `iosApp/iosApp/Info.plist`.
**Android permission:** `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION` in `composeApp/src/androidMain/AndroidManifest.xml`.

---

### `station-agent` — feature/station

**Scope:** `feature/station/src/`
**Triggers:** station list/detail, water metro stations/routes, parking, metro routes, contacts, station picker, trip planning.

**Before starting:** Read
- `feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/di/StationModule.kt`
- `feature/station/src/commonMain/kotlin/com/metrowatch/kochi/station/domain/StationRepository.kt`
- Relevant sub-package for the specific task (list/, detail/, watermetro/, parking/, etc.)

**Sub-packages and their owners:**
| Sub-package | Screen | ViewModel |
|---|---|---|
| `list/` | `StationListScreen` | `StationViewModel` |
| `detail/` | station detail composable | (inline or shared VM) |
| `picker/` | `StationPickerScreen` | `StationPickerViewModel` |
| `watermetro/` | `WaterMetroStationsScreen`, `WaterMetroRoutesScreen` | `WaterMetroStationsViewModel`, `WaterMetroRoutesViewModel` |
| `metroroutes/` | `MetroRoutesScreen` | `MetroRoutesViewModel` |
| `parking/` | `ParkingScreen` | `ParkingViewModel` |
| `plantrip/` | `PlanTripScreen` | `PlanTripViewModel` |
| `contact/` | `ContactsScreen` | `ContactsViewModel` |

**Patterns to enforce:**
- `StationRepository` (interface) / `StationRepositoryImpl` (impl) — singleton in Koin.
- `TripRepository` / `TripRepositoryImpl` — factory (scoped per use).
- Station search filters across `nameEn`, `nameMl`, `nameHi` — case-insensitive.
- Station picker result is passed via `LocalNavigationResults` key `"station_picker_result"`.
- `PlanTripUseCase` computes: departure → arrival path, summed distances, fare from slabs, estimated time.

**DI registration (`StationModule.kt`):**
```kotlin
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
```

---

### `timings-agent` — feature/timings

**Scope:** `feature/timings/src/`
**Triggers:** train timing table, timing detail calculation, water metro timetable.

**Before starting:** Read
- `feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/domain/TimingsRepository.kt`
- `feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/domain/CalculateTimingsUseCase.kt`
- `feature/timings/src/commonMain/kotlin/com/metrowatch/kochi/timings/di/TimingsModule.kt`

**Patterns to enforce:**
- `TimingsRepository` / `TimingsRepositoryImpl` — factory (not singleton).
- `CalculateTimingsUseCase` — factory; accepts departure + arrival station IDs, returns `Flow<DomainState<TrainTiming>>`.
- Timing calculation: distance between stations / 35 km/h average speed → minutes → format `"HH:mm"`.
- `TimingDetailScreen` shows per-station arrival times across the route.

**DI registration (`TimingsModule.kt`):**
```kotlin
factoryOf(::TimingsRepositoryImpl) bind TimingsRepository::class
factoryOf(::CalculateTimingsUseCase)
viewModelOf(::TimingTableViewModel)
viewModelOf(::TimingDetailViewModel)
```

---

### `fare-agent` — feature/fare

**Scope:** `feature/fare/src/`
**Triggers:** fare calculator, general fares display, water metro fares.

**Before starting:** Read
- `feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/domain/FareRepository.kt`
- `feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/domain/CalculateFareUseCase.kt`
- `feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/presentation/FareViewModel.kt`
- `feature/fare/src/commonMain/kotlin/com/metrowatch/kochi/fare/di/FareModule.kt`

**Patterns to enforce:**
- `FareRepository` / `FareRepositoryImpl` — factory.
- Fare = sum of inter-station distances matched against `FareSlabEntity` (lower-bound lookup).
- `FareUiState` fields: `departureStation`, `arrivalStation`, `distance`, `fare`, `showDetails`, `isLoading`, `isPickingDeparture`.
- Station selection uses `StationPickerScreen` via `Route.StationPicker`; result retrieved from `LocalNavigationResults`.
- `WaterMetroFareScreen` is a separate screen under the same Koin module.

**DI registration (`FareModule.kt`):**
```kotlin
factoryOf(::FareRepositoryImpl) bind FareRepository::class
factoryOf(::CalculateFareUseCase)
viewModelOf(::FareViewModel)
```

---

### `settings-agent` — feature/settings

**Scope:** `feature/settings/src/`
**Triggers:** settings screen UI, user preferences, app configuration options.

**Before starting:** Read
- `feature/settings/src/commonMain/kotlin/com/metrowatch/kochi/settings/SettingsViewModel.kt`
- `feature/settings/src/commonMain/kotlin/com/metrowatch/kochi/settings/SettingsScreen.kt`
- `feature/settings/src/commonMain/kotlin/com/metrowatch/kochi/settings/di/SettingsModule.kt`

**DI registration (`SettingsModule.kt`):**
```kotlin
viewModelOf(::SettingsViewModel)
```

---

### `app-agent` — composeApp

**Scope:** `composeApp/src/`
**Triggers:** Koin init wiring, `AppGraph.kt` route registration, splash screen, platform entry points.

**Before starting:** Read
- `composeApp/src/commonMain/kotlin/com/metrowatch/kochi/AppGraph.kt`
- `composeApp/src/commonMain/kotlin/com/metrowatch/kochi/App.kt`
- `composeApp/src/commonMain/kotlin/com/metrowatch/kochi/di/Koin.kt`

**Patterns to enforce:**
- `initKoin()` in `Koin.kt` is the single Koin startup point. Add new feature Koin modules here.
- Route → composable mapping lives in `AppGraph.kt`. Every `Route.*` object added by `navigation-agent` must get a `composable<Route.Foo> { ... }` entry here.
- `App.kt` provides `MetroTheme` + `CompositionLocalProvider(LocalNavigationResults ...)`.
- Android entry: `MainActivity` calls `setContent { App() }`.
- iOS entry: `MainViewController.kt` exports `ComposeUIViewController { App() }`.
- `SplashViewModel` (registered in `appModule`) handles initial data sync via `SyncRepository`.

**DI registration (`appModule` in `Koin.kt`):**
```kotlin
viewModelOf(::SplashViewModel)
```

---

## Cross-Module Coordination Workflows

### Workflow: Add a new full-screen feature

Spawn agents in this order (sequential where arrow, parallel where `||`):

```
domain-agent        → define repository interface + domain model
       ↓
data-agent          → add entity + DAO queries + DI binding
  ||
navigation-agent    → add Route.NewFeature to Routes.kt
       ↓
feature/<x>-agent   → implement ViewModel + UiState + Intent + Screen + Koin module
       ↓
app-agent           → register Koin module in initKoin() + add composable in AppGraph.kt
```

**Verification after completion:** `./gradlew :composeApp:assembleDebug`

---

### Workflow: Add a new entity / data field

```
data-agent          → add entity field + update DAO + increment DB version + add Migration
       ↓
feature/<x>-agent   → update UiState + ViewModel to surface the new field
       ↓
ui-agent            → update Screen composable if new field needs rendering
```

---

### Workflow: New shared UI component

```
ui-agent            → implement composable in core/ui/components/
       ↓
feature/<x>-agent   → consume the new composable in the target screen
```

---

### Workflow: New bottom tab

```
navigation-agent    → add value to BottomTab enum + new graph Route
       ↓
feature/<x>-agent   → create feature module with screen + ViewModel + Koin module
       ↓
app-agent           → add tab to HomeScaffold + register in AppGraph + initKoin
```

---

### Workflow: Remote API / sync change

```
data-agent          → update ConfigModels DTO + ConfigDao.updateConfig() + SyncRepository
       ↓
feature/<x>-agent   → update use case + ViewModel to consume new fields
```

---

### Workflow: Bug fix (single module)

Spawn only the agent that owns the file. No coordination needed unless the fix crosses module boundaries.

---

## Build Commands

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Install on connected Android device
./gradlew :composeApp:installDebug

# Compile single module (fast check)
./gradlew :feature:home:compileDebugKotlinAndroid
./gradlew :core:data:compileDebugKotlinAndroid

# Run all tests
./gradlew test

# Run module-specific tests
./gradlew :core:data:test
./gradlew :feature:station:test

# iOS — open iosApp/ in Xcode, then Run
```

---

## Technical Reference

### Package Conventions

| Module | Package root |
|---|---|
| composeApp | `com.metrowatch.kochi` |
| core/data | `com.metrowatch.kochi.data` |
| core/domain | `com.metrowatch.kochi.domain` |
| core/navigation | `com.metrowatch.kochi.navigation` |
| core/ui | `com.metrowatch.kochi.ui` |
| feature/home | `com.metrowatch.kochi.home` |
| feature/station | `com.metrowatch.kochi.station` |
| feature/timings | `com.metrowatch.kochi.timings` |
| feature/fare | `com.metrowatch.kochi.fare` |
| feature/settings | `com.metrowatch.kochi.settings` |

### Key Dependencies (libs.versions.toml)

| Library | Version |
|---|---|
| Kotlin | 2.1.0 |
| Compose Multiplatform | 1.7.3 |
| AGP | 8.13.2 |
| AndroidX Room (KMP) | 2.7.0-alpha11 |
| AndroidX SQLite | 2.5.0-alpha11 |
| Ktor | 2.3.12 |
| Koin | 3.5.6 |
| Koin Compose Multiplatform | 1.2.0-Beta4 |
| Kotlinx Coroutines | 1.8.1 |
| Kotlinx Serialization | 1.7.3 |
| KSP | 2.1.0-1.0.29 |
| Coil | 3.0.0-alpha06 |
| Play Services Location | 21.3.0 |
| compileSdk | 36 |
| minSdk | 24 |

### ViewModel Pattern

```kotlin
class FooViewModel(dep: FooDep) : ViewModel() {
    private val _uiState = MutableStateFlow(FooUiState())
    val uiState: StateFlow<FooUiState> = _uiState.asStateFlow()

    private val _navigationEffect = MutableSharedFlow<Route>()
    val navigationEffect: SharedFlow<Route> = _navigationEffect.asSharedFlow()

    fun onIntent(intent: FooIntent) { /* process in viewModelScope */ }
}
```

### Use Case Pattern

```kotlin
class FooUseCase(private val repo: FooRepository) : BaseUseCase<FooResult>() {
    override fun invoke(): Flow<DomainState<FooResult>> = flow {
        emit(DomainState.Loading)
        apiCall { /* suspend call */ }
        emit(DomainState.Success(result))
    }
}
```

### Expect/Actual Pattern for Platform Code

Create three files per platform concern:
- `commonMain/.../Foo.kt` — `expect fun foo(): Bar`
- `androidMain/.../Foo.android.kt` — `actual fun foo(): Bar { /* Android impl */ }`
- `iosMain/.../Foo.ios.kt` — `actual fun foo(): Bar { /* iOS impl */ }`

### Remote API

- **Base URL:** `https://bray5sxxd3.execute-api.ap-south-1.amazonaws.com`
- `GET /config/version` → `VersionResponse`
- `GET /config` → `ConfigResponse` (full configuration blob)

### Room Database

`AppDatabase` version is tracked in `core/data/src/commonMain`. Increment version + provide `Migration` whenever a table is added or altered. Schema exports go to `core/data/schemas/`.

### CommonMain Safety Rules

- No `String.format` — use `(value * 10).roundToInt()` for decimals.
- No `java.*` imports.
- No platform-specific APIs — wrap them behind `expect/actual`.
- Haversine and all pure math calculations belong in `commonMain`.
