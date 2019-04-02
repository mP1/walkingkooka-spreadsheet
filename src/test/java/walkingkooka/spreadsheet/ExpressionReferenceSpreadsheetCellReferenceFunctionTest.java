package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.MemberVisibility;
import walkingkooka.util.FunctionTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ExpressionReferenceSpreadsheetCellReferenceFunctionTest implements FunctionTesting<ExpressionReferenceSpreadsheetCellReferenceFunction,
        ExpressionReference,
        Optional<SpreadsheetCellReference>>,
        ClassTesting2<ExpressionReferenceSpreadsheetCellReferenceFunction>,
        ToStringTesting<ExpressionReferenceSpreadsheetCellReferenceFunction> {

    @Test
    public void testWithNullLabelStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            ExpressionReferenceSpreadsheetCellReferenceFunction.with(null, SpreadsheetRangeStores.fake());
        });
    }

    @Test
    public void testWithNullSpreadsheetRangeStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            ExpressionReferenceSpreadsheetCellReferenceFunction.with(SpreadsheetLabelStores.fake(), null);
        });
    }

    @Test
    public void testApplyNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createFunction().apply(null);
        });
    }

    @Test
    public void testCell() {
        final SpreadsheetCellReference z99 = SpreadsheetCellReference.parse("Z99");
        this.applyAndCheck(z99, Optional.of(z99));
    }

    @Test
    public void testRange() {
        this.applyAndCheck(this.rangeC1C2(), Optional.of(this.cellC1()));
    }

    @Test
    public void testLabelToCell() {
        this.applyAndCheck(this.labelB1(), Optional.of(this.cellB1()));
    }

    @Test
    public void testLabelToUnknown() {
        this.applyAndCheck(SpreadsheetLabelName.with("unknown"), Optional.empty());
    }

    @Test
    public void testLabelToRange() {
        this.applyAndCheck(this.labelRangeC1D2(), Optional.of(this.cellC1()));
    }

    @Test
    public void testToString() {
        final SpreadsheetLabelStore labelStore = SpreadsheetLabelStores.fake();
        final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore = SpreadsheetRangeStores.fake();
        this.toStringAndCheck(ExpressionReferenceSpreadsheetCellReferenceFunction.with(labelStore, rangeToCellStore),
                "ExpressionReference->SpreadsheetCellReference(" + labelStore + " " + rangeToCellStore + ")");
    }

    @Override
    public ExpressionReferenceSpreadsheetCellReferenceFunction createFunction() {
        final SpreadsheetLabelStore labelStore = SpreadsheetLabelStores.treeMap();

        labelStore.save(SpreadsheetLabelMapping.with(labelB1(), this.cellB1()));
        labelStore.save(SpreadsheetLabelMapping.with(labelRangeC1D2(), this.rangeC1C2()));

        final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore = SpreadsheetRangeStores.treeMap();
        rangeToCellStore.addValue(this.rangeC1C2(), this.cellC1());

        return ExpressionReferenceSpreadsheetCellReferenceFunction.with(SpreadsheetLabelStores.readOnly(labelStore),
                SpreadsheetRangeStores.readOnly(rangeToCellStore));
    }

    private SpreadsheetLabelName labelB1() {
        return SpreadsheetLabelName.with("labelB1");
    }

    private SpreadsheetCellReference cellB1() {
        return SpreadsheetCellReference.parse("B1");
    }

    private SpreadsheetLabelName labelRangeC1D2() {
        return SpreadsheetLabelName.with("labelRangeC1D2");
    }

    private SpreadsheetRange rangeC1C2() {
        return SpreadsheetRange.with(this.cellC1(), this.cellC2());
    }

    private SpreadsheetCellReference cellC1() {
        return SpreadsheetCellReference.parse("C1");
    }

    private SpreadsheetCellReference cellC2() {
        return SpreadsheetCellReference.parse("C2");
    }

    @Override
    public Class<ExpressionReferenceSpreadsheetCellReferenceFunction> type() {
        return ExpressionReferenceSpreadsheetCellReferenceFunction.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
