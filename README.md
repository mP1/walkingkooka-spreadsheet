[![Build Status](https://github.com/mP1/walkingkooka-spreadsheet/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-spreadsheet/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-spreadsheet/badge.svg?branch=master)](https://coveralls.io/repos/github/mP1/walkingkooka-spreadsheet?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-spreadsheet.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-spreadsheet/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-spreadsheet.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-spreadsheet/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-spreadsheet)
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
      - User provided names for the days/months of the week [SpreadsheetCell](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/SpreadsheetCell.java#L305)
      - Custom decimal number symbols for each cell [SpreadsheetCell](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/SpreadsheetCell.java#L342)
    - Numbers
      - [Currency symbol](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameCurrencySymbol.java)
      - [Decimal separator](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameDecimalSeparator.java)
      - [Exponent symbol](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameExponentSymbol.java)
      - [Group symbol](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameGroupSeparator.java)
      - [Negative sign](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameNegativeSign.java)
      - [Percentage symbol](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNamePercentSymbol.java)
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
- Formatter
  Some default format pattern(s) for each of the spreadsheet value types. Note cells can have their own format pattern assigned which will be used instead.
  - [Date](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern.java)
  - [DateTime](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern.java)
  - [Number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetNumberFormatPattern.java)
  - [Text](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetTextFormatPattern.java)
  - [Time](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern.java)
  - [Custom formatters](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatter.java)
    - Adding support for authoring (and uploading) or selecting from the store custom formatters *SEE BELOW*
  - [General digit count](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameGeneralNumberFormatDigitCount.java)
    Controls the number of digits that can appear when the `General` format pattern is selected.
- Parser
  - Some default parsing pattern(s) used to parse text into any of the supported spreadsheet value types.
    - [Date](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetDateParsePattern.java)
    - [DateTime](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePattern.java)
    - [Number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetNumberParsePattern.java)
    - [Time](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/meta/SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePattern.java)
    - Adding support for authoring (and uploading) or selecting from the store custom parsers.
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

### [SpreadsheetConverters](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)

These `Converters` along with a few others belonging to other repos are used to convert values from one type to another.
These support expressions where values are converters as necessary from one value type to another, 
eg 

- basic
- [boolean-to-String](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterBooleanToString.java)
- [booleans](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- collection
- [color](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [color-to-number](https://github.com/mP1/walkingkooka-color/blob/master/src/main/java/walkingkooka/color/convert/ColorToNumberConverter.java)
- [color-to-text](https://github.com/mP1/walkingkooka-color/blob/master/src/main/java/walkingkooka/color/convert/ColorToTextConverter.java)
- [dateTimeSymbols](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetSelectionToSpreadsheetSelectionConverter.java)
- [decimalNumberSymbols](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetSelectionToSpreadsheetSelectionConverter.java)
- [environment](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [error-throwing](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterSpreadsheetErrorThrowing.java)
- [error-to-number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterSpreadsheetErrorToNumber.java)
- [expression](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [format-pattern-to-string](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterFormatPatternToString.java)
- [form-and-validation](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [general](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterGeneral.java)
- [has-style-style](https://github.com/mP1/walkingkooka-tree-text/blob/master/src/main/java/walkingkooka/tree/text/convert/HasTextStyleToTextStyleConverter.java)
- [json](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [json-to](https://github.com/mP1/walkingkooka-tree-json-convert/blob/master/src/main/java/walkingkooka/tree/json/convert/JsonNodeToUnmarshallingConverter.java)
- [locale](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [null-to-number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterNullToNumber.java) Handles converting null/missing cell values into 0, 10 + B2 when B2 is missing becomes 10 + 0. 
- [number-to-color](https://github.com/mP1/walkingkooka-color/blob/master/src/main/java/walkingkooka/color/convert/NumberToColorConverter.java)
- [number-to-number](https://github.com/mP1/walkingkooka-tree/blob/master/src/main/java/walkingkooka/tree/expression/convert/ExpressionNumberConverters.java)
- [parser](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [plugins](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [selection-to-selection](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetSelectionToSpreadsheetSelectionConverter.java)
- [selection-to-text](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterSpreadsheetSelectionToText.java)
- [simple](https://github.com/mP1/walkingkooka-convert/blob/master/src/main/java/walkingkooka/convert/SimpleConverter.java)
- [spreadsheet-cell-to](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterSpreadsheetCell.java)
- [spreadsheet-metadata](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [spreadsheet-value](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [style](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [template](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [text](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [text-to-color](https://github.com/mP1/walkingkooka-color/blob/master/src/main/java/walkingkooka/color/convert/TextToColorConverter.java)
- [text-to-environment-value-name](https://github.com/mP1/walkingkooka-environment/blob/master/src/main/java/walkingkooka/environment/convert/EnvironmentConverterStringToEnvironmentValueName.java)
- [text-to-error](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetError.java)
- [text-to-expression](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToExpression.java)
- [text-to-form-name](https://github.com/mP1/walkingkooka-validation/blob/master/src/main/java/walkingkooka/validation/convert/TextToFormNameConverter.java)
- [text-to-locale](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToLocale.java)
- [text-to-selection](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetSelection.java)
- [text-to-spreadsheet-color-name](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetColorName.java)
- [text-to-spreadsheet-formatter-selector](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetFormatterSelector.java)
- [text-to-spreadsheet-id](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetId.java)
- [text-to-spreadsheet-metadata](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetMetadata.java)
- [text-to-spreadsheet-metadata-property-name](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetMetadataPropertyName.java)
- [text-to-spreadsheet-metadata-color](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetMetadataColor.java)
- [text-to-spreadsheet-name](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToSpreadsheetName.java)
- [text-to-spreadsheet-text](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/textToSpreadsheetText.java)
- [text-to-template-value-name](https://github.com/mP1/walkingkooka-template/blob/master/src/main/java/walkingkooka/template/convert/StringToTemplateValueNameConverter.java)
- [text-to-text](https://github.com/mP1/walkingkooka-convert/blob/master/src/main/java/walkingkooka/convert/CharacterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrStringConverter.java)
- [text-to-text-node](https://github.com/mP1/walkingkooka-tree-text/blob/master/src/main/java/walkingkooka/tree/text/convert/TextToTextNodeConverter.java)
- [text-to-text-style](https://github.com/mP1/walkingkooka-tree-text/blob/master/src/main/java/walkingkooka/tree/text/convert/TextToTextStyleConverter.java)
- [text-to-text-style-property-name](https://github.com/mP1/walkingkooka-tree-text/blob/master/src/main/java/walkingkooka/tree/text/convert/TextToTextStylePropertyNameConverter.java)
- [text-to-url](https://github.com/mP1/walkingkooka-net-convert/blob/master/src/main/java/walkingkooka/net/convert/TextToUrlConverter.java)
- [text-to-validation-error](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverterTextToValidationError.java)
- [text-to-validator-selector](https://github.com/mP1/walkingkooka-validation/blob/master/src/main/java/walkingkooka/validation/convert/TextToValidatorSelectorConverter.java)
- [text-to-value-type](https://github.com/mP1/walkingkooka-validation/blob/master/src/main/java/walkingkooka/validation/convert/TextToValidationValueTypeNameConverter.java)
- [textNode](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [to-json](https://github.com/mP1/walkingkooka-tree-json-convert/blob/master/src/main/java/walkingkooka/tree/json/convert/ToJsonNodeMarshallingConverter.java)
- [to-number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [to-styleable](https://github.com/mP1/walkingkooka-tree-text/blob/master/src/main/java/walkingkooka/tree/text/convert/ToStyleableConverter.java)
- [to-text-node](https://github.com/mP1/walkingkooka-tree-text/blob/master/src/main/java/walkingkooka/tree/text/convert/HasTextNodeToTextNodeConverter.java)
- [to-validation-error-list](https://github.com/mP1/walkingkooka-validation/blob/master/src/main/java/walkingkooka/validation/convert/ValidationErrorListConverter.java)
- [url](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/convert/SpreadsheetConverters.java)
- [url-to-hyperlink](https://github.com/mP1/walkingkooka-tree-text/blob/master/src/main/java/walkingkooka/tree/text/convert/UrlToHyperlinkConverter.java)
- [url-to-image](https://github.com/mP1/walkingkooka-tree-text/blob/master/src/main/java/walkingkooka/tree/text/convert/UrlToImageConverter.java)

### [SpreadsheetComparators](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/compare/SpreadsheetComparator.java)

All sorting is performed by using a selected [SpreadsheetComparator](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/compare/SpreadsheetComparator.java),
which is identical to a `java.util.Comparator`. These may be enabled to supporting sorting one or more column/row/cell-range.

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

Examples of combining multiple `SpreadsheetComparators` for a column range might include.

- `day-of-month` then `month-of-year` then `year`
- `seconds-of-minute` then `minute-of-day` then `hour-of-day`

When sorting a cell-range/column/rows it is possible to sort each column/row with different `SpreadsheetComparator(s)`.

The plugin architecture allows authoring/installing custom comparators.

### [ExpressionFunction](https://github.com/mP1/walkingkooka-tree/blob/master/src/main/java/walkingkooka/tree/expression/function/ExpressionFunction.java)

Functions within a formula expressions are defined by individual `ExpressionFunction`.

Currently there are about 100+ functions available and these are
listed [HERE](https://github.com/mP1/walkingkooka-spreadsheet-expression-function).

- Additional `ExpressionFunction(s)` may be provided via a
  custom [ExpressionFunctionProvider](https://github.com/mP1/walkingkooka-tree-expression-function-provider/blob/master/src/main/java/walkingkooka/tree/expression/function/provider/ExpressionFunctionProvider.java)
  *DONE*
- Uploaded plugins [TODO](https://github.com/mP1/walkingkooka-spreadsheet-plugin/issues/16)

### [SpreadsheetFormatter](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatter.java)

A `SpreadsheetFormatter` is used to format the cell value into text that is displayed within the grid of cells.

There are several built-in SpreadsheetFormatter(s) one for each Spreadsheet type, each supporting the standard patterns
to allow user customisation of that value type along with a single color.

- dd/mm/yyyy [date](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterDateTime.java)
- dd/mm/yyyy hh:mm:ss [date-time](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterDateTime.java)
- default text [default-text](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/pattern/SpreadsheetPattern.java#L1163)
- General [General](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterGeneral.java)
- $0.00 [number](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterNumber.java)
- @ [text](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterText.java)
- hh:mm:ss [time](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetPatternSpreadsheetFormatterDateTime.java)
- [expression](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/ExpressionSpreadsheetFormatter.java)
- Additional `SpreadsheetFormatter(s)` may be provided via a custom [SpreadsheetFormatterProvider](https://github.com/mP1/walkingkooka-spreadsheet/blob/master/src/main/java/walkingkooka/spreadsheet/format/SpreadsheetFormatterProvider.java) *DONE*
- Uploaded plugins [TODO](https://github.com/mP1/walkingkooka-spreadsheet-plugin/issues/14)
- SpreadsheetEngine integration (Currently only patterns are used refactoring work needs to be
  done) [TODO](https://github.com/mP1/walkingkooka-spreadsheet/issues/4177)

### Other internal components

*TODO* Mention here
*TODO* Dynamic plugin support