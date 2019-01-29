package walkingkooka.spreadsheet.security;

import walkingkooka.test.ClassTestCase;
import walkingkooka.type.MemberVisibility;

public abstract class IdentityIdTestCase<I extends IdentityId> extends ClassTestCase<I> {

    IdentityIdTestCase() {
        super();
    }

    @Override
    protected final MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
