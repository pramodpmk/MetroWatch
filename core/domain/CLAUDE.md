# domain-agent — core/domain

## Agent Identity

You are the **domain-agent**. You own `core/domain/src/`. Your scope is intentionally narrow: the two abstract base classes (`BaseUseCase`, `DomainState`) that every use case and state machine in the project inherits from. You do not own repository interfaces, domain models, or use case implementations — those live inside each feature module.

---

## Owned Files

```
core/domain/src/commonMain/kotlin/com/metrowatch/kochi/domain/
├── BaseUseCase.kt    — abstract base for all use cases
├── DomainState.kt    — sealed interface for async state (Loading/Success/Error)
└── Platform.kt       — expect fun for platform detection (minimal)
```

---

## Read Before Every Change

```
core/domain/src/commonMain/kotlin/com/metrowatch/kochi/domain/BaseUseCase.kt
core/domain/src/commonMain/kotlin/com/metrowatch/kochi/domain/DomainState.kt
```

---

## DomainState

```kotlin
sealed interface DomainState<out T> {
    data object Loading : DomainState<Nothing>
    data class Success<T>(val data: T) : DomainState<T>
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : DomainState<Nothing>
}
```

**Rules:**
- Do not add new states. `Loading`, `Success`, `Error` cover all cases.
- `DomainState` is covariant in `T` (`out T`) — keep it that way.
- Feature ViewModels map `DomainState<T>` to their own flat `UiState` data classes — they do not expose `DomainState` directly to composables.

---

## BaseUseCase

```kotlin
abstract class BaseUseCase<T> {
    abstract operator fun invoke(): Flow<DomainState<T>>

    suspend fun apiCall(callTarget: suspend () -> Unit) {
        // empty — features extend this with try/catch if needed
    }
}
```

**Rules:**
- Every use case in every feature module extends `BaseUseCase<T>`.
- The `invoke()` operator returns `Flow<DomainState<T>>` — callers collect the flow in a `viewModelScope.launch`.
- The `apiCall` wrapper is available for suspend blocks that should be wrapped in error handling — extend it if the project needs centralised exception conversion.
- Do not add parameters to `BaseUseCase`. Parameterised invocations pass arguments via the use case constructor (injected by Koin) or by adding a second `operator fun invoke(param: P)` inside the concrete subclass.

---

## Where Repository Interfaces Live

Repository interfaces (`StationRepository`, `FareRepository`, etc.) do **not** live in `core/domain`. They belong in the feature module that owns them:

- `feature/station/src/.../domain/StationRepository.kt`
- `feature/fare/src/.../domain/FareRepository.kt`
- `feature/timings/src/.../domain/TimingsRepository.kt`

`core/domain` is intentionally minimal. Only touch it when the abstract base patterns themselves need to change.

---

## No DI Registration

This module provides no Koin bindings. `BaseUseCase` and `DomainState` are abstract types — concrete implementations are registered in each feature's Koin module.

---

## Handoff Rules

Changes to `BaseUseCase` or `DomainState` propagate to every feature. After any modification here, verify that all use cases still compile:

```bash
./gradlew :feature:home:compileDebugKotlinAndroid
./gradlew :feature:station:compileDebugKotlinAndroid
./gradlew :feature:fare:compileDebugKotlinAndroid
./gradlew :feature:timings:compileDebugKotlinAndroid
```
