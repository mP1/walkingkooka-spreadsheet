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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetDatePatterns}.
 */
public final class SpreadsheetDatePatterns extends SpreadsheetPatterns2<SpreadsheetFormatDateParserToken> {
    
     /**
     * Factory that creates a {@link SpreadsheetDatePatterns} from the given tokens.
     */
    static SpreadsheetDatePatterns withToken(final ParserToken token) {
        final SpreadsheetDatePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDatePatternsSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetDatePatterns(visitor.tokens());
    }

    /**
     * Factory that creates a {@link SpreadsheetDatePatterns} from the given tokens.
     */
    static SpreadsheetDatePatterns withTokens(final List<SpreadsheetFormatDateParserToken> tokens) {
        check(tokens);

        final SpreadsheetDatePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDatePatternsSpreadsheetFormatParserTokenVisitor.with();
        tokens.forEach(visitor::startAccept);
        return new SpreadsheetDatePatterns(visitor.tokens());
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDatePatterns(final List<SpreadsheetFormatDateParserToken> tokens) {
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
        return other instanceof SpreadsheetDatePatterns;
    }

    // HasParser........................................................................................................

    @Override
    Parser<ParserContext> createDateTimeFormatterParser(final int i) {
        return Parsers.localDate(SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction.with(this.value().get(i),
                false)); // AMPM=false
    }
}
