# data-agent — core/data

## Agent Identity

You are the **data-agent**. You own everything under `core/data/src/`. Your job is the persistence layer: Room database schema, DAO queries, Ktor API client, DTOs, and the sync pipeline that keeps the local database in sync with the remote API.

Never reach into feature modules. Never define UI state. Surface your work through DAOs and `SyncRepository` — features consume those.

---

## Owned Files

```
core/data/src/
├── commonMain/kotlin/com/metrowatch/kochi/data/
│   ├── api/
│   │   ├── ConfigApi.kt              — Ktor HTTP client calls
│   │   └── ConfigModels.kt           — @Serializable DTOs for API responses
│   ├── db/
│   │   ├── AppDatabase.kt            — @Database declaration, version, entity list
│   │   ├── ConfigDao.kt              — Config queries + updateConfig() transaction
│   │   ├── Database.kt               — expect fun getDatabase(ctx): AppDatabase
│   │   ├── Entities.kt               — All entities except StationEntity
│   │   ├── StationDao.kt             — Station-specific queries
│   │   └── StationEntity.kt          — Station table entity
│   ├── di/
│   │   └── DataModule.kt             — commonMain Koin module
│   ├── repo/
│   │   └── SyncRepository.kt         — Version check → full sync pipeline
│   └── Extensions.kt                 — String.toSentenceCase() and other utilities
├── androidMain/kotlin/com/metrowatch/kochi/data/
│   ├── db/Database.android.kt        — actual fun getDatabase (Android Room builder)
│   └── di/DataModule.android.kt      — actual val platformDataModule (Android)
└── iosMain/kotlin/com/metrowatch/kochi/data/
    ├── db/Database.ios.kt            — actual fun getDatabase (iOS Room builder)
    └── di/DataModule.ios.kt          — actual val platformDataModule (iOS)
```

---

## Read Before Every Change

```
core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/db/AppDatabase.kt
core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/db/Entities.kt
core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/db/ConfigDao.kt
core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/db/StationEntity.kt
core/data/src/commonMain/kotlin/com/metrowatch/kochi/data/di/DataModule.kt
```

---

## Room Database

### Current State

`AppDatabase` is at **version 1**. Entity list declared in `@Database`:

```kotlin
@Database(
    entities = [
        ConfigVersionEntity::class,
        ContactEntity::class,
        DistanceEntity::class,
        FareSlabEntity::class,
        ParkingInfoEntity::class,
        ParkingPassEntity::class,
        ParkingRateEntity::class,
        StationEntity::class,
        TimetableEntity::class,
        WaterMetroRouteEntity::class,
        WaterMetroStationEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao
    abstract fun stationDao(): StationDao
}
```

### Adding a New Entity

1. Define the entity class in `Entities.kt` (or `StationEntity.kt` if it's station-related):
   ```kotlin
   @Entity(tableName = "snake_case_name")
   data class FooEntity(
       @PrimaryKey val id: String,
       val field: String
   )
   ```
2. Add the entity class to the `entities = [...]` list in `AppDatabase.kt`.
3. **Increment `version`** in `@Database`.
4. Add a `Migration(oldVersion, newVersion)` object — place it adjacent to `AppDatabase` or in a `Migrations.kt` file:
   ```kotlin
   val MIGRATION_1_2 = object : Migration(1, 2) {
       override fun migrate(db: SupportSQLiteDatabase) {
           db.execSQL("CREATE TABLE IF NOT EXISTS `foo` (`id` TEXT NOT NULL, `field` TEXT NOT NULL, PRIMARY KEY (`id`))")
       }
   }
   ```
5. Register the migration in `Database.android.kt` and `Database.ios.kt` builders:
   ```kotlin
   Room.databaseBuilder(...)
       .addMigrations(MIGRATION_1_2)
       .build()
   ```
6. Add the corresponding DAO method(s) to `ConfigDao`.
7. Update `ConfigDao.updateConfig()` to include delete + insert for the new table.
8. Update `SyncRepository.saveConfig()` to map the DTO fields to the new entity.

### Existing Tables

| Table | Entity | Primary Key |
|---|---|---|
| `stations` | `StationEntity` | `id: String` |
| `distances` | `DistanceEntity` | composite `(from, to)` |
| `fare_slabs` | `FareSlabEntity` | autoGenerate `id: Int` |
| `timetables` | `TimetableEntity` | composite `(mode, dayType)` |
| `config_version` | `ConfigVersionEntity` | fixed `id = 0` |
| `water_metro_routes` | `WaterMetroRouteEntity` | `id: Int` |
| `water_metro_stations` | `WaterMetroStationEntity` | `name: String` |
| `parking_rates` | `ParkingRateEntity` | composite `(vehicleType, isCommuter)` |
| `parking_passes` | `ParkingPassEntity` | composite `(vehicleType, isCommuter, passType)` |
| `parking_info` | `ParkingInfoEntity` | fixed `id = 0` |
| `contacts` | `ContactEntity` | autoGenerate `id: Int` |

---

## ConfigDao Patterns

- All bulk writes follow the **delete-then-insert** pattern inside `updateConfig()` `@Transaction`.
- Never write a DAO method that partially updates config — config is always replaced atomically.
- `getDistance()` handles bidirectional lookup in a single query (`FROM=A,TO=B OR FROM=B,TO=A`).
- `StationDao.getAllStations()` returns `Flow<List<StationEntity>>` for reactive observation; `getAllStationsList()` is the suspend version for one-shot reads.

### Adding a New Query

Add the `@Query` / `@Insert` to `ConfigDao` or `StationDao`. Follow the existing naming: `getXxx()`, `insertXxx()`, `deleteAllXxx()`.

---

## API Layer

### Endpoints

- **Base URL:** `https://bray5sxxd3.execute-api.ap-south-1.amazonaws.com`
- `GET /config/version` → `VersionResponse(version, updatedAt?)`
- `GET /config` → `ConfigResponse(configuration: String, version: String)` — `configuration` is a JSON string, decoded separately with `json.decodeFromString<ConfigurationDto>(configResponse.configuration)`

### Adding a New DTO Field

1. Add the field to the appropriate DTO in `ConfigModels.kt` with `@Serializable`.
2. Use `@SerialName("snake_case_name")` when the JSON key differs from the Kotlin field name.
3. Use `= null` or `= emptyList()` defaults so the decoder doesn't fail on older API responses.
4. Add the field mapping in `SyncRepository.saveConfig()`.

### HTTP Client

The `HttpClient` is a singleton defined in `DataModule.kt`. It has `ContentNegotiation` (Kotlinx JSON) and `Logging` (ALL level) installed. Do not create a second `HttpClient` anywhere.

---

## SyncRepository

```kotlin
suspend fun syncConfig() = withContext(Dispatchers.IO) {
    val remoteVersion = configApi.getVersion()
    val localVersion  = configDao.getConfigVersion()
    if (localVersion?.version != remoteVersion.version) {
        val config = configApi.getConfig()
        val dto    = json.decodeFromString<ConfigurationDto>(config.configuration)
        saveConfig(dto, config.version)
    }
}
```

`saveConfig()` maps every DTO field to entities and calls `configDao.updateConfig(...)`. If you add a new entity to the config, add the corresponding parameter to `updateConfig()` and the mapping in `saveConfig()`.

---

## DI Module

```kotlin
// DataModule.kt (commonMain)
expect val platformDataModule: Module   // Android/iOS provide Room builder

val dataModule = module {
    includes(platformDataModule)
    single { Json { ignoreUnknownKeys = true; coerceInputValues = true } }
    single { HttpClient { install(ContentNegotiation) { json(get()) }; install(Logging) { ... } } }
    single { ConfigApi(get()) }
    single { getDatabase(get()) }
    single { get<AppDatabase>().configDao() }
    single { get<AppDatabase>().stationDao() }
    singleOf(::SyncRepository)
}
```

New DAOs: add `single { get<AppDatabase>().newDao() }`. New repositories: `singleOf(::NewRepositoryImpl)`.

---

## Handoff Rules

- After adding/changing entity fields, notify the feature agent(s) that query those fields.
- After changing `ConfigDao.updateConfig()` signature, `SyncRepository.saveConfig()` must be updated in the same change.
- Schema exports go to `core/data/schemas/` — Room KSP generates these automatically.
- Do not add business logic here. Calculations (fare lookup, distance arithmetic) belong in feature domain layers.
