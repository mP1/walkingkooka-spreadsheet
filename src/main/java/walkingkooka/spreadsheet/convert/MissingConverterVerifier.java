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

import walkingkooka.collect.list.Lists;
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
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorException;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
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
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
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
        final boolean sort = SpreadsheetMetadataPropertyName.SORT_CONVERTER.equals(propertyName);
        final boolean validation = SpreadsheetMetadataPropertyName.VALIDATION_CONVERTER.equals(propertyName);
        final boolean terminal = false; // TODO later

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

        // color-to-color...............................................................................................
        if (formatting) {
            finder.addIfConversionFail(
                Color.BLACK,
                HslColor.class,
                SpreadsheetConvertersConverterProvider.COLOR_TO_COLOR
            );

            finder.addIfConversionFail(
                Color.BLACK,
                HsvColor.class,
                SpreadsheetConvertersConverterProvider.COLOR_TO_COLOR
            );

            finder.addIfConversionFail(
                Color.BLACK,
                RgbColor.class,
                SpreadsheetConvertersConverterProvider.COLOR_TO_COLOR
            );
        }

        // error-to-number..............................................................................................
        if (formula) {
            finder.addIfConversionFail(
                error,
                ExpressionNumber.class,
                SpreadsheetConvertersConverterProvider.ERROR_TO_NUMBER
            );
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

        // TODO format-pattern-to-string

        // general......................................................................................................
        {
            finder.addIfConversionFail(
                Lists.of(
                    "true",
                    "false"
                ),
                ExpressionNumber.class,
                SpreadsheetConvertersConverterProvider.GENERAL
            );
            finder.addIfConversionFail(
                Lists.of(
                    "true",
                    "false"
                ),
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.GENERAL
            );

            finder.addIfConversionFail(
                Lists.of(
                    "true",
                    "false"
                ),
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.GENERAL
            );

            // general numbers
            finder.addIfConversionFail(
                kind.one(),
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.GENERAL
            );

            // SpreadsheetSelection
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
                SpreadsheetConvertersConverterProvider.GENERAL
            );
        }

        // has-style-style..............................................................................................
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        if (formatting) {
            finder.addIfConversionFail(
                Lists.of(
                    style,
                    SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                        .setStyle(style)
                ),
                TextStyle.class,
                SpreadsheetConvertersConverterProvider.HAS_TEXT_STYLE_TO_STYLE
            );
        }

        // null-to-number...............................................................................................
        if (formatting) {
            finder.addIfConversionFail(
                (Object) null, // dont want List overload
                NUMBER_TYPES,
                SpreadsheetConvertersConverterProvider.NULL_TO_NUMBER
            );
        }

        // number-to-number.............................................................................................
        finder.addIfConversionFail(
            1,
            NUMBER_TYPES,
            SpreadsheetConvertersConverterProvider.NULL_TO_NUMBER
        );

        // selection-to-selection.......................................................................................
        if (formula) {
            finder.addIfConversionFail(
                Lists.of(
                    cell,
                    cellRange,
                    column,
                    columnRange,
                    row,
                    rowRange
                ),
                SpreadsheetCellReference.class,
                SpreadsheetConvertersConverterProvider.SELECTION_TO_SELECTION
            );

            finder.addIfConversionFail(
                column,
                SpreadsheetColumnReference.class,
                SpreadsheetConvertersConverterProvider.SELECTION_TO_SELECTION
            );

            finder.addIfConversionFail(
                row,
                SpreadsheetRowReference.class,
                SpreadsheetConvertersConverterProvider.SELECTION_TO_SELECTION
            );
        }

        // simple.......................................................................................................
        finder.addIfConversionFail(
            "Hello",
            String.class,
            SpreadsheetConvertersConverterProvider.SIMPLE
        );

        // spreadsheet-cell-to..........................................................................................
        if (formatting) {
            final Locale locale = context.locale();

            final SpreadsheetCell spreadsheetCell = SpreadsheetSelection.A1.setFormula(
                    SpreadsheetFormula.EMPTY.setText("=1+2")
                ).setDateTimeSymbols(
                    Optional.of(
                        DateTimeSymbols.fromDateFormatSymbols(
                            new DateFormatSymbols(locale)
                        )
                    )
                ).setDecimalNumberSymbols(
                    Optional.of(
                        DecimalNumberSymbols.fromDecimalFormatSymbols(
                            '+',
                            new DecimalFormatSymbols(locale)
                        )
                    )
                ).setLocale(
                    Optional.of(locale)
                ).setFormatter(
                    Optional.of(
                        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT
                    )
                ).setParser(
                    Optional.of(
                        SpreadsheetParserSelector.parse("test-parser")
                    )
                ).setStyle(style)
                .setValidator(
                    Optional.of(
                        ValidatorSelector.parse("test-validator")
                    )
                );

            finder.addIfConversionFail(
                spreadsheetCell,
                Lists.of(
                    String.class,
                    DateTimeSymbols.class,
                    DecimalNumberSymbols.class,
                    Locale.class,
                    SpreadsheetFormatterSelector.class,
                    SpreadsheetParserSelector.class,
                    TextStyle.class,
                    ValidatorSelector.class
                ),
                SpreadsheetConvertersConverterProvider.SPREADSHEET_CELL_TO
            );
        }

        // text-to-color................................................................................................
        if (formatting) {
            finder.addIfConversionFail(
                "#123456",
                Color.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_COLOR
            );
        }

        // text-to-error................................................................................................
        finder.addIfConversionFail(
            error,
            SpreadsheetError.class,
            SpreadsheetConvertersConverterProvider.TEXT_TO_ERROR
        );

        // text-to-expression...........................................................................................
        finder.addIfConversionFail(
            "1+sum(2)",
            Expression.class,
            SpreadsheetConvertersConverterProvider.TEXT_TO_EXPRESSION
        );

        // text-to-form-name............................................................................................
        if (validation) {
            finder.addIfConversionFail(
                "Form123",
                FormName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_FORM_NAME
            );
        }

        // text-to-locale...............................................................................................
        if (formula || find) {
            finder.addIfConversionFail(
                "en-AU",
                Locale.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_LOCALE
            );
        }

        // text-to-selection...........................................................................................
        if (formula || validation) {
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
                    SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
                );
            }

            finder.addIfConversionFail(
                cell.toString(),
                Lists.of(
                    SpreadsheetCellReference.class,
                    SpreadsheetCellRangeReference.class
                ),
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );

            finder.addIfConversionFail(
                cellRange.toString(),
                SpreadsheetCellRangeReference.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );

            finder.addIfConversionFail(
                label.toString(),
                SpreadsheetLabelName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );

            finder.addIfConversionFail(
                column.toString(),
                Lists.of(
                    SpreadsheetColumnReference.class,
                    SpreadsheetColumnRangeReference.class
                ),
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );

            finder.addIfConversionFail(
                column.toString(),
                SpreadsheetColumnReference.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );

            finder.addIfConversionFail(
                columnRange.toString(),
                SpreadsheetColumnRangeReference.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );

            finder.addIfConversionFail(
                row.toString(),
                Lists.of(
                    SpreadsheetRowReference.class,
                    SpreadsheetRowRangeReference.class
                ),
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );

            finder.addIfConversionFail(
                row.toString(),
                SpreadsheetRowReference.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );

            finder.addIfConversionFail(
                rowRange.toString(),
                SpreadsheetRowRangeReference.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SELECTION
            );
        }

        // text-to-spreadsheet-color-name...............................................................................
        if (formatting) {
            finder.addIfConversionFail(
                SpreadsheetColorName.BLACK.value(),
                SpreadsheetColorName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_COLOR_NAME
            );
        }

        // text-to-spreadsheet-formatter-selection......................................................................
        if (formatting) {
            finder.addIfConversionFail(
                SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT,
                SpreadsheetFormatterSelector.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_FORMATTER_SELECTOR
            );
        }

        // text-to-spreadsheet-id.......................................................................................
        final SpreadsheetId spreadsheetId = SpreadsheetId.with(0x123);

        // will be enabled by terminal
        if (terminal) {
            finder.addIfConversionFail(
                spreadsheetId.toString(),
                SpreadsheetId.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_ID
            );
        }

        // text-to-spreadsheet-metadata.................................................................................
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            spreadsheetId
        );

        if (terminal) {
            finder.addIfConversionFail(
                context.marshall(metadata)
                    .toString(),
                SpreadsheetMetadata.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_METADATA
            );
        }

        // text-to-spreadsheet-color....................................................................................
        if (formatting) {
            final SpreadsheetColorName spreadsheetColorName = SpreadsheetColorName.BLACK;

            finder.addIfConversionFail(
                spreadsheetColorName.toString(),
                SpreadsheetColorName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_COLOR_NAME
            );
        }

        // text-to-spreadsheet-property-name............................................................................
        final SpreadsheetMetadataPropertyName<SpreadsheetName> spreadsheetMetadataPropertyName = SpreadsheetMetadataPropertyName.SPREADSHEET_NAME;

        if (terminal) {
            finder.addIfConversionFail(
                spreadsheetMetadataPropertyName.value(),
                SpreadsheetMetadataPropertyName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_METADATA_PROPERTY_NAME
            );
        }

        // text-to-error................................................................................................
        if (validation) {
            finder.addIfConversionFail(
                error.text(),
                SpreadsheetError.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_ERROR
            );
        }

        // text-to-spreadsheet-name.....................................................................................
        final SpreadsheetName spreadsheetName = SpreadsheetName.with("SpreadsheetName123");

        if (terminal) {
            finder.addIfConversionFail(
                spreadsheetName.text(),
                SpreadsheetName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_NAME
            );
        }

        // text-to-spreadsheet-text.....................................................................................
        final SpreadsheetText spreadsheetText = SpreadsheetText.with("Text123");

        if (formatting) {
            finder.addIfConversionFail(
                spreadsheetText.text(),
                SpreadsheetText.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_SPREADSHEET_TEXT
            );
        }

        // text-to-template-value-name..................................................................................
        if (formatting) {
            finder.addIfConversionFail(
                TemplateValueName.with("TemplateValue123"),
                TemplateValueName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_TEMPLATE_VALUE_NAME
            );
        }

        // text-to-text.................................................................................................
        finder.addIfConversionFail(
            Lists.of(
                'A',
                "Text"
            ),
            String.class,
            SpreadsheetConvertersConverterProvider.TEXT_TO_TEXT
        );

        // text-to-textNode.............................................................................................
        if (formatting) {
            finder.addIfConversionFail(
                Lists.of(
                    'A',
                    "Text"
                ),
                TextNode.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_TEXT_NODE
            );

            // text-to-textStyle............................................................................................
            finder.addIfConversionFail(
                style.text(),
                TextStyle.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_TEXT_STYLE
            );

            // text-to-text-style-property-name.............................................................................
            finder.addIfConversionFail(
                TextStylePropertyName.BACKGROUND_COLOR.text(),
                TextStylePropertyName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_TEXT_STYLE_PROPERTY_NAME
            );
        }

        // text-to-url..................................................................................................
        Url url = Url.parse("https://example.com/123");

        if (formatting) {
            finder.addIfConversionFail(
                url.text(),
                String.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_URL
            );
        }

        // text-to-validation-error.....................................................................................
        if (validation) {
            finder.addIfConversionFail(
                ValidationError.with(
                    cell,
                    "Error message 123"
                ).text(),
                ValidationError.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_VALIDATION_ERROR
            );

            // text-to-validation-selector..............................................................................
            finder.addIfConversionFail(
                ValidatorSelector.parse("test-validator").text(),
                ValidatorSelector.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_VALIDATOR_SELECTOR
            );
        }

        // text-to-value-type...........................................................................................
        if (validation) {
            finder.addIfConversionFail(
                SpreadsheetValueType.TEXT.value(),
                ValidationValueTypeName.class,
                SpreadsheetConvertersConverterProvider.TEXT_TO_VALUE_TYPE
            );
        }

        // to-json......................................................................................................
        // TODO add to-json converter later
        if (false) {
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

        // to-text-node.................................................................................................
        if (formatting) {
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
                SpreadsheetConvertersConverterProvider.TO_STYLEABLE
            );

            finder.addIfConversionFail(
                Lists.of(
                    'A',
                    "Text123",
                    TextNode.text("Text123")
                ),
                TextNode.class,
                SpreadsheetConvertersConverterProvider.TO_TEXT_NODE
            );

            // url-to-hyperlink.........................................................................................
            finder.addIfConversionFail(
                url.text(),
                Hyperlink.class,
                SpreadsheetConvertersConverterProvider.URL_TO_HYPERLINK
            );

            // url-to-image.............................................................................................
            finder.addIfConversionFail(
                url.text(),
                Image.class,
                SpreadsheetConvertersConverterProvider.URL_TO_IMAGE
            );
        }

        if (validation) {
            // to-validation-error-list.................................................................................
            finder.addIfConversionFail(
                Lists.of(
                    "Validation error message 1",
                    SpreadsheetForms.error(
                        SpreadsheetSelection.A1,
                        "Validation error message2"
                    )
                ),
                ValidationErrorList.class,
                SpreadsheetConvertersConverterProvider.TO_VALIDATION_ERROR_LIST
            );
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
        ;
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
