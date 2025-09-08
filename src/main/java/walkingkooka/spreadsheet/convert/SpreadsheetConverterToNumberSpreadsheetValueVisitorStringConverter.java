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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.text.cursor.parser.BigDecimalParserToken;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContexts;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Converter} that inserts a {@link BigDecimal} as the type to {@link Converters#parser(Class, Parser, Function, BiFunction)}.
 */
final class SpreadsheetConverterToNumberSpreadsheetValueVisitorStringConverter extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterToNumberSpreadsheetValueVisitorStringConverter INSTANCE = new SpreadsheetConverterToNumberSpreadsheetValueVisitorStringConverter();


    private SpreadsheetConverterToNumberSpreadsheetValueVisitorStringConverter() {
        super();
        this.parser = Converters.parser(
            BigDecimal.class,
            Parsers.bigDecimal(), // parser
            (final SpreadsheetConverterContext c) -> ParserContexts.basic(
                InvalidCharacterExceptionFactory.POSITION,
                DateTimeContexts.fake(),
                c
            ),
            (ParserToken t, SpreadsheetConverterContext c) -> t.cast(BigDecimalParserToken.class).value()
        );
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return true; // always true because assumes SpreadsheetConverterToNumber#canConvert
    }

    @Override
    public <T> Either<T, String> doConvert(final Object value,
                                           final Class<T> type,
                                           final SpreadsheetConverterContext context) {
        final Either<BigDecimal, String> parsed = this.parser.convert(
            value,
            BigDecimal.class, // wont accept other Number types
            context
        );

        final Either<T, String> result;

        if(parsed.isLeft()) {
            result = context.convert(
                parsed.leftValue(),
                type
            );

        } else {
            result = this.failConversion(
                value,
                type
            );
        }

        return result;
    }

    private final Converter<SpreadsheetConverterContext> parser;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "String to Number";
    }
}
