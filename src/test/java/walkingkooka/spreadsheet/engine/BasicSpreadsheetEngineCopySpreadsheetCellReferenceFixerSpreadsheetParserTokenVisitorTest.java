package walkingkooka.spreadsheet.engine;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitorTesting;
import walkingkooka.type.MemberVisibility;

public final class BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest implements SpreadsheetParserTokenVisitorTesting<BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    @Override
    public BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createVisitor() {
        return new BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(0, 0);
    }

    @Override
    public String typeNamePrefix() {
        return BasicSpreadsheetEngine.class.getSimpleName();
    }

    @Override
    public Class<BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> type() {
        return BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
