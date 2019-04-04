package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

public final class BasicSpreadsheetEngineSaveCellTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineSaveCell>
implements ToStringTesting<BasicSpreadsheetEngineSaveCell> {

    @Test
    public void testToString() {
        final SpreadsheetCell cell = SpreadsheetCell.with(SpreadsheetCellReference.parse("A99"),
        SpreadsheetFormula.with("1+2"),
                SpreadsheetCellStyle.EMPTY);
        this.toStringAndCheck(new BasicSpreadsheetEngineSaveCell(cell, null, null, null ), cell.toString());
    }

    @Override
    public Class<BasicSpreadsheetEngineSaveCell> type() {
        return BasicSpreadsheetEngineSaveCell.class;
    }

    @Override
    public String typeNameSuffix() {
        return "SaveCell";
    }
}
