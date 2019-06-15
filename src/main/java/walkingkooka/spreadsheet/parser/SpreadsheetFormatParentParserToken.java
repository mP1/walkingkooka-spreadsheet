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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Base class for a token that contain another child token.
 */
abstract class SpreadsheetFormatParentParserToken<T extends SpreadsheetFormatParentParserToken> extends SpreadsheetFormatParserToken
        implements ParentParserToken<T> {

    final static List<ParserToken> WITHOUT_COMPUTE_REQUIRED = null;

    SpreadsheetFormatParentParserToken(final List<ParserToken> value, final String text, final List<ParserToken> valueWithout) {
        super(text);
        this.value = value;
        this.without = value.equals(valueWithout) ?
                Optional.of(this) :
                computeWithout(value);
    }

    private Optional<SpreadsheetFormatParserToken> computeWithout(final List<ParserToken> value) {
        final List<ParserToken> without = filterWithout(value);

        return Optional.of(value.size() == without.size() ?
                this :
                this.replace(without, this.text(), without));
    }

    private static List<ParserToken> filterWithout(final List<ParserToken> value) {
        return value.stream()
                .filter(t -> t instanceof SpreadsheetFormatParserToken && !t.isNoise())
                .collect(Collectors.toList());
    }

    @Override
    public final Optional<SpreadsheetFormatParserToken> withoutSymbols() {
        return this.without;
    }

    private final Optional<SpreadsheetFormatParserToken> without;

    @Override
    public final List<ParserToken> value() {
        return this.value;
    }

    final SpreadsheetFormatParentParserToken setValue0(final List<ParserToken> tokens) {
        final List<ParserToken> copy = copyAndCheckTokens(tokens);

        return this.value().equals(copy) ?
                this :
                this.replace(copy, this.text(), filterWithout(copy));
    }

    final List<ParserToken> value;

    /**
     * Factory that creates a new {@link SpreadsheetFormatParentParserToken} with the same text but new tokens.
     */
    abstract SpreadsheetFormatParentParserToken replace(final List<ParserToken> tokens, final String text, final List<ParserToken> without);

    // isXXX............................................................................................................

    @Override
    public final boolean isAmPm() {
        return false;
    }

    @Override
    public final boolean isBracketCloseSymbol() {
        return false;
    }

    @Override
    public final boolean isBracketOpenSymbol() {
        return false;
    }

    @Override
    public final boolean isColorLiteralSymbol() {
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
    public final boolean isEqualsSymbol() {
        return false;
    }

    @Override
    public final boolean isEscape() {
        return false;
    }

    @Override
    public final boolean isExponentSymbol() {
        return false;
    }

    @Override
    public final boolean isFractionSymbol() {
        return false;
    }

    @Override
    public final boolean isGeneralSymbol() {
        return false;
    }

    @Override
    public final boolean isGreaterThanSymbol() {
        return false;
    }

    @Override
    public final boolean isGreaterThanEqualsSymbol() {
        return false;
    }

    @Override
    public final boolean isHour() {
        return false;
    }

    @Override
    public final boolean isLessThanSymbol() {
        return false;
    }

    @Override
    public final boolean isLessThanEqualsSymbol() {
        return false;
    }

    @Override
    public final boolean isMonthOrMinute() {
        return false;
    }

    @Override
    public final boolean isNotEqualsSymbol() {
        return false;
    }

    @Override
    public final boolean isPercentSymbol() {
        return false;
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
    public final boolean isSeparatorSymbol() {
        return false;
    }

    @Override
    public final boolean isStar() {
        return false;
    }

    @Override
    public final boolean isSymbol() {
        return false;
    }

    @Override
    public final boolean isTextLiteral() {
        return false;
    }

    @Override
    public final boolean isThousands() {
        return false;
    }

    @Override
    public final boolean isTextPlaceholder() {
        return false;
    }

    @Override
    public final boolean isUnderscore() {
        return false;
    }

    @Override
    public final boolean isWhitespace() {
        return false;
    }

    @Override
    public final boolean isYear() {
        return false;
    }

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    final void acceptValues(final SpreadsheetFormatParserTokenVisitor visitor) {
        for (ParserToken token : this.value()) {
            visitor.accept(token);
        }
    }
}
