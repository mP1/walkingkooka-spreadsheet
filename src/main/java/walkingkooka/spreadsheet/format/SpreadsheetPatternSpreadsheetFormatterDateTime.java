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

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that formats any value after converting it to a {@link LocalDateTime}.
 */
final class SpreadsheetPatternSpreadsheetFormatterDateTime implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterDateTime} parse a {@link SpreadsheetFormatDateTimeParserToken}
     */
    static SpreadsheetPatternSpreadsheetFormatterDateTime with(final SpreadsheetFormatDateTimeParserToken token,
                                                               final Class<? extends Temporal> valueType) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(valueType, "valueType");

        return new SpreadsheetPatternSpreadsheetFormatterDateTime(token, valueType);
    }

    /**
     * Private ctor use static parse.
     */
    private SpreadsheetPatternSpreadsheetFormatterDateTime(final SpreadsheetFormatDateTimeParserToken token,
                                                           final Class<? extends Temporal> valueType) {
        super();
        this.token = token;
        this.valueType = valueType;

        final SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor analysis = SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor.with();
        analysis.accept(token);
        this.twelveHour = analysis.twelveHour;
        this.millisecondDecimals = analysis.millisecondDecimals;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return null != value &&
                this.valueType.equals(value.getClass()) &&
                context.canConvertOrFail(
                        value,
                        LocalDateTime.class
                );
    }

    /**
     * Used to filter allowing different formatters for different {@link Temporal} types.
     */
    private final Class<? extends Temporal> valueType;

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
    public int hashCode() {
        return Objects.hash(
                this.valueType,
                this.token
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetPatternSpreadsheetFormatterDateTime && this.equals0((SpreadsheetPatternSpreadsheetFormatterDateTime) other);
    }

    private boolean equals0(final SpreadsheetPatternSpreadsheetFormatterDateTime other) {
        return this.valueType == other.valueType &&
                this.token.equals(other.token);
    }

    @Override
    public String toString() {
        return this.token.text();
    }

    private final SpreadsheetFormatDateTimeParserToken token;
}
