package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.test.PublicClassTestCase;

public abstract class IdentityTestCase<I extends Identity<ID>, ID extends IdentityId> extends PublicClassTestCase<I> {

    IdentityTestCase() {
        super();
    }

    @Test(expected = NullPointerException.class)
    public final void testWithNullIdFails() {
        this.createIdentity(null);
    }

    final I createIdentity() {
        return this.createIdentity(this.createId());
    }

    abstract I createIdentity(final ID id);

    abstract ID createId();
}
