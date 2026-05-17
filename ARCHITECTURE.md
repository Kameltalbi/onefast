# Architecture FastFlow

## Vue d'ensemble

FastFlow suit une architecture **Clean Architecture** combinée avec le pattern **MVVM** (Model-View-ViewModel) pour garantir une séparation claire des responsabilités, une testabilité optimale et une maintenabilité à long terme.

## Structure des couches

```
app/
├── data/                    # Couche Data
│   ├── local/              # Base de données locale
│   │   ├── entity/         # Entités Room
│   │   ├── dao/            # Data Access Objects
│   │   └── FastFlowDatabase.kt
│   ├── repository/         # Implémentations des repositories
│   ├── notification/       # Gestion des notifications
│   └── receiver/           # BroadcastReceivers
│
├── domain/                  # Couche Domain
│   ├── model/              # Modèles métier
│   ├── repository/         # Interfaces des repositories
│   └── usecase/            # Use Cases (logique métier)
│       ├── fasting/
│       ├── weight/
│       └── stats/
│
├── presentation/            # Couche Presentation
│   ├── dashboard/          # Écran principal
│   ├── weight/             # Gestion du poids
│   ├── components/         # Composants réutilisables
│   ├── theme/              # Thème Material Design 3
│   └── MainActivity.kt
│
└── di/                      # Dependency Injection (Hilt)
    ├── DatabaseModule.kt
    └── RepositoryModule.kt
```

## Couche Data

### Responsabilités
- Accès aux données (Room Database)
- Gestion de la persistance locale
- Implémentation des repositories
- Gestion des notifications et alarmes

### Composants clés
- **FastFlowDatabase**: Base de données Room principale
- **DAOs**: Accès aux données (FastingSessionDao, WeightEntryDao)
- **Repositories**: Implémentations concrètes
- **NotificationHelper**: Gestion des notifications
- **AlarmScheduler**: Planification des alarmes

## Couche Domain

### Responsabilités
- Logique métier pure
- Modèles de domaine
- Contrats des repositories
- Use Cases

### Modèles principaux
- **FastingSession**: Session de jeûne
- **FastingType**: Types de jeûne (16:8, 18:6, 20:4, etc.)
- **FastingStatus**: États du jeûne
- **WeightEntry**: Entrée de poids
- **UserStats**: Statistiques utilisateur

### Use Cases
- **StartFastingUseCase**: Démarrer un jeûne
- **PauseFastingUseCase**: Mettre en pause
- **ResumeFastingUseCase**: Reprendre
- **StopFastingUseCase**: Arrêter
- **AddWeightEntryUseCase**: Ajouter un poids
- **GetUserStatsUseCase**: Récupérer les stats

## Couche Presentation

### Responsabilités
- Interface utilisateur (Jetpack Compose)
- Gestion de l'état UI
- Navigation
- ViewModels

### Composants UI
- **DashboardScreen**: Écran principal avec cercle de progression
- **WeightScreen**: Suivi du poids
- **FastingCircle**: Composant de progression animé
- **StatsCard**: Carte de statistiques

### ViewModels
- **DashboardViewModel**: État du dashboard
- **WeightViewModel**: État du suivi de poids

## Flux de données

```
UI (Composable)
    ↓
ViewModel (State Management)
    ↓
Use Case (Business Logic)
    ↓
Repository Interface (Contract)
    ↓
Repository Implementation (Data Access)
    ↓
DAO / Data Source
    ↓
Room Database
```

## Dependency Injection

Utilisation de **Hilt** pour l'injection de dépendances :

- `@HiltAndroidApp`: Application class
- `@AndroidEntryPoint`: Activities, Fragments, ViewModels
- `@Inject`: Injection de constructeur
- Modules: DatabaseModule, RepositoryModule

## Gestion de l'état

- **StateFlow**: Pour les états réactifs
- **Flow**: Pour les streams de données
- **Compose State**: Pour l'état UI local

## Notifications et Alarmes

### AlarmManager
- Alarmes exactes pour la fin du jeûne
- Persistance après redémarrage (BootReceiver)
- Gestion des permissions Android 12+

### Notifications
- Canal de notification dédié
- Notifications pour début/fin de jeûne
- Rappels d'hydratation

## Persistance des données

### Room Database
- Version: 1
- Entités: FastingSessionEntity, WeightEntryEntity
- Migration: Destructive (MVP)

### DataStore
- Préférences utilisateur (futur)
- Configuration de l'app

## Tests (À implémenter)

### Tests unitaires
- Use Cases
- ViewModels
- Repositories

### Tests d'intégration
- DAOs
- Database

### Tests UI
- Composables
- Navigation

## Évolutions futures

1. **Synchronisation cloud**: Firebase / Backend custom
2. **IA Coaching**: Analyse comportementale
3. **Widgets**: Home screen widgets
4. **WearOS**: Support montres connectées
5. **Health Connect**: Intégration données santé
6. **Gamification**: Achievements, badges
7. **Social**: Communauté, challenges

## Bonnes pratiques

- ✅ Single Responsibility Principle
- ✅ Dependency Inversion
- ✅ Separation of Concerns
- ✅ Reactive Programming (Flow)
- ✅ Immutable State
- ✅ Type Safety
- ✅ Error Handling (Result<T>)
