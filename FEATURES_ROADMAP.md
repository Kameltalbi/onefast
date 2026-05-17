# OneFast — Roadmap des fonctionnalités

> **Principe directeur** : ne pas faire une simple “timer app”. La valeur vient du suivi intelligent, de la motivation et de la personnalisation.
>
> **Stratégie** : IA + UX ultra simple + marché arabe/francophone + mode Ramadan + motivation quotidienne (coach / compagnon).

---

## Légende des statuts

| Statut | Signification |
|--------|---------------|
| `fait` | Livré et utilisable |
| `partiel` | Base en place, sous-fonctions manquantes |
| `prochain` | Prochaine fonction à implémenter |
| `planifié` | Dans la file, pas commencé |
| `premium` | Réservé à l’offre payante |

---

## File d’implémentation (une par une)

| # | Fonction | Statut | Phase |
|---|----------|--------|-------|
| 1 | Timer de jeûne intelligent | `partiel` | MVP |
| 2 | Tableau de bord simple | `partiel` | MVP |
| 3 | Suivi poids & mensurations | `partiel` | MVP |
| 4 | Notifications intelligentes | `partiel` | MVP |
| 5 | Coach IA nutrition | `planifié` | IA |
| 6 | IA prédictive | `planifié` | IA |
| 7 | Détection des risques | `planifié` | IA |
| 8 | Challenges | `planifié` | Communauté |
| 9 | Groupe / communauté | `planifié` | Communauté |
| 10 | Générateur de repas IA | `planifié` | Premium |
| 11 | Synchronisation santé | `planifié` | Premium |
| 12 | Analyse du sommeil | `planifié` | Premium |
| 13 | Mode Ramadan | `planifié` | Différenciant |
| 14 | Scan IA des repas | `planifié` | Différenciant |
| 15 | Assistant vocal IA | `planifié` | Différenciant |

**Prochaine étape recommandée** : terminer le **#1 Timer** (plans manquants + widget), puis enrichir le **#2 Dashboard**.

---

## Modèle économique

### Gratuit
- Timer + historique
- Poids de base
- Notifications essentielles
- Dashboard minimal

### Premium
- Coach IA nutrition
- Prédictions & détection risques
- Scan repas & générateur repas
- Plans personnalisés avancés
- Statistiques avancées
- Sync santé & sommeil
- Mode Ramadan avancé (si contenu exclusif)

---

# Détail des 15 fonctions

---

## 1. Timer de jeûne intelligent — `partiel` → `prochain`

**Objectif** : cœur de l’app, mais intelligent (pas un simple chrono).

### Méthodes de jeûne

| Plan | Statut |
|------|--------|
| 12/12 | `planifié` |
| 14/10 | `planifié` |
| 16/8 | `fait` |
| 18/6 | `fait` |
| 20/4 | `fait` |
| OMAD | `partiel` (enum + premium, UI à débloquer) |
| Personnalisé | `partiel` (enum + premium, logique horaires à faire) |

### Sous-fonctions

- [x] Démarrer / arrêter le jeûne
- [x] Pause / reprise
- [x] Historique des sessions (Room)
- [x] Notification début / fin de jeûne (basique)
- [ ] Démarrage automatique (selon horaire habituel)
- [ ] Widget mobile (écran d’accueil)
- [ ] Sélection 12/12 et 14/10 dans l’onboarding et le dashboard
- [ ] Plan personnalisé : saisie heures jeûne / repas
- [ ] OMAD accessible (gratuit ou premium selon décision produit)

**Fichiers existants** : `FastingType.kt`, `FastingRepository`, `DashboardScreen`, `OnboardingScreen`

---

## 2. Tableau de bord simple — `partiel`

**Objectif** : design minimal, motivant, une info clé par zone.

### Affichages

| Élément | Statut |
|---------|--------|
| Temps restant | `fait` (cercle + timer) |
| Durée du jeûne actuel | `fait` |
| Série de jours (streak) | `partiel` (stats calculées, UI à renforcer) |
| Poids actuel | `partiel` (via stats) |
| Calories estimées | `planifié` |
| Progression hebdomadaire | `planifié` (graphique) |

### Sous-fonctions

- [x] Bouton d’action géant (start / pause / stop)
- [x] Cercle de progression animé
- [x] Cartes stats (streak, heures totales, etc.)
- [ ] Graphique semaine (heures jeûnées / jour)
- [ ] Estimation calories (formule simple ou sync Health)
- [ ] Message motivationnel contextuel (sans IA d’abord, templates)

**Fichiers existants** : `DashboardScreen.kt`, `FastingCircle.kt`, `StatsCard.kt`, `GetUserStatsUseCase`

---

## 3. Suivi du poids et des mensurations — `partiel`

**Objectif** : preuves visuelles de progrès = rétention.

### Données

| Donnée | Statut |
|--------|--------|
| Poids | `fait` |
| Tour de taille | `planifié` |
| IMC (calculé) | `planifié` |
| Graphique d’évolution | `planifié` (Vico déjà en dépendances) |
| Photos avant / après | `planifié` |

### Sous-fonctions

- [x] Ajouter une entrée de poids
- [x] Historique liste
- [ ] Entité `BodyMeasurement` (taille, hanches, etc.)
- [ ] Calcul et affichage IMC
- [ ] Graphique courbe poids (7j / 30j / 90j)
- [ ] Galerie photos comparatives (stockage local + privacy)
- [ ] Unités kg / lbs (paramètres)

**Fichiers existants** : `WeightScreen.kt`, `WeightEntry`, `WeightRepository`

---

## 4. Notifications intelligentes — `partiel`

**Objectif** : rétention via messages utiles au bon moment.

### Exemples cibles

| Message | Statut |
|---------|--------|
| « Il vous reste 2 h » | `planifié` |
| « Hydratez-vous » | `planifié` |
| « Phase combustion des graisses » (~12–16 h) | `planifié` |
| « Bravo, 7 jours consécutifs » | `planifié` |
| Début / fin de jeûne | `fait` |

### Sous-fonctions

- [x] Canal de notifications
- [x] Notifications début / fin
- [ ] Alarmes programmées selon phase du jeûne
- [ ] Templates multilingues (FR / AR / EN)
- [ ] Préférences utilisateur (types activés, horaires silencieux)
- [ ] Notification streak / milestones

**Fichiers existants** : `NotificationHelper.kt`, `AlarmScheduler.kt`, `AlarmReceiver.kt`

---

## 5. Coach IA nutrition — `planifié` · `premium`

**Objectif** : forte valeur perçue, différenciation vs concurrents.

### Cas d’usage

- « J’ai faim »
- « Que manger après mon jeûne ? »
- « Je suis fatigué »
- « Puis-je boire un café ? »

### Contexte injecté à l’IA

- Durée du jeûne en cours
- Poids & objectif
- Plan actif (16/8, OMAD, etc.)
- Activité physique (si sync #11)

### Sous-fonctions

- [ ] Écran chat (Compose)
- [ ] API LLM (OpenAI / Gemini / Claude) + clé sécurisée
- [ ] Prompt système avec garde-fous médicaux (disclaimers)
- [ ] Historique des conversations (local)
- [ ] Limite gratuite (ex. 3 questions/jour) vs illimité premium

---

## 6. IA prédictive — `planifié` · `premium`

**Objectif** : projection motivante, pas diagnostic médical.

### Exemples

- Perte de poids probable selon le rythme
- Temps pour atteindre l’objectif (ex. 85 kg en 4 mois)
- Rythme de jeûne optimal suggéré

### Sous-fonctions

- [ ] Saisie objectif poids + date cible
- [ ] Modèle simple (régression sur historique local)
- [ ] Carte « projection » sur le dashboard
- [ ] Recalcul hebdomadaire automatique

---

## 7. Détection des risques — `planifié` · `premium`

**Objectif** : crédibilité et sécurité utilisateur.

### Signaux à détecter

- Jeûne excessif (durée / fréquence)
- Perte de poids trop rapide
- Fatigue déclarée souvent (via coach ou check-in)
- Hydratation insuffisante (rappels ignorés)

### Sous-fonctions

- [ ] Règles métier (seuils configurables)
- [ ] Bannière / dialogue d’alerte non bloquant
- [ ] Recommandation de consulter un professionnel
- [ ] Journal des alertes (optionnel)

---

## 8. Challenges — `planifié`

**Objectif** : gamification + rétention.

### Types

- 7 jours
- 30 jours
- Ramadan challenge
- Summer body challenge

### Sous-fonctions

- [ ] Définition challenge (durée, règle de validation)
- [ ] Badges locaux
- [ ] Classement (nécessite backend ou Firebase)
- [ ] Écran « Mes défis »
- [ ] Notification mi-parcours / victoire

---

## 9. Groupe / communauté — `planifié`

**Objectif** : croissance organique.

### Contenu partagé

- Résultats (streak, poids %)
- Idées de repas
- Messages motivation

### Sous-fonctions

- [ ] Auth utilisateur (Firebase / Supabase)
- [ ] Fil d’actualité ou groupes thématiques
- [ ] Modération basique
- [ ] Partage anonymisé optionnel

---

## 10. Générateur de repas IA — `planifié` · `premium`

### Paramètres

- Calories cible
- Objectif (perte / maintien)
- Pays / cuisine
- Budget
- Régimes : halal, végétarien, etc.

### Sous-fonctions

- [ ] Formulaire préférences
- [ ] Génération plan repas (IA)
- [ ] Liste courses exportable
- [ ] Favoris / historique repas

---

## 11. Synchronisation santé — `planifié` · `premium`

### Plateformes

- Google Health Connect (Android prioritaire)
- Samsung Health
- Apple Health (si version iOS future)

### Données

- Pas
- Sommeil
- Calories
- Fréquence cardiaque

### Sous-fonctions

- [ ] Permission Health Connect
- [ ] Lecture / écriture poids
- [ ] Affichage données corrélées sur dashboard

---

## 12. Analyse du sommeil — `planifié` · `premium`

**Objectif** : corréler sommeil ↔ faim ↔ énergie ↔ poids.

### Sous-fonctions

- [ ] Import durée sommeil (via #11)
- [ ] Graphique corrélation simple
- [ ] Insight texte (« les nuits < 6 h, vous arrêtez le jeûne plus tôt »)
- [ ] Conseils sommeil dans notifications

---

## 13. Mode Ramadan — `planifié` · différenciant

**Objectif** : gros levier acquisition marché arabe / MENA.

### Fonctions

- Horaires automatiques suhoor / iftar selon ville
- Suivi hydratation
- Conseils nutritionnels Ramadan
- Challenge Ramadan (#8)

### Sous-fonctions

- [ ] API horaires prière / coucher soleil (ville + GPS)
- [ ] UI mode Ramadan dédiée
- [ ] Rappels hydratation entre iftar et suhoor
- [ ] Contenu AR + FR

---

## 14. Scan IA des repas — `planifié` · `premium`

### Flux

1. Photo du repas
2. IA estime calories, protéines, glucides, lipides
3. Score qualité nutritionnelle

### Sous-fonctions

- [ ] Caméra / galerie
- [ ] API vision (OpenAI Vision / Gemini)
- [ ] Historique repas scannés
- [ ] Ajout au journal quotidien

---

## 15. Assistant vocal IA — `planifié` · `premium`

### Exemple

> « Puis-je casser mon jeûne avec des dattes ? »

### Sous-fonctions

- [ ] Speech-to-text (Android)
- [ ] Même backend que coach #5
- [ ] Text-to-speech réponse (optionnel)
- [ ] Mode mains libres pendant le jeûne

---

# Jalons par phase

## Phase A — MVP solide (semaines 1–4)
1. Compléter timer (#1)
2. Enrichir dashboard (#2)
3. Graphiques poids (#3)
4. Notifications contextuelles (#4)
5. Paramètres + permissions runtime

## Phase B — Rétention (semaines 5–8)
6. Widget
7. Challenges locaux (#8)
8. Onboarding amélioré + objectif poids

## Phase C — IA & Premium (semaines 9–14)
9. Billing Google Play
10. Coach IA (#5)
11. Prédictions (#6)
12. Détection risques (#7)

## Phase D — Différenciation (semaines 15+)
13. Mode Ramadan (#13)
14. Sync santé (#11–12)
15. Scan repas (#14)
16. Communauté (#9)
17. Vocal (#15)

---

# Ce qui existe déjà dans le code (référence rapide)

| Domaine | Implémenté |
|---------|------------|
| Architecture | Clean + Hilt + Room + Compose |
| Jeûne | Start / pause / resume / stop, sessions en base |
| Plans | 16:8, 18:6, 20:4 (+ OMAD/custom en enum premium) |
| UI | Onboarding 3 étapes, Dashboard, Poids, navigation |
| Stats | Streak, heures totales, perte de poids |
| Notifs | Début / fin jeûne, alarmes |
| i18n | FR, EN, ES, DE, PT, AR (strings) |
| Billing | Dépendance présente, pas branchée |

---

# Journal d’implémentation

> Cocher ici au fur et à mesure de chaque livraison.

| Date | # | Fonction / sous-tâche | PR / commit |
|------|---|------------------------|-------------|
| 2026-05-17 | — | Roadmap créée, build émulateur corrigé | — |
| | 1 | | |
| | 2 | | |
| | 3 | | |
| | … | | |

---

*Dernière mise à jour : 2026-05-17*
