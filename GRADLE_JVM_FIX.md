# 🔧 Fix Gradle JVM - OneFast

## ❌ Problème

```
Incompatible Gradle JVM
The minima compatible Gradle version is 8.5
The maxima compatible Gradle JVM version is 19
```

## ✅ Solution

### Option 1 : Configurer la JVM dans Android Studio (Recommandé)

#### Étape 1 : Ouvrir les Paramètres Gradle

```
Android Studio → Settings (ou Preferences sur Mac)
→ Build, Execution, Deployment
→ Build Tools
→ Gradle
```

#### Étape 2 : Sélectionner la Bonne JVM

Dans **Gradle JVM**, sélectionner :
- **JDK 17** (recommandé)
- Ou **JDK 19** (maximum compatible)

**Options disponibles** :
```
○ Embedded JDK (version 17.x.x)  ← Choisir celle-ci
○ JAVA_HOME
○ JDK 17
○ Download JDK...
```

#### Étape 3 : Appliquer et Sync

1. Cliquer sur **Apply**
2. Cliquer sur **OK**
3. Sync Gradle : `File → Sync Project with Gradle Files`

---

### Option 2 : Utiliser la JVM Embedded d'Android Studio

Android Studio inclut une JVM compatible. Pour l'utiliser :

```
Settings → Gradle → Gradle JVM → Embedded JDK
```

---

### Option 3 : Télécharger JDK 17

Si vous n'avez pas JDK 17 :

#### Sur macOS (avec Homebrew)

```bash
# Installer Homebrew si nécessaire
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Installer JDK 17
brew install openjdk@17

# Lier JDK
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

#### Sur Windows

1. Télécharger depuis [Adoptium](https://adoptium.net/)
2. Choisir **Temurin 17 (LTS)**
3. Installer
4. Redémarrer Android Studio

#### Sur Linux

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Fedora
sudo dnf install java-17-openjdk-devel
```

---

## 🔍 Vérifier la Version de Java

### Dans le Terminal

```bash
java -version
```

**Résultat attendu** :
```
openjdk version "17.0.x" 2023-xx-xx
OpenJDK Runtime Environment (build 17.0.x+x)
OpenJDK 64-Bit Server VM (build 17.0.x+x, mixed mode)
```

### Dans Android Studio

```
Help → About → Copy and Paste
```

Chercher la ligne :
```
Runtime version: 17.0.x
```

---

## 📊 Compatibilité Gradle vs JVM

| Gradle Version | JVM Min | JVM Max | Recommandé |
|----------------|---------|---------|------------|
| 8.0 - 8.4 | 8 | 19 | JDK 17 |
| **8.5** | **8** | **19** | **JDK 17** ✅ |
| 8.6+ | 8 | 20 | JDK 17 |

---

## 🎯 Configuration Projet OneFast

### Versions Actuelles

```kotlin
// build.gradle.kts (project)
AGP: 8.2.0
Kotlin: 1.9.20
```

```properties
// gradle-wrapper.properties
Gradle: 8.5  ✅ (mis à jour)
```

### Versions Requises

- **Gradle** : 8.5+
- **JVM** : 17 (recommandé) ou 19 (max)
- **Android Gradle Plugin** : 8.2.0

---

## 🚀 Après Configuration

### 1. Invalider les Caches

```
File → Invalidate Caches → Invalidate and Restart
```

### 2. Clean Build

```bash
./gradlew clean
```

### 3. Sync Gradle

```
File → Sync Project with Gradle Files
```

### 4. Rebuild

```
Build → Rebuild Project
```

---

## ⚠️ Erreurs Courantes

### Erreur : "JAVA_HOME is not set"

**Solution** :
```bash
# macOS/Linux
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Ajouter à ~/.zshrc ou ~/.bashrc
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```

### Erreur : "Could not determine java version"

**Solution** : Redémarrer Android Studio après avoir changé la JVM

### Erreur : "Unsupported class file major version"

**Solution** : La JVM est trop ancienne, installer JDK 17

---

## 📝 Checklist de Résolution

- [ ] Gradle version ≥ 8.5 (dans gradle-wrapper.properties)
- [ ] JVM version = 17 (dans Android Studio Settings)
- [ ] JAVA_HOME configuré (dans le terminal)
- [ ] Caches invalidés
- [ ] Projet clean
- [ ] Gradle sync réussi
- [ ] Build réussi

---

## 🎉 Résultat Attendu

Après configuration correcte :

```
BUILD SUCCESSFUL in Xs
```

Aucune erreur de JVM incompatible ! ✅

---

## 💡 Recommandation Finale

**Utiliser la JVM Embedded d'Android Studio** :
- ✅ Toujours compatible
- ✅ Pas de configuration manuelle
- ✅ Mise à jour automatique
- ✅ Fonctionne out-of-the-box

```
Settings → Gradle → Gradle JVM → Embedded JDK (17.x.x)
```

**C'est la solution la plus simple et la plus fiable !** 🚀
