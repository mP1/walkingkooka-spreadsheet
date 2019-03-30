package walkingkooka.spreadsheet.engine;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitor;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitorTesting;
import walkingkooka.tree.expression.ExpressionNodeVisitor;
import walkingkooka.type.MemberVisibility;

public final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor>
        implements SpreadsheetParserTokenVisitorTesting<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    @Override
    public BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createVisitor() {
        return new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(0, 0, null, null));
    }

    @Override
    public Class<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> type() {
        return BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return SpreadsheetParserTokenVisitor.class.getSimpleName();
    }
}
