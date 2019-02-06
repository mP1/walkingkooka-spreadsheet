package walkingkooka.spreadsheet.security;

import walkingkooka.test.ClassTestCase;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.MemberVisibility;

public abstract class IdentityIdTestCase<I extends IdentityId> extends ClassTestCase<I>
        implements ToStringTesting<I> {

    IdentityIdTestCase() {
        super();
    }

    @Override
    protected final MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
