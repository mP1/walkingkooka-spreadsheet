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

import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * A wrapper around a numeric type that is also a percentage.
 */
public final class SpreadsheetCellRangeParserToken extends SpreadsheetBinaryParserToken
        implements HasSpreadsheetReference<SpreadsheetCellRangeReference> {

    static SpreadsheetCellRangeParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetCellRangeParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetCellRangeParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        final SpreadsheetCellRangeParserTokenSpreadsheetParserTokenVisitor visitor = SpreadsheetCellRangeParserTokenSpreadsheetParserTokenVisitor.with();
        visitor.accept(this);

        final List<SpreadsheetCellReferenceParserToken> components = visitor.components;
        final int count = components.size();

        switch (count) {
            case 0:
                throw new IllegalArgumentException("Missing begin");
            case 1:
                throw new IllegalArgumentException("Missing end");
            case 2:
                this.cellRange = components.get(0).cell()
                        .cellRange(components.get(1).cell());
                break;
            default:
                throw new IllegalArgumentException("Ranges must only have begin & end");
        }
    }

    public SpreadsheetCellRangeReference toCellRange() {
        return this.cellRange;
    }

    private final SpreadsheetCellRangeReference cellRange;

    // children.........................................................................................................

    @Override
    public SpreadsheetCellRangeParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetCellRangeParserToken::with
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

    // HasSpreadsheetReference..........................................................................................

    @Override
    public SpreadsheetCellRangeReference reference() {
        return this.toCellRange();
    }
}
