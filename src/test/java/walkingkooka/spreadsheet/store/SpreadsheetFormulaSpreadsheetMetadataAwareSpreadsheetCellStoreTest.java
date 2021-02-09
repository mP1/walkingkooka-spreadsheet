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

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.tree.expression.Expression;

import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore> {

    private final String CURRENCY_SYMBOL = "$AUD";
    private final char DECIMAL_SEPARATOR = ',';
    private final static int DEFAULT_YEAR = 1800;
    private final String EXPONENT_SYMBOL = "e";
    private final char GROUPING_SEPARATOR = '.';
    private final char NEGATIVE_SIGN = '-';
    private final char PERCENT = '^';
    private final char POSITIVE_SIGN = '+';
    private final static int TWO_DIGIT_YEAR = 25;
    private final char VALUE_SEPARATOR = ';';

    // with.............................................................................................................

    @Test
    public void testWithNullCellStoreFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(null, this.metadata()));
    }

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(this.cellStore(), null));
    }

    // save.............................................................................................................

    @Test
    public void testSaveFormulaWithExpressionTextUnchanged() {
        final String text = "1";

        final SpreadsheetFormula formula = SpreadsheetFormula.with(text)
                .setToken(
                        Optional.of(SpreadsheetParserToken.number(
                                Lists.of(SpreadsheetParserToken.digits(text, text)),
                                text
                        ))
                )
                .setExpression(
                        Optional.of(
                                number(1)
                        )
                );

        final SpreadsheetCell cell = SpreadsheetCell.with(
                SpreadsheetCellReference.parseCellReference("B2"),
                formula
        );

        this.saveAndCheck(
                cell,
                cell,
                cell
        );
    }

    @Test
    public void testSaveFormulaWithoutToken() {
        final String text = "2";

        final SpreadsheetFormula formula = SpreadsheetFormula.with(text);

        final SpreadsheetCell requires = SpreadsheetCell.with(
                SpreadsheetCellReference.parseCellReference("B2"),
                formula
        );

        final Optional<Expression> expression = Optional.of(
                number(2)
        );

        final SpreadsheetCell with = requires.setFormula(
                formula.setToken(
                        Optional.of(
                                SpreadsheetParserToken.number(
                                        Lists.of(SpreadsheetParserToken.digits(text, text)),
                                        text
                                )
                        )
                )
                        .setExpression(expression)
        );

        this.saveAndCheck(
                requires,
                with,
                with
        );
    }

    private void saveAndCheck(final SpreadsheetCell cell,
                              final SpreadsheetCell saved,
                              final SpreadsheetCell loaded) {
        this.saved = null;
        final SpreadsheetCellStore cellStore = new FakeSpreadsheetCellStore() {
            @Override
            public SpreadsheetCell save(final SpreadsheetCell cell) {
                SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreTest.this.saved = cell;
                return cell;
            }
        };

        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetCell returned = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(cellStore, metadata)
                .save(cell);
        assertEquals(saved,
                this.saved,
                () -> "saved " + cell + " metadata=" + metadata
        );
        assertEquals(loaded,
                returned,
                () -> "returned saved " + cell + " metadata=" + metadata
        );
    }

    private SpreadsheetCell saved;

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredDayName() {
        final String text = "Tuesday 9/2/2021";

        final SpreadsheetFormula formula = SpreadsheetFormula.with(text)
                .setToken(
                        Optional.of(
                                SpreadsheetParserToken.date(
                                        Lists.of(
                                                SpreadsheetParserToken.dayName(2, "Tuesday"),
                                                SpreadsheetParserToken.whitespace(" ", " "),
                                                SpreadsheetParserToken.dayNumber(9, "9"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.monthNumber(2, "2"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.year(2021, "2021")
                                        ),
                                        text
                                )
                        )
                );

        final SpreadsheetCell requires = this.cell(formula);

        final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        store.save(requires);

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore loader = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                store,
                this.metadata()
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES"))
        );

        final String text2 = "martes 9/2/2021";
        assertEquals(
                requires.setFormula(
                        formula.setText(text2)
                        .setToken(
                                Optional.of(
                                        SpreadsheetParserToken.date(
                                                Lists.of(
                                                        SpreadsheetParserToken.dayName(2, "martes"),
                                                        SpreadsheetParserToken.whitespace(" ", " "),
                                                        SpreadsheetParserToken.dayNumber(9, "9"),
                                                        SpreadsheetParserToken.textLiteral("/", "/"),
                                                        SpreadsheetParserToken.monthNumber(2, "2"),
                                                        SpreadsheetParserToken.textLiteral("/", "/"),
                                                        SpreadsheetParserToken.year(2021, "2021")
                                                ),
                                                text2
                                        )
                                )
                        ).setExpression(
                                Optional.of(
                                       Expression.localDate(
                                               LocalDate.of(2021, 2, 9)
                                       )
                                )
                        )
                ),
                loader.loadOrFail(requires.reference()),
                () -> "didnt rewrite formula"
        );
    }

    private SpreadsheetCell cell(SpreadsheetFormula formula) {
        return SpreadsheetCell.with(
                SpreadsheetCellReference.parseCellReference("C3"),
                formula
        );
    }

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredDayNameAbbreviation() {
        final String text = "Tuesday 9/2/2021";

        final SpreadsheetFormula formula = SpreadsheetFormula.with(text)
                .setToken(
                        Optional.of(
                                SpreadsheetParserToken.date(
                                        Lists.of(
                                                SpreadsheetParserToken.dayNameAbbreviation(2, "Tuesday"),
                                                SpreadsheetParserToken.whitespace(" ", " "),
                                                SpreadsheetParserToken.dayNumber(9, "9"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.monthNumber(2, "2"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.year(2021, "2021")
                                        ),
                                        text
                                )
                        )
                );

        final SpreadsheetCell requires = this.cell(formula);

        final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        store.save(requires);

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore loader = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                store,
                this.metadata()
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES"))
        );

        final String text2 = "mar. 9/2/2021";
        assertEquals(
                requires.setFormula(
                        formula.setText(text2)
                                .setToken(
                                        Optional.of(
                                                SpreadsheetParserToken.date(
                                                        Lists.of(
                                                                SpreadsheetParserToken.dayNameAbbreviation(2, "mar."),
                                                                SpreadsheetParserToken.whitespace(" ", " "),
                                                                SpreadsheetParserToken.dayNumber(9, "9"),
                                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                                SpreadsheetParserToken.monthNumber(2, "2"),
                                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                                SpreadsheetParserToken.year(2021, "2021")
                                                        ),
                                                        text2
                                                )
                                        )
                                ).setExpression(
                                        Optional.of(
                                            Expression.localDate(
                                                LocalDate.of(2021, 2, 9)
                                            )
                                    )
                        )
                ),
                loader.loadOrFail(requires.reference()),
                () -> "didnt rewrite formula"
        );
    }

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredMonthName() {
        final String text = "9/February/2021";

        final SpreadsheetFormula formula = SpreadsheetFormula.with(text)
                .setToken(
                        Optional.of(
                                SpreadsheetParserToken.date(
                                        Lists.of(
                                                SpreadsheetParserToken.dayNumber(9, "9"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.monthName(2, "February"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.year(2021, "2021")
                                        ),
                                        text
                                )
                        )
                );

        final SpreadsheetCell requires = this.cell(formula);

        final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        store.save(requires);

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore loader = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                store,
                this.metadata()
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES"))
        );

        final String text2 = "9/marzo/2021";
        assertEquals(
                requires.setFormula(
                        formula.setText(text2)
                                .setToken(
                                        Optional.of(
                                                SpreadsheetParserToken.date(
                                                        Lists.of(
                                                                SpreadsheetParserToken.dayNumber(9, "9"),
                                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                                SpreadsheetParserToken.monthName(2, "marzo"),
                                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                                SpreadsheetParserToken.year(2021, "2021")
                                                        ),
                                                        text2
                                                )
                                        )
                                ).setExpression(
                                Optional.of(
                                        Expression.localDate(
                                                LocalDate.of(2021, 2, 9)
                                        )
                                )
                        )
                ),
                loader.loadOrFail(requires.reference()),
                () -> "didnt rewrite formula"
        );
    }

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredMonthNameAbbreviation() {
        final String text = "9/Feb/2021";

        final SpreadsheetFormula formula = SpreadsheetFormula.with(text)
                .setToken(
                        Optional.of(
                                SpreadsheetParserToken.date(
                                        Lists.of(
                                                SpreadsheetParserToken.dayNumber(9, "9"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.monthNameAbbreviation(2, "Feb"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.year(2021, "2021")
                                        ),
                                        text
                                )
                        )
                );

        final SpreadsheetCell requires = this.cell(formula);

        final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        store.save(requires);

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore loader = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                store,
                this.metadata()
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES"))
        );

        final String text2 = "9/mar./2021";
        assertEquals(
                requires.setFormula(
                        formula.setText(text2)
                                .setToken(
                                        Optional.of(
                                                SpreadsheetParserToken.date(
                                                        Lists.of(
                                                                SpreadsheetParserToken.dayNumber(9, "9"),
                                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                                SpreadsheetParserToken.monthNameAbbreviation(2, "mar."),
                                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                                SpreadsheetParserToken.year(2021, "2021")
                                                        ),
                                                        text2
                                                )
                                        )
                                ).setExpression(
                                Optional.of(
                                        Expression.localDate(
                                                LocalDate.of(2021, 2, 9)
                                        )
                                )
                        )
                ),
                loader.loadOrFail(requires.reference()),
                () -> "didnt rewrite formula"
        );
    }

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredNumber() {
        final String text = "3" + DECIMAL_SEPARATOR + "5";
        final SpreadsheetFormula formula = SpreadsheetFormula.with(text)
                .setToken(
                        Optional.of(
                                this.numberParserToken(DECIMAL_SEPARATOR)
                        )
                );

        final SpreadsheetCell requires = this.cell(formula);

        final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        store.save(requires);

        final char decimalSeparator2 = ';';
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore loader = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                store,
                this.metadata()
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, decimalSeparator2)
        );

        assertEquals(
                requires.setFormula(
                        formula.setText(text.replace(DECIMAL_SEPARATOR, decimalSeparator2))
                        .setToken(
                                Optional.of(
                                        this.numberParserToken(decimalSeparator2)
                                )
                        ).setExpression(
                                Optional.of(
                                        number(3.5)
                                )
                        )
                ),
                loader.loadOrFail(requires.reference()),
                () -> "didnt rewrite formula"
        );
    }

    @Test
    public void testSaveFormulaWithTokenAndExpressionTextUpdateRequired() {
        final String text = "3" + DECIMAL_SEPARATOR + "5";
        final SpreadsheetFormula formula = SpreadsheetFormula.with(text)
                .setToken(
                        Optional.of(
                                this.numberParserToken(DECIMAL_SEPARATOR)
                        )
                ).setExpression(
                        Optional.of(
                                number(3.5)
                        )
                );

        final SpreadsheetCell requires = this.cell(formula);

        final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        store.save(requires);

        final char decimalSeparator2 = ';';
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore loader = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                store,
                this.metadata()
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, decimalSeparator2)
        );

        assertEquals(
                requires.setFormula(
                        formula.setText(text.replace(DECIMAL_SEPARATOR, decimalSeparator2))
                                .setToken(
                                Optional.of(
                                        this.numberParserToken(decimalSeparator2)
                                )
                        ).setExpression(
                                Optional.of(
                                        number(3.5)
                                )
                        )
                ),
                loader.loadOrFail(requires.reference()),
                () -> "didnt rewrite formula"
        );
    }

    private SpreadsheetParserToken numberParserToken(final char decimalSeparator) {
        return SpreadsheetParserToken.number(
                Lists.of(
                        SpreadsheetParserToken.digits("3", "3"),
                        SpreadsheetParserToken.decimalSeparatorSymbol("" + decimalSeparator, "" + decimalSeparator),
                        SpreadsheetParserToken.digits("5", "5")
                ),
                "3" + decimalSeparator + "5"
        );
    }

    private Expression number(final Number number) {
        return Expression.expressionNumber(EXPRESSION_NUMBER_KIND.create(number));
    }

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredAmpm() {
        final String text = "9:59 PM";

        final SpreadsheetFormula formula = SpreadsheetFormula.with(text)
                .setToken(
                        Optional.of(
                                SpreadsheetParserToken.time(
                                        Lists.of(
                                                SpreadsheetParserToken.hour(9, "9"),
                                                SpreadsheetParserToken.textLiteral(":", ":"),
                                                SpreadsheetParserToken.minute(59, "59"),
                                                SpreadsheetParserToken.textLiteral(" ", " "),
                                                SpreadsheetParserToken.amPm(12, "PM")
                                        ),
                                        text
                                )
                        )
                );

        final SpreadsheetCell requires = this.cell(formula);

        final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        store.save(requires);

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore loader = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                store,
                this.metadata()
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES"))
        );

        final String text2 = "9:59 p. m.";
        assertEquals(
                requires.setFormula(
                        formula.setText(text2)
                                .setToken(
                                        Optional.of(
                                                SpreadsheetParserToken.time(
                                                        Lists.of(
                                                                SpreadsheetParserToken.hour(9, "9"),
                                                                SpreadsheetParserToken.textLiteral(":", ":"),
                                                                SpreadsheetParserToken.minute(59, "59"),
                                                                SpreadsheetParserToken.textLiteral(" ", " "),
                                                                SpreadsheetParserToken.amPm(12, "p. m.")
                                                        ),
                                                        text2
                                                )
                                        )
                                ).setExpression(
                                Optional.of(
                                        Expression.localTime(
                                                LocalTime.of(9 +12, 59)
                                        )
                                )
                        )
                ),
                loader.loadOrFail(requires.reference()),
                () -> "didnt rewrite formula"
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.fake();
        final SpreadsheetMetadata metadata = this.metadata();
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore store = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(cellStore, metadata);

        this.toStringAndCheck(store, metadata + " " + cellStore);
    }

    @Override
    public SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore createStore() {
        return SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(this.cellStore(), this.metadata());
    }

    private SpreadsheetCellStore cellStore() {
        return SpreadsheetCellStores.treeMap();
    }

    private SpreadsheetMetadata metadata() {
        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.DATE_PARSE_PATTERNS, SpreadsheetPattern.parseDateParsePatterns("d/m/y"))
                .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetPattern.parseDateTimeParsePatterns("d/m/y h:mm"))
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, GROUPING_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSE_PATTERNS, SpreadsheetPattern.parseNumberParsePatterns("#.#"))
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PRECISION, MathContext.DECIMAL32.getPrecision())
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR)
                .set(SpreadsheetMetadataPropertyName.TIME_PARSE_PATTERNS, SpreadsheetPattern.parseTimeParsePatterns("hh:mm"))
                .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, TWO_DIGIT_YEAR)
                .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR);
    }

    @Override
    public Class<SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore> type() {
        return SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetFormula.class.getSimpleName();
    }
}
