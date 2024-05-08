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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparatorTesting2;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverter;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.compare.SpreadsheetColumnOrRowSpreadsheetComparators;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorContexts;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellRangeComparatorTest implements ComparatorTesting2<SpreadsheetCellRangeComparator, List<SpreadsheetCell>>,
        TreePrintableTesting {

    @Test
    public void testWithNullComparatorsFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellRangeComparator.with(
                        null, // spreadsheetComparators
                        SpreadsheetComparatorContexts.fake()
                )
        );
    }

    @Test
    public void testWithEmptyComparatorsFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetCellRangeComparator.with(
                        Lists.empty(), // spreadsheetComparators
                        SpreadsheetComparatorContexts.fake()
                )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellRangeComparator.with(
                        SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                                "A=text",
                                SpreadsheetComparatorProviders.builtIn()
                        ), // spreadsheetComparators
                        null // context
                )
        );
    }

    // Comparator.......................................................................................................

    @Test
    public void testCompareSingleColumn() {
        final SpreadsheetCell a1 = this.cell(
                "A1",
                LocalDate.of(1999, 12, 1)
        );
        final SpreadsheetCell a2 = this.cell(
                "A2",
                LocalDate.of(1999, 12, 2)
        );
        final SpreadsheetCell a3 = this.cell(
                "A3",
                LocalDate.of(1999, 12, 3)
        );
        this.comparatorArraySortAndCheck(
                list(a1),
                list(a2),
                list(a3),
                list(a1), // expected
                list(a2),
                list(a3)
        );
    }

    @Test
    public void testCompareSingleColumnSomeEqual() {
        final SpreadsheetCell a1 = this.cell(
                "A1",
                LocalDate.of(1999, 12, 1)
        );
        final SpreadsheetCell a2 = this.cell(
                "A2",
                LocalDate.of(1999, 12, 2)
        );
        final SpreadsheetCell a3 = this.cell(
                "A3",
                LocalDate.of(1999, 12, 1)
        );
        this.comparatorArraySortAndCheck(
                list(a1),
                list(a2),
                list(a3),
                list(a1), // expected
                list(a3),
                list(a2)
        );
    }

    @Test
    public void testCompareSingleColumnSomeMissing() {
        final SpreadsheetCell a1 = this.cell(
                "A1",
                LocalDate.of(1999, 12, 1)
        );
        final SpreadsheetCell a2 = this.cell(
                "A2",
                LocalDate.of(1999, 12, 2)
        );
        final SpreadsheetCell a3 = this.cell(
                "A3",
                LocalDate.of(1999, 12, 1)
        );
        final SpreadsheetCell a4 = null;

        this.comparatorArraySortAndCheck(
                list(a1),
                list(a2),
                list(a3),
                list(),
                list(a1), // expected
                list(a3),
                list(a2),
                list()
        );
    }

    @Test
    public void testCompareMultipleColumn() {
        final SpreadsheetCell a1 = this.cell(
                "A1",
                LocalDate.of(1999, 12, 1)
        );
        final SpreadsheetCell a2 = this.cell(
                "A2",
                LocalDate.of(1999, 12, 1)
        );

        final SpreadsheetCell b1 = this.cell(
                "b1",
                "Second"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "First"
        );

        this.comparatorArraySortAndCheck(
                list(a1, b1),
                list(a2, b2),
                list(a2, b2), // expected
                list(a1, b1)
        );
    }

    @Test
    public void testCompareMultipleColumnSomeMissing() {
        final SpreadsheetCell a1 = this.cell(
                "A1",
                LocalDate.of(1999, 12, 1)
        );
        final SpreadsheetCell a2 = this.cell(
                "A2",
                LocalDate.of(1999, 12, 1)
        );

        final SpreadsheetCell b1 = this.cell(
                "b1",
                "First"
        );
        final SpreadsheetCell b2 = null;

        this.comparatorArraySortAndCheck(
                list(a1, b1),
                list(a2, b2),
                list(a1, b1), // expected
                list(a2, b2) // null is after
        );
    }

    @Test
    public void testCompareMultipleColumn2() {
        final SpreadsheetCell a1 = this.cell(
                "A1",
                LocalDate.of(1999, 12, 1)
        );
        final SpreadsheetCell a2 = this.cell(
                "A2",
                LocalDate.of(1999, 12, 1)
        );
        final SpreadsheetCell a3 = this.cell(
                "A3",
                LocalDate.of(1999, 12, 3)
        );
        final SpreadsheetCell a4 = this.cell(
                "A4",
                LocalDate.of(1999, 12, 3)
        );
        final SpreadsheetCell a5 = this.cell(
                "A5",
                LocalDate.of(1999, 12, 5)
        );

        final SpreadsheetCell b1 = this.cell(
                "b1",
                "Second"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "First"
        );
        final SpreadsheetCell b3 = this.cell(
                "b3",
                "Third"
        );
        final SpreadsheetCell b4 = this.cell(
                "b4",
                "TT Fourth"
        );
        final SpreadsheetCell b5 = this.cell(
                "b5",
                "Fifth"
        );

        this.comparatorArraySortAndCheck(
                list(a1, b1),
                list(a2, b2),
                list(a3, b3),
                list(a4, b4),
                list(a5, b5),
                list(a2, b2), // expected
                list(a1, b1),
                list(a3, b3),
                list(a4, b4),
                list(a5, b5)
        );
    }

    @Test
    public void testCompareMoreColumnsThanComparators() {
        final SpreadsheetCell a1 = this.cell(
                "A1",
                "same"
        );
        final SpreadsheetCell a2 = this.cell(
                "A2",
                "same"
        );

        final SpreadsheetCell b1 = this.cell(
                "b1",
                "same"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "same"
        );
        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "A=text"
                ),
                list(a1, b1),
                list(a2, b2),
                list(a1, b1), // expected
                list(a2, b2)
        );
    }

    @Test
    public void testCompareLessColumnsThanComparators() {
        final SpreadsheetCell a1 = this.cell(
                "A1",
                "same"
        );
        final SpreadsheetCell a2 = this.cell(
                "A2",
                "same"
        );

        final SpreadsheetCell b1 = this.cell(
                "b1",
                "same"
        );
        final SpreadsheetCell b2 = this.cell(
                "b2",
                "same"
        );
        this.comparatorArraySortAndCheck(
                this.createComparator(
                        "A=text;B=text;C=text"
                ),
                list(a1, b1),
                list(a2, b2),
                list(a1, b1), // expected
                list(a2, b2)
        );
    }

    private SpreadsheetCell cell(final String reference,
                                 final Object value) {
        return SpreadsheetSelection.parseCell(reference)
                .setFormula(
                        SpreadsheetFormula.EMPTY.setValue(
                                Optional.ofNullable(value)
                        )
                );
    }

    private static List<SpreadsheetCell> list(final SpreadsheetCell... cells) {
        return Lists.of(cells);
    }

    // Object...........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetComparatorContext context = SpreadsheetComparatorContexts.fake();

        this.toStringAndCheck(
                SpreadsheetCellRangeComparator.with(
                        SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                                "B=day-of-month",
                                SpreadsheetComparatorProviders.builtIn()
                        ),
                        context
                ),
                "B=day-of-month " + context
        );
    }

    @Test
    public void testToStringReversed() {
        final SpreadsheetComparatorContext context = SpreadsheetComparatorContexts.fake();

        this.toStringAndCheck(
                SpreadsheetCellRangeComparator.with(
                        SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                                "B=day-of-month DOWN",
                                SpreadsheetComparatorProviders.builtIn()
                        ),
                        context
                ),
                "B=day-of-month DOWN " + context
        );
    }

    @Test
    public void testToStringSeveralComparators() {
        final SpreadsheetComparatorContext context = SpreadsheetComparatorContexts.fake();

        this.toStringAndCheck(
                SpreadsheetCellRangeComparator.with(
                        SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                                "B=day-of-month,month-of-year,year",
                                SpreadsheetComparatorProviders.builtIn()
                        ),
                        context
                ),
                "B=day-of-month,month-of-year,year " + context
        );
    }

    @Test
    public void testToStringSeveralComparatorsSomeDown() {
        final SpreadsheetComparatorContext context = SpreadsheetComparatorContexts.fake();

        this.toStringAndCheck(
                SpreadsheetCellRangeComparator.with(
                        SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                                "B=day-of-month,month-of-year DOWN,year DOWN",
                                SpreadsheetComparatorProviders.builtIn()
                        ),
                        context
                ),
                "B=day-of-month,month-of-year DOWN,year DOWN " + context
        );
    }

    @Override
    public SpreadsheetCellRangeComparator createComparator() {
        return this.createComparator("B=day-of-month;C=text-case-insensitive;D=text-case-insensitive");
    }

    private SpreadsheetCellRangeComparator createComparator(final String comparators) {
        return this.createComparator(
                comparators,
                new FakeConverter<>() {

                    @Override
                    public <T> Either<T, String> convert(final Object value,
                                                         final Class<T> target,
                                                         final SpreadsheetConverterContext context) {
                        if (value instanceof String && LocalDate.class == target) {
                            try {
                                return this.successfulConversion(
                                        LocalDate.parse((String) value),
                                        target
                                );
                            } catch (final Exception ignore) {
                                // eventually becomes a failConversion
                            }
                        }
                        if (value instanceof LocalDate && LocalDate.class == target) {
                            return this.successfulConversion(
                                    value,
                                    target
                            );
                        }
                        if (value instanceof String && String.class == target) {
                            return this.successfulConversion(
                                    value,
                                    target
                            );
                        }
                        return this.failConversion(
                                value,
                                target
                        );
                    }

                    @Override
                    public String toString() {
                        return "TestConverter";
                    }
                }
        );
    }

    private SpreadsheetCellRangeComparator createComparator(final String comparators,
                                                            final Converter<SpreadsheetConverterContext> converter) {
        return SpreadsheetCellRangeComparator.with(
                SpreadsheetColumnOrRowSpreadsheetComparators.parse(
                        comparators,
                        SpreadsheetComparatorProviders.builtIn()
                ),
                SpreadsheetComparatorContexts.basic(
                        SpreadsheetConverterContexts.basic(
                                converter,
                                (label) -> {
                                    throw new UnsupportedOperationException();
                                },
                                ExpressionNumberConverterContexts.basic(
                                        Converters.fake(),
                                        ConverterContexts.basic(
                                                Converters.fake(),
                                                DateTimeContexts.locale(
                                                        Locale.forLanguageTag("EN-AU"),
                                                        1950, // defaultYear
                                                        50, // twoDigitYears
                                                        LocalDateTime::now
                                                ),
                                                DecimalNumberContexts.american(MathContext.DECIMAL32)
                                        ),
                                        ExpressionNumberKind.BIG_DECIMAL
                                )
                        )
                )
        );
    }

    @Override
    public Class<SpreadsheetCellRangeComparator> type() {
        return SpreadsheetCellRangeComparator.class;
    }
}
