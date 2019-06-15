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

/**
 * Base class for any leaf that holds a numeric value.
 */
abstract class SpreadsheetNumericParserToken<T> extends SpreadsheetNonSymbolParserToken<T> {

    SpreadsheetNumericParserToken(final T value, final String text) {
        super(value, text);
    }

    @Override
    public final boolean isColumnReference() {
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
    public final boolean isRowReference() {
        return false;
    }
}
