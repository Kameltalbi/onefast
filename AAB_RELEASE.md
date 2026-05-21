# Générer l'AAB pour le Play Store

## 1. Produits Google Play (obligatoire pour les abonnements)

Créer dans Play Console → Abonnements, avec ces IDs exacts :

| ID produit | Tier |
|------------|------|
| `onefast_pro_monthly` | Pro |
| `onefast_pro_yearly` | Pro |
| `onefast_premium_monthly` | Premium |
| `onefast_premium_yearly` | Premium |

L'écran tarifs lance par défaut l'abonnement **annuel** (`*_yearly`).

## 2. Signature release

```bash
cp keystore.properties.example keystore.properties
# Éditer keystore.properties + placer release.keystore à la racine du projet
```

## 3. Build AAB

```bash
./gradlew bundleRelease
```

Sortie : `app/build/outputs/bundle/release/app-release.aab`

## 4. Checklist avant upload

- [ ] Tester achat / restauration sur piste interne
- [ ] Notifications + alarmes exactes sur Android 13+
- [ ] Vérifier les 7 langues (FR, EN, ES, DE, PT, AR, TR)
- [ ] Politique de confidentialité / CGU (liens dans Paramètres)
