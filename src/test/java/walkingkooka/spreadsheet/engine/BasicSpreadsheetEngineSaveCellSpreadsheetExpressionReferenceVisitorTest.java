package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;

public final class BasicSpreadsheetEngineSaveCellSpreadsheetExpressionReferenceVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineSaveCellSpreadsheetExpressionReferenceVisitor> {
    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<BasicSpreadsheetEngineSaveCellSpreadsheetExpressionReferenceVisitor> type() {
        return BasicSpreadsheetEngineSaveCellSpreadsheetExpressionReferenceVisitor.class;
    }
}
