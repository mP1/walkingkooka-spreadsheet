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
package walkingkooka.spreadsheet.parser;

import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

/**
 * Base class for any token with two parameters.
 */
abstract class SpreadsheetBinaryParserToken<T extends SpreadsheetBinaryParserToken> extends SpreadsheetParentParserToken<T> {

    SpreadsheetBinaryParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        final List<ParserToken> without = ParentParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (2 != count) {
            throw new IllegalArgumentException("Expected 2 tokens but got " + count + "=" + without);
        }
        this.left = without.get(0).cast(SpreadsheetParserToken.class);
        this.right = without.get(1).cast(SpreadsheetParserToken.class);
    }

    /**
     * Returns the left parameter.
     */
    public final SpreadsheetParserToken left() {
        return this.left;
    }

    private final SpreadsheetParserToken left;

    /**
     * Returns the right parameter.
     */
    public final SpreadsheetParserToken right() {
        return this.right;
    }

    private final SpreadsheetParserToken right;
}
