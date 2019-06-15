/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.format;

import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.type.PublicStaticHelper;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

/**
 * Collection of static factory methods for numerous {@link SpreadsheetTextFormatter}.
 */
public final class SpreadsheetTextFormatters implements PublicStaticHelper {

    /**
     * {@see BigDecimalSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter<BigDecimal> bigDecimal(final SpreadsheetFormatBigDecimalParserToken token,
                                                                  final MathContext mathContext) {
        return BigDecimalSpreadsheetTextFormatter.with(token, mathContext);
    }

    /**
     * {@see BigDecimalFractionSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter<BigDecimal> bigDecimalFraction(final SpreadsheetFormatFractionParserToken token,
                                                                          final MathContext mathContext,
                                                                          final Function<BigDecimal, Fraction> fractioner) {
        return BigDecimalFractionSpreadsheetTextFormatter.with(token, mathContext, fractioner);
    }

    /**
     * {@see ColorSpreadsheetTextFormatter}
     */
    public static <T> SpreadsheetTextFormatter color(final SpreadsheetFormatColorParserToken token,
                                                     final SpreadsheetTextFormatter<T> formatter) {
        return ColorSpreadsheetTextFormatter.with(token, formatter);
    }

    /**
     * {@link ConditionSpreadsheetTextFormatter}
     */
    public static <T> SpreadsheetTextFormatter<T> conditional(final SpreadsheetFormatConditionParserToken token,
                                                              final SpreadsheetTextFormatter<T> formatter) {
        return ConditionSpreadsheetTextFormatter.with(token, formatter);
    }

    /**
     * {@see ExpressionSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter<Object> expression(final SpreadsheetFormatExpressionParserToken token,
                                                              final MathContext mathContext,
                                                              final Function<BigDecimal, Fraction> fractioner) {
        return ExpressionSpreadsheetTextFormatter.with(token, mathContext, fractioner);
    }

    /**
     * {@see FakeSpreadsheetTextFormatter}
     */
    public static <V> SpreadsheetTextFormatter<V> fake() {
        return new FakeSpreadsheetTextFormatter<V>();
    }

    /**
     * {@see FixedSpreadsheetTextFormatter}
     */
    public static <V> SpreadsheetTextFormatter<V> fixed(final Class<V> type,
                                                        final Optional<SpreadsheetFormattedText> formattedText) {
        return FixedSpreadsheetTextFormatter.with(type, formattedText);
    }

    /**
     * {@see GeneralSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter<Object> general() {
        return GeneralSpreadsheetTextFormatter.INSTANCE;
    }

    /**
     * {@see LocalDateTimeSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter<LocalDateTime> localDateTime(final SpreadsheetFormatDateTimeParserToken token) {
        return LocalDateTimeSpreadsheetTextFormatter.with(token);
    }

    /**
     * {@see TextSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter<String> text(final SpreadsheetFormatTextParserToken token) {
        return TextSpreadsheetTextFormatter.with(token);
    }

    /**
     * Stops creation
     */
    private SpreadsheetTextFormatters() {
        throw new UnsupportedOperationException();
    }
}
