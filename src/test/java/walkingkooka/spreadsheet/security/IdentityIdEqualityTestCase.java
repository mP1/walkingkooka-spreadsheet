package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

public abstract class IdentityIdEqualityTestCase<I extends IdentityId> extends HashCodeEqualsDefinedEqualityTestCase<I> {

    final static long ID_VALUE = 1;

    IdentityIdEqualityTestCase() {
        super();
    }

    @Test
    public final void testDifferentValue() {
        this.checkNotEquals(this.createObject(999));
    }

    @Override
    protected final I createObject() {
        return this.createObject(ID_VALUE);
    }

    abstract I createObject(final long id);
}
