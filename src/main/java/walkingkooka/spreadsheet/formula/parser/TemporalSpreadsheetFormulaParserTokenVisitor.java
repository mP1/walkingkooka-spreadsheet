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

package walkingkooka.spreadsheet.formula.parser;

import walkingkooka.ToStringBuilder;
import walkingkooka.datetime.DateTimeContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A {@link SpreadsheetFormulaParserTokenVisitor} that accepts a {@link SpreadsheetFormulaParserToken} and after visiting may be used to create
 * one of {@link LocalDate}, {@link LocalDateTime} or {@link LocalTime}.
 */
final class TemporalSpreadsheetFormulaParserTokenVisitor extends SpreadsheetFormulaParserTokenVisitor {

    /**
     * Creates a {@link TemporalSpreadsheetFormulaParserTokenVisitor}, visits the {@link ParentSpreadsheetFormulaParserToken} and returns the visitor
     * which has methods to create one of {@link LocalDate}, {@link LocalDateTime} or {@link LocalTime}.
     */
    static TemporalSpreadsheetFormulaParserTokenVisitor acceptSpreadsheetParentParserToken(final ParentSpreadsheetFormulaParserToken token,
                                                                                           final int defaultYear) {
        final TemporalSpreadsheetFormulaParserTokenVisitor visitor = new TemporalSpreadsheetFormulaParserTokenVisitor(defaultYear);
        visitor.accept(token);
        return visitor;
    }

    // @VisibleForTesting
    TemporalSpreadsheetFormulaParserTokenVisitor(final int defaultYear) {
        super();
        this.year = defaultYear;
    }

    // visit....................................................................................................
    // ignore all SymbolParserTokens, dont bother to collect them.

    @Override
    protected void visit(final AmPmSpreadsheetFormulaParserToken token) {
        this.ampm = token.value();
    }

    @Override
    protected void visit(final DayNumberSpreadsheetFormulaParserToken token) {
        this.day = token.value();
    }

    @Override
    protected void visit(final HourSpreadsheetFormulaParserToken token) {
        this.hour = token.value();
    }

    @Override
    protected void visit(final MillisecondSpreadsheetFormulaParserToken token) {
        this.millis = token.value();
    }

    @Override
    protected void visit(final MinuteSpreadsheetFormulaParserToken token) {
        this.minute = token.value();
    }

    @Override
    protected void visit(final MonthNameSpreadsheetFormulaParserToken token) {
        this.month = token.value();
    }

    @Override
    protected void visit(final MonthNameAbbreviationSpreadsheetFormulaParserToken token) {
        this.month = token.value();
    }

    @Override
    protected void visit(final MonthNameInitialSpreadsheetFormulaParserToken token) {
        this.month = token.value();
    }

    @Override
    protected void visit(final MonthNumberSpreadsheetFormulaParserToken token) {
        this.month = token.value();
    }

    @Override
    protected void visit(final SecondsSpreadsheetFormulaParserToken token) {
        this.seconds = token.value();
    }

    @Override
    protected void visit(final YearSpreadsheetFormulaParserToken token) {
        this.year = token.value();
        this.twoDigitYear = token.text().length() <= 2;
    }

    // toXXX ............................................................................................................

    /**
     * Creates a {@link LocalDate} assuming defaults have been set and an entire {@link DateSpreadsheetFormulaParserToken} has
     * been visited.
     */
    LocalDate toLocalDate(final DateTimeContext context) {
        Objects.requireNonNull(context, "context");

        final int year = this.year;

        return LocalDate.of(
            this.twoDigitYear ? context.twoToFourDigitYear(year) : year,
            this.month,
            this.day
        );
    }

    /**
     * Creates a {@link LocalTime} assuming defaults have been set and an entire {@link TimeSpreadsheetFormulaParserToken} has
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
     * Creates a {@link LocalDateTime} assuming defaults have been set and an entire {@link DateTimeSpreadsheetFormulaParserToken} has
     * been visited.
     */
    LocalDateTime toLocalDateTime(final DateTimeContext context) {
        return LocalDateTime.of(
            this.toLocalDate(context),
            this.toLocalTime()
        );
    }

    private int day = 1;
    private int month = 1;
    private int year;
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
