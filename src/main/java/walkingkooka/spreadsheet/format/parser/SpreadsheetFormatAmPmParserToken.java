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
import java.util.function.Predicate;

/**
 * Represents a am/pm placeholder.
 */
public final class SpreadsheetFormatAmPmParserToken extends SpreadsheetFormatNonSymbolParserToken<String> {

    static SpreadsheetFormatAmPmParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatAmPmParserToken(value, text);
    }

    private SpreadsheetFormatAmPmParserToken(final String value, final String text) {
        super(value, text);
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatAmPmParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                           final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetFormatAmPmParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatAmPmParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                      final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetFormatAmPmParserToken.class
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
        SpreadsheetFormatParserTokenKind kind;

        final String text = this.text();
        final boolean lower = Character.isLowerCase(text.charAt(0));

        switch (text.length()) {
            case 3:
                kind = lower ?
                        SpreadsheetFormatParserTokenKind.AMPM_INITIAL_LOWER :
                        SpreadsheetFormatParserTokenKind.AMPM_INITIAL_UPPER;
                break;
            default:
                kind = lower ?
                        SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER :
                        SpreadsheetFormatParserTokenKind.AMPM_FULL_UPPER;
                break;
        }

        return kind.asOptional;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatAmPmParserToken;
    }

}
