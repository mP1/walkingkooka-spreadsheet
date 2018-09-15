package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;

public final class SpreadsheetLabelMappingEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetLabelMapping> {

    private final static SpreadsheetLabelName LABEL =SpreadsheetLabelName.with("label");
    private final static SpreadsheetCellReference CELL = cell(1);

    @Test
    public void testDifferentLabel() {
        this.checkNotEquals(SpreadsheetLabelMapping.with(SpreadsheetLabelName.with("different"), CELL));
    }

    @Test
    public void testDifferentCell() {
        this.checkNotEquals(SpreadsheetLabelMapping.with(LABEL, cell(99)));
    }

    @Override
    protected SpreadsheetLabelMapping createObject() {
        return SpreadsheetLabelMapping.with(LABEL, CELL);
    }

    private static SpreadsheetCellReference cell(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column)
                .setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }
}
