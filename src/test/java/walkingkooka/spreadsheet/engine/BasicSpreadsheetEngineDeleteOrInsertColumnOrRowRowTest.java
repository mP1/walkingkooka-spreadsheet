package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTesting2;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitor;
import walkingkooka.type.MemberVisibility;

public final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRowTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow> {

    @Override
    public Class<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow> type() {
        return BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.class;
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return "Row";
    }
}
