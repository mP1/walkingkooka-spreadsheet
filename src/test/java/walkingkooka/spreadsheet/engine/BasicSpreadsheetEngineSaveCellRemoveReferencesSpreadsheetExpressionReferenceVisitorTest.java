package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;

public final class BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitor> {
    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitor> type() {
        return BasicSpreadsheetEngineSaveCellRemoveReferencesSpreadsheetExpressionReferenceVisitor.class;
    }
}
