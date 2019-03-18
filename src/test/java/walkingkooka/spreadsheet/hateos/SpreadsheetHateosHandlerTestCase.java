package walkingkooka.spreadsheet.hateos;

import walkingkooka.test.ClassTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.MemberVisibility;

public abstract class SpreadsheetHateosHandlerTestCase<T> implements ClassTesting<T>, TypeNameTesting<T> {

    SpreadsheetHateosHandlerTestCase() {
        super();
    }

    @Override
    public final MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
