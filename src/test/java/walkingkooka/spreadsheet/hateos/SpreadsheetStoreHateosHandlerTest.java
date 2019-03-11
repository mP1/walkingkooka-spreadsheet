package walkingkooka.spreadsheet.hateos;

import walkingkooka.Cast;
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.MemberVisibility;

public final class SpreadsheetStoreHateosHandlerTest implements ClassTesting2<SpreadsheetStoreHateosHandler<?, ?, ?>> {
    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<SpreadsheetStoreHateosHandler<?, ?, ?>> type() {
        return Cast.to(SpreadsheetStoreHateosHandler.class);
    }
}
