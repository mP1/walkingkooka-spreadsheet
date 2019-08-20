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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.type.PublicStaticHelper;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Collection of static factory methods for numerous {@link SpreadsheetFormatter}.
 */
public final class SpreadsheetFormatters implements PublicStaticHelper {

    /**
     * {@see ColorSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter color(final SpreadsheetFormatColorParserToken token,
                                             final SpreadsheetFormatter formatter) {
        return ColorSpreadsheetFormatter.with(token, formatter);
    }

    /**
     * {@link ConditionSpreadsheetFormatter}
     */
    public static <T> SpreadsheetFormatter conditional(final SpreadsheetFormatConditionParserToken token,
                                                       final SpreadsheetFormatter formatter) {
        return ConditionSpreadsheetFormatter.with(token, formatter);
    }

    /**
     * {@see ExpressionSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter expression(final SpreadsheetFormatExpressionParserToken token,
                                                  final Function<BigDecimal, Fraction> fractioner) {
        return ExpressionSpreadsheetFormatter.with(token, fractioner);
    }

    /**
     * {@see FakeSpreadsheetFormatter}
     */
    public static <V> SpreadsheetFormatter fake() {
        return new FakeSpreadsheetFormatter();
    }

    /**
     * {@see FractionSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter fraction(final SpreadsheetFormatFractionParserToken token,
                                                final Function<BigDecimal, Fraction> fractioner) {
        return FractionSpreadsheetFormatter.with(token, fractioner);
    }

    /**
     * {@see GeneralSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter general() {
        return GeneralSpreadsheetFormatter.INSTANCE;
    }

    /**
     * {@see LocalDateTimeSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter localDateTime(final SpreadsheetFormatDateTimeParserToken token) {
        return LocalDateTimeSpreadsheetFormatter.with(token);
    }

    /**
     * {@see NumberSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter number(final SpreadsheetFormatNumberParserToken token) {
        return NumberSpreadsheetFormatter.with(token);
    }

    /**
     * {@see TextSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter text(final SpreadsheetFormatTextParserToken token) {
        return TextSpreadsheetFormatter.with(token);
    }

    /**
     * Stops creation
     */
    private SpreadsheetFormatters() {
        throw new UnsupportedOperationException();
    }
}
