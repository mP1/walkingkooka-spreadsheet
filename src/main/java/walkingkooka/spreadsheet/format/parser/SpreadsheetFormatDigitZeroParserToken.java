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
package walkingkooka.spreadsheet.format.parser;


import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents the digit including leading zeros.
 */
public final class SpreadsheetFormatDigitZeroParserToken extends SpreadsheetFormatNonSymbolParserToken<String> {

    static SpreadsheetFormatDigitZeroParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatDigitZeroParserToken(value, text);
    }

    private SpreadsheetFormatDigitZeroParserToken(final String value, final String text) {
        super(value, text);
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatDigitZeroParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetFormatDigitZeroParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatDigitZeroParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                           final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetFormatDigitZeroParserToken.class
        );
    }

    // visitor........................................................................................................

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetFormatParserTokenKind ................................................................................

    @Override
    public Optional<SpreadsheetFormatParserTokenKind> kind() {
        return SpreadsheetFormatParserTokenKind.DIGIT_ZERO.asOptional;
    }

    // equals ..........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatDigitZeroParserToken;
    }

}
