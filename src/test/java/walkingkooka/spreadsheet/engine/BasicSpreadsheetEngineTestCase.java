package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.JavaVisibility;

public abstract class BasicSpreadsheetEngineTestCase<T> implements TypeNameTesting<T>, ClassTesting<T> {

    BasicSpreadsheetEngineTestCase() {
        super();
    }

    // TypeNameTesting.....................................................................................................

    @Override
    public final String typeNamePrefix() {
        return BasicSpreadsheetEngine.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
