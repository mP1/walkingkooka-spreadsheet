package walkingkooka.spreadsheet;

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.PublicStaticHelperTesting;
import walkingkooka.type.MemberVisibility;

import java.lang.reflect.Method;

public class SpreadsheetBiConsumersTest implements ClassTesting2<SpreadsheetBiConsumers>,
        PublicStaticHelperTesting<SpreadsheetBiConsumers> {

    @Override
    public Class<SpreadsheetBiConsumers> type() {
        return SpreadsheetBiConsumers.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}

