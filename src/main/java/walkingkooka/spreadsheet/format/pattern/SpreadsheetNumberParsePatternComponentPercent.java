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

import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;

/**
 * A {@link SpreadsheetNumberParsePatternComponent} that matches one or more percent symbol(s).
 * Each percent symbol that appears within a numerical value will scale the value by 100%.
 */
final class SpreadsheetNumberParsePatternComponentPercent extends SpreadsheetNumberParsePatternComponentNonDigit {

    /**
     * Singleton
     */
    final static SpreadsheetNumberParsePatternComponentPercent INSTANCE = new SpreadsheetNumberParsePatternComponentPercent();

    private SpreadsheetNumberParsePatternComponentPercent() {
        super();
    }

    @Override
    boolean isExpressionCompatible() {
        return true;
    }

    @Override
    boolean parse(final TextCursor cursor,
                  final SpreadsheetNumberParsePatternRequest request) {
        boolean completed = false;

        if(cursor.isNotEmpty()) {
            final char percentSymbol = request.context.percentSymbol();
            if(cursor.at() == percentSymbol) {
                final TextCursorSavePoint save = cursor.save();
                cursor.next();

                while(cursor.isNotEmpty()) {
                    if(cursor.at() != percentSymbol) {
                        break;
                    }

                    cursor.next();
                }

                final String text = save.textBetween()
                    .toString();
                request.add(
                    SpreadsheetFormulaParserToken.percentSymbol(
                        text,
                        text
                    )
                );

                completed = request.nextComponent(cursor);
            }
        }

        return completed;
    }

    @Override
    public String toString() {
        return "%";
    }
}
