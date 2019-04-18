package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

public abstract class SpreadsheetEngineHateosHandlerTestCase<T> extends SpreadsheetHateosHandlerTestCase<T> {

    SpreadsheetEngineHateosHandlerTestCase() {
        super();
    }

    final SpreadsheetDelta delta() {
        return SpreadsheetDelta.with(this.spreadsheetId(), Sets.of(this.cell()));
    }

    final SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(123);
    }

    final SpreadsheetCell cell() {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse("A99"),
                SpreadsheetFormula.with("1+2"),
                SpreadsheetCellStyle.EMPTY);
    }

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetEngine.class.getSimpleName();
    }
}
