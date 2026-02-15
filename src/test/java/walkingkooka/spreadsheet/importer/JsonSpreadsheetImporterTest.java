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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.OptionalDateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.OptionalDecimalNumberSymbols;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.export.SpreadsheetExporterContexts;
import walkingkooka.spreadsheet.export.SpreadsheetExporters;
import walkingkooka.spreadsheet.format.provider.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.provider.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetCellRange;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;
import walkingkooka.tree.text.OptionalTextNode;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.util.OptionalCurrency;
import walkingkooka.validation.OptionalValueType;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.provider.ValidatorSelector;

import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class JsonSpreadsheetImporterTest implements SpreadsheetImporterTesting<JsonSpreadsheetImporter>,
    SpreadsheetMetadataTesting,
    ToStringTesting<JsonSpreadsheetImporter>,
    ClassTesting2<JsonSpreadsheetImporter> {

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    private final static Optional<Currency> CURRENCY = java.util.Optional.of(
        Currency.getInstance("AUD")
    );

    private final static Optional<DateTimeSymbols> DATE_TIME_SYMBOLS = Optional.of(
        DateTimeSymbols.fromDateFormatSymbols(
            new DateFormatSymbols(LOCALE)
        )
    );

    private final static Optional<DecimalNumberSymbols> DECIMAL_NUMBER_SYMBOLS = Optional.of(
        DecimalNumberSymbols.fromDecimalFormatSymbols(
            '+',
            new DecimalFormatSymbols(LOCALE)
        )
    );

    private final static Optional<SpreadsheetFormatterSelector> FORMATTER = Optional.of(
        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT
    );

    private final static Optional<Locale> OPTIONAL_LOCALE = Optional.of(
        Locale.forLanguageTag("en-AU")
    );

    private final static Optional<SpreadsheetParserSelector> PARSER = Optional.of(
        SpreadsheetParserSelector.parse("test-parser-123")
    );

    private final static TextStyle STYLE = TextStyle.EMPTY.set(
        TextStylePropertyName.COLOR,
        Color.BLACK
    );

    private final static Optional<ValidatorSelector> VALIDATOR = Optional.of(
        ValidatorSelector.parse("test-validator-123")
    );

    private final static Optional<ValueType> VALUE_TYPE = Optional.of(
        ValueType.with("hello")
    );

    private final static Optional<TextNode> FORMATTED_VALUE = Optional.of(
        TextNode.text("Formatted text 123")
    );

    private final static SpreadsheetCellReference A1 = SpreadsheetSelection.A1;

    private final static SpreadsheetFormula FORMULA_A1 = SpreadsheetFormula.EMPTY.setText("=1")
        .setValueType(VALUE_TYPE);

    private final static SpreadsheetCell CELL_A1 = A1.setFormula(FORMULA_A1)
        .setCurrency(CURRENCY)
        .setDateTimeSymbols(DATE_TIME_SYMBOLS)
        .setDecimalNumberSymbols(DECIMAL_NUMBER_SYMBOLS)
        .setFormatter(FORMATTER)
        .setLocale(OPTIONAL_LOCALE)
        .setParser(PARSER)
        .setStyle(STYLE)
        .setValidator(VALIDATOR)
        .setFormattedValue(FORMATTED_VALUE);

    private final static SpreadsheetCellReference A2 = SpreadsheetSelection.parseCell("A2");

    private final static SpreadsheetFormula FORMULA_A2 = SpreadsheetFormula.EMPTY.setText("=2");

    private final static SpreadsheetCell CELL_A2 = A2.setFormula(FORMULA_A2);

    @Test
    public void testDoImportWithCells() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.CELL,
            SpreadsheetImporterCellValue.cell(CELL_A1),
            SpreadsheetImporterCellValue.cell(CELL_A2)
        );
    }

    @Test
    public void testDoImportWithFormula() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.FORMULA,
            SpreadsheetImporterCellValue.formula(
                A1,
                FORMULA_A1.setValueType(SpreadsheetFormula.NO_VALUE_TYPE)
            ),
            SpreadsheetImporterCellValue.formula(
                A2,
                FORMULA_A2
            )
        );
    }

    @Test
    public void testDoImportWithCurrency() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.CURRENCY,
            SpreadsheetImporterCellValue.currency(
                A1,
                OptionalCurrency.with(CURRENCY)
            ),
            SpreadsheetImporterCellValue.currency(
                A2,
                OptionalCurrency.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithDateTimeSymbols() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.DATE_TIME_SYMBOLS,
            SpreadsheetImporterCellValue.dateTimeSymbols(
                A1,
                OptionalDateTimeSymbols.with(DATE_TIME_SYMBOLS)
            ),
            SpreadsheetImporterCellValue.dateTimeSymbols(
                A2,
                OptionalDateTimeSymbols.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithDecimalNumberSymbols() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.DECIMAL_NUMBER_SYMBOLS,
            SpreadsheetImporterCellValue.decimalNumberSymbols(
                A1,
                OptionalDecimalNumberSymbols.with(DECIMAL_NUMBER_SYMBOLS)
            ),
            SpreadsheetImporterCellValue.decimalNumberSymbols(
                A2,
                OptionalDecimalNumberSymbols.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithFormatter() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.FORMATTER,
            SpreadsheetImporterCellValue.formatter(
                A1,
                OptionalSpreadsheetFormatterSelector.with(FORMATTER)
            ),
            SpreadsheetImporterCellValue.formatter(
                A2,
                OptionalSpreadsheetFormatterSelector.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithParser() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.PARSER,
            SpreadsheetImporterCellValue.parser(
                A1,
                OptionalSpreadsheetParserSelector.with(PARSER)
            ),
            SpreadsheetImporterCellValue.parser(
                A2,
                OptionalSpreadsheetParserSelector.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithStyle() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.STYLE,
            SpreadsheetImporterCellValue.style(
                A1,
                STYLE
            ),
            SpreadsheetImporterCellValue.style(
                A2,
                TextStyle.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithValueType() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.VALUE_TYPE,
            SpreadsheetImporterCellValue.valueType(
                A1,
                OptionalValueType.with(VALUE_TYPE)
            ),
            SpreadsheetImporterCellValue.valueType(
                A2,
                OptionalValueType.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithFormattedValue() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.VALUE,
            SpreadsheetImporterCellValue.formattedValue(
                A1,
                OptionalTextNode.with(FORMATTED_VALUE)
            ),
            SpreadsheetImporterCellValue.formattedValue(
                A2,
                OptionalTextNode.EMPTY
            )
        );
    }

    private void doImportAndCheck(final SpreadsheetCellValueKind valueKind,
                                  final SpreadsheetImporterCellValue... values) {
        this.doImportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.of(
                    CELL_A1,
                    CELL_A2
                )
            ),
            valueKind,
            values
        );
    }

    private void doImportAndCheck(final SpreadsheetCellRange cells,
                                  final SpreadsheetCellValueKind valueKind,
                                  final SpreadsheetImporterCellValue... values) {
        this.doImportAndCheck(
            cells,
            valueKind,
            Lists.of(values)
        );
    }

    private void doImportAndCheck(final SpreadsheetCellRange cells,
                                  final SpreadsheetCellValueKind valueKind,
                                  final List<SpreadsheetImporterCellValue> values) {
        this.doImportAndCheck(
            SpreadsheetExporters.json()
                .export(
                    cells,
                    valueKind,
                    SpreadsheetExporterContexts.basic(
                        SpreadsheetMetadata.EMPTY,
                        JSON_NODE_MARSHALL_CONTEXT
                    )
                ),
            values
        );
    }

    @Override
    public JsonSpreadsheetImporter createSpreadsheetImporter() {
        return JsonSpreadsheetImporter.INSTANCE;
    }

    @Override
    public SpreadsheetImporterContext createContext() {
        return SpreadsheetImporterContexts.basic(
            JSON_NODE_UNMARSHALL_CONTEXT
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            JsonSpreadsheetImporter.INSTANCE,
            JsonSpreadsheetImporter.class.getSimpleName()
        );
    }

    // class............................................................................................................

    @Override
    public Class<JsonSpreadsheetImporter> type() {
        return JsonSpreadsheetImporter.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
