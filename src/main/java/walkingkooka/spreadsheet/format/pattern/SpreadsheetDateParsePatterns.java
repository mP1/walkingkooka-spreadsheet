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
import walkingkooka.convert.Converters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetDateParsePatterns}.
 */
public final class SpreadsheetDateParsePatterns extends SpreadsheetParsePatterns2<SpreadsheetFormatDateParserToken> {
    
     /**
     * Factory that creates a {@link SpreadsheetDateParsePatterns} from the given tokens.
     */
    static SpreadsheetDateParsePatterns withToken(final ParserToken token) {
        final SpreadsheetDateParsePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDateParsePatternsSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetDateParsePatterns(visitor.tokens());
    }

    /**
     * Factory that creates a {@link SpreadsheetDateParsePatterns} from the given tokens.
     */
    static SpreadsheetDateParsePatterns withTokens(final List<SpreadsheetFormatDateParserToken> tokens) {
        check(tokens);

        final SpreadsheetDateParsePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDateParsePatternsSpreadsheetFormatParserTokenVisitor.with();
        tokens.forEach(visitor::startAccept);
        return new SpreadsheetDateParsePatterns(visitor.tokens());
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDateParsePatterns(final List<SpreadsheetFormatDateParserToken> tokens) {
        super(tokens);
    }

    @Override
    public boolean isDate() {
        return true;
    }

    @Override
    public boolean isDateTime() {
        return false;
    }

    @Override
    public boolean isTime() {
        return false;
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDateParsePatterns;
    }

    // HasConverter.....................................................................................................

    @Override
    Converter createDateTimeFormatterConverter(final int i) {
        return Converters.stringLocalDate(this.dateTimeContextDateTimeFormatterFunction(i));
    }

    // HasParser........................................................................................................

    @Override
    Parser<ParserContext> createDateTimeFormatterParser(final int i) {
        return Parsers.localDate(this.dateTimeContextDateTimeFormatterFunction(i));
    }

    private SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunction dateTimeContextDateTimeFormatterFunction(final int i) {
        return SpreadsheetParsePatterns2DateTimeContextDateTimeFormatterFunction.with(this.value().get(i), false);
    }
}
