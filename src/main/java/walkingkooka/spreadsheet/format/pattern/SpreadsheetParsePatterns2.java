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

import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.Parsers;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Base class for any class that creates a {@link Parser} from a {@link DateTimeFormatter}.
 */
abstract class SpreadsheetParsePatterns2<T extends SpreadsheetFormatParserToken> extends SpreadsheetParsePatterns<T> {

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetParsePatterns2(final List<T> tokens) {
        super(tokens);
    }

    // HasConverter.....................................................................................................

    @Override
    final Converter<ConverterContext> createConverter() {
        return Converters.collection(IntStream.range(0, this.value().size())
                .mapToObj(this::createDateTimeFormatterConverter)
                .collect(Collectors.toList()));
    }

    /**
     * Sub classes should create a {@link Converter} using the given nth {@link SpreadsheetFormatParserToken}.
     */
    abstract Converter<ConverterContext> createDateTimeFormatterConverter(final int i);

    // HasParser........................................................................................................

    @Override
    final Parser<ParserContext> createParser() {
        return Parsers.alternatives(IntStream.range(0, this.value().size())
                .mapToObj(this::createDateTimeFormatterParser)
                .collect(Collectors.toList()));
    }

    /**
     * Sub classes should create a {@link Parser} using the given nth {@link SpreadsheetFormatParserToken}.
     */
    abstract Parser<ParserContext> createDateTimeFormatterParser(final int i);
}
