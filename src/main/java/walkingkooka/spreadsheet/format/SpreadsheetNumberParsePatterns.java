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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetNumberParsePatterns}.
 */
public final class SpreadsheetNumberParsePatterns extends SpreadsheetParsePatterns<SpreadsheetFormatNumberParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePatterns} from the given tokens.
     */
    static SpreadsheetNumberParsePatterns withToken(final ParserToken token) {
        final SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetNumberParsePatterns(visitor.tokens(), visitor.patterns);
    }

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePatterns} from the given tokens.
     */
    static SpreadsheetNumberParsePatterns withTokens(final List<SpreadsheetFormatNumberParserToken> tokens) {
        check(tokens);

        final SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetNumberParsePatternsSpreadsheetFormatParserTokenVisitor.with();
        tokens.forEach(visitor::startAccept);
        return new SpreadsheetNumberParsePatterns(visitor.tokens(), visitor.patterns);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetNumberParsePatterns(final List<SpreadsheetFormatNumberParserToken> tokens,
                                           final List<List<SpreadsheetNumberParsePatternsComponent>> patterns) {
        super(tokens);
        this.patterns = patterns;
    }

    @Override
    public boolean isDate() {
        return false;
    }

    @Override
    public boolean isDateTime() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public boolean isTime() {
        return false;
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetNumberParsePatterns;
    }

    // HasConverter.....................................................................................................

    @Override
    SpreadsheetNumberParsePatternsConverter createConverter() {
        return SpreadsheetNumberParsePatternsConverter.with(this);
    }

    // HasParser........................................................................................................

    @Override
    Parser<ParserContext> createParser() {
        throw new UnsupportedOperationException();
    }

    /**
     * The outer {@link List} contains an element for each pattern, with the inner {@link List} containing the components.
     */
    final List<List<SpreadsheetNumberParsePatternsComponent>> patterns;
}
