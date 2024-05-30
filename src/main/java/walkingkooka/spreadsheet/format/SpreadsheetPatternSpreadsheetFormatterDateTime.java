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

package walkingkooka.spreadsheet.format;

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that formats any value after converting it to a {@link LocalDateTime}.
 */
final class SpreadsheetPatternSpreadsheetFormatterDateTime implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterDateTime} parse a {@link SpreadsheetFormatDateTimeParserToken}
     */
    static SpreadsheetPatternSpreadsheetFormatterDateTime with(final SpreadsheetFormatDateTimeParserToken token,
                                                               final Predicate<Object> typeTester) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(typeTester, "typeTester");

        return new SpreadsheetPatternSpreadsheetFormatterDateTime(token, typeTester);
    }

    /**
     * Private ctor use static parse.
     */
    private SpreadsheetPatternSpreadsheetFormatterDateTime(final SpreadsheetFormatDateTimeParserToken token,
                                                           final Predicate<Object> typeTester) {
        super();
        this.token = token;
        this.typeTester = typeTester;

        final SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor analysis = SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor.with();
        analysis.accept(token);
        this.twelveHour = analysis.twelveHour;
        this.millisecondDecimals = analysis.millisecondDecimals;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return this.typeTester.test(value) && context.canConvertOrFail(value, LocalDateTime.class);
    }

    /**
     * Used to filter allowing different formatters for different {@link Temporal} types.
     */
    private final Predicate<Object> typeTester;

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Object value,
                                                           final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return Optional.of(
                this.formatLocalDateTime(
                        context.convertOrFail(
                                value,
                                LocalDateTime.class
                        ),
                        context
                )
        );
    }

    private SpreadsheetText formatLocalDateTime(final LocalDateTime dateTime,
                                                final SpreadsheetFormatterContext context) {
        return SpreadsheetPatternSpreadsheetFormatterDateTimeFormatSpreadsheetFormatParserTokenVisitor.format(this.token,
                dateTime,
                context,
                this.twelveHour,
                this.millisecondDecimals);
    }

    private final boolean twelveHour;
    private final int millisecondDecimals;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.token.text();
    }

    private final SpreadsheetFormatDateTimeParserToken token;
}
