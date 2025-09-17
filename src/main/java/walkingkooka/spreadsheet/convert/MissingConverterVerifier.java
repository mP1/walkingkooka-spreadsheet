/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.convert;

import walkingkooka.collect.list.BooleanList;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.list.StringList;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.color.HslColor;
import walkingkooka.color.HsvColor;
import walkingkooka.color.RgbColor;
import walkingkooka.convert.Converter;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.LocalDateList;
import walkingkooka.datetime.LocalDateTimeList;
import walkingkooka.datetime.LocalTimeList;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.NumberList;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorException;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonArray;
import walkingkooka.tree.json.JsonBoolean;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNumber;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.text.Hyperlink;
import walkingkooka.tree.text.Image;
import walkingkooka.tree.text.Styleable;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationErrorList;
import walkingkooka.validation.ValidationValueTypeName;
import walkingkooka.validation.form.FormName;
import walkingkooka.validation.provider.ValidatorSelector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Helper that may be used to validate a submitted {@link Converter} is able to convert required types.
 * Note if a {@link UnsupportedOperationException} is thrown, it will be rethrown and the converter test not marked as a
 * FAIL. This is useful particularly in tests which may be using a {@link FakeSpreadsheetConverterContext} with some
 * unimplemented methods.
 */
final class MissingConverterVerifier {

    private final static List<Class<?>> NUMBER_TYPES = Lists.of(
        ExpressionNumber.class,
        Byte.class,
        Short.class,
        Integer.class,
        Long.class,
        Float.class,
        Double.class,
        BigInteger.class,
        BigDecimal.class
    );

    static {
        final List<Class<?>> types = Lists.array();
        types.addAll(NUMBER_TYPES);
        types.remove(Byte.class);
        types.remove(Short.class);

        NUMBER_TYPES_WITHOUT_BYTE_SHORT = types;
    }

    private static final List<Class<?>> NUMBER_TYPES_WITHOUT_BYTE_SHORT;

    private final static LocalDate DATE = LocalDate.of(
        1999,
        12,
        31
    );

    static {
        final List<Class<?>> types = Lists.array();
        types.add(Boolean.class);
        types.add(LocalDateTime.class);
        types.addAll(NUMBER_TYPES);
        types.remove(Byte.class);
        types.remove(Short.class);
        types.add(String.class);

        DATE_TO_TYPES = types;
    }

    private static final List<Class<?>> DATE_TO_TYPES;

    // time component must be zero otherwise DateTime -> integer types like BigInteger will fail
    private final static LocalDateTime DATE_TIME = LocalDateTime.of(
        1999,
        12,
        31,
        0,
        0,
        0
    );

    static {
        final List<Class<?>> types = Lists.array();
        types.add(Boolean.class);
        types.add(LocalDate.class);
        types.add(LocalTime.class);
        types.addAll(NUMBER_TYPES);
        types.remove(Byte.class);
        types.remove(Short.class);
        types.add(String.class);

        DATE_TIME_TO_TYPES = types;
    }

    private static final List<Class<?>> DATE_TIME_TO_TYPES;


    private final static LocalTime TIME = DATE_TIME.toLocalTime();

    static {
        final List<Class<?>> types = Lists.array();
        types.add(Boolean.class);
        types.add(LocalDateTime.class);
        types.addAll(NUMBER_TYPES);
        types.remove(Byte.class);
        types.remove(Short.class);
        types.add(String.class);

        TIME_TO_TYPES = types;
    }

    private static final List<Class<?>> TIME_TO_TYPES;

    /**
     * Note no tests actually involve converting {@link CharSequence} to something else, because marshalling
     * does not support the {@link CharSequence} interface types like {@link StringBuilder} etc.
     */
    static Set<MissingConverter> verify(final Converter<SpreadsheetConverterContext> converter,
                                        final SpreadsheetMetadataPropertyName<ConverterSelector> propertyName,
                                        final SpreadsheetConverterContext context) {
        Objects.requireNonNull(converter, "converter");
        Objects.requireNonNull(propertyName, "propertyName");
        Objects.requireNonNull(context, "context");

        final boolean find = SpreadsheetMetadataPropertyName.FIND_CONVERTER.equals(propertyName);
        final boolean formatting = SpreadsheetMetadataPropertyName.FORMATTING_CONVERTER.equals(propertyName);
        final boolean formula = SpreadsheetMetadataPropertyName.FORMULA_CONVERTER.equals(propertyName);
        final boolean scripting = SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER.equals(propertyName);
        final boolean sort = SpreadsheetMetadataPropertyName.SORT_CONVERTER.equals(propertyName);
        final boolean validation = SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER.equals(propertyName);

        final MissingConverterVerifier finder = new MissingConverterVerifier(
            converter,
            context
        );

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCellRangeReference cellRange = SpreadsheetSelection.parseCellRange("B2:C3");
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("A");
        final SpreadsheetColumnRangeReference columnRange = SpreadsheetSelection.parseColumnRange("B:C");
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("1");
        final SpreadsheetRowRangeReference rowRange = SpreadsheetSelection.parseRowRange("2:3");

        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final ExpressionNumberKind kind = context.expressionNumberKind();
        final SpreadsheetError error = SpreadsheetError.referenceNotFound(cell);
        SpreadsheetErrorKind.NAME.setMessage("Value");

        final SpreadsheetFormatterSelector formatterSelector = SpreadsheetFormatterSelector.parse("test-formatter");
        final SpreadsheetParserSelector parserSelector = SpreadsheetParserSelector.parse("test-parser");
        final ValidatorSelector validatorSelector = ValidatorSelector.parse("test-validator");

        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        final Locale locale = Locale.forLanguageTag("en-AU");

        final SpreadsheetCell spreadsheetCell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1+2+3")
        ).setFormatter(
            Optional.of(formatterSelector)
        ).setParser(
            Optional.of(parserSelector)
        ).setValidator(
            Optional.of(validatorSelector)
        ).setLocale(
            Optional.of(locale)
        ).setStyle(style);

        // basic........................................................................................................
        finder.addIfConversionFail(
            Lists.of(
                null,
                spreadsheetCell
            ),
            Object.class,
            SpreadsheetConvertersConverterProvider.BASIC
        );

        // boolean......................................................................................................
        finder.addIfConversionFail(
            Lists.of(
                0,
                1,
                kind.zero(),
                kind.one(),
                SpreadsheetStrings.BOOLEAN_TRUE,
                SpreadsheetStrings.BOOLEAN_FALSE
            ),
            Boolean.class,
            SpreadsheetConvertersConverterProvider.BOOLEAN
        );

        finder.addIfConversionFail(
            Lists.of(
                SpreadsheetStrings.BOOLEAN_TRUE,
                SpreadsheetStrings.BOOLEAN_FALSE
            ),
            String.class,
            SpreadsheetConvertersConverterProvider.BOOLEAN
        );

        // collection-to-list...........................................................................................
        {
            // The List is the value not a List of values.
            finder.addIfConversionFail(
                (Object)
                    Lists.of(
                    true,
                    false
                ),
                BooleanList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST // COLLECTION_TO_LIST
            );

            finder.addIfConversionFail(
                (Object)
                    Lists.of(
                    "Apple 1",
                    "Banana 2"
                    ),
                CsvStringList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST // COLLECTION_TO_LIST
            );

            finder.addIfConversionFail(
                (Object)
                    Lists.of(
                        LocalDate.of(
                            1999,
                            12,
                            31
                        ),
                        LocalDate.of(
                            2000,
                            2,
                            2
                        ),
                        null
                    ),
                LocalDateList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST // COLLECTION_TO_LIST
            );

            finder.addIfConversionFail(
                (Object)
                    Lists.of(
                        LocalDateTime.of(
                            1999,
                            12,
                            31,
                            12,
                            0,
                            0
                        ),
                        LocalDateTime.of(
                            2000,
                            2,
                            2,
                            2,
                            22,
                            22
                        ),
                        null
                    ),
                LocalDateTimeList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST // COLLECTION_TO_LIST
            );

            finder.addIfConversionFail(
                (Object)
                    Lists.of(
                        LocalTime.of(
                            12,
                            58,
                            59
                        ),
                        LocalTime.of(
                            2,
                            22,
                            22
                        ),
                        null
                    ),
                LocalDateTimeList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST // COLLECTION_TO_LIST
            );

            finder.addIfConversionFail(
                (Object)
                    Lists.of(
                        kind.create(1),
                        kind.create(22),
                        kind.create(333.5),
                        null
                    ),
                NumberList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST // COLLECTION_TO_LIST
            );

            finder.addIfConversionFail(
                (Object)
                    Lists.of(
                        "Apple",
                        "Banana",
                        null
                    ),
                StringList.class,
                SpreadsheetConvertersConverterProvider.COLLECTION_TO_LIST // COLLECTION_TO_LIST
            );
        }

        // color........................................................................................................
        {
            // color-to-color...........................................................................................
            if (formatting || scripting) {
                finder.addIfConversionFail(
                    Color.BLACK,
                    Lists.of(
                        HslColor.class,
                        HsvColor.class,
                        RgbColor.class
                    ),
                    SpreadsheetConvertersConverterProvider.COLOR // COLOR_TO_COLOR
                );
            }

            // color-to-number..........................................................................................
            if (formatting || scripting) {
                finder.addIfConversionFail(
                    Color.BLACK,
                    NUMBER_TYPES_WITHOUT_BYTE_SHORT,
                    SpreadsheetConvertersConverterProvider.COLOR // COLOR_TO_NUMBER
                );

                final RgbColor rgb = Color.parseRgb("#12345678");

                finder.addIfConversionFail(
                    Lists.of(
                        rgb.alpha(),
                        rgb.red(),
                        rgb.green(),
                        rgb.blue()
                    ),
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.COLOR // COLOR_TO_NUMBER
                );
            }

            // text-to-color............................................................................................
            if (formula || formatting || scripting) {
                finder.addIfConversionFail(
                    "#123456",
                    Color.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_COLOR
                );
            }

            // text-to-spreadsheet-color-name...........................................................................
            if (formatting || scripting) {
                finder.addIfConversionFail(
                    SpreadsheetColorName.BLACK.value(),
                    SpreadsheetColorName.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_COLOR_NAME
                );
            }
        }

        // date-time....................................................................................................
        {
            // date -> dateTime & number & text & time
            finder.addIfConversionFail(
                DATE, // date
                DATE_TO_TYPES,
                SpreadsheetConvertersConverterProvider.DATE_TIME
            );

            // datetime ->
            finder.addIfConversionFail(
                DATE_TIME, // date
                DATE_TIME_TO_TYPES,
                SpreadsheetConvertersConverterProvider.DATE_TIME
            );

            // time ->
            finder.addIfConversionFail(
                TIME,
                TIME_TO_TYPES,
                SpreadsheetConvertersConverterProvider.DATE_TIME
            );

            // text -> number
            finder.addIfConversionFail(
                "123",
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.DATE_TIME
            );

            // text -> date
            finder.addIfConversionFail(
                "1999/12/31",
                LocalDate.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME
            );

            // text -> dateTime
            finder.addIfConversionFail(
                "1999/12/31 12:58",
                LocalDateTime.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME
            );

            // text -> time
            finder.addIfConversionFail(
                "12:58:59",
                LocalTime.class,
                SpreadsheetConvertersConverterProvider.DATE_TIME
            );
        }

        // environment..................................................................................................
        {
            if (scripting) {
                finder.addIfConversionFail(
                    error,
                    SpreadsheetError.class,
                    SpreadsheetConvertersConverterProvider.ENVIRONMENT // TEXT_TO_ENVIRONMENT_VALUE_NAME
                );
            }
        }

        // error-throwing...............................................................................................
        if (formula) {
            finder.addIfConversionFail(
                error,
                ExpressionNumber.class,
                SpreadsheetConvertersConverterProvider.ERROR_THROWING,
                true
            );
        }

        // expression...................................................................................................
        {
            if (find || formatting || formula || scripting || validation) {
                finder.addIfConversionFail(
                    "1+sum(2)",
                    Expression.class,
                    SpreadsheetConvertersConverterProvider.EXPRESSION
                );
            }
        }

        // json.........................................................................................................
        {
            // to-json..................................................................................................
            if (formula || scripting) {
                finder.addIfConversionFail(
                    "Hello",
                    Lists.of(
                        JsonNode.class,
                        JsonArray.class,
                        JsonBoolean.class,
                        JsonNumber.class,
                        JsonString.class,
                        JsonObject.class
                    ),
                    SpreadsheetConvertersConverterProvider.TO_JSON
                );
            }
        }

        // locale.......................................................................................................
        {
            if (formula || find || formatting || scripting) {
                finder.addIfConversionFail(
                    Lists.of(
                        locale.toLanguageTag(),
                        locale
                    ),
                    DateTimeSymbols.class,
                    SpreadsheetConvertersConverterProvider.LOCALE // DATE_TIME_SYMBOLS
                );
            }

            if (formula || find || formatting || scripting) {
                finder.addIfConversionFail(
                    Lists.of(
                        locale.toLanguageTag(),
                        locale
                    ),
                    DecimalNumberSymbols.class,
                    SpreadsheetConvertersConverterProvider.LOCALE // DECIMAL_NUMBER_SYMBOLS
                );
            }

            // text-to-locale...............................................................................................
            if (formula || find || scripting) {
                finder.addIfConversionFail(
                    locale.toLanguageTag(),
                    Locale.class,
                    SpreadsheetConvertersConverterProvider.LOCALE // TEXT_TO_LOCALE
                );
            }

            if (formula || find || scripting) {
                finder.addIfConversionFail(
                    Lists.of(
                        locale,
                        spreadsheetCell
                    ),
                    Locale.class,
                    SpreadsheetConvertersConverterProvider.LOCALE // TEXT_TO_LOCALE
                );
            }
        }

        // number.......................................................................................................
        {
            // null-to-number...........................................................................................
            if (formatting || scripting) {
                finder.addIfConversionFail(
                    (Object) null, // dont want List overload
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.NUMBER
                );
            }

            // number-to-number.........................................................................................
            finder.addIfConversionFail(
                1,
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.NUMBER
            );
        }

        // plugins......................................................................................................
        {
            if (scripting || validation) {
                finder.addIfConversionFail(
                    formatterSelector.text(),
                    SpreadsheetFormatterSelector.class,
                    SpreadsheetConvertersConverterProvider.PLUGINS // SPREADSHEET_VALUE
                );
            }

            // text-to-validation-selector..............................................................................
            if (scripting || validation) {
                finder.addIfConversionFail(
                    validatorSelector.text(),
                    ValidatorSelector.class,
                    SpreadsheetConvertersConverterProvider.PLUGINS // TEXT_TO_VALIDATOR_SELECTOR
                );
            }
        }

        // spreadsheetMetadata..........................................................................................
        {
            // text-to-spreadsheet-id...................................................................................
            final SpreadsheetId spreadsheetId = SpreadsheetId.with(0x123);

            // will be enabled by terminal
            if (scripting) {
                finder.addIfConversionFail(
                    spreadsheetId.toString(),
                    SpreadsheetId.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA // TEXT_TO_SPREADSHEET_ID
                );
            }

            // text-to-spreadsheet-metadata.................................................................................
            final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                spreadsheetId
            );

            if (scripting) {
                finder.addIfConversionFail(
                    context.marshall(metadata)
                        .toString(),
                    SpreadsheetMetadata.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA // TEXT_TO_SPREADSHEET_METADATA
                );
            }

            // text-to-spreadsheet-property-name........................................................................
            final SpreadsheetMetadataPropertyName<SpreadsheetName> spreadsheetMetadataPropertyName = SpreadsheetMetadataPropertyName.SPREADSHEET_NAME;

            if (scripting) {
                finder.addIfConversionFail(
                    spreadsheetMetadataPropertyName.value(),
                    SpreadsheetMetadataPropertyName.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA // TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME
                );
            }


            // text-to-spreadsheet-name.................................................................................
            final SpreadsheetName spreadsheetName = SpreadsheetName.with("SpreadsheetName123");

            if (scripting) {
                finder.addIfConversionFail(
                    spreadsheetName.text(),
                    SpreadsheetName.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_METADATA // TEXT_TO_SPREADSHEET_NAME
                );
            }
        }

        // spreadsheetValue.............................................................................................
        {
            // error-to-error...........................................................................................
            if (formula) {
                finder.addIfConversionFail(
                    error,
                    SpreadsheetError.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // ERROR_TO_ERROR
                );
            }

            // error-to-number..........................................................................................
            if (formula) {
                finder.addIfConversionFail(
                    error,
                    ExpressionNumber.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // ERROR_TO_NUMBER
                );
            }

            if (formatting || scripting) {
                finder.addIfConversionFail(
                    (Object) null, // dont want List overload
                    NUMBER_TYPES,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // NULL_TO_NUMBER
                );
            }

            // has-spreadsheet-formatter-selector.......................................................................
            if (scripting || validation) {
                finder.addIfConversionFail(
                    spreadsheetCell,
                    SpreadsheetFormatterSelector.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE
                );
            }

            // has-spreadsheet-parser-selector.......................................................................
            if (scripting || validation) {
                finder.addIfConversionFail(
                    spreadsheetCell,
                    SpreadsheetParserSelector.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE
                );
            }

            // has-validation-selector..................................................................................
            if (scripting || validation) {
                finder.addIfConversionFail(
                    spreadsheetCell,
                    ValidatorSelector.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE
                );
            }

            // spreadsheet-selection-to-spreadsheet-selection...............................................................
            if (formula) {
                finder.addIfConversionFail(
                    Lists.of(
                        cell,
                        cellRange
                    ),
                    SpreadsheetCellReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    Lists.of(
                        cell,
                        cellRange,
                        column,
                        columnRange,
                        row,
                        rowRange
                    ),
                    SpreadsheetCellRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    column,
                    SpreadsheetColumnReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    Lists.of(
                        column,
                        columnRange
                    ),
                    SpreadsheetColumnRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    row,
                    SpreadsheetRowReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    Lists.of(
                        row,
                        rowRange
                    ),
                    SpreadsheetRowRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // SPREADSHEET_SELECTION_TO_SPREADSHEET_SELECTION
                );
            }

            // spreadsheet-selection-to-text............................................................................

            if (formula || scripting || validation) {
                finder.addIfConversionFail(
                    Lists.of(
                        cell,
                        cellRange,
                        column,
                        columnRange,
                        row,
                        rowRange,
                        label
                    ),
                    String.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE
                );
            }

            // text-to-error............................................................................................
            if (validation) {
                finder.addIfConversionFail(
                    error.text(),
                    SpreadsheetError.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_ERROR
                );
            }

            // text-to-spreadsheet-selection............................................................................
            if (formula || scripting || validation) {
                for (final SpreadsheetSelection selection : Lists.of(
                    cell,
                    cellRange,
                    column,
                    columnRange,
                    label
                )) {
                    finder.addIfConversionFail(
                        selection.toString(),
                        selection.getClass(),
                        SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                    );
                }

                finder.addIfConversionFail(
                    cell.toString(),
                    Lists.of(
                        SpreadsheetCellReference.class,
                        SpreadsheetCellRangeReference.class
                    ),
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    cellRange.toString(),
                    SpreadsheetCellRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    label.toString(),
                    SpreadsheetLabelName.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    column.toString(),
                    Lists.of(
                        SpreadsheetColumnReference.class,
                        SpreadsheetColumnRangeReference.class
                    ),
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    column.toString(),
                    SpreadsheetColumnReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    columnRange.toString(),
                    SpreadsheetColumnRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    row.toString(),
                    Lists.of(
                        SpreadsheetRowReference.class,
                        SpreadsheetRowRangeReference.class
                    ),
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    row.toString(),
                    SpreadsheetRowReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );

                finder.addIfConversionFail(
                    rowRange.toString(),
                    SpreadsheetRowRangeReference.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_SPREADSHEET_SELECTION
                );
            }

            // text-to-value-type.......................................................................................
            if (validation) {
                finder.addIfConversionFail(
                    SpreadsheetValueType.TEXT.value(),
                    ValidationValueTypeName.class,
                    SpreadsheetConvertersConverterProvider.SPREADSHEET_VALUE // TEXT_TO_VALUE_TYPE
                );
            }
        }

        // style-.......................................................................................................
        {
            // has-style-...............................................................................................
            if (formatting || scripting) {
                finder.addIfConversionFail(
                    Lists.of(
                        style,
                        SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                            .setStyle(style)
                    ),
                    TextStyle.class,
                    SpreadsheetConvertersConverterProvider.STYLE // HAS_STYLE
                );
            }

            // text-to-spreadsheet-text.................................................................................
            final SpreadsheetText spreadsheetText = SpreadsheetText.with("Text123");

            if (formatting || scripting) {
                finder.addIfConversionFail(
                    spreadsheetText.text(),
                    SpreadsheetText.class,
                    SpreadsheetConvertersConverterProvider.STYLE // TEXT_TO_SPREADSHEET_TEXT
                );

                // text-to-textStyle....................................................................................
                finder.addIfConversionFail(
                    style.text(),
                    TextStyle.class,
                    SpreadsheetConvertersConverterProvider.STYLE // TEXT_TO_TEXT_STYLE
                );

                // text-to-text-style-property-name.....................................................................
                finder.addIfConversionFail(
                    TextStylePropertyName.BACKGROUND_COLOR.text(),
                    TextStylePropertyName.class,
                    SpreadsheetConvertersConverterProvider.STYLE // TEXT_TO_TEXT_STYLE_PROPERTY_NAME
                );
            }
        }

        // template.....................................................................................................
        {
            // text-to-template-value-name..............................................................................
            if (formatting) {
                finder.addIfConversionFail(
                    TemplateValueName.with("TemplateValue123"),
                    TemplateValueName.class,
                    SpreadsheetConvertersConverterProvider.TEMPLATE // TEXT_TO_TEMPLATE_VALUE_NAME
                );
            }
        }

        // text.........................................................................................................
        finder.addIfConversionFail(
            Lists.of(
                'A',
                "Text",
                Url.parseAbsolute("https://example.com/123"),
                spreadsheetCell.formula()
            ),
            String.class,
            SpreadsheetConvertersConverterProvider.TEXT
        );

        // text-node....................................................................................................
        {
            if (formatting || scripting) {
                // has-text-node........................................................................................
                finder.addIfConversionFail(
                    Lists.of(
                        TextNode.text("Text123").setTextStyle(
                            TextStyle.EMPTY.set(
                                TextStylePropertyName.COLOR,
                                Color.BLACK
                            )
                        ),
                        TextStyle.EMPTY.set(
                            TextStylePropertyName.TEXT_ALIGN,
                            TextAlign.CENTER
                        )
                    ),
                    Styleable.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE // TO_STYLEABLE
                );

                finder.addIfConversionFail(
                    Lists.of(
                        'A',
                        "Text123",
                        TextNode.text("Text123")
                    ),
                    TextNode.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE // HAS_TEXT_NODE
                );

                // text-to-textNode.....................................................................................
                finder.addIfConversionFail(
                    Lists.of(
                        'A',
                        "Text"
                    ),
                    TextNode.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE // TEXT_TO_TEXT_NODE
                );
            }

            // text-to-XXX-list.........................................................................................
            {
                // text-to-boolean-list.................................................................................
                finder.addIfConversionFail(
                    "TRUE, FALSE, true",
                    BooleanList.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_BOOLEAN_LIST
                );

                // text-to-csv-string-list..............................................................................
                finder.addIfConversionFail(
                    "apple, banana, \"333 444\"",
                    CsvStringList.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_CSV_STRING_LIST
                );

                // text-to-date-list....................................................................................
                finder.addIfConversionFail(
                    "1999/12/31",
                    LocalDateList.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_DATE_LIST
                );

                // text-to-date-time-list...............................................................................
                finder.addIfConversionFail(
                    "1999/12/31 12:58",
                    LocalDateTimeList.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_DATE_TIME_LIST
                );

                // text-to-number-list..................................................................................
                finder.addIfConversionFail(
                    "1, 22, 333.5",
                    NumberList.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_NUMBER_LIST
                );

                // text-to-string-list..................................................................................
                finder.addIfConversionFail(
                    "apple, banana, 333",
                    StringList.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_STRING_LIST
                );

                // text-to-time-list....................................................................................
                finder.addIfConversionFail(
                    "12:58:59",
                    LocalTimeList.class,
                    SpreadsheetConvertersConverterProvider.TEXT_TO_TIME_LIST
                );
            }

            // text-to-url..............................................................................................
            if (formatting || scripting) {
                final Url url = Url.parse("https://example.com/123");

                if (formatting) {
                    finder.addIfConversionFail(
                        url.text(),
                        String.class,
                        SpreadsheetConvertersConverterProvider.TEXT_NODE // TEXT_TO_URL
                    );
                }

                // url-to-hyperlink.....................................................................................
                finder.addIfConversionFail(
                    url.text(),
                    Hyperlink.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE // URL_TO_HYPERLINK
                );

                // url-to-image.........................................................................................
                finder.addIfConversionFail(
                    url.text(),
                    Image.class,
                    SpreadsheetConvertersConverterProvider.TEXT_NODE // URL_TO_IMAGE
                );
            }

            // url......................................................................................................
            if (formatting || scripting) {
                final Url url = Url.parse("https://example.com/123");

                if (formatting) {
                    finder.addIfConversionFail(
                        url.text(),
                        String.class,
                        SpreadsheetConvertersConverterProvider.URL // TEXT_TO_URL
                    );
                }

                // url-to-hyperlink.....................................................................................
                finder.addIfConversionFail(
                    url.text(),
                    Hyperlink.class,
                    SpreadsheetConvertersConverterProvider.URL // URL_TO_HYPERLINK
                );

                // url-to-image.........................................................................................
                finder.addIfConversionFail(
                    url.text(),
                    Image.class,
                    SpreadsheetConvertersConverterProvider.URL // URL_TO_IMAGE
                );
            }

            // validation...............................................................................................
            {
                if (scripting || validation) {
                    if (validation) {
                        finder.addIfConversionFail(
                            "Form123",
                            FormName.class,
                            SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION // TEXT_TO_FORM_NAME
                        );

                        // text-to-validation-error.....................................................................
                        finder.addIfConversionFail(
                            ValidationError.with(
                                cell,
                                "Error message 123"
                            ).text(),
                            ValidationError.class,
                            SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION // TEXT_TO_VALIDATION_ERROR
                        );
                    }
                }

                if (validation) {
                    // to-validation-error-list.........................................................................
                    finder.addIfConversionFail(
                        Lists.of(
                            "Validation error message 1",
                            SpreadsheetForms.error(
                                SpreadsheetSelection.A1,
                                "Validation error message2"
                            )
                        ),
                        ValidationErrorList.class,
                        SpreadsheetConvertersConverterProvider.FORM_AND_VALIDATION // TO_VALIDATION_ERROR_LIST
                    );
                }
            }
        }

        return MissingConverterSet.with(
            new TreeSet<>(
                finder.missing.values()
            )
        );
    }

    private MissingConverterVerifier(final Converter<SpreadsheetConverterContext> converter,
                                     final SpreadsheetConverterContext context) {
        this.converter = converter;
        this.context = context;

        this.missing = Maps.sorted();
    }

    private void addIfConversionFail(final List<Object> values,
                                     final List<Class<?>> types,
                                     final ConverterName name) {
        for (final Object value : values) {
            for (final Class<?> type : types) {
                this.addIfConversionFail(
                    value,
                    type,
                    name
                );
            }
        }
    }

    private void addIfConversionFail(final List<Object> values,
                                     final Class<?> type,
                                     final ConverterName name) {
        for (final Object value : values) {
            this.addIfConversionFail(
                value,
                type,
                name
            );
        }
    }

    private void addIfConversionFail(final Object value,
                                     final List<Class<?>> types,
                                     final ConverterName name) {
        for (final Class<?> type : types) {
            this.addIfConversionFail(
                value,
                type,
                name
            );
        }
    }

    private void addIfConversionFail(final Object value,
                                     final Class<?> type,
                                     final ConverterName name) {
        this.addIfConversionFail(
            value,
            type,
            name,
            false // expectedSpreadsheetErrorException
        );
    }

    private void addIfConversionFail(final Object value,
                                     final Class<?> type,
                                     final ConverterName name,
                                     final boolean expectedSpreadsheetErrorException) {
        boolean failed = false;
        try {
            failed = this.converter.convert(
                value,
                type,
                this.context
            ).isRight();
        } catch (final UnsupportedOperationException rethrow) {
            throw rethrow;
        } catch (final SpreadsheetErrorException ignore) {
            failed = !expectedSpreadsheetErrorException;
        } catch (final Exception cause) {
            cause.printStackTrace(); // KEEP!
            failed = true;
        }

        if (failed) {
            this.add(
                value,
                type,
                name
            );
        }
    }

    private void add(final Object value,
                     final Class<?> type,
                     final ConverterName name) {
        final MissingConverterValue missingConverterValue = MissingConverterValue.with(
            value,
            type.getName()
        );

        final MissingConverter missingConverter = this.missing.get(name);

        this.missing.put(
            name,
            null == missingConverter ?
                MissingConverter.with(
                    name,
                    Sets.of(missingConverterValue)
                ) :
                missingConverter.add(missingConverterValue)
        );
    }

    private final Converter<SpreadsheetConverterContext> converter;

    private final SpreadsheetConverterContext context;

    private final Map<ConverterName, MissingConverter> missing;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.missing.toString();
    }
}
