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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGeneralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNotEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTimeParserToken;
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
    protected Visiting startVisit(final SpreadsheetFormatColorParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateTimeParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatEqualsParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGeneralParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTextParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        this.addToken(token);
        return Visiting.SKIP;
    }

    @Override
    protected void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
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
