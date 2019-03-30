package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

public final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn> {

    @Override
    public Class<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn> type() {
        return BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumn.class;
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return "Column";
    }
}
