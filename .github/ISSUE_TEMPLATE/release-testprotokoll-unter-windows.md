---
name: Release Testprotokoll unter Windows
about: Vorlage für Release Testprotokoll unter Windows
title: Release 12.X.XX unter Windows
labels: test
assignees: ''

---

Getestet mit Server xxxxxx

# Testprotokoll unter Windows

## Anmeldung
- [ ]  Anmeldung an den Server mittles Default - Profil
- [ ] Anmeldung an den Server durch manuelles eintragen der Anmeldedaten
- [ ] Wiederholtes Anmelden mit einem Profil, bei dem das Passwort falsch eingetragen wurde und die Anwendung direkt darauf geschlossen wurde (siehe 2. Kommentar #388)
- [ ] Wenn sich die ModelVersion geändert hat wird die workbench.xmi mit entsprechender Warnung gelöscht (Anwendung startet wie mit -clearPersistedState)

## Keine Verbindung zum CAS möglich
- [ ] Die angezeigte Fehlermeldung enthält Details zum Fehler
- [ ] Beim Indexladen: Fehlermeldung und Knopf ist wieder aktivierbar
- [ ] Beim Öffnen einer Maske: Wenn die Maske schon einmal geladen wurde wird diese verwendet

## Workspace-Ordner
- [ ] Der aktuelle Workspace-Ordner wird in den Einstellungen angezeigt
- [ ] Der aktuelle Workspace-Ordner kann über die Einstellungen gelöscht werden
- [ ] Das Löschen eines Profils in der Login-Maske löscht auch den entsprechenden Workspace-Ordner

## Suche (Knöpfe in der Toolbar)
- [ ]  Suchtabelle kann zurückgesetzt werden
- [ ]  Eine Zeile der Suchtabelle kann gelöscht werden
- [ ]  Breite der Spalten kann optimiert werden

### Suchkriterien 
- [ ] Default Suchkriterien werden beim Starten geladen
- [ ] Mehrere Suchkriterien können unter verschiedenen Namen gespeichert werden
- [ ] Nicht-Default Suchkriterien können geladen werden
- [ ] Suchkriterien können gelöscht werden

## Index (Knöpfe in der Toolbar)
- [ ] Index kann geladen werden und ist nach Suchkriterien gefiltert
- [ ] Bei Gruppierung können die Gruppen erweitert und zusammengeklappt werden
- [ ] Daten können exportiert werden
- [ ]  Breite der Spalten kann optimiert werden

### Indexdruck + Druckeinstellungen
<img width="946" alt="Bildschirmfoto 2021-04-20 um 20 49 45" src="https://user-images.githubusercontent.com/20420898/115448394-f4361800-a219-11eb-8971-d0a023aa49bf.png">

- [ ] Alle Spalten haben die selbe Reihenfolge wie auch in der Anwendung angezeigt (Reihenfolge kann verändert werden)
- [ ] XML/XSL können erstellt werden (in /< Workspaceordner >/PDF/)
- [ ] Schriftgröße kann verändert werden
- [ ] Spaltenbreite kann optimiert werden, ansonsten wird Breite aus Index übernommen
- [ ] Leere Spalten können verborgen werden
- [ ] Gruppenspalten können verborgen werden
- [ ] Suchkriterien können angezeigt werden 
- [ ] Interne Vorschau kann aktiviert werden 

## Details (Knöpfe in der Toolbar)
- [ ] Einträge können gespeichert werden
- [ ] Ein neues Detail kann erstellt werden
- [ ] Ein Datensatz kann gelöscht werden
- [ ] Änderungen können verworfen werden
- [ ] Die Breite der Anwendung kann optimiert werden

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
- [ ] Perspektiven können angeheftet werden und bleiben in der Leiste, wenn man sie schließt
- [ ] Angeheftete und dann geschlossene Perspektiven können wieder geöffnet werden
- [ ] Angeheftete Perspektiven werden persistiert
- [ ] Die Reihenfolge der Perspektiven ist nach einem Neustart wieder gleich (zum Testen z.B.: normale, angeheftete und geschlossene, normale)

## Keybindings
Wenn nicht anders vermerkt, sollen die Shortcuts in der ganzen Anwendung funktionieren.
- [ ] crtl+Q: Anwendung beenden
- [ ] crtl+Z: Undo (in Textfeldern)
- [ ] crtl+S: Detail speichern (nur wenn Detailpart im Fokus ist)
- [ ] crtl+N: neues Detail
- [ ] F4: Search Part auswählen -> Erstes Feld wird ausgewählt
- [ ] F5: Index neu laden
- [ ] F6: Detail Part auswählen -> Erste Zelle wird selektiert
