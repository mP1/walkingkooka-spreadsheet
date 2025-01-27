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

import java.util.Objects;

/**
 * Represents the seconds within a time or date/time.
 */
public final class SecondsSpreadsheetParserToken extends NonSymbolSpreadsheetParserToken<Integer> {

    static SecondsSpreadsheetParserToken with(final int value, final String text) {
        Objects.requireNonNull(text, "text");

        return new SecondsSpreadsheetParserToken(value, text);
    }

    private SecondsSpreadsheetParserToken(final int value, final String text) {
        super(value, text);
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }
}
