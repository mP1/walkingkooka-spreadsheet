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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.text.cursor.parser.ParserToken;

/**
 * Holds a valid {@link SpreadsheetPattern} to format {@link String text}.
 */
public final class SpreadsheetTextFormatPattern extends SpreadsheetFormatPattern {

    /**
     * Factory that creates a {@link SpreadsheetTextFormatPattern} from the given token.
     */
    static SpreadsheetTextFormatPattern with(final ParserToken token) {
        SpreadsheetTextFormatPatternSpreadsheetFormatParserTokenVisitor.with()
                .startAccept(token);

        return new SpreadsheetTextFormatPattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetTextFormatPattern(final ParserToken token) {
        super(token);
    }

    // TreePrintable....................................................................................................

    @Override
    String printTreeTypeName() {
        return "text-format-pattern";
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetTextFormatPattern;
    }
}
