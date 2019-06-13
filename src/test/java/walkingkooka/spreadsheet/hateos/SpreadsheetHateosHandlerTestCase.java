package walkingkooka.spreadsheet.hateos;

import walkingkooka.test.ClassTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.JavaVisibility;

public abstract class SpreadsheetHateosHandlerTestCase<T> implements ClassTesting<T>,
        ToStringTesting<T>,
        TypeNameTesting<T> {

    SpreadsheetHateosHandlerTestCase() {
        super();
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
