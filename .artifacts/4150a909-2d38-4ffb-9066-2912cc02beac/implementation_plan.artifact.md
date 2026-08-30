# Weather Application Implementation Plan

Design and develop a Weather Application using Kotlin and XML with Firebase Authentication, Location services, Weather API integration, Room database, and SharedPreferences.

## User Review Required

> [!IMPORTANT]
> **Firebase Setup**: I will add the Firebase Authentication dependency, but you will need to provide the `google-services.json` file if it's not already in the project (it wasn't visible in the file list).
> **Weather API Key**: I will use OpenWeatherMap API. You will need to provide an API key, or I can use a placeholder for you to replace.

## Proposed Changes

### Dependencies and Configuration

I will update `libs.versions.toml` and `app/build.gradle.kts` to include:
- **Networking**: Retrofit & Gson
- **Coroutines**: For asynchronous API calls and database operations.
- **Location**: Google Play Services Location.
- **Database**: Room (modern replacement for raw SQLite).
- **Images**: Glide for weather icons.
- **Navigation**: Jetpack Navigation component (optional, but recommended for clean structure).

---

### Authentication (Firebase)

#### [NEW] [LoginActivity.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/LoginActivity.kt)
#### [NEW] [RegisterActivity.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/RegisterActivity.kt)
#### [NEW] [activity_login.xml](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/res/layout/activity_login.xml)
#### [MODIFY] [activity_register.xml](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/res/layout/activity_register.xml)

- Implement sign-in and sign-up logic using `FirebaseAuth`.
- Basic validation (email format, password length).

---

### Weather API & Location

#### [NEW] [WeatherApiService.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/api/WeatherApiService.kt)
#### [NEW] [WeatherModels.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/api/WeatherModels.kt)
#### [NEW] [LocationHelper.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/utils/LocationHelper.kt)

- Define Retrofit interface for weather data.
- Request `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions.
- Fetch weather data based on current coordinates.

---

### Local Persistence (Room/SQLite)

#### [NEW] [WeatherEntity.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/db/WeatherEntity.kt)
#### [NEW] [WeatherDao.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/db/WeatherDao.kt)
#### [NEW] [WeatherDatabase.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/db/WeatherDatabase.kt)

- Define schema for saving weather records.
- Implement CRUD operations: Save, View (Read), Update, Delete.

---

### UI & Features

#### [MODIFY] [MainActivity.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/MainActivity.kt)
#### [MODIFY] [activity_main.xml](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/res/layout/activity_main.xml)
#### [NEW] [WeatherAdapter.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/ui/WeatherAdapter.kt)
#### [NEW] [item_weather.xml](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/res/layout/item_weather.xml)
#### [NEW] [SettingsActivity.kt](file:///C:/Users/lokii/Downloads/weather_app/app/src/main/java/com/example/weather_app/SettingsActivity.kt)

- Display current weather (Location, Temp, Condition, Humidity, Wind).
- Refresh button.
- RecyclerView to list saved weather records.
- Edit/Delete functionality for records.
- Settings for temperature unit (Celsius/Fahrenheit) using `SharedPreferences`.
- Share functionality using an implicit `ACTION_SEND` Intent.

---

## Verification Plan

### Automated Tests
- Unit tests for API data mapping.
- Room database DAO tests.

### Manual Verification
1. Launch app -> Redirect to Login/Register if not authenticated.
2. Grant location permission -> Verify current weather is displayed.
3. Save weather record -> Check if it appears in the list.
4. Update/Delete record -> Verify persistence.
5. Change unit in Settings -> Verify UI updates accordingly.
6. Click Share -> Verify the sharing dialog appears with correct info.
