package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

public final class BasicSpreadsheetEngineDeleteColumnOrRowTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineDeleteColumnOrRow> {
    @Override
    public Class<BasicSpreadsheetEngineDeleteColumnOrRow> type() {
        return BasicSpreadsheetEngineDeleteColumnOrRow.class;
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return "ColumnOrRow";
    }
}
