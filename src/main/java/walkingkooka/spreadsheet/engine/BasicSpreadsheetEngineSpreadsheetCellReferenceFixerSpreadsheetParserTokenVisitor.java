package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetAdditionParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetBetweenSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetBigDecimalParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetBigIntegerParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCloseParenthesisSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetDivideSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetDivisionParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetDoubleParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetEqualsParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetEqualsSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetFunctionNameParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetFunctionParameterSeparatorSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetFunctionParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGreaterThanEqualsParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGreaterThanEqualsSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGreaterThanParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGreaterThanSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGroupParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelNameParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLessThanEqualsParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLessThanEqualsSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLessThanParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLessThanSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLocalDateParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLocalDateTimeParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLocalTimeParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLongParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetMinusSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetMultiplicationParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetMultiplySymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetNegativeParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetNotEqualsParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetNotEqualsSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetOpenParenthesisSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitor;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPercentSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPercentageParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPlusSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPowerParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPowerSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRangeParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetSubtractionParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetTextParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetWhitespaceParserToken;
import walkingkooka.tree.visit.Visiting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParserTokenVisitor} that handles visiting and updating {@link SpreadsheetCellReferenceParserToken}
 * so cell references after an insert or delete row/column are corrected.
 */
final class BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

    /**
     * Accepts a token tree and updates rows and columns.
     */
    static SpreadsheetParserToken expressionFixReferences(final SpreadsheetParserToken token,
                                                          final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        final BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor visitor = new BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(columnOrRow);
        visitor.accept(token);

        final List<SpreadsheetParserToken> tokens = visitor.children;
        final int count = tokens.size();
        if (1 != count) {
            throw new IllegalStateException("Expected only 1 child but got " + count + "=" + tokens);
        }

        return tokens.get(0);
    }

    /**
     * Package private ctor use static method.
     */
    // @VisibleForTesting
    BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        super();
        this.columnOrRow = columnOrRow;
    }

    private final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow;

    @Override
    protected final Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetAdditionParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetCellReferenceParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetDivisionParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetEqualsParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetFunctionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetFunctionParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetGreaterThanParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGroupParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetGroupParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetLessThanParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetMultiplicationParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetNegativeParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetNotEqualsParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetPercentageParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetPercentageParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetPowerParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetPowerParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetRangeParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetRangeParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetSubtractionParserToken token) {
        this.exit(token);
        super.endVisit(token);
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
    protected final void visit(final SpreadsheetCloseParenthesisSymbolParserToken token) {
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
        super.visit(token);
    }

    @Override
    protected final void visit(final SpreadsheetOpenParenthesisSymbolParserToken token) {
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

    private void enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();
        this.invalidCellReference = false;
    }

    private <P extends SpreadsheetParserToken & ParentParserToken> void exit(final P parent) {
        final List<SpreadsheetParserToken> children = this.children;
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
        this.add(
                this.invalidCellReference ?
                        this.cellReferenceDeleted(parent) :
                        SpreadsheetParserToken.class.cast(parent.setValue(children).setTextFromValues()));
        this.invalidCellReference = false;
    }

    /**
     * When true, the parent {@link SpreadsheetParserToken} which should be a {@link SpreadsheetCellReferenceParserToken}
     * will be replaced by {@link #cellReferenceDeleted(SpreadsheetParserToken)}.
     */
    private boolean invalidCellReference = false;

    /**
     * Returns a function that when executed will report that the original cell reference was deleted.
     */
    private SpreadsheetParserToken cellReferenceDeleted(final SpreadsheetParserToken token) {
        final String message = token.text();

        final List<ParserToken> tokens = Lists.of(SpreadsheetFormula.INVALID_CELL_REFERENCE_PARSER_TOKEN,
                SpreadsheetParserToken.openParenthesisSymbol("(", "("),
                SpreadsheetParserToken.text(message, message),
                SpreadsheetParserToken.openParenthesisSymbol(")", ")"));
        return SpreadsheetParserToken.function(tokens, ParserToken.text(tokens));
    }

    private void leaf(final Optional<SpreadsheetParserToken> token) {
        if (token.isPresent()) {
            this.add(token.get());
        } else {
            this.invalidCellReference = true;
        }
    }

    private void leaf(final SpreadsheetParserToken token) {
        this.add(token);
    }

    private void add(final SpreadsheetParserToken child) {
        Objects.requireNonNull(child, "child");
        this.children.add(child);
    }

    private Stack<List<SpreadsheetParserToken>> previousChildren = Stacks.arrayList();

    private List<SpreadsheetParserToken> children = Lists.array();

    @Override
    public final String toString() {
        return this.children + "," + this.previousChildren;
    }
}
