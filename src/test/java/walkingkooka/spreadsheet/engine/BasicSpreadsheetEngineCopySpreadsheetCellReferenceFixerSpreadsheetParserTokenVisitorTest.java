package walkingkooka.spreadsheet.engine;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitorTestCase;

public final class BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest extends SpreadsheetParserTokenVisitorTestCase<BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    @Override
    protected BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createParserTokenVisitor() {
        return new BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(0, 0);
    }

    @Override
    protected String requiredNamePrefix() {
        return BasicSpreadsheetEngine.class.getSimpleName();
    }

    @Override
    protected Class<BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> parserTokenVisitorType() {
        return BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }
}
