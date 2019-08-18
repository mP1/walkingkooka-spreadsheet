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
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FailedConversionException;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

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
    public <T> T convert(final Object value,
                         final Class<T> type,
                         final ConverterContext context) {
        T converted = null;

        final TextCursor cursor = TextCursors.charSequence(String.class.cast(value));
        final TextCursorSavePoint save = cursor.save();

        for (List<SpreadsheetNumberParsePatternsComponent> pattern : this.pattern.patterns) {
            final SpreadsheetNumberParsePatternsContext patternsContext = SpreadsheetNumberParsePatternsContext.with(pattern.iterator(), context);
            patternsContext.nextComponent(cursor);
            if (cursor.isEmpty() && false == patternsContext.isRequired()) {
                try {
                    converted = NUMBER.convert(patternsContext.computeValue(), type, context);
                    break; // conversion successful.
                } catch (final RuntimeException cause) {
                    throw new FailedConversionException(value, type, cause);
                }
            }
            save.restore();
        }

        if (null == converted) {
            throw new FailedConversionException(value, type);
        }

        return converted;
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
