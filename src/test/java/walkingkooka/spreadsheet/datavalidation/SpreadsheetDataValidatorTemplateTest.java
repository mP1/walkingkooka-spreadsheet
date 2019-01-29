package walkingkooka.spreadsheet.datavalidation;

import walkingkooka.test.ClassTestCase;
import walkingkooka.type.MemberVisibility;

public final class SpreadsheetDataValidatorTemplateTest extends ClassTestCase<SpreadsheetDataValidatorTemplate> {
    @Override
    protected Class<SpreadsheetDataValidatorTemplate> type() {
        return SpreadsheetDataValidatorTemplate.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
