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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Tries to convert a value to a {@link BigDecimal} and then tests a condition and if it is true, executes the given {@link SpreadsheetFormatter}.
 */
final class SpreadsheetPatternSpreadsheetFormatterCondition extends SpreadsheetPatternSpreadsheetFormatter {

    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterCondition}
     */
    static SpreadsheetPatternSpreadsheetFormatterCondition with(final SpreadsheetFormatConditionParserToken token,
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
    private SpreadsheetPatternSpreadsheetFormatterCondition(final SpreadsheetFormatConditionParserToken token,
                                                            final SpreadsheetPatternSpreadsheetFormatter formatter) {
        super();

        this.token = token;
        this.predicate = SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor.predicateOrFail(token);
        this.formatter = formatter;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return context.convert(value, BigDecimal.class)
                .mapLeft(l -> this.predicate.test(l) && this.formatter.canFormat(value, context))
                .orElseLeft(false);
    }

    @Override
    Optional<SpreadsheetText> format0(final Object value, final SpreadsheetFormatterContext context) {
        return context.convert(value, BigDecimal.class)
                .mapLeft(this.predicate::test)
                .orElseLeft(false) ?
                this.formatter.format(value, context) :
                Optional.empty();
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
    public String toString() {
        return this.token.text() + " " + this.formatter;
    }

    private final SpreadsheetFormatConditionParserToken token;
}
