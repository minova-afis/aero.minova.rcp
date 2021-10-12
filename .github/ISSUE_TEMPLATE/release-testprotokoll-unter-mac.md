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
- [ ] Wenn sich die ModelVersion geändert hat wird die workbench.xmi mit entsprechender Warnung gelöscht (Anwendung startet wie mit -clearPersistedState)

## Keine Verbindung zum CAS möglich
- [ ] Die angezeigte Fehlermeldung enthält Details zum Fehler
- [ ] Beim Indexladen: Fehlermeldung und Knopf ist wieder aktivierbar
- [ ] Beim Öffnen einer Maske: Wenn die Maske schon einmal geladen wurde wird diese verwendet

## Workspace-Ordner
- [ ] Der aktuelle Workspace-Ordner wird in den Einstellungen angezeigt
- [ ] Der aktuelle Workspace-Ordner kann über die Einstellungen geöffnet werden (im File Explorer des jeweiligen Betriebssystems)
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
<img width="823" alt="Bildschirmfoto 2021-04-20 um 22 27 50" src="https://user-images.githubusercontent.com/77741125/115459729-acb68880-a227-11eb-9ce3-49d04540c6d9.png">

- [ ] Alle Spalten haben die selbe Reihenfolge wie auch in der Anwendung angezeigt (Reihenfolge kann verändert werden)
- [ ] XML/XSL können erstellt werden (in /< Workspaceordner >/PDF/)
- [ ] Schriftgröße kann verändert werden
- [ ] Spaltenbreite kann optimiert werden, ansonsten wird Breite aus Index übernommen
- [ ] Leere Spalten können verborgen werden
- [ ] Gruppenspalten können verborgen werden
- [ ] Suchkriterien können angezeigt werden 
- [ ] Interne Vorschau kann aktiviert werden 

## Detail (Knöpfe in der Toolbar)
- [ ] Einträge können gespeichert werden
- [ ] Ein neues Detail kann erstellt werden
- [ ] Ein Datensatz kann gelöscht werden
- [ ] Änderungen können verworfen werden
- [ ] Die Breite der Anwendung kann optimiert werden

## Detail Grid
- [ ] Daten werden geladen (Bei Auswahl eines Datensatzes im Index)
- [ ] Pflichtfelder sind orangenmarkiert
- [ ] Detail kann erst gespeichert werden, wenn alle Pflichtfelder ausgefüllt sind
- [ ] Lookups sind als Kombobox ausfüllbar

## Detail Grid (Knöpfe in der Toolbar)
- [ ] Neue Zeile wird hinzugefügt
- [ ] Ausgewählte Zeilen werden gelöscht (nur aktiv wenn mindestens eine Zeile komplett ausgewählt wird)
- [ ] Toggle zwischen allen Zeilen werden angezeigt und ~5 Zeilen angezeigt (min 2, max 10). Außerdem werden Zeilenhöhen optimiert *2
- [ ] Toggle zwischen kompletter Nattable wird angezeigt und Section hat "Standard" Breite. Außerdem werden Spaltenbreiten optimiert *1

## Detail Grid (KeyBindings)
- [ ] SHIFT + M1 + N: neue Zeile wird hinzugefügt
- [ ] SHIFT + M1 + D: markierte Zeile/-n löschen (nur aktive wenn eine ganze Zeile markiert ist)
- [ ] SHIFT + M1 + H: breiten Optimierung (siehe *1 bei Detail Grid (Knöpfe in der Toolbar))
- [ ] SHIFT + M1 + V: höhen Optimierung (siehe *2 bei Detail Grid (Knöpfe in der Toolbar))

## Dirty Flag
- [ ] Dirty Flag wird beim Detail gesetzt, sobald dieser bearbeitet wird
- [ ] Dirty Flag wird bei der Perspektive gesetzt, sobald ein Part darin das Dirty Flag hat
- [ ] Dirty Flag wird sofort überall entfernt, wenn die Änderungen rückgängig gemacht wurden
- [ ] Nachricht wird angezeigt, dass alle Änderungen verworfen werden, wenn ein neuer Datensatz im Index selektiert wird. (Nur wenn ShowDiscardChangesDialogIndex gesetzt ist)
- [ ] Nachricht wird angezeigt, dass alle Änderungen verworfen werden, wenn die Anwendung geschlossen wird. Die geänderten Perspektiven werden aufgelistet
- [ ] Nachricht wird angezeigt, wenn eine geänderte Perspektive geschlossen werden soll

## Traverse Verhalten
- [ ] Tab selektiert das nächste Feld (Wenn SelectAllControls **nicht** gesetzt ist)
- [ ] Tab selektiert alle Controls (Toolbar, Section usw.) (Wenn SelectAllControls gesetzt ist)
- [ ] Ctrl + Tab in einem Beschreibung-/ Textfeld bewirken ein Tabstop
- [ ] Tab verlässt nie den Part
- [ ] Tab selektiert beim Grid nur das erste Feld (zweiter Tab in der NatTable verlässt die NatTable)
- [ ] Enter selektiert das **nächste leere** Pflichtfeld (Wenn EnterSelectsFirstRequired **nicht** gesetzt ist)
- [ ] Enter selektiert das **erste leere** Pflichtfeld (Wenn EnterSelectsFirstRequired gesetzt ist)
- [ ] Bei Enter in einer Auswahlbox bleibt man im **selben** Feld (Wenn LookupEnterSelectsNextRequired **nicht** gesetzt ist)
- [ ] Enter in einer Auswahlbox selektiert das **nächste leere**  Pflichtfeld (Wenn LookupEnterSelectsNextRequired gesetzt ist, EnterSelectsFirstRequired ist egal)
- [ ] Enter öffnet geschlossene Section, wenn ein Pflichtfeld darin selektiert wird
- [ ] Ctrl + Enter in einem Beschreibung- / TextFeld bewirkt einen Zeilenumbruch

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

## Keybindings Perspektive
KeyBIndings die in der ganzen Perspektive ansprechbar sind:
- [ ] M1+Q: Anwendung beenden
- [ ] M1+R: Resize Parts
- [ ] F4: Search Part auswählen -> Erstes Feld wird ausgewählt
- [ ] F5: Index neu laden
- [ ] F6: Detail Part auswählen -> Erste Zelle wird selektiert

## Key-Bindings im DetailPart
KeyBIndings die nur im DetailPart ansprechbar sind:
- [ ] M1+Z: Undo (in Textfeldern)
- [ ] M1+S: Detail speichern (nur wenn Detailpart im Fokus ist)
- [ ] M1+N: neues Detail
- [ ] CR: Nächstes leeres Pflichtfeld wird selektiert oder es wird gespeichert

## Key-Bindings im SearchPart
KeyBIndings die nur im SearchPart ansprechbar sind:
- [ ] M1+N : Alle Einträge aus den Suchzeilen werden gelöscht. Es werden alle Zeilen bis auf eine entfernt.
- [ ] M1+D : Selektierte Zeile wird gelöscht
- [ ] M1+S : Suchkriterien speichern (nur wenn SearchPart im Fokus ist)
- [ ] SHIFT+M1+S : Suchkriterien speichern unter <Bezeichnung>
- [ ] SHIFT+M1+D : Löschen von gespeicherten Suchkriterien
- [ ] M1+L : Default Suchkriterien laden
- [ ] SHIFT+M1+H : Optimieren (Resize Horizontal)

## Key-Bindings im IndexPart
KeyBIndings die nur im IndexPart ansprechbar sind:
- [ ] M1+N: Neues Detail

## Preferences
- [ ] Die Sprachauswahl zeigt nur unterstützte Sprachen an
- [ ] Das angegebene Pattern für das Datum formatiert die Date-, Time- und DateTimeFields
- [ ] Das angegebene Pattern für die Zeit formatiert die Time- und DateTimeFields
- [ ] Datepattern erlaubt nur [.,/Leerzeichen] als Trennzeichen
- [ ] Timepattern erlaubt nur [:/Leerzeichen] als Trennzeichen
- [ ] Icons ändern sich, wenn die Schrift- und Icongröße geändert wird
- [ ] Wenn kein Pattern angegeben ist, wird der Standard verwendet (Test: Vorschlagsliste ist nicht leer)

## Plugin laden (Nur Stundenerfassung)
- [ ] Mitarbeiter im Detail ist vorbelegt (Nur wenn in den Preferences hinterlegt)
- [ ] Datum im Detail ist vorbelegt
- [ ] Arbeitszeit wird automatisch ausgerechnet
- [ ] Beim Betätigen von "Arbeitszeit füllen" öffnet sich ein Wizard
