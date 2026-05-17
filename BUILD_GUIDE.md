# Guide de Compilation - OneFast

## ✅ Prérequis

### Logiciels Requis
- **Android Studio** : Hedgehog (2023.1.1) ou plus récent
- **JDK** : Version 17
- **Android SDK** : API 26 (minimum) à API 34 (target)

### Vérifier l'installation

```bash
# Vérifier Java
java -version
# Devrait afficher: openjdk version "17.x.x"

# Vérifier Android SDK
echo $ANDROID_HOME
# Devrait afficher le chemin vers le SDK
```

---

## 🚀 Compilation Rapide

### Méthode 1 : Via Android Studio (Recommandé)

1. **Ouvrir le projet**
   ```
   File → Open → Sélectionner le dossier FastFlow
   ```

2. **Synchroniser Gradle**
   ```
   File → Sync Project with Gradle Files
   ```

3. **Compiler**
   ```
   Build → Make Project (Ctrl+F9 / Cmd+F9)
   ```

4. **Lancer sur émulateur**
   ```
   Run → Run 'app' (Shift+F10)
   ```

### Méthode 2 : Via Ligne de Commande

```bash
# Se placer dans le dossier du projet
cd /Users/kameltalbi/Repos3/FastFlow

# Rendre gradlew exécutable (macOS/Linux)
chmod +x gradlew

# Nettoyer le projet
./gradlew clean

# Compiler en mode debug
./gradlew assembleDebug

# Compiler en mode release
./gradlew assembleRelease

# Installer sur un appareil connecté
./gradlew installDebug
```

---

## 📱 Lancer l'Application

### Sur Émulateur

1. **Créer un émulateur** (si pas déjà fait)
   ```
   Tools → Device Manager → Create Device
   - Choisir: Pixel 6
   - API Level: 34 (Android 14)
   ```

2. **Lancer l'émulateur**
   ```
   Cliquer sur le bouton Play dans Device Manager
   ```

3. **Installer l'app**
   ```bash
   ./gradlew installDebug
   ```

### Sur Appareil Physique

1. **Activer le mode développeur**
   - Paramètres → À propos du téléphone
   - Taper 7 fois sur "Numéro de build"

2. **Activer le débogage USB**
   - Paramètres → Options pour les développeurs
   - Activer "Débogage USB"

3. **Connecter l'appareil** et installer
   ```bash
   # Vérifier que l'appareil est détecté
   adb devices
   
   # Installer l'app
   ./gradlew installDebug
   ```

---

## 🐛 Résolution des Problèmes

### Erreur: "SDK location not found"

**Solution:**
```bash
# Créer le fichier local.properties
echo "sdk.dir=/Users/VOTRE_NOM/Library/Android/sdk" > local.properties
```

### Erreur: "Gradle sync failed"

**Solution:**
```bash
# Nettoyer le cache Gradle
./gradlew clean --refresh-dependencies

# Ou dans Android Studio:
File → Invalidate Caches → Invalidate and Restart
```

### Erreur: "Manifest merger failed"

**Solution:**
Vérifier que `AndroidManifest.xml` pointe vers `OneFastApplication`:
```xml
<application
    android:name=".OneFastApplication"
    ...
```

### Erreur: "Unresolved reference"

**Solution:**
```bash
# Rebuild le projet
./gradlew clean build
```

---

## 📦 Générer l'APK

### APK Debug (pour tests)

```bash
./gradlew assembleDebug
```

**Fichier généré:**
```
app/build/outputs/apk/debug/app-debug.apk
```

### APK Release (pour production)

1. **Créer un keystore** (première fois seulement)
   ```bash
   keytool -genkey -v -keystore onefast-release.keystore \
     -alias onefast -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Configurer gradle.properties**
   ```properties
   ONEFAST_RELEASE_STORE_FILE=../onefast-release.keystore
   ONEFAST_RELEASE_STORE_PASSWORD=votre_mot_de_passe
   ONEFAST_RELEASE_KEY_ALIAS=onefast
   ONEFAST_RELEASE_KEY_PASSWORD=votre_mot_de_passe
   ```

3. **Générer l'APK**
   ```bash
   ./gradlew assembleRelease
   ```

**Fichier généré:**
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🏪 Générer l'AAB (pour Play Store)

```bash
./gradlew bundleRelease
```

**Fichier généré:**
```
app/build/outputs/bundle/release/app-release.aab
```

---

## 🧪 Lancer les Tests

### Tests Unitaires

```bash
./gradlew test
```

### Tests Instrumentés (sur appareil/émulateur)

```bash
./gradlew connectedAndroidTest
```

### Tests avec Rapport

```bash
./gradlew test --info
```

**Rapport HTML:**
```
app/build/reports/tests/testDebugUnitTest/index.html
```

---

## 📊 Analyser le Code

### Lint (vérification du code)

```bash
./gradlew lint
```

**Rapport:**
```
app/build/reports/lint-results.html
```

### Détection de bugs

```bash
./gradlew lintDebug
```

---

## 🔧 Commandes Utiles

### Lister toutes les tâches Gradle

```bash
./gradlew tasks
```

### Voir les dépendances

```bash
./gradlew dependencies
```

### Nettoyer complètement le projet

```bash
./gradlew clean
rm -rf .gradle
rm -rf app/build
```

### Vérifier la version de Gradle

```bash
./gradlew --version
```

---

## 📱 Configuration de l'Émulateur

### Émulateur Recommandé

- **Appareil** : Pixel 6
- **API Level** : 34 (Android 14)
- **RAM** : 2048 MB
- **Stockage** : 2048 MB

### Créer via ligne de commande

```bash
# Lister les images système disponibles
sdkmanager --list

# Télécharger l'image système
sdkmanager "system-images;android-34;google_apis;x86_64"

# Créer l'AVD
avdmanager create avd -n Pixel_6_API_34 \
  -k "system-images;android-34;google_apis;x86_64" \
  -d "pixel_6"

# Lancer l'émulateur
emulator -avd Pixel_6_API_34
```

---

## 🎯 Checklist Avant Compilation

- [ ] Android Studio installé et à jour
- [ ] JDK 17 installé
- [ ] Android SDK configuré
- [ ] Émulateur créé (ou appareil connecté)
- [ ] Gradle sync réussi
- [ ] Pas d'erreurs dans le code
- [ ] Permissions dans AndroidManifest.xml
- [ ] Icônes de l'app présentes

---

## 📞 Support

### En cas de problème

1. **Vérifier les logs**
   ```bash
   ./gradlew build --stacktrace
   ```

2. **Nettoyer et rebuild**
   ```bash
   ./gradlew clean build
   ```

3. **Vérifier la documentation**
   - [Android Developer Guide](https://developer.android.com)
   - [Gradle Documentation](https://docs.gradle.org)

---

## 🚀 Prochaines Étapes

Après compilation réussie :

1. ✅ Tester toutes les fonctionnalités
2. ✅ Vérifier les notifications
3. ✅ Tester sur différents appareils
4. ✅ Optimiser les performances
5. ✅ Préparer pour le Play Store

---

## 📝 Notes Importantes

### Versions

- **minSdk** : 26 (Android 8.0)
- **targetSdk** : 34 (Android 14)
- **compileSdk** : 34

### Permissions Requises

- `POST_NOTIFICATIONS` (Android 13+)
- `SCHEDULE_EXACT_ALARM` (Android 12+)
- `RECEIVE_BOOT_COMPLETED`
- `WAKE_LOCK`

### Taille de l'APK

- **Debug** : ~15-20 MB
- **Release (minifié)** : ~8-12 MB

---

## ✅ Compilation Réussie !

Si tout s'est bien passé, vous devriez voir :

```
BUILD SUCCESSFUL in Xs
```

L'application **OneFast** est maintenant prête à être testée ! 🎉
