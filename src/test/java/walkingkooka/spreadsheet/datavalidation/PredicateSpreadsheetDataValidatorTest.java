package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.Predicates;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PredicateSpreadsheetDataValidatorTest extends SpreadsheetDataValidatorTemplateTestCase<PredicateSpreadsheetDataValidator, String> {

    private final static String VALUE = "abc123";

    @Test
    public void testPredicatePass() {
        this.validatePassCheck(VALUE);
    }

    @Test
    public void testPredicateFalse() {
        this.validateFailCheck("Value that fails validation");
    }

    @Test
    public void testToString() {
        assertEquals(this.predicate().toString(), this.createSpreadsheetDataValidator().toString());
    }

    @Override
    protected PredicateSpreadsheetDataValidator createSpreadsheetDataValidator() {
        return PredicateSpreadsheetDataValidator.with(String.class, this.predicate());
    }

    private Predicate<String> predicate() {
        return Predicates.setContains(Sets.of(VALUE));
    }

    @Override
    protected String value() {
        return VALUE;
    }

    @Override
    protected Class<String> valueType() {
        return String.class;
    }

    @Override
    protected SpreadsheetDataValidatorContext createContext() {
        return SpreadsheetDataValidatorContexts.fake();
    }

    @Override
    protected Class<PredicateSpreadsheetDataValidator> type() {
        return PredicateSpreadsheetDataValidator.class;
    }
}
