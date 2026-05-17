# Principes UX - OneFast

## 🎯 Philosophie : Simplicité Extrême

OneFast doit être l'application de jeûne **la plus simple à utiliser** du marché. Chaque interaction doit être intuitive, rapide et sans friction.

---

## ✨ Règles d'Or

### 1. **Règle des 3 secondes**
L'utilisateur doit comprendre comment utiliser l'app en moins de 3 secondes.

### 2. **Règle du 1 tap**
Les actions principales ne doivent jamais nécessiter plus d'1 tap.

### 3. **Règle du zéro apprentissage**
Aucun tutoriel ne devrait être nécessaire pour utiliser les fonctions de base.

### 4. **Règle du feedback immédiat**
Chaque action doit avoir un retour visuel/haptique instantané.

---

## 🎨 Design Minimaliste

### Écran Principal (Dashboard)

#### ✅ CE QU'ON GARDE
- **Grand cercle de progression** (élément central visuel)
- **1 gros bouton d'action** (Démarrer/Pause/Arrêter)
- **Temps restant en GROS** (info la plus importante)
- **4 stats maximum** (pas de surcharge cognitive)

#### ❌ CE QU'ON ÉVITE
- Trop de texte
- Menus cachés
- Options complexes
- Jargon technique

---

## 🚀 Parcours Utilisateur Simplifié

### Premier Lancement (Onboarding)

```
Écran 1: "Bienvenue sur OneFast"
→ Animation du cercle de progression
→ 1 phrase: "Le jeûne intermittent simplifié"

Écran 2: "Choisissez votre plan"
→ 3 cartes visuelles (16:8, 18:6, 20:4)
→ Icônes + description 1 ligne
→ Plan 16:8 pré-sélectionné (recommandé)

Écran 3: "Prêt à commencer ?"
→ Gros bouton "Démarrer mon premier jeûne"
→ Lien "Je commence plus tard"

TOTAL: 3 écrans, 2 taps maximum
```

### Utilisation Quotidienne

```
1. Ouvrir l'app
   ↓
2. Voir le cercle de progression
   ↓
3. Tap sur le bouton central
   ↓
4. C'est tout ! ✅
```

---

## 🎯 Hiérarchie Visuelle

### Taille des éléments (par importance)

1. **XXL** - Cercle de progression + Pourcentage
2. **XL** - Bouton d'action principal
3. **L** - Temps restant
4. **M** - Stats secondaires
5. **S** - Labels et textes explicatifs

### Couleurs (par fonction)

- **Bleu** (#016AEB) - Jeûne actif, action principale
- **Orange** (#FFB200) - Fenêtre repas, attention
- **Gris clair** - Éléments inactifs
- **Vert** - Succès, objectif atteint
- **Rouge** - Arrêt, annulation

---

## 📱 Interactions Simplifiées

### Gestes Intuitifs

| Action | Geste | Feedback |
|--------|-------|----------|
| Démarrer jeûne | Tap bouton | Animation + Vibration |
| Voir stats | Scroll vers bas | Transition fluide |
| Ajouter poids | Tap FAB | Dialog simple |
| Changer plan | Tap sur type de jeûne | Bottom sheet |
| Pause | Tap bouton pause | Animation pause |

### Animations

- **Cercle de progression** : Animation continue (1 update/seconde)
- **Boutons** : Scale effect au tap (0.95x)
- **Transitions** : Fade + Slide (300ms)
- **Succès** : Confetti + Vibration

---

## 🔔 Notifications Intelligentes

### Principe : Utiles, jamais intrusives

#### Notifications Essentielles (ON par défaut)
- ✅ Jeûne terminé (avec félicitations)
- ✅ Fenêtre repas ouverte

#### Notifications Optionnelles (OFF par défaut)
- 🔕 Rappel hydratation
- 🔕 Motivation quotidienne
- 🔕 Rappel de pesée

#### Ton des notifications
- **Positif** : "Bravo ! 16h de jeûne complétées 🎉"
- **Encourageant** : "Plus que 2h, vous y êtes presque !"
- **Jamais culpabilisant** : ❌ "Vous avez raté votre jeûne"

---

## 📊 Statistiques Simplifiées

### Dashboard : 4 KPIs Maximum

1. **Série actuelle** (🔥 3 jours)
2. **Jeûnes terminés** (✅ 12)
3. **Heures totales** (⏱️ 156h)
4. **Poids actuel** (⚖️ 75.2 kg)

### Écran Stats Détaillées (Premium)
- Graphique poids (simple ligne)
- Calendrier des jeûnes (heatmap)
- Moyenne hebdomadaire
- Meilleur série

---

## 🎮 Gamification Subtile

### Éléments Motivants (sans être intrusif)

#### Achievements Discrets
- 🥉 Premier jeûne complété
- 🥈 7 jours de suite
- 🥇 30 jours de suite
- 💎 100 jeûnes

#### Feedback Positif
- Animation de confetti (jeûne terminé)
- Message de félicitation
- Progression visuelle de la série

#### PAS de :
- ❌ Classements compétitifs
- ❌ Pression sociale
- ❌ Notifications agressives
- ❌ Publicités intrusives

---

## 🚫 Anti-Patterns à Éviter

### ❌ Complexité Inutile
- Trop d'options dans les paramètres
- Menus à plusieurs niveaux
- Formulaires longs
- Jargon technique

### ❌ Friction
- Demander trop d'informations au départ
- Forcer la création de compte
- Paywall agressif
- Tutoriel obligatoire

### ❌ Surcharge Visuelle
- Trop de couleurs
- Animations excessives
- Pop-ups intempestifs
- Bannières publicitaires

---

## ✅ Checklist Simplicité

Avant chaque release, vérifier :

- [ ] Un nouvel utilisateur peut démarrer un jeûne en moins de 30 secondes
- [ ] L'écran principal ne contient que l'essentiel
- [ ] Chaque bouton a un label clair
- [ ] Les animations sont fluides (60fps)
- [ ] Aucune action ne nécessite plus de 2 taps
- [ ] Les erreurs sont expliquées simplement
- [ ] L'app fonctionne sans connexion internet
- [ ] Le temps de chargement est < 1 seconde
- [ ] Les textes sont courts et clairs
- [ ] Les icônes sont universellement compréhensibles

---

## 🎯 Objectifs Mesurables

### Métriques UX Cibles

| Métrique | Objectif |
|----------|----------|
| Temps avant 1er jeûne | < 1 minute |
| Taux d'abandon onboarding | < 10% |
| Temps moyen par session | 15-30 secondes |
| Taux de rétention J7 | > 40% |
| Note App Store | > 4.5/5 |
| Mentions "simple" dans reviews | > 30% |

---

## 🔄 Itération Continue

### Feedback Utilisateur

1. **Analytics** : Suivre les points de friction
2. **Reviews** : Analyser les commentaires
3. **Tests utilisateurs** : Observer de vraies personnes
4. **A/B Testing** : Tester les variations

### Questions à se poser régulièrement

- Cette fonctionnalité est-elle vraiment nécessaire ?
- Peut-on simplifier cette interaction ?
- Un enfant de 10 ans comprendrait-il ?
- Y a-t-il une façon plus directe de faire ça ?

---

## 💡 Inspiration

### Apps Référence (Simplicité)

- **Headspace** : Onboarding fluide
- **Calm** : Design minimaliste
- **Duolingo** : Gamification subtile
- **Forest** : Feedback visuel fort
- **Simple** (Banking) : Clarté extrême

### Principe KISS

> "Keep It Simple, Stupid"
> 
> La meilleure interface est celle qu'on ne remarque pas.

---

## 🎨 Design System Simplifié

### Composants Réutilisables

1. **PrimaryButton** : 1 seul style, gros, coloré
2. **StatsCard** : Format uniforme
3. **ProgressCircle** : Élément central
4. **SimpleDialog** : Confirmations rapides

### Règle de Cohérence

- Même padding partout (16dp)
- Même border radius (12dp)
- Même durée d'animation (300ms)
- Même police (System default)

---

## 📱 Accessibilité = Simplicité

### Principes Inclusifs

- **Contraste** : Minimum 4.5:1
- **Taille de texte** : Minimum 16sp
- **Zones de tap** : Minimum 48dp
- **Labels** : Toujours présents
- **Support TalkBack** : Navigation vocale

---

## 🎯 Conclusion

> "La simplicité est la sophistication suprême." - Léonard de Vinci

OneFast doit être si simple qu'utiliser l'app devient un plaisir, pas une corvée. Chaque pixel, chaque interaction, chaque mot doit servir l'utilisateur.

**Objectif final** : Que l'utilisateur se concentre sur son jeûne, pas sur l'application.
