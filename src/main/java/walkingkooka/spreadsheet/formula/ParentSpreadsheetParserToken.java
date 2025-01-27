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

import walkingkooka.Value;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

/**
 * Base class for a parent token that contain one more child {@link SpreadsheetParserToken tokens}.
 */
abstract public class ParentSpreadsheetParserToken extends SpreadsheetParserToken
        implements Value<List<ParserToken>> {

    ParentSpreadsheetParserToken(final List<ParserToken> value, final String text) {
        super(text);
        this.value = value;
    }

    @Override
    public final List<ParserToken> value() {
        return this.value;
    }

    private final List<ParserToken> value;

    @Override
    final int operatorPriority() {
        return IGNORED;
    }

    @Override
    final SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        throw new UnsupportedOperationException();
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    final void acceptValues(final SpreadsheetParserTokenVisitor visitor) {
        for (ParserToken token : this.value()) {
            visitor.accept(token);
        }
    }
}
