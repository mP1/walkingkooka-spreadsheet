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
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.Expression;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreTest extends SpreadsheetCellStoreTestCase<SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore> {

    private final static char DECIMAL_SEPARATOR = ',';
    private final static char PERCENT = '^';

    // with.............................................................................................................

    @Test
    public void testWithNullCellStoreFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        null,
                        METADATA,
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
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
                        PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetParserProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        this.cellStore(),
                        METADATA,
                        null,
                        PROVIDER_CONTEXT
                )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        this.cellStore(),
                        METADATA,
                        SPREADSHEET_PARSER_PROVIDER,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetCellStore cellStore = this.cellStore();

        this.check(
                SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        cellStore,
                        METADATA,
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                ),
                cellStore,
                METADATA,
                SPREADSHEET_PARSER_PROVIDER
        );
    }

    @Test
    public void testWithSpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreAndSameMetadata() {
        final SpreadsheetCellStore cellStore = this.cellStore();

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore wrapped = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                cellStore,
                METADATA,
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
        );

        assertSame(
                wrapped,
                SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        wrapped,
                        METADATA,
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                ));
    }

    @Test
    public void testWithSpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStoreDifferentMetadata() {
        final SpreadsheetCellStore cellStore = this.cellStore();

        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore wrapped = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                cellStore,
                METADATA,
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
        );

        final SpreadsheetMetadata differentMetadata = METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(99999L)
        );

        this.check(
                SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        wrapped,
                        differentMetadata,
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                ),
                cellStore,
                differentMetadata,
                SPREADSHEET_PARSER_PROVIDER
        );
    }

    private void check(final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore store,
                       final SpreadsheetCellStore cellStore,
                       final SpreadsheetMetadata metadata,
                       final SpreadsheetParserProvider spreadsheetParserProvider) {
        assertSame(cellStore, store.store, "cellStore");
        assertSame(metadata, store.metadata, "metadata");
        assertSame(spreadsheetParserProvider, store.spreadsheetParserProvider, "spreadsheetParserProvider");
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

        final SpreadsheetCell returned = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                        cellStore,
                        METADATA,
                        SPREADSHEET_PARSER_PROVIDER,
                        PROVIDER_CONTEXT
                ).save(cell);
        this.checkEquals(saved,
                this.saved,
                () -> "saved " + cell + " metadata=" + METADATA
        );
        this.checkEquals(loaded,
                returned,
                () -> "returned saved " + cell + " metadata=" + METADATA
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.LOCALE,
                        Locale.forLanguageTag("ES")
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.LOCALE,
                        Locale.forLanguageTag("ES")
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.LOCALE,
                        Locale.forLanguageTag("ES")
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.LOCALE,
                        Locale.forLanguageTag("ES")
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
                        decimalSeparator2
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL,
                        percent2
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
                        decimalSeparator2
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR,
                        decimalSeparator2
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
                METADATA.set(
                        SpreadsheetMetadataPropertyName.LOCALE,
                        Locale.forLanguageTag("ES")
                ),
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
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
        final SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore store = SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                cellStore,
                METADATA,
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
        );

        this.toStringAndCheck(
                store,
                METADATA + " " + cellStore + " " + SPREADSHEET_PARSER_PROVIDER + " " + PROVIDER_CONTEXT
        );
    }

    @Override
    public SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore createStore() {
        return SpreadsheetFormulaSpreadsheetMetadataAwareSpreadsheetCellStore.with(
                this.cellStore(),
                METADATA,
                SPREADSHEET_PARSER_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    private SpreadsheetCellStore cellStore() {
        return SpreadsheetCellStores.treeMap();
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
