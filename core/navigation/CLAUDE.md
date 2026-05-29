# navigation-agent — core/navigation

## Agent Identity

You are the **navigation-agent**. You own `core/navigation/src/`. Your job is the shared navigation contract: the `Route` sealed interface (all global screen addresses), `BottomTab` enum, and the `NavigationResults` mechanism for passing data between screens without `savedStateHandle`.

You define where screens *can* be navigated to. The `composeApp/AppGraph.kt` (owned by `app-agent`) wires these abstract routes to feature-specific composables.

---

## Owned Files

```
core/navigation/src/commonMain/kotlin/com/metrowatch/kochi/navigation/
├── Routes.kt                         — sealed interface Route (all global routes)
├── BottomTab.kt                      — enum for bottom navigation tabs
├── NavigationResults.kt              — NavigationResults class (key/value store)
├── NavigationResultHandler.kt        — CompositionLocal provider
├── StationResult.kt                  — data class for station picker result
└── result/NavigationKeys.kt          — string constants for navigation result keys
```

---

## Read Before Every Change

```
core/navigation/src/commonMain/kotlin/com/metrowatch/kochi/navigation/Routes.kt
core/navigation/src/commonMain/kotlin/com/metrowatch/kochi/navigation/BottomTab.kt
core/navigation/src/commonMain/kotlin/com/metrowatch/kochi/navigation/NavigationResults.kt
```

---

## Route Sealed Interface

```kotlin
@Serializable
sealed interface Route {
    // Graph containers (one per feature nav graph)
    @Serializable data object HomeGraph    : Route
    @Serializable data object StationGraph : Route
    @Serializable data object TimingsGraph : Route
    @Serializable data object FareGraph    : Route
    @Serializable data object SettingsGraph: Route

    // Screen destinations
    @Serializable data object StationList      : Route
    @Serializable data class  StationDetail(val stationId: String) : Route
    @Serializable data object StationPicker    : Route
    @Serializable data object FareCalculation  : Route
    @Serializable data object Timings          : Route
    @Serializable data object WaterMetroTimings: Route
    @Serializable data object PlanTrip         : Route
    @Serializable data object WaterMetroStations: Route
    @Serializable data object WaterMetroRoutes : Route
    @Serializable data object Parking          : Route
    @Serializable data object MetroRoutes      : Route
    @Serializable data object WaterMetroFare   : Route
    @Serializable data object Splash           : Route
    @Serializable data object Home             : Route
    @Serializable data object Contacts         : Route
    @Serializable data class  WebView(val title: String, val url: String) : Route
}
```

Also defined in `Routes.kt`:

```kotlin
@Serializable
sealed interface HomeDestination : Route {
    @Serializable data object Home : HomeDestination
    @Serializable data object Tabs : HomeDestination
}
```

### Adding a New Route

1. Add the entry to `Routes.kt` with `@Serializable` annotation.
2. Use `data object` for parameter-free routes; `data class` for routes carrying data.
3. Notify `app-agent` to add a `composable<Route.NewFoo> { ... }` entry in `AppGraph.kt`.
4. Notify the relevant feature-agent to add the internal feature route to its own `XxxRoutes` sealed interface and navigation graph extension function.

### Rules

- Every `Route` entry must have `@Serializable` — the navigation library uses kotlinx serialization for type-safe routing.
- Do not put composables or ViewModels here. Routes are pure data.
- Graph container routes (`XxxGraph`) are used as nav graph start destinations only.

---

## BottomTab

```kotlin
enum class BottomTab(val label: String) {
    HOME("Home"),
    CONTACTS("Contacts"),
    SETTINGS("Settings")
}
```

Add a new value here only when adding a new bottom-navigation tab. Coordinate with `app-agent` to update `HomeScaffold`.

---

## NavigationResults

Used to pass values between screens (e.g., station picker → fare calculator) in a KMP-safe way.

```kotlin
class NavigationResults {
    fun <T> set(key: String, value: T)      // called by picker/sender screen
    fun <T> consume(key: String): T?         // called by receiver screen (removes the value)
    fun clearResult(key: String)
    val version: Int                         // state-backed; increments on every set()
}
```

`NavigationResults` is provided via `LocalNavigationResults` composition local in `App.kt`. Feature composables access it via `val nav = LocalNavigationResults.current`.

### Station Picker Key

The station picker uses key `"station_picker_result"` defined in `NavigationKeys.kt`. The receiver calls `nav.consume<Station>("station_picker_result")` inside a `LaunchedEffect(nav.version)`.

### Adding a New Result Key

Add a constant to `NavigationKeys.kt`:
```kotlin
object NavigationKeys {
    const val STATION_PICKER_RESULT = "station_picker_result"
    const val MY_NEW_KEY = "my_new_key"
}
```

---

## No DI Registration

This module has no Koin bindings. `NavigationResults` is instantiated with `remember { NavigationResults() }` in `App.kt`.

---

## Handoff Rules

- After adding any `Route.*` entry, always notify `app-agent` to wire it in `AppGraph.kt`.
- After adding a `NavigationKeys` constant, notify the feature-agents on both the sender and receiver side.
