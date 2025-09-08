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

import walkingkooka.Context;
import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.formula.parser.DigitsSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Iterator;
import java.util.List;

/**
 * The {@link Context} which accompanies the parsing process and builds up the {@link Number}.
 */
final class SpreadsheetNumberParsePatternRequest {

    static SpreadsheetNumberParsePatternRequest with(final Iterator<SpreadsheetNumberParsePatternComponent> next,
                                                     final SpreadsheetNumberParsePatternMode mode,
                                                     final DecimalNumberContext context) {
        return new SpreadsheetNumberParsePatternRequest(
            next,
            mode,
            context
        );
    }

    private SpreadsheetNumberParsePatternRequest(final Iterator<SpreadsheetNumberParsePatternComponent> next,
                                                 final SpreadsheetNumberParsePatternMode mode,
                                                 final DecimalNumberContext context) {
        super();

        this.next = next;
        this.mode = mode;
        this.context = context;
    }

    final SpreadsheetNumberParsePatternMode mode;

    void setDigitMode(final SpreadsheetNumberParsePatternComponentDigitMode digitMode) {
        this.digitMode = digitMode;
    }

    /**
     * This will provide the source for a few locale sensitive characters such as the decimal point.
     */
    final DecimalNumberContext context;

    void add(final SpreadsheetFormulaParserToken token) {
        this.addNumberIfNecessary();
        this.add0(token);
    }

    private void add0(final SpreadsheetFormulaParserToken token) {
        this.tokens.add(token);

        this.digits.setLength(0); // StringBuilder is not shared, ok to reuse
    }

    /**
     * The visitor will create the individual tokens during the parsing process.
     */
    final List<ParserToken> tokens = Lists.array();

    boolean addNumberIfNecessary() {
        final StringBuilder digits = this.digits;
        final boolean added = digits.length() > 0;
        if (added) {
            final String text = digits.toString();
            this.add0(
                SpreadsheetFormulaParserToken.digits(
                    text,
                    text
                )
            );
        }
        return added;
    }

    /**
     * Aggregates the digit characters, which may include group separator separators.
     * This will become the text of any created {@link DigitsSpreadsheetFormulaParserToken}.
     */
    final StringBuilder digits = new StringBuilder();
    //
    /**
     * Controls what part of the number the next digit belongs.
     */
    SpreadsheetNumberParsePatternComponentDigitMode digitMode = SpreadsheetNumberParsePatternComponentDigitMode.INTEGER_OR_SIGN;

    /**
     * Calls the nextComponent component if one exists.
     */
    boolean nextComponent(final TextCursor cursor) {
        return !this.next.hasNext() || this.next.next()
            .parse(cursor, this); // finished!
    }

    /**
     * An {@link Iterator} which contains the next component, when empty the end of the text has been reached.
     */
    final Iterator<SpreadsheetNumberParsePatternComponent> next;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .label("context").value(this.context)
            .label("digitMode").value(this.digitMode)
            .label("digits").value(this.digits)
            .label("tokens").value(this.tokens)
            .build();
    }
}
