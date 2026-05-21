#!/usr/bin/env python3
"""Merge missing string keys from values/strings.xml into locale files using EN + overrides."""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"

def parse_strings(path: Path) -> dict[str, str]:
    text = path.read_text(encoding="utf-8")
    out = {}
    for m in re.finditer(r'<(string|string-array)\s+name="([^"]+)"[^>]*>(.*?)</\1>', text, re.DOTALL):
        out[m.group(2)] = m.group(3).strip()
    return out

def escape_xml(s: str) -> str:
    # Do not escape apostrophes — Android accepts them inside double-quoted strings.
    return (
        s.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace('"', "&quot;")
    )

def format_entry(name: str, value: str, is_array: bool) -> str:
    if is_array or value.strip().startswith("<item>"):
        inner = value if value.strip().startswith("<item>") else f"        <item>{value}</item>"
        return f'    <string-array name="{name}">\n{inner}\n    </string-array>\n'
    return f'    <string name="{name}">{escape_xml(value)}</string>\n'

LOCALE_OVERRIDES: dict[str, dict[str, str]] = {
    "de": {
        "hydration_title": "Hydration",
        "pricing_title": "OneFast Pro",
        "pricing_subscribe": "Abonnieren",
        "pricing_restore": "Käufe wiederherstellen",
        "exact_alarm_permission_title": "Exakte Alarme",
        "exact_alarm_permission_body": "Damit wir Sie genau zum Ende des Fastens erinnern, erlauben Sie exakte Alarme in den Android-Einstellungen.",
        "exact_alarm_permission_allow": "Einstellungen öffnen",
        "exact_alarm_permission_later": "Später",
    },
    "es": {
        "hydration_title": "Hidratación",
        "pricing_subscribe": "Suscribirse",
        "pricing_restore": "Restaurar compras",
        "exact_alarm_permission_title": "Alarmas exactas",
        "exact_alarm_permission_body": "Para avisarte exactamente al terminar el ayuno, permite alarmas exactas en los ajustes de Android.",
        "exact_alarm_permission_allow": "Abrir ajustes",
        "exact_alarm_permission_later": "Más tarde",
    },
    "pt": {
        "hydration_title": "Hidratação",
        "pricing_subscribe": "Assinar",
        "pricing_restore": "Restaurar compras",
        "exact_alarm_permission_title": "Alarmes exatos",
        "exact_alarm_permission_body": "Para lembrar você exatamente ao fim do jejum, permita alarmes exatos nas configurações do Android.",
        "exact_alarm_permission_allow": "Abrir configurações",
        "exact_alarm_permission_later": "Mais tarde",
    },
    "ar": {
        "hydration_title": "الترطيب",
        "pricing_subscribe": "اشترك",
        "pricing_restore": "استعادة المشتريات",
        "exact_alarm_permission_title": "منبهات دقيقة",
        "exact_alarm_permission_body": "لتذكيرك بدقة عند انتهاء الصيام، اسمح بالمنبهات الدقيقة في إعدادات أندرويد.",
        "exact_alarm_permission_allow": "فتح الإعدادات",
        "exact_alarm_permission_later": "لاحقاً",
    },
    "tr": {
        "hydration_title": "Hidrasyon",
        "pricing_subscribe": "Abone ol",
        "pricing_restore": "Satın alımları geri yükle",
        "exact_alarm_permission_title": "Kesin alarmlar",
        "exact_alarm_permission_body": "Orucunuz bittiğinde tam zamanında hatırlatmak için Android ayarlarından kesin alarmlara izin verin.",
        "exact_alarm_permission_allow": "Ayarları aç",
        "exact_alarm_permission_later": "Sonra",
    },
}

def main():
    base = parse_strings(RES / "values/strings.xml")
    en = parse_strings(RES / "values-en/strings.xml")

    for locale in ["values-ar", "values-de", "values-es", "values-pt", "values-tr", "values-en"]:
        path = RES / locale / "strings.xml"
        if not path.exists():
            continue
        existing = parse_strings(path)
        missing = [k for k in base if k not in existing]
        if not missing:
            print(f"{locale}: OK")
            continue

        loc_key = locale.replace("values-", "")
        overrides = LOCALE_OVERRIDES.get(loc_key, {})
        block = "\n    <!-- Auto-synced missing keys -->\n"
        for key in sorted(missing):
            val = overrides.get(key) or en.get(key) or base[key]
            is_array = "tips_bodies" in key or "tips_titles" in key or key == "ramadan_tips"
            block += format_entry(key, val, is_array)

        text = path.read_text(encoding="utf-8")
        text = text.replace("</resources>", block + "</resources>")
        path.write_text(text, encoding="utf-8")
        print(f"{locale}: added {len(missing)} keys")

if __name__ == "__main__":
    main()
