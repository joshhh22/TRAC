# TRAC (Tradevis Track and Care) — Technical Architecture & Context

> **Single Source of Truth** for AI development copilots, software architects, and engineering team members.

---

## 1. Project Overview
TRAC (*Tradevis Track and Care*) adalah aplikasi mobile Android berbasis Jetpack Compose yang dirancang untuk pelaporan dan pemantauan kerusakan fasilitas sekolah (khususnya SMK / lingkungan pendidikan kejuruan seperti jurusan RPL, AKL, MP, Manlog, ULW, BD, BR).
Aplikasi ini memungkinkan siswa/pelapor untuk:
- Mendaftar (*sign up*) dan masuk (*sign in*) menggunakan akun siswa/email.
- Mengirim laporan kerusakan fasilitas (kategori, lokasi per lantai/ruangan, judul, deskripsi, serta bukti foto kamera/galeri).
- Memantau status laporan (*Pending*, *In Progress*, *Completed*).
- Meninjau notifikasi sistem dan memperbarui profil/identitas lokal & cloud.

---

## 2. Tech Stack
| Layer / Tool | Technology & Version | Status & Notes |
|---|---|---|
| **Language** | Kotlin `2.0.21` | Modern Kotlin dengan K2 Compose Compiler plugin |
| **Build Tool** | Gradle `9.6.0` (Wrapper) | Didukung Foojay toolchain resolver |
| **Android Gradle Plugin** | AGP `9.4.1` | Android Application Plugin |
| **JDK Environment** | OpenJDK 25 (Android Studio JBR) | `sourceCompatibility = VERSION_11`, `targetCompatibility = VERSION_11` |
| **Android SDK** | `compileSdk = release(37)`, `minSdk = 24`, `targetSdk = 37` | Android 14+ / 15 targeting |
| **UI Framework** | Jetpack Compose (BOM `2024.09.00`) | Material3, Canvas 2D custom vector drawing |
| **State & Concurrency** | Kotlin Coroutines `1.8+`, StateFlow, MutableStateFlow | `AndroidViewModel`, `viewModelScope` |
| **BaaS & Networking** | Supabase Kotlin SDK `3.0.0` (Auth, PostgREST) | Ktor Client Android `3.0.0` engine |
| **Serialization** | `kotlinx-serialization-json` `1.7.3` | Serializer untuk DTO PostgREST |
| **Local Persistence** | `SharedPreferences` (`MODE_PRIVATE`) | Unencrypted session & preferences storage |
| **Testing** | JUnit 4 (`4.13.2`), AndroidX JUnit (`1.1.5`), Espresso (`3.5.1`) | Default template only (zero test coverage) |

---

## 3. Architecture Pattern
- **Pattern**: Pola MVVM (*Model-View-ViewModel*) yang disederhanakan tanpa UseCase/Domain layer formal dan tanpa Dependency Injection (DI) framework.
- **Layering**:
  1. **UI Layer (`com.example.trac` & `components`)**:
     - Single Activity (`MainActivity.kt`) menampung seluruh screen dalam satu `AnimatedContent` berbasis enum `Screen`.
     - Pure Jetpack Compose UI dengan custom Canvas drawing untuk ikon, banner, ilustrasi, dan avatar.
  2. **ViewModel Layer (`com.example.trac.viewmodel`)**:
     - `AuthViewModel` & `ReportViewModel` mewarisi `AndroidViewModel`.
     - Mengelola state melalui `MutableStateFlow<AuthUiState>` dan `MutableStateFlow<ReportUiState>`.
  3. **Data Layer (`com.example.trac.data`)**:
     - `AuthRepository`: Berinteraksi dengan Supabase GoTrue Auth dan mengelola `SessionPreferences`.
     - `ReportRepository`: Berinteraksi dengan Supabase PostgREST table `"reports"`.
     - `SessionPreferences`: Mengelola SharedPreferences lokal (`"trac_user_session"`).
     - `SupabaseClientManager`: Singleton object penyedia `SupabaseClient`.
  4. **Utility Layer (`com.example.trac.util`)**:
     - `ImageUtils`: Encoding Bitmap/Uri ke Base64 data URL dan decoding Base64 ke Bitmap.

---

## 4. Directory & Package Structure
```text
TRAC/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml          # Internet permission & MainActivity definition
│   │   │   ├── java/com/example/trac/
│   │   │   │   ├── MainActivity.kt          # Single-Activity host, Screen enum, Navigation & top state
│   │   │   │   ├── SplashTracScreen.kt      # Onboarding / splash screen
│   │   │   │   ├── LoginTracScreen.kt       # Authentication login form
│   │   │   │   ├── RegisterTracScreen.kt    # Student registration form
│   │   │   │   ├── HomeTracScreen.kt        # Dashboard, summary counters, recent reports
│   │   │   │   ├── CreateReportTracScreen.kt# Report creation with image picker & room sheet
│   │   │   │   ├── ReportListTracScreen.kt  # All reports with filter chips & search
│   │   │   │   ├── ReportDetailTracScreen.kt# Detail view of a report
│   │   │   │   ├── NotificationsTracScreen.kt# Mock notifications & system updates
│   │   │   │   ├── EditIdentityTracScreen.kt# Profile & student class edit screen
│   │   │   │   ├── SettingsTracScreen.kt    # Language & dark theme toggles
│   │   │   │   ├── TermsTracScreen.kt       # Terms & conditions view
│   │   │   │   ├── components/
│   │   │   │   │   ├── InAppBanner.kt       # Animated error/success feedback banner
│   │   │   │   │   ├── ProfileSidebarDrawer.kt # Slide-out right side navigation drawer
│   │   │   │   │   └── TracBottomNavBar.kt  # Stationary 5-slot bottom navigation bar
│   │   │   │   ├── data/
│   │   │   │   │   ├── AuthRepository.kt    # Auth logic & session bridge
│   │   │   │   │   ├── ReportData.kt        # Serializable DTO for reports table
│   │   │   │   │   ├── ReportRepository.kt  # PostgREST CRUD operations
│   │   │   │   │   ├── SessionPreferences.kt# SharedPreferences wrapper
│   │   │   │   │   └── SupabaseClient.kt    # Supabase Client Manager singleton
│   │   │   │   ├── util/
│   │   │   │   │   └── ImageUtils.kt        # Base64 bitmap conversions & scaling
│   │   │   │   └── viewmodel/
│   │   │   │       ├── AuthViewModel.kt     # Auth UI state & coroutine dispatcher
│   │   │   │       └── ReportViewModel.kt   # Reports list state & submission handling
│   │   │   └── res/
│   │   │       ├── layout/activity_main.xml # Legacy template layout (unused by setContent)
│   │   │       ├── values/                  # Strings, colors, themes (forced light base)
│   │   │       └── xml/                     # Backup rules & data extraction rules
│   │   ├── test/java/com/example/trac/      # Unit tests (template only)
│   │   └── androidTest/java/com/example/trac/# Instrumentation tests (template only)
│   ├── build.gradle.kts                     # Module build configuration
│   └── .gitignore
├── gradle/
│   ├── libs.versions.toml                   # Version catalog (dependencies & plugins)
│   ├── gradle-daemon-jvm.properties         # Toolchain Java 25 configuration
│   └── wrapper/                             # Gradle wrapper (9.6.0)
├── build.gradle.kts                         # Root build configuration
├── settings.gradle.kts                      # Module includes & repository management
├── gradle.properties                        # JVM memory args & configuration cache
└── docs/                                    # Technical documentation & AI copilot rules
```

---

## 5. Important Files Reference
- [MainActivity.kt](file:///c:/Users/josh/TRAC/app/src/main/java/com/example/trac/MainActivity.kt): Jantung navigasi dan state sentral seluruh aplikasi.
- [AuthViewModel.kt](file:///c:/Users/josh/TRAC/app/src/main/java/com/example/trac/viewmodel/AuthViewModel.kt): Mengatur alur login, register, sign-out, dan mapping pesan error Supabase.
- [ReportViewModel.kt](file:///c:/Users/josh/TRAC/app/src/main/java/com/example/trac/viewmodel/ReportViewModel.kt): Mengatur pemanggilan data laporan dari Supabase dan state pembuatan laporan.
- [SupabaseClient.kt](file:///c:/Users/josh/TRAC/app/src/main/java/com/example/trac/data/SupabaseClient.kt): Konfigurasi endpoint dan API key Supabase.
- [SessionPreferences.kt](file:///c:/Users/josh/TRAC/app/src/main/java/com/example/trac/data/SessionPreferences.kt): Penyimpanan session user, bahasa, dan dark mode secara lokal.
- [ImageUtils.kt](file:///c:/Users/josh/TRAC/app/src/main/java/com/example/trac/util/ImageUtils.kt): Utility konversi gambar ke Base64 JPEG.

---

## 6. Data Flow & Communication
```text
User Interaction
       │
       ▼
Composable Screen (e.g. CreateReportTracScreen)
       │ (Invokes Lambda Callback)
       ▼
MainActivity TRACApp Container
       │ (Calls ViewModel method)
       ▼
ViewModel (e.g. ReportViewModel)
       │ (Launches coroutine on viewModelScope)
       ▼
Repository (e.g. ReportRepository with Dispatchers.IO)
       │ (PostgREST HTTP Request via Ktor)
       ▼
Supabase Cloud Database (table: "reports")
       │
       ▼
Response / Result<T>
       │
       ▼
StateFlow Mutation (_reports.value / _uiState.value)
       │
       ▼
Composable Recomposition (MainActivity / Home / ReportList)
```

---

## 7. State Management
- **Screen State**: Enum `Screen` disimpan di `MainActivity` via `remember { mutableStateOf(...) }`.
- **Async UI States**:
  - `AuthUiState`: `Idle`, `Loading`, `Success(message, isLoginSuccess)`, `Error(message)`.
  - `ReportUiState`: `Idle`, `Loading`, `Success(message)`, `Error(message)`.
- **Data States**:
  - `ReportViewModel.reports`: `StateFlow<List<ReportData>>`.
- **Duplicated State Risk**: Nama, kelas, dan avatar tersimpan di `SessionPreferences`, diduplikasi di variabel `remember` di `MainActivity`, dan dibaca secara non-reactive saat pertama kali dimuat.

---

## 8. API & Database Architecture
- **Supabase Instance**: Cloud PostgREST API.
- **Table `"reports"` Schema**:
  - `id`: Text / UUID (Primary Key, autogenerated oleh Supabase).
  - `created_at`: Timestamptz (ISO string).
  - `user_id`: Text (ID pengguna pelapor).
  - `user_name`: Text (Nama lengkap pengguna).
  - `title`: Text (Judul laporan).
  - `location`: Text (Ruangan / lokasi lantai).
  - `category`: Text (Kategori kerusakan).
  - `description`: Text (Deskripsi rinci kerusakan).
  - `status`: Text (Default: `"Pending"`, dapat berupa `"In Progress"` atau `"Completed"`).
  - `image_url`: Text (Saat ini menampung Base64 data URI `"data:image/jpeg;base64,..."`, bukan URL storage).

---

## 9. Authentication & Authorization
- **Provider**: Supabase GoTrue Auth via Email & Password provider.
- **Auto Domain Convention**: Jika pengguna mendaftar atau login hanya dengan username (tanpa tanda `@`), `AuthViewModel` otomatis menambahkan `@gmail.com`.
- **User Metadata**: Menyimpan field `full_name`, `role` (`"Siswa"`), dan `user_class` di Supabase `user_metadata`.
- **Session Caching**: Setelah login berhasil, session disimpan di Android `SharedPreferences` sehingga saat aplikasi dibuka kembali, user tidak perlu login ulang.

---

## 10. Navigation Architecture
- Menggunakan Custom Enum Navigation (`Screen` enum) di dalam `MainActivity.kt`.
- Transisi layar diatur oleh `AnimatedContent` dengan kombinasi `slideInHorizontally`, `fadeIn`, `scaleIn`, `scaleOut`, dan `fadeOut`.
- **Backstack**: Manual handling melalui `BackHandler`. Layar child (`CREATE_REPORT`, `REPORT_LIST`, `NOTIFICATIONS`, `SETTINGS`, `EDIT_IDENTITY`) langsung kembali ke `HOME`. `REPORT_DETAIL` kembali ke `previousScreen`. Double-tap back di `HOME` / `SPLASH` untuk keluar aplikasi.

---

## 11. Dependencies
Semua dependency dikelola via Gradle Version Catalog (`gradle/libs.versions.toml`):
- `androidx.compose.bom:2024.09.00`
- `io.github.jan-tennert.supabase:bom:3.0.0` (`auth-kt`, `postgrest-kt`)
- `io.ktor:ktor-client-android:3.0.0`
- `org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3`
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7`
- `androidx.activity:activity-compose:1.8.2`
- Legacy unused dependencies: `androidx.appcompat`, `androidx.constraintlayout`, `com.google.android.material:material`.

---

## 12. Known Technical Debt & Issues
1. **Base64 Image Storage**: Menyimpan string Base64 gambar langsung ke tabel PostgreSQL alih-alih mengunggah binary ke Supabase Storage bucket.
2. **Main Thread Image Processing**: Pemanggilan `ImageUtils.uriToBase64` dilakukan secara synchronous di callback klik tombol UI (`CreateReportTracScreen` & `EditIdentityTracScreen`).
3. **No Lazy Loading on Report List**: `ReportListTracScreen` menggunakan `Column` + `verticalScroll` + `forEach`, bukan `LazyColumn`, menyebabkan seluruh item (dan Base64 imagenya) di-render sekaligus.
4. **Mocked Features**: Notifikasi dan Komentar pada detail laporan saat ini hanya beroperasi secara in-memory mock tanpa tabel backend Supabase.
5. **No Dependency Injection**: Inisialisasi Repository dilakukan langsung di dalam constructor ViewModel atau composable.
6. **No Automated Test Coverage**: Tidak ada unit test untuk ViewModel, Repository, atau UI.

---

## 13. Security Concerns
- Hardcoded Supabase URL & Anon Key di source code (`SupabaseClient.kt`).
- SharedPreferences tidak terenkripsi (`Context.MODE_PRIVATE`).
- User input tidak disanitasi sebelum dikirim ke database (bergantung sepenuhnya pada parameterization Supabase PostgREST).
- Tidak ada validasi format gambar atau batas ukuran byte sebelum konversi Base64.

---

## 14. Performance Concerns
- Risiko `OutOfMemoryError` (OOM) saat user mengambil gambar beresolusi tinggi (kamera 12MP/48MP/108MP) karena decode bitmap tanpa `inSampleSize`.
- Recomputing filter di `ReportListTracScreen` pada setiap recomposition tanpa `remember`.
- Beban transfer jaringan yang membengkak karena payload data URL Base64 yang diunduh berulang kali dalam list laporan.

---

## 15. Testing Status
- Unit Tests: 1 file (`ExampleUnitTest.kt`, boilerplate).
- Instrumented Tests: 1 file (`ExampleInstrumentedTest.kt`, boilerplate).
- Code Coverage: 0% functional coverage.

---

## 16. Current Development Status
- Project status: **Feature Complete Functional Prototype**.
- Build status: **Clean Compile** (`compileDebugKotlin` SUCCESS, `testDebugUnitTest` SUCCESS).

---

## 17. Important Conventions
1. **Bahasa & Internasionalisasi**: Aplikasi mendukung toggle Bahasa Indonesia dan Bahasa Inggris secara manual via state `isIndonesian`. Gunakan format string ternary/kondisional yang konsisten.
2. **Theming**: Dark Mode dikontrol manual via boolean `isDarkMode` / `isAppDarkMode`, bukan system default theme. Selalu sediakan mapping warna `pageBg`, `cardBg`, `textPrimary`, `textSecondary`, dan `borderCol`.
3. **Canvas Drawing**: Ikon dan ilustrasi sebagian besar digambar menggunakan Compose `Canvas` 2D Path untuk performa dan independence dari asset XML/PNG.

---

## 18. Things That Must NOT Be Changed Casually
1. **Database Table Name & Keys**: Tabel `"reports"` dengan field `title`, `location`, `category`, `description`, `status`, `image_url` terikat langsung dengan skema database Supabase live.
2. **Class Names List**: Daftar 28 kelas SMK di `RegisterTracScreen` dan `EditIdentityTracScreen` adalah data resmi lingkungan sekolah sasaran.
3. **Floor & Room Hierarchy**: Struktur Lantai 1 s/d Lantai 4 di `CreateReportTracScreen` merefleksikan denah fisik sekolah.
4. **Auto-Append Email Domain**: Behavior `@gmail.com` jika user menginput username tanpa domain harus dipertahankan kecuali diarahkan lain oleh user.
