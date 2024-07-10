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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatFractionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

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
     * {@see AutomaticSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter automatic(final SpreadsheetFormatter date,
                                                 final SpreadsheetFormatter dateTime,
                                                 final SpreadsheetFormatter number,
                                                 final SpreadsheetFormatter text,
                                                 final SpreadsheetFormatter time) {
        return AutomaticSpreadsheetFormatter.with(
                date,
                dateTime,
                number,
                text,
                time
        );
    }

    /**
     * {@see ChainSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter chain(final List<SpreadsheetFormatter> formatters) {
        return ChainSpreadsheetFormatter.with(formatters);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterColor}
     */
    public static SpreadsheetPatternSpreadsheetFormatter color(final SpreadsheetFormatColorParserToken token,
                                                               final SpreadsheetPatternSpreadsheetFormatter formatter) {
        return SpreadsheetPatternSpreadsheetFormatterColor.with(token, formatter);
    }

    /**
     * {@link SpreadsheetPatternSpreadsheetFormatterCondition}
     */
    public static SpreadsheetPatternSpreadsheetFormatter conditional(final SpreadsheetFormatConditionParserToken token,
                                                                     final SpreadsheetPatternSpreadsheetFormatter formatter) {
        return SpreadsheetPatternSpreadsheetFormatterCondition.with(token, formatter);
    }

    /**
     * {@see ContextFormatTextSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter contextFormat() {
        return ContextFormatTextSpreadsheetFormatter.INSTANCE;
    }

    /**
     * {@see ConverterSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter converter(final Converter<ExpressionNumberConverterContext> converter) {
        return ConverterSpreadsheetFormatter.with(converter);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterDateTime}
     */
    public static SpreadsheetPatternSpreadsheetFormatter dateTime(final SpreadsheetFormatDateTimeParserToken token,
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
     * {@see EmptySpreadsheetFormatter}
     */
    public static SpreadsheetFormatter empty() {
        return EmptySpreadsheetFormatter.INSTANCE;
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
    public static SpreadsheetPatternSpreadsheetFormatter fakeSpreadsheetPattern() {
        return new FakeSpreadsheetPatternSpreadsheetFormatter();
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterFraction}
     */
    public static SpreadsheetPatternSpreadsheetFormatter fraction(final SpreadsheetFormatFractionParserToken token,
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
    public static SpreadsheetPatternSpreadsheetFormatter number(final SpreadsheetFormatNumberParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterNumber.with(token);
    }

    /**
     * {@see SpreadsheetPatternSpreadsheetFormatterChain}
     */
    public static SpreadsheetPatternSpreadsheetFormatter spreadsheetPatternChain(final List<SpreadsheetPatternSpreadsheetFormatter> formatters) {
        return SpreadsheetPatternSpreadsheetFormatterChain.with(formatters);
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
    public static SpreadsheetPatternSpreadsheetFormatter text(final SpreadsheetFormatTextParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterText.with(token);
    }

    /**
     * Stops creation
     */
    private SpreadsheetFormatters() {
        throw new UnsupportedOperationException();
    }
}
