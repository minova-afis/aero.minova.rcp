# Changelog
All wesentlichen Änderungen für dieses Projekt werden hier dokumentiert.

Das Format basiert auf [Keep a Changelog](https://keepachangelog.com/en/1.0.0).

Mögliche Tags für Änderungen:

- `Neu` für neue Funktionalitäten
- `Änderung` für Änderungen an bestehenden Funktionalitäten
- `Bugfixes` für Ausbessern von Fehlern
- `Doku` für neue/geänderte Dokumentation

Der entsprechende Abschnitt des Changelogs wird auch jeweils in die [Releasenotes](https://github.com/minova-afis/aero.minova.rcp/releases) kopiert.

## [Unreleased]

### Neu

## [12.7.0] - 17.01.2023
**ACHTUNG: Ab diesem Release wird JAVA Version 17 benötigt!**

### Änderung
- Benötigte Java Version von 11 auf 17 ändern
- Java 17 statt Java 11 ausliefern
- JaxB2-Version auf 3.1.0
- Überall im Projekt Java-Version nutzen, statt hardcoded Version
- GitHub Workflows auf Java 17 geupgraded
- Java Version in den MANIFEST.MFs auf 17 aktualisiert

## [12.6.0] - 09.01.2023

### Neu
- use-Resolve-Parms Eigenschaft kann ab jetzt in den Lookups verwendet werden. Damit werden die übergebenen Paramter auch in die Resolve-Prozedur übergeben. Zuvor war dies nur für die List-prozeduren möglich!
- QuantityField. Ein neues Feld für die gleichzeitige Angabe von Wert und Einheit

### Änderung
- Methode callProcedureWithTable deprecated
- Meldung "Letzter Zustand wird wieder hergestellt" entfernen


## [12.5.0] - 21.11.2022

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**


### Neu
- Screenshots von Sections und der Detail-Toolbar können über Rechtsklick erstellt werden

### Änderung

- Tabelle tSiteParameter NICHT schon beim Starten anfragen. Tabelle wird erst angefragt, wenn ein Helper einen Wert tatsächlich benötigt. Damit werden Exceptions verhindert, wenn ein Projekt nicht auf data.schema aufbaut.

### Bugfixes
- Auslesen von festen Werten aus xbs korrigiert
- Index-Druck: 
  - Summary-Row ausbessern, Summary an richtigen Spalte anzeigen
  - Doubles formattieren wie in Maske angegeben

## [12.4.2] - 07.09.2022

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**

### Neu

### Änderung

### Bugfixes
- Browser wird beim Aufrufen einer READ-Prozedur geleert

## [12.4.1] - 06.09.2022

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**

### Neu

### Änderung

### Bugfixes
- Dateien im Browser Widget werden in voller Größe angezeigt

## [12.4.0] - 2022-09-02

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**

### Neu
- Statische Werte können über XBS konfiguriert werden
- Browser Section, für die Darstellung von Attachments im Detail
- PageOrHeadOrGridWrapper ausgelagert in eigene Klasse, SectionWrapper

### Bugfixes
- Helper für OP-Grids nicht doppelt hinzufügen

## [12.3.0] - 2022-08-25

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**

### Neu
- CI pipeline mit SonarQube einrichten

### Änderung
- Index nach Blockieren neu Laden, wenn Einstellung gesetzt ist
- Aufruf zum Auflisten der Werte bei Read-Only Lookups nicht ausführen
- Tests für ParamStringUtil
- GridChangeEvent um die geänderte Zeile erweitert
- RowsToInsert/ RowsToUpdate/ RowsToDelete über MGrid abrufbar gemacht
- Bump maven-deploy-plugin from 2.8.2 to 3.0.0
- Anfragen/Antworten ans/vom CAS im Login-Dialog Loggen
- Tests für ValueDe-/Serializer
- Unnötige Tests für die Anzahl an TimeZones entfernen, PreferencewindowTests beim Bauen ausführen
- Einstellung des Dark-Modes unter MacOS ignorieren
- Code smells, Bugs und Security Hotspots mit Hilfe von Sonarqube entfernen
- Ersten drei Radioboxen in selbe Zeile wie Label, Label rechtsbündig
- Nicht mehr benötigte und fehlerhafte Datei AFIS_MDI.mdi entfernen
- Mehrere Helper pro Maske unterstützen, auch Helper aus OPs nutzen
- Fehler bei Login Loggen, Verwirrende Meldung ("too many authentication attempts") entfernen 
- Bei nicht-pflicht Lookups einzigen Wert nicht automatisch eintragen
- Hinweis "Keine Datensätze geladen" als Benachrichtigungs-Popup anzeigen
- Connection-String und Username in Einstellungen anzeigen

### Bugfixes
- Auswahl von Radioboxen komplett entfernen, wenn ein bereits ausgewähltes Element geklickt wird 
- Revert mit vorbelegten Feldern funktioniert nun wie erwartet
- Indexdruck von Masken mit Leerzeichen im Titel ermöglichen
- ParamString Felder speichern nun ShortDate/-Time Werte korrekt
- Zum Überprüfen, ob ein Datensatz im Detail geladen ist, erstes primary-Feld nutzen, statt fest Feld mit "KeyLong". Damit kann auch in Masken ohne "KeyLong" Feld gelöscht werden
- Indexdruck mit DateTimeSpalten ermöglichen, Datums-/Zeitformat und Zeitzone aus Einstellungen nutzen
- Beschreibung von Lookups in Wizards anzeigen
- DirtyFlag verbessern  
  - Flag nach Laden eines Datensatzes neu berechnen 
  - `null` Values von ParamString und Period korrekt überprüfen
- ParamString verbessern
  - Weitere Felder im selben Abschnitt ermöglichen
  - Unterfelder entfernen, wenn null-Form aufgerufen wird
  - Nach Neuzeichnen der Section selbes Feld wieder auswählen


## [12.2.1] - 2022-07-14

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**

### Bugfixes
- Update von ParamString Feldern ausbessern


## [12.2.0] - 2022-07-13

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**

### Neu
- Einstellung "Dateien nicht lokal zwischenspeichern"

### Änderung
- Tabliste einer Section updaten, wenn sich read-only eines Feldes ändert
- Nach Tab/Enter automatisch zu ausgewählten Feld scrollen

### Bugfixes
- Exception vermeiden, wenn eine Section keine ID hat
- Die korrekte TimeZone verwenden, dass bei Eingabe von "0 0" im DateTimeField die korrekte Zeit wiedergegeben wird

## [12.1.1] - 2022-07-1

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**

### Änderung
- IDataService um Methoden getHttpClientBuilder() und getServer() erweitert

### Bugfixes
- Exception vermeiden, wenn kein Datepattern gegeben ist
- Radioboxen unter Windows nutzbar machen

## [12.1.0] - 2022-07-06

**ACHTUNG: Dieser Release benötigt mindestes CAS Version 12.38.0. Damit ist er nicht für die Stundenerfassung geeignet!**

### Neu
- Neuer Feldtyp "Periode"
- Neuer Feldtyp "Radiobox"
- Felder können über Helper auf (un-)sichtbar gesetzt werden

### Änderung
- Index-Anfragen ans CAS über POST-Requests (benötigt mindestens CAS Version 12.38.0)
- Abschnitte/Sections können nur minimiert werden, wenn sie ein Icon besitzen
- Eigene Klasse zum Anzeigen und Parsen von Fehlermeldungen und Benachrichtigungen
- Eigene Klasse für das Checken und Anzeigen des Dirty-Flags
- IWindowCloseHandler über Model Addon registriert
- Anwendung startet auch wenn keine xbs gegeben ist
- Eine Default-Fehlermeldung wird angezeigt, wenn der Server eine komplett leere Antwort liefert
- Das Feld mit dem eigentlichen Param-String Text (z.B. {0-8-4}test{1-0-0}{2-7-1}2...) wird nicht mehr angezeigt
- Kleine Lücke zwischen Nummerfeld und Einheit sowie Lookup und Beschreibung
- Äbhängigkeit auf log4j entfernt
- Tycho und Tycho-pomless Version 2.7.4 nutzen

### Bugfixes
- Tag `visible=false` von Pages in der xml-Maske wird ausgewertet
- Knopf "Wokrspace Löschen" unter Windows löscht alle Einstellungen und persistierte Daten
- Wenn beim Persistieren eines Grids ein Fehler auftritt kann beim nächsten Öffnen das Detial trotzdem aufgebaut werden
- Feld anzeigen, wenn visible=true gesetzt wird, auch wenn das Feld laut Maske nicht sichtbar ist
- Dirty-Flag funktioniert auch mit Para-String Feldern
- Fehlermeldung bei Timeout in Lookups wird angezeigt
- Fehlermeldung beim Laden einer Datei vom CAS wird angezeigt
- Lookups und Textfelder haben gleiche Breite
- Wenn der eingegebene Text in einem Lookup genau mit einem Matchcode übereinstimmt rückt dieses Element an die erste Stelle der Liste und kann auch mit der Maus ausgewählt werden
- Keine Fehlermeldung, wenn erstes Element in Lookup ein Sonderzeichen enthält
- Kopfsection in Statistik-Ansicht wird sofort nach dem Erstellen gestylt

### Doku
- [Erstellen von Grids in XML Maske](https://github.com/minova-afis/aero.minova.rcp/wiki/Erstellen-von-Grids-in-XML-Maske)
