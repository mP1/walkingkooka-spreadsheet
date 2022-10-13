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

package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParentParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTokenVisitor;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetParserTokenVisitor} that handles visiting and updating {@link SpreadsheetCellReferenceParserToken}
 * so cell references after an insert or delete row/column are corrected.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor extends BasicSpreadsheetEngineSpreadsheetParserTokenVisitor {

    /**
     * Accepts a token tree and updates rows and columns.
     */
    static SpreadsheetParserToken expressionFixReferences(final SpreadsheetParserToken token,
                                                          final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor visitor = new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(columnOrRow);
        visitor.accept(token);

        final List<ParserToken> tokens = visitor.children;
        final int count = tokens.size();
        if (1 != count) {
            throw new IllegalStateException("Expected only 1 child but got " + count + "=" + tokens);
        }

        return tokens.get(0).cast(SpreadsheetParserToken.class);
    }

    /**
     * Package private ctor use static method.
     */
    // @VisibleForTesting
    BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        super();
        this.columnOrRow = columnOrRow;
    }

    private final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow;

    // leaf ......................................................................................................

    @Override
    Optional<SpreadsheetColumnReferenceParserToken> visitColumn(final SpreadsheetColumnReferenceParserToken token) {
        return this.columnOrRow.fixCellReferencesWithinExpression(token);
    }

    @Override
    Optional<SpreadsheetRowReferenceParserToken> visitRow(final SpreadsheetRowReferenceParserToken token) {
        return this.columnOrRow.fixCellReferencesWithinExpression(token);
    }

    @Override
    void enter0() {
        this.invalidCellReference = false;
    }

    @Override
    <PP extends SpreadsheetParentParserToken> SpreadsheetParserToken exit0(final PP parent,
                                                                           final List<ParserToken> children,
                                                                           final BiFunction<List<ParserToken>, String, PP> factory) {
        final boolean invalidCellReference = this.invalidCellReference;
        this.invalidCellReference = false;

        return invalidCellReference ?
                REF_ERROR :
                factory.apply(children, ParserToken.text(children));
    }

    /**
     * When true, the parent {@link SpreadsheetParserToken} which should be a {@link SpreadsheetCellReferenceParserToken}
     * will be replaced by a <code>#REF!</code>.
     */
    private boolean invalidCellReference = false;

    private final static SpreadsheetParserToken REF_ERROR = SpreadsheetParserToken.error(
            SpreadsheetError.selectionDeleted(),
            SpreadsheetError.selectionDeleted()
                    .kind()
                    .text()
    );

    @Override
    void leaf(final Optional<? extends SpreadsheetParserToken> token) {
        if (token.isPresent()) {
            this.add(token.get());
        } else {
            this.invalidCellReference = true;
        }
    }

    @Override
    public String toString() {
        return this.children + "," + this.previousChildren;
    }
}
