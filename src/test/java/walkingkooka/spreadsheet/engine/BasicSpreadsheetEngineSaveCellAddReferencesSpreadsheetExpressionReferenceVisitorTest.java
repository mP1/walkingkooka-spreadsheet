package walkingkooka.spreadsheet.engine;

import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;

public final class BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitor> {
    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitor> type() {
        return BasicSpreadsheetEngineSaveCellAddReferencesSpreadsheetExpressionReferenceVisitor.class;
    }
}
