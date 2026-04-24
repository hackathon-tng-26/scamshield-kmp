# CLAUDE.md

This file provides guidance to AI assistants (Claude Code, Cursor, etc.) when working in this repository.

## Critical Rules - READ FIRST

1. **NEVER COMMIT** unless the user explicitly says "commit", "commit the code", or similar.
2. **NEVER PUSH** unless the user explicitly requests it.
3. **NO COMMENTS** in code (strict). See the Code Style section below.
4. **STAY IN SCOPE** — this is a hackathon build. 5 screens. No profile, no history, no top-up, no QR pay, no onboarding. If in doubt: don't add it.

## Project Overview

Kotlin Multiplatform (KMP) mobile client for **ScamShield** — an AI-powered anti-scam layer on top of a TNG-like e-wallet transfer flow. Built for the TNG Digital FinHack 2026 hackathon (25–26 April 2026).

**Package**: `my.scamshield`
**Version**: 0.1.0 (debug-only, no flavors)
**Targets**: Android (primary), iOS (stubbed — shared module only, no Xcode project).

Architecture inherits conventions from the Felinius KMP app (Clean Architecture + feature modules + Koin + Voyager). The pattern deltas from Felinius are:
- No Firebase, no Crashlytics, no RevenueCat, no WorkManager, no Rive/Lottie, no TFLite.
- No JS / Wasm / web targets.
- Single `debug` build type, no dev/staging/prod flavors.
- No offline cache / sync manager.
- No login/register/onboarding — `demo_user_01` hardcoded in `HardcodedSessionRepository`.

## Build Commands

```bash
# Android
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug

# iOS (no Xcode project) — just verify compilation
./gradlew :composeApp:linkDebugFrameworkIosArm64
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# All tests
./gradlew :composeApp:allTests
./gradlew :composeApp:jvmTest --tests "*.TransferConfirmViewModelTest"
```

## Architecture

Clean Architecture, three layers per feature:

```
presentation/ -> domain/ <- data/
```

- **domain/**: pure Kotlin — models, repository interfaces, use cases.
- **data/**: implementations — DTOs, mappers, remote sources, repository impls (including `Fake*` for offline rehearsal).
- **presentation/**: UI — Compose screens, ScreenModel ViewModels, UI states.

## Feature Modules

```
feature/
├── auth/            # HardcodedSessionRepository (demo_user_01), no login flow
├── login/           # 1-tap continue screen
├── home/            # entry screen post-login, Send Money button
├── transfer/        # THE CORE — compose + confirm + scam warning + success
├── devicetrust/     # L1 UI stub — device cooldown screen, not triggered live
└── scenarios/       # demo scenarios G1 / R1 loader for rehearsal mode
```

## Key Patterns

### Dependency Injection (Koin 4.1)

Each feature has its own module aggregated into `AppModule`:

```
app/di/AppModule.kt               — aggregates all modules
core/di/CoreModule.kt             — Logger, AppClock, ApiConfig
core/di/NetworkModule.kt          — Ktor HttpClient
feature/<name>/di/<Name>Module.kt — per-feature
```

Platform init via `expect fun initKoin()` in `my.scamshield.di.KoinHelper.kt`.

### Screens + ViewModels (Voyager)

- `Screen` subclasses in `feature/<name>/presentation/<subfeature>/`
- ViewModels are `ScreenModel` — use `screenModelScope.launch { … }` for async
- UI state is a `data class` in the same folder — single StateFlow per ViewModel
- Inject via `koinScreenModel<MyVM>()` in the `@Composable Content()`

### HTTP Client (Ktor)

`HttpClientFactory` in `core/data/remote/` configures:
- JSON serialization (`ContentNegotiation` + `kotlinx-serialization`)
- Timeout + retry for 5xx
- Request logging (kermit-backed)

No auth / bearer tokens needed for hackathon — backend uses a shared env-var secret.

### Repository Pattern

Each domain feature has:
- `domain/repository/XxxRepository.kt` interface
- `data/repository/XxxRepositoryImpl.kt` real impl
- `data/repository/FakeXxxRepository.kt` for rehearsal when backend is down

Swap via `USE_FAKE_TRANSFER_REPO` constant in `transferModule`. Default = true.

### Demo scenarios (rehearsal mode)

Defined in `feature/scenarios/domain/model/DemoScenarios.kt` — the two canonical scenarios G1 (RM50 to Siti → GREEN) and R1 (RM2,000 to mule → RED) exactly match doc 05 §4 in the plan folder. Do not edit these without updating the seed script AND the plan doc.

## Source Sets

```
composeApp/src/
├── commonMain/     # Shared code (99%)
├── commonTest/     # Shared tests (Turbine, coroutines-test, ktor-client-mock)
├── androidMain/    # Android-specific (Logger impl, Context holder, Koin init)
└── iosMain/        # iOS-specific (stub Logger, Koin init)
```

## Code Style

### Comments Policy (STRICT — same as Felinius)

- **NO COMMENTS**: inline, KDoc, or explanatory. None.
- **ONLY exceptions**:
  - `TODO:` for critical follow-ups
  - `FIXME:` for known bugs
- Self-documenting code via clear naming. If code needs a comment, rename the function.

### Other Rules

- Use `Logger` via Koin injection — never `println` or `android.util.Log.d`.
- Prefer `StateFlow` over `LiveData` or bare `var`. One StateFlow per ViewModel.
- `@OptIn(ExperimentalMaterial3Api::class)` on screens that use `TopAppBar` — don't propagate it to callers.
- `collectAsStateWithLifecycle()` for state collection in screens — not `collectAsState()`.
- UI uses Material3 theme (`ScamShieldTheme { … }`). Do not use Material2.

## Common Pitfalls

- **ScreenModel re-creation**: Voyager re-uses the same ScreenModel across nav. Don't load data in `init {}` — use an explicit `load()` in `LaunchedEffect`.
- **LaunchedEffect(Unit)**: Use a specific key (e.g. `LaunchedEffect(transaction.id)`).
- **`@OptIn(ExperimentalMaterial3Api::class)`**: Required for `TopAppBar`.
- **`TopAppBar` + nested `Scaffold`**: Don't do it. Let the parent `Scaffold` own the top bar.

## Demo data & expected scores

See `/plan/05_data_seeding.docx` §4. Summary:

| ID | Sender | Recipient | Amount | Expected verdict | Expected score |
|----|--------|-----------|--------|------------------|----------------|
| G1 | demo_user_01 (Wafi) | Siti Aminah (+60 12-345 6789) | RM 50   | GREEN | 15–25 |
| R1 | demo_user_01 | recipient_mule_01 (+60 11-XXXX 8712) | RM 2,000 | RED   | 82–90 |

If `FakeTransferRepository` is swapped for the real `TransferRepositoryImpl`, the backend must produce the same verdicts for these exact phone numbers or the demo breaks.

## Plan docs (read before Saturday)

Located at `/Users/ahmadwafi/hackaton-tng/plan/`:

- `01_problem_analysis.docx` — scam lifecycle problems
- `02_solution_layers.docx` — 3-layer defence (L1/L2/L3)
- `03_demo_script.docx` — storyboards + stack decision
- `04_bo_dashboard.docx` — BO spec + hero mule graph
- `05_data_seeding.docx` — synthetic data + LOCKED scenarios
- `06_pitch_structure.docx` — 7-minute pitch arc + Q&A prep
- `07_decisions_to_lock.docx` — sign-off before hackathon
