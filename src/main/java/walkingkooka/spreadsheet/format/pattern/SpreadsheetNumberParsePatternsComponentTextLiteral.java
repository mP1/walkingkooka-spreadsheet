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

import walkingkooka.text.cursor.TextCursor;

/**
 * A component within a number that requires each and every character in the given text to be matched with case being important.
 */
final class SpreadsheetNumberParsePatternsComponentTextLiteral extends SpreadsheetNumberParsePatternsComponent2 {

    static SpreadsheetNumberParsePatternsComponentTextLiteral with(final String text) {
        return new SpreadsheetNumberParsePatternsComponentTextLiteral(text);
    }

    private SpreadsheetNumberParsePatternsComponentTextLiteral(final String text) {
        super();
        this.text = text;
    }

    @Override
    void parse(final TextCursor cursor,
               final SpreadsheetNumberParsePatternsContext context) {
        this.parseToken(cursor, this.text, context);
    }

    private final String text;

    @Override
    public String toString() {
        return this.text;
    }
}
