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

import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.util.Optionals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetTextFormatter} that formats a {@link String}.
 * <a href="https://developers.google.com/sheets/api/guides/formats"></a>
 */
final class ExpressionSpreadsheetTextFormatter extends SpreadsheetTextFormatter3<Object, SpreadsheetFormatExpressionParserToken> {

    /**
     * Creates a {@link ExpressionSpreadsheetTextFormatter} from a {@link SpreadsheetFormatTextParserToken}.
     */
    static ExpressionSpreadsheetTextFormatter with(final SpreadsheetFormatExpressionParserToken token,
                                                   final MathContext mathContext,
                                                   final Function<BigDecimal, Fraction> fractioner) {
        check(token);
        Objects.requireNonNull(mathContext, "mathContext");
        Objects.requireNonNull(fractioner, "fractioner");

        return new ExpressionSpreadsheetTextFormatter(token,
                ExpressionSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor.analyze(token, mathContext, fractioner));
    }

    /**
     * Private ctor use static parse.
     */
    private ExpressionSpreadsheetTextFormatter(final SpreadsheetFormatExpressionParserToken token,
                                               final List<SpreadsheetTextFormatter<Object>> formatters) {
        super(token);
        if (formatters.size() > 4) {
            throw new IllegalArgumentException("Expected at most 4 formatters but got " + formatters.size() + "=" + formatters);
        }
        this.formatters = formatters;
    }

    @Override
    public Class<Object> type() {
        return Object.class;
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final Object value, final SpreadsheetTextFormatContext context) {
        return this.formatters.stream()
                .skip(this.skip(value))
                .filter(f -> f.type().isInstance(value))
                .flatMap(f -> Optionals.stream(f.format(value, context)))
                .findFirst();
    }

    /**
     * Special case text, skip ahead until the 4th formatter, for other types like date/number etc start from the first.
     */
    private long skip(final Object value) {
        return value instanceof String ?
                3 :
                0;
    }

    private final List<SpreadsheetTextFormatter<Object>> formatters;

    @Override
    String toStringSuffix() {
        return "";
    }
}
