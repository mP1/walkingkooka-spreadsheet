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
 * Text literals within a parse number pattern are not required and ignored
 */
final class SpreadsheetNumberParsePatternComponentTextLiteral extends SpreadsheetNumberParsePatternComponent2 {

    static SpreadsheetNumberParsePatternComponentTextLiteral with(final String text) {
        return new SpreadsheetNumberParsePatternComponentTextLiteral(text);
    }

    private SpreadsheetNumberParsePatternComponentTextLiteral(final String text) {
        super();
        this.text = text;
    }

    @Override
    boolean isExpressionCompatible() {
        return false;
    }

    @Override
    boolean parse(final TextCursor cursor,
                  final SpreadsheetNumberParsePatternRequest request) {
        // not consumed
        return request.nextComponent(cursor);
    }

    private final String text;

    @Override
    public String toString() {
        return this.text;
    }
}
