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

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.Predicate;

/**
 * A reference that includes a defined name or column and row.
 */
public final class SpreadsheetCellReferenceParserToken extends SpreadsheetParentParserToken
        implements SpreadsheetReferenceParserToken {

    static SpreadsheetCellReferenceParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetCellReferenceParserToken(Lists.immutable(value), checkText(text));
    }

    private SpreadsheetCellReferenceParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        final SpreadsheetCellReferenceParserTokenSpreadsheetParserTokenVisitor visitor = SpreadsheetCellReferenceParserTokenSpreadsheetParserTokenVisitor.with();
        visitor.accept(this);

        final SpreadsheetRowReferenceParserToken row = visitor.row;
        if (null == row) {
            throw new IllegalArgumentException("Row missing from cell=" + text);
        }
        final SpreadsheetColumnReferenceParserToken column = visitor.column;
        if (null == column) {
            throw new IllegalArgumentException("Column missing from cell=" + text);
        }
        this.cell = row.value().setColumn(column.value());
    }

    public SpreadsheetCellReference cell() {
        return this.cell;
    }

    private final SpreadsheetCellReference cell;

    // children.........................................................................................................

    @Override
    public SpreadsheetCellReferenceParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetCellReferenceParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public SpreadsheetCellReferenceParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveFirstIf(
                this,
                predicate,
                SpreadsheetCellReferenceParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetCellReferenceParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                SpreadsheetCellReferenceParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetCellReferenceParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                              final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetCellReferenceParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetCellReferenceParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                         final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetCellReferenceParserToken.class
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetCellReferenceParserToken;
    }
}
