# TODO - OneFast

> **Roadmap produit complète** : voir [`FEATURES_ROADMAP.md`](FEATURES_ROADMAP.md)  
> Les 15 fonctions (timer, dashboard, IA, Ramadan, etc.) y sont détaillées avec statut et ordre d’implémentation **une par une**.

## ✅ Complété (MVP v1.0)

### Architecture & Configuration
- [x] Configuration Gradle (build.gradle.kts)
- [x] Configuration Android (AndroidManifest.xml)
- [x] Dépendances (Compose, Room, Hilt, etc.)
- [x] Structure Clean Architecture
- [x] Dependency Injection avec Hilt

### Couche Data
- [x] Entités Room (FastingSessionEntity, WeightEntryEntity)
- [x] DAOs (FastingSessionDao, WeightEntryDao)
- [x] Database (FastFlowDatabase)
- [x] Repository implementations
- [x] Système de notifications (NotificationHelper)
- [x] Gestion des alarmes (AlarmScheduler)
- [x] BroadcastReceivers (AlarmReceiver, BootReceiver)

### Couche Domain
- [x] Modèles métier (FastingSession, WeightEntry, UserStats)
- [x] Repository interfaces
- [x] Use Cases (Fasting, Weight, Stats)

### Couche Presentation
- [x] Thème Material Design 3
- [x] ViewModels (Dashboard, Weight)
- [x] DashboardScreen avec cercle de progression
- [x] WeightScreen avec historique
- [x] Composants réutilisables (FastingCircle, StatsCard)
- [x] Navigation bottom bar
- [x] MainActivity

### Ressources
- [x] Strings (FR + EN)
- [x] Colors
- [x] Themes
- [x] Icons

### Documentation
- [x] README.md
- [x] ARCHITECTURE.md
- [x] SETUP.md

---

## 🎯 Prochaine fonction (selon FEATURES_ROADMAP.md)

**#1 Timer de jeûne intelligent** — compléter avant les autres :
- [ ] Ajouter plans 12/12 et 14/10
- [ ] Widget écran d’accueil
- [ ] Démarrage automatique (horaire habituel)
- [ ] Plan personnalisé (saisie heures)
- [ ] Débloquer / finaliser OMAD dans l’UI

---

## 🔨 À faire (Priorité haute)

### Tests
- [ ] Tests unitaires pour Use Cases
- [ ] Tests unitaires pour ViewModels
- [ ] Tests unitaires pour Repositories
- [ ] Tests d'intégration pour DAOs
- [ ] Tests UI pour les Composables

### Icônes & Assets
- [ ] Créer l'icône de l'application (ic_launcher)
- [ ] Créer les icônes adaptatives
- [ ] Ajouter les splash screen assets

### Permissions Runtime
- [ ] Implémenter la demande de permission notifications (Android 13+)
- [ ] Implémenter la demande d'alarmes exactes (Android 12+)
- [ ] Écran d'explication des permissions

### Optimisations
- [ ] Optimiser les requêtes Room
- [ ] Ajouter des index sur les tables
- [ ] Gérer les cas d'erreur réseau (futur)
- [ ] Améliorer la gestion de la mémoire

---

## 📱 Fonctionnalités V1.1

### Améliorations UX
- [ ] Animations de transition entre écrans
- [ ] Feedback haptique
- [ ] Sons de notification personnalisés
- [ ] Mode sombre automatique
- [ ] Onboarding pour nouveaux utilisateurs

### Graphiques
- [ ] Graphique de progression du poids (Vico Charts)
- [ ] Graphique des heures de jeûne par semaine
- [ ] Graphique de la série (streak)

### Widgets
- [ ] Widget home screen avec timer
- [ ] Widget de statistiques
- [ ] Widget de poids actuel

### Paramètres
- [ ] Écran de paramètres
- [ ] Choix de l'unité de poids (kg/lbs)
- [ ] Personnalisation des notifications
- [ ] Choix du thème (clair/sombre/auto)
- [ ] Choix de la langue

---

## 🚀 Fonctionnalités V2.0 (Premium)

### Plans avancés
- [ ] Plan OMAD (One Meal A Day)
- [ ] Plan 5:2 (5 jours normal, 2 jours jeûne)
- [ ] Plan personnalisé avec horaires custom
- [ ] Historique des plans utilisés

### Statistiques avancées
- [ ] Analyse des tendances
- [ ] Prédictions basées sur l'historique
- [ ] Comparaison mois par mois
- [ ] Export des données (CSV, PDF)
- [ ] Rapports hebdomadaires/mensuels

### Coaching IA
- [ ] Analyse comportementale
- [ ] Conseils personnalisés
- [ ] Motivation contextuelle
- [ ] Détection des patterns
- [ ] Suggestions d'amélioration

### Nutrition
- [ ] Journal alimentaire
- [ ] Recettes adaptées au jeûne
- [ ] Suggestions de repas
- [ ] Calcul des calories
- [ ] Macronutriments

### Social & Gamification
- [ ] Système d'achievements
- [ ] Badges de progression
- [ ] Classements
- [ ] Challenges communautaires
- [ ] Partage de résultats

---

## 💳 Système de Billing

### Google Play Billing
- [ ] Intégration Billing Library 6.0
- [ ] Abonnement mensuel (9,99 $)
- [ ] Abonnement annuel (59,99 $)
- [ ] Période d'essai gratuite (7 jours)
- [ ] Écran Premium/Paywall
- [ ] Restauration des achats
- [ ] Gestion des états d'abonnement

### Paywall Design
- [ ] Design élégant et non intrusif
- [ ] Présentation des fonctionnalités Premium
- [ ] Comparaison Free vs Premium
- [ ] Témoignages utilisateurs
- [ ] FAQ

---

## 🔄 Synchronisation & Cloud

### Backend
- [ ] API REST ou Firebase
- [ ] Authentification utilisateur
- [ ] Synchronisation multi-appareils
- [ ] Backup automatique
- [ ] Restauration des données

### Sécurité
- [ ] Chiffrement des données sensibles
- [ ] Authentification sécurisée
- [ ] Politique de confidentialité
- [ ] RGPD compliance
- [ ] Conditions d'utilisation

---

## 📊 Analytics & Monitoring

### Analytics
- [ ] Firebase Analytics
- [ ] Suivi des événements clés
- [ ] Funnel de conversion
- [ ] Taux de rétention
- [ ] Crash reporting

### Performance
- [ ] Firebase Performance Monitoring
- [ ] Optimisation du temps de démarrage
- [ ] Optimisation de la consommation batterie
- [ ] Monitoring de la mémoire

---

## 🌍 Internationalisation

### Langues supplémentaires
- [x] Espagnol
- [x] Allemand
- [x] Portugais
- [x] Arabe
- [ ] Italien
- [ ] Chinois
- [ ] Japonais
- [ ] Russe
- [ ] Hindi

### Localisation
- [ ] Formats de date/heure locaux
- [ ] Formats de poids locaux
- [ ] Devises locales pour le billing

---

## 🔗 Intégrations

### Health Connect
- [ ] Lecture des données de poids
- [ ] Lecture des données d'activité
- [ ] Écriture des données de jeûne
- [ ] Synchronisation automatique

### Wearables
- [ ] Support WearOS
- [ ] Support Apple Watch (si iOS)
- [ ] Notifications sur montre
- [ ] Contrôle du timer depuis la montre

### Autres
- [ ] Google Fit
- [ ] Samsung Health
- [ ] MyFitnessPal

---

## 🐛 Bugs connus

_Aucun bug connu pour le moment_

---

## 📝 Notes

### Priorités immédiates (avant release)
1. Tests complets
2. Icônes de l'application
3. Permissions runtime
4. Onboarding
5. Graphiques de poids

### Métriques de succès
- Taux de rétention J7 > 40%
- Taux de conversion Premium > 5%
- Note Play Store > 4.5/5
- Temps moyen de session > 3 minutes

### Roadmap timeline
- **V1.0 MVP**: Semaine 1-5 (ACTUEL)
- **V1.1**: Semaine 6-8
- **V2.0 Premium**: Semaine 9-14
- **V2.1 Cloud**: Semaine 15-18
- **V3.0 IA**: Semaine 19-24
