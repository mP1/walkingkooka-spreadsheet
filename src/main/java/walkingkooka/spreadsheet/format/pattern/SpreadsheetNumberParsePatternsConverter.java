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

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * The {@link Converter} that handles each pattern returned by {@link SpreadsheetNumberParsePatterns#converter()}
 */
final class SpreadsheetNumberParsePatternsConverter implements Converter {

    static SpreadsheetNumberParsePatternsConverter with(final SpreadsheetNumberParsePatterns pattern) {
        return new SpreadsheetNumberParsePatternsConverter(pattern);
    }

    private SpreadsheetNumberParsePatternsConverter(final SpreadsheetNumberParsePatterns pattern) {
        super();
        this.pattern = pattern;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final ConverterContext context) {
        return value instanceof String &&
                (Byte.class == type ||
                        Short.class == type ||
                        Integer.class == type ||
                        Long.class == type ||
                        Float.class == type ||
                        Double.class == type ||
                        BigDecimal.class == type ||
                        BigInteger.class == type ||
                        Number.class == type);
    }

    /**
     * Tries all the components until all the text and components are consumed. The {@link Number} is then converted to the target type.
     */
    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final ConverterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(context, "context");

        return value instanceof String ?
                this.convertString((String) value, type, context) :
                this.failConversion(value, type);
    }

    private <T> Either<T, String> convertString(final String value,
                                                final Class<T> type,
                                                final ConverterContext context) {
        Either<T, String> result = null;

        final TextCursor cursor = TextCursors.charSequence(value);
        final TextCursorSavePoint save = cursor.save();

        // try all patterns until success or return failure.
        for (List<SpreadsheetNumberParsePatternsComponent> pattern : this.pattern.patterns) {
            final SpreadsheetNumberParsePatternsContext patternsContext = SpreadsheetNumberParsePatternsContext.with(pattern.iterator(), context);
            patternsContext.nextComponent(cursor);
            if (cursor.isEmpty() && false == patternsContext.isRequired()) {
                result = NUMBER.convert(patternsContext.computeValue(), type, context);
                break; // conversion successful.
            }
            save.restore();
        }

        if (null == result) {
            result = this.failConversion(value, type);
        }

        return result;
    }

    /**
     * A {@link Converter} that converts the parsed {@link BigDecimal} to the requested {@link Number}.
     */
    private final static Converter NUMBER = Converters.numberNumber();

    @Override
    public String toString() {
        return this.pattern.toString();
    }

    /**
     * The enclosing {@link SpreadsheetNumberParsePatterns}.
     */
    private final SpreadsheetNumberParsePatterns pattern;
}
