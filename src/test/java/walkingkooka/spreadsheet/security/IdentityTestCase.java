package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class IdentityTestCase<I extends Identity<ID>, ID extends IdentityId>
        extends ClassTestCase<I>
        implements HashCodeEqualsDefinedTesting<I> {

    IdentityTestCase() {
        super();
    }

    @Test
    public final void testWithNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createIdentity(null);
        });
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
