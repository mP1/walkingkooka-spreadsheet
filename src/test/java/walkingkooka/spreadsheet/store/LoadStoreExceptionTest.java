package walkingkooka.spreadsheet.store;

import walkingkooka.test.StandardThrowableTesting;
import walkingkooka.test.TestCase;

public final class LoadStoreExceptionTest extends TestCase
        implements StandardThrowableTesting<LoadStoreException> {

    @Override
    public Class<LoadStoreException> type() {
        return LoadStoreException.class;
    }
}
