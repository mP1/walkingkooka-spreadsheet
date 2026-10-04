package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.PublicClassTesting;

public final class HasOptionalSpreadsheetFormatterSelectorTestingTest implements HasOptionalSpreadsheetFormatterSelectorTesting,
    PublicClassTesting<HasOptionalSpreadsheetFormatterSelectorTesting> {

    @Test
    public void testConstants() {
        this.checkNotEquals(
            FORMATTER_SELECTOR,
            DIFFERENT_FORMATTER_SELECTOR
        );
    }

    @Test
    public void testOptionalConstants() {
        this.checkNotEquals(
            OPTIONAL_FORMATTER_SELECTOR,
            OPTIONAL_DIFFERENT_FORMATTER_SELECTOR
        );
    }

    // class............................................................................................................

    @Override
    public Class<HasOptionalSpreadsheetFormatterSelectorTesting> type() {
        return HasOptionalSpreadsheetFormatterSelectorTesting.class;
    }
}