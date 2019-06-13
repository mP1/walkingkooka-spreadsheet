package walkingkooka.spreadsheet.engine;

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitorTesting;
import walkingkooka.type.JavaVisibility;

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
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
