package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTesting2;
import walkingkooka.tree.expression.ExpressionNodeVisitor;
import walkingkooka.type.MemberVisibility;

public final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineDeleteOrInsertColumnOrRow> {

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return "DeleteOrInsertColumnOrRow";
    }

    // ClassTesting..........................................................................

    @Override
    public Class<BasicSpreadsheetEngineDeleteOrInsertColumnOrRow> type() {
        return BasicSpreadsheetEngineDeleteOrInsertColumnOrRow.class;
    }
}
