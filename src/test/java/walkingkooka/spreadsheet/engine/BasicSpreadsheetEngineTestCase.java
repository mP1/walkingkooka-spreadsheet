package walkingkooka.spreadsheet.engine;

import walkingkooka.test.ClassTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.MemberVisibility;

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
    public final MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }
}
