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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.ConditionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A {@link SpreadsheetPatternSpreadsheetFormatter} that wraps another {@link SpreadsheetPatternSpreadsheetFormatter} which only formats if the condition is true.
 */
final class SpreadsheetPatternSpreadsheetFormatterCondition implements SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterCondition}
     */
    static SpreadsheetPatternSpreadsheetFormatterCondition with(final ConditionSpreadsheetFormatParserToken token,
                                                                final SpreadsheetPatternSpreadsheetFormatter formatter) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(formatter, "formatter");

        return new SpreadsheetPatternSpreadsheetFormatterCondition(
            token,
            formatter
        );
    }

    /**
     * Private use factory
     */
    private SpreadsheetPatternSpreadsheetFormatterCondition(final ConditionSpreadsheetFormatParserToken token,
                                                            final SpreadsheetPatternSpreadsheetFormatter formatter) {
        super();

        this.token = token;
        this.predicate = SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor.predicateOrFail(token);
        this.formatter = formatter;
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                           final SpreadsheetFormatterContext context) {
        return context.convert(
                value.orElse(null),
                BigDecimal.class
            ).mapLeft(v -> null != v && this.predicate.test(v))
            .orElseLeft(false) ?
            this.formatter.formatSpreadsheetText(
                value,
                context
            ) :
            Optional.empty();
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetFormatterSelectorToken.tokens(this.token);
    }

    /**
     * The formatter that will be executed if the guard (condition) test passes.
     */
    private final SpreadsheetPatternSpreadsheetFormatter formatter;

    /**
     * A guard which only executes the formatter if the condition is true.
     */
    final Predicate<BigDecimal> predicate;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.token.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetPatternSpreadsheetFormatterCondition && this.equals0((SpreadsheetPatternSpreadsheetFormatterCondition) other);
    }

    private boolean equals0(final SpreadsheetPatternSpreadsheetFormatterCondition other) {
        return this.token.equals(other.token) &&
            this.formatter.equals(other.formatter);
    }

    @Override
    public String toString() {
        return this.token.text() + " " + this.formatter;
    }

    private final ConditionSpreadsheetFormatParserToken token;
}
