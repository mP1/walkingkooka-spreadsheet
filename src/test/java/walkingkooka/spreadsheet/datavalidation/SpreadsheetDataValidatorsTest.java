package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.test.PublicStaticHelperTestCase;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetDataValidatorsTest extends PublicStaticHelperTestCase<SpreadsheetDataValidators> {

    private final static BigDecimal BIGDECIMAL_BELOW = BigDecimal.valueOf(5);
    private final static BigDecimal BIGDECIMAL_LOWER = BigDecimal.valueOf(10);
    private final static BigDecimal BIGDECIMAL_BETWEEN = BigDecimal.valueOf(15);
    private final static BigDecimal BIGDECIMAL_UPPER = BigDecimal.valueOf(20);
    private final static BigDecimal BIGDECIMAL_ABOVE = BigDecimal.valueOf(30);

    // BigDecimal eq..........................................................................................

    @Test
    public void testBigDecimalEqualsBelow() {
        this.validateFailCheck(bigDecimalEqualsValidator(), BIGDECIMAL_BELOW);
    }

    @Test
    public void testBigDecimalEqualsLower() {
        this.validateFailCheck(bigDecimalEqualsValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testBigDecimalEqualsValue() {
        this.validatePassCheck(bigDecimalEqualsValidator(), BIGDECIMAL_BETWEEN);
    }

    @Test
    public void testBigDecimalEqualsUpper() {
        this.validateFailCheck(bigDecimalEqualsValidator(), BIGDECIMAL_UPPER);
    }

    @Test
    public void testBigDecimalEqualsAbove() {
        this.validateFailCheck(bigDecimalEqualsValidator(), BIGDECIMAL_ABOVE);
    }

    private SpreadsheetDataValidator<BigDecimal> bigDecimalEqualsValidator() {
        return SpreadsheetDataValidators.bigDecimalEquals(BIGDECIMAL_BETWEEN);
    }

    // BigDecimal eq..........................................................................................

    @Test
    public void testBigDecimalNotEqualsBelow() {
        this.validatePassCheck(bigDecimalNotEqualsValidator(), BIGDECIMAL_BELOW);
    }

    @Test
    public void testBigDecimalNotEqualsLower() {
        this.validatePassCheck(bigDecimalNotEqualsValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testBigDecimalNotEqualsValue() {
        this.validateFailCheck(bigDecimalNotEqualsValidator(), BIGDECIMAL_BETWEEN);
    }

    @Test
    public void testBigDecimalNotEqualsUpper() {
        this.validatePassCheck(bigDecimalNotEqualsValidator(), BIGDECIMAL_UPPER);
    }

    @Test
    public void testBigDecimalNotEqualsAbove() {
        this.validatePassCheck(bigDecimalNotEqualsValidator(), BIGDECIMAL_ABOVE);
    }

    private SpreadsheetDataValidator<BigDecimal> bigDecimalNotEqualsValidator() {
        return SpreadsheetDataValidators.bigDecimalNotEquals(BIGDECIMAL_BETWEEN);
    }
    // BigDecimal gt..........................................................................................

    @Test
    public void testBigDecimalGreaterThanBelow() {
        this.validateFailCheck(bigDecimalGreaterThanValidator(), BIGDECIMAL_BELOW);
    }

    @Test
    public void testBigDecimalGreaterThanLower() {
        this.validateFailCheck(bigDecimalGreaterThanValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testBigDecimalGreaterThanValue() {
        this.validateFailCheck(bigDecimalGreaterThanValidator(), BIGDECIMAL_BETWEEN);
    }

    @Test
    public void testBigDecimalGreaterThanUpper() {
        this.validatePassCheck(bigDecimalGreaterThanValidator(), BIGDECIMAL_UPPER);
    }

    @Test
    public void testBigDecimalGreaterThanAbove() {
        this.validatePassCheck(bigDecimalGreaterThanValidator(), BIGDECIMAL_ABOVE);
    }

    private SpreadsheetDataValidator<BigDecimal> bigDecimalGreaterThanValidator() {
        return SpreadsheetDataValidators.bigDecimalGreaterThan(BIGDECIMAL_BETWEEN);
    }

    // BigDecimal gte..........................................................................................

    @Test
    public void testBigDecimalGreaterThanEqualsBelow() {
        this.validateFailCheck(bigDecimalGreaterThanEqualsValidator(), BIGDECIMAL_BELOW);
    }

    @Test
    public void testBigDecimalGreaterThanEqualsLower() {
        this.validateFailCheck(bigDecimalGreaterThanEqualsValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testBigDecimalGreaterThanEqualsValue() {
        this.validatePassCheck(bigDecimalGreaterThanEqualsValidator(), BIGDECIMAL_BETWEEN);
    }

    @Test
    public void testBigDecimalGreaterThanEqualsUpper() {
        this.validatePassCheck(bigDecimalGreaterThanEqualsValidator(), BIGDECIMAL_UPPER);
    }

    @Test
    public void testBigDecimalGreaterThanEqualsAbove() {
        this.validatePassCheck(bigDecimalGreaterThanEqualsValidator(), BIGDECIMAL_ABOVE);
    }

    private SpreadsheetDataValidator<BigDecimal> bigDecimalGreaterThanEqualsValidator() {
        return SpreadsheetDataValidators.bigDecimalGreaterThanEquals(BIGDECIMAL_BETWEEN);
    }

    // BigDecimal lt..........................................................................................

    @Test
    public void testBigDecimalLessThanBelow() {
        this.validatePassCheck(bigDecimalLessThanValidator(), BIGDECIMAL_BELOW);
    }

    @Test
    public void testBigDecimalLessThanLower() {
        this.validatePassCheck(bigDecimalLessThanValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testBigDecimalLessThanLessThanValue() {
        this.validateFailCheck(bigDecimalLessThanValidator(), BIGDECIMAL_BETWEEN);
    }

    @Test
    public void testBigDecimalLessThanUpper() {
        this.validateFailCheck(bigDecimalLessThanValidator(), BIGDECIMAL_UPPER);
    }

    @Test
    public void testBigDecimalLessThanAbove() {
        this.validateFailCheck(bigDecimalLessThanValidator(), BIGDECIMAL_ABOVE);
    }

    private SpreadsheetDataValidator<BigDecimal> bigDecimalLessThanValidator() {
        return SpreadsheetDataValidators.bigDecimalLessThan(BIGDECIMAL_BETWEEN);
    }

    // BigDecimal lte..........................................................................................

    @Test
    public void testBigDecimalLessThanEqualsBelow() {
        this.validatePassCheck(bigDecimalLessThanEqualsValidator(), BIGDECIMAL_BELOW);
    }

    @Test
    public void testBigDecimalLessThanEqualsLower() {
        this.validatePassCheck(bigDecimalLessThanEqualsValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testBigDecimalLessThanEqualsLessValue() {
        this.validatePassCheck(bigDecimalLessThanEqualsValidator(), BIGDECIMAL_BETWEEN);
    }

    @Test
    public void testBigDecimalLessThanEqualsUpper() {
        this.validateFailCheck(bigDecimalLessThanEqualsValidator(), BIGDECIMAL_UPPER);
    }

    @Test
    public void testBigDecimalLessThanEqualsAbove() {
        this.validateFailCheck(bigDecimalLessThanEqualsValidator(), BIGDECIMAL_ABOVE);
    }

    private SpreadsheetDataValidator<BigDecimal> bigDecimalLessThanEqualsValidator() {
        return SpreadsheetDataValidators.bigDecimalLessThanEquals(BIGDECIMAL_BETWEEN);
    }

    // BigDecimal between..........................................................................................

    @Test
    public void testBigDecimalBetweenBelow() {
        this.validateFailCheck(bigDecimalBetweenValidator(), BIGDECIMAL_BELOW);
    }

    @Test
    public void testBigDecimalBetweenLower() {
        this.validatePassCheck(bigDecimalBetweenValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testBigDecimalBetweenBetween() {
        this.validatePassCheck(bigDecimalBetweenValidator(), BIGDECIMAL_BETWEEN);
    }

    @Test
    public void testBigDecimalBetweenUpper() {
        this.validatePassCheck(bigDecimalBetweenValidator(), BIGDECIMAL_UPPER);
    }

    @Test
    public void testBigDecimalBetweenAbove() {
        this.validateFailCheck(bigDecimalBetweenValidator(), BIGDECIMAL_ABOVE);
    }

    private SpreadsheetDataValidator<BigDecimal> bigDecimalBetweenValidator() {
        return SpreadsheetDataValidators.bigDecimalBetween(BIGDECIMAL_LOWER, BIGDECIMAL_UPPER);
    }

    // BigDecimal NOT between..........................................................................................

    @Test
    public void testBigDecimalNotBetweenBelow() {
        this.validatePassCheck(bigDecimalNotBetweenValidator(), BIGDECIMAL_BELOW);
    }

    @Test
    public void testBigDecimalNotBetweenLower() {
        this.validateFailCheck(bigDecimalNotBetweenValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testBigDecimalNotBetweenBetween() {
        this.validateFailCheck(bigDecimalNotBetweenValidator(), BIGDECIMAL_BETWEEN);
    }

    @Test
    public void testBigDecimalNotBetweenUpper() {
        this.validateFailCheck(bigDecimalNotBetweenValidator(), BIGDECIMAL_UPPER);
    }

    @Test
    public void testBigDecimalNotBetweenAbove() {
        this.validatePassCheck(bigDecimalNotBetweenValidator(), BIGDECIMAL_ABOVE);
    }

    private SpreadsheetDataValidator<BigDecimal> bigDecimalNotBetweenValidator() {
        return SpreadsheetDataValidators.bigDecimalNotBetween(BIGDECIMAL_LOWER, BIGDECIMAL_UPPER);
    }

    // custom .........................................................................................

    @Test
    public void testCustomFormulaFalse() {
        this.validateFailCheck(this.customFormulaSpreadsheetDataValidator(), BIGDECIMAL_LOWER);
    }

    @Test
    public void testCustomFormulaTrue() {
        this.validatePassCheck(this.customFormulaSpreadsheetDataValidator(), BIGDECIMAL_UPPER);
    }

    private SpreadsheetDataValidator customFormulaSpreadsheetDataValidator() {
        return SpreadsheetDataValidators.customFormula(this.expression());
    }

    private ExpressionNode expression() {
        return ExpressionNode.greaterThan(
                ExpressionNode.reference(this.cellReference()),
                ExpressionNode.valueOrFail(BIGDECIMAL_BETWEEN));
    }

    // date ...........................................................................................................

    private final static LocalDate DATE_BELOW = LocalDate.of(1998, 10, 29);
    private final static LocalDate DATE_LOWER = LocalDate.of(1999, 11, 30);
    private final static LocalDate DATE_BETWEEN = LocalDate.of(2000, 12, 31);
    private final static LocalDate DATE_UPPER = LocalDate.of(2001, 1, 1);
    private final static LocalDate DATE_ABOVE = LocalDate.of(2002, 2, 2);

// Date eq..........................................................................................

    @Test
    public void testLocalDateEqualsBelow() {
        this.validateFailCheck(dateEqualsValidator(), DATE_BELOW);
    }

    @Test
    public void testLocalDateEqualsLower() {
        this.validateFailCheck(dateEqualsValidator(), DATE_LOWER);
    }

    @Test
    public void testLocalDateEqualsValue() {
        this.validatePassCheck(dateEqualsValidator(), DATE_BETWEEN);
    }

    @Test
    public void testLocalDateEqualsUpper() {
        this.validateFailCheck(dateEqualsValidator(), DATE_UPPER);
    }

    @Test
    public void testLocalDateEqualsAbove() {
        this.validateFailCheck(dateEqualsValidator(), DATE_ABOVE);
    }

    private SpreadsheetDataValidator<LocalDate> dateEqualsValidator() {
        return SpreadsheetDataValidators.localDateEquals(DATE_BETWEEN);
    }

// Date after..........................................................................................

    @Test
    public void testLocalDateAfterBelow() {
        this.validateFailCheck(localDateAfterValidator(), DATE_BELOW);
    }

    @Test
    public void testLocalDateAfterLower() {
        this.validateFailCheck(localDateAfterValidator(), DATE_LOWER);
    }

    @Test
    public void testLocalDateAfterValue() {
        this.validateFailCheck(localDateAfterValidator(), DATE_BETWEEN);
    }

    @Test
    public void testLocalDateAfterUpper() {
        this.validatePassCheck(localDateAfterValidator(), DATE_UPPER);
    }

    @Test
    public void testLocalDateAfterAbove() {
        this.validatePassCheck(localDateAfterValidator(), DATE_ABOVE);
    }

    private SpreadsheetDataValidator<LocalDate> localDateAfterValidator() {
        return SpreadsheetDataValidators.localDateAfter(DATE_BETWEEN);
    }

// Date after or on..........................................................................................

    @Test
    public void testLocalDateAfterOrOnBelow() {
        this.validateFailCheck(localDateAfterOrOnValidator(), DATE_BELOW);
    }

    @Test
    public void testLocalDateAfterOrOnLower() {
        this.validateFailCheck(localDateAfterOrOnValidator(), DATE_LOWER);
    }

    @Test
    public void testLocalDateAfterOrOnValue() {
        this.validatePassCheck(localDateAfterOrOnValidator(), DATE_BETWEEN);
    }

    @Test
    public void testLocalDateAfterOrOnUpper() {
        this.validatePassCheck(localDateAfterOrOnValidator(), DATE_UPPER);
    }

    @Test
    public void testLocalDateAfterOrOnAbove() {
        this.validatePassCheck(localDateAfterOrOnValidator(), DATE_ABOVE);
    }

    private SpreadsheetDataValidator<LocalDate> localDateAfterOrOnValidator() {
        return SpreadsheetDataValidators.localDateAfterOrOn(DATE_BETWEEN);
    }

// Date lt..........................................................................................

    @Test
    public void testLocalDateBeforeBelow() {
        this.validatePassCheck(localDateBeforeValidator(), DATE_BELOW);
    }

    @Test
    public void testLocalDateBeforeLower() {
        this.validatePassCheck(localDateBeforeValidator(), DATE_LOWER);
    }

    @Test
    public void testLocalDateBeforeBeforeValue() {
        this.validateFailCheck(localDateBeforeValidator(), DATE_BETWEEN);
    }

    @Test
    public void testLocalDateBeforeUpper() {
        this.validateFailCheck(localDateBeforeValidator(), DATE_UPPER);
    }

    @Test
    public void testLocalDateBeforeAbove() {
        this.validateFailCheck(localDateBeforeValidator(), DATE_ABOVE);
    }

    private SpreadsheetDataValidator<LocalDate> localDateBeforeValidator() {
        return SpreadsheetDataValidators.localDateBefore(DATE_BETWEEN);
    }

// Date lte..........................................................................................

    @Test
    public void testLocalDateBeforeOrOnBelow() {
        this.validatePassCheck(localDateBeforeOrOnValidator(), DATE_BELOW);
    }

    @Test
    public void testLocalDateBeforeOrOnLower() {
        this.validatePassCheck(localDateBeforeOrOnValidator(), DATE_LOWER);
    }

    @Test
    public void testLocalDateBeforeOrOnLessValue() {
        this.validatePassCheck(localDateBeforeOrOnValidator(), DATE_BETWEEN);
    }

    @Test
    public void testLocalDateBeforeOrOnUpper() {
        this.validateFailCheck(localDateBeforeOrOnValidator(), DATE_UPPER);
    }

    @Test
    public void testLocalDateBeforeOrOnAbove() {
        this.validateFailCheck(localDateBeforeOrOnValidator(), DATE_ABOVE);
    }

    private SpreadsheetDataValidator<LocalDate> localDateBeforeOrOnValidator() {
        return SpreadsheetDataValidators.localDateBeforeOrOn(DATE_BETWEEN);
    }

// Date between..........................................................................................

    @Test
    public void testLocalDateBetweenBelow() {
        this.validateFailCheck(localDateBetweenValidator(), DATE_BELOW);
    }

    @Test
    public void testLocalDateBetweenLower() {
        this.validatePassCheck(localDateBetweenValidator(), DATE_LOWER);
    }

    @Test
    public void testLocalDateBetweenBetween() {
        this.validatePassCheck(localDateBetweenValidator(), DATE_BETWEEN);
    }

    @Test
    public void testLocalDateBetweenUpper() {
        this.validatePassCheck(localDateBetweenValidator(), DATE_UPPER);
    }

    @Test
    public void testLocalDateBetweenAbove() {
        this.validateFailCheck(localDateBetweenValidator(), DATE_ABOVE);
    }

    private SpreadsheetDataValidator<LocalDate> localDateBetweenValidator() {
        return SpreadsheetDataValidators.localDateBetween(DATE_LOWER, DATE_UPPER);
    }

// Date NOT between..........................................................................................

    @Test
    public void testLocalDateNotBetweenBelow() {
        this.validatePassCheck(localDateNotBetweenValidator(), DATE_BELOW);
    }

    @Test
    public void testLocalDateNotBetweenLower() {
        this.validateFailCheck(localDateNotBetweenValidator(), DATE_LOWER);
    }

    @Test
    public void testLocalDateNotBetweenBetween() {
        this.validateFailCheck(localDateNotBetweenValidator(), DATE_BETWEEN);
    }

    @Test
    public void testLocalDateNotBetweenUpper() {
        this.validateFailCheck(localDateNotBetweenValidator(), DATE_UPPER);
    }

    @Test
    public void testLocalDateNotBetweenAbove() {
        this.validatePassCheck(localDateNotBetweenValidator(), DATE_ABOVE);
    }

    private SpreadsheetDataValidator<LocalDate> localDateNotBetweenValidator() {
        return SpreadsheetDataValidators.localDateNotBetween(DATE_LOWER, DATE_UPPER);
    }

    // text ...........................................................................................................

    @Test
    public void testTextAbsoluteUrlPass() {
        this.validatePassCheck(SpreadsheetDataValidators.textAbsoluteUrl(), "http://example.com");
    }

    @Test
    public void testTextAbsoluteUrlFail() {
        this.validateFailCheck(SpreadsheetDataValidators.textAbsoluteUrl(), "abc123");
    }

    @Test
    public void testTextCommaSeparatedPass() {
        this.validatePassCheck(this.textCommaSeparated(), "abc");
    }

    @Test
    public void testTextCommaSeparatedFail() {
        this.validateFailCheck(this.textCommaSeparated(), "not!");
    }

    @Test
    public void testTextCommaSeparatedDifferentCaseFail() {
        this.validateFailCheck(this.textCommaSeparated(), "ABC");
    }

    private SpreadsheetDataValidator<String> textCommaSeparated() {
        return SpreadsheetDataValidators.textCommaSeparated("abc,def,ghi");
    }

    @Test
    public void testTextContainsPass() {
        this.validatePassCheck(this.textContains(), "abc");
    }

    @Test
    public void testTextContainsFail() {
        this.validateFailCheck(this.textContains(), "doesnt");
    }

    @Test
    public void testTextContainsDifferentCaseFail() {
        this.validateFailCheck(this.textContains(), "ABC");
    }

    private SpreadsheetDataValidator<String> textContains() {
        return SpreadsheetDataValidators.textContains("a");
    }

    @Test
    public void testTextDoesntContainPass() {
        this.validatePassCheck(this.textDoesntContain(), "def");
    }

    @Test
    public void testTextDoesntContainFail() {
        this.validateFailCheck(this.textDoesntContain(), "abc");
    }

    @Test
    public void testTextDoesntContainDifferentCasePass() {
        this.validatePassCheck(this.textDoesntContain(), "ABC");
    }

    private SpreadsheetDataValidator<String> textDoesntContain() {
        return SpreadsheetDataValidators.textDoesntContain("a");
    }

    @Test
    public void testTextEmailPass() {
        this.validatePassCheck(SpreadsheetDataValidators.textEmail(), "user@example.com");
    }

    @Test
    public void testTextEmailFail() {
        this.validateFailCheck(SpreadsheetDataValidators.textEmail(), "abc123");
    }

    @Test
    public void testTextEqualsPass() {
        this.validatePassCheck(this.textEquals(), "abc");
    }

    @Test
    public void testTextEqualsFail() {
        this.validateFailCheck(this.textEquals(), "different-fails");
    }

    @Test
    public void testTextEqualsDifferentCaseFail() {
        this.validateFailCheck(this.textEquals(), "ABC");
    }

    private SpreadsheetDataValidator<String> textEquals() {
        return SpreadsheetDataValidators.textEquals("abc");
    }

    // helpers....................................................................................................

    private <TT> void validatePassCheck(final SpreadsheetDataValidator<TT> validator, final TT value) {
        this.validateAndCheck(validator, value, true);
    }

    private <TT> void validateFailCheck(final SpreadsheetDataValidator<TT> validator, final TT value) {
        this.validateAndCheck(validator, value, false);
    }

    private <TT> void validateAndCheck(final SpreadsheetDataValidator<TT> validator,
                                       final TT value,
                                       final boolean expected) {
        assertEquals(expected,
                validator.validate(value, this.createContext(value)),
                ()-> validator + " " + CharSequences.quoteIfChars(value));
    }

    private SpreadsheetDataValidatorContext createContext(final Object value) {
        return BasicSpreadsheetDataValidatorContext.with(this.cellReference(), value, this.expressionEvaluationContext());
    }

    final ExpressionReference cellReference() {
        return SpreadsheetReferenceKind.RELATIVE.column(1).setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    final ExpressionEvaluationContext expressionEvaluationContext() {
        final Converter all = Converters.collection(
                Lists.of(Converters.simple(),
                        Converters.truthyNumberBoolean()));

        return new FakeExpressionEvaluationContext() {
            @Override
            public <T> T convert(final Object value, final Class<T> target) {
                return all.convert(value, target, ConverterContexts.basic(this));
            }
        };
    }

    @Override
    protected Class<SpreadsheetDataValidators> type() {
        return SpreadsheetDataValidators.class;
    }

    @Override
    protected boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
