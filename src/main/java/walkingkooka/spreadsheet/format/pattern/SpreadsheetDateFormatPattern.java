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
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.function.Consumer;

/**
 * Holds a valid {@link SpreadsheetDateFormatPattern}.
 */
public final class SpreadsheetDateFormatPattern extends SpreadsheetFormatPattern {

    /**
     * Factory that creates a {@link ParserToken} parse the given token.
     */
    static SpreadsheetDateFormatPattern with(final ParserToken token) {
        SpreadsheetDateFormatPatternSpreadsheetFormatParserTokenVisitor.with()
            .startAccept(token);

        return new SpreadsheetDateFormatPattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDateFormatPattern(final ParserToken token) {
        super(token);
    }

    @Override
    void missingCondition(final int index,
                          final int total,
                          final SpreadsheetPatternSpreadsheetFormatter formatter,
                          final Consumer<SpreadsheetPatternSpreadsheetFormatter> formatters) {
        formatters.accept(formatter);
    }

    // patterns.........................................................................................................

    @Override
    public List<SpreadsheetDateFormatPattern> patterns() {
        if (null == this.patterns) {
            this.patterns = SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor.patterns(
                this,
                SpreadsheetDateFormatPattern::new
            );
        }
        return this.patterns;
    }

    // remove...........................................................................................................

    @Override
    public SpreadsheetDateFormatPattern removeColor() {
        return this.removeIf(
            COLOR_PREDICATE,
            SpreadsheetDateFormatPattern::new
        );
    }

    // set color........................................................................................................

    @Override
    public SpreadsheetDateFormatPattern setColorName(final SpreadsheetColorName name) {
        return this.setColorName0(
            name,
            SpreadsheetPattern::parseDateFormatPattern
        );
    }

    @Override
    public SpreadsheetPattern setColorNumber(final int colorNumber) {
        return this.setColorNumber0(
            colorNumber,
            SpreadsheetPattern::parseDateFormatPattern
        );
    }

    // removeCondition.................................................................................................

    @Override
    public SpreadsheetDateFormatPattern removeCondition() {
        return this.removeIf(
            CONDITION_PREDICATE,
            SpreadsheetDateFormatPattern::new
        );
    }
}
