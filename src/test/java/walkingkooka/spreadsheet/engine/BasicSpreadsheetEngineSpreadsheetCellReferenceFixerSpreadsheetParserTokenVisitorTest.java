package walkingkooka.spreadsheet.engine;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitorTestCase;

public final class BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest extends SpreadsheetParserTokenVisitorTestCase<BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    @Override
    protected BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createParserTokenVisitor() {
        return new BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(0, 0, null));
    }

    @Override
    protected String requiredNamePrefix() {
        return BasicSpreadsheetEngine.class.getSimpleName();
    }

    @Override
    protected Class<BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> parserTokenVisitorType() {
        return BasicSpreadsheetEngineSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }
}
