# settings-agent — feature/settings

## Agent Identity

You are the **settings-agent**. You own everything under `feature/settings/src/`. Your responsibilities: the settings screen UI and any user preference logic. This is a lightweight module — expand it when new app-level settings are added.

---

## Owned Files

```
feature/settings/src/commonMain/kotlin/com/metrowatch/kochi/settings/
├── presentation/
│   ├── SettingsViewModel.kt     — settings state management
│   └── SettingsScreen.kt        — settings composable
├── navigation/
│   └── SettingsNavigation.kt    — SettingsRoutes + settingsGraph() extension
└── di/
    └── SettingsModule.kt
```

---

## Read Before Every Change

```
feature/settings/src/commonMain/kotlin/com/metrowatch/kochi/settings/presentation/SettingsViewModel.kt
feature/settings/src/commonMain/kotlin/com/metrowatch/kochi/settings/presentation/SettingsScreen.kt
feature/settings/src/commonMain/kotlin/com/metrowatch/kochi/settings/di/SettingsModule.kt
feature/settings/src/commonMain/kotlin/com/metrowatch/kochi/settings/navigation/SettingsNavigation.kt
```

---

## ViewModel Pattern

```kotlin
class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
}
```

When adding a new setting:
1. Add the field to `SettingsUiState`.
2. Add the corresponding toggle/action to `SettingsViewModel`.
3. If the setting persists across sessions, inject a `DataStore` or `SharedPreferences` wrapper — add it to `SettingsModule.kt` as `single`.

---

## Feature Navigation

```kotlin
// SettingsNavigation.kt (infer pattern from other feature navigations)
sealed interface SettingsRoutes {
    @Serializable data object Settings : SettingsRoutes
}

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    composable<SettingsRoutes.Settings> {
        SettingsRoute(navController)
    }
}
```

The settings tab is reached from `BottomTab.SETTINGS` — no global `Route.*` dispatch needed for the root screen. Nested settings screens (if added) would need `Route.*` entries.

---

## DI Module (exact)

```kotlin
val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
```

Add new dependencies (data stores, repositories) here as `single` or `singleOf`.

---

## Adding a New Settings Entry

1. Add the state field to `SettingsUiState` data class.
2. Handle read/write in `SettingsViewModel`.
3. Add the UI row/toggle to `SettingsScreen.kt` using `BrandToolBar` + `Material3` components from `core/ui`.
4. If persistence is needed: create a `SettingsRepository` interface + impl, add to `SettingsModule.kt`.
