package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface IdentityTesting<I extends Identity<ID>, ID extends IdentityId>
        extends ClassTesting2<I>,
        HashCodeEqualsDefinedTesting<I>,
        ToStringTesting<I> {
    
    @Test
    default void testWithNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createIdentity(null);
        });
    }

    @Override
    default I createObject() {
        return this.createIdentity();
    }

    default I createIdentity() {
        return this.createIdentity(this.createId());
    }

    I createIdentity(final ID id);

    ID createId();

    @Override
    default MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
