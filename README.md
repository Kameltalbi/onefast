# OneFast - Application de Jeûne Intermittent

Application Android native moderne dédiée au jeûne intermittent avec suivi intelligent des cycles, gestion du poids et analyse des performances.

## 🎯 Objectifs

- Suivi intelligent des cycles de jeûne
- Gestion des fenêtres alimentaires
- Suivi du poids avec graphiques
- Analyse des performances
- Accompagnement motivationnel
- Architecture évolutive pour IA future

## 🛠 Stack Technique

- **Langage**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **Base de données**: Room Database
- **Préférences**: DataStore
- **Notifications**: AlarmManager
- **Tâches background**: WorkManager
- **Paiement**: Google Play Billing
- **Graphiques**: Compose Charts
- **Design**: Material Design 3

## 📁 Structure du Projet

```
app/
├── data/              # Couche Data (Room, DataStore, Repository impl)
├── domain/            # Couche Domain (Use Cases, Repository interfaces, Models)
├── presentation/      # Couche Presentation (ViewModels, Composables, UI)
└── di/               # Dependency Injection
```

## 🚀 Fonctionnalités MVP

### Version 1.0

- ✅ Gestion du jeûne (démarrage, pause, arrêt)
- ✅ Plans de jeûne: 16:8, 18:6, 20:4
- ✅ Notifications locales
- ✅ Suivi du poids avec historique
- ✅ Dashboard avec cercle de progression
- ✅ Statistiques de base

### Premium

- 🔒 Plans avancés (OMAD, personnalisés)
- 🔒 Statistiques avancées
- 🔒 Coaching IA
- 🔒 Nutrition & Recettes

## 🎨 Design

- **Couleur principale**: Bleu nuit `#0B192C`
- **Accent jeûne**: Bleu `#016AEB`
- **Fenêtre repas**: Orange `#FFB200`
- **Background**: `#F5F7F8`

## 📱 Langues Supportées

- �🇧 Anglais (English)
- 🇪🇸 Espagnol (Español)
- 🇵🇹 Portugais (Português)
- ��🇷 Français
- 🇸🇦 Arabe (العربية)
- �� Allemand (Deutsch)

## 🗓 Roadmap

1. **Phase 1**: Architecture & Setup (1-2 semaines)
2. **Phase 2**: Core Timer (1 semaine)
3. **Phase 3**: Dashboard & Poids (1 semaine)
4. **Phase 4**: Premium & Billing (1 semaine)
5. **Phase 5**: QA & Publication (1 semaine)

## 📄 License

Propriétaire
