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
package walkingkooka.spreadsheet.formula;

import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

/**
 * Base class for any token with two parameters.
 */
abstract public class BinarySpreadsheetFormulaParserToken extends ParentSpreadsheetFormulaParserToken {

    BinarySpreadsheetFormulaParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        final List<ParserToken> without = ParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (2 != count) {
            throw new IllegalArgumentException("Expected 2 tokens but got " + count + "=" + without);
        }
        this.left = without.get(0).cast(SpreadsheetFormulaParserToken.class);
        this.right = without.get(1).cast(SpreadsheetFormulaParserToken.class);
    }

    /**
     * Returns the left parameter.
     */
    public final SpreadsheetFormulaParserToken left() {
        return this.left;
    }

    private final SpreadsheetFormulaParserToken left;

    /**
     * Returns the right parameter.
     */
    public final SpreadsheetFormulaParserToken right() {
        return this.right;
    }

    private final SpreadsheetFormulaParserToken right;
}
