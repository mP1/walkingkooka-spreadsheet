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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.SpreadsheetAdditionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetBetweenSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetBigDecimalParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetBigIntegerParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDivideSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDivisionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDoubleParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetFunctionNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetFunctionParameterSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetFunctionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGroupParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLabelNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLocalDateParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLocalDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLocalTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLongParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMinusSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMultiplicationParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMultiplySymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNegativeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNotEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNotEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParenthesisCloseSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParenthesisOpenSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTokenVisitor;
import walkingkooka.spreadsheet.parser.SpreadsheetPercentSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPercentageParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPlusSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPowerParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPowerSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetRangeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetSubtractionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTextParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetWhitespaceParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetParserTokenVisitor} that handles visiting and updating {@link SpreadsheetCellReferenceParserToken}
 * so cell references after an insert or delete row/column are corrected.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

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

        return tokens.get(0).cast();
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

    @Override
    protected final Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetAdditionParserToken token) {
        this.exit(token, SpreadsheetParserToken::addition);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetCellReferenceParserToken token) {
        this.exit(token, SpreadsheetParserToken::cellReference);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetDivisionParserToken token) {
        this.exit(token, SpreadsheetParserToken::division);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::equalsParserToken);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetFunctionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetFunctionParserToken token) {
        this.exit(token, SpreadsheetParserToken::function);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetGreaterThanParserToken token) {
        this.exit(token, SpreadsheetParserToken::greaterThan);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::greaterThanEquals);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGroupParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetGroupParserToken token) {
        this.exit(token, SpreadsheetParserToken::group);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetLessThanParserToken token) {
        this.exit(token, SpreadsheetParserToken::lessThan);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::lessThanEquals);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetMultiplicationParserToken token) {
        this.exit(token, SpreadsheetParserToken::multiplication);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetNegativeParserToken token) {
        this.exit(token, SpreadsheetParserToken::negative);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetNotEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::notEquals);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetPercentageParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetPercentageParserToken token) {
        this.exit(token, SpreadsheetParserToken::percentage);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetPowerParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetPowerParserToken token) {
        this.exit(token, SpreadsheetParserToken::power);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetRangeParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetRangeParserToken token) {
        this.exit(token, SpreadsheetParserToken::range);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetSubtractionParserToken token) {
        this.exit(token, SpreadsheetParserToken::subtraction);
    }

    // leaf ......................................................................................................

    @Override
    protected final void visit(final SpreadsheetBetweenSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetBigDecimalParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetBigIntegerParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetColumnReferenceParserToken token) {
        this.leaf(this.columnOrRow.fixCellReferencesWithinExpression(token));
    }

    @Override
    protected final void visit(final SpreadsheetDivideSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDoubleParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetFunctionNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetFunctionParameterSeparatorSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetGreaterThanSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetGreaterThanEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLabelNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLessThanSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLessThanEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLocalDateParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLocalDateTimeParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLocalTimeParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLongParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMinusSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMultiplySymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetNotEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetParenthesisCloseSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetParenthesisOpenSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPercentSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPlusSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPowerSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetRowReferenceParserToken token) {
        this.leaf(this.columnOrRow.fixCellReferencesWithinExpression(token));
    }

    @Override
    protected final void visit(final SpreadsheetTextParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetWhitespaceParserToken token) {
        this.leaf(token);
    }

    private Visiting enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();
        this.invalidCellReference = false;

        return Visiting.CONTINUE;
    }

    private <P extends SpreadsheetParserToken & ParentParserToken> void exit(final P parent,
                                                                             final BiFunction<List<ParserToken>, String, ParserToken> factory) {
        final List<ParserToken> children = this.children;
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
        this.add(
                this.invalidCellReference ?
                        this.cellReferenceDeleted(parent) :
                        factory.apply(children, ParserToken.text(children)));
        this.invalidCellReference = false;
    }

    /**
     * When true, the parent {@link SpreadsheetParserToken} which should be a {@link SpreadsheetCellReferenceParserToken}
     * will be replaced by {@link #cellReferenceDeleted(SpreadsheetParserToken)}.
     */
    private boolean invalidCellReference = false;

    /**
     * Returns a function that when executed will report that the original cell reference was deleted.
     * The replaced token will appear to be invocation of a function with the reference in quotes.
     */
    private SpreadsheetParserToken cellReferenceDeleted(final SpreadsheetParserToken token) {
        final String text = token.text();

        final List<ParserToken> tokens = Lists.of(SpreadsheetFormula.INVALID_CELL_REFERENCE_PARSER_TOKEN,
                SpreadsheetParserToken.parenthesisOpenSymbol("(", "("),
                SpreadsheetParserToken.text(text, CharSequences.quote(text).toString()),
                SpreadsheetParserToken.parenthesisCloseSymbol(")", ")"));
        return SpreadsheetParserToken.function(tokens, ParserToken.text(tokens));
    }

    private void leaf(final Optional<SpreadsheetParserToken> token) {
        if (token.isPresent()) {
            this.add(token.get());
        } else {
            this.invalidCellReference = true;
        }
    }

    private void leaf(final ParserToken token) {
        this.add(token);
    }

    private void add(final ParserToken child) {
        Objects.requireNonNull(child, "child");
        this.children.add(child);
    }

    private Stack<List<ParserToken>> previousChildren = Stacks.arrayList();

    private List<ParserToken> children = Lists.array();

    @Override
    public final String toString() {
        return this.children + "," + this.previousChildren;
    }
}
