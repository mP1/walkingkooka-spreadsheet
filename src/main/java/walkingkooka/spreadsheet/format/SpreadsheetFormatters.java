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

import walkingkooka.convert.Converter;
import walkingkooka.math.Fraction;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ConditionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.FractionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternSpreadsheetFormatters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;

import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Collection of static factory methods for numerous {@link SpreadsheetFormatter}.
 * <br>
 * It is also possible to get a default {@link SpreadsheetFormatter} using {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind#formatter(Locale)}.
 */
public final class SpreadsheetFormatters implements PublicStaticHelper {

    /**
     * {@see SpreadsheetFormatterSharedAutomatic}
     */
    public static SpreadsheetFormatter automatic(final SpreadsheetFormatter date,
                                                 final SpreadsheetFormatter dateTime,
                                                 final SpreadsheetFormatter error,
                                                 final SpreadsheetFormatter number,
                                                 final SpreadsheetFormatter text,
                                                 final SpreadsheetFormatter time) {
        return SpreadsheetFormatterSharedAutomatic.with(
            date,
            dateTime,
            error,
            number,
            text,
            time
        );
    }

    /**
     * {@see SpreadsheetFormatterSharedBadgeError}
     */
    public static SpreadsheetFormatter badgeError(final SpreadsheetFormatter formatter) {
        return SpreadsheetFormatterSharedBadgeError.with(formatter);
    }

    /**
     * {@see SpreadsheetFormatterCollection}
     */
    public static SpreadsheetFormatter collection(final List<SpreadsheetFormatter> formatters) {
        return SpreadsheetFormatterCollection.with(formatters);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatters#color}
     */
    public static SpreadsheetPatternSpreadsheetFormatter color(final ColorSpreadsheetFormatParserToken token,
                                                               final SpreadsheetPatternSpreadsheetFormatter formatter) {
        return SpreadsheetPatternSpreadsheetFormatters.color(token, formatter);
    }

    /**
     * {@link SpreadsheetPatternSpreadsheetFormatters#conditional}
     */
    public static SpreadsheetFormatter conditional(final ConditionSpreadsheetFormatParserToken token,
                                                   final SpreadsheetPatternSpreadsheetFormatter formatter) {
        return SpreadsheetPatternSpreadsheetFormatters.conditional(token, formatter);
    }

    /**
     * {@see ContextFormatTextSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter contextFormatValue() {
        return SpreadsheetFormatterSharedContextFormat.INSTANCE;
    }

    /**
     * {@see SpreadsheetFormatterSharedConverter}
     */
    public static SpreadsheetFormatter converter(final Converter<ExpressionNumberConverterContext> converter) {
        return SpreadsheetFormatterSharedConverter.with(converter);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterDateTime}
     */
    public static SpreadsheetFormatter dateTime(final DateTimeSpreadsheetFormatParserToken token,
                                                final Class<? extends Temporal> valueType) {
        return SpreadsheetPatternSpreadsheetFormatters.dateTime(
            token,
            valueType
        );
    }

    /**
     * A {@link SpreadsheetFormatter} that prints the text with no colour.
     * This is equivalent to the pattern <pre>@</pre>
     */
    public static SpreadsheetFormatter defaultText() {
        return SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN.formatter();
    }

    /**
     * {@see SpreadsheetFormatterSharedEmpty}
     */
    public static SpreadsheetFormatter empty() {
        return SpreadsheetFormatterSharedEmpty.INSTANCE;
    }

    /**
     * {@see SpreadsheetFormatterSharedExpression}
     */
    public static SpreadsheetFormatter expression(final Expression expression) {
        return SpreadsheetFormatterSharedExpression.with(expression);
    }

    /**
     * {@see FakeSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter fake() {
        return new FakeSpreadsheetFormatter();
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter fakeSpreadsheetPattern() {
        return SpreadsheetPatternSpreadsheetFormatters.fake();
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterFraction}
     */
    public static SpreadsheetFormatter fraction(final FractionSpreadsheetFormatParserToken token,
                                                final Function<BigDecimal, Fraction> fractioner) {
        return SpreadsheetPatternSpreadsheetFormatters.fraction(token, fractioner);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterGeneral}
     */
    public static SpreadsheetFormatter general() {
        return SpreadsheetPatternSpreadsheetFormatters.general();
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterNumber}
     */
    public static SpreadsheetFormatter number(final NumberSpreadsheetFormatParserToken token,
                                                                final boolean suppressMinusSignsWithinParens) {
        return SpreadsheetPatternSpreadsheetFormatters.number(
            token,
            suppressMinusSignsWithinParens
        );
    }

    /**
     * {@see SpreadsheetPatternKind#formatter{java.util.Locale}
     */
    public static SpreadsheetFormatter spreadsheetPatternKind(final SpreadsheetPatternKind kind,
                                                              final Locale locale) {
        return kind.formatter(locale);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterText}
     */
    public static SpreadsheetFormatter text(final TextSpreadsheetFormatParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatters.text(token);
    }

    /**
     * {@see SpreadsheetFormatterSharedToString}
     */
    public static SpreadsheetFormatter toString(final SpreadsheetFormatter formatter,
                                                final String toString) {
        return SpreadsheetFormatterSharedToString.with(
            formatter,
            toString
        );
    }
    
    /**
     * Stops creation
     */
    private SpreadsheetFormatters() {
        throw new UnsupportedOperationException();
    }
}
