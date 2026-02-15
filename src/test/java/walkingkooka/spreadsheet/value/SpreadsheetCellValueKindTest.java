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

package walkingkooka.spreadsheet.value;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.validation.ValueType;
import walkingkooka.validation.provider.ValidatorSelector;

import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public final class SpreadsheetCellValueKindTest implements TreePrintableTesting,
    ClassTesting<SpreadsheetCellValueKind> {

    @Test
    public void testCellValue() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValueType(
                Optional.of(
                    ValueType.with("hello-value-type")
                )
            )
        ).setCurrency(
            Optional.of(
                Currency.getInstance("AUD")
            )
        ).setDateTimeSymbols(
            Optional.of(
                DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(Locale.FRANCE)
                )
            )
        ).setDecimalNumberSymbols(
            Optional.of(
                DecimalNumberSymbols.fromDecimalFormatSymbols(
                    '+',
                    new DecimalFormatSymbols(Locale.FRANCE)
                )
            )
        ).setLocale(
            Optional.of(Locale.ENGLISH)
        ).setFormatter(
            Optional.of(
                SpreadsheetFormatterSelector.parse("hello-formatter")
            )
        ).setFormattedValue(
            Optional.of(
                TextNode.text("formatted-value")
            )
        ).setParser(
            Optional.of(
                SpreadsheetParserSelector.parse("hello-parser")
            )
        ).setStyle(
            TextStyle.EMPTY.set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.CENTER
            )
        ).setValidator(
            Optional.of(
                ValidatorSelector.parse("hello-validator")
            )
        );

        final Set<Object> values = Sets.hash();
        for (final SpreadsheetCellValueKind kind : SpreadsheetCellValueKind.values()) {
            final Object value = values.add(
                kind.cellValue(cell)
            );
            this.checkNotEquals(
                Optional.empty(),
                value,
                () -> kind + " value missing returned Optional#empty"
            );

            this.checkEquals(
                true,
                value,
                () -> kind + " returned duplicate value (must be returning wrong property"
            );
        }
    }

    @Test
    public void testCellValueWithCell() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.CELL,
            (c) -> c
        );
    }

    @Test
    public void testCellValueWithCurrency() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.CURRENCY,
            (c) -> c.currency()
        );
    }

    @Test
    public void testCellValueWithDateTimeSymbols() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.DATE_TIME_SYMBOLS,
            (c) -> c.dateTimeSymbols()
        );
    }

    @Test
    public void testCellValueWithDecimalNumberSymbols() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.DECIMAL_NUMBER_SYMBOLS,
            (c) -> c.decimalNumberSymbols()
        );
    }

    @Test
    public void testCellValueWithFormula() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.FORMULA,
            (c) -> c.formula()
        );
    }

    @Test
    public void testCellValueWithFormatter() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.FORMATTER,
            (c) -> c.formatter()
        );
    }

    @Test
    public void testCellValueWithLocale() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.LOCALE,
            (c) -> c.locale()
        );
    }

    @Test
    public void testCellValueWithParser() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.PARSER,
            (c) -> c.parser()
        );
    }

    @Test
    public void testCellValueWithStyle() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.STYLE,
            (c) -> c.style()
        );
    }

    @Test
    public void testCellValueWithValidator() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.VALIDATOR,
            (c) -> c.validator()
        );
    }

    @Test
    public void testCellValueWithValue() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.VALUE,
            (c) -> c.formula()
                .value()
        );
    }

    @Test
    public void testCellValueWithValueType() {
        this.cellValueAndCheck(
            SpreadsheetCellValueKind.VALUE_TYPE,
            (c) -> c.formula()
                .valueType()
        );
    }

    private void cellValueAndCheck(final SpreadsheetCellValueKind kind,
                                   final Function<SpreadsheetCell, Object> expected) {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setValueType(
                Optional.of(
                    ValueType.with("hello-value-type")
                )
            )
        ).setCurrency(
            Optional.of(
                Currency.getInstance("AUD")
            )
        ).setDateTimeSymbols(
            Optional.of(
                DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(Locale.FRANCE)
                )
            )
        ).setDecimalNumberSymbols(
            Optional.of(
                DecimalNumberSymbols.fromDecimalFormatSymbols(
                    '+',
                    new DecimalFormatSymbols(Locale.FRANCE)
                )
            )
        ).setLocale(
            Optional.of(Locale.ENGLISH)
        ).setFormatter(
            Optional.of(
                SpreadsheetFormatterSelector.parse("hello-formatter")
            )
        ).setFormattedValue(
            Optional.of(
                TextNode.text("formatted-value")
            )
        ).setParser(
            Optional.of(
                SpreadsheetParserSelector.parse("hello-parser")
            )
        ).setStyle(
            TextStyle.EMPTY.set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.CENTER
            )
        ).setValidator(
            Optional.of(
                ValidatorSelector.parse("hello-validator")
            )
        );

        this.cellValueAndCheck(
            kind,
            cell,
            expected.apply(cell)
        );
    }

    private void cellValueAndCheck(final SpreadsheetCellValueKind kind,
                                   final SpreadsheetCell cell,
                                   final Object expected) {
        this.checkEquals(
            expected,
            kind.cellValue(cell),
            cell::toString
        );
    }

    // fileExtension....................................................................................................

    @Test
    public void testFileExtensionWithCell() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.CELL,
            "cell"
        );
    }

    @Test
    public void testFileExtensionWithCurrency() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.CURRENCY,
            "currency"
        );
    }

    @Test
    public void testFileExtensionWithDecimalNumberSymbols() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.DECIMAL_NUMBER_SYMBOLS,
            "decimal-number-symbols"
        );
    }

    @Test
    public void testFileExtensionWithValueType() {
        this.fileExtensionAndCheck(
            SpreadsheetCellValueKind.VALUE_TYPE,
            "value-type"
        );
    }

    private void fileExtensionAndCheck(final SpreadsheetCellValueKind kind,
                                       final String expected) {
        this.checkEquals(
            expected,
            kind.fileExtension()
                .value()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCellValueKind> type() {
        return SpreadsheetCellValueKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
