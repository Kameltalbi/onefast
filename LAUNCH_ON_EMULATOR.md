# 🚀 Lancer OneFast sur Émulateur

## ✅ Émulateur Détecté !

Un émulateur Android est déjà en cours d'exécution : `emulator-5554`

---

## 🎯 Méthode 1 : Via Android Studio (Recommandé)

### Étape 1 : Vérifier l'Émulateur

En haut à droite d'Android Studio, vous devriez voir :
```
[Pixel 6 API 34] ▼
```

### Étape 2 : Lancer l'Application

**Cliquez sur le bouton ▶️ Play vert** en haut à droite

Ou utilisez le raccourci :
- **Mac** : `Shift + F10`
- **Windows/Linux** : `Shift + F10`

### Étape 3 : Attendre l'Installation

Vous verrez dans la console :
```
Installing APK...
Launching 'app'...
```

### Étape 4 : L'App Se Lance !

L'application **OneFast** devrait s'ouvrir automatiquement sur l'émulateur ! 🎉

---

## 🎯 Méthode 2 : Via Menu Run

```
Run → Run 'app'
```

Ou :

```
Run → Debug 'app'  (pour déboguer)
```

---

## 🎯 Méthode 3 : Via Gradle (Alternative)

Si vous préférez la ligne de commande :

### Étape 1 : Télécharger le Gradle Wrapper JAR

```bash
cd /Users/kameltalbi/Repos3/FastFlow

# Créer le dossier wrapper s'il n'existe pas
mkdir -p gradle/wrapper

# Télécharger le JAR
curl -L https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar \
  -o gradle/wrapper/gradle-wrapper.jar
```

### Étape 2 : Installer l'App

```bash
./gradlew installDebug
```

### Étape 3 : Lancer l'App

```bash
adb shell am start -n com.fastflow.app/.presentation.MainActivity
```

---

## 📱 Ce Que Vous Allez Voir

### 1. **Écran de Bienvenue** 👋
- Emoji géant
- "Le jeûne intermittent simplifié"
- Bouton "Commencer"

### 2. **Choix du Plan** 🎯
- 3 cartes : 16:8 (recommandé), 18:6, 20:4
- Badge orange sur 16:8
- Sélection visuelle avec ✓

### 3. **Prêt !** 🚀
- Confirmation du choix
- "Démarrer mon premier jeûne"

### 4. **Dashboard** 📊
- **Bouton géant** au centre (120dp)
- Cercle de progression
- Statistiques en bas

---

## 🎨 Fonctionnalités à Tester

### Sur l'Onboarding
- [ ] Tap "Commencer"
- [ ] Sélectionner un plan différent
- [ ] Voir le checkmark ✓
- [ ] Tap "Continuer"
- [ ] Tap "Démarrer mon premier jeûne"

### Sur le Dashboard
- [ ] Tap le **bouton géant** "Démarrer"
- [ ] Voir le cercle de progression s'animer
- [ ] Tap "Pause"
- [ ] Tap "Reprendre"
- [ ] Tap "Arrêter le jeûne"
- [ ] Naviguer vers "Poids"
- [ ] Ajouter une entrée de poids

### Notifications
- [ ] Démarrer un jeûne
- [ ] Vérifier la notification
- [ ] Mettre l'app en arrière-plan
- [ ] Vérifier que le timer continue

---

## 🐛 Dépannage

### L'App Ne Se Lance Pas

**Solution 1 : Rebuild**
```
Build → Clean Project
Build → Rebuild Project
Run → Run 'app'
```

**Solution 2 : Redémarrer l'Émulateur**
```
Tools → Device Manager → [Votre émulateur] → Stop
Tools → Device Manager → [Votre émulateur] → Start
```

**Solution 3 : Désinstaller et Réinstaller**
```bash
adb uninstall com.fastflow.app
./gradlew installDebug
```

### L'Émulateur Est Lent

**Activer l'Accélération Matérielle** :
```
Tools → AVD Manager → [Votre émulateur] → Edit
→ Graphics: Hardware - GLES 2.0
```

### Erreur "App Not Installed"

**Vérifier l'Espace Disque** :
```bash
adb shell df /data
```

Si plein, supprimer des apps :
```bash
adb shell pm list packages
adb uninstall <package_name>
```

---

## 📸 Captures d'Écran

Pour prendre des screenshots :

```
Tools → Device Manager → [Émulateur] → Camera icon
```

Ou :

```bash
adb exec-out screencap -p > screenshot.png
```

---

## 🎥 Enregistrer une Vidéo

```
Tools → Device Manager → [Émulateur] → Record icon
```

Ou :

```bash
adb shell screenrecord /sdcard/demo.mp4
# Arrêter avec Ctrl+C
adb pull /sdcard/demo.mp4
```

---

## ✅ Checklist de Test

### Onboarding
- [ ] Les 3 écrans s'affichent correctement
- [ ] Les animations sont fluides
- [ ] La sélection du plan fonctionne
- [ ] Le bouton "Démarrer" fonctionne

### Dashboard
- [ ] Le bouton géant est bien visible (120dp)
- [ ] L'animation de pulse fonctionne
- [ ] Le cercle de progression s'anime
- [ ] Les stats s'affichent correctement

### Fonctionnalités
- [ ] Démarrer un jeûne fonctionne
- [ ] Pause fonctionne
- [ ] Reprendre fonctionne
- [ ] Arrêter fonctionne
- [ ] Navigation entre écrans fonctionne

### Performance
- [ ] Pas de lag
- [ ] Animations à 60fps
- [ ] Temps de démarrage < 2s
- [ ] Pas de crash

---

## 🎉 Résultat Attendu

Après avoir cliqué sur **▶️ Play** :

1. ✅ L'app se compile
2. ✅ L'APK s'installe sur l'émulateur
3. ✅ L'app se lance automatiquement
4. ✅ Vous voyez l'écran de bienvenue OneFast

**Temps total : ~10-30 secondes**

---

## 💡 Astuce Pro

### Lancement Rapide

Une fois l'app installée, pour la relancer rapidement :

```bash
adb shell am start -n com.fastflow.app/.presentation.MainActivity
```

### Voir les Logs en Direct

```bash
adb logcat | grep "OneFast"
```

### Forcer l'Arrêt

```bash
adb shell am force-stop com.fastflow.app
```

---

## 🚀 C'est Parti !

**Cliquez sur le bouton ▶️ Play vert en haut à droite d'Android Studio !**

L'application **OneFast** va se lancer sur votre émulateur ! 🎉

Profitez de votre création ! 🚀
