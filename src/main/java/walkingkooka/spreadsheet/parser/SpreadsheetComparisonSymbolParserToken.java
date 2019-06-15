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
 * Base class for all binary operand symbols.
 */
abstract class SpreadsheetComparisonSymbolParserToken extends SpreadsheetBinaryOperandSymbolParserToken {

    SpreadsheetComparisonSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    public final boolean isBetweenSymbol() {
        return false;
    }

    @Override
    public final boolean isDivideSymbol() {
        return false;
    }

    @Override
    public final boolean isMinusSymbol() {
        return false;
    }

    @Override
    public final boolean isMultiplySymbol() {
        return false;
    }

    @Override
    public final boolean isPlusSymbol() {
        return false;
    }

    @Override
    public final boolean isPowerSymbol() {
        return false;
    }

    @Override
    final int operatorPriority() {
        return GREATER_THAN_LESS_THAN_PRIORITY;
    }
}
