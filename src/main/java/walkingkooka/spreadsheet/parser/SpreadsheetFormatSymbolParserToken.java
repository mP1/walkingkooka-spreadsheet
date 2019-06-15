/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */
package walkingkooka.spreadsheet.parser;

import walkingkooka.tree.search.SearchNode;

import java.util.Optional;

/**
 * Base class for all spreadsheet format symbol parser tokens.
 */
abstract class SpreadsheetFormatSymbolParserToken extends SpreadsheetFormatLeafParserToken<String> {

    SpreadsheetFormatSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    public final Optional<SpreadsheetFormatParserToken> withoutSymbols() {
        return Optional.empty();
    }

    // is...............................................................................................................

    @Override
    public final boolean isAmPm() {
        return false;
    }

    @Override
    public final boolean isColorName() {
        return false;
    }

    @Override
    public final boolean isColorNumber() {
        return false;
    }

    @Override
    public final boolean isConditionNumber() {
        return false;
    }

    @Override
    public final boolean isCurrency() {
        return false;
    }

    @Override
    public final boolean isDay() {
        return false;
    }

    @Override
    public final boolean isDecimalPoint() {
        return false;
    }

    @Override
    public final boolean isDigit() {
        return false;
    }

    @Override
    public final boolean isDigitLeadingSpace() {
        return false;
    }

    @Override
    public final boolean isDigitLeadingZero() {
        return false;
    }

    @Override
    public final boolean isEscape() {
        return false;
    }

    @Override
    public final boolean isHour() {
        return false;
    }

    @Override
    public final boolean isMonthOrMinute() {
        return false;
    }

    @Override
    public final boolean isNoise() {
        return true;
    }

    @Override
    public final boolean isQuotedText() {
        return false;
    }

    @Override
    public final boolean isSecond() {
        return false;
    }

    @Override
    public final boolean isSymbol() {
        return true;
    }

    @Override
    public final boolean isStar() {
        return false;
    }

    @Override
    public final boolean isTextLiteral() {
        return false;
    }

    @Override
    public final boolean isTextPlaceholder() {
        return false;
    }

    @Override
    public final boolean isThousands() {
        return false;
    }

    @Override
    public final boolean isUnderscore() {
        return false;
    }

    @Override
    public final boolean isYear() {
        return false;
    }

    // HasSearchNode ...............................................................................................

    @Override
    public SearchNode toSearchNode() {
        return SearchNode.text(this.text(), this.value());
    }
}
