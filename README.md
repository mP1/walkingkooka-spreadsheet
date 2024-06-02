[![Build Status](https://github.com/mP1/walkingkooka-spreadsheet/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-spreadsheet/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-spreadsheet/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-spreadsheet?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-spreadsheet.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-spreadsheet/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-spreadsheet.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-spreadsheet/alerts/)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

# Application

This repo contains the powerful engine that performs all the features and actions expected of a functional spreadsheet.

The application is logically separated into two parts.

- The [client](https://github.com/mP1/walkingkooka-spreadsheet-dominokit) contains the web browser application.
  Actions performed by the user become REST API calls to the server.
- The [server](https://github.com/mP1/walkingkooka-spreadsheet-server) contains all the supporting REST APIs using JSON
  for request and response payloads that eventually interact with
  [engine](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/engine/SpreadsheetEngine.java).

## Global settings ([SpreadsheetMetadata](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadata.java))

Each and every spreadsheet is represented by a single `SpreadsheetMetadata` object instance.
A wide variety of items are stored for each spreadsheet including but not limited to:

- Each item is allocated a
  unique [SpreadsheetMetadataPropertyName](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyName.java).
- Changes to any of these properties will force a recalculation and formatting of every cell in the spreadsheet.

- Spreadsheet identifier/name
  - [SpreadsheetId](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetId.java)
    The unique identifier for each spreadsheet.
  - [SpreadsheetName](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/SpreadsheetName.java)
    A descriptive name of the spreadsheet.
- Audit metadata such as the creator/last modified users and timestamps.
  - [creator](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameCreator.java)
  - [create timestamp](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameCreateDateTime.java)
  - [last modified by](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameModifiedBy.java)
  - [last modified timestamp](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameModifiedDateTime.java)
- Locale
  - [locale](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameLocale.java)
  - It is possible to replace the initial `Locale` symbols used when formatting numbers and dates.
    - Date
      - [TODO](https://github.com/mP1/walkingkooka-spreadsheet/issues/4129) User provided names for the days of the week
      - [TODO](https://github.com/mP1/walkingkooka-spreadsheet/issues/4130) User provided names for the months of the
        year
    - Numbers
      - [Currency symbol](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameCurrencySymbol.java)
      - [Decimal separator](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameDecimalSeparator.java)
      - [Exponent symbol](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameExponentSymbol.java)
      - [Group symbol](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameGroupSeparator.java)
      - [Negative sign](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameNegativeSign.java)
      - [Percentage symbol](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNamePercentageSymbol.java)
      - [Positive sign](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNamePositiveSign.java)
      - [Value separator](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameValueSeparator.java)
- Date
  - [DateTime offset](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameDateTimeOffset.java)
    Used to select the date for the numeric value of 0. This is used to select whether 1901 or 1904 is the starting
    epoch for date values.
  - [Default Year](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameDefaultYear.java)
  - [Two Digit Year](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameTwoDigitYear.java)
- Numbers
  - [NumberKind](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameExpressionNumberKind.java)
    - Mathematical computations in two flavours are supported
      - 64 bit fast with limited precision (12 decimal places) just like Excel and Google Sheets
      - Variable, slower supporting any number of digits of precision, more is slower.
        - [Precision](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNamePrecision.java)
        - [Rounding mode](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameRoundingMode.java)
- Formatting
  Some default format pattern(s) for each of the spreadsheet value types. Note cells can have their own format pattern
  assigned which will be used instead.
  - [Date](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern.java)
  - [DateTime](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern.java)
  - [Number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern.java)
  - [Text](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetTextFormatPattern.java)
  - [Time](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern.java)
  - [Custom formatters](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatter.java)
    - TODO Adding support for authoring (and uploading) or selecting from the store custom formatters.
  - [General digit count](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount.java)
    Controls the number of digits that can appear when the `General` format pattern is selected.
- Parsing
  - Some default parsing pattern(s) used to parse text into any of the supported spreadsheet value types.
    - [Date](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetDateParsePattern.java)
    - [DateTime](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePattern.java)
    - [Number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePattern.java)
    - [Time](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePattern.java)
    - TODO Adding support for authoring (and uploading) or selecting from the store custom formatters.
- Viewport
  These properties control the spreadsheet grid view.
  - [Frozen columns](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameFrozenColumns.java)
    How many column(s) are frozen if any.
  - [Frozen rows](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameFrozenRows.java)
    How many row(s) are frozen if any.
  - [Hide Zero Value](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameHideZeroValues.java)
    Control whether zero values are hidden or shown.
  - [Viewport](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameViewport.java)
    Includes the range of cells displayed and any Cell(s)/Column(s)/Row(s) selection.

## Internal components

There are many internal components that contribute to the core functionality of a spreadsheet. Eventually each of
these will be a plugin where users can contribute an alternative or supplementary choice.

### [SpreadsheetComparators](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/compare/SpreadsheetComparator.java)

All sorting is performed by using a selected `SpreadsheetComparator`, which is identical to a `java.util.Comparator`
but also includes a `type` property of `java.lang.Class`. The `type` property is used to convert each value prior to the
actual comparison.

This supports advanced features such as sorting a range of cells with

- Date
- DateTime
- Day of Month
- Day of Week
- Hour of AMPM
- Hour of Day
- Month of Year
- Nano of Second
- Number
- Second of Minute
- Text
- Text case-insensitive
- Time
- Year

It is thus possible to sort a column(s) in the following possible ways

- `day-of-month` then `month-of-year` then `year`
- `seconds-of-minute` then `minute-of-day` then `hour-of-day`

When sorting a cell-range/column/rows it is possible to sort each column/row with different `SpreadsheetComparator(s)`.

- [TODO](https://github.com/mP1/walkingkooka-spreadsheet-plugin/issues/15) Additional `SpreadsheetComparator(s)` may be
  provided via a
  custom [SpreadsheetComparatorProvider](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/compare/SpreadsheetComparatorProvider.java)

### [SpreadsheetFormatter](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatter.java)

A `SpreadsheetFormatter` is used to format the cell value into text that is displayed within the grid of cells.

There are several built-in SpreadsheetFormatter(s) one for each Spreadsheet type, each supporting the standard patterns
to allow
user customisation of that value type along with a single color.

- [date](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterDateTime.java)
  dd/mm/yyyy
- [date-time](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterDateTime.java)
  dd/mm/yyyy hh:mm:ss
- [General](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterGeneral.java)
  General
- [number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterNumber.java) $#.###
- [text](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterText.java) @
- [time](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterDateTime.java)
  hh:mm:ss

- [TODO](https://github.com/mP1/walkingkooka-spreadsheet-plugin/issues/14) Additional `SpreadsheetFormatter(s)` may be
  provided via a
  custom [SpreadsheetFormatterProvider](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatterProvider.java)

TODO