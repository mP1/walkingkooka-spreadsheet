[![Build Status](https://travis-ci.com/mP1/walkingkooka-spreadsheet.svg?branch=master)](https://travis-ci.com/mP1/walkingkooka-spreadsheet.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-spreadsheet/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-spreadsheet?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-spreadsheet.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-spreadsheet/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-spreadsheet.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-spreadsheet/alerts/)



A web based spreadsheet application.

## High level Achievements, Goals, Wishlist

The list is not exhaustive but does provide some detail of numerous sub systems or building blocks that a complete spreadsheet web application
require.

- Tests: There are tests and mixins to assist testing in many forms.
- Parsing formula expressions into a tree of expression tokens
- Support for 32/64bit/Unlimited precision integers/decimal point numbers with transparent conversion at execution.
- Support during formula evaluation for cell references, labels and ranges
- Conversion during evaluation between data types is open and transparent during evaluation, with potential for introduction of user selected
  types and conversion strategies.
- Row, Cell and Range creation, insertion, deletion concepts along with storage.
- Custom Number, Date/Time, Text formats: Mostly completed except for fractions which remain outstanding.
- Conditional formatting - basic foundation available
- Functions within formula expressions are supported but the 300 or so different formulas within Excel remain outstanding.
- Storage: Storage abstractions are available and currently use memory structures (Maps) and are intended for quick and fast testing.
  It should be relatively painless to introduce implementations that use a real persistant storage such as a RDBMS or similar technology.
- Security: This has not been implemented, however a design is available that exists as a distinct system with permissions
  and roles for users and groups matched to individual components within the entire Spreadsheet document. Limited support for these components
  are already available, and such components includes:
  - Cells
  - Ranges
  - Columns
  - Rows
  - Comments
  - Live data sources: This concept requires some expansion
- Multiple live data sources: 
  - A csv file could provided a view of columns and rows
  - A table from a URL identified by an xpath.
  - Many other, one possibility would be to expose the interface and allow user selected implementations.
  - The above ranges would be read only from the web, but updates notified by period fetching or watching the filesystem for user request.   
- Authentication: The authentication system must be separate and pluggable and not deeply embedded within the Spreadsheet in any manner.
- REST interface: Spreadsheet CRUD and individual operations such as cell evaluation, and other operations are currently exposed in JSON.
- Numerous customisation of the spreadsheet to the individual are already captured in metadata, these includes:
  - Default (numerous) parsing and formatting patterns for the different base custom types: numbers, date/times, text.
  - Locale aware choices
  - Other spreadsheet metadata such as default date system base, audit metadata and more.
- Data validation: This remains outstanding.
- Rich text: All text is stored in a platform agnostic manner that supports numerous attributes to provide a rich text
  experience, including colour, text styling and more.
- Web UI: Currently react and probably a MaterialUI look and feel has been selected and work has started using the provided working server REST services.
- Reports created by mixing text and images together with variable placeholders that refer to artifacts in a spreadsheet.
- Plugin support for components such as:
  - parsing
  - formatting
  - formulas
  - data sources
- A mirror system would be required to introduce a layer for security (eg guard and limit reflection access) and to
  provide abstractions around system facilities which require attention (file and network access).

The summary above is very brief and tickets creation for individual work items remain outstanding.


## [Sample](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/test/java/walkingkooka/spreadsheet/sample/Sample.java)

A working sample that demonstrates a working engine that creates 2 cells, one referencing the value of the other
and evaluates the formula of both. All other spreadsheets are supported, such as a conditional format, labels and more.
The above link has the full sample.

```java
final SpreadsheetCellStore cellStore = cellStore();
final SpreadsheetLabelStore labelStore = SpreadsheetLabelStores.treeMap();

final SpreadsheetEngine engine = engine(cellStore, labelStore);
final SpreadsheetEngineContext engineContext = engineContext(engine, labelStore);

engine.saveCell(SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("A1"), SpreadsheetFormula.with("12+B2")), engineContext);

final SpreadsheetDelta delta = engine.saveCell(SpreadsheetCell.with(SpreadsheetCellReference.parseCellReference("B2"), SpreadsheetFormula.with("34")), engineContext);

final Set<String> saved = delta.cells()
        .stream()
        .map(c -> c.formula().value().get().toString())
        .collect(Collectors.toCollection(Sets::sorted));

// a1=12+b2
// a1=12+34
// b2=34
assertEquals(Sets.of("46", "34"), saved);
```



## Getting the source

You can either download the source using the "ZIP" button at the top
of the github page, or you can make a clone using git:

```
git clone git://github.com/mP1/walkingkooka-spreadsheet.git
```
 