package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.type.MemberVisibility;

public abstract class IdentityTestCase<I extends Identity<ID>, ID extends IdentityId>
        extends ClassTestCase<I>
        implements HashCodeEqualsDefinedTesting<I> {

    IdentityTestCase() {
        super();
    }

    @Test(expected = NullPointerException.class)
    public final void testWithNullIdFails() {
        this.createIdentity(null);
    }

    @Override
    public final I createObject() {
        return this.createIdentity();
    }

    final I createIdentity() {
        return this.createIdentity(this.createId());
    }

    abstract I createIdentity(final ID id);

    abstract ID createId();

    @Override
    protected final MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
