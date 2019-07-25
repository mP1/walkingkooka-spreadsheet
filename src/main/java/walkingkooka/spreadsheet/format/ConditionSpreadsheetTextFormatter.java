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

import walkingkooka.convert.ConversionException;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Tries to convert a value to a {@link BigDecimal} and then tests a condition and if it is true, executes the given {@link SpreadsheetTextFormatter}.
 */
final class ConditionSpreadsheetTextFormatter extends SpreadsheetTextFormatter3<SpreadsheetFormatConditionParserToken> {

    /**
     * Creates a {@link ConditionSpreadsheetTextFormatter}
     */
    static ConditionSpreadsheetTextFormatter with(final SpreadsheetFormatConditionParserToken token,
                                                  final SpreadsheetTextFormatter formatter) {
        check(token);
        Objects.requireNonNull(formatter, "formatter");

        return new ConditionSpreadsheetTextFormatter(token, formatter);
    }

    /**
     * Private use factory
     */
    private ConditionSpreadsheetTextFormatter(final SpreadsheetFormatConditionParserToken token,
                                              final SpreadsheetTextFormatter formatter) {
        super(token);

        this.predicate = ConditionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor.predicateOrFail(token);
        this.formatter = formatter;
    }

    @Override
    public boolean canFormat(final Object value) {
        return this.formatter.canFormat(value);
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final Object value, final SpreadsheetTextFormatContext context) {

        // predicate test result inverted because $value is on the wrong side of compare
        return this.test(value, context) ?
                this.formatter.format(value, context) :
                Optional.empty();
    }

    private boolean test(final Object value, final SpreadsheetTextFormatContext context) {
        boolean result;
        try {
            result = this.predicate.test(context.convert(value, BigDecimal.class));
        } catch (final ConversionException fail) {
            result = false;
        }
        return result;
    }

    /**
     * The formatter that will be executed if the guard test passes.
     */
    private final SpreadsheetTextFormatter formatter;

    /**
     * A guard which only executes the formatter if the condition is true.
     */
    final Predicate<BigDecimal> predicate;

    @Override
    final String toStringSuffix() {
        return " " + this.formatter;
    }
}
