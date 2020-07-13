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

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A {@link SpreadsheetFormatter} that formats a {@link String}.
 * <a href="https://developers.google.com/sheets/api/guides/formats"></a>
 */
final class ExpressionSpreadsheetFormatter extends SpreadsheetFormatter3<SpreadsheetFormatExpressionParserToken> {

    /**
     * Creates a {@link ExpressionSpreadsheetFormatter} from a {@link SpreadsheetFormatTextParserToken}.
     */
    static ExpressionSpreadsheetFormatter with(final SpreadsheetFormatExpressionParserToken token,
                                               final Function<BigDecimal, Fraction> fractioner) {
        checkParserToken(token);
        Objects.requireNonNull(fractioner, "fractioner");

        return new ExpressionSpreadsheetFormatter(token,
                ExpressionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor.analyze(token, fractioner));
    }

    /**
     * Private ctor use static parse.
     */
    private ExpressionSpreadsheetFormatter(final SpreadsheetFormatExpressionParserToken token,
                                           final List<SpreadsheetFormatter> formatters) {
        super(token);
        if (formatters.size() > 4) {
            throw new IllegalArgumentException("Expected at most 4 formatters but got " + formatters.size() + "=" + formatters);
        }
        this.formatters = formatters;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        return this.formatters.stream()
                .filter(f -> f.canFormat(value, context))
                .limit(1)
                .count() == 1;
    }

    /**
     * If none of the formatters match and format do a {@link SpreadsheetFormatterContext#defaultFormatText}
     */
    @Override
    Optional<SpreadsheetText> format0(final Object value, final SpreadsheetFormatterContext context) {
        return this.formatters.stream()
                .skip(this.skip(value))
                .filter(f -> f.canFormat(value, context))
                .flatMap(f -> f.format(value, context).map(Stream::of).orElse(Stream.empty())) // Optional.stream not supported in j2cl.
                .findFirst()
                .or(() -> context.defaultFormatText(value));
    }

    /**
     * Special case text, skip ahead until the 4th formatter, for other types like date/number etc start from the first.
     */
    private long skip(final Object value) {
        return value instanceof String ?
                3 :
                0;
    }

    private final List<SpreadsheetFormatter> formatters;

    @Override
    String toStringSuffix() {
        return "";
    }
}
