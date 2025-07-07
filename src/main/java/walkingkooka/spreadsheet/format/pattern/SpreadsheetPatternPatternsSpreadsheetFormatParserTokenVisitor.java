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
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.DateTimeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GeneralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NotEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SeparatorSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.TextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TimeSpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.Function;

/**
 * A visitor that is used to extract individual patterns within a larger pattern.
 */
final class SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor<T extends SpreadsheetPattern> extends SpreadsheetFormatParserTokenVisitor {

    static <T extends SpreadsheetPattern> List<T> patterns(final T parent,
                                                           final Function<ParserToken, T> patternFactory) {
        final SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor<T> visitor = new SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor<>(patternFactory);
        visitor.accept(parent.value());
        visitor.createAndSavePatternIfNecessary();

        final List<T> patterns = visitor.patterns;
        return patterns.size() == 1 && patterns.get(0).equals(parent) ?
            Lists.of(parent) :
            patterns;
    }

    // VisibilityForTesting
    SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor(final Function<ParserToken, T> patternFactory) {
        this.patternFactory = patternFactory;
    }

    @Override
    protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final GeneralSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected void visit(final SeparatorSymbolSpreadsheetFormatParserToken token) {
        this.createAndSavePatternIfNecessary();
    }

    private void createAndSavePatternIfNecessary() {
        final List<ParserToken> tokens = this.pattern;
        if (null != tokens && false == tokens.isEmpty()) {
            final T pattern = this.patternFactory.apply(
                ParserTokens.sequence(
                    tokens,
                    ParserToken.text(tokens)
                )
            );
            pattern.patterns = Lists.of(pattern);
            this.patterns.add(pattern);
            this.pattern = null;
        }
    }

    /**
     * Factory that creates a new {@link SpreadsheetPattern} for each sub pattern between separators.
     */
    private final Function<ParserToken, T> patternFactory;

    private void addToken(final ParserToken token) {
        List<ParserToken> pattern = this.pattern;
        if (null == pattern) {
            pattern = Lists.array();
            this.pattern = pattern;
        }
        pattern.add(token);
    }

    /**
     * Collects all the tokens for a single pattern.
     */
    private List<ParserToken> pattern;

    /**
     * Aggregates all patterns.
     */
    private final List<T> patterns = Lists.array();

    @Override
    public String toString() {
        return this.patterns.toString();
    }
}
