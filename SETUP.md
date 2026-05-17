# Guide de Configuration - FastFlow

## Prérequis

### Logiciels requis
- **Android Studio**: Hedgehog (2023.1.1) ou plus récent
- **JDK**: Version 17
- **Gradle**: 8.2.0 (inclus dans le wrapper)
- **Kotlin**: 1.9.20
- **Android SDK**: API 26 (minimum) à API 34 (target)

### Connaissances recommandées
- Kotlin
- Jetpack Compose
- Clean Architecture
- MVVM Pattern
- Coroutines & Flow
- Room Database
- Hilt/Dagger

## Installation

### 1. Cloner le projet

```bash
git clone <repository-url>
cd FastFlow
```

### 2. Ouvrir dans Android Studio

1. Lancez Android Studio
2. File → Open
3. Sélectionnez le dossier `FastFlow`
4. Attendez la synchronisation Gradle

### 3. Configuration du SDK

Assurez-vous d'avoir installé :
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Android Emulator (pour les tests)

### 4. Synchronisation Gradle

```bash
./gradlew build
```

## Configuration de l'environnement

### Permissions Android

Les permissions suivantes sont requises (déjà dans le Manifest) :

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

### Configuration des notifications (Android 13+)

L'application demandera automatiquement la permission de notification au premier lancement.

### Configuration des alarmes exactes (Android 12+)

Pour Android 12 et supérieur, l'utilisateur devra autoriser les alarmes exactes dans les paramètres système.

## Lancement de l'application

### Via Android Studio

1. Sélectionnez un émulateur ou connectez un appareil physique
2. Cliquez sur Run (▶️) ou `Shift + F10`

### Via ligne de commande

```bash
# Debug build
./gradlew installDebug

# Release build (nécessite configuration de signature)
./gradlew installRelease
```

## Structure de la base de données

### Tables créées automatiquement

1. **fasting_sessions**
   - id (PK)
   - startTime
   - endTimeExpected
   - endTimeActual
   - fastingType
   - status
   - pausedAt
   - totalPausedDuration

2. **weight_entries**
   - id (PK)
   - timestamp
   - weight
   - unit

## Configuration pour le développement

### Debug Mode

Le mode debug est activé par défaut. Pour voir les logs :

```bash
adb logcat | grep FastFlow
```

### Inspection de la base de données

Utilisez **Database Inspector** dans Android Studio :
1. View → Tool Windows → App Inspection
2. Sélectionnez l'onglet Database Inspector
3. Explorez les tables

### Hilt Configuration

Hilt est configuré automatiquement. Assurez-vous que :
- `@HiltAndroidApp` est sur l'Application class
- `@AndroidEntryPoint` est sur MainActivity
- `@HiltViewModel` est sur les ViewModels

## Tests

### Lancer les tests unitaires

```bash
./gradlew test
```

### Lancer les tests instrumentés

```bash
./gradlew connectedAndroidTest
```

## Build de production

### 1. Créer un keystore

```bash
keytool -genkey -v -keystore fastflow-release.keystore \
  -alias fastflow -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configurer gradle.properties

Créez `gradle.properties` local :

```properties
FASTFLOW_RELEASE_STORE_FILE=../fastflow-release.keystore
FASTFLOW_RELEASE_STORE_PASSWORD=your_password
FASTFLOW_RELEASE_KEY_ALIAS=fastflow
FASTFLOW_RELEASE_KEY_PASSWORD=your_password
```

### 3. Build APK/AAB

```bash
# APK
./gradlew assembleRelease

# Android App Bundle (pour Play Store)
./gradlew bundleRelease
```

## Dépendances principales

| Bibliothèque | Version | Usage |
|-------------|---------|-------|
| Jetpack Compose | 2023.10.01 | UI Framework |
| Room | 2.6.1 | Database |
| Hilt | 2.48 | Dependency Injection |
| Navigation Compose | 2.7.5 | Navigation |
| Coroutines | 1.7.3 | Asynchrone |
| DataStore | 1.0.0 | Préférences |
| WorkManager | 2.9.0 | Background tasks |
| Vico Charts | 1.13.1 | Graphiques |

## Troubleshooting

### Erreur de synchronisation Gradle

```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Problème de permissions

Vérifiez que toutes les permissions sont dans le Manifest et demandées au runtime pour Android 6+.

### Base de données corrompue

```bash
adb shell
run-as com.fastflow.app
rm -rf databases/
```

### Notifications ne fonctionnent pas

1. Vérifiez les permissions
2. Vérifiez le canal de notification
3. Testez sur un appareil physique (l'émulateur peut avoir des bugs)

## Ressources utiles

- [Documentation Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Material Design 3](https://m3.material.io/)

## Support

Pour toute question ou problème :
1. Vérifiez la documentation
2. Consultez les issues GitHub
3. Contactez l'équipe de développement
