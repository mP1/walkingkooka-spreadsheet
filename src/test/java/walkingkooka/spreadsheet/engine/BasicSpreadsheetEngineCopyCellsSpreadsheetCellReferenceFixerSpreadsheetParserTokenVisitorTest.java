package walkingkooka.spreadsheet.engine;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitorTesting;
import walkingkooka.type.MemberVisibility;

public final class BasicSpreadsheetEngineCopyCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest implements SpreadsheetParserTokenVisitorTesting<BasicSpreadsheetEngineCopyCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    @Override
    public BasicSpreadsheetEngineCopyCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createVisitor() {
        return new BasicSpreadsheetEngineCopyCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(0, 0);
    }

    @Override
    public String typeNamePrefix() {
        return BasicSpreadsheetEngine.class.getSimpleName();
    }

    @Override
    public Class<BasicSpreadsheetEngineCopyCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> type() {
        return BasicSpreadsheetEngineCopyCellsSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
