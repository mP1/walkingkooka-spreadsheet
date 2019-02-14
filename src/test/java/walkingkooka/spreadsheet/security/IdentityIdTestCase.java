package walkingkooka.spreadsheet.security;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.MemberVisibility;

public abstract class IdentityIdTestCase<I extends IdentityId> implements ClassTesting2<I>,
        ToStringTesting<I> {

    IdentityIdTestCase() {
        super();
    }

    @Override
    public final MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
