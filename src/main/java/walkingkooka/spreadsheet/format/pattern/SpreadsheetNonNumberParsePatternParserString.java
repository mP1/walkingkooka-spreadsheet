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

import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link Parser} that handles partial matches parse a list taken parse the {@link SpreadsheetParserContext}.
 */
final class SpreadsheetNonNumberParsePatternParserString extends SpreadsheetNonNumberParsePatternParser {

    static SpreadsheetNonNumberParsePatternParserString with(final Function<SpreadsheetParserContext, List<String>> values,
                                                             final BiFunction<Integer, String, SpreadsheetFormulaParserToken> tokenFactory,
                                                             final String pattern) {
        return new SpreadsheetNonNumberParsePatternParserString(
            values,
            tokenFactory,
            pattern
        );
    }

    private SpreadsheetNonNumberParsePatternParserString(final Function<SpreadsheetParserContext, List<String>> values,
                                                         final BiFunction<Integer, String, SpreadsheetFormulaParserToken> tokenFactory,
                                                         final String pattern) {
        super();
        this.values = values;
        this.tokenFactory = tokenFactory;
        this.pattern = pattern;
    }

    @Override
    SpreadsheetFormulaParserToken parseNotEmpty0(final TextCursor cursor,
                                                 final SpreadsheetParserContext context,
                                                 final TextCursorSavePoint start) {
        SpreadsheetFormulaParserToken token = null;

        final List<String> list = this.values.apply(context);
        int count = list.size();
        final String[] values = list.toArray(new String[count]);

        int i = 0;

        Exit:
//
        for (; ; ) {
            final char c = cursor.at();

            int candidates = 0;
            int choice = -1;
            String choiceText = null;

            for (int j = 0; j < values.length; j++) {
                final String possible = values[j];

                if (null != possible) {
                    if (i < possible.length() && isEqual(possible.charAt(i), c)) {
                        candidates++;
                        if (1 == candidates) {
                            choice = j;
                            choiceText = possible;
                        }
                    } else {
                        values[j] = null; // no match ignore
                    }
                }
            }

            i++;
            switch (candidates) {
                case 0:
                    break Exit;
                case 1:
                    cursor.next();
                    TextCursorSavePoint save = cursor.save();

                    for (; ; ) {
                        if (cursor.isEmpty() || i == choiceText.length()) { // lgtm [java/dereferenced-value-may-be-null]
                            token = this.token(choice, start);
                            break Exit;
                        }
                        if (!isEqual(choiceText.charAt(i), cursor.at())) {
                            save.restore();
                            token = this.token(choice, start);
                            break Exit;
                        }
                        save = cursor.save();
                        cursor.next();
                        i++;
                    }
                default:
                    cursor.next();
                    if (cursor.isEmpty()) {
                        break Exit;
                    }
                    // keep trying remaining candidates
                    break;
            }
        }

        return token;
    }

    private static boolean isEqual(final char c, final char d) {
        return SpreadsheetStrings.CASE_SENSITIVITY.isEqual(c, d);
    }

    /**
     * This provides the list of month names, month names abbreviations or am/pms.
     */
    private final Function<SpreadsheetParserContext, List<String>> values;

    private SpreadsheetFormulaParserToken token(final int choice,
                                                final TextCursorSavePoint start) {
        return this.tokenFactory.apply(
            choice,
            start.textBetween().toString()
        );
    }

    /**
     * Factory that creates the {@link SpreadsheetFormulaParserToken}. This is typically a method-reference to a static
     * {@link SpreadsheetFormulaParserToken} factory method.
     */
    private final BiFunction<Integer, String, SpreadsheetFormulaParserToken> tokenFactory;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SpreadsheetNonNumberParsePatternParserString && this.equals0((SpreadsheetNonNumberParsePatternParserString) other);
    }

    private boolean equals0(final SpreadsheetNonNumberParsePatternParserString other) {
        return this.pattern.equals(other.pattern);
    }

    @Override
    public String toString() {
        return this.pattern;
    }

    private final String pattern;
}
