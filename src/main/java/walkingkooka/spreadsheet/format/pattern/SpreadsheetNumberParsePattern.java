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

import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionNumber;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetNumberParsePattern}.
 */
public final class SpreadsheetNumberParsePattern extends SpreadsheetParsePattern {

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePattern} parse the given tokens.
     */
    static SpreadsheetNumberParsePattern with(final ParserToken token) {
        return new SpreadsheetNumberParsePattern(
                token,
                SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor.patterns(token)
        );
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetNumberParsePattern(final ParserToken token,
                                          final List<List<SpreadsheetNumberParsePatternComponent>> patterns) {
        super(token);
        this.patternComponents = patterns;
    }

    // parse............................................................................................................

    /**
     * Tries to parse the given {@link String text} into a {@link ExpressionNumber} or throw.
     */
    @Override
    public ExpressionNumber parse(final String text,
                                  final SpreadsheetParserContext context) {
        return this.parser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(text), context)
                .get()
                .cast(SpreadsheetNumberParserToken.class)
                .toNumber(context);
    }

    // toFormat.........................................................................................................

    @Override
    public SpreadsheetNumberFormatPattern toFormat() {
        return SpreadsheetPattern.numberFormatPattern(this.value());
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetNumberParsePattern;
    }

    // HasConverter.....................................................................................................

    @Override
    SpreadsheetNumberParsePatternConverter createConverter() {
        return SpreadsheetNumberParsePatternConverter.with(this);
    }

    // HasParser........................................................................................................

    @Override
    SpreadsheetNumberParsePatternParser createParser() {
        return SpreadsheetNumberParsePatternParser.with(this, SpreadsheetNumberParsePatternMode.VALUE);
    }

    /**
     * Returns a {@link Parser} which will try all the patterns.
     */
    public Parser<SpreadsheetParserContext> expressionParser() {
        if (null == this.expressionParser) {
            this.expressionParser = this.createExpressionParser();
        }
        return this.expressionParser;
    }

    private Parser<SpreadsheetParserContext> expressionParser;

    private SpreadsheetNumberParsePatternParser createExpressionParser() {
        return SpreadsheetNumberParsePatternParser.with(this, SpreadsheetNumberParsePatternMode.EXPRESSION);
    }

    /**
     * The outer {@link List} contains an element for each pattern, with the inner {@link List} containing the components.
     */
    final List<List<SpreadsheetNumberParsePatternComponent>> patternComponents;

    // patterns.........................................................................................................

    @Override
    public List<SpreadsheetNumberParsePattern> patterns() {
        if (null == this.patterns) {
            this.patterns = SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor.patterns(
                    this,
                    SpreadsheetNumberParsePattern::with
            );
        }

        return this.patterns;
    }

    // removeColor......................................................................................................

    @Override
    public SpreadsheetNumberParsePattern removeColor() {
        return this;
    }

    // removeCondition..................................................................................................

    @Override
    public SpreadsheetNumberParsePattern removeCondition() {
        return this;
    }
}
