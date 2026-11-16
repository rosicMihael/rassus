# RASSUS — 2. laboratorijska vježba (Kafka)

Početni **Gradle projekt** za drugu laboratorijsku vježbu iz Raspodijeljenih sustava.
Projekt ne sadrži implementaciju senzorskog čvora ni koordinatora.
Za zahtjeve i način rada koristite službeni tekst laboratorijske vježbe.

## Sadržaj

- `src/main/java/hr/fer/tel/rassus/clock/EmulatedSystemClock.java` — priložena klasa za emulaciju fizičkog sata (logika klase nije mijenjana).
- `src/main/resources/readings.csv` — ulazni podaci s očitanjima senzora, uključujući stupac `NO2`.
- `build.gradle` — dodane ovisnosti za Kafka klijenta, CSV i JSON.
- `gradlew.bat` i `gradle/wrapper/` — Gradle Wrapper **9.1.0**.

## Početak rada na Windowsu

Otvorite terminal u direktoriju projekta i izvršite:

```cmd
gradlew.bat build
```

Preporučuje se **Java 25**. Ova preporuka nije obveza; projekt ne nameće
fiksnu verziju JDK-a kroz Gradle toolchain.

Za pokretanje Kafke, odabir identifikatora, registraciju, slanje poruka i
sortiranje očitanja slijedite upute laboratorijske vježbe. Potrebne klase
za senzore i koordinator studenti implementiraju samostalno.
