package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.VisitorTesting;
import walkingkooka.type.MemberVisibility;

import java.util.function.Consumer;

public final class ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitorTest implements
        VisitorTesting<ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor, ExpressionReference>,
        ToStringTesting<ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor> {

    @Test
    public void testToString() {
        final ExpressionReferenceSpreadsheetCellReferencesBiConsumer f = ExpressionReferenceSpreadsheetCellReferencesBiConsumer.with(SpreadsheetLabelStores.fake(),
                SpreadsheetRangeStores.fake());
        final Consumer<SpreadsheetCellReference> references = new Consumer<SpreadsheetCellReference>() {
            @Override
            public void accept(final SpreadsheetCellReference reference) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return "SpreadsheetCellReferences123";
            }
        };
        this.toStringAndCheck(new ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor(f, references),
                f.toString() + " SpreadsheetCellReferences123");
    }

    @Override
    public ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor(null, null);
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return ExpressionReferenceSpreadsheetCellReferencesBiConsumer.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor> type() {
        return ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor.class;
    }
}
