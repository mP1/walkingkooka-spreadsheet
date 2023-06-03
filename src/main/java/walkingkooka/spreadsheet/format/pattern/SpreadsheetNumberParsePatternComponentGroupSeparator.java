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
 * A {@link SpreadsheetNumberParsePatternComponent} that does not actually expect or consume the group separator character.
 */
final class SpreadsheetNumberParsePatternComponentGroupSeparator extends SpreadsheetNumberParsePatternComponentNonDigit {

    /**
     * Singleton
     */
    final static SpreadsheetNumberParsePatternComponentGroupSeparator INSTANCE = new SpreadsheetNumberParsePatternComponentGroupSeparator();

    private SpreadsheetNumberParsePatternComponentGroupSeparator() {
        super();
    }

    @Override
    boolean isExpressionCompatible() {
        return false;
    }

    @Override
    boolean parse(final TextCursor cursor,
                  final SpreadsheetNumberParsePatternRequest request) {
        return request.nextComponent(cursor);
    }

    @Override
    public String toString() {
        return ",";
    }
}
