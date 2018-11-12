package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

public abstract class IdentityEqualityTestCase<I extends Identity<ID>, ID extends IdentityId> extends HashCodeEqualsDefinedEqualityTestCase<I> {

    final static long ID_VALUE = 1;

    IdentityEqualityTestCase() {
        super();
    }

    @Test
    public final void testDifferentId() {
        this.checkNotEquals(this.createObject(this.createId(999)));
    }

    @Override
    protected final I createObject() {
        return this.createObject(this.createId(ID_VALUE));
    }

    abstract I createObject(final ID id);

    abstract ID createId(final long value);
}
