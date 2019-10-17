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
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Tries to convert a value to a {@link BigDecimal} and then tests a condition and if it is true, executes the given {@link SpreadsheetFormatter}.
 */
final class ConditionSpreadsheetFormatter extends SpreadsheetFormatter3<SpreadsheetFormatConditionParserToken> {

    /**
     * Creates a {@link ConditionSpreadsheetFormatter}
     */
    static ConditionSpreadsheetFormatter with(final SpreadsheetFormatConditionParserToken token,
                                              final SpreadsheetFormatter formatter) {
        checkParserToken(token);
        checkFormatter(formatter);

        return new ConditionSpreadsheetFormatter(token, formatter);
    }

    /**
     * Private use factory
     */
    private ConditionSpreadsheetFormatter(final SpreadsheetFormatConditionParserToken token,
                                          final SpreadsheetFormatter formatter) {
        super(token);

        this.predicate = ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor.predicateOrFail(token);
        this.formatter = formatter;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        return this.formatter.canFormat(value, context);
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
     * The formatter that will be executed if the guard test passes.
     */
    private final SpreadsheetFormatter formatter;

    /**
     * A guard which only executes the formatter if the condition is true.
     */
    final Predicate<BigDecimal> predicate;

    @Override
    final String toStringSuffix() {
        return " " + this.formatter;
    }
}
