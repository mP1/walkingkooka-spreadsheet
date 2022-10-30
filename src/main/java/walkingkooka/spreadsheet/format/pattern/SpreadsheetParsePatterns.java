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
import walkingkooka.convert.HasConverter;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.HasParser;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

/**
 * Holds a {@link ParserToken} typically a date/dateime/time and possibly color or conditions.
 */
public abstract class SpreadsheetParsePatterns extends SpreadsheetPattern
        implements HasConverter<ExpressionNumberConverterContext>,
        HasParser<SpreadsheetParserContext> {

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetParsePatterns(final ParserToken token) {
        super(token);
    }

    // HasConverter........................................................................................................

    /**
     * Returns a {@link Converter} which will try all the patterns.
     */
    public final Converter<ExpressionNumberConverterContext> converter() {
        if (null == this.converter) {
            this.converter = this.createConverter();
        }
        return this.converter;
    }

    private Converter<ExpressionNumberConverterContext> converter;

    /**
     * Factory that lazily creates a {@link Converter}
     */
    abstract Converter<ExpressionNumberConverterContext> createConverter();

    // HasParser........................................................................................................

    /**
     * Returns a {@link Parser} which will try all the patterns.<br>
     * {@link java.time.LocalDate}, {@link java.time.LocalDateTime}, {@link java.time.LocalTime} will all fail to parse
     * the if the value has extra trailing text. If this parse is for {@link walkingkooka.tree.expression.ExpressionNumber}
     * and will be used to parse number literals the {@link Parser#andEmptyTextCursor()} must be called afterwards.
     */
    public final Parser<SpreadsheetParserContext> parser() {
        if (null == this.parser) {
            this.parser = this.createParser();
        }
        return this.parser;
    }

    private Parser<SpreadsheetParserContext> parser;

    /**
     * Factory that lazily creates a {@link Parser}
     */
    abstract Parser<SpreadsheetParserContext> createParser();
}
