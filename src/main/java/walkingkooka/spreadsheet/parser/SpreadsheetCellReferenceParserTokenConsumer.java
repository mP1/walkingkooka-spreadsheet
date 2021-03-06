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

import java.util.function.Consumer;

/**
 * Used to filter the column and row for a {@link SpreadsheetCellReferenceParserToken}.
 */
final class SpreadsheetCellReferenceParserTokenConsumer implements Consumer<SpreadsheetParserToken> {

    static SpreadsheetCellReferenceParserTokenConsumer with() {
        return new SpreadsheetCellReferenceParserTokenConsumer();
    }

    private SpreadsheetCellReferenceParserTokenConsumer() {
        super();
    }

    @Override
    public void accept(final SpreadsheetParserToken token) {
        if (token.isColumnReference()) {
            this.column = token.cast(SpreadsheetColumnReferenceParserToken.class);
        }
        if (token.isRowReference()) {
            this.row = token.cast(SpreadsheetRowReferenceParserToken.class);
        }
    }

    SpreadsheetColumnReferenceParserToken column;
    SpreadsheetRowReferenceParserToken row;
}
