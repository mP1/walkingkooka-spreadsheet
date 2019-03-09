package walkingkooka.spreadsheet.hateos;

import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.test.ClassTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.MemberVisibility;

public abstract class SpreadsheetHateosHandlerTestCase<T> implements ClassTesting<T>, TypeNameTesting<T> {

    SpreadsheetHateosHandlerTestCase() {
        super();
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetEngine.class.getSimpleName();
    }
}
