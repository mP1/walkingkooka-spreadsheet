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


import java.util.Optional;

/**
 * Represents a day placeholder.
 */
public final class SpreadsheetFormatDayParserToken extends SpreadsheetFormatNonSymbolParserToken<String> {

    static SpreadsheetFormatDayParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatDayParserToken(value, text);
    }

    private SpreadsheetFormatDayParserToken(final String value, final String text) {
        super(value, text);
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

        switch (this.text().length()) {
            case 1:
                kind = SpreadsheetFormatParserTokenKind.DAY_WITHOUT_LEADING_ZERO;
                break;
            case 2:
                kind = SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO;
                break;
            case 3:
                kind = SpreadsheetFormatParserTokenKind.DAY_NAME_ABBREVIATION;
                break;
            default:
                kind = SpreadsheetFormatParserTokenKind.DAY_NAME_FULL;
                break;
        }

        return kind.asOptional;
    }
}
