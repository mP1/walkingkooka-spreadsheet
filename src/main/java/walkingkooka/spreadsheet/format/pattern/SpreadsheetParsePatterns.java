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

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.HasParser;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds a a {@link List} of {@link SpreadsheetFormatDateTimeParserToken date/time} or {@link SpreadsheetFormatNumberParserToken} number tokens and some common functionality.
 */
public abstract class SpreadsheetParsePatterns<T extends SpreadsheetFormatParserToken> extends SpreadsheetPattern<List<T>>
    implements HasConverter,
        HasParser<ParserContext> {

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetParsePatterns(final List<T> tokens) {
        super(Lists.immutable(tokens));
    }

    // Object...........................................................................................................

    private final static String SEPARATOR = ";";

    /**
     * Attempts to reconstruct an equivalent but not exact pattern representation of the given tokens. The actual
     * optional whitespace and separator tokens are not present only the individual patterns.
     */
    @Override
    public final String toString() {
        return this.value.stream()
                .map(Object::toString)
                .collect(Collectors.joining(SEPARATOR));
    }

    // HasConverter........................................................................................................

    /**
     * Returns a {@link Converter} which will try all the patterns.
     */
    public final Converter converter() {
        if (null == this.converter) {
            this.converter = this.createConverter();
        }
        return this.converter;
    }

    private Converter converter;

    /**
     * Factory that lazily creates a {@link Converter}
     */
    abstract Converter createConverter();
    
    // HasParser........................................................................................................

    /**
     * Returns a {@link Parser} which will try all the patterns.
     */
    public final Parser<ParserContext> parser() {
        if (null == this.parser) {
            this.parser = this.createParser();
        }
        return this.parser;
    }

    private Parser<ParserContext> parser;

    /**
     * Factory that lazily creates a {@link Parser}
     */
    abstract Parser<ParserContext> createParser();
}
