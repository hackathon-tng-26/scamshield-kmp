# ScamShield — KMP mobile client

AI-powered anti-scam layer for e-wallet transfers. Built for TNG Digital FinHack 2026.

## Stack

- Kotlin 2.3.0 · Compose Multiplatform 1.11.0-alpha01
- Koin 4.1 · Ktor 3.3 · Voyager 1.1.0-beta03
- Kermit (logging) · Multiplatform Settings (persistence)

Android target is primary for the demo. iOS target compiles but no Xcode project is shipped in this scaffold.

## Quick start

```bash
# 1. Copy local properties example and point to your Android SDK
cp local.properties.example local.properties
# Edit local.properties: set sdk.dir to your Android SDK path

# 2. Assemble the Android debug APK
./gradlew :androidApp:assembleDebug

# 3. Install on a running emulator / connected device
./gradlew :androidApp:installDebug
```

The app defaults to a **Fake** transfer repository so the demo runs without a backend. To wire to a live FastAPI:

1. Set `SCAMSHIELD_API_BASE_URL` in `local.properties`
2. Flip `USE_FAKE_TRANSFER_REPO = false` in `feature/transfer/di/TransferModule.kt`

## Rehearsal mode

The **Send Money** flow exposes two pre-seeded shortcut buttons on the compose screen:

- **G1 · Siti · RM 50** → GREEN verdict (happy path)
- **R1 · mule · RM 2,000** → RED verdict (scam warning)

These match the locked demo scenarios in `/plan/05_data_seeding.docx` §4.

## Structure

```
scamshield/
├── androidApp/                  # Android launcher (thin)
├── composeApp/                  # Shared code + UI
│   └── src/commonMain/kotlin/my/scamshield/
│       ├── app/                 # App.kt + AppModule
│       ├── core/                # DI, Logger, AppClock, HTTP, theme
│       └── feature/
│           ├── auth/            # hardcoded session
│           ├── login/           # 1-tap continue
│           ├── home/            # Send Money entry
│           ├── transfer/        # compose · confirm · warning · success
│           ├── devicetrust/     # L1 cooldown stub
│           └── scenarios/       # G1/R1 demo loader
├── gradle/
│   └── libs.versions.toml
└── CLAUDE.md                    # AI assistant conventions
```

See `CLAUDE.md` for conventions, code-style rules, and pitfalls.

See `/plan/` for the full hackathon plan documents.
