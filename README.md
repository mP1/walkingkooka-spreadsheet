[![Build Status](https://github.com/mP1/walkingkooka-spreadsheet/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-spreadsheet/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-spreadsheet/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-spreadsheet?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-spreadsheet.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-spreadsheet/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-spreadsheet.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-spreadsheet/alerts/)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

## Composition

A web based spreadsheet application. This project while central also includes features and more from other sister
projects [github](https://github.com/mP1).

- The [react](https://github.com/mP1/walkingkooka-spreadsheet-react) contains a pure web client with little if any
  spreadsheet logic.
- The [server](https://github.com/mP1/walkingkooka-spreadsheet-server) contains the many REST APIs (all json), which
  forward requests to services within this project. This project takes care of http/network/json type stuff.



## Tests

There are automated tests for all areas or components within the spreadsheet including related and dependent projects of
which there are many. These are mentioned first because they illustrate the care and features available and also provide
an example of the API usage.

- Helpers (interfaces with default/defender methods) are provided for basically all interfaces, think mixins.
- Helpers include numerous methods so tests concentrate on data, logic and not boilerplate.
- Mocks and fakes are also provided for all interfaces.
- Helpers include informative messages and asserts to ensure correctness, and pretty printed messages and context upon
  failures
- Since most interfaces are SAM or contain at most a few methods its quite simple to implement if necessary.

## Global settings ([Metadata](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadata.java))

- All global like settings are captured and not hardcoded [SEE](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadata.java).
- The user can change each and every one of these settings or values including the decimal point, locale and more and
  they are honoured.
- The [react web app](https://github.com/mP1/walkingkooka-spreadsheet-react) include numerous cypress integration tests
  that demonstrate updating of this global metadata and instant feedback such as cells being updated with new locale
  settings etc.
- User selectable support for `double` (fast, lower precision, less memory) or `java.math.BigDecimal` (slower, user
  selectable precision, more memory).
- User selectable rounding, precision support.
- The `SpreadsheetMetadata` class captures many properties/settings and makes for an interesting read to get a deeper understanding of internals.



## Functions

- About 30-40 functions are completed, into numerous projects, much work remains outstanding.
- Dozens of basic number type functions: [numbers](https://github.com/mP1/walkingkooka-tree-expression-function-number)
- Dozens of basic string type functions: [string](https://github.com/mP1/walkingkooka-tree-expression-function-string)
- Work remains to create projects to host other categories of functions such as: banking/finance etc.
- Ideas include the support of user/3rd party/open source functions which would execute within a sandbox.



## Storage

- Interfaces are defined for storage of all artifacts within a spreadsheet, including but not limited too cells, label &
  range definitions and more.
- Possible ideas may be separated into two broad categories of storage, read only and read/write.
- Examples of read/write include memory (DONE),
  RDBMS [TODO](https://github.com/mP1/walkingkooka-spreadsheet/issues/1291)
- Other forms of read only Storage would allow mixture of numerous data sources
- A CSV, TSV or XML file (uploaded, a network path, url) could provide a table like range, Rules would be implemented to
  update a local (server) cache copy.
- Same idea as for the previous read only files but for a table within a web page.



## Formatting

- Support for text, numbers, dates, date/time, time including honouring of spreadsheet set localization completed.
- Formatting numbers as fractions [TODO](https://github.com/mP1/walkingkooka-spreadsheet/issues/341)
- Conditional formatting is also supported.



## Data Validation

- Minimal work has been done in this area.
- This is another area where support for 3rd party plugins executed within a sandbox would help enable users to truely
  customise their experience and data security.



## Security

- Security and permissions remain outstanding.



## Comments / Multi-user

- Comments for individual cells or ranges [TODO](https://github.com/mP1/walkingkooka-spreadsheet/issues/352)
- Chat like features.
- Multi user support should be possible as each API call currently supports returning all related updated cells.



A lot has been done and a lot remains, the README and [issues](https://github.com/mP1/walkingkooka-spreadsheet/issues/) for each project are ideal starting grounds to discover progress and features.



## [Sample](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/test/java/walkingkooka/spreadsheet/sample/Sample.java)

A working sample that demonstrates a working engine that creates 2 cells, one referencing the value of the other and
evaluates the formula of both. All other spreadsheets are supported, such as a conditional format, labels and more. The
above link has the full sample.

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


 