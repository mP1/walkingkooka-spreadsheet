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

/**
 * A reference that includes a defined name or column and row.
 */
public final class SpreadsheetCellReferenceParserToken extends SpreadsheetParentParserToken<SpreadsheetCellReferenceParserToken>
        implements SpreadsheetReferenceParserToken {

    static SpreadsheetCellReferenceParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetCellReferenceParserToken(Lists.immutable(value), checkText(text));
    }

    private SpreadsheetCellReferenceParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        final SpreadsheetCellReferenceParserTokenConsumer checker = SpreadsheetCellReferenceParserTokenConsumer.with();
        value.stream()
                .filter(t -> t instanceof SpreadsheetParserToken)
                .map(t -> (SpreadsheetParserToken) t)
                .forEach(checker);
        final SpreadsheetRowReferenceParserToken row = checker.row;
        if (null == row) {
            throw new IllegalArgumentException("Row missing from cell=" + text);
        }
        final SpreadsheetColumnReferenceParserToken column = checker.column;
        if (null == column) {
            throw new IllegalArgumentException("Column missing from cell=" + text);
        }
        this.cell = row.value().setColumn(column.value());
    }

    public SpreadsheetCellReference cell() {
        return this.cell;
    }

    private final SpreadsheetCellReference cell;

    // isXXX............................................................................................................

    @Override
    public boolean isAddition() {
        return false;
    }

    @Override
    public boolean isCellReference() {
        return true;
    }

    @Override
    public boolean isDivision() {
        return false;
    }

    @Override
    public boolean isEquals() {
        return false;
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isGreaterThan() {
        return false;
    }

    @Override
    public boolean isGreaterThanEquals() {
        return false;
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    @Override
    public boolean isLessThan() {
        return false;
    }

    @Override
    public boolean isLessThanEquals() {
        return false;
    }

    @Override
    public boolean isMultiplication() {
        return false;
    }

    @Override
    public boolean isNotEquals() {
        return false;
    }

    @Override
    public boolean isPower() {
        return false;
    }

    @Override
    public boolean isNegative() {
        return false;
    }

    @Override
    public boolean isPercentage() {
        return false;
    }

    @Override
    public boolean isRange() {
        return false;
    }

    @Override
    public boolean isSubtraction() {
        return false;
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
