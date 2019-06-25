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

import walkingkooka.tree.search.SearchNode;

/**
 * Base class for all spreadsheet symbol parser tokens.
 */
abstract class SpreadsheetSymbolParserToken extends SpreadsheetLeafParserToken<String> {

    SpreadsheetSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    public final boolean isBigDecimal() {
        return false;
    }

    @Override
    public final boolean isBigInteger() {
        return false;
    }

    @Override
    public final boolean isColumnReference() {
        return false;
    }

    @Override
    public final boolean isDouble() {
        return false;
    }

    @Override
    public final boolean isFunctionName() {
        return false;
    }

    @Override
    public final boolean isLabelName() {
        return false;
    }

    @Override
    public final boolean isLocalDate() {
        return false;
    }

    @Override
    public final boolean isLocalDateTime() {
        return false;
    }

    @Override
    public final boolean isLocalTime() {
        return false;
    }

    @Override
    public final boolean isLong() {
        return false;
    }

    @Override
    public final boolean isRowReference() {
        return false;
    }

    @Override
    public final boolean isSymbol() {
        return true;
    }

    @Override
    public final boolean isText() {
        return false;
    }

    @Override
    public final boolean isNoise() {
        return true;
    }

    // HasSearchNode ...............................................................................................

    @Override
    public final SearchNode toSearchNode() {
        return SearchNode.text(this.text(), this.value());
    }
}
