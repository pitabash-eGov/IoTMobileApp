# IoT Solution - Architecture & Design Documentation

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [System Architecture](#2-system-architecture)
3. [Clean Architecture Layers](#3-clean-architecture-layers)
4. [Module Dependency Graph](#4-module-dependency-graph)
5. [Component Diagram](#5-component-diagram)
6. [Data Flow Architecture](#6-data-flow-architecture)
7. [Sequence Diagrams](#7-sequence-diagrams)
8. [Flow Diagrams](#8-flow-diagrams)
9. [Navigation Architecture](#9-navigation-architecture)
10. [Dependency Injection Graph](#10-dependency-injection-graph)
11. [Data Models & Relationships](#11-data-models--relationships)
12. [Network Architecture](#12-network-architecture)
13. [MQTT Real-Time Communication](#13-mqtt-real-time-communication)
14. [Security Architecture](#14-security-architecture)
15. [Background Services](#15-background-services)
16. [UI Component Hierarchy](#16-ui-component-hierarchy)
17. [Error Handling Strategy](#17-error-handling-strategy)
18. [Technology Stack](#18-technology-stack)
19. [Directory Structure](#19-directory-structure)
20. [API Reference](#20-api-reference)

---

## 1. Project Overview

**IoT Solution** is an Android application for monitoring and controlling IoT devices. It provides real-time device management through MQTT messaging, weather integration, and map-based device visualization.

| Attribute         | Value                            |
|-------------------|----------------------------------|
| Package           | `com.foodchain.iotsolution`      |
| Min SDK           | 24 (Android 7.0)                 |
| Target SDK        | 36                               |
| Kotlin            | 2.0.21                           |
| Compose BOM       | 2024.09.00                       |
| Architecture      | Clean Architecture + MVVM        |
| DI Framework      | Hilt (with KSP)                  |
| Build System      | Gradle 8.13 (Kotlin DSL)         |

### Key Features

- User authentication (JWT-based login/signup)
- Device CRUD operations (8 device types supported)
- Real-time device control via MQTT (5 control types)
- Weather data integration (OpenWeatherMap)
- Map-based device visualization (Google Maps)
- Foreground MQTT service with notifications
- Dark theme support
- Configurable MQTT broker

---

## 2. System Architecture

### High-Level System Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        IoT Solution App                             │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    PRESENTATION LAYER                         │  │
│  │  ┌─────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────────┐ │  │
│  │  │ Screens │ │ViewModels│ │UiStates  │ │  Components      │ │  │
│  │  │ (8)     │ │ (8)      │ │ (8)      │ │  (11 reusable)   │ │  │
│  │  └────┬────┘ └────┬─────┘ └──────────┘ └──────────────────┘ │  │
│  └───────┼───────────┼──────────────────────────────────────────┘  │
│          │           │                                              │
│  ┌───────┼───────────┼──────────────────────────────────────────┐  │
│  │       │    DOMAIN LAYER                                      │  │
│  │  ┌────▼────┐ ┌────▼─────┐ ┌──────────────┐                  │  │
│  │  │Use Cases│ │Repository│ │ Domain Models │                  │  │
│  │  │ (12)    │ │Interfaces│ │ (8)           │                  │  │
│  │  │         │ │ (3)      │ │               │                  │  │
│  │  └────┬────┘ └──────────┘ └──────────────┘                  │  │
│  └───────┼──────────────────────────────────────────────────────┘  │
│          │                                                          │
│  ┌───────┼──────────────────────────────────────────────────────┐  │
│  │       │         DATA LAYER                                   │  │
│  │  ┌────▼──────┐ ┌──────────┐ ┌──────────┐ ┌───────────────┐  │  │
│  │  │Repository │ │ Remote   │ │  Local   │ │  Services     │  │  │
│  │  │Impls (3)  │ │ APIs (3) │ │DataStore │ │  MQTT/Notif   │  │  │
│  │  └───────────┘ │ DTOs (7) │ └──────────┘ └───────────────┘  │  │
│  │                │Mappers(2)│                                   │  │
│  │                └──────────┘                                   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              DEPENDENCY INJECTION (Hilt)                      │  │
│  │  AppModule | NetworkModule | RepositoryModule | MqttModule    │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
           │                    │                     │
           ▼                    ▼                     ▼
   ┌──────────────┐   ┌────────────────┐   ┌──────────────────┐
   │  REST API    │   │  MQTT Broker   │   │ OpenWeatherMap   │
   │  Server      │   │  (HiveMQ)      │   │ API              │
   │  (JWT Auth)  │   │                │   │                  │
   └──────────────┘   └────────────────┘   └──────────────────┘
```

### External Dependencies

| Service            | Purpose                  | Protocol  | Auth         |
|--------------------|--------------------------|-----------|--------------|
| REST API Server    | User & device management | HTTPS     | JWT Bearer   |
| MQTT Broker        | Real-time device comms   | TCP/MQTT  | None (configurable) |
| OpenWeatherMap API | Weather data             | HTTPS     | API Key      |
| Google Maps SDK    | Device map visualization | Native SDK| API Key      |

---

## 3. Clean Architecture Layers

### Layer Dependency Rule

```
┌─────────────────────────────────────────────────┐
│              PRESENTATION LAYER                  │
│         (Screens, ViewModels, Components)        │
│                                                  │
│  Depends on: Domain Layer                        │
│  Framework: Jetpack Compose, Hilt ViewModels     │
└──────────────────────┬──────────────────────────┘
                       │ depends on
                       ▼
┌─────────────────────────────────────────────────┐
│                DOMAIN LAYER                      │
│       (Models, Repository Interfaces,            │
│        Use Cases)                                │
│                                                  │
│  Depends on: Nothing (pure Kotlin)               │
│  No framework dependencies                       │
└──────────────────────┬──────────────────────────┘
                       ▲ implements
                       │
┌─────────────────────────────────────────────────┐
│                 DATA LAYER                       │
│      (Repository Impls, APIs, DTOs, Mappers,     │
│       DataStore, MQTT, Services)                 │
│                                                  │
│  Depends on: Domain Layer                        │
│  Framework: Retrofit, Paho MQTT, DataStore       │
└─────────────────────────────────────────────────┘
```

**Key Principle**: Dependencies point **inward**. The domain layer has zero external dependencies. The presentation and data layers depend on the domain layer, never on each other directly.

### Layer Responsibilities

| Layer        | Contents                      | Responsibility                              |
|-------------|-------------------------------|---------------------------------------------|
| Domain       | Models, Interfaces, Use Cases | Business logic, validation, contracts        |
| Data         | Impls, APIs, DTOs, Mappers    | Data access, network, persistence, mapping   |
| Presentation | Screens, ViewModels, UI State | UI rendering, user interaction, state mgmt   |
| DI           | Hilt Modules                  | Wiring dependencies across layers            |

---

## 4. Module Dependency Graph

```
                    ┌─────────────┐
                    │  :app       │
                    │ (Android)   │
                    └──────┬──────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
            ▼              ▼              ▼
    ┌──────────────┐ ┌──────────┐ ┌─────────────┐
    │ presentation │ │   di     │ │    data      │
    │              │ │          │ │              │
    │ - auth/      │ │ - App   │ │ - remote/    │
    │ - home/      │ │ - Net   │ │   - api/     │
    │ - device/    │ │ - Repo  │ │   - dto/     │
    │ - map/       │ │ - Mqtt  │ │   - mqtt/    │
    │ - settings/  │ │         │ │   - intercept│
    │ - splash/    │ │         │ │ - local/     │
    │ - components/│ │         │ │ - mapper/    │
    │ - navigation/│ │         │ │ - repository/│
    └──────┬───────┘ └────┬────┘ │ - service/   │
           │              │      └──────┬───────┘
           │              │             │
           └──────────────┼─────────────┘
                          │
                          ▼
                   ┌─────────────┐
                   │   domain    │
                   │             │
                   │ - model/    │
                   │ - repository│
                   │ - usecase/  │
                   └──────┬──────┘
                          │
                          ▼
                   ┌─────────────┐
                   │    core     │
                   │             │
                   │ - constants │
                   │ - util      │
                   └─────────────┘
```

---

## 5. Component Diagram

### MVVM Pattern per Screen

```
┌─────────────────────────────────────────────────────────┐
│                    Screen (Composable)                    │
│                                                          │
│  Observes UiState via collectAsStateWithLifecycle()      │
│  Calls ViewModel methods on user interaction             │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│                    ViewModel (@HiltViewModel)             │
│                                                          │
│  ┌──────────────┐  ┌──────────────────────────────────┐ │
│  │ _uiState     │  │  Use Case Invocations            │ │
│  │ MutableState │  │                                   │ │
│  │ Flow         │──│  collect { resource ->            │ │
│  │              │  │    when(resource) {               │ │
│  │  Exposed as  │  │      Loading -> show spinner      │ │
│  │  StateFlow   │  │      Success -> update state      │ │
│  └──────────────┘  │      Error   -> show error        │ │
│                    │    }                               │ │
│                    │  }                                 │ │
│                    └──────────────────────────────────┘ │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│                    Use Case                              │
│                                                          │
│  - Input validation                                      │
│  - Business rule enforcement                             │
│  - Emits Flow<Resource<T>>                               │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│                    Repository Implementation              │
│                                                          │
│  - API calls (Retrofit)                                  │
│  - MQTT operations                                       │
│  - Local storage (DataStore)                             │
│  - DTO → Domain mapping                                  │
└─────────────────────────────────────────────────────────┘
```

### All Screens and Their ViewModels

| Screen             | ViewModel             | Use Cases Used                                        |
|--------------------|-----------------------|-------------------------------------------------------|
| SplashScreen       | SplashViewModel       | GetAuthStateUseCase                                   |
| LoginScreen        | LoginViewModel        | LoginUseCase                                          |
| SignUpScreen       | SignUpViewModel       | SignUpUseCase                                         |
| HomeScreen         | HomeViewModel         | GetAllDevicesUseCase, GetWeatherUseCase               |
| DeviceListScreen   | DeviceListViewModel   | GetAllDevicesUseCase, DeleteDeviceUseCase             |
| DeviceDetailScreen | DeviceDetailViewModel | GetDeviceByIdUseCase, ControlDeviceUseCase, ObserveDeviceUpdatesUseCase |
| AddDeviceScreen    | AddDeviceViewModel    | AddDeviceUseCase                                      |
| MapScreen          | MapViewModel          | GetAllDevicesUseCase                                  |
| SettingsScreen     | SettingsViewModel     | LogoutUseCase                                         |

---

## 6. Data Flow Architecture

### Unidirectional Data Flow (UDF)

```
    ┌──────────────────────────────────────────────────┐
    │                                                   │
    │   ┌──────────┐    State     ┌──────────────────┐ │
    │   │ViewModel │─────────────▶│    UI (Screen)   │ │
    │   │          │  (StateFlow) │                   │ │
    │   │          │              │                   │ │
    │   │          │◀─────────────│                   │ │
    │   └────┬─────┘   Events    └──────────────────┘ │
    │        │        (method calls)                    │
    │        │                                          │
    │        ▼                                          │
    │   ┌──────────┐                                   │
    │   │ Use Case │                                   │
    │   └────┬─────┘                                   │
    │        │                                          │
    │        ▼                                          │
    │   ┌──────────────┐                               │
    │   │  Repository   │                               │
    │   │  (API/MQTT/   │                               │
    │   │   DataStore)  │                               │
    │   └──────────────┘                               │
    │                                                   │
    └──────────────────────────────────────────────────┘

    Legend:
    ──────▶  Data flows down (state)
    ◀──────  Events flow up (user actions)
```

### Resource Wrapper Pattern

```
sealed class Resource<T>
    │
    ├── Loading(data: T? = null)     →  Show progress indicator
    │
    ├── Success(data: T)             →  Display data, hide loading
    │
    └── Error(message: String,       →  Show error message
              data: T? = null)           (optionally show stale data)
```

---

## 7. Sequence Diagrams

### 7.1 Authentication Flow (Login)

```
┌──────┐     ┌───────────┐    ┌──────────┐   ┌──────────────┐   ┌────────┐   ┌───────────┐
│ User │     │LoginScreen│    │LoginVM   │   │LoginUseCase  │   │AuthRepo│   │  AuthApi  │
└──┬───┘     └─────┬─────┘    └────┬─────┘   └──────┬───────┘   └───┬────┘   └─────┬─────┘
   │               │               │                 │               │              │
   │  Enter email  │               │                 │               │              │
   │──────────────▶│               │                 │               │              │
   │               │onEmailChange()│                 │               │              │
   │               │──────────────▶│                 │               │              │
   │               │               │                 │               │              │
   │  Enter pass   │               │                 │               │              │
   │──────────────▶│               │                 │               │              │
   │               │onPassChange() │                 │               │              │
   │               │──────────────▶│                 │               │              │
   │               │               │                 │               │              │
   │  Tap Login    │               │                 │               │              │
   │──────────────▶│               │                 │               │              │
   │               │  login()      │                 │               │              │
   │               │──────────────▶│                 │               │              │
   │               │               │  invoke(email,  │               │              │
   │               │               │  password)      │               │              │
   │               │               │────────────────▶│               │              │
   │               │               │                 │               │              │
   │               │               │  emit Loading   │               │              │
   │               │               │◀────────────────│               │              │
   │               │  UiState      │                 │               │              │
   │               │  isLoading=T  │                 │  Validate     │              │
   │               │◀──────────────│                 │  email/pass   │              │
   │  Show spinner │               │                 │  ─────────    │              │
   │◀──────────────│               │                 │               │              │
   │               │               │                 │  login(e,p)   │              │
   │               │               │                 │──────────────▶│              │
   │               │               │                 │               │ POST /login  │
   │               │               │                 │               │─────────────▶│
   │               │               │                 │               │              │
   │               │               │                 │               │ AuthResponse │
   │               │               │                 │               │  (JWT+User)  │
   │               │               │                 │               │◀─────────────│
   │               │               │                 │               │              │
   │               │               │                 │               │──┐           │
   │               │               │                 │               │  │Save tokens│
   │               │               │                 │               │  │to DataStore│
   │               │               │                 │               │◀─┘           │
   │               │               │                 │               │              │
   │               │               │                 │  Resource     │              │
   │               │               │                 │  .Success     │              │
   │               │               │                 │◀──────────────│              │
   │               │               │  emit Success   │               │              │
   │               │               │◀────────────────│               │              │
   │               │  UiState      │                 │               │              │
   │               │  isSuccess=T  │                 │               │              │
   │               │◀──────────────│                 │               │              │
   │  Navigate to  │               │                 │               │              │
   │  HomeScreen   │               │                 │               │              │
   │◀──────────────│               │                 │               │              │
   │               │               │                 │               │              │
```

### 7.2 Device Control Flow (MQTT)

```
┌──────┐  ┌────────────┐  ┌──────────┐  ┌───────────┐  ┌──────────┐  ┌───────────┐  ┌──────────┐
│ User │  │DetailScreen│  │DetailVM  │  │ControlUC  │  │DeviceRepo│  │MqttManager│  │MQTT Broker│
└──┬───┘  └─────┬──────┘  └────┬─────┘  └─────┬─────┘  └────┬─────┘  └─────┬─────┘  └────┬─────┘
   │            │              │               │             │              │              │
   │  Toggle    │              │               │             │              │              │
   │  switch    │              │               │             │              │              │
   │───────────▶│              │               │             │              │              │
   │            │sendControl   │               │             │              │              │
   │            │(id, value)   │               │             │              │              │
   │            │─────────────▶│               │             │              │              │
   │            │              │               │             │              │              │
   │            │              │──┐ Optimistic │             │              │              │
   │            │              │  │ UI update  │             │              │              │
   │            │              │◀─┘            │             │              │              │
   │            │              │               │             │              │              │
   │  UI updates│              │  invoke       │             │              │              │
   │  instantly │              │  (deviceId,   │             │              │              │
   │◀───────────│              │   controlId,  │             │              │              │
   │            │              │   value)      │             │              │              │
   │            │              │──────────────▶│             │              │              │
   │            │              │               │sendControl  │              │              │
   │            │              │               │Command()    │              │              │
   │            │              │               │────────────▶│              │              │
   │            │              │               │             │ publish()    │              │
   │            │              │               │             │─────────────▶│              │
   │            │              │               │             │              │  PUBLISH      │
   │            │              │               │             │              │  devices/     │
   │            │              │               │             │              │  {id}/control │
   │            │              │               │             │              │─────────────▶│
   │            │              │               │             │              │              │
   │            │              │               │             │              │              │
   │            │  ═══════ MQTT Subscription (Background) ══════════════  │              │
   │            │              │               │             │              │              │
   │            │              │               │             │              │  PUBLISH      │
   │            │              │               │             │              │  devices/     │
   │            │              │               │             │              │  {id}/status  │
   │            │              │               │             │              │◀─────────────│
   │            │              │               │             │              │              │
   │            │              │               │             │ SharedFlow   │              │
   │            │              │               │             │ MqttMessage  │              │
   │            │              │               │             │◀─────────────│              │
   │            │              │               │             │              │              │
   │            │              │  observeDevice│             │              │              │
   │            │              │  Updates()    │             │              │              │
   │            │              │  ◀════════════│═════════════│              │              │
   │            │              │               │             │              │              │
   │            │              │──┐ Parse      │             │              │              │
   │            │              │  │ "ctrlId:val"│            │              │              │
   │            │              │  │ Update      │            │              │              │
   │            │              │  │ control    │             │              │              │
   │            │              │◀─┘            │             │              │              │
   │            │  UiState     │               │             │              │              │
   │            │  updated     │               │             │              │              │
   │            │◀─────────────│               │             │              │              │
   │  Control   │              │               │             │              │              │
   │  confirmed │              │               │             │              │              │
   │◀───────────│              │               │             │              │              │
```

### 7.3 App Startup & Splash Flow

```
┌─────────┐  ┌────────────┐  ┌──────────┐  ┌──────────────┐  ┌───────────────┐
│App Start│  │SplashScreen│  │SplashVM  │  │GetAuthStateUC│  │DataStoreManager│
└────┬────┘  └─────┬──────┘  └────┬─────┘  └──────┬───────┘  └───────┬───────┘
     │             │              │                │                  │
     │  onCreate   │              │                │                  │
     │────────────▶│              │                │                  │
     │             │  init{}      │                │                  │
     │             │─────────────▶│                │                  │
     │             │              │  invoke()      │                  │
     │             │              │───────────────▶│                  │
     │             │              │                │  getAuthToken()  │
     │             │              │                │─────────────────▶│
     │             │              │                │                  │
     │             │              │                │  Flow<String?>   │
     │             │              │                │◀─────────────────│
     │             │              │                │                  │
     │             │              │                │──┐ map to        │
     │             │              │                │  │ !token         │
     │             │              │                │  │ .isNullOrEmpty │
     │             │              │                │◀─┘               │
     │             │              │                │                  │
     │             │              │  Flow<Boolean> │                  │
     │             │              │◀───────────────│                  │
     │             │              │                │                  │
     │             │  isAuth=true │                │                  │
     │             │◀─────────────│                │                  │
     │             │              │                │                  │
     │             │  navigate    │                │                  │
     │             │  (Home)      │                │                  │
     │             │──────────────────────────▶ NavController         │
     │             │              │                │                  │
     │                            │                │                  │
     │  ── OR (if not auth) ──   │                │                  │
     │             │              │                │                  │
     │             │  isAuth=false│                │                  │
     │             │◀─────────────│                │                  │
     │             │              │                │                  │
     │             │  navigate    │                │                  │
     │             │  (Login)     │                │                  │
     │             │──────────────────────────▶ NavController         │
```

### 7.4 Weather Data Loading

```
┌──────────┐  ┌─────────┐  ┌─────────────┐  ┌────────────┐  ┌──────────────┐  ┌────────────────┐
│HomeScreen│  │HomeVM   │  │GetWeatherUC │  │WeatherRepo │  │WeatherApi    │  │OpenWeatherMap  │
└────┬─────┘  └────┬────┘  └──────┬──────┘  └─────┬──────┘  └──────┬───────┘  └───────┬────────┘
     │             │              │                │               │                   │
     │  Compose    │              │                │               │                   │
     │  init       │              │                │               │                   │
     │────────────▶│              │                │               │                   │
     │             │  loadWeather │                │               │                   │
     │             │  (lat, lon)  │                │               │                   │
     │             │──┐           │                │               │                   │
     │             │  │Get last   │                │               │                   │
     │             │  │location   │                │               │                   │
     │             │  │from       │                │               │                   │
     │             │  │DataStore  │                │               │                   │
     │             │◀─┘           │                │               │                   │
     │             │              │                │               │                   │
     │             │  invoke(     │                │               │                   │
     │             │  lat, lon)   │                │               │                   │
     │             │─────────────▶│                │               │                   │
     │             │              │  emit Loading  │               │                   │
     │             │◀─────────────│                │               │                   │
     │  Show       │              │                │               │                   │
     │  spinner    │              │  getWeather()  │               │                   │
     │◀────────────│              │───────────────▶│               │                   │
     │             │              │                │  GET /weather │                   │
     │             │              │                │  ?lat&lon     │                   │
     │             │              │                │  &appid       │                   │
     │             │              │                │──────────────▶│                   │
     │             │              │                │               │  HTTP GET         │
     │             │              │                │               │──────────────────▶│
     │             │              │                │               │                   │
     │             │              │                │               │  WeatherResponse  │
     │             │              │                │               │◀──────────────────│
     │             │              │                │               │                   │
     │             │              │                │  Weather      │                   │
     │             │              │                │  Response     │                   │
     │             │              │                │◀──────────────│                   │
     │             │              │                │               │                   │
     │             │              │                │──┐            │                   │
     │             │              │                │  │ Map via     │                   │
     │             │              │                │  │ Weather     │                   │
     │             │              │                │  │ Mapper      │                   │
     │             │              │                │◀─┘            │                   │
     │             │              │                │               │                   │
     │             │              │  emit Success  │               │                   │
     │             │              │  (WeatherData) │               │                   │
     │             │              │◀───────────────│               │                   │
     │             │              │                │               │                   │
     │             │  UiState     │                │               │                   │
     │             │  weather=data│                │               │                   │
     │◀────────────│              │                │               │                   │
     │             │              │                │               │                   │
     │  Show       │              │                │               │                   │
     │  WeatherWidget             │                │               │                   │
     │◀────────────│              │                │               │                   │
```

---

## 8. Flow Diagrams

### 8.1 Application Startup Flow

```
                        ┌─────────────────┐
                        │   App Launch     │
                        └────────┬────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │ IoTSolutionApp  │
                        │ @HiltAndroidApp │
                        └────────┬────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │  MainActivity   │
                        │  @AndroidEntry  │
                        └────────┬────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │  Apply Theme    │
                        │  (Dark/Light)   │
                        └────────┬────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │    NavGraph     │
                        │  (startDest =   │
                        │   Splash)       │
                        └────────┬────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │  SplashScreen   │
                        └────────┬────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │  Check Auth     │
                        │  Token in       │
                        │  DataStore      │
                        └────────┬────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
           ┌────────────────┐       ┌────────────────┐
           │  Token EXISTS  │       │  No Token      │
           └───────┬────────┘       └───────┬────────┘
                   │                        │
                   ▼                        ▼
           ┌────────────────┐       ┌────────────────┐
           │  HomeScreen    │       │  LoginScreen   │
           │  + Start MQTT  │       │                │
           │    Service     │       │                │
           └────────────────┘       └────────────────┘
```

### 8.2 User Registration & Login Flow

```
                    ┌──────────────┐
                    │  LoginScreen │
                    └──────┬───────┘
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼                         ▼
     ┌────────────────┐        ┌────────────────┐
     │  Enter Email & │        │  Navigate to   │
     │  Password      │        │  SignUpScreen   │
     └───────┬────────┘        └───────┬────────┘
             │                         │
             ▼                         ▼
     ┌────────────────┐        ┌────────────────┐
     │  Tap "Login"   │        │  Enter Name,   │
     └───────┬────────┘        │  Email, Pass,  │
             │                 │  Confirm Pass  │
             ▼                 └───────┬────────┘
     ┌────────────────┐                │
     │  Validate      │                ▼
     │  ┌────────────┐│        ┌────────────────┐
     │  │Email valid? ││        │  Tap "Sign Up" │
     │  │Pass >= 6?  ││        └───────┬────────┘
     │  └────────────┘│                │
     └───────┬────────┘                ▼
             │                 ┌────────────────┐
        ┌────┴────┐            │  Validate All  │
        │         │            │  Fields Match  │
        ▼         ▼            └───────┬────────┘
   ┌────────┐ ┌────────┐              │
   │ Valid  │ │Invalid │         ┌────┴────┐
   └───┬────┘ └───┬────┘         │         │
       │          │               ▼         ▼
       │          ▼          ┌────────┐ ┌────────┐
       │    ┌──────────┐     │ Valid  │ │Invalid │
       │    │Show Error│     └───┬────┘ └───┬────┘
       │    └──────────┘         │          │
       │                         │          ▼
       ▼                         │    ┌──────────┐
  ┌────────────────┐             │    │Show Error│
  │  API Request   │             │    └──────────┘
  │  POST /login   │             │
  └───────┬────────┘             ▼
          │                ┌────────────────┐
     ┌────┴────┐           │  API Request   │
     │         │           │  POST /register│
     ▼         ▼           └───────┬────────┘
┌─────────┐ ┌─────────┐          │
│ Success │ │  Error  │     ┌────┴────┐
└────┬────┘ └────┬────┘     │         │
     │           │          ▼         ▼
     │           ▼     ┌─────────┐ ┌─────────┐
     │     ┌──────────┐│ Success │ │  Error  │
     │     │Show Error││         │ └────┬────┘
     │     │Message   │└────┬────┘      │
     │     └──────────┘     │           ▼
     │                      │     ┌──────────┐
     ▼                      │     │Show Error│
┌────────────────┐          │     └──────────┘
│  Save JWT to   │          │
│  DataStore     │◀─────────┘
└───────┬────────┘
        │
        ▼
┌────────────────┐
│  Navigate to   │
│  HomeScreen    │
└────────────────┘
```

### 8.3 Device Management Flow

```
                         ┌──────────────────┐
                         │   HomeScreen     │
                         └────────┬─────────┘
                                  │
                                  ▼
                    ┌──────────────────────────┐
                    │  Navigate to DeviceList  │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │   DeviceListScreen       │
                    │   ┌────────────────────┐ │
                    │   │ Search / Filter    │ │
                    │   └────────────────────┘ │
                    └────────────┬─────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                   │
              ▼                  ▼                   ▼
     ┌────────────────┐ ┌────────────────┐  ┌────────────────┐
     │  Tap Device    │ │  Tap Add (+)   │  │  Swipe Delete  │
     └───────┬────────┘ └───────┬────────┘  └───────┬────────┘
             │                  │                    │
             ▼                  ▼                    ▼
     ┌────────────────┐ ┌────────────────┐  ┌────────────────┐
     │DeviceDetail    │ │ AddDevice      │  │  DELETE API    │
     │Screen          │ │ Screen         │  │  /devices/{id} │
     └───────┬────────┘ └───────┬────────┘  └───────┬────────┘
             │                  │                    │
             ▼                  ▼                    ▼
     ┌────────────────┐ ┌────────────────┐  ┌────────────────┐
     │ View device    │ │ Fill form:     │  │ Remove from    │
     │ info &         │ │ - Name         │  │ list           │
     │ controls       │ │ - Type         │  └────────────────┘
     └───────┬────────┘ │ - Location     │
             │          │ - Controls     │
             ▼          └───────┬────────┘
     ┌────────────────┐         │
     │ Interact with  │         ▼
     │ controls:      │ ┌────────────────┐
     │ - Toggle       │ │  POST /devices │
     │ - Slider       │ │  Create device │
     │ - Button       │ └───────┬────────┘
     │ - Dropdown     │         │
     │ - Color picker │         ▼
     └───────┬────────┘ ┌────────────────┐
             │          │ Navigate back  │
             ▼          │ to DeviceList  │
     ┌────────────────┐ └────────────────┘
     │ MQTT Publish   │
     │ devices/{id}/  │
     │ control        │
     └───────┬────────┘
             │
             ▼
     ┌────────────────┐
     │ MQTT Subscribe │
     │ for device     │
     │ status updates │
     └────────────────┘
```

### 8.4 MQTT Connection Lifecycle

```
                    ┌──────────────────┐
                    │  App Starts /    │
                    │  Settings Change │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │  Read broker URL │
                    │  from DataStore  │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │  MqttManager     │
                    │  .connect()      │
                    │                  │
                    │  State:          │
                    │  CONNECTING      │
                    └────────┬─────────┘
                             │
                    ┌────────┴────────┐
                    │                 │
                    ▼                 ▼
           ┌────────────────┐ ┌────────────────┐
           │  Connected     │ │  Failed        │
           │                │ │                │
           │  State:        │ │  State:        │
           │  CONNECTED     │ │  ERROR         │
           └───────┬────────┘ └───────┬────────┘
                   │                  │
                   ▼                  ▼
           ┌────────────────┐ ┌────────────────┐
           │  Subscribe to  │ │  Auto-Retry    │
           │  device topics │ │  (automatic    │
           │                │ │   reconnect)   │
           └───────┬────────┘ └───────┬────────┘
                   │                  │
                   ▼                  │
           ┌────────────────┐         │
           │  Receive MQTT  │         │
           │  Messages      │◀────────┘
           │                │  (on reconnect, resubscribe)
           │  SharedFlow    │
           │  emission      │
           └───────┬────────┘
                   │
              ┌────┴────┐
              │         │
              ▼         ▼
     ┌────────────┐ ┌──────────────┐
     │ Device     │ │ Notification │
     │ Detail     │ │ via          │
     │ Screen     │ │ Foreground   │
     │ (UI update)│ │ Service      │
     └────────────┘ └──────────────┘
```

### 8.5 JWT Token Lifecycle

```
                    ┌──────────────────┐
                    │  Login / SignUp  │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │  Receive JWT     │
                    │  + Refresh Token │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │  Store in        │
                    │  DataStore       │
                    │  (encrypted)     │
                    └────────┬─────────┘
                             │
                             ▼
               ┌─────────────────────────────┐
               │  Every API Request          │
               │  (via AuthInterceptor)      │
               │                             │
               │  ┌───────────────────────┐  │
               │  │ Read token from       │  │
               │  │ DataStore             │  │
               │  │ (runBlocking)         │  │
               │  └───────────┬───────────┘  │
               │              │              │
               │              ▼              │
               │  ┌───────────────────────┐  │
               │  │ Add header:           │  │
               │  │ Authorization:        │  │
               │  │ Bearer <token>        │  │
               │  └───────────────────────┘  │
               └─────────────────────────────┘
                             │
                             ▼
               ┌─────────────────────────────┐
               │         On Logout           │
               │                             │
               │  1. Clear DataStore tokens  │
               │  2. Disconnect MQTT         │
               │  3. Stop Foreground Service │
               │  4. Navigate to Login       │
               └─────────────────────────────┘
```

---

## 9. Navigation Architecture

### Navigation Graph

```
                         ┌─────────────────┐
                         │    NavHost       │
                         │  startDest =     │
                         │  Splash          │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │   SplashScreen  │
                         └────────┬────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
                    ▼                           ▼
         ┌──────────────────┐        ┌──────────────────┐
         │   AUTH GRAPH     │        │   MAIN GRAPH     │
         │   (nested)       │        │   (nested)       │
         │                  │        │                  │
         │  ┌────────────┐  │        │  ┌────────────┐  │
         │  │LoginScreen │◀─┼────────┼──│ HomeScreen │  │
         │  └─────┬──────┘  │        │  └─────┬──────┘  │
         │        │         │        │        │         │
         │        ▼         │        │   ┌────┼────┬────┼──────┐
         │  ┌────────────┐  │        │   │    │    │    │      │
         │  │SignUpScreen│  │        │   ▼    │    ▼    │      ▼
         │  └────────────┘  │        │ ┌────┐ │ ┌─────┐│ ┌──────────┐
         └──────────────────┘        │ │List│ │ │Map  ││ │Settings  │
                                     │ └─┬──┘ │ └──┬──┘│ └─────┬────┘
                                     │   │    │    │   │       │
                                     │   ▼    │    │   │       ▼
                                     │ ┌────┐ │    │   │  ┌──────────┐
                                     │ │Add │ │    │   │  │ Logout → │
                                     │ │Dev.│ │    │   │  │ Login    │
                                     │ └────┘ │    │   │  └──────────┘
                                     │        │    │   │
                                     │        ▼    ▼   │
                                     │   ┌────────────┐│
                                     │   │DeviceDetail││
                                     │   │?deviceId   ││
                                     │   └────────────┘│
                                     └─────────────────┘
```

### Screen Routes

| Screen       | Route                    | Arguments     | Accessible From           |
|-------------|--------------------------|---------------|---------------------------|
| Splash       | `splash`                 | None          | App start                 |
| Login        | `login`                  | None          | Splash, SignUp            |
| SignUp       | `signup`                 | None          | Login                     |
| Home         | `home`                   | None          | Splash, Login, SignUp, BottomNav |
| DeviceList   | `device_list`            | None          | Home, BottomNav           |
| DeviceDetail | `device_detail/{deviceId}` | deviceId: String | DeviceList, Home, Map  |
| AddDevice    | `add_device`             | None          | DeviceList                |
| Map          | `map`                    | None          | Home, BottomNav           |
| Settings     | `settings`               | None          | Home, BottomNav           |

### Bottom Navigation Bar

```
┌─────────────────────────────────────────────────────┐
│                                                      │
│   🏠 Home    📱 Devices    🗺️ Map    ⚙️ Settings    │
│                                                      │
└─────────────────────────────────────────────────────┘
```

---

## 10. Dependency Injection Graph

### Hilt Module Hierarchy

```
┌──────────────────────────────────────────────────────────────┐
│                 @HiltAndroidApp                               │
│                 IoTSolutionApp                                │
└───────────────────────┬──────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┬───────────────┐
        │               │               │               │
        ▼               ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────┐
│  AppModule   │ │NetworkModule │ │ RepoModule   │ │MqttModule│
│  @InstallIn  │ │ @InstallIn   │ │ @InstallIn   │ │@InstallIn│
│ (Singleton)  │ │ (Singleton)  │ │ (Singleton)  │ │(Singleton│
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘ └────┬─────┘
       │                │               │               │
       │ @Provides      │ @Provides     │ @Binds        │@Provides
       │                │               │               │
       ▼                ▼               ▼               ▼
┌─────────────┐  ┌─────────────┐ ┌─────────────┐ ┌──────────┐
│ Gson        │  │ OkHttp (2x) │ │ AuthRepo    │ │MqttManager│
│ DataStore   │  │ Retrofit(2x)│ │ DeviceRepo  │ └──────────┘
│ Manager     │  │ AuthApi     │ │ WeatherRepo │
└─────────────┘  │ DeviceApi   │ └─────────────┘
                 │ WeatherApi  │
                 └─────────────┘
```

### Detailed Injection Map

```
NetworkModule
├── @Named("AuthOkHttpClient")     ← AuthInterceptor + Logging
├── @Named("WeatherOkHttpClient")  ← Logging only
├── @Named("MainRetrofit")         ← AuthOkHttpClient + BASE_URL
├── @Named("WeatherRetrofit")      ← WeatherOkHttpClient + WEATHER_BASE_URL
├── AuthApi                        ← MainRetrofit
├── DeviceApi                      ← MainRetrofit
└── WeatherApi                     ← WeatherRetrofit

RepositoryModule
├── AuthRepository      ← AuthRepositoryImpl(AuthApi, DataStoreManager)
├── DeviceRepository    ← DeviceRepositoryImpl(DeviceApi, MqttManager)
└── WeatherRepository   ← WeatherRepositoryImpl(WeatherApi)

ViewModels (auto-injected via @HiltViewModel)
├── SplashViewModel     ← GetAuthStateUseCase
├── LoginViewModel      ← LoginUseCase
├── SignUpViewModel     ← SignUpUseCase
├── HomeViewModel       ← GetAllDevicesUseCase, GetWeatherUseCase,
│                         DataStoreManager, MqttManager
├── DeviceListViewModel ← GetAllDevicesUseCase, DeleteDeviceUseCase
├── DeviceDetailVM      ← GetDeviceByIdUseCase, ControlDeviceUseCase,
│                         ObserveDeviceUpdatesUseCase
├── AddDeviceViewModel  ← AddDeviceUseCase
├── MapViewModel        ← GetAllDevicesUseCase, DataStoreManager
└── SettingsViewModel   ← LogoutUseCase, DataStoreManager, MqttManager
```

---

## 11. Data Models & Relationships

### Entity Relationship Diagram

```
┌──────────────────────────┐       ┌──────────────────────────┐
│         User             │       │        Device            │
├──────────────────────────┤       ├──────────────────────────┤
│ id: String               │       │ id: String               │
│ email: String            │  owns │ name: String             │
│ name: String             │──────▶│ type: DeviceType         │
│ profileImageUrl: String? │  1:N  │ isOnline: Boolean        │
└──────────────────────────┘       │ mqttTopicPrefix: String  │
                                   │ createdAt: Long          │
                                   │ updatedAt: Long          │
                                   ├──────────────────────────┤
                                   │ location: DeviceLocation │──┐
                                   │ controls: List<Control>  │──┼──┐
                                   └──────────────────────────┘  │  │
                                                                  │  │
                            ┌─────────────────────────────────────┘  │
                            │                                        │
                            ▼                                        │
                   ┌──────────────────────┐                         │
                   │   DeviceLocation     │                         │
                   ├──────────────────────┤                         │
                   │ latitude: Double     │                         │
                   │ longitude: Double    │                         │
                   │ address: String      │                         │
                   └──────────────────────┘                         │
                                                                     │
                            ┌────────────────────────────────────────┘
                            │
                            ▼
                   ┌──────────────────────────┐
                   │    DeviceControl         │
                   ├──────────────────────────┤
                   │ id: String               │
                   │ name: String             │
                   │ type: ControlType        │
                   │ currentValue: String     │
                   │ minValue: Double?        │
                   │ maxValue: Double?        │
                   │ step: Double?            │
                   │ options: List<String>?   │
                   └──────────────────────────┘
```

### Enumerations

```
┌────────────────────┐         ┌────────────────────┐
│    DeviceType      │         │    ControlType     │
├────────────────────┤         ├────────────────────┤
│ LIGHT              │         │ TOGGLE             │
│ THERMOSTAT         │         │ SLIDER             │
│ SWITCH             │         │ BUTTON             │
│ SENSOR             │         │ DROPDOWN           │
│ CAMERA             │         │ COLOR_PICKER       │
│ LOCK               │         └────────────────────┘
│ FAN                │
│ CUSTOM             │
└────────────────────┘
```

### DTO to Domain Mapping

```
┌─────────────────┐                    ┌─────────────────┐
│  Data Layer     │   DeviceMapper     │  Domain Layer   │
│  (DTOs)         │───────────────────▶│  (Models)       │
├─────────────────┤                    ├─────────────────┤
│ DeviceDto       │ ──── toDomain() ──▶│ Device          │
│ DeviceLocation  │ ──── toDomain() ──▶│ DeviceLocation  │
│   Dto           │                    │                 │
│ DeviceControl   │ ──── toDomain() ──▶│ DeviceControl   │
│   Dto           │                    │                 │
│ UserDto         │ ──── toDomain() ──▶│ User            │
└─────────────────┘                    └─────────────────┘

┌─────────────────┐                    ┌─────────────────┐
│ WeatherResponse │  WeatherMapper     │  WeatherData    │
├─────────────────┤───────────────────▶├─────────────────┤
│ main.temp       │                    │ temperature     │
│ main.humidity   │                    │ humidity        │
│ weather[0].desc │                    │ description     │
│ weather[0].icon │                    │ iconUrl         │
│ wind.speed      │                    │ windSpeed       │
│ name            │                    │ cityName        │
│ sys.country     │                    │ country         │
└─────────────────┘                    └─────────────────┘
```

---

## 12. Network Architecture

### Dual Retrofit Configuration

```
┌──────────────────────────────────────────────────────────────────┐
│                     OkHttp Layer                                  │
│                                                                   │
│  ┌───────────────────────────┐  ┌───────────────────────────┐   │
│  │  Auth OkHttpClient        │  │  Weather OkHttpClient      │   │
│  │                           │  │                            │   │
│  │  ┌─────────────────────┐  │  │  ┌─────────────────────┐  │   │
│  │  │  AuthInterceptor    │  │  │  │  Logging Interceptor │  │   │
│  │  │  (JWT Bearer token) │  │  │  │  (BODY level)       │  │   │
│  │  └─────────────────────┘  │  │  └─────────────────────┘  │   │
│  │  ┌─────────────────────┐  │  │                            │   │
│  │  │  Logging Interceptor │  │  │  Timeout: 30s (all)      │   │
│  │  │  (BODY level)       │  │  └───────────────────────────┘   │
│  │  └─────────────────────┘  │                                   │
│  │                           │                                   │
│  │  Timeout: 30s (all)      │                                   │
│  └───────────────────────────┘                                   │
└──────────────────────────────────────────────────────────────────┘
                    │                              │
                    ▼                              ▼
┌───────────────────────────────┐  ┌───────────────────────────────┐
│   Main Retrofit               │  │   Weather Retrofit             │
│   baseUrl: BuildConfig        │  │   baseUrl: api.openweathermap │
│            .BASE_URL          │  │            .org                │
│                               │  │                               │
│  ┌───────────┐ ┌───────────┐  │  │  ┌────────────┐              │
│  │ AuthApi   │ │ DeviceApi │  │  │  │ WeatherApi │              │
│  └───────────┘ └───────────┘  │  │  └────────────┘              │
└───────────────────────────────┘  └───────────────────────────────┘
```

### Request/Response Flow

```
┌─────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│ UseCase │────▶│  Repository  │────▶│  Retrofit    │────▶│  Server  │
│         │     │  Impl        │     │  API Call    │     │          │
└─────────┘     └──────────────┘     └──────────────┘     └──────────┘
                                                                │
                                                                ▼
┌─────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│Resource │◀────│  Mapper      │◀────│  DTO         │◀────│  JSON    │
│<Domain> │     │  .toDomain() │     │  (Gson)      │     │ Response │
└─────────┘     └──────────────┘     └──────────────┘     └──────────┘
```

---

## 13. MQTT Real-Time Communication

### MQTT Topic Structure

```
devices/
├── {deviceId}/
│   ├── status        ← Device online/offline status
│   ├── control       ← Commands sent TO the device
│   └── telemetry     ← Sensor data FROM the device
```

| Topic Pattern                  | Direction       | QoS | Purpose                       |
|-------------------------------|-----------------|-----|-------------------------------|
| `devices/{id}/status`          | Broker → App    | 1   | Device online/offline updates |
| `devices/{id}/control`         | App → Broker    | 1   | Send control commands         |
| `devices/{id}/telemetry`       | Broker → App    | 1   | Receive sensor readings       |

### MQTT Message Flow Architecture

```
                    ┌─────────────────────────────────────┐
                    │           MQTT Broker                │
                    │          (HiveMQ / Custom)           │
                    └──────┬────────────────┬──────────────┘
                           │                │
                   SUBSCRIBE           PUBLISH
                   (status,            (control)
                    telemetry)
                           │                │
                           ▼                │
                    ┌──────────────────┐    │
                    │   MqttManager    │◀───┘
                    │   (Singleton)    │
                    │                  │
                    │ ┌──────────────┐ │
                    │ │ MqttClient   │ │
                    │ │ (Paho)       │ │
                    │ └──────────────┘ │
                    │                  │
                    │ ┌──────────────┐ │
                    │ │ SharedFlow   │ │──── MqttMessage emissions
                    │ │ <MqttMessage>│ │
                    │ └──────────────┘ │
                    │                  │
                    │ ┌──────────────┐ │
                    │ │ StateFlow    │ │──── ConnectionState
                    │ │ <ConnState>  │ │
                    │ └──────────────┘ │
                    └────────┬─────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
                ▼            ▼            ▼
        ┌────────────┐ ┌──────────┐ ┌──────────────┐
        │DeviceDetail│ │ Home     │ │MqttForeground│
        │ViewModel   │ │ViewModel│ │Service       │
        │            │ │          │ │              │
        │(filter by  │ │(conn.   │ │(notification │
        │ deviceId)  │ │ state)  │ │ updates)     │
        └────────────┘ └──────────┘ └──────────────┘
```

### MqttManager State Machine

```
                     ┌────────────┐
                     │DISCONNECTED│◀──────────────────────┐
                     └─────┬──────┘                       │
                           │                              │
                    connect()                      disconnect()
                           │                              │
                           ▼                              │
                     ┌────────────┐                       │
                     │ CONNECTING │                       │
                     └─────┬──────┘                       │
                           │                              │
              ┌────────────┴────────────┐                 │
              │                         │                 │
         onSuccess                  onFailure             │
              │                         │                 │
              ▼                         ▼                 │
       ┌────────────┐            ┌────────────┐          │
       │ CONNECTED  │            │   ERROR    │──────────┘
       └─────┬──────┘            └─────┬──────┘
             │                         │
        connectionLost            auto-reconnect
             │                    (built-in Paho)
             ▼                         │
       ┌────────────┐                  │
       │DISCONNECTED│──────────────────┘
       └────────────┘
```

---

## 14. Security Architecture

### Authentication Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Security Layer                             │
│                                                              │
│   ┌──────────────┐     ┌──────────────┐     ┌────────────┐  │
│   │  Login /     │     │  JWT Token   │     │  DataStore │  │
│   │  Sign Up     │────▶│  Received    │────▶│  Storage   │  │
│   └──────────────┘     └──────────────┘     └──────┬─────┘  │
│                                                     │        │
│                                                     │        │
│   ┌──────────────┐     ┌──────────────┐            │        │
│   │  API Request │◀────│  Auth        │◀───────────┘        │
│   │  with Bearer │     │  Interceptor │                     │
│   │  Header      │     │  (OkHttp)    │                     │
│   └──────────────┘     └──────────────┘                     │
│                                                              │
│   ┌──────────────────────────────────────────────────────┐  │
│   │  Token Refresh Flow                                   │  │
│   │                                                       │  │
│   │  AuthApi.refreshToken() → New JWT → DataStore update  │  │
│   └──────────────────────────────────────────────────────┘  │
│                                                              │
│   ┌──────────────────────────────────────────────────────┐  │
│   │  Logout Flow                                          │  │
│   │                                                       │  │
│   │  1. Clear tokens from DataStore                       │  │
│   │  2. Clear user data from DataStore                    │  │
│   │  3. Disconnect MQTT                                   │  │
│   │  4. Stop Foreground Service                           │  │
│   │  5. Navigate to Login (clear backstack)               │  │
│   └──────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

### Input Validation

| Field             | Validation Rule                     | Location         |
|-------------------|-------------------------------------|------------------|
| Email             | `Patterns.EMAIL_ADDRESS` regex      | Use Case layer   |
| Password          | Minimum 6 characters                | Use Case layer   |
| Confirm Password  | Must match password field           | Use Case layer   |
| Device Name       | Non-blank required                  | ViewModel layer  |

---

## 15. Background Services

### Foreground Service Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                MqttForegroundService                              │
│                (LifecycleService + @AndroidEntryPoint)            │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                    onCreate()                                │ │
│  │  Initialize service, create notification channels            │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                 onStartCommand()                             │ │
│  │                                                              │ │
│  │  1. startForeground(notification)                            │ │
│  │  2. connectMqttIfNeeded()                                    │ │
│  │  3. observeConnectionState()  ──▶  Update notification       │ │
│  │  4. observeMessages()         ──▶  Show device alerts        │ │
│  │                                                              │ │
│  │  Returns: START_STICKY (auto-restart on kill)                │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │               NotificationHelper                             │ │
│  │                                                              │ │
│  │  Channel: MQTT Service    (LOW)     ──▶ Persistent status    │ │
│  │  Channel: Device Alerts   (HIGH)    ──▶ Device events        │ │
│  │  Channel: Connection      (DEFAULT) ──▶ Connect/disconnect   │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │            BootCompletedReceiver                              │ │
│  │                                                              │ │
│  │  On device boot → Start MqttForegroundService                │ │
│  └─────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

---

## 16. UI Component Hierarchy

### Reusable Component Map

```
┌─────────────────────────────────────────────────────────────┐
│                    App Theme (Material3)                      │
│  ┌────────────────────────────────────────────────────────┐  │
│  │                   Scaffold                              │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │  TopBar                                           │  │  │
│  │  │  (title, navigation, actions)                     │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │                                                         │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │  Screen Content                                   │  │  │
│  │  │                                                    │  │  │
│  │  │  Composed of reusable components:                  │  │  │
│  │  │                                                    │  │  │
│  │  │  ┌──────────────┐  ┌───────────────────────────┐  │  │  │
│  │  │  │ DeviceCard   │  │ WeatherWidget             │  │  │  │
│  │  │  │ ┌──────────┐ │  │ (temp, humidity, wind,    │  │  │  │
│  │  │  │ │DeviceIcon│ │  │  icon, city)              │  │  │  │
│  │  │  │ └──────────┘ │  └───────────────────────────┘  │  │  │
│  │  │  └──────────────┘                                  │  │  │
│  │  │                                                    │  │  │
│  │  │  ┌──────────────────────────────────────────────┐  │  │  │
│  │  │  │ Device Controls                              │  │  │  │
│  │  │  │                                               │  │  │  │
│  │  │  │ ┌──────────┐ ┌─────────────┐ ┌────────────┐ │  │  │  │
│  │  │  │ │ Toggle   │ │ControlSlider│ │ Control    │ │  │  │  │
│  │  │  │ │ Switch   │ │(min/max/step)│ │ Button    │ │  │  │  │
│  │  │  │ └──────────┘ └─────────────┘ └────────────┘ │  │  │  │
│  │  │  └──────────────────────────────────────────────┘  │  │  │
│  │  │                                                    │  │  │
│  │  │  ┌──────────────┐  ┌───────────────────────────┐  │  │  │
│  │  │  │ MapPreview   │  │ LoadingIndicator /        │  │  │  │
│  │  │  │ (Google Maps)│  │ ErrorMessage              │  │  │  │
│  │  │  └──────────────┘  └───────────────────────────┘  │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │                                                         │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │  BottomNavBar                                     │  │  │
│  │  │  [Home] [Devices] [Map] [Settings]                │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Component Usage Matrix

| Component         | Home | DeviceList | DeviceDetail | AddDevice | Map | Settings |
|-------------------|------|------------|--------------|-----------|-----|----------|
| TopBar            |  Y   |     Y      |      Y       |     Y     |  Y  |    Y     |
| BottomNavBar      |  Y   |     Y      |              |           |  Y  |    Y     |
| DeviceCard        |  Y   |     Y      |              |           |     |          |
| DeviceIcon        |  Y   |     Y      |      Y       |     Y     |     |          |
| ToggleSwitch      |      |            |      Y       |           |     |    Y     |
| ControlSlider     |      |            |      Y       |           |     |          |
| ControlButton     |      |            |      Y       |           |     |          |
| WeatherWidget     |  Y   |            |              |           |     |          |
| MapPreview        |      |            |      Y       |           |  Y  |          |
| LoadingIndicator  |  Y   |     Y      |      Y       |     Y     |  Y  |          |
| ErrorMessage      |  Y   |     Y      |      Y       |     Y     |  Y  |          |

---

## 17. Error Handling Strategy

### Error Flow

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  API Call     │     │  Exception   │     │  Resource    │
│  (Retrofit)   │────▶│  Caught in   │────▶│  .Error()   │
│               │     │  Repository  │     │             │
└──────────────┘     └──────────────┘     └──────┬───────┘
                                                  │
                                                  ▼
                                          ┌──────────────┐
                                          │  Use Case    │
                                          │  emits Error │
                                          │  via Flow    │
                                          └──────┬───────┘
                                                  │
                                                  ▼
                                          ┌──────────────┐
                                          │  ViewModel   │
                                          │  updates     │
                                          │  UiState     │
                                          │  .error      │
                                          └──────┬───────┘
                                                  │
                                                  ▼
                                          ┌──────────────┐
                                          │  Screen      │
                                          │  shows       │
                                          │  ErrorMessage│
                                          │  component   │
                                          └──────────────┘
```

### Error Categories

| Layer        | Error Type                  | Handling                            |
|-------------|----------------------------|-------------------------------------|
| Network      | IOException, timeout        | Resource.Error with user message    |
| HTTP         | 401 Unauthorized           | Token refresh / redirect to login   |
| HTTP         | 4xx/5xx                    | Resource.Error with server message  |
| Validation   | Invalid email/password      | UiState.error before API call       |
| MQTT         | Connection lost             | Auto-reconnect, StateFlow update    |
| Mapping      | Unknown enum value          | Fallback to CUSTOM/TOGGLE default   |

---

## 18. Technology Stack

### Full Dependency Map

```
┌─────────────────────────────────────────────────────────────────┐
│                        Android Platform                          │
│  SDK 24-36 | Kotlin 2.0.21 | Compose BOM 2024.09.00            │
└─────────────────────────────────────────────────────────────────┘

┌─────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────┐
│   UI Layer  │  │  Network     │  │  Persistence │  │  DI      │
├─────────────┤  ├──────────────┤  ├──────────────┤  ├──────────┤
│ Compose     │  │ Retrofit     │  │ DataStore    │  │ Hilt     │
│ Material3   │  │ 2.11.0       │  │ 1.1.1        │  │ 2.53.1   │
│ Navigation  │  │              │  │              │  │          │
│ Compose     │  │ OkHttp       │  │              │  │ KSP      │
│             │  │ 4.12.0       │  │              │  │ (no kapt)│
│ Google Maps │  │              │  │              │  │          │
│ Compose     │  │ Gson         │  │              │  │          │
│ 6.2.1       │  │ Converter    │  │              │  │          │
└─────────────┘  └──────────────┘  └──────────────┘  └──────────┘

┌─────────────┐  ┌──────────────┐  ┌──────────────┐
│  Real-time  │  │  Async       │  │  Lifecycle   │
├─────────────┤  ├──────────────┤  ├──────────────┤
│ Eclipse     │  │ Coroutines   │  │ ViewModel    │
│ Paho MQTT   │  │ (core/android│  │ Compose      │
│ 1.2.5       │  │  /play-svcs) │  │              │
│             │  │              │  │ Lifecycle     │
│ Paho        │  │ Flow         │  │ Runtime      │
│ Service     │  │ StateFlow    │  │ Compose      │
│ 1.1.1       │  │ SharedFlow   │  │              │
└─────────────┘  └──────────────┘  └──────────────┘
```

---

## 19. Directory Structure

```
IoTSolution/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/foodchain/iotsolution/
│           ├── IoTSolutionApp.kt                      # @HiltAndroidApp
│           ├── MainActivity.kt                        # @AndroidEntryPoint
│           │
│           ├── core/
│           │   ├── constants/
│           │   │   └── AppConstants.kt                # MQTT topics, URLs, timeouts
│           │   └── util/
│           │       ├── Resource.kt                    # Sealed class wrapper
│           │       ├── Extensions.kt                  # Validation, Response mapping
│           │       ├── NetworkUtils.kt                # Connectivity checks
│           │       └── DateTimeUtils.kt               # Date formatting
│           │
│           ├── domain/
│           │   ├── model/
│           │   │   ├── Device.kt                      # Core device entity
│           │   │   ├── DeviceType.kt                  # 8-type enum
│           │   │   ├── DeviceControl.kt               # Generic control model
│           │   │   ├── ControlType.kt                 # 5-type enum
│           │   │   ├── DeviceLocation.kt              # Lat/lon/address
│           │   │   ├── User.kt                        # User profile
│           │   │   ├── WeatherData.kt                 # Weather info
│           │   │   └── MqttMessage.kt                 # MQTT payload wrapper
│           │   │
│           │   ├── repository/
│           │   │   ├── AuthRepository.kt              # Auth contract
│           │   │   ├── DeviceRepository.kt            # Device CRUD + MQTT
│           │   │   └── WeatherRepository.kt           # Weather contract
│           │   │
│           │   └── usecase/
│           │       ├── auth/
│           │       │   ├── LoginUseCase.kt
│           │       │   ├── SignUpUseCase.kt
│           │       │   ├── LogoutUseCase.kt
│           │       │   └── GetAuthStateUseCase.kt
│           │       ├── device/
│           │       │   ├── GetAllDevicesUseCase.kt
│           │       │   ├── GetDeviceByIdUseCase.kt
│           │       │   ├── AddDeviceUseCase.kt
│           │       │   ├── UpdateDeviceUseCase.kt
│           │       │   ├── DeleteDeviceUseCase.kt
│           │       │   ├── ControlDeviceUseCase.kt
│           │       │   └── ObserveDeviceUpdatesUseCase.kt
│           │       └── weather/
│           │           └── GetWeatherUseCase.kt
│           │
│           ├── data/
│           │   ├── remote/
│           │   │   ├── api/
│           │   │   │   ├── AuthApi.kt                 # POST login/register
│           │   │   │   ├── DeviceApi.kt               # CRUD endpoints
│           │   │   │   └── WeatherApi.kt              # OpenWeather
│           │   │   ├── dto/
│           │   │   │   ├── auth/
│           │   │   │   │   ├── AuthResponse.kt
│           │   │   │   │   ├── LoginRequest.kt
│           │   │   │   │   └── SignUpRequest.kt
│           │   │   │   ├── device/
│           │   │   │   │   ├── DeviceDto.kt
│           │   │   │   │   ├── CreateDeviceRequest.kt
│           │   │   │   │   └── UpdateDeviceRequest.kt
│           │   │   │   └── weather/
│           │   │   │       └── WeatherResponse.kt
│           │   │   ├── mqtt/
│           │   │   │   └── MqttManager.kt             # Paho wrapper
│           │   │   └── interceptor/
│           │   │       └── AuthInterceptor.kt         # JWT injection
│           │   ├── local/
│           │   │   └── DataStoreManager.kt            # Preferences store
│           │   ├── mapper/
│           │   │   ├── DeviceMapper.kt                # DTO ↔ Domain
│           │   │   └── WeatherMapper.kt               # Response → Domain
│           │   ├── repository/
│           │   │   ├── AuthRepositoryImpl.kt
│           │   │   ├── DeviceRepositoryImpl.kt
│           │   │   └── WeatherRepositoryImpl.kt
│           │   └── service/
│           │       ├── MqttForegroundService.kt       # Persistent MQTT
│           │       ├── NotificationHelper.kt          # 3 channels
│           │       └── BootCompletedReceiver.kt       # Auto-start
│           │
│           ├── di/
│           │   ├── AppModule.kt                       # Gson, DataStore
│           │   ├── NetworkModule.kt                   # OkHttp, Retrofit, APIs
│           │   ├── RepositoryModule.kt                # Interface bindings
│           │   └── MqttModule.kt                      # MqttManager
│           │
│           ├── presentation/
│           │   ├── navigation/
│           │   │   ├── Screen.kt                      # Route definitions
│           │   │   └── NavGraph.kt                    # Navigation setup
│           │   ├── auth/
│           │   │   ├── login/
│           │   │   │   ├── LoginScreen.kt
│           │   │   │   ├── LoginViewModel.kt
│           │   │   │   └── LoginUiState.kt
│           │   │   └── signup/
│           │   │       ├── SignUpScreen.kt
│           │   │       ├── SignUpViewModel.kt
│           │   │       └── SignUpUiState.kt
│           │   ├── home/
│           │   │   ├── HomeScreen.kt
│           │   │   ├── HomeViewModel.kt
│           │   │   └── HomeUiState.kt
│           │   ├── device/
│           │   │   ├── list/
│           │   │   │   ├── DeviceListScreen.kt
│           │   │   │   ├── DeviceListViewModel.kt
│           │   │   │   └── DeviceListUiState.kt
│           │   │   ├── detail/
│           │   │   │   ├── DeviceDetailScreen.kt
│           │   │   │   ├── DeviceDetailViewModel.kt
│           │   │   │   └── DeviceDetailUiState.kt
│           │   │   └── add/
│           │   │       ├── AddDeviceScreen.kt
│           │   │       ├── AddDeviceViewModel.kt
│           │   │       └── AddDeviceUiState.kt
│           │   ├── map/
│           │   │   ├── MapScreen.kt
│           │   │   ├── MapViewModel.kt
│           │   │   └── MapUiState.kt
│           │   ├── settings/
│           │   │   ├── SettingsScreen.kt
│           │   │   ├── SettingsViewModel.kt
│           │   │   └── SettingsUiState.kt
│           │   ├── splash/
│           │   │   ├── SplashScreen.kt
│           │   │   └── SplashViewModel.kt
│           │   └── components/
│           │       ├── DeviceIcon.kt
│           │       ├── DeviceCard.kt
│           │       ├── ControlButton.kt
│           │       ├── ToggleSwitch.kt
│           │       ├── BottomNavBar.kt
│           │       ├── ControlSlider.kt
│           │       ├── TopBar.kt
│           │       ├── WeatherWidget.kt
│           │       ├── MapPreview.kt
│           │       ├── LoadingIndicator.kt
│           │       └── ErrorMessage.kt
│           │
│           └── ui/theme/
│               ├── Theme.kt
│               ├── Color.kt
│               ├── Type.kt
│               ├── Spacing.kt
│               └── Shape.kt
│
├── gradle/
│   └── libs.versions.toml                             # Version catalog
├── build.gradle.kts                                   # Project-level
├── settings.gradle.kts
└── local.properties                                   # API keys (gitignored)
```

---

## 20. API Reference

### Authentication Endpoints

| Method | Endpoint          | Body              | Response         | Auth     |
|--------|-------------------|--------------------|------------------|----------|
| POST   | `/auth/login`     | LoginRequest       | AuthResponse     | None     |
| POST   | `/auth/register`  | SignUpRequest       | AuthResponse     | None     |
| POST   | `/auth/refresh`   | (empty)            | AuthResponse     | Bearer   |
| GET    | `/auth/me`        | (none)             | UserDto          | Bearer   |

### Device Endpoints

| Method | Endpoint             | Body                  | Response           | Auth     |
|--------|----------------------|------------------------|--------------------|----------|
| GET    | `/devices`           | (none)                | List\<DeviceDto\>  | Bearer   |
| GET    | `/devices/{id}`      | (none)                | DeviceDto          | Bearer   |
| POST   | `/devices`           | CreateDeviceRequest    | DeviceDto          | Bearer   |
| PUT    | `/devices/{id}`      | UpdateDeviceRequest    | DeviceDto          | Bearer   |
| DELETE | `/devices/{id}`      | (none)                | (empty)            | Bearer   |

### Weather Endpoint

| Method | Endpoint                | Query Params               | Response          | Auth      |
|--------|-------------------------|----------------------------|-------------------|-----------|
| GET    | `/data/2.5/weather`     | lat, lon, appid, units     | WeatherResponse   | API Key   |

### MQTT Topics

| Topic                        | Direction    | Payload Format      | QoS |
|------------------------------|-------------|---------------------|-----|
| `devices/{id}/status`        | Subscribe    | JSON (online/offline)| 1   |
| `devices/{id}/control`       | Publish      | `controlId:value`   | 1   |
| `devices/{id}/telemetry`     | Subscribe    | JSON (sensor data)  | 1   |

### Request/Response Models

```
LoginRequest {
    email: String
    password: String
}

SignUpRequest {
    name: String
    email: String
    password: String
}

AuthResponse {
    token: String           // JWT access token
    refresh_token: String   // JWT refresh token
    user: UserDto
}

UserDto {
    id: String
    email: String
    name: String
    profile_image_url: String?
}

CreateDeviceRequest {
    name: String
    type: String            // DeviceType enum name
    location: DeviceLocationDto
    controls: List<DeviceControlDto>
}

DeviceDto {
    id: String
    name: String
    type: String
    is_online: Boolean
    mqtt_topic_prefix: String
    location: DeviceLocationDto
    controls: List<DeviceControlDto>
    created_at: Long
    updated_at: Long
}
```

---

## Glossary

| Term           | Definition                                                     |
|----------------|----------------------------------------------------------------|
| Clean Architecture | Software architecture separating concerns into layers       |
| MVVM           | Model-View-ViewModel pattern for UI architecture               |
| Hilt           | Android dependency injection framework built on Dagger         |
| KSP            | Kotlin Symbol Processing - annotation processor                |
| JWT            | JSON Web Token for stateless authentication                    |
| MQTT           | Message Queuing Telemetry Transport - IoT messaging protocol   |
| DataStore      | Jetpack library for async, type-safe key-value storage         |
| Compose        | Android's modern declarative UI toolkit                        |
| StateFlow      | Hot flow that emits the current and new state updates          |
| SharedFlow     | Hot flow that emits values to multiple collectors              |
| Resource       | Sealed class wrapping Loading/Success/Error states             |
| UDF            | Unidirectional Data Flow - state flows down, events flow up    |

---

*Generated for IoT Solution v1.0 | Package: com.foodchain.iotsolution*
*Last Updated: February 2026*
