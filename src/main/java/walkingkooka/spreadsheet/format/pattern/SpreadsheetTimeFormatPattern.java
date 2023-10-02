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

import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.function.Consumer;

/**
 * Holds a valid {@link SpreadsheetTimeFormatPattern}.
 */
public final class SpreadsheetTimeFormatPattern extends SpreadsheetFormatPattern {

    /**
     * Factory that creates a {@link ParserToken} parse the given token.
     */
    static SpreadsheetTimeFormatPattern with(final ParserToken token) {
        SpreadsheetTimeFormatPatternSpreadsheetFormatParserTokenVisitor.with().startAccept(token);

        return new SpreadsheetTimeFormatPattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTimeFormatPattern(final ParserToken token) {
        super(token);
    }

    @Override
    void missingCondition(final int index,
                          final int total,
                          final SpreadsheetFormatter formatter,
                          final Consumer<SpreadsheetFormatter> formatters) {
        formatters.accept(formatter);
    }

    // patterns.........................................................................................................

    @Override
    public List<SpreadsheetTimeFormatPattern> patterns() {
        if (null == this.patterns) {
            this.patterns = SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor.patterns(
                    this,
                    SpreadsheetTimeFormatPattern::new
            );
        }

        return this.patterns;
    }

    // remove...........................................................................................................

    @Override
    public SpreadsheetTimeFormatPattern removeColor() {
        return this.removeIf0(
                COLOR_PREDICATE,
                SpreadsheetTimeFormatPattern::new
        );
    }

    // set color........................................................................................................

    @Override
    public SpreadsheetTimeFormatPattern setColorName(final SpreadsheetColorName name) {
        return this.setColorName0(
                name,
                SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Override
    public SpreadsheetTimeFormatPattern setColorNumber(final int colorNumber) {
        return this.setColorNumber0(
                colorNumber,
                SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    // removeCondition..................................................................................................

    @Override
    public SpreadsheetTimeFormatPattern removeCondition() {
        return this.removeIf0(
                CONDITION_PREDICATE,
                SpreadsheetTimeFormatPattern::new
        );
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTimeFormatPattern;
    }
}
