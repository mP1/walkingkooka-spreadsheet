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

import walkingkooka.Either;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that formats any value after converting it to a {@link LocalDateTime}.
 */
final class SpreadsheetPatternSpreadsheetFormatterDateTime implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterDateTime} parse a {@link DateTimeSpreadsheetFormatParserToken}
     */
    static SpreadsheetPatternSpreadsheetFormatterDateTime with(final DateTimeSpreadsheetFormatParserToken token,
                                                               final Class<? extends Temporal> valueType) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(valueType, "valueType");

        return new SpreadsheetPatternSpreadsheetFormatterDateTime(token, valueType);
    }

    /**
     * Private ctor use static parse.
     */
    private SpreadsheetPatternSpreadsheetFormatterDateTime(final DateTimeSpreadsheetFormatParserToken token,
                                                           final Class<? extends Temporal> valueType) {
        super();
        this.token = token;
        this.valueType = valueType;

        final SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor analysis = SpreadsheetPatternSpreadsheetFormatterDateTimeAnalysisSpreadsheetFormatParserTokenVisitor.with();
        analysis.accept(token);
        this.twelveHour = analysis.twelveHour;
        this.millisecondDecimals = analysis.millisecondDecimals;
    }

    /**
     * Used to filter allowing different formatters for different {@link Temporal} types.
     */
    private final Class<? extends Temporal> valueType;

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                           final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        final Object valueOrNull = value.orElse(null);

        final Either<LocalDateTime, String> valueAsDateTime = null != valueOrNull ?
            context.convert(
                valueOrNull,
                LocalDateTime.class
            ) :
            null;

        return Optional.ofNullable(
            null != valueAsDateTime && valueAsDateTime.isLeft() ?
                this.formatLocalDateTime(
                    valueAsDateTime.leftValue(),
                    context
                ) :
                null
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

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetFormatterSelectorToken.tokens(this.token);
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

    private final DateTimeSpreadsheetFormatParserToken token;
}
