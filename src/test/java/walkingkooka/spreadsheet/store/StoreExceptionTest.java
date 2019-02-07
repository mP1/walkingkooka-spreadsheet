package walkingkooka.spreadsheet.store;

import walkingkooka.test.StandardThrowableTesting;
import walkingkooka.test.TestCase;

public final class StoreExceptionTest extends TestCase
        implements StandardThrowableTesting<StoreException> {

    @Override
    public Class<StoreException> type() {
        return StoreException.class;
    }
}
