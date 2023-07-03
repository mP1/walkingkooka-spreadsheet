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
 * Holds a valid {@link SpreadsheetDateTimeFormatPattern}.
 */
public final class SpreadsheetDateTimeFormatPattern extends SpreadsheetFormatPattern {

    /**
     * Factory that creates a {@link ParserToken} from the given token.
     */
    static SpreadsheetDateTimeFormatPattern with(final ParserToken token) {
        SpreadsheetDateTimeFormatPatternSpreadsheetFormatParserTokenVisitor.with()
                .startAccept(token);

        return new SpreadsheetDateTimeFormatPattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDateTimeFormatPattern(final ParserToken token) {
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
    public List<SpreadsheetDateTimeFormatPattern> patterns() {
        if (null == this.patterns) {
            this.patterns = SpreadsheetPatternPatternsSpreadsheetFormatParserTokenVisitor.patterns(
                    this,
                    SpreadsheetDateTimeFormatPattern::new
            );
        }
        return this.patterns;
    }

    // remove...........................................................................................................

    @Override
    public SpreadsheetDateTimeFormatPattern removeColor() {
        return this.removeIf0(
                COLOR_PREDICATE,
                SpreadsheetDateTimeFormatPattern::new
        );
    }

    // set color........................................................................................................

    @Override
    public SpreadsheetDateTimeFormatPattern setColorName(final SpreadsheetColorName name) {
        return this.setColorName0(
                name,
                SpreadsheetPattern::parseDateTimeFormatPattern
        );
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDateTimeFormatPattern;
    }
}
