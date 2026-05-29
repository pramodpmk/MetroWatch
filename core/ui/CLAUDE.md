# ui-agent — core/ui

## Agent Identity

You are the **ui-agent**. You own `core/ui/src/`. Your job is the shared design system: the brand theme, colour tokens, typography, and reusable composables that all feature modules import. You do not own any business logic, ViewModels, or feature-specific screens.

---

## Owned Files

```
core/ui/src/commonMain/kotlin/com/metrowatch/kochi/ui/
├── theme/
│   ├── Color.kt               — brand colour tokens
│   ├── Type.kt                — typography scale
│   └── Theme.kt               — MetroTheme composable (MaterialTheme wrapper)
├── components/
│   ├── BrandToolBar.kt        — canonical app bar for all screens
│   ├── TextComponents.kt      — DisplayText and other text wrappers
│   ├── GroupsAndLists.kt      — RowGrid, grouped list composables
│   ├── PageStructureComposable.kt — full-page layout scaffolds
│   ├── WebView.kt             — expect fun WebViewComposable(url)
│   └── WebViewScreen.kt       — full-screen WebView wrapper
├── AppLogger.kt               — logging utility (println wrapper)
└── Platform.kt                — expect fun getLocalTime(): Pair<Int,Int>

core/ui/src/androidMain/kotlin/com/metrowatch/kochi/ui/
├── components/WebView.android.kt  — actual WebView (Android WebView)
└── Platform.android.kt            — actual getLocalTime()

core/ui/src/iosMain/kotlin/com/metrowatch/kochi/ui/
├── components/WebView.ios.kt      — actual WebView (WKWebView via UIKitView)
└── Platform.ios.kt                — actual getLocalTime()
```

---

## Read Before Every Change

```
core/ui/src/commonMain/kotlin/com/metrowatch/kochi/ui/theme/Color.kt
core/ui/src/commonMain/kotlin/com/metrowatch/kochi/ui/theme/Theme.kt
core/ui/src/commonMain/kotlin/com/metrowatch/kochi/ui/components/BrandToolBar.kt
core/ui/src/commonMain/kotlin/com/metrowatch/kochi/ui/components/TextComponents.kt
```

---

## Brand Colour Tokens

```kotlin
val BrandBlue   = Color(0xFF039076)   // primary brand teal-green — toolbars, backgrounds
val LightBlueBg = Color(0xFFE8F0FF)   // light page backgrounds
val NearestStationLabelColor = Color(0xFF6200EE) // accent purple
val NextTrainGreen = Color(0xFF00C853) // status green
val BrandWhite  = Color(0xFFFFFFFF)
val BrandGray   = Color(0xFF9E9E9E)
```

**Rules:**
- Never hardcode `Color(0xFF...)` literals inside feature modules. Import from this file.
- Do not rename existing tokens — they are referenced across all feature modules.
- When adding a new colour, add it here with a semantic name, then use it in the feature.

---

## BrandToolBar

The canonical top app bar for every screen. Always use this — never create a one-off toolbar in a feature module.

```kotlin
@Composable
fun BrandToolBar(
    title: String,
    navigationIcon: ImageVector? = null,    // back arrow etc.
    onNavigationClick: () -> Unit = {},
    trailingIcon: ImageVector? = null,       // optional action icon
    onTrailingClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}  // expandable slot below title row
)
```

Background is `BrandBlue`, title and icons are `BrandWhite`, handles `WindowInsets.statusBars` padding.

---

## TextComponents

`DisplayText` is the standard text composable. Use it instead of raw `Text` for consistent styling:

```kotlin
@Composable
fun DisplayText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyMedium
)
```

---

## getLocalTime

`expect fun getLocalTime(): Pair<Int, Int>` — returns `(hour24, minute)` using the device clock. Used in `HomeViewModel` to calculate next train time. Platform actuals are in `Platform.android.kt` / `Platform.ios.kt`.

---

## Adding a New Shared Composable

1. If the composable is purely in Compose (no platform API), add it to the appropriate file in `components/` or create a new file there.
2. If the composable requires platform-specific implementation, create:
   - `components/Foo.kt` with `expect @Composable fun Foo(...)`
   - `components/Foo.android.kt` with `actual @Composable fun Foo(...)`
   - `components/Foo.ios.kt` with `actual @Composable fun Foo(...)`
3. Never import `android.*` or UIKit types in `commonMain`.

---

## WebView

The WebView composable is already abstracted:
- `WebView.kt` — `expect @Composable fun WebViewComposable(url: String, modifier: Modifier)`
- `WebViewScreen.kt` — full-screen route wrapper with `BrandToolBar` and back navigation
- Platform actuals in `androidMain` and `iosMain`

Route: `Route.WebView(title, url)` in `core/navigation`; wired in `AppGraph.kt`.

---

## MetroTheme

`MetroTheme` wraps `MaterialTheme`. It is light-only (no dark mode). Do not add dark mode variants unless explicitly requested.

```kotlin
@Composable
fun MetroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(...),
        typography = MetroTypography,
        content = content
    )
}
```

---

## No DI Registration

This module has no Koin bindings.

---

## Handoff Rules

- When you add or change a shared composable signature, notify all feature agents that use it (check with `grep -r "BrandToolBar\|DisplayText"` across feature modules).
- When you change `getLocalTime()`, notify `home-agent` (the only current consumer).
