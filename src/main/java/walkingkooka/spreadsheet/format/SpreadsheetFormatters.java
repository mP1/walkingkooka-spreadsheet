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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Collection of static factory methods for numerous {@link SpreadsheetFormatter}.
 * <br>
 * It is also possible to get a default {@link SpreadsheetFormatter} using {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind#formatter(Locale)}.
 */
public final class SpreadsheetFormatters implements PublicStaticHelper {

    /**
     * {@see ChainSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter chain(final List<SpreadsheetFormatter> formatters) {
        return ChainSpreadsheetFormatter.with(formatters);
    }

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
    public static SpreadsheetFormatter conditional(final SpreadsheetFormatConditionParserToken token,
                                                   final SpreadsheetFormatter formatter) {
        return ConditionSpreadsheetFormatter.with(token, formatter);
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
     * {@see DateTimeSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter dateTime(final SpreadsheetFormatDateTimeParserToken token,
                                                final Predicate<Object> typeTester) {
        return DateTimeSpreadsheetFormatter.with(token, typeTester);
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
     * {@see NumberSpreadsheetFormatter}
     */
    public static SpreadsheetFormatter number(final SpreadsheetFormatNumberParserToken token) {
        return NumberSpreadsheetFormatter.with(token);
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
    public static SpreadsheetFormatter text(final SpreadsheetFormatTextParserToken token) {
        return SpreadsheetPatternSpreadsheetFormatterText.with(token);
    }

    /**
     * Stops creation
     */
    private SpreadsheetFormatters() {
        throw new UnsupportedOperationException();
    }
}
