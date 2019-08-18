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

package walkingkooka.spreadsheet.format;

import walkingkooka.text.cursor.TextCursor;

/**
 * A {@link SpreadsheetNumberParsePatternsComponent} that matches any whitespace character.
 */
final class SpreadsheetNumberParsePatternsComponentWhitespace extends SpreadsheetNumberParsePatternsComponent2 {

    /**
     * Singleton
     */
    final static SpreadsheetNumberParsePatternsComponentWhitespace INSTANCE = new SpreadsheetNumberParsePatternsComponentWhitespace();

    private SpreadsheetNumberParsePatternsComponentWhitespace() {
        super();
    }

    @Override
    void parse(final TextCursor cursor,
               final SpreadsheetNumberParsePatternsContext context) {
        if (false == cursor.isEmpty()) {
            if (Character.isWhitespace(cursor.at())) {
                cursor.next();
                context.nextComponent(cursor);
            }
        }
    }

    @Override
    public String toString() {
        return " ";
    }
}
