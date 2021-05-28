---
name: Release Testprotokoll unter Mac
about: Vorlage für Release Testprotokoll unter Mac
title: Release 12.X.XX unter MacOS
labels: test
assignees: ''

---

Getestet mit Server xxxxxx

# Testprotokoll unter macOS

## Anmeldung
- [ ]  Anmeldung an den Server mittles Default - Profil
- [ ] Anmeldung an den Server durch manuelles eintragen der Anmeldedaten
- [ ] Wiederholtes Anmelden mit einem Profil, bei dem das Passwort falsch eingetragen wurde und die Anwendung direkt darauf geschlossen wurde (siehe 2. Kommentar #388)

### Gespeicherte Suchkriterien werden geladen
- [ ] Funktioniert

## Keine Verbindung zum CAS möglich
- [ ] Die angezeigte Fehlermeldung enthält Details zum Fehler
- [ ] Beim Indexladen: Fehlermeldung und Knopf ist wieder aktivierbar
- [ ] Beim Öffnen einer Maske: Wenn die Maske schon einmal geladen wurde wird diese verwendet

## Workspace-Ordner
- [ ] Der aktuelle Workspace-Ordner wird in den Einstellungen angezeigt
- [ ] Der aktuelle Workspace-Ordner kann über die Einstellungen gelöscht werden
- [ ] Das Löschen eines Profils in der Login-Maske löscht auch den entsprechenden Workspace-Ordner

## Indexdruck
<img width="823" alt="Bildschirmfoto 2021-04-20 um 22 27 50" src="https://user-images.githubusercontent.com/77741125/115459729-acb68880-a227-11eb-9ce3-49d04540c6d9.png">

- [ ] Alle Spalten haben die selbe Reihenfolge wie auch in der Anwendung angezeigt (Reihenfolge kann verändert werden)

### Druckeinstellungen
- [ ] XML/XSL können erstellt werden (Workspaceordner -> PDF)
- [ ] Schriftgröße kann verändert werden
- [ ] Spaltenbreite kann optimiert werden, ansonsten wird Breite aus Index übernommen
- [ ] Leere Spalten können verborgen werden
- [ ] Gruppenspalten können verborgen werden
- [ ] Suchkriterien können angezeigt werden 
- [ ] Interne Vorschau kann aktiviert werden 

## Traverselistener
- [ ] Tab selektiert das nächste Feld (Wenn SelectAllControls **nicht** gesetzt ist)
- [ ] Tab selektiert alle Controls (Toolbar, Section usw.) (Wenn SelectAllControls gesetzt ist)
- [ ] Enter selektiert das **nächste leere** Pflichtfeld (Wenn EnterSelectsFirstRequired **nicht** gesetzt ist)
- [ ] Enter selektiert das **erste leere** Pflichtfeld (Wenn EnterSelectsFirstRequired gesetzt ist)
- [ ] Bei Enter in einer Auswahlbox bleibt man im **selben** Feld (Wenn LookupEnterSelectsNextRequired **nicht** gesetzt ist)
- [ ] Enter in einer Auswahlbox selektiert das **nächste leere**  Pflichtfeld (Wenn LookupEnterSelectsNextRequired gesetzt ist, EnterSelectsFirstRequired ist egal)

## PerspectiveSwitcher
- [ ] Die Perspektive kann über das Menü oben geändert werden
- [ ] Die Perspektive kann über die Leiste unten geändert werden
- [ ] Bei einem Neustart sind in der Leiste die gleichen Perspektiven wieder vorhanden
- [ ] Perspektiven können über Rechtsklick geschlossen werden (inklusive der letzten)
- [ ] Es wird unterstützt, dass Masken in der application.mdi unterschiedliche Dateinamen und IDs haben (siehe #487)

## Keybindings
Wenn nicht anders vermerkt, sollen die Shortcuts in der ganzen Anwendung funktionieren.
- [ ] cmd+Q: Anwendung beenden
- [ ] cmd+Z: Undo (in Textfeldern)
- [ ] cmd+S: Detail speichern (nur wenn Detailpart im Fokus ist)
- [ ] cmd+N: neues Detail
- [ ] F4: Search Part auswählen
- [ ] F5: Index neu laden
- [ ] F6: Detail Part auswählen

# Tests für CTS VG Eibelstadt
 
## Maske Anruf
<img width="706" alt="Bildschirmfoto 2021-04-20 um 22 29 07" src="https://user-images.githubusercontent.com/77741125/115459886-da9bcd00-a227-11eb-806c-a44b78983c75.png">

- [ ] Erfassung eines Anrufs mittels Eingabe einer neuen Testperson
- [ ] Erfassung eines Anrufs mittels bereits existierender Testperson
- [ ] Gleichzeitiges erfassen von Terminen, wenn es 2 Slots gibt (Keiner vorher belegt)
- [ ] Gleichzeitiges erfassen von Terminen, wenn es 2 Slots gibt (Einer vorher belegt) -> Einer funktioniert, eine Fehlermeldung

<img width="574" alt="Bildschirmfoto 2021-04-20 um 22 35 35" src="https://user-images.githubusercontent.com/77741125/115460655-cf956c80-a228-11eb-8342-5fb660745e56.png">

- [ ] Testtermin der einer Person zugeordnet wurde, darf keiner anderen Person zugeordnet werden (überschreiben) 
(Zuordnung nehmen, und einer anderen Person zuordnen (Lookup))
- [ ]  Daten einer zugeordneten Testperson sollen geändert werden.  Zugeordneter Termin bleibt bestehen
(Die Angaben der Testperson werden aktualisiert, aber der Termin bleibt bestehen)

## Maske Testperson
- [ ] Passwort wird nicht im Klartext angezeigt
- [ ] Passwort der Testperson kann nicht geändert werden
- [ ] Passwort der Testperson kann zurückgesetzt werden

## Maske Anmeldung
<img width="679" alt="Bildschirmfoto 2021-04-20 um 22 33 37" src="https://user-images.githubusercontent.com/77741125/115460366-7a595b00-a228-11eb-92df-a0ad2f97355f.png">

### E-Mail Versand 
- [ ] positives Testergebnis an Testperson
- [ ] positives Testergebnis an Teststrecke
- [ ] negatives Testergebnis an Testperson
- [ ] negatives Testergebnis an Teststrecke

### Detaildruck (Maske Anmeldung)
- [ ] Funktioniert mit positiv
- [ ] Funktioniert mit negativ
