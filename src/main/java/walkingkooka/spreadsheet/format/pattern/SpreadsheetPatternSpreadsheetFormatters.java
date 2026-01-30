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

import walkingkooka.math.Fraction;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ConditionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.FractionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;

import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Collection of static factory methods for numerous {@link SpreadsheetFormatter}.
 * <br>
 * It is also possible to get a default {@link SpreadsheetFormatter} using {@link SpreadsheetPatternKind#formatter(Locale)}.
 */
public final class SpreadsheetPatternSpreadsheetFormatters implements PublicStaticHelper {

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterCollection}
     */
    public static SpreadsheetPatternSpreadsheetFormatter collection(final List<SpreadsheetPatternSpreadsheetFormatter> formatters) {
        return SpreadsheetPatternSpreadsheetFormatterCollection.with(formatters);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterColor}
     */
    public static SpreadsheetPatternSpreadsheetFormatter color(final ColorSpreadsheetFormatParserToken token,
                                                               final SpreadsheetPatternSpreadsheetFormatter formatter) {
        return SpreadsheetPatternSpreadsheetFormatterColor.with(token, formatter);
    }

    /**
     * {@link SpreadsheetPatternSpreadsheetFormatterCondition}
     */
    public static SpreadsheetPatternSpreadsheetFormatter conditional(final ConditionSpreadsheetFormatParserToken token,
                                                                     final SpreadsheetPatternSpreadsheetFormatter formatter) {
        return SpreadsheetPatternSpreadsheetFormatterCondition.with(token, formatter);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterDateTime}
     */
    public static SpreadsheetPatternSpreadsheetFormatter dateTime(final DateTimeSpreadsheetFormatParserToken token,
                                                                  final Class<? extends Temporal> valueType) {
        return SpreadsheetPatternSpreadsheetFormatterDateTime.with(
            token,
            valueType
        );
    }

    /**
     * A {@link SpreadsheetPatternSpreadsheetFormatter} that prints the text with no colour.
     * This is equivalent to the pattern <pre>@</pre>
     */
    public static SpreadsheetPatternSpreadsheetFormatter defaultText() {
        return SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN.formatter();
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatter}
     */
    public static SpreadsheetPatternSpreadsheetFormatter fake() {
        return new FakeSpreadsheetPatternSpreadsheetFormatter();
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterFraction}
     */
    public static SpreadsheetPatternSpreadsheetFormatter fraction(final FractionSpreadsheetFormatParserToken token,
                                                                  final Function<BigDecimal, Fraction> fractioner) {
        return SpreadsheetPatternSpreadsheetFormatterFraction.with(token, fractioner);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterGeneral}
     */
    public static SpreadsheetPatternSpreadsheetFormatter general() {
        return SpreadsheetPatternSpreadsheetFormatterGeneral.INSTANCE;
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterNumber}
     */
    public static SpreadsheetPatternSpreadsheetFormatter number(final NumberSpreadsheetFormatParserToken token,
                                                                final boolean suppressMinusSignsWithinParens) {
        return SpreadsheetPatternSpreadsheetFormatterNumber.with(
            token,
            suppressMinusSignsWithinParens
        );
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterText}
     */
    public static SpreadsheetPatternSpreadsheetFormatter text(final TextSpreadsheetFormatParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterText.with(token);
    }

    /**
     * Stops creation
     */
    private SpreadsheetPatternSpreadsheetFormatters() {
        throw new UnsupportedOperationException();
    }
}
