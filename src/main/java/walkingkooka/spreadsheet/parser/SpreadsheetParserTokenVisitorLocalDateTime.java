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

package walkingkooka.spreadsheet.parser;

import walkingkooka.ToStringBuilder;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A {@link SpreadsheetParserTokenVisitor} that accepts a {@link SpreadsheetParserToken} and after visiting may be used to create
 * one of {@link LocalDate}, {@link LocalDateTime} or {@link LocalTime}.
 */
final class SpreadsheetParserTokenVisitorLocalDateTime extends SpreadsheetParserTokenVisitor {

    /**
     * Creates a {@link SpreadsheetParserTokenVisitorLocalDateTime}, visits the {@link SpreadsheetParentParserToken} and returns the visitor
     * which has methods to create one of {@link LocalDate}, {@link LocalDateTime} or {@link LocalTime}.
     */
    static SpreadsheetParserTokenVisitorLocalDateTime acceptSpreadsheetParentParserToken(final SpreadsheetParentParserToken token) {
        final SpreadsheetParserTokenVisitorLocalDateTime visitor = new SpreadsheetParserTokenVisitorLocalDateTime();
        visitor.accept(token);
        return visitor;
    }

    // @VisibleForTesting
    SpreadsheetParserTokenVisitorLocalDateTime() {
        super();
    }

    // visit....................................................................................................
    // ignore all SymbolParserTokens, dont bother to collect them.

    @Override
    protected void visit(final SpreadsheetAmPmParserToken token) {
        this.ampm = token.value();
    }

    @Override
    protected void visit(final SpreadsheetDayNumberParserToken token) {
        this.day = token.value();
    }

    @Override
    protected void visit(final SpreadsheetHourParserToken token) {
        this.hour = token.value();
    }

    @Override
    protected void visit(final SpreadsheetMillisecondParserToken token) {
        this.millis = token.value();
    }

    @Override
    protected void visit(final SpreadsheetMinuteParserToken token) {
        this.minute = token.value();
    }

    @Override
    protected void visit(final SpreadsheetMonthNameParserToken token) {
        this.month = token.value();
    }

    @Override
    protected void visit(final SpreadsheetMonthNameAbbreviationParserToken token) {
        this.month = token.value();
    }

    @Override
    protected void visit(final SpreadsheetMonthNameInitialParserToken token) {
        this.month = token.value();
    }

    @Override
    protected void visit(final SpreadsheetMonthNumberParserToken token) {
        this.month = token.value();
    }

    @Override
    protected void visit(final SpreadsheetSecondsParserToken token) {
        this.seconds = token.value();
    }

    @Override
    protected void visit(final SpreadsheetYearParserToken token) {
        this.year = token.value();
        this.twoDigitYear = token.text().length() <= 2;
    }

    // toXXX ............................................................................................................

    /**
     * Creates a {@link LocalDate} assuming defaults have been set and an entire {@link SpreadsheetDateParserToken} has
     * been visited.
     */
    LocalDate toLocalDate(final ExpressionEvaluationContext context) {
        Objects.requireNonNull(context, "context");

        final int year = this.year;

        return LocalDate.of(
                this.twoDigitYear ? context.twoToFourDigitYear(year) : year,
                this.month,
                this.day
        );
    }

    /**
     * Creates a {@link LocalTime} assuming defaults have been set and an entire {@link SpreadsheetTimeParserToken} has
     * been visited.
     */
    LocalTime toLocalTime() {
        return LocalTime.of(
                this.hour + this.ampm,
                this.minute,
                this.seconds,
                this.millis
        );
    }

    /**
     * Creates a {@link LocalDateTime} assuming defaults have been set and an entire {@link SpreadsheetDateTimeParserToken} has
     * been visited.
     */
    LocalDateTime toLocalDateTime(final ExpressionEvaluationContext context) {
        return LocalDateTime.of(
                this.toLocalDate(context),
                this.toLocalTime()
        );
    }

    private int day = 1;
    private int month = 1;
    private int year = 0; // https://github.com/mP1/walkingkooka-spreadsheet/issues/1309 Default year when parsing date or date/time
    private boolean twoDigitYear = false;

    private int hour = 0;
    private int minute = 0;
    private int seconds = 0;
    private int millis = 0;
    private int ampm = 0;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("day").value(this.day)
                .label("month").value(this.month)
                .label("year").value(this.year)
                .label("hour").value(this.hour)
                .label("minutes").value(this.minute)
                .label("seconds").value(this.seconds)
                .label("millis").value(this.millis)
                .build();
    }
}
