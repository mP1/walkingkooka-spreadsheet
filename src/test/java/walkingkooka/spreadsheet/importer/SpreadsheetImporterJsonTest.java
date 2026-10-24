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
import walkingkooka.currency.OptionalCurrency;
import walkingkooka.datetime.OptionalDateTimeSymbols;
import walkingkooka.math.OptionalDecimalNumberSymbols;
import walkingkooka.reflect.PackagePrivateClassTesting;
import walkingkooka.spreadsheet.export.SpreadsheetExporterContexts;
import walkingkooka.spreadsheet.export.SpreadsheetExporters;
import walkingkooka.spreadsheet.format.provider.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.provider.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.OptionalSpreadsheetValue;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetCellRange;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.util.OptionalLocale;
import walkingkooka.validation.OptionalValueType;
import walkingkooka.validation.provider.OptionalValidatorSelector;

import java.util.List;
import java.util.Optional;

public final class SpreadsheetImporterJsonTest implements SpreadsheetImporterTesting<SpreadsheetImporterJson>,
    HasSpreadsheetMetadataTesting,
    ToStringTesting<SpreadsheetImporterJson>,
    PackagePrivateClassTesting<SpreadsheetImporterJson> {

    private final static Optional<Object> VALUE = Optional.of(
        EXPRESSION_NUMBER_KIND.create(123)
    );

    private final static Optional<TextNode> FORMATTED_VALUE = Optional.of(
        TextNode.text("Formatted text 123")
    );

    private final static SpreadsheetCellReference A1 = SpreadsheetSelection.A1;

    private final static SpreadsheetFormula FORMULA_A1 = SpreadsheetFormula.EMPTY.setText("=1")
        .setValueType(OPTIONAL_VALUE_TYPE)
        .setValue(VALUE); // setValue AFTER setValueType otherwise value gets cleared

    private final static SpreadsheetCell SPREADSHEET_CELL = A1.setFormula(FORMULA_A1)
        .setCurrency(OPTIONAL_CURRENCY)
        .setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
        .setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS)
        .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
        .setLocale(OPTIONAL_LOCALE)
        .setParser(OPTIONAL_PARSER_SELECTOR)
        .setStyle(TEXT_STYLE)
        .setValidator(OPTIONAL_VALIDATOR_SELECTOR)
        .setFormattedValue(FORMATTED_VALUE);

    private final static SpreadsheetCellReference A2 = SpreadsheetSelection.parseCell("A2");

    private final static SpreadsheetFormula FORMULA_A2 = SpreadsheetFormula.EMPTY.setText("=2");

    private final static SpreadsheetCell CELL_A2 = A2.setFormula(FORMULA_A2);

    @Test
    public void testDoImportWithCells() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.CELL,
            SpreadsheetImporterCellValue.cell(SPREADSHEET_CELL),
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
                OptionalCurrency.with(OPTIONAL_CURRENCY)
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
                OptionalDateTimeSymbols.with(OPTIONAL_DATE_TIME_SYMBOLS)
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
                OptionalDecimalNumberSymbols.with(OPTIONAL_DECIMAL_NUMBER_SYMBOLS)
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
                OptionalSpreadsheetFormatterSelector.with(OPTIONAL_FORMATTER_SELECTOR)
            ),
            SpreadsheetImporterCellValue.formatter(
                A2,
                OptionalSpreadsheetFormatterSelector.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithLocale() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.LOCALE,
            SpreadsheetImporterCellValue.locale(
                A1,
                OptionalLocale.with(
                    Optional.of(LOCALE)
                )
            ),
            SpreadsheetImporterCellValue.locale(
                A2,
                OptionalLocale.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithParser() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.PARSER,
            SpreadsheetImporterCellValue.parser(
                A1,
                OptionalSpreadsheetParserSelector.with(OPTIONAL_PARSER_SELECTOR)
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
                TEXT_STYLE
            ),
            SpreadsheetImporterCellValue.style(
                A2,
                TextStyle.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithValidator() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.VALIDATOR,
            SpreadsheetImporterCellValue.validator(
                A1,
                OptionalValidatorSelector.with(OPTIONAL_VALIDATOR_SELECTOR)
            ),
            SpreadsheetImporterCellValue.validator(
                A2,
                OptionalValidatorSelector.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithValue() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.VALUE,
            SpreadsheetImporterCellValue.value(
                A1,
                OptionalSpreadsheetValue.with(VALUE)
            ),
            SpreadsheetImporterCellValue.value(
                A2,
                OptionalSpreadsheetValue.EMPTY
            )
        );
    }

    @Test
    public void testDoImportWithValueType() {
        this.doImportAndCheck(
            SpreadsheetCellValueKind.VALUE_TYPE,
            SpreadsheetImporterCellValue.valueType(
                A1,
                OptionalValueType.with(OPTIONAL_VALUE_TYPE)
            ),
            SpreadsheetImporterCellValue.valueType(
                A2,
                OptionalValueType.EMPTY
            )
        );
    }

    private void doImportAndCheck(final SpreadsheetCellValueKind valueKind,
                                  final SpreadsheetImporterCellValue... values) {
        this.doImportAndCheck(
            SpreadsheetCellRange.with(SpreadsheetSelection.ALL_CELLS)
                .setValue(
                    Sets.of(
                        SPREADSHEET_CELL,
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
    public SpreadsheetImporterJson createSpreadsheetImporter() {
        return SpreadsheetImporterJson.INSTANCE;
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
            SpreadsheetImporterJson.INSTANCE,
            SpreadsheetImporterJson.class.getSimpleName()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetImporterJson> type() {
        return SpreadsheetImporterJson.class;
    }
}
