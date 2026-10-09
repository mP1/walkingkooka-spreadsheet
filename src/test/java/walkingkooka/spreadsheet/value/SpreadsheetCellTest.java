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
import walkingkooka.CanBeEmptyTesting;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.currency.HasOptionalCurrencyTesting;
import walkingkooka.currency.provider.CurrencyExchangeRaterSelector;
import walkingkooka.currency.provider.HasOptionalCurrencyExchangeRaterSelectorTesting;
import walkingkooka.datetime.HasOptionalDateTimeSymbolsTesting;
import walkingkooka.math.HasOptionalDecimalNumberSymbolsTesting;
import walkingkooka.net.header.HasContentTypeTesting;
import walkingkooka.net.http.server.hateos.HateosResourceTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.HasOptionalSpreadsheetFormatterSelectorTesting;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.provider.HasOptionalSpreadsheetParserSelectorTesting;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.CanReplaceReferencesTesting2;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.patch.PatchableTesting;
import walkingkooka.tree.text.HasTextNodeTesting;
import walkingkooka.tree.text.HasTextStyleTesting;
import walkingkooka.tree.text.TextAlign;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.util.HasOptionalLocaleTesting;
import walkingkooka.validation.HasOptionalValueTypeTesting;
import walkingkooka.validation.HasValidationPromptValueTesting;
import walkingkooka.validation.ValidationChoice;
import walkingkooka.validation.ValidationChoiceList;
import walkingkooka.validation.provider.HasOptionalValidatorSelectorTesting;
import walkingkooka.validation.provider.ValidatorSelector;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellTest implements CanBeEmptyTesting,
    ClassTesting2<SpreadsheetCell>,
    CanReplaceReferencesTesting2<SpreadsheetCell>,
    HashCodeEqualsDefinedTesting2<SpreadsheetCell>,
    HasContentTypeTesting,
    HasOptionalCurrencyTesting,
    HasOptionalCurrencyExchangeRaterSelectorTesting,
    HasOptionalDateTimeSymbolsTesting,
    HasOptionalDecimalNumberSymbolsTesting,
    HasOptionalLocaleTesting,
    HasOptionalSpreadsheetFormatterSelectorTesting,
    HasOptionalSpreadsheetParserSelectorTesting,
    HasOptionalValidatorSelectorTesting,
    HasOptionalValueTypeTesting,
    HasSpreadsheetCellTesting,
    HasSpreadsheetReferenceTesting,
    HasTextNodeTesting,
    HasTextStyleTesting,
    HasTextTesting,
    HasValidationPromptValueTesting,
    HateosResourceTesting2<SpreadsheetCell, SpreadsheetCellReference>,
    JsonNodeMarshallerTesting<SpreadsheetCell>,
    ParseStringTesting<SpreadsheetCell>,
    PatchableTesting<SpreadsheetCell>,
    HasSpreadsheetMetadataTesting,
    ThrowableTesting,
    ToStringTesting<SpreadsheetCell>,
    TreePrintableTesting {

    private final static SpreadsheetFormula FORMULA = SpreadsheetFormula.EMPTY
        .setText("=1+2");

    private final static SpreadsheetFormula FORMULA_EQ_1 = SpreadsheetFormula.EMPTY
        .setText("=1");

    private final static SpreadsheetFormula DIFFERENT_FORMULA = SpreadsheetFormula.EMPTY.setText("'Different");

    private final static Optional<SpreadsheetFormulaParserToken> TOKEN = Optional.of(
        SpreadsheetFormulaParserToken.expression(
            Lists.of(
                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                SpreadsheetFormulaParserToken.addition(
                    Lists.of(
                        SpreadsheetFormulaParserToken.number(
                            List.of(
                                SpreadsheetFormulaParserToken.digits("1", "1")
                            ),
                            "1"
                        ),
                        SpreadsheetFormulaParserToken.number(
                            List.of(
                                SpreadsheetFormulaParserToken.digits("2", "2")
                            ),
                            "2"
                        )
                    ),
                    FORMULA.text().substring(1)
                )
            ),
            FORMULA.text()
        )
    );

    private final static Optional<Expression> EXPRESSION = Optional.of(
        Expression.add(
            Expression.value(
                EXPRESSION_NUMBER_KIND.one()
            ),
            Expression.value(
                EXPRESSION_NUMBER_KIND.create(2)
            )
        )
    );

    private final static Optional<Object> VALUE = Optional.of(3);

    private final static Optional<TextNode> FORMATTED_VALUE = Optional.of(
        TextNode.text("formattedValue-text")
    );

    final Optional<TextNode> DIFFERENT_FORMATTED_VALUE = Optional.of(
        TextNode.text("different-formatted-value")
    );

    // with.............................................................................................................

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCell.with(
                null,
                FORMULA
            )
        );
    }

    @Test
    public void testWithNullFormulaFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCell.with(
                REFERENCE,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetCell cell = this.createCell();

        this.referenceAndCheck(cell, REFERENCE);
        this.formulaAndCheck(cell, FORMULA);
        this.currencyAndCheck(cell, CURRENCY);
        this.currencyExchangeRaterAndCheck(cell, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(cell, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(cell, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(cell, FORMATTER_SELECTOR);
        this.localeAndCheck(cell, LOCALE);
        this.parserAndCheck(cell, PARSER_SELECTOR);
        this.styleAndCheck(cell, TEXT_STYLE);
        this.validatorAndCheck(cell, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(cell, FORMATTED_VALUE);
    }

    @Test
    public void testWithAbsoluteReference() {
        final SpreadsheetCellReference reference = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.parseCell("$B$2"),
            FORMULA
        );

        this.referenceAndCheck(
            cell,
            reference.toRelative()
        );
        this.formulaAndCheck(cell, FORMULA);
        this.currencyAndCheck(cell);
        this.currencyExchangeRaterAndCheck(cell);
        this.dateTimeSymbolsAndCheck(cell);
        this.decimalNumberSymbolsAndCheck(cell);
        this.formatterAndCheck(cell);
        this.localeAndCheck(cell);
        this.parserAndCheck(cell);
        this.styleAndCheck(cell);
        this.validatorAndCheck(cell);
        this.formattedValueAndCheck(cell);
    }

    @Test
    public void testWithFormula() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, FORMULA);

        this.referenceAndCheck(cell, REFERENCE);
        this.formulaAndCheck(cell, FORMULA);
        this.currencyAndCheck(cell);
        this.currencyExchangeRaterAndCheck(cell);
        this.dateTimeSymbolsAndCheck(cell);
        this.decimalNumberSymbolsAndCheck(cell);
        this.formatterAndCheck(cell);
        this.localeAndCheck(cell);
        this.parserAndCheck(cell);
        this.styleAndCheck(cell);
        this.validatorAndCheck(cell);
        this.formattedValueAndCheck(cell);
    }

    @Test
    public void testWithFormulaListValue() {
        final Optional<Object> value = Optional.of(
            Lists.empty()
        );

        final SpreadsheetCell cell = SpreadsheetCell.with(
            REFERENCE,
            FORMULA.setValue(value)
        );

        this.referenceAndCheck(cell, REFERENCE);
        this.formulaAndCheck(
            cell,
            FORMULA.setValue(
                Optional.of(
                    SpreadsheetErrorKind.VALUE.spreadsheetError()
                        .setValue(value)
                )
            )
        );
        this.currencyAndCheck(cell);
        this.currencyExchangeRaterAndCheck(cell);
        this.dateTimeSymbolsAndCheck(cell);
        this.decimalNumberSymbolsAndCheck(cell);
        this.formatterAndCheck(cell);
        this.localeAndCheck(cell);
        this.parserAndCheck(cell);
        this.styleAndCheck(cell);
        this.validatorAndCheck(cell);
        this.formattedValueAndCheck(cell);
    }

    @Test
    public void testWithFormulaWithValue() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setValue(
            Optional.of(
                Optional.of(123)
            )
        );

        final SpreadsheetCell cell = SpreadsheetCell.with(
            REFERENCE,
            formula
        );

        this.referenceAndCheck(cell, REFERENCE);
        this.formulaAndCheck(
            cell,
            formula
        );
        this.currencyAndCheck(cell);
        this.currencyExchangeRaterAndCheck(cell);
        this.dateTimeSymbolsAndCheck(cell);
        this.decimalNumberSymbolsAndCheck(cell);
        this.formatterAndCheck(cell);
        this.localeAndCheck(cell);
        this.parserAndCheck(cell);
        this.styleAndCheck(cell);
        this.validatorAndCheck(cell);
        this.formattedValueAndCheck(cell);
    }

    // setReference.....................................................................................................

    @Test
    public void testSetReferenceNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setReference(null)
        );
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setReference(
                cell.reference()
            )
        );
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCell different = cell.setReference(DIFFERENT_REFERENCE);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, DIFFERENT_REFERENCE);
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(cell, CURRENCY);
        this.currencyExchangeRaterAndCheck(cell, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(cell, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(cell, FORMATTER_SELECTOR);
        this.localeAndCheck(cell, LOCALE);
        this.parserAndCheck(cell, PARSER_SELECTOR);
        this.referenceAndCheck(cell, REFERENCE);
        this.checkEquals(
            cell.parser(),
            different.parser(),
            "parser"
        );

        this.formulaAndCheck(cell, FORMULA);
        this.checkEquals(
            cell.formatter(),
            different.formatter(),
            "formatter"
        );
        this.validatorAndCheck(cell, VALIDATOR_SELECTOR);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
    }

    // setFormula.......................................................................................................

    @Test
    public void testSetFormulaNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setFormula(null)
        );
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setFormula(
                cell.formula()
            )
        );
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCell different = cell.setFormula(DIFFERENT_FORMULA);
        assertNotSame(
            cell,
            different
        );

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, DIFFERENT_FORMULA);
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(cell, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of formula / value change.
    }

    @Test
    public void testSetFormulaWithListValue() {
        final SpreadsheetCell cell = this.createCell();

        final Optional<Object> value = Optional.of(
            Lists.of(1, 22, "three")
        );

        final SpreadsheetCell different = cell.setFormula(
            SpreadsheetFormula.EMPTY
                .setValue(value)
        );
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
            different,
            SpreadsheetFormula.EMPTY
                .setValue(
                    Optional.of(
                        SpreadsheetErrorKind.VALUE.spreadsheetError()
                            .setValue(value)
                    )
                )
        );
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(cell, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of formula / value change.
    }

    private void formulaAndCheck(final SpreadsheetCell cell,
                                 final SpreadsheetFormula formula) {
        this.checkEquals(
            formula,
            cell.formula(),
            "formula"
        );
    }

    // setCurrency......................................................................................................

    @Test
    public void testSetCurrencyNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setCurrency(null)
        );
    }

    @Test
    public void testSetCurrencySame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setCurrency(
                cell.currency()
            )
        );
    }

    @Test
    public void testSetCurrencyDifferent() {
        final SpreadsheetCell cell = this.createCell();

        final SpreadsheetCell different = cell.setCurrency(OPTIONAL_DIFFERENT_CURRENCY);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(
            different,
            DIFFERENT_CURRENCY
        );
        this.currencyExchangeRaterAndCheck(cell, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.decimalNumberSymbolsAndCheck(cell, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(cell, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of currency / value change.
    }

    // setCurrencyExchangeRater.........................................................................................

    @Test
    public void testSetCurrencyExchangeRaterNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setCurrencyExchangeRater(null)
        );
    }

    @Test
    public void testSetCurrencyExchangeRaterSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setCurrencyExchangeRater(
                cell.currencyExchangeRater()
            )
        );
    }

    @Test
    public void testSetCurrencyExchangeRaterDifferent() {
        final SpreadsheetCell cell = this.createCell();

        final SpreadsheetCell different = cell.setCurrencyExchangeRater(OPTIONAL_DIFFERENT_CURRENCY_EXCHANGE_RATER_SELECTOR);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(
            different,
            DIFFERENT_CURRENCY_EXCHANGE_RATER_SELECTOR
        );
        this.decimalNumberSymbolsAndCheck(cell, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(cell, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);

        this.formattedValueAndCheck(different); // clear formattedValue because of currencyExchangeRater / value change.
    }

    private void currencyExchangeRaterAndCheck(final SpreadsheetCell cell) {
        this.currencyExchangeRaterAndCheck(
            cell,
            SpreadsheetCell.NO_CURRENCY_EXCHANGE_RATER
        );
    }

    private void currencyExchangeRaterAndCheck(final SpreadsheetCell cell,
                                               final CurrencyExchangeRaterSelector currencyExchangeRater) {
        this.currencyExchangeRaterAndCheck(
            cell,
            Optional.of(currencyExchangeRater)
        );
    }

    private void currencyExchangeRaterAndCheck(final SpreadsheetCell cell,
                                               final Optional<CurrencyExchangeRaterSelector> currencyExchangeRater) {
        this.checkEquals(
            currencyExchangeRater,
            cell.currencyExchangeRater(),
            "currencyExchangeRater"
        );

        this.currencyExchangeRaterSelectorAndCheck(
            cell,
            currencyExchangeRater
        );
    }

    // setDateTimeSymbols...............................................................................................

    @Test
    public void testSetDateTimeSymbolsNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setDateTimeSymbols(null)
        );
    }

    @Test
    public void testSetDateTimeSymbolsSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setDateTimeSymbols(
                cell.dateTimeSymbols()
            )
        );
    }

    @Test
    public void testSetDateTimeSymbolsDifferent() {
        final SpreadsheetCell cell = this.createCell();

        final SpreadsheetCell different = cell.setDateTimeSymbols(OPTIONAL_DIFFERENT_DATE_TIME_SYMBOLS);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(
            different,
            DIFFERENT_DATE_TIME_SYMBOLS
        );
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of dateTimeSymbols / value change.
    }

    // setDecimalNumberSymbols..........................................................................................

    @Test
    public void testSetDecimalNumberSymbolsNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setDecimalNumberSymbols(null)
        );
    }

    @Test
    public void testSetDecimalNumberSymbolsSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setDecimalNumberSymbols(
                cell.decimalNumberSymbols()
            )
        );
    }

    @Test
    public void testSetDecimalNumberSymbolsDifferent() {
        final SpreadsheetCell cell = this.createCell();

        final SpreadsheetCell different = cell.setDecimalNumberSymbols(OPTIONAL_DIFFERENT_DECIMAL_NUMBER_SYMBOLS);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(
            different,
            OPTIONAL_DIFFERENT_DECIMAL_NUMBER_SYMBOLS
        );
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of decimalNumberSymbols / value change.
    }

    // setLocale........................................................................................................

    @Test
    public void testSetLocaleNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setLocale(null)
        );
    }

    @Test
    public void testSetLocaleSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setLocale(
                cell.locale()
            )
        );
    }

    @Test
    public void testSetLocaleDifferent() {
        final SpreadsheetCell cell = this.createCell();

        final SpreadsheetCell different = cell.setLocale(OPTIONAL_DIFFERENT_LOCALE);
        assertNotSame(
            cell,
            different
        );

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
            different,
            FORMULA
        );
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(
            different,
            DIFFERENT_LOCALE
        );
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different);
    }

    // setFormatter.....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetFormatterNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setFormatter(null)
        );
    }

    @Test
    public void testSetFormatterSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setFormatter(
                cell.formatter()
            )
        );
    }

    @Test
    public void testSetFormatterDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCell different = cell.setFormatter(OPTIONAL_DIFFERENT_FORMATTER_SELECTOR);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
            different,
            FORMULA
        );
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(
            different,
            DIFFERENT_FORMATTER_SELECTOR
        );
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of format change
    }

    @Test
    public void testSetFormatterWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, FORMULA)
            .setFormatter(OPTIONAL_FORMATTER_SELECTOR);
        final SpreadsheetCell different = cell.setFormatter(SpreadsheetCell.NO_FORMATTER);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(different);
        this.currencyExchangeRaterAndCheck(different);
        this.dateTimeSymbolsAndCheck(different);
        this.decimalNumberSymbolsAndCheck(different);
        this.formatterAndCheck(different);
        this.localeAndCheck(different);
        this.parserAndCheck(different);
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheck(different);
    }

    private void formatterAndCheck(final SpreadsheetCell cell) {
        this.formatterSelectorAndCheck(
            cell,
            SpreadsheetCell.NO_FORMATTER
        );
    }

    private void formatterAndCheck(final SpreadsheetCell cell,
                                   final SpreadsheetFormatterSelector formatter) {
        this.formatterSelectorAndCheck(
            cell,
            Optional.of(formatter)
        );
    }

    private void formatterAndCheck(final SpreadsheetCell cell,
                                   final Optional<SpreadsheetFormatterSelector> formatter) {
        this.checkEquals(
            formatter,
            cell.formatter(),
            "formatter"
        );

        this.formatterSelectorAndCheck(
            cell,
            formatter
        );
    }

    // setParser........................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetParserNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setParser(null)
        );
    }

    @Test
    public void testSetParserSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setParser(
                cell.parser()
            )
        );
    }

    @Test
    public void testSetParserSameDoesntClearFormulaToken() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
            .setText("'A");

        final SpreadsheetCell cell = this.createCell()
            .setFormula(
                formula.setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.text(
                            Lists.of(
                                SpreadsheetFormulaParserToken.textLiteral("'A", "'A")
                            ),
                            "'A"
                        )
                    )
                )
            );
        assertSame(
            cell,
            cell.setParser(cell.parser())
        );
    }

    @Test
    public void testSetParserDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCell different = cell.setParser(OPTIONAL_DIFFERENT_PARSER_SELECTOR);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
            different,
            FORMULA
        );
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(
            different,
            DIFFERENT_PARSER_SELECTOR
        );
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of format change
    }

    @Test
    public void testSetParserDifferentClearsFormulaTokenAndExpression() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
            .setText("'A");

        final SpreadsheetCell cell = this.createCell()
            .setFormula(
                formula.setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.text(
                            Lists.of(
                                SpreadsheetFormulaParserToken.textLiteral("'A", "'A")
                            ),
                            "'A"
                        )
                    )
                )
            );
        final SpreadsheetCell different = cell.setParser(OPTIONAL_DIFFERENT_PARSER_SELECTOR);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, formula);
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, DIFFERENT_PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of format change
    }

    @Test
    public void testSetParserWhenWithout() {
        final SpreadsheetCell cell = SpreadsheetCell.with(REFERENCE, FORMULA);
        final SpreadsheetCell different = cell.setParser(OPTIONAL_PARSER_SELECTOR);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(different, FORMULA);
        this.currencyAndCheck(different);
        this.currencyExchangeRaterAndCheck(different);
        this.dateTimeSymbolsAndCheck(different);
        this.decimalNumberSymbolsAndCheck(different);
        this.formatterAndCheck(different);
        this.localeAndCheck(cell);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different);
        this.validatorAndCheck(different);
        this.formattedValueAndCheck(different);
    }

    private void parserAndCheck(final SpreadsheetCell cell) {
        this.parserAndCheck(
            cell,
            SpreadsheetCell.NO_PARSER
        );
    }

    private void parserAndCheck(final SpreadsheetCell cell,
                                final SpreadsheetParserSelector selector) {
        this.parserAndCheck(
            cell,
            Optional.of(selector)
        );
    }

    private void parserAndCheck(final SpreadsheetCell cell,
                                final Optional<SpreadsheetParserSelector> selector) {
        this.checkEquals(
            selector,
            cell.parser(),
            "parser"
        );

        this.parserSelectorAndCheck(
            cell,
            selector
        );
    }

    // setStyle.........................................................................................................

    @Test
    public void testSetStyleNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setStyle(null)
        );
    }

    @Test
    public void testSetStyleSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setStyle(
                cell.style()
            )
        );
    }

    @Test
    public void testSetStyleDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCell different = cell.setStyle(DIFFERENT_TEXT_STYLE);
        assertNotSame(
            cell,
            different
        );

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
            different,
            FORMULA
        );
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(
            different,
            DIFFERENT_TEXT_STYLE
        );
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(different); // clear formattedValue because of text properties change
    }

    private void styleAndCheck(final SpreadsheetCell cell) {
        this.styleAndCheck(
            cell,
            SpreadsheetCell.NO_STYLE
        );
    }

    private void styleAndCheck(final SpreadsheetCell cell,
                               final TextStyle style) {
        this.checkEquals(
            style,
            cell.style(),
            "style"
        );
    }

    // setValidator.....................................................................................................

    @Test
    public void testSetValidatorNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell()
                .setValidator(null)
        );
    }

    @Test
    public void testSetValidatorSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setValidator(
                cell.validator()
            )
        );
    }

    @Test
    public void testSetValidatorDifferent() {
        final SpreadsheetCell cell = this.createCell();

        final SpreadsheetCell different = cell.setValidator(OPTIONAL_DIFFERENT_VALIDATOR_SELECTOR);
        assertNotSame(
            cell,
            different
        );

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
            different,
            FORMULA
        );
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(different, FORMATTER_SELECTOR);
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(
            different,
            OPTIONAL_DIFFERENT_VALIDATOR_SELECTOR
        );
        this.formattedValueAndCheck(different);
    }

    private void validatorAndCheck(final SpreadsheetCell cell) {
        this.validatorAndCheck(
            cell,
            SpreadsheetCell.NO_VALIDATOR
        );
    }

    private void validatorAndCheck(final SpreadsheetCell cell,
                                   final ValidatorSelector expected) {
        this.validatorAndCheck(
            cell,
            Optional.of(expected)
        );
    }

    private void validatorAndCheck(final SpreadsheetCell cell,
                                   final Optional<ValidatorSelector> expected) {
        this.checkEquals(
            expected,
            cell.validator(),
            "validator"
        );

        this.validatorSelectorAndCheck(
            cell,
            expected
        );
    }

    // setFormattedValue................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetFormattedValueNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createCell().setFormattedValue(null)
        );
    }

    @Test
    public void testSetFormattedValueSame() {
        final SpreadsheetCell cell = this.createCell();
        assertSame(
            cell,
            cell.setFormattedValue(FORMATTED_VALUE)
        );
    }

    @Test
    public void testSetFormattedValueDifferent() {
        final SpreadsheetCell cell = this.createCell();
        final SpreadsheetCell different = cell.setFormattedValue(DIFFERENT_FORMATTED_VALUE);
        assertNotSame(cell, different);

        this.referenceAndCheck(different, REFERENCE);
        this.formulaAndCheck(
            different,
            FORMULA
        );
        this.currencyAndCheck(different, CURRENCY);
        this.currencyExchangeRaterAndCheck(different, CURRENCY_EXCHANGE_RATER_SELECTOR);
        this.dateTimeSymbolsAndCheck(different, DATE_TIME_SYMBOLS);
        this.decimalNumberSymbolsAndCheck(different, DECIMAL_NUMBER_SYMBOLS);
        this.formatterAndCheck(
            different,
            FORMATTER_SELECTOR
        );
        this.localeAndCheck(different, LOCALE);
        this.parserAndCheck(different, PARSER_SELECTOR);
        this.styleAndCheck(different, TEXT_STYLE);
        this.validatorAndCheck(different, VALIDATOR_SELECTOR);
        this.formattedValueAndCheck(
            different,
            DIFFERENT_FORMATTED_VALUE
        );
    }

    private void formattedValueAndCheck(final SpreadsheetCell cell) {
        this.formattedValueAndCheck(
            cell,
            SpreadsheetCell.NO_FORMATTED_VALUE
        );
    }

    private void formattedValueAndCheck(final SpreadsheetCell cell,
                                        final Optional<TextNode> formatted) {
        this.checkEquals(
            formatted,
            cell.formattedValue(),
            "formattedValue"
        );
    }

    // replaceReferences................................................................................................

    @Test
    public void testReplaceReferencesWithMapperReturnsEmptyForReference() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .replaceReferences((cell) -> Optional.empty())
        );
        this.getMessageAndCheck(
            thrown,
            "Mapper returned nothing for A1"
        );
    }

    @Test
    public void testReplaceReferencesMapperReturnsCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            parseFormula("=1+B2")
        );
        this.replaceReferencesAndCheck(
            cell,
            Optional::of
        );
    }

    @Test
    public void testReplaceReferencesMove() {
        this.replaceReferencesAndCheck(
            SpreadsheetSelection.A1.setFormula(
                parseFormula("=1+B2")
            ),
            (c) ->
                Optional.of(
                    c.add(
                        1,
                        2
                    )
                ),
            SpreadsheetSelection.parseCell("B3")
                .setFormula(
                    parseFormula("=1+C4")
                )
        );
    }

    @Override
    public SpreadsheetCell createReplaceReference() {
        return this.createCell();
    }

    // HasText..........................................................................................................

    @Test
    public void testTextWhenReferenceAndEmptyFormulaText() {
        this.textAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            "A1,,,,,,,,,,,,,"
        );
    }

    @Test
    public void testTextWhenReferenceAndNonEmptyFormulaText() {
        this.textAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY.setText("=1+2+magic(\"hello\")")),
            "A1,\"=1+2+magic(\"\"hello\"\")\",,,,,,,,,,,,"
        );
    }

    @Test
    public void testTextWhenReferenceAndValueTypeAndFormattedValue() {
        this.textAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setValueType(
                    OPTIONAL_VALUE_TYPE
                ).setValue(
                    Optional.of(123)
                )
            ).setFormattedValue(
                Optional.of(
                    SpreadsheetText.with("Formatted-value-text")
                        .setColor(
                            Optional.of(
                                Color.parse("#123456")
                            )
                        ).textNode()
                )
            ),
            "A1,,Text,\"{\"\"type\"\": \"\"int\"\",\"\"value\"\": 123}\",,,,,,,,,,\"{\"\"type\"\": \"\"text-style-node\"\",\"\"value\"\": {\"\"styles\"\": {\"\"color\"\": \"\"#123456\"\"},\"\"children\"\": [{\"\"type\"\": \"\"text\"\",\"\"value\"\": \"\"Formatted-value-text\"\"}]}}\""
        );
    }

    @Test
    public void testTextWhenAllPropertiesSet() {
        this.textAndCheck(
            SpreadsheetSelection.A1.setFormula(
                    SpreadsheetFormula.EMPTY.setText("123")
                        .setValue(
                            Optional.of(123)
                        )
                ).setCurrency(OPTIONAL_CURRENCY)
                .setCurrencyExchangeRater(OPTIONAL_CURRENCY_EXCHANGE_RATER_SELECTOR)
                .setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
                .setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS)
                .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
                .setLocale(OPTIONAL_LOCALE)
                .setParser(OPTIONAL_PARSER_SELECTOR)
                .setStyle(TEXT_STYLE)
                .setValidator(
                    Optional.of(ValidatorSelector.parse("hello-validator"))
                ).setFormattedValue(
                    Optional.of(
                        SpreadsheetText.with("Formatted-value-text")
                            .setColor(
                                Optional.of(
                                    Color.parse("#123456")
                                )
                            ).textNode()
                    )
                ),
            "A1,123,,\"{\"\"type\"\": \"\"int\"\",\"\"value\"\": 123}\",AUD,test-currency-exchange-rater-111,\"\"\"am,pm\"\",\"\"January,February,March,April,May,June,July,August,September,October,November,December\"\",\"\"Jan.,Feb.,Mar.,Apr.,May,Jun.,Jul.,Aug.,Sep.,Oct.,Nov.,Dec.\"\",\"\"Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday\"\",\"\"Sun.,Mon.,Tue.,Wed.,Thu.,Fri.,Sat.\"\"\",\"-,+,0,$,.,e,\"\",\"\",∞,.,NaN,%,‰\",formatter-selector-111,\"{\"\"type\"\": \"\"locale\"\",\"\"value\"\": \"\"en-AU\"\"}\",parser-selector-111,color: black;,hello-validator,\"{\"\"type\"\": \"\"text-style-node\"\",\"\"value\"\": {\"\"styles\"\": {\"\"color\"\": \"\"#123456\"\"},\"\"children\"\": [{\"\"type\"\": \"\"text\"\",\"\"value\"\": \"\"Formatted-value-text\"\"}]}}\""
        );
    }

    // HasValidationPromptValue.........................................................................................

    @Test
    public void testValidationPromptValueWithErrorWithValidationChoiceList() {
        final ValidationChoiceList choices = ValidationChoiceList.EMPTY.concat(
            ValidationChoice.with(
                "Label1",
                Optional.of(
                    111
                )
            )
        );

        this.validationPromptValueAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1")
                    .setValue(
                        Optional.of(111)
                    ).setError(
                        Optional.of(
                            SpreadsheetErrorKind.ERROR.spreadsheetError()
                                .setValue(
                                    Optional.of(choices)
                                )
                        )
                    )
            ),
            choices
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseReferenceAndFormulaText() {
        this.textAndParseAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("123")
            )
        );
    }

    @Test
    public void testParseValueInteger() {
        this.textAndParseAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("123")
                    .setValue(
                        Optional.of(123)
                    )
            )
        );
    }

    @Test
    public void testParseValueExpressionNumber() {
        this.textAndParseAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("123")
                    .setValue(
                        Optional.of(
                            EXPRESSION_NUMBER_KIND.create(456.75)
                        )
                    )
            )
        );
    }

    @Test
    public void testParseAllProperties() {
        this.textAndParseAndCheck(
            SpreadsheetSelection.A1.setFormula(
                    SpreadsheetFormula.EMPTY.setText("123")
                        .setValue(
                            Optional.of(
                                LocalDate.of(1999, 12, 31)
                            )
                        ).setValueType(
                            OPTIONAL_VALUE_TYPE
                        )
                ).setCurrency(OPTIONAL_CURRENCY)
                .setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
                .setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS).setFormattedValue(
                    Optional.of(
                        SpreadsheetText.with("Formatted-value-text")
                            .setColor(
                                Optional.of(
                                    Color.parse("#123456")
                                )
                            ).textNode()
                    )
                ).setFormatter(OPTIONAL_FORMATTER_SELECTOR)
                .setParser(OPTIONAL_PARSER_SELECTOR)
                .setStyle(TEXT_STYLE)
                .setValidator(OPTIONAL_VALIDATOR_SELECTOR)
        );
    }

    private void textAndParseAndCheck(final SpreadsheetCell cell) {
        this.parseStringAndCheck(
            cell.text(),
            cell
        );
    }

    @Override
    public SpreadsheetCell parseString(final String text) {
        return SpreadsheetCell.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // equals ..........................................................................................................

    @Test
    public void testEqualsDifferentParser() {
        this.checkNotEquals(
            this.createObject()
                .setParser(
                    Optional.of(
                        SpreadsheetPattern.parseNumberParsePattern("\"different-pattern\"")
                            .spreadsheetParserSelector()
                    )
                )
        );
    }

    @Test
    public void testEqualsDifferentReference() {
        this.checkNotEquals(
            this.createObject(
                REFERENCE.add(1, 1),
                FORMULA
            )
        );
    }


    @Test
    public void testEqualsDifferentFormula() {
        this.checkNotEquals(
            this.createObject(
                REFERENCE,
                DIFFERENT_FORMULA
            )
        );
    }

    @Test
    public void testEqualsDifferentCurrency() {
        this.checkNotEquals(
            this.createObject()
                .setCurrency(OPTIONAL_DIFFERENT_CURRENCY)
        );
    }

    @Test
    public void testEqualsDifferentCurrencyExchangeRater() {
        this.checkNotEquals(
            this.createObject()
                .setCurrencyExchangeRater(OPTIONAL_DIFFERENT_CURRENCY_EXCHANGE_RATER_SELECTOR)
        );
    }

    @Test
    public void testEqualsDifferentDateTimeSymbols() {
        this.checkNotEquals(
            this.createObject()
                .setDateTimeSymbols(
                    OPTIONAL_DIFFERENT_DATE_TIME_SYMBOLS
                )
        );
    }

    @Test
    public void testEqualsDifferentDecimalNumberSymbols() {
        this.checkNotEquals(
            this.createObject()
                .setDecimalNumberSymbols(OPTIONAL_DIFFERENT_DECIMAL_NUMBER_SYMBOLS)
        );
    }

    @Test
    public void testEqualsDifferentLocale() {
        this.checkNotEquals(
            this.createObject()
                .setLocale(OPTIONAL_DIFFERENT_LOCALE)
        );
    }

    @Test
    public void testEqualsDifferentTextStyle() {
        this.checkNotEquals(
            this.createObject()
                .setStyle(DIFFERENT_TEXT_STYLE)
        );
    }

    @Test
    public void testEqualsDifferentValidator() {
        this.checkNotEquals(
            this.createObject()
                .setValidator(OPTIONAL_DIFFERENT_VALIDATOR_SELECTOR)
        );
    }

    @Test
    public void testEqualsDifferentFormatter() {
        this.checkNotEquals(
            this.createObject()
                .setFormatter(
                    Optional.of(
                        SpreadsheetPattern.parseTextFormatPattern("\"different-pattern\"")
                            .spreadsheetFormatterSelector()
                    )
                )
        );
    }

    @Test
    public void testEqualsDifferentFormatted() {
        this.checkNotEquals(
            this.createObject()
                .setFormattedValue(
                    Optional.of(
                        TextNode.text("different-formattedValue")
                    )
                )
        );
    }

    @Override
    public SpreadsheetCell createObject() {
        return this.createObject(
            REFERENCE,
            FORMULA
        );
    }

    private SpreadsheetCell createObject(final SpreadsheetCellReference reference,
                                         final SpreadsheetFormula formula) {
        return SpreadsheetCell.with(
                reference,
                formula
            ).setCurrency(OPTIONAL_CURRENCY)
            .setCurrencyExchangeRater(OPTIONAL_CURRENCY_EXCHANGE_RATER_SELECTOR)
            .setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
            .setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS)
            .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
            .setLocale(OPTIONAL_LOCALE)
            .setParser(OPTIONAL_PARSER_SELECTOR)
            .setStyle(TEXT_STYLE)
            .setValidator(OPTIONAL_VALIDATOR_SELECTOR)
            .setFormattedValue(FORMATTED_VALUE);
    }

    // json.............................................................................................................

    @Test
    public void testUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(12));
    }

    @Test
    public void testUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array());
    }

    @Test
    public void testUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"));
    }

    @Test
    public void testUnmarshallObjectEmptyFails() {
        this.unmarshallFails(JsonNode.object());
    }

    @Test
    public void testUnmarshallObjectReferenceMissingFails() {
        this.unmarshallFails(
            JsonNode.object()
                .set(
                    SpreadsheetCell.FORMULA_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(
                        FORMULA
                    )
                )
        );
    }

    @Test
    public void testUnmarshallObjectReferenceMissingFails2() {
        this.unmarshallFails(
            JsonNode.object()
                .set(
                    SpreadsheetCell.FORMULA_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(FORMULA))
                .set(
                    SpreadsheetCell.STYLE_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(TEXT_STYLE)
                )
        );
    }

    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyle() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    JsonPropertyName.with(REFERENCE.toString()),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.FORMULA_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(FORMULA)
                        ).set(
                            SpreadsheetCell.STYLE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(TEXT_STYLE)
                        )
                ),
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setStyle(TEXT_STYLE)
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormatter() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    JsonPropertyName.with(
                        REFERENCE.toString()
                    ),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.FORMULA_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(FORMULA)
                        ).set(
                            SpreadsheetCell.STYLE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(TEXT_STYLE)
                        ).set(
                            SpreadsheetCell.FORMATTER_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(FORMATTER_SELECTOR)
                        )
                ),
            SpreadsheetCell.with(
                    REFERENCE,
                    FORMULA
                ).setStyle(TEXT_STYLE)
                .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormattedCell() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    JsonPropertyName.with(
                        REFERENCE.toString()
                    ),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.FORMULA_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(FORMULA)
                        ).set(
                            SpreadsheetCell.STYLE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(TEXT_STYLE)
                        ).set(
                            SpreadsheetCell.FORMATTED_VALUE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshallWithType(
                                FORMATTED_VALUE
                                    .get()
                            )
                        )
                ),
            SpreadsheetCell.with(REFERENCE, FORMULA)
                .setStyle(TEXT_STYLE)
                .setFormattedValue(FORMATTED_VALUE));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndParsePattern() {
        final SpreadsheetFormula formula = FORMULA
            .setToken(
                Optional.of(
                    SpreadsheetFormulaParserToken.text(
                        Lists.of(
                            SpreadsheetFormulaParserToken.textLiteral("'A", "'A")
                        ),
                        "'A"
                    )
                )
            );

        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    JsonPropertyName.with(REFERENCE.toString()),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.FORMULA_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(formula)
                        ).set(
                            SpreadsheetCell.PARSER_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(PARSER_SELECTOR)
                        ).set(
                            SpreadsheetCell.FORMATTED_VALUE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshallWithType(
                                FORMATTED_VALUE
                                    .get()
                            )
                        )
                ),
            REFERENCE
                .setFormula(SpreadsheetFormula.EMPTY)
                .setParser(OPTIONAL_PARSER_SELECTOR)
                .setFormula(formula)
                .setFormattedValue(FORMATTED_VALUE)
        );
    }

    @Test
    public void testUnmarshallObjectCurrency() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(JsonPropertyName.with(REFERENCE.toString()),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.CURRENCY_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(CURRENCY)
                        )
                ),
            SpreadsheetCell.with(
                REFERENCE,
                SpreadsheetFormula.EMPTY
            ).setCurrency(OPTIONAL_CURRENCY)
        );
    }

    @Test
    public void testUnmarshallObjectDateTimeSymbols() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(JsonPropertyName.with(REFERENCE.toString()),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.DATE_TIME_SYMBOLS_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(DATE_TIME_SYMBOLS)
                        )
                ),
            SpreadsheetCell.with(
                REFERENCE,
                SpreadsheetFormula.EMPTY
            ).setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
        );
    }

    @Test
    public void testUnmarshallObjectDecimalNumberSymbols() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(JsonPropertyName.with(REFERENCE.toString()),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.DECIMAL_NUMBER_SYMBOLS_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(DECIMAL_NUMBER_SYMBOLS)
                        )
                ),
            SpreadsheetCell.with(
                REFERENCE,
                SpreadsheetFormula.EMPTY
            ).setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS)
        );
    }

    @Test
    public void testUnmarshallObjectLocale() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(JsonPropertyName.with(REFERENCE.toString()),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.LOCALE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(LOCALE)
                        )
                ),
            SpreadsheetCell.with(
                REFERENCE,
                SpreadsheetFormula.EMPTY
            ).setLocale(
                OPTIONAL_LOCALE
            )
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndFormatterAndFormattedCell() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(JsonPropertyName.with(REFERENCE.toString()),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.FORMULA_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(
                                FORMULA
                            )
                        ).set(
                            SpreadsheetCell.FORMATTER_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(FORMATTER_SELECTOR)
                        ).set(
                            SpreadsheetCell.FORMATTED_VALUE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshallWithType(
                                FORMATTED_VALUE
                                    .get()
                            )
                        )
                ),
            SpreadsheetCell.with(REFERENCE, FORMULA)
                .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
                .setFormattedValue(FORMATTED_VALUE));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testUnmarshallObjectReferenceAndFormulaAndTextStyleAndFormatterAndFormattedCell() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    JsonPropertyName.with(REFERENCE.toString()),
                    JsonNode.object()
                        .set(
                            SpreadsheetCell.FORMULA_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(
                                FORMULA
                            )
                        ).set(
                            SpreadsheetCell.STYLE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(TEXT_STYLE)
                        ).set(
                            SpreadsheetCell.FORMATTER_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshall(FORMATTER_SELECTOR)
                        ).set(
                            SpreadsheetCell.FORMATTED_VALUE_PROPERTY,
                            JSON_NODE_MARSHALL_CONTEXT.marshallWithType(
                                FORMATTED_VALUE.get()
                            )
                        )
                ),
            SpreadsheetCell.with(REFERENCE, FORMULA)
                .setStyle(TEXT_STYLE)
                .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
                .setFormattedValue(FORMATTED_VALUE));
    }

    // json.............................................................................................................

    @Test
    public void testMarshallAllProperties() {
        this.marshallAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setCurrency(
                OPTIONAL_CURRENCY
            ).setDateTimeSymbols(
                OPTIONAL_DATE_TIME_SYMBOLS
            ).setDecimalNumberSymbols(
                OPTIONAL_DECIMAL_NUMBER_SYMBOLS
            ).setFormatter(
                Optional.of(
                    SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN.spreadsheetFormatterSelector()
                )
            ).setLocale(
                OPTIONAL_LOCALE
            ).setParser(
                Optional.of(
                    SpreadsheetParserSelector.parse("parser-123")

                )
            ).setValidator(
                Optional.of(
                    ValidatorSelector.parse("validator-123")
                )
            ),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"currency\": \"AUD\",\n" +
                "    \"dateTimeSymbols\": {\n" +
                "      \"ampms\": [\n" +
                "        \"am\",\n" +
                "        \"pm\"\n" +
                "      ],\n" +
                "      \"monthNames\": [\n" +
                "        \"January\",\n" +
                "        \"February\",\n" +
                "        \"March\",\n" +
                "        \"April\",\n" +
                "        \"May\",\n" +
                "        \"June\",\n" +
                "        \"July\",\n" +
                "        \"August\",\n" +
                "        \"September\",\n" +
                "        \"October\",\n" +
                "        \"November\",\n" +
                "        \"December\"\n" +
                "      ],\n" +
                "      \"monthNameAbbreviations\": [\n" +
                "        \"Jan.\",\n" +
                "        \"Feb.\",\n" +
                "        \"Mar.\",\n" +
                "        \"Apr.\",\n" +
                "        \"May\",\n" +
                "        \"Jun.\",\n" +
                "        \"Jul.\",\n" +
                "        \"Aug.\",\n" +
                "        \"Sep.\",\n" +
                "        \"Oct.\",\n" +
                "        \"Nov.\",\n" +
                "        \"Dec.\"\n" +
                "      ],\n" +
                "      \"weekDayNames\": [\n" +
                "        \"Sunday\",\n" +
                "        \"Monday\",\n" +
                "        \"Tuesday\",\n" +
                "        \"Wednesday\",\n" +
                "        \"Thursday\",\n" +
                "        \"Friday\",\n" +
                "        \"Saturday\"\n" +
                "      ],\n" +
                "      \"weekDayNameAbbreviations\": [\n" +
                "        \"Sun.\",\n" +
                "        \"Mon.\",\n" +
                "        \"Tue.\",\n" +
                "        \"Wed.\",\n" +
                "        \"Thu.\",\n" +
                "        \"Fri.\",\n" +
                "        \"Sat.\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"decimalNumberSymbols\": {\n" +
                "      \"negativeSign\": \"-\",\n" +
                "      \"positiveSign\": \"+\",\n" +
                "      \"zeroDigit\": \"0\",\n" +
                "      \"currencySymbol\": \"$\",\n" +
                "      \"decimalSeparator\": \".\",\n" +
                "      \"exponentSymbol\": \"e\",\n" +
                "      \"groupSeparator\": \",\",\n" +
                "      \"infinitySymbol\": \"∞\",\n" +
                "      \"monetaryDecimalSeparator\": \".\",\n" +
                "      \"nanSymbol\": \"NaN\",\n" +
                "      \"percentSymbol\": \"%\",\n" +
                "      \"permillSymbol\": \"‰\"\n" +
                "    },\n" +
                "    \"formatter\": \"text @\",\n" +
                "    \"locale\": \"en-AU\",\n" +
                "    \"parser\": \"parser-123\",\n" +
                "    \"validator\": \"validator-123\"\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithFormula() {
        this.marshallAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ),
            "{\"A1\": {\"formula\": {\"text\": \"=1+2\"}}}");
    }

    @Test
    public void testMarshallWithCurrency() {
        this.marshallAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setCurrency(
                OPTIONAL_CURRENCY
            ),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"currency\": \"AUD\"\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithCurrencyExchangeRater() {
        this.marshallAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setCurrencyExchangeRater(OPTIONAL_DIFFERENT_CURRENCY_EXCHANGE_RATER_SELECTOR),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"currencyExchangeRater\": \"test-different-currency-exchange-rater-222\"\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithDateTimeSymbols() {
        this.marshallAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"dateTimeSymbols\": {\n" +
                "      \"ampms\": [\n" +
                "        \"am\",\n" +
                "        \"pm\"\n" +
                "      ],\n" +
                "      \"monthNames\": [\n" +
                "        \"January\",\n" +
                "        \"February\",\n" +
                "        \"March\",\n" +
                "        \"April\",\n" +
                "        \"May\",\n" +
                "        \"June\",\n" +
                "        \"July\",\n" +
                "        \"August\",\n" +
                "        \"September\",\n" +
                "        \"October\",\n" +
                "        \"November\",\n" +
                "        \"December\"\n" +
                "      ],\n" +
                "      \"monthNameAbbreviations\": [\n" +
                "        \"Jan.\",\n" +
                "        \"Feb.\",\n" +
                "        \"Mar.\",\n" +
                "        \"Apr.\",\n" +
                "        \"May\",\n" +
                "        \"Jun.\",\n" +
                "        \"Jul.\",\n" +
                "        \"Aug.\",\n" +
                "        \"Sep.\",\n" +
                "        \"Oct.\",\n" +
                "        \"Nov.\",\n" +
                "        \"Dec.\"\n" +
                "      ],\n" +
                "      \"weekDayNames\": [\n" +
                "        \"Sunday\",\n" +
                "        \"Monday\",\n" +
                "        \"Tuesday\",\n" +
                "        \"Wednesday\",\n" +
                "        \"Thursday\",\n" +
                "        \"Friday\",\n" +
                "        \"Saturday\"\n" +
                "      ],\n" +
                "      \"weekDayNameAbbreviations\": [\n" +
                "        \"Sun.\",\n" +
                "        \"Mon.\",\n" +
                "        \"Tue.\",\n" +
                "        \"Wed.\",\n" +
                "        \"Thu.\",\n" +
                "        \"Fri.\",\n" +
                "        \"Sat.\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithDecimalNumberSymbols() {
        this.marshallAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"decimalNumberSymbols\": {\n" +
                "      \"negativeSign\": \"-\",\n" +
                "      \"positiveSign\": \"+\",\n" +
                "      \"zeroDigit\": \"0\",\n" +
                "      \"currencySymbol\": \"$\",\n" +
                "      \"decimalSeparator\": \".\",\n" +
                "      \"exponentSymbol\": \"e\",\n" +
                "      \"groupSeparator\": \",\",\n" +
                "      \"infinitySymbol\": \"∞\",\n" +
                "      \"monetaryDecimalSeparator\": \".\",\n" +
                "      \"nanSymbol\": \"NaN\",\n" +
                "      \"percentSymbol\": \"%\",\n" +
                "      \"permillSymbol\": \"‰\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithStyle() {
        this.marshallAndCheck(
            SpreadsheetCell.with(
                    REFERENCE,
                    FORMULA
                )
                .setStyle(TEXT_STYLE),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"style\": {\n" +
                "      \"color\": \"black\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithFormattedValue() {
        this.marshallAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setFormattedValue(FORMATTED_VALUE),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"formattedValue\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"formattedValue-text\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallWithStyleAndFormattedValue() {
        this.marshallAndCheck(
            this.createCell()
                .setStyle(TEXT_STYLE)
                .setFormattedValue(FORMATTED_VALUE),
                "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    },\n" +
                "    \"currency\": \"AUD\",\n" +
                "    \"currencyExchangeRater\": \"test-currency-exchange-rater-111\",\n" +
                "    \"dateTimeSymbols\": {\n" +
                "      \"ampms\": [\n" +
                "        \"am\",\n" +
                "        \"pm\"\n" +
                "      ],\n" +
                "      \"monthNames\": [\n" +
                "        \"January\",\n" +
                "        \"February\",\n" +
                "        \"March\",\n" +
                "        \"April\",\n" +
                "        \"May\",\n" +
                "        \"June\",\n" +
                "        \"July\",\n" +
                "        \"August\",\n" +
                "        \"September\",\n" +
                "        \"October\",\n" +
                "        \"November\",\n" +
                "        \"December\"\n" +
                "      ],\n" +
                "      \"monthNameAbbreviations\": [\n" +
                "        \"Jan.\",\n" +
                "        \"Feb.\",\n" +
                "        \"Mar.\",\n" +
                "        \"Apr.\",\n" +
                "        \"May\",\n" +
                "        \"Jun.\",\n" +
                "        \"Jul.\",\n" +
                "        \"Aug.\",\n" +
                "        \"Sep.\",\n" +
                "        \"Oct.\",\n" +
                "        \"Nov.\",\n" +
                "        \"Dec.\"\n" +
                "      ],\n" +
                "      \"weekDayNames\": [\n" +
                "        \"Sunday\",\n" +
                "        \"Monday\",\n" +
                "        \"Tuesday\",\n" +
                "        \"Wednesday\",\n" +
                "        \"Thursday\",\n" +
                "        \"Friday\",\n" +
                "        \"Saturday\"\n" +
                "      ],\n" +
                "      \"weekDayNameAbbreviations\": [\n" +
                "        \"Sun.\",\n" +
                "        \"Mon.\",\n" +
                "        \"Tue.\",\n" +
                "        \"Wed.\",\n" +
                "        \"Thu.\",\n" +
                "        \"Fri.\",\n" +
                "        \"Sat.\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"decimalNumberSymbols\": {\n" +
                "      \"negativeSign\": \"-\",\n" +
                "      \"positiveSign\": \"+\",\n" +
                "      \"zeroDigit\": \"0\",\n" +
                "      \"currencySymbol\": \"$\",\n" +
                "      \"decimalSeparator\": \".\",\n" +
                "      \"exponentSymbol\": \"e\",\n" +
                "      \"groupSeparator\": \",\",\n" +
                "      \"infinitySymbol\": \"∞\",\n" +
                "      \"monetaryDecimalSeparator\": \".\",\n" +
                "      \"nanSymbol\": \"NaN\",\n" +
                "      \"percentSymbol\": \"%\",\n" +
                "      \"permillSymbol\": \"‰\"\n" +
                "    },\n" +
                "    \"formatter\": \"formatter-selector-111\",\n" +
                "    \"locale\": \"en-AU\",\n" +
                "    \"parser\": \"parser-selector-111\",\n" +
                "    \"style\": {\n" +
                "      \"color\": \"black\"\n" +
                "    },\n" +
                "    \"validator\": \"absolute-url\",\n" +
                "    \"formattedValue\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"formattedValue-text\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallFormulaRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    @Test
    public void testMarshallStyleRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(
            SpreadsheetSelection.parseCell("A99")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(
                    TextStyle.EMPTY.set(
                        TextStylePropertyName.BACKGROUND_COLOR,
                        Color.parse("#123456")
                    )
                )
        );
    }

    @Test
    public void testMarshallFormulaStyleFormatterAndFormattedRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(
            SpreadsheetSelection.parseCell("A99")
                .setFormula(SpreadsheetFormula.EMPTY.setText("=123.5"))
                .setStyle(TextStyle.EMPTY.set(TextStylePropertyName.BACKGROUND_COLOR, Color.parse("#123456")))
                .setFormatter(
                    Optional.of(
                        SpreadsheetPattern.parseNumberFormatPattern("##")
                            .spreadsheetFormatterSelector()
                    )
                ).setFormattedValue(
                    Optional.of(
                        TextNode.text("abc123")
                    )
                )
        );
    }

    @Test
    public void testUnmarshallWithStyle() {
        this.unmarshallAndCheck(
            "{\n" +
                "   \"A123\": {\n" +
                "      \"style\": {\n" +
                "          \"color\": \"black\"\n" +
                "      }\n" +
                "   }\n" +
                "}",
            SpreadsheetSelection.parseCell("A123")
                .setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(TEXT_STYLE)
        );
    }

    @Override
    public SpreadsheetCell createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetCell unmarshall(final JsonNode jsonNode,
                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetCell.unmarshall(jsonNode, context);
    }

    private void checkEquals(final JsonNode node,
                             final String expected) {
        this.checkEquals(
            JsonNode.parse(expected),
            JsonNode.object()
                .appendChild(node)
        );
    }

    // HateosResourceTesting............................................................................................

    @Test
    public void testHateosLinkIdAbsoluteReference() {
        this.hateosLinkIdAndCheck(
            this.createCell("$B$21"),
            "B21"
        );
    }

    @Test
    public void testHateosLinkIdRelativeReference() {
        this.hateosLinkIdAndCheck(
            this.createCell("C9"),
            "C9"
        );
    }

    @Override
    public SpreadsheetCell createHateosResource() {
        return this.createCell();
    }

    // patch............................................................................................................

    @Test
    public void testPatchEmptyObject() {
        this.patchAndCheck(
            this.createPatchable(),
            JsonNode.object()
        );
    }

    @Test
    public void testPatchFormulaTextSame() {
        this.patchAndCheck(
            SpreadsheetCell.with(
                SpreadsheetSelection.A1,
                FORMULA
            ),
            JsonNode.object()
                .set(
                    SpreadsheetCell.FORMULA_PROPERTY,
                    JsonObject.object()
                        .set(
                            JsonPropertyName.with("text"),
                            FORMULA.text()
                        )
                )
        );
    }

    @Test
    public void testPatchFormulaText() {
        final SpreadsheetCellReference cellReference = SpreadsheetSelection.A1;

        this.patchAndCheck(
            SpreadsheetCell.with(
                cellReference,
                FORMULA_EQ_1
            ),
            JsonNode.object()
                .set(
                    SpreadsheetCell.FORMULA_PROPERTY,
                    JsonObject.object()
                        .set(
                            JsonPropertyName.with("text"),
                            FORMULA.text()
                        )
                ),
            SpreadsheetCell.with(
                cellReference,
                FORMULA
            )
        );
    }

    @Test
    public void testPatchCurrency() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        );

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .set(
                    SpreadsheetCell.CURRENCY_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(CURRENCY)
                ),
            cell.setCurrency(OPTIONAL_CURRENCY)
        );
    }

    @Test
    public void testPatchCurrencyExchangeRater() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        );

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .set(
                    SpreadsheetCell.CURRENCY_EXCHANGE_RATER_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(
                        CURRENCY_EXCHANGE_RATER_SELECTOR
                    )
                ),
            cell.setCurrencyExchangeRater(OPTIONAL_CURRENCY_EXCHANGE_RATER_SELECTOR)
        );
    }

    @Test
    public void testPatchDateTimeSymbols() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        );

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .set(
                    SpreadsheetCell.DATE_TIME_SYMBOLS_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(
                        DATE_TIME_SYMBOLS
                    )
                ),
            cell.setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
        );
    }

    @Test
    public void testPatchLocale() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        );

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .set(
                    SpreadsheetCell.LOCALE_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(LOCALE)
                ),
            cell.setLocale(OPTIONAL_LOCALE)
        );
    }

    @Test
    public void testPatchFormatter() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        ).setFormatter(
            Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@")
                    .spreadsheetFormatterSelector()
            )
        );

        final SpreadsheetFormatterSelector formatter = SpreadsheetPattern.parseTextFormatPattern("@@@")
            .spreadsheetFormatterSelector();

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .set(
                    SpreadsheetCell.FORMATTER_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(formatter)
                ),
            cell.setFormatter(
                Optional.of(
                    formatter
                )
            )
        );
    }

    @Test
    public void testPatchFormatterRemove() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        ).setFormatter(
            Optional.of(
                SpreadsheetPattern.parseTextFormatPattern("@")
                    .spreadsheetFormatterSelector()
            )
        );

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .setNull(
                    SpreadsheetCell.FORMATTER_PROPERTY
                ),
            cell.setFormatter(
                SpreadsheetCell.NO_FORMATTER
            )
        );
    }

    @Test
    public void testPatchStyle() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        );

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .set(
                    SpreadsheetCell.STYLE_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(TEXT_STYLE)
                ),
            cell.setStyle(TEXT_STYLE)
        );
    }

    @Test
    public void testPatchStyle2() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        );

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .set(
                    SpreadsheetCell.STYLE_PROPERTY,
                    JSON_NODE_MARSHALL_CONTEXT.marshall(DIFFERENT_TEXT_STYLE)
                ),
            cell.setStyle(DIFFERENT_TEXT_STYLE)
        );
    }

    @Test
    public void testPatchStyleAddProperty() {
        final SpreadsheetCell cell = SpreadsheetCell.with(
            SpreadsheetSelection.A1,
            FORMULA_EQ_1
        ).setStyle(TEXT_STYLE);

        final TextStylePropertyName<Color> color = TextStylePropertyName.COLOR;
        final Color colorValue = Color.WHITE;

        this.patchAndCheck(
            cell,
            JsonNode.object()
                .set(SpreadsheetCell.STYLE_PROPERTY, JsonObject.object()
                    .set(
                        JsonPropertyName.with(color.value()),
                        JSON_NODE_MARSHALL_CONTEXT.marshall(colorValue)
                    )
                ),
            cell.setStyle(
                TEXT_STYLE.set(
                    color,
                    colorValue
                )
            )
        );
    }

    @Test
    public void testPatchCellReferenceFails() {
        final JsonPropertyName name = SpreadsheetCell.REFERENCE_PROPERTY;
        final String value = "A1";

        this.patchInvalidPropertyFails(
            this.createPatchable(),
            JsonNode.object()
                .set(
                    name,
                    value
                ),
            name,
            JsonNode.string(value)
        );
    }

    @Test
    public void testPatchFormattedFails() {
        final JsonPropertyName name = SpreadsheetCell.FORMATTED_VALUE_PROPERTY;
        final String value = "@";

        this.patchInvalidPropertyFails(
            this.createPatchable(),
            JsonNode.object()
                .set(
                    name,
                    value
                ),
            name,
            JsonNode.string(value)
        );
    }

    // PatchableTesting.................................................................................................

    @Override
    public SpreadsheetCell createPatchable() {
        return this.createObject();
    }

    @Override
    public JsonNode createPatch() {
        return JsonNode.object();
    }

    @Override
    public JsonNodeUnmarshallContext createPatchContext() {
        return JSON_NODE_UNMARSHALL_CONTEXT;
    }

    // XXXPatch.........................................................................................................

    @Test
    public void testFormulaPatchNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .formulaPatch(null)
        );
    }

    @Test
    public void testFormulaPatch() {
        final Optional<SpreadsheetFormatterSelector> formatter = Optional.of(
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy")
                .spreadsheetFormatterSelector()
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(FORMULA)
            .setFormatter(formatter);

        final JsonNode patch = cell.formulaPatch(
            JSON_NODE_MARSHALL_CONTEXT
        );

        this.checkEquals(
            patch,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"text\": \"=1+2\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );

        this.patchAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormatter(formatter),
            patch,
            cell
        );
    }

    @Test
    public void testFormatterPatchNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .formatterPatch(null)
        );
    }

    @Test
    public void testFormatterPatchNotEmpty() {
        final Optional<SpreadsheetFormatterSelector> formatter = Optional.of(
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy")
                .spreadsheetFormatterSelector()
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
            .setFormatter(formatter);

        final JsonNode patch = cell.formatterPatch(
            JSON_NODE_MARSHALL_CONTEXT
        );
        this.checkEquals(
            patch,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formatter\": \"date dd/mm/yyyy\"\n" +
                "  }\n" +
                "}"
        );

        this.patchAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormatter(formatter),
            patch,
            cell
        );
    }

    @Test
    public void testFormatterPatchEmpty() {
        final Optional<SpreadsheetFormatterSelector> formatter = SpreadsheetCell.NO_FORMATTER;
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
            .setFormatter(formatter);

        final JsonNode patch = cell.formatterPatch(JSON_NODE_MARSHALL_CONTEXT);
        this.checkEquals(
            patch,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formatter\": null\n" +
                "  }\n" +
                "}"
        );

        this.patchAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormatter(formatter),
            patch,
            cell
        );
    }

    @Test
    public void testParserPatchNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .parserPatch(null)
        );
    }

    @Test
    public void testParserPatchNotEmpty() {
        final Optional<SpreadsheetParserSelector> parser = Optional.of(
            SpreadsheetPattern.parseDateParsePattern("yyyy/mm/dd")
                .spreadsheetParserSelector()
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
            .setParser(parser);

        final JsonNode patch = cell.parserPatch(JSON_NODE_MARSHALL_CONTEXT);
        this.checkEquals(
            patch,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"parser\": \"date yyyy/mm/dd\"\n" +
                "  }\n" +
                "}"
        );

        this.patchAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setParser(parser),
            patch,
            cell
        );
    }

    @Test
    public void testParserPatchEmpty() {
        final Optional<SpreadsheetParserSelector> parser = SpreadsheetCell.NO_PARSER;
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
            .setParser(parser);

        final JsonNode patch = cell.parserPatch(JSON_NODE_MARSHALL_CONTEXT);
        this.checkEquals(
            patch,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"parser\": null\n" +
                "  }\n" +
                "}"
        );

        this.patchAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setParser(parser),
            patch,
            cell
        );
    }

    @Test
    public void testStylePatchNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .stylePatch(null)
        );
    }

    @Test
    public void testStylePatch() {
        final TextStyle style = TextStyle.EMPTY.set(
            TextStylePropertyName.TEXT_ALIGN,
            TextAlign.CENTER
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            FORMULA
        ).setStyle(
            TextStyle.EMPTY.set(
                TextStylePropertyName.TEXT_ALIGN,
                TextAlign.CENTER
            )
        );

        final JsonNode patch = cell.stylePatch(JSON_NODE_MARSHALL_CONTEXT);
        this.checkEquals(
            patch,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"style\": {\n" +
                "      \"textAlign\": \"CENTER\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );

        this.patchAndCheck(
            cell.setStyle(
                TextStyle.EMPTY.set(
                    TextStylePropertyName.TEXT_ALIGN,
                    TextAlign.CENTER
                )
            ),
            patch,
            cell.setStyle(style)
        );
    }

    // validatorPatch...................................................................................................

    @Test
    public void testValidatorPatchNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .validatorPatch(null)
        );
    }

    @Test
    public void testValidatorPatchNotEmpty() {
        final Optional<ValidatorSelector> validator = Optional.of(
            ValidatorSelector.parse("hello-validator")
        );
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
            .setValidator(validator);

        final JsonNode patch = cell.validatorPatch(JSON_NODE_MARSHALL_CONTEXT);
        this.checkEquals(
            patch,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"validator\": \"hello-validator\"\n" +
                "  }\n" +
                "}"
        );

        this.patchAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setValidator(validator),
            patch,
            cell
        );
    }

    @Test
    public void testValidatorPatchEmpty() {
        final Optional<ValidatorSelector> validator = SpreadsheetCell.NO_VALIDATOR;
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
            .setValidator(validator);

        final JsonNode patch = cell.validatorPatch(JSON_NODE_MARSHALL_CONTEXT);

        this.checkEquals(
            patch,
            "{\n" +
                "  \"A1\": {\n" +
                "    \"validator\": null\n" +
                "  }\n" +
                "}"
        );

        this.patchAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setValidator(validator),
            patch,
            cell
        );
    }

    // treePrintable....................................................................................................

    @Test
    public void testTreePrintFormula() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA
            ),
            "Cell A1\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=1+2\"\n"
        );
    }

    @Test
    public void testTreePrintFormulaToken() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA.setToken(TOKEN)

            ),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n"
        );
    }

    @Test
    public void testTreePrintFormulaTokenExpression() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA.setToken(TOKEN)
                    .setExpression(EXPRESSION)

            ),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n"
        );
    }

    @Test
    public void testTreePrintFormulaTokenExpressionValue() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA.setToken(TOKEN)
                    .setExpression(EXPRESSION)
                    .setValue(VALUE)

            ),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "    value:\n" +
                "      3\n"
        );
    }

    @Test
    public void testTreePrintFormulaTokenExpressionError() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA.setToken(TOKEN)
                    .setToken(TOKEN)
                    .setExpression(EXPRESSION)
                    .setValue(
                        Optional.of(
                            SpreadsheetErrorKind.VALUE.setMessage("error message 1")
                        )
                    )

            ),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "    value:\n" +
                "      #VALUE!\n" +
                "        \"error message 1\"\n"
        );
    }

    @Test
    public void testTreePrintFormulaTokenExpressionValueStyle() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA.setToken(TOKEN)
                    .setToken(TOKEN)
                    .setExpression(EXPRESSION)
                    .setValue(VALUE)
            ).setStyle(TEXT_STYLE),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "    value:\n" +
                "      3\n" +
                "  style:\n" +
                "    TextStyle\n" +
                "      color=black\n"
        );
    }

    @Test
    public void testTreePrintFormulaCurrency() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA.setToken(TOKEN)
            ).setCurrency(OPTIONAL_CURRENCY),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "  currency:\n" +
                "    AUD (java.util.Currency)\n"
        );
    }

    @Test
    public void testTreePrintFormulaCurrencyExchangeRater() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA
            ).setCurrency(OPTIONAL_CURRENCY),
            "Cell A1\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=1+2\"\n" +
                "  currency:\n" +
                "    AUD (java.util.Currency)\n"
        );
    }

    @Test
    public void testTreePrintFormulaDateTimeSymbols() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA
            ).setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS),
            "Cell A1\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=1+2\"\n" +
                "  dateTimeSymbols:\n" +
                "    DateTimeSymbols\n" +
                "      ampms\n" +
                "        am\n" +
                "        pm\n" +
                "      monthNames\n" +
                "        January\n" +
                "        February\n" +
                "        March\n" +
                "        April\n" +
                "        May\n" +
                "        June\n" +
                "        July\n" +
                "        August\n" +
                "        September\n" +
                "        October\n" +
                "        November\n" +
                "        December\n" +
                "      monthNameAbbreviations\n" +
                "        Jan.\n" +
                "        Feb.\n" +
                "        Mar.\n" +
                "        Apr.\n" +
                "        May\n" +
                "        Jun.\n" +
                "        Jul.\n" +
                "        Aug.\n" +
                "        Sep.\n" +
                "        Oct.\n" +
                "        Nov.\n" +
                "        Dec.\n" +
                "      weekDayNames\n" +
                "        Sunday\n" +
                "        Monday\n" +
                "        Tuesday\n" +
                "        Wednesday\n" +
                "        Thursday\n" +
                "        Friday\n" +
                "        Saturday\n" +
                "      weekDayNameAbbreviations\n" +
                "        Sun.\n" +
                "        Mon.\n" +
                "        Tue.\n" +
                "        Wed.\n" +
                "        Thu.\n" +
                "        Fri.\n" +
                "        Sat.\n"
        );
    }


    @Test
    public void testTreePrintFormulaDecimalNumberSymbols() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA
            ).setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS),
            "Cell A1\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=1+2\"\n" +
                "  decimalNumberSymbols:\n" +
                "    DecimalNumberSymbols\n" +
                "      negativeSign\n" +
                "        '-'\n" +
                "      positiveSign\n" +
                "        '+'\n" +
                "      zeroDigit\n" +
                "        '0'\n" +
                "      currencySymbol\n" +
                "        \"$\"\n" +
                "      decimalSeparator\n" +
                "        '.'\n" +
                "      exponentSymbol\n" +
                "        \"e\"\n" +
                "      groupSeparator\n" +
                "        ','\n" +
                "      infinitySymbol\n" +
                "        \"∞\"\n" +
                "      monetaryDecimalSeparator\n" +
                "        '.'\n" +
                "      nanSymbol\n" +
                "        \"NaN\"\n" +
                "      percentSymbol\n" +
                "        '%'\n" +
                "      permillSymbol\n" +
                "        '‰'\n"
        );
    }

    @Test
    public void testTreePrintFormulaLocale() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA
            ).setLocale(
                OPTIONAL_LOCALE
            ),
            "Cell A1\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=1+2\"\n" +
                "  locale:\n" +
                "    en_AU (java.util.Locale)\n"
        );
    }

    @Test
    public void testTreePrintFormulaTokenExpressionValueStyleParser() {
        this.treePrintAndCheck(
            ABSOLUTE_REFERENCE
                .setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(TEXT_STYLE)
                .setParser(OPTIONAL_PARSER_SELECTOR)
                .setFormula(
                    this.FORMULA
                        .setToken(this.TOKEN)
                        .setExpression(EXPRESSION)
                        .setValue(VALUE)
                ),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "    value:\n" +
                "      3\n" +
                "  parser:\n" +
                "    parser-selector-111\n" +
                "  style:\n" +
                "    TextStyle\n" +
                "      color=black\n"
        );
    }

    @Test
    public void testTreePrintFormulaTokenExpressionValueStyleParserFormatter() {
        this.treePrintAndCheck(
            ABSOLUTE_REFERENCE
                .setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(TEXT_STYLE)
                .setParser(OPTIONAL_PARSER_SELECTOR)
                .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
                .setFormula(
                    FORMULA.setToken(this.TOKEN)
                        .setExpression(EXPRESSION)
                        .setValue(VALUE)
                ),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "    value:\n" +
                "      3\n" +
                "  formatter:\n" +
                "    formatter-selector-111\n" +
                "  parser:\n" +
                "    parser-selector-111\n" +
                "  style:\n" +
                "    TextStyle\n" +
                "      color=black\n"
        );
    }

    @Test
    public void testTreePrintFormulaTokenExpressionValueStyleFormatter() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                    ABSOLUTE_REFERENCE,
                    FORMULA.setToken(TOKEN)
                        .setExpression(EXPRESSION)
                        .setValue(VALUE)
                ).setStyle(TEXT_STYLE)
                .setFormatter(OPTIONAL_FORMATTER_SELECTOR),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "    value:\n" +
                "      3\n" +
                "  formatter:\n" +
                "    formatter-selector-111\n" +
                "  style:\n" +
                "    TextStyle\n" +
                "      color=black\n"
        );
    }

    @Test
    public void testTreePrintFormulaTokenExpressionValueStyleFormatterFormatted() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                    ABSOLUTE_REFERENCE,
                    FORMULA.setToken(TOKEN)
                        .setExpression(EXPRESSION)
                        .setValue(VALUE)
                ).setStyle(TEXT_STYLE)
                .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
                .setFormattedValue(FORMATTED_VALUE),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "    value:\n" +
                "      3\n" +
                "  formatter:\n" +
                "    formatter-selector-111\n" +
                "  style:\n" +
                "    TextStyle\n" +
                "      color=black\n" +
                "  formattedValue:\n" +
                "    Text \"formattedValue-text\"\n"
        );
    }

    @Test
    public void testTreePrintFormulaValidator() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                ABSOLUTE_REFERENCE,
                FORMULA.setToken(TOKEN)
                    .setExpression(EXPRESSION)
                    .setValue(VALUE)
            ).setValidator(OPTIONAL_VALIDATOR_SELECTOR),
            "Cell A1\n" +
                "  Formula\n" +
                "    token:\n" +
                "      ExpressionSpreadsheetFormula \"=1+2\"\n" +
                "        EqualsSymbolSpreadsheetFormula \"=\" \"=\"\n" +
                "        AdditionSpreadsheetFormula \"1+2\"\n" +
                "          NumberSpreadsheetFormula \"1\"\n" +
                "            DigitsSpreadsheetFormula \"1\" \"1\"\n" +
                "          NumberSpreadsheetFormula \"2\"\n" +
                "            DigitsSpreadsheetFormula \"2\" \"2\"\n" +
                "    expression:\n" +
                "      AddExpression\n" +
                "        ValueExpression 1 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "        ValueExpression 2 (walkingkooka.tree.expression.ExpressionNumberBigDecimal)\n" +
                "    value:\n" +
                "      3\n" +
                "  validator:\n" +
                "    absolute-url\n"
        );
    }

    @Test
    public void testTreePrintAllProperties() {
        this.treePrintAndCheck(
            SpreadsheetCell.with(
                    REFERENCE,
                    FORMULA
                ).setCurrency(OPTIONAL_CURRENCY)
                .setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS)
                .setDecimalNumberSymbols(OPTIONAL_DECIMAL_NUMBER_SYMBOLS)
                .setFormatter(OPTIONAL_FORMATTER_SELECTOR)
                .setLocale(OPTIONAL_LOCALE)
                .setParser(OPTIONAL_PARSER_SELECTOR)
                .setStyle(TEXT_STYLE)
                .setValidator(OPTIONAL_VALIDATOR_SELECTOR),
            "Cell A1\n" +
                "  Formula\n" +
                "    text:\n" +
                "      \"=1+2\"\n" +
                "  currency:\n" +
                "    AUD (java.util.Currency)\n" +
                "  dateTimeSymbols:\n" +
                "    DateTimeSymbols\n" +
                "      ampms\n" +
                "        am\n" +
                "        pm\n" +
                "      monthNames\n" +
                "        January\n" +
                "        February\n" +
                "        March\n" +
                "        April\n" +
                "        May\n" +
                "        June\n" +
                "        July\n" +
                "        August\n" +
                "        September\n" +
                "        October\n" +
                "        November\n" +
                "        December\n" +
                "      monthNameAbbreviations\n" +
                "        Jan.\n" +
                "        Feb.\n" +
                "        Mar.\n" +
                "        Apr.\n" +
                "        May\n" +
                "        Jun.\n" +
                "        Jul.\n" +
                "        Aug.\n" +
                "        Sep.\n" +
                "        Oct.\n" +
                "        Nov.\n" +
                "        Dec.\n" +
                "      weekDayNames\n" +
                "        Sunday\n" +
                "        Monday\n" +
                "        Tuesday\n" +
                "        Wednesday\n" +
                "        Thursday\n" +
                "        Friday\n" +
                "        Saturday\n" +
                "      weekDayNameAbbreviations\n" +
                "        Sun.\n" +
                "        Mon.\n" +
                "        Tue.\n" +
                "        Wed.\n" +
                "        Thu.\n" +
                "        Fri.\n" +
                "        Sat.\n" +
                "  decimalNumberSymbols:\n" +
                "    DecimalNumberSymbols\n" +
                "      negativeSign\n" +
                "        '-'\n" +
                "      positiveSign\n" +
                "        '+'\n" +
                "      zeroDigit\n" +
                "        '0'\n" +
                "      currencySymbol\n" +
                "        \"$\"\n" +
                "      decimalSeparator\n" +
                "        '.'\n" +
                "      exponentSymbol\n" +
                "        \"e\"\n" +
                "      groupSeparator\n" +
                "        ','\n" +
                "      infinitySymbol\n" +
                "        \"∞\"\n" +
                "      monetaryDecimalSeparator\n" +
                "        '.'\n" +
                "      nanSymbol\n" +
                "        \"NaN\"\n" +
                "      percentSymbol\n" +
                "        '%'\n" +
                "      permillSymbol\n" +
                "        '‰'\n" +
                "  formatter:\n" +
                "    formatter-selector-111\n" +
                "  locale:\n" +
                "    en_AU (java.util.Locale)\n" +
                "  parser:\n" +
                "    parser-selector-111\n" +
                "  style:\n" +
                "    TextStyle\n" +
                "      color=black\n" +
                "  validator:\n" +
                "    absolute-url\n"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringEmptyFormula() {
        this.toStringAndCheck(
            REFERENCE.setFormula(SpreadsheetFormula.EMPTY),
            REFERENCE.toString()
        );
    }

    @Test
    public void testToStringWithCurrency() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setCurrency(OPTIONAL_CURRENCY),
            "A1 \"=1+2\" currency=\"AUD\""
        );
    }

    @Test
    public void testToStringWithCurrencyExchangeRater() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setCurrencyExchangeRater(OPTIONAL_DIFFERENT_CURRENCY_EXCHANGE_RATER_SELECTOR),
            "A1 \"=1+2\" currencyExchangeRater=\"test-different-currency-exchange-rater-222\""
        );
    }

    @Test
    public void testToStringWithDateTimeSymbols() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setDateTimeSymbols(OPTIONAL_DATE_TIME_SYMBOLS),
            "A1 \"=1+2\" dateTimeSymbols=\"ampms=\"am\", \"pm\" monthNames=\"January\", \"February\", \"March\", \"April\", \"May\", \"June\", \"July\", \"August\", \"September\", \"October\", \"November\", \"December\" monthNameAbbreviations=\"Jan.\", \"Feb.\", \"Mar.\", \"Apr.\", \"May\", \"Jun.\", \"Jul.\", \"Aug.\", \"Sep.\", \"Oct.\", \"Nov.\", \"Dec.\" weekDayNames=\"Sunday\", \"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\" weekDayNameAbbreviations=\"Sun.\", \"Mon.\", \"Tue.\", \"Wed.\", \"Thu.\", \"Fri.\", \"Sat.\"\""
        );
    }

    @Test
    public void testToStringFormula() {
        this.toStringAndCheck(
            REFERENCE.setFormula(FORMULA),
            "A1 \"=1+2\""
        );
    }

    @Test
    public void testToStringWithLocale() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setLocale(OPTIONAL_LOCALE),
            "A1 \"=1+2\" locale=\"en_AU\""
        );
    }

    @Test
    public void testToStringWithFormatter() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setFormatter(OPTIONAL_FORMATTER_SELECTOR),
            "A1 \"=1+2\" formatter=\"formatter-selector-111\""
        );
    }

    @Test
    public void testToStringWithParser() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setParser(OPTIONAL_PARSER_SELECTOR),
            "A1 \"=1+2\" parser=\"parser-selector-111\""
        );
    }

    @Test
    public void testToStringWithTextStyle() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setStyle(TEXT_STYLE),
            "A1 \"=1+2\" style={color=black}"
        );
    }

    @Test
    public void testToStringWithValidator() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                REFERENCE,
                FORMULA
            ).setValidator(OPTIONAL_VALIDATOR_SELECTOR),
            "A1 \"=1+2\" validator=\"absolute-url\""
        );
    }

    @Test
    public void testToStringWithFormatterLocaleParserTextStyleValidator() {
        this.toStringAndCheck(
            SpreadsheetCell.with(
                    REFERENCE,
                    FORMULA
                ).setFormatter(OPTIONAL_FORMATTER_SELECTOR)
                .setLocale(OPTIONAL_DIFFERENT_LOCALE)
                .setParser(OPTIONAL_PARSER_SELECTOR)
                .setStyle(TEXT_STYLE)
                .setValidator(OPTIONAL_VALIDATOR_SELECTOR),
            "A1 \"=1+2\" formatter=\"formatter-selector-111\" locale=\"en_NZ\" parser=\"parser-selector-111\" style={color=black} validator=\"absolute-url\""
        );
    }

    // HasSpreadsheetReference..........................................................................................

    @Test
    public void testReference() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("AB123");

        this.referenceAndCheck(
            cell.setFormula(SpreadsheetFormula.EMPTY),
            cell
        );
    }

    // HasTextNode......................................................................................................

    @Test
    public void testHasTextNode() {
        final TextNode textNode = TextNode.text("Hello World 123");

        this.textNodeAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormattedValue(
                    Optional.of(textNode)
                ),
            textNode
        );
    }

    @Test
    public void testHasTextNodeWhenMissing() {
        this.textNodeAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            null
        );
    }

    // HasTextStyle.....................................................................................................

    @Test
    public void testHasTextStyle() {
        final TextStyle textStyle = TextStyle.EMPTY.set(
            TextStylePropertyName.TEXT_ALIGN,
            TextAlign.LEFT
        );

        this.textStyleAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setStyle(textStyle),
            textStyle
        );
    }

    // HasOptionalValueType.............................................................................................

    @Test
    public void testValueType() {
        this.valueTypeAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setValueType(OPTIONAL_VALUE_TYPE)
            ),
            VALUE_TYPE
        );
    }

    // CanBeEmpty.......................................................................................................

    @Test
    public void testCanBeEmptyEmpty() {
        this.isEmptyAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            true
        );
    }

    @Test
    public void testCanBeEmptyNotEmpty() {
        this.isEmptyAndCheck(
            SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1")
            ),
            false
        );
    }

    // helpers..........................................................................................................

    private SpreadsheetCell createCell() {
        return this.createObject();
    }

    private SpreadsheetCell createCell(final String reference) {
        return SpreadsheetCell.with(
            SpreadsheetSelection.parseCell(reference),
            FORMULA
        );
    }

    private SpreadsheetFormula parseFormula(final String text) {
        return SpreadsheetFormula.parse(
            TextCursors.charSequence(text),
            SpreadsheetFormulaParsers.valueOrExpression(
                Parsers.never()
            ),
            SPREADSHEET_PARSER_CONTEXT
        );
    }

    // HasContentType...................................................................................................

    @Test
    public void testContentType() {
        this.contentTypeAndCheck(
            this.createObject(),
            "application/json+walkingkooka.spreadsheet.value.SpreadsheetCell"
        );
    }

    // HasSpreadsheetCell...............................................................................................

    @Test
    public void testCell() {
        final SpreadsheetCell spreadsheetCell = this.createCell();

        this.cellAndCheck(
            spreadsheetCell,
            spreadsheetCell
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCell> type() {
        return SpreadsheetCell.class;
    }
}
