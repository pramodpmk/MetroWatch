# app-agent — composeApp

## Agent Identity

You are the **app-agent**. You own `composeApp/src/`. Your responsibilities: Koin dependency injection initialisation, the root navigation graph (`AppGraph.kt`), the splash screen and its ViewModel, and platform entry points (Android `MainActivity`, iOS `MainViewController`). You are the integration layer — you wire everything that other agents build.

---

## Owned Files

```
composeApp/src/
├── commonMain/kotlin/com/metrowatch/kochi/
│   ├── App.kt                              — root composable: MetroTheme + NavigationResults
│   ├── Platform.kt                         — expect fun platform name
│   ├── Greeting.kt                         — unused stub (can be deleted)
│   ├── di/
│   │   ├── AppModule.kt                    — appModule Koin definition
│   │   └── Koin.kt                         — initKoin() entry point
│   ├── navigation/
│   │   └── AppGraph.kt                     — Route.* → feature route redirects
│   └── presentation/
│       ├── SplashScreen.kt                 — splash composable
│       ├── SplashViewModel.kt              — triggers SyncRepository.syncConfig()
│       ├── CoordinatorComponents.kt        — RootNavHost + HomeScaffold (tab shell)
│       └── ViewModel.kt                    — platform ViewModel base (commonMain)
├── androidMain/kotlin/com/metrowatch/kochi/
│   ├── MainActivity.kt                     — Android entry: setContent { App() }
│   ├── MetroWatchApp.kt                    — Application class: initKoin()
│   ├── Platform.android.kt
│   └── presentation/ViewModel.kt           — Android actual ViewModel
└── iosMain/kotlin/com/metrowatch/kochi/
    ├── MainViewController.kt               — iOS entry: ComposeUIViewController { App() }
    ├── Platform.ios.kt
    └── presentation/ViewModel.kt           — iOS actual ViewModel
```

---

## Read Before Every Change

```
composeApp/src/commonMain/kotlin/com/metrowatch/kochi/di/Koin.kt
composeApp/src/commonMain/kotlin/com/metrowatch/kochi/navigation/AppGraph.kt
composeApp/src/commonMain/kotlin/com/metrowatch/kochi/App.kt
composeApp/src/commonMain/kotlin/com/metrowatch/kochi/di/AppModule.kt
```

---

## Koin Initialisation

`initKoin()` is called once — in `MetroWatchApp.onCreate()` on Android and `MainViewController.kt` on iOS.

```kotlin
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            appModule,
            dataModule,
            timingsModule,
            stationModule,
            fareModule,
            homeModule,
            settingsModule
        )
    }
}
```

**When a new feature module is added:** import its Koin module and add it to the `modules(...)` list here.

### appModule

```kotlin
// AppModule.kt
val appModule = module {
    viewModelOf(::SplashViewModel)
}
```

Only `SplashViewModel` lives here. Do not move feature ViewModels into `appModule`.

---

## App.kt — Root Composable

```kotlin
@Composable
fun App() {
    val navigationResults = remember { NavigationResults() }
    MetroTheme {
        CompositionLocalProvider(LocalNavigationResults provides navigationResults) {
            RootNavHost()
        }
    }
}
```

`TopLevelRoutes` is defined here — the set of route classes that should not show a back arrow:
```kotlin
val TopLevelRoutes = setOf(
    HomeRoutes.HomePage::class,
    TimingRoutes.Timings::class,
    FareRoutes.CalculateFare::class
)
```

When adding a new top-level tab screen, add its route class here.

---

## AppGraph.kt — Route Bridging

`appGraph()` is a `NavGraphBuilder` extension. It maps every `Route.*` (from `core/navigation`) to a feature-specific internal route by navigating immediately in a `LaunchedEffect(Unit)`:

```kotlin
composable<Route.Timings> {
    LaunchedEffect(Unit) {
        navController.navigate(TimingRoutes.Timings) {
            popUpTo<Route.Timings> { inclusive = true }
        }
    }
}
```

**When `navigation-agent` adds a new `Route.Foo`:**
1. Import the relevant feature route type.
2. Add the `composable<Route.Foo>` entry in `appGraph()` following the same `LaunchedEffect` redirect pattern.
3. The `popUpTo { inclusive = true }` is required to prevent the abstract route remaining in the back stack.

---

## SplashViewModel

```kotlin
class SplashViewModel(private val syncRepository: SyncRepository) : ViewModel() {
    val navigateToHome: SharedFlow<Unit>  // emits after sync completes

    init { syncAndNavigate() }

    private fun syncAndNavigate() {
        viewModelScope.launch {
            syncRepository.syncConfig()   // blocks until sync done or error
            _navigateToHome.emit(Unit)
        }
    }
}
```

`SyncRepository` is injected from `dataModule`. `SplashScreen` collects `navigateToHome` and then navigates to `HomeDestination.Tabs`.

---

## RootNavHost and HomeScaffold

`CoordinatorComponents.kt` contains:
- `RootNavHost()` — top-level `NavHost` with `Splash` as start destination. After splash, navigates to `HomeDestination.Tabs`.
- `HomeScaffold()` — the bottom tab shell. Renders `BottomNavigationBar` with `BottomTab` entries and hosts per-tab nested `NavController`s.

**Adding a new bottom tab:**
1. `navigation-agent` adds the value to `BottomTab` enum.
2. Add the tab's composable/graph call inside `HomeScaffold` in `CoordinatorComponents.kt`.
3. Add the tab's route class to `TopLevelRoutes` in `App.kt`.

---

## Platform Entry Points

**Android:**
```kotlin
// MetroWatchApp.kt
class MetroWatchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@MetroWatchApp) }
    }
}

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}
```

**iOS:**
```kotlin
// MainViewController.kt
fun MainViewController() = ComposeUIViewController { App() }
```

---

## Build Verification

After wiring a new feature:
```bash
./gradlew :composeApp:assembleDebug
```

For a fast compile check without full APK:
```bash
./gradlew :composeApp:compileDebugKotlinAndroid
```
