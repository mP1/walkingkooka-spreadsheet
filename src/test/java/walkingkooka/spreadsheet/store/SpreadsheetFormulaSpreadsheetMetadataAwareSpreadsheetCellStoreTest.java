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
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.format.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;

import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore> {

    private final static String CURRENCY_SYMBOL = "$AUD";
    private final static char DECIMAL_SEPARATOR = ',';
    private final static int DEFAULT_YEAR = 1800;
    private final static String EXPONENT_SYMBOL = "e";
    private final static char GROUP_SEPARATOR = '.';
    private final static char NEGATIVE_SIGN = '-';
    private final static char PERCENT = '^';
    private final static char POSITIVE_SIGN = '+';
    private final static int TWO_DIGIT_YEAR = 25;
    private final static char VALUE_SEPARATOR = ';';

    private final static SpreadsheetParserProvider SPREADSHEET_PARSER_PROVIDER = SpreadsheetParserProviders.spreadsheetParsePattern();
    private final static Supplier<LocalDateTime> NOW = LocalDateTime::now;

    // with.............................................................................................................

    @Test
    public void testWithNullCellStoreFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        null,
                        this.metadata(),
                        SPREADSHEET_PARSER_PROVIDER,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        this.cellStore(),
                        null,
                        SPREADSHEET_PARSER_PROVIDER,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetParserProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        this.cellStore(),
                        this.metadata(),
                        null,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullNowFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        this.cellStore(),
                        this.metadata(),
                        SPREADSHEET_PARSER_PROVIDER,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetMetadata metadata = this.metadata();

        this.check(
                SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        cellStore,
                        metadata,
                        SPREADSHEET_PARSER_PROVIDER,
                        NOW
                ),
                cellStore,
                metadata,
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );
    }

    @Test
    public void testWithSpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreAndSameMetadata() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetMetadata metadata = this.metadata();

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore wrapped = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                cellStore,
                metadata,
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        assertSame(
                wrapped,
                SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        wrapped,
                        metadata,
                        SPREADSHEET_PARSER_PROVIDER,
                        NOW
                ));
    }

    @Test
    public void testWithSpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreDifferentMetadata() {
        final SpreadsheetCellStore cellStore = this.cellStore();
        final SpreadsheetMetadata metadata = this.metadata();

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore wrapped = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                cellStore,
                metadata,
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        final SpreadsheetMetadata differentMetadata = metadata.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(99999L)
        );

        this.check(
                SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        wrapped,
                        differentMetadata,
                        SPREADSHEET_PARSER_PROVIDER,
                        NOW
                ),
                cellStore,
                differentMetadata,
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );
    }

    private void check(final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore store,
                       final SpreadsheetCellStore cellStore,
                       final SpreadsheetMetadata metadata,
                       final SpreadsheetParserProvider spreadsheetParserProvider,
                       final Supplier<LocalDateTime> now) {
        assertSame(cellStore, store.store, "cellStore");
        assertSame(metadata, store.metadata, "metadata");
        assertSame(spreadsheetParserProvider, store.spreadsheetParserProvider, "spreadsheetParserProvider");
        assertSame(now, store.now, "now");
    }

    // save.............................................................................................................

    @Test
    public void testSaveFormulaWithExpressionTextUnchanged() {
        final String text = "1";

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
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

        final SpreadsheetCell cell = SpreadsheetSelection.parseCell("B2")
                .setFormula(
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

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text);

        final SpreadsheetCell requires = SpreadsheetSelection.parseCell("B2")
                .setFormula(
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

    @Test
    public void testSaveFormulaWithInvalidDate() {
        final String text = "99:12:00";

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text);

        final SpreadsheetCell requires = SpreadsheetSelection.parseCell("B2")
                .setFormula(formula);

        final SpreadsheetCell with = requires.setFormula(
                formula.setValue(
                        Optional.of(
                                SpreadsheetErrorKind.ERROR.setMessage(
                                        "Invalid character '9' at (1,1) \"99:12:00\" expected APOSTROPHE_STRING | EQUALS_EXPRESSION | VALUE"
                                )
                        )
                )
        );

        this.saveAndCheck(
                requires,
                with,
                with
        );
    }

    @Test
    public void testSaveFormulaWithInvalidDate2() {
        final String text = "99/12/00";

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text);

        final SpreadsheetCell requires = SpreadsheetSelection.parseCell("B2")
                .setFormula(formula);

        final SpreadsheetCell with = requires.setFormula(
                formula.setToken(
                        Optional.of(
                                SpreadsheetParserToken.date(
                                        Lists.of(
                                                SpreadsheetParserToken.dayNumber(99, "99"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.monthNumber(12, "12"),
                                                SpreadsheetParserToken.textLiteral("/", "/"),
                                                SpreadsheetParserToken.year(0, "00")
                                        ),
                                        text
                                )
                        )
                ).setValue(
                        Optional.of(
                                SpreadsheetErrorKind.VALUE.setMessage("Invalid value for DayOfMonth (valid values 1 - 28/31): 99")
                        )
                )
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
        final SpreadsheetCell returned = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        cellStore,
                        metadata,
                        SPREADSHEET_PARSER_PROVIDER,
                        NOW
                )
                .save(cell);
        this.checkEquals(saved,
                this.saved,
                () -> "saved " + cell + " metadata=" + metadata
        );
        this.checkEquals(loaded,
                returned,
                () -> "returned saved " + cell + " metadata=" + metadata
        );
    }

    private SpreadsheetCell saved;

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredDayName() {
        final String text = "Tuesday 9/2/2021";

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
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
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES")),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        final String text2 = "martes 9/2/2021";
        this.checkEquals(
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
                                        Expression.value(
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
        return SpreadsheetSelection.parseCell("C3")
                .setFormula(formula);
    }

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredDayNameAbbreviation() {
        final String text = "Tuesday 9/2/2021";

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
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
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES")),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        final String text2 = "mar. 9/2/2021";
        this.checkEquals(
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
                                        Expression.value(
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

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
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
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES")),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        final String text2 = "9/marzo/2021";
        this.checkEquals(
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
                                        Expression.value(
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

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
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
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES")),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        final String text2 = "9/mar./2021";
        this.checkEquals(
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
                                        Expression.value(
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
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
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
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, decimalSeparator2),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        this.checkEquals(
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
    public void testSaveFormulaWithTokenTextUpdateRequiredPercet() {
        final String text = "=150" + PERCENT;
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
                .setToken(
                        Optional.of(
                                this.expressionNumberWithPercentParserToken(PERCENT)
                        )
                );

        final SpreadsheetCell requires = this.cell(formula);

        final SpreadsheetCellStore store = SpreadsheetCellStores.treeMap();
        store.save(requires);

        final char percent2 = ';';
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore loader = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                store,
                this.metadata()
                        .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, percent2),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        final String text2 = text.replace(PERCENT, percent2);
        this.checkEquals(
                requires.setFormula(
                        formula.setText(text2)
                                .setToken(
                                        Optional.of(
                                                this.expressionNumberWithPercentParserToken(percent2)
                                        )
                                ).setExpression(
                                        Optional.of(
                                                number(1.5)
                                        )
                        )
                ),
                loader.loadOrFail(requires.reference()),
                () -> "didnt rewrite formula"
        );
    }

    private SpreadsheetParserToken expressionNumberWithPercentParserToken(final char percentSymbol) {
        return SpreadsheetParserToken.expression(
                Lists.of(
                        SpreadsheetParserToken.equalsSymbol("=", "="),
                        SpreadsheetParserToken.number(
                                Lists.of(
                                        SpreadsheetParserToken.digits("150", "150"),
                                        SpreadsheetParserToken.percentSymbol("" + percentSymbol, "" + percentSymbol)
                                ),
                                "150" + percentSymbol
                        )
                ),
                "=150" + percentSymbol
        );
    }

    @Test
    public void testSaveFormulaWithTokenAndExpressionTextUpdateRequired() {
        final String text = "3" + DECIMAL_SEPARATOR + "5";
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
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
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, decimalSeparator2),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        this.checkEquals(
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
    public void testSaveFormulaWithTokenAndExpressionTextUpdateRequired2() {
        final String text = "=3" + DECIMAL_SEPARATOR + "5";
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
                .setToken(
                        Optional.of(
                                SpreadsheetParserToken.expression(
                                        Lists.of(
                                                SpreadsheetParserToken.equalsSymbol("=", "="),
                                                this.numberParserToken(DECIMAL_SEPARATOR)
                                        ),
                                        text
                                )
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
                        .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, decimalSeparator2),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        final String text2 = text.replace(DECIMAL_SEPARATOR, decimalSeparator2);
        this.checkEquals(
                requires.setFormula(
                        formula.setText(text2)
                                .setToken(
                                        Optional.of(
                                                SpreadsheetParserToken.expression(
                                                        Lists.of(
                                                                SpreadsheetParserToken.equalsSymbol("=", "="),
                                                                this.numberParserToken(decimalSeparator2)
                                                        ),
                                                        text2
                                                )
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
        return Expression.value(
                EXPRESSION_NUMBER_KIND.create(number)
        );
    }

    @Test
    public void testSaveFormulaWithTokenTextUpdateRequiredAmpm() {
        final String text = "9:59 PM";

        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY
                .setText(text)
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
                        .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("ES")),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        final String text2 = "9:59 p. m.";
        this.checkEquals(
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
                                        Expression.value(
                                                LocalTime.of(9 + 12, 59)
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
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore store = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                cellStore,
                metadata,
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );

        this.toStringAndCheck(
                store,
                metadata + " " + cellStore + " " + SPREADSHEET_PARSER_PROVIDER
        );
    }

    @Override
    public SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore createStore() {
        return SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                this.cellStore(),
                this.metadata(),
                SPREADSHEET_PARSER_PROVIDER,
                NOW
        );
    }

    private SpreadsheetCellStore cellStore() {
        return SpreadsheetCellStores.treeMap();
    }

    private SpreadsheetMetadata metadata() {
        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.DATE_PARSER, SpreadsheetPattern.parseDateParsePattern("d/m/y").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("d/m/y h:mm").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, DEFAULT_YEAR)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT_SYMBOL)
                .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, NEGATIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.NUMBER_PARSER, SpreadsheetPattern.parseNumberParsePattern("#;#.#").spreadsheetParserSelector())
                .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, POSITIVE_SIGN)
                .set(SpreadsheetMetadataPropertyName.PRECISION, MathContext.DECIMAL32.getPrecision())
                .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR)
                .set(SpreadsheetMetadataPropertyName.TIME_PARSER, SpreadsheetPattern.parseTimeParsePattern("hh:mm").spreadsheetParserSelector())
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
