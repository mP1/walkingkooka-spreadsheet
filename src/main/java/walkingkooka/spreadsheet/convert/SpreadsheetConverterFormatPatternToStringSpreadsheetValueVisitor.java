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

import walkingkooka.color.Color;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.HasSpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetValueVisitor;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.tree.expression.ExpressionNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * This {@link SpreadsheetValueVisitor} attempts to use the provided pattern to format the value except for
 * some values like {@link Character}, {@link String} and subclasses of {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection}
 * which ignore the pattern and simply converted using {@link Object#toString()}.
 * <br>
 * Note it is possible for this fail if the pattern is invalid and parsing of the pattern fails.
 */
final class SpreadsheetConverterFormatPatternToStringSpreadsheetValueVisitor extends SpreadsheetValueVisitor {

    static String format(final Object value,
                         final String pattern,
                         final SpreadsheetConverterContext context) {
        final SpreadsheetConverterFormatPatternToStringSpreadsheetValueVisitor visitor = new SpreadsheetConverterFormatPatternToStringSpreadsheetValueVisitor(
            pattern,
            context
        );
        visitor.accept(value);
        return visitor.formatted;
    }

    // VisibleForTesting
    SpreadsheetConverterFormatPatternToStringSpreadsheetValueVisitor(final String pattern,
                                                                     final SpreadsheetConverterContext context) {
        super();
        this.pattern = pattern;
        this.context = context;
    }

    @Override
    protected void visit(final BigDecimal value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final BigInteger value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final Boolean value) {
        this.formatExpressionNumber(
            value ? 1 : 0
        );
    }

    @Override
    protected void visit(final Byte value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final Character value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final ExpressionNumber value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final Float value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final Double value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final Integer value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final LocalDate value) {
        this.patternFormatterFormatOrEmptyText(
            value,
            SpreadsheetPattern.parseDateFormatPattern(this.pattern)
        );
    }

    @Override
    protected void visit(final LocalDateTime value) {
        this.patternFormatterFormatOrEmptyText(
            value,
            SpreadsheetPattern.parseDateTimeFormatPattern(this.pattern)
        );
    }

    @Override
    protected void visit(final LocalTime value) {
        this.patternFormatterFormatOrEmptyText(
            value,
            SpreadsheetPattern.parseTimeFormatPattern(this.pattern)
        );
    }

    @Override
    protected void visit(final Long value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetCellReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetColumnReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetLabelName value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final SpreadsheetRowReference value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final Short value) {
        this.formatExpressionNumber(value);
    }

    @Override
    protected void visit(final String value) {
        this.formatText(value);
    }

    @Override
    protected void visit(final Object value) {
        this.formatText(value);
    }

    @Override
    protected void visitNull() {
        this.formatText("");
    }

    private void formatExpressionNumber(final Number number) {
        this.formatExpressionNumber(this.context.expressionNumberKind().create(number));
    }

    /**
     * Parses the pattern into a {@link SpreadsheetFormatter} and then formats the given number.
     */
    private void formatExpressionNumber(final ExpressionNumber number) {
        this.patternFormatterFormatOrEmptyText(
            number,
            SpreadsheetPattern.parseNumberFormatPattern(this.pattern)
        );
    }

    private void patternFormatterFormatOrEmptyText(final Object value,
                                                   final SpreadsheetPattern pattern) {
        this.formatText(
            pattern.formatter()
                .formatOrEmptyText(
                    Optional.of(value),
                    SpreadsheetFormatterContexts.basic(
                        HasSpreadsheetCell.NO_CELL,
                        this::numberToColor,
                        this::nameToColor,
                        1,
                        DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT, // default general-format-number-digit-count
                        SpreadsheetFormatters.fake(), // should never be called
                        (final Optional<Object> v) -> {
                            throw new UnsupportedOperationException();
                        },
                        this.context, // SpreadsheetConverterContext
                        SpreadsheetFormatterProviders.empty(),
                        ProviderContexts.fake()
                    )
                ).text()
        );
    }

    private Optional<Color> numberToColor(final Integer value) {
        return SpreadsheetText.WITHOUT_COLOR; // ignore the colour number
    }

    private Optional<Color> nameToColor(final SpreadsheetColorName name) {
        return SpreadsheetText.WITHOUT_COLOR; // ignore the colour name.
    }

    /**
     * Characters, strings and other non numeric values like {@link walkingkooka.spreadsheet.reference.SpreadsheetSelection}
     * are formatted as their plain text ignoring the pattern.
     */
    private void formatText(final Object value) {
        this.formatted = value.toString();
    }

    /**
     * A pattern, depending on the value this will be used to create a subclass of {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern}.
     */
    private final String pattern;

    private final SpreadsheetConverterContext context;

    private String formatted;

    @Override
    public String toString() {
        return this.formatted;
    }
}
