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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.type.PublicStaticHelper;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Collection of static factory methods for numerous {@link SpreadsheetTextFormatter}.
 */
public final class SpreadsheetTextFormatters implements PublicStaticHelper {

    /**
     * {@see BigDecimalFractionSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter bigDecimalFraction(final SpreadsheetFormatFractionParserToken token,
                                                              final Function<BigDecimal, Fraction> fractioner) {
        return BigDecimalFractionSpreadsheetTextFormatter.with(token, fractioner);
    }

    /**
     * {@see ColorSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter color(final SpreadsheetFormatColorParserToken token,
                                                 final SpreadsheetTextFormatter formatter) {
        return ColorSpreadsheetTextFormatter.with(token, formatter);
    }

    /**
     * {@link ConditionSpreadsheetTextFormatter}
     */
    public static <T> SpreadsheetTextFormatter conditional(final SpreadsheetFormatConditionParserToken token,
                                                           final SpreadsheetTextFormatter formatter) {
        return ConditionSpreadsheetTextFormatter.with(token, formatter);
    }

    /**
     * {@see ExpressionSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter expression(final SpreadsheetFormatExpressionParserToken token,
                                                      final Function<BigDecimal, Fraction> fractioner) {
        return ExpressionSpreadsheetTextFormatter.with(token, fractioner);
    }

    /**
     * {@see FakeSpreadsheetTextFormatter}
     */
    public static <V> SpreadsheetTextFormatter fake() {
        return new FakeSpreadsheetTextFormatter();
    }

    /**
     * {@see GeneralSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter general() {
        return GeneralSpreadsheetTextFormatter.INSTANCE;
    }

    /**
     * {@see LocalDateTimeSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter localDateTime(final SpreadsheetFormatDateTimeParserToken token) {
        return LocalDateTimeSpreadsheetTextFormatter.with(token);
    }

    /**
     * {@see NumberSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter number(final SpreadsheetFormatBigDecimalParserToken token) {
        return NumberSpreadsheetTextFormatter.with(token);
    }

    /**
     * {@see TextSpreadsheetTextFormatter}
     */
    public static SpreadsheetTextFormatter text(final SpreadsheetFormatTextParserToken token) {
        return TextSpreadsheetTextFormatter.with(token);
    }

    /**
     * Stops creation
     */
    private SpreadsheetTextFormatters() {
        throw new UnsupportedOperationException();
    }
}
