package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.predicate.Predicates;

import java.util.function.Predicate;

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
        this.toStringAndCheck(this.createSpreadsheetDataValidator(), this.predicate().toString());
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
    public Class<String> valueType() {
        return String.class;
    }

    @Override
    protected SpreadsheetDataValidatorContext createContext() {
        return SpreadsheetDataValidatorContexts.fake();
    }

    @Override
    public Class<PredicateSpreadsheetDataValidator> type() {
        return PredicateSpreadsheetDataValidator.class;
    }
}
