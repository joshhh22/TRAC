# AI Development Rules for TRAC Android Project

> **MANDATORY RULES**: Every AI assistant, copilot, or automated engineer working on the TRAC codebase MUST strictly adhere to these rules before proposing or making changes.

---

## 1. Architectural Integrity & Boundaries
1. **Do not modify the architecture without explicit user approval**:
   - Maintain the existing MVVM pattern. Do not introduce complex frameworks (e.g. MVI, Clean Architecture multi-modules, Hilt, Koin) unless explicitly instructed by the user.
2. **Preserve Navigation Pattern**:
   - The application relies on `MainActivity.kt`'s `Screen` enum and `AnimatedContent`. Do not rewrite navigation using Jetpack Navigation Compose without explicit approval.
3. **No Duplicate Implementations**:
   - Before adding utility functions, dialogs, or custom components, inspect `components/`, `data/`, and `util/` to reuse existing implementations.
4. **Preserve Database Contracts**:
   - Do NOT alter table names, column names, or serial names in `ReportData` (`@SerialName(...)`) without confirming Supabase backend schema migration.

---

## 2. Dependencies & Libraries
1. **Never introduce a new dependency if an existing one can solve the problem**:
   - The project already has Kotlin Coroutines, StateFlow, Ktor, Supabase SDK, and Compose Material3.
   - Do NOT add Coil, Glide, or Retrofit unless explicitly requested.
2. **Use Version Catalog Only**:
   - All dependency additions or version bumps must be declared in `gradle/libs.versions.toml`. Never hardcode library versions directly in `build.gradle.kts`.

---

## 3. Concurrency, Threading & Performance
1. **Never do heavy computations or I/O on the Main Thread**:
   - Image conversions (`BitmapFactory.decode*`, `Bitmap.compress`, `Base64.encode`) MUST run inside a coroutine with `Dispatchers.IO` or `Dispatchers.Default`, never inside an `onClick` lambda.
2. **Use Lazy Lists for Dynamic Collections**:
   - Always use `LazyColumn` / `LazyRow` with unique keys (`key = { it.id }`) for unbounded lists (such as reports), never `Column` with `verticalScroll` and `forEach`.
3. **Prevent Unnecessary Recompositions**:
   - Expensive computations (such as filtering or searching report lists) must be wrapped in `remember(key1, key2)`.
   - Use stable types or `@Immutable` data classes where appropriate.

---

## 4. UI, Styling & Conventions
1. **Respect Manual Dual-Theming**:
   - The app uses explicit boolean parameters `isDarkMode` and `isIndonesian`. Always propagate these parameters to newly created screens and components.
   - Maintain the 5-token color convention: `pageBg`, `cardBg`, `textPrimary`, `textSecondary`, and `borderCol`.
2. **Preserve Canvas-Based Vector Graphics**:
   - TRAC uses custom Compose `Canvas` drawing for icons and illustrations. Do not replace them with random material icons unless asked.
3. **Maintain Domain-Specific Master Data**:
   - Do NOT modify or delete the 28 school class list or the 4-floor room directory without user guidance.

---

## 5. Security & Sensitive Information
1. **Never commit or expose secrets**:
   - Do not print, log, or hardcode production service keys, passwords, or tokens in logs or code.
   - Any sensitive keys should ideally be migrated to `local.properties` or `BuildConfig`.
2. **Sanitize and Validate Inputs**:
   - Always validate user inputs (email format, password length, required fields) both on UI and ViewModel layers.

---

## 6. Pre-Implementation Workflow (Copilot Protocol)

Before writing or modifying any code for future tasks:
1. **Locate & Read**: Identify existing files and read their exact implementation.
2. **Trace Dependencies**: Understand who calls the function/component and what side effects may occur.
3. **Plan & Explain**: Briefly describe the planned approach and potential impacts before writing code.
4. **Compile & Verify**: After any changes, verify build validity using `./gradlew compileDebugKotlin` and run unit tests.
5. **Report Assumptions**: State any assumptions explicitly rather than guessing undocumented behavior.
