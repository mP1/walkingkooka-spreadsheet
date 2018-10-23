package walkingkooka.spreadsheet.engine;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitorTestCase;

public final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest extends SpreadsheetParserTokenVisitorTestCase<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    @Override
    protected BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createParserTokenVisitor() {
        return new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(0, 0, null, null));
    }

    @Override
    protected String requiredNamePrefix() {
        return BasicSpreadsheetEngine.class.getSimpleName();
    }

    @Override
    protected Class<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> parserTokenVisitorType() {
        return BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }
}
