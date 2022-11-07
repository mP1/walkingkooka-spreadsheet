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
import walkingkooka.convert.Converters;
import walkingkooka.math.Maths;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

import java.util.List;
import java.util.Objects;

/**
 * The {@link Converter} that handles each pattern returned by {@link SpreadsheetNumberParsePattern#converter()}
 */
final class SpreadsheetNumberParsePatternsConverter implements Converter<ExpressionNumberConverterContext> {

    static SpreadsheetNumberParsePatternsConverter with(final SpreadsheetNumberParsePattern pattern) {
        return new SpreadsheetNumberParsePatternsConverter(pattern);
    }

    private SpreadsheetNumberParsePatternsConverter(final SpreadsheetNumberParsePattern pattern) {
        super();
        this.pattern = pattern;
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final ExpressionNumberConverterContext context) {
        return value instanceof String && ExpressionNumber.isClass(type);
    }

    /**
     * Tries all the components until all the text and components are consumed. The {@link Number} is then converted to the target type.
     */
    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final ExpressionNumberConverterContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(context, "context");

        return value instanceof String ?
                this.convertString((String) value, type, context) :
                this.failConversion(value, type);
    }

    private <T> Either<T, String> convertString(final String value,
                                                final Class<T> type,
                                                final ExpressionNumberConverterContext context) {
        Either<T, String> result = null;

        final TextCursor cursor = TextCursors.charSequence(value);
        final TextCursorSavePoint save = cursor.save();

        // try all patterns until success or return failure.
        for (final List<SpreadsheetNumberParsePatternsComponent> pattern : this.pattern.patterns) {
            final SpreadsheetNumberParsePatternsRequest request = SpreadsheetNumberParsePatternsRequest.with(
                    pattern.iterator(),
                    SpreadsheetNumberParsePatternsMode.VALUE,
                    context
            );
            if (request.nextComponent(cursor) && cursor.isEmpty()) {
                final List<ParserToken> tokens = request.tokens;
                result = tokens.isEmpty() ?
                        null :
                        convertString0(save, tokens, type, context);
            }
            save.restore();
        }

        if (null == result) {
            result = this.failConversion(value, type);
        }

        return result;
    }

    /**
     * First convert the {@link ParserToken} to a {@link walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken} and then a {@link ExpressionNumber}
     * and then to the requested target type.
     */
    private <T> Either<T, String> convertString0(final TextCursorSavePoint save,
                                                 final List<ParserToken> tokens,
                                                 final Class<T> targetType,
                                                 final ExpressionNumberConverterContext context) {
        final ExpressionNumber number = SpreadsheetParserToken.number(
                tokens,
                save.textBetween().toString()
        ).toNumber(SpreadsheetNumberParsePatternsConverterExpressionEvaluationContext.with(context));

        // targetType will be either a Number or ExpressionNumber, the former requires a convert from ExpressionNumber.value
        return Maths.isNumberClass(targetType) ?
                NUMBER.convert(
                        number,
                        targetType,
                        context
                ).mapRight(old -> save.textBetween().toString()) :
                ExpressionNumber.isClass(targetType) ?
                        this.successfulConversion(
                                number,
                                targetType
                        ) :
                        failConversion(save.textBetween().toString(), targetType);
    }

    /**
     * A {@link Converter} that accepts a {@link ExpressionNumber} and converts it to a {@link Number}.
     */
    private final static Converter<ExpressionNumberConverterContext> NUMBER = ExpressionNumber.fromConverter(Converters.numberNumber());

    @Override
    public String toString() {
        return this.pattern.toString();
    }

    /**
     * The enclosing {@link SpreadsheetNumberParsePattern}.
     */
    private final SpreadsheetNumberParsePattern pattern;
}
