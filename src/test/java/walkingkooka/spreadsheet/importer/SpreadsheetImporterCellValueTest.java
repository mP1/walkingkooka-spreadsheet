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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.datetime.OptionalDateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.math.OptionalDecimalNumberSymbols;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.provider.OptionalSpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.provider.OptionalSpreadsheetParserSelector;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.OptionalSpreadsheetValue;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.text.OptionalTextNode;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.util.OptionalCurrency;
import walkingkooka.util.OptionalLocale;
import walkingkooka.validation.provider.OptionalValidatorSelector;
import walkingkooka.validation.provider.ValidatorSelector;

import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetImporterCellValueTest implements HasSpreadsheetReferenceTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetImporterCellValue>,
    ToStringTesting<SpreadsheetImporterCellValue>,
    TreePrintableTesting,
    ClassTesting2<SpreadsheetImporterCellValue> {

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.A1;

    private final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    // cell.............................................................................................................

    @Test
    public void testCellWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.cell(null)
        );
    }

    @Test
    public void testCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY);

        this.check(
            SpreadsheetImporterCellValue.cell(cell),
            cell.reference(),
            cell
        );
    }

    // formula..........................................................................................................

    @Test
    public void testFormulaWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.formula(
                null,
                SpreadsheetFormula.EMPTY
            )
        );
    }

    @Test
    public void testFormulaWithNullFormulaFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.formula(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testFormula() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");

        this.check(
            SpreadsheetImporterCellValue.formula(
                CELL_REFERENCE,
                formula
            ),
            formula
        );
    }

    // currency..........................................................................................................

    @Test
    public void testCurrencyWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.currency(
                null,
                OptionalCurrency.EMPTY
            )
        );
    }

    @Test
    public void testCurrencyWithNullCurrencyFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.currency(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testCurrency() {
        final OptionalCurrency currency = OptionalCurrency.with(
            Optional.of(
                Currency.getInstance("AUD")
            )
        );

        this.check(
            SpreadsheetImporterCellValue.currency(
                CELL_REFERENCE,
                currency
            ),
            currency
        );
    }

    // dateTimeSymbols..................................................................................................

    @Test
    public void testDateTimeSymbolsWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.dateTimeSymbols(
                null,
                OptionalDateTimeSymbols.EMPTY
            )
        );
    }

    @Test
    public void testDateTimeSymbolsWithNullDateTimeSymbolsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.dateTimeSymbols(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testDateTimeSymbols() {
        final OptionalDateTimeSymbols dateTimeSymbols = OptionalDateTimeSymbols.with(
            Optional.of(
                DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(LOCALE)
                )
            )
        );

        this.check(
            SpreadsheetImporterCellValue.dateTimeSymbols(
                CELL_REFERENCE,
                dateTimeSymbols
            ),
            dateTimeSymbols
        );
    }

    // decimalNumberSymbols.............................................................................................

    @Test
    public void testDecimalNumberSymbolsWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.decimalNumberSymbols(
                null,
                OptionalDecimalNumberSymbols.EMPTY
            )
        );
    }

    @Test
    public void testDecimalNumberSymbolsWithNullDecimalNumberSymbolsFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.decimalNumberSymbols(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testDecimalNumberSymbols() {
        final OptionalDecimalNumberSymbols decimalNumberSymbols = OptionalDecimalNumberSymbols.with(
            Optional.of(
                DecimalNumberSymbols.fromDecimalFormatSymbols(
                    '+',
                    new DecimalFormatSymbols(LOCALE)
                )
            )
        );

        this.check(
            SpreadsheetImporterCellValue.decimalNumberSymbols(
                CELL_REFERENCE,
                decimalNumberSymbols
            ),
            decimalNumberSymbols
        );
    }
    
    // formatter........................................................................................................

    @Test
    public void testFormatterWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.formatter(
                null,
                OptionalSpreadsheetFormatterSelector.EMPTY
            )
        );
    }

    @Test
    public void testFormatterWithNullFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.formatter(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testFormatter() {
        final OptionalSpreadsheetFormatterSelector formatter = OptionalSpreadsheetFormatterSelector.with(
            Optional.of(
                SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT
            )
        );

        this.check(
            SpreadsheetImporterCellValue.formatter(
                CELL_REFERENCE,
                formatter
            ),
            formatter
        );
    }

    // locale..........................................................................................................

    @Test
    public void testLocaleWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.locale(
                null,
                OptionalLocale.EMPTY
            )
        );
    }

    @Test
    public void testLocaleWithNullLocaleFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.locale(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testLocale() {
        final OptionalLocale locale = OptionalLocale.with(
            Optional.of(LOCALE)
        );

        this.check(
            SpreadsheetImporterCellValue.locale(
                CELL_REFERENCE,
                locale
            ),
            locale
        );
    }
    
    // parser...........................................................................................................

    @Test
    public void testParserWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.parser(
                null,
                OptionalSpreadsheetParserSelector.EMPTY
            )
        );
    }

    @Test
    public void testParserWithNullParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.parser(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testParser() {
        final OptionalSpreadsheetParserSelector parser = OptionalSpreadsheetParserSelector.with(
            Optional.of(
                SpreadsheetParserSelector.parse("test-parser")
            )
        );

        this.check(
            SpreadsheetImporterCellValue.parser(
                CELL_REFERENCE,
                parser
            ),
            parser
        );
    }

    // textStyle........................................................................................................

    @Test
    public void testStyleWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.style(
                null,
                TextStyle.EMPTY.set(
                    TextStylePropertyName.COLOR,
                    Color.BLACK
                )
            )
        );
    }

    @Test
    public void testStyleFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.style(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testStyle() {
        final TextStyle textStyle = TextStyle.EMPTY.set(
            TextStylePropertyName.COLOR,
            Color.BLACK
        );

        this.check(
            SpreadsheetImporterCellValue.style(
                CELL_REFERENCE,
                textStyle
            ),
            textStyle
        );
    }

    // validator........................................................................................................

    @Test
    public void testValidatorWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.validator(
                null,
                OptionalValidatorSelector.EMPTY
            )
        );
    }

    @Test
    public void testValidatorWithNullValidatorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.validator(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testValidator() {
        final OptionalValidatorSelector validator = OptionalValidatorSelector.with(
            Optional.of(
                ValidatorSelector.parse("test-validator-123")
            )
        );

        this.check(
            SpreadsheetImporterCellValue.validator(
                CELL_REFERENCE,
                validator
            ),
            validator
        );
    }
    
    // value............................................................................................................

    @Test
    public void testValueWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.value(
                null,
                OptionalSpreadsheetValue.EMPTY
            )
        );
    }

    @Test
    public void testValueWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.value(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testValue() {
        final OptionalSpreadsheetValue<?> value = OptionalSpreadsheetValue.with(
            Optional.of(
                "Hello"
            )
        );

        this.check(
            SpreadsheetImporterCellValue.value(
                CELL_REFERENCE,
                value
            ),
            value
        );
    }

    // formattedValue...................................................................................................

    @Test
    public void testFormattedValueWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.formattedValue(
                null,
                OptionalTextNode.EMPTY
            )
        );
    }

    @Test
    public void testFormattedValueWithNullFormattedValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCellValue.formattedValue(
                SpreadsheetSelection.A1,
                null
            )
        );
    }

    @Test
    public void testFormattedValue() {
        final OptionalTextNode formattedValue = OptionalTextNode.with(
            Optional.of(
                TextNode.text("Hello")
            )
        );

        this.check(
            SpreadsheetImporterCellValue.formattedValue(
                CELL_REFERENCE,
                formattedValue
            ),
            formattedValue
        );
    }

    private void check(final SpreadsheetImporterCellValue importCellValue,
                       final Object value) {
        this.check(
            importCellValue,
            CELL_REFERENCE,
            value
        );
    }

    private void check(final SpreadsheetImporterCellValue importCellValue,
                       final SpreadsheetCellReference reference,
                       final Object value) {
        this.referenceAndCheck(
            importCellValue,
            reference
        );
        this.checkEquals(
            value,
            importCellValue.value(),
            "value"
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
            SpreadsheetImporterCellValue.formula(
                CELL_REFERENCE,
                SpreadsheetFormula.EMPTY.setText("=123+456")
            )
        );
    }

    @Override
    public SpreadsheetImporterCellValue createObject() {
        return SpreadsheetImporterCellValue.cell(
            CELL_REFERENCE.setFormula(
                SpreadsheetFormula.EMPTY.setText("=123+456")
            )
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetImporterCellValue.cell(
                CELL_REFERENCE.setFormula(
                    SpreadsheetFormula.EMPTY.setText("=123+456")
                )
            ),
            "A1=A1 \"=123+456\""
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintableCell() {
        this.treePrintAndCheck(
            SpreadsheetImporterCellValue.cell(
                CELL_REFERENCE.setFormula(
                    SpreadsheetFormula.EMPTY.setText("=123+456")
                )
            ),
            "A1\n" +
                "  Cell A1\n" +
                "    Formula\n" +
                "      text:\n" +
                "        \"=123+456\"\n"
        );
    }

    @Test
    public void testTreePrintableFormula() {
        this.treePrintAndCheck(
            SpreadsheetImporterCellValue.formula(
                CELL_REFERENCE,
                SpreadsheetFormula.EMPTY.setText("=123+456")
            ),
            "A1\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=123+456\"\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetImporterCellValue> type() {
        return SpreadsheetImporterCellValue.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
