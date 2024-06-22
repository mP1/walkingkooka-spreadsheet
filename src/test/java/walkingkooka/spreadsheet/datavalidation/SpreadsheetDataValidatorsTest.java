/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;

import java.lang.reflect.Method;
import java.time.LocalDate;

public final class SpreadsheetDataValidatorsTest implements ClassTesting2<SpreadsheetDataValidators>,
        PublicStaticHelperTesting<SpreadsheetDataValidators> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    private static ExpressionNumber expressionNumber(final int value) {
        return EXPRESSION_NUMBER_KIND.create(value);
    }

    private final static ExpressionNumber EXPRESSION_NUMBER_BELOW = expressionNumber(5);
    private final static ExpressionNumber EXPRESSION_NUMBER_LOWER = expressionNumber(10);
    private final static ExpressionNumber EXPRESSION_NUMBER_BETWEEN = expressionNumber(15);
    private final static ExpressionNumber EXPRESSION_NUMBER_UPPER = expressionNumber(20);
    private final static ExpressionNumber EXPRESSION_NUMBER_ABOVE = expressionNumber(30);

    // ExpressionNumber eq..........................................................................................

    @Test
    public void testExpressionNumberEqualsBelow() {
        this.validateFailCheck(expressionNumberEqualsValidator(), EXPRESSION_NUMBER_BELOW);
    }

    @Test
    public void testExpressionNumberEqualsLower() {
        this.validateFailCheck(expressionNumberEqualsValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testExpressionNumberEqualsValue() {
        this.validatePassCheck(expressionNumberEqualsValidator(), EXPRESSION_NUMBER_BETWEEN);
    }

    @Test
    public void testExpressionNumberEqualsUpper() {
        this.validateFailCheck(expressionNumberEqualsValidator(), EXPRESSION_NUMBER_UPPER);
    }

    @Test
    public void testExpressionNumberEqualsAbove() {
        this.validateFailCheck(expressionNumberEqualsValidator(), EXPRESSION_NUMBER_ABOVE);
    }

    private SpreadsheetDataValidator<ExpressionNumber> expressionNumberEqualsValidator() {
        return SpreadsheetDataValidators.expressionNumberEquals(EXPRESSION_NUMBER_BETWEEN);
    }

    // ExpressionNumber eq..........................................................................................

    @Test
    public void testExpressionNumberNotEqualsBelow() {
        this.validatePassCheck(expressionNumberNotEqualsValidator(), EXPRESSION_NUMBER_BELOW);
    }

    @Test
    public void testExpressionNumberNotEqualsLower() {
        this.validatePassCheck(expressionNumberNotEqualsValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testExpressionNumberNotEqualsValue() {
        this.validateFailCheck(expressionNumberNotEqualsValidator(), EXPRESSION_NUMBER_BETWEEN);
    }

    @Test
    public void testExpressionNumberNotEqualsUpper() {
        this.validatePassCheck(expressionNumberNotEqualsValidator(), EXPRESSION_NUMBER_UPPER);
    }

    @Test
    public void testExpressionNumberNotEqualsAbove() {
        this.validatePassCheck(expressionNumberNotEqualsValidator(), EXPRESSION_NUMBER_ABOVE);
    }

    private SpreadsheetDataValidator<ExpressionNumber> expressionNumberNotEqualsValidator() {
        return SpreadsheetDataValidators.expressionNumberNotEquals(EXPRESSION_NUMBER_BETWEEN);
    }
    // ExpressionNumber gt..........................................................................................

    @Test
    public void testExpressionNumberGreaterThanBelow() {
        this.validateFailCheck(expressionNumberGreaterThanValidator(), EXPRESSION_NUMBER_BELOW);
    }

    @Test
    public void testExpressionNumberGreaterThanLower() {
        this.validateFailCheck(expressionNumberGreaterThanValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testExpressionNumberGreaterThanValue() {
        this.validateFailCheck(expressionNumberGreaterThanValidator(), EXPRESSION_NUMBER_BETWEEN);
    }

    @Test
    public void testExpressionNumberGreaterThanUpper() {
        this.validatePassCheck(expressionNumberGreaterThanValidator(), EXPRESSION_NUMBER_UPPER);
    }

    @Test
    public void testExpressionNumberGreaterThanAbove() {
        this.validatePassCheck(expressionNumberGreaterThanValidator(), EXPRESSION_NUMBER_ABOVE);
    }

    private SpreadsheetDataValidator<ExpressionNumber> expressionNumberGreaterThanValidator() {
        return SpreadsheetDataValidators.expressionNumberGreaterThan(EXPRESSION_NUMBER_BETWEEN);
    }

    // ExpressionNumber gte..........................................................................................

    @Test
    public void testExpressionNumberGreaterThanEqualsBelow() {
        this.validateFailCheck(expressionNumberGreaterThanEqualsValidator(), EXPRESSION_NUMBER_BELOW);
    }

    @Test
    public void testExpressionNumberGreaterThanEqualsLower() {
        this.validateFailCheck(expressionNumberGreaterThanEqualsValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testExpressionNumberGreaterThanEqualsValue() {
        this.validatePassCheck(expressionNumberGreaterThanEqualsValidator(), EXPRESSION_NUMBER_BETWEEN);
    }

    @Test
    public void testExpressionNumberGreaterThanEqualsUpper() {
        this.validatePassCheck(expressionNumberGreaterThanEqualsValidator(), EXPRESSION_NUMBER_UPPER);
    }

    @Test
    public void testExpressionNumberGreaterThanEqualsAbove() {
        this.validatePassCheck(expressionNumberGreaterThanEqualsValidator(), EXPRESSION_NUMBER_ABOVE);
    }

    private SpreadsheetDataValidator<ExpressionNumber> expressionNumberGreaterThanEqualsValidator() {
        return SpreadsheetDataValidators.expressionNumberGreaterThanEquals(EXPRESSION_NUMBER_BETWEEN);
    }

    // ExpressionNumber lt..........................................................................................

    @Test
    public void testExpressionNumberLessThanBelow() {
        this.validatePassCheck(expressionNumberLessThanValidator(), EXPRESSION_NUMBER_BELOW);
    }

    @Test
    public void testExpressionNumberLessThanLower() {
        this.validatePassCheck(expressionNumberLessThanValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testExpressionNumberLessThanLessThanValue() {
        this.validateFailCheck(expressionNumberLessThanValidator(), EXPRESSION_NUMBER_BETWEEN);
    }

    @Test
    public void testExpressionNumberLessThanUpper() {
        this.validateFailCheck(expressionNumberLessThanValidator(), EXPRESSION_NUMBER_UPPER);
    }

    @Test
    public void testExpressionNumberLessThanAbove() {
        this.validateFailCheck(expressionNumberLessThanValidator(), EXPRESSION_NUMBER_ABOVE);
    }

    private SpreadsheetDataValidator<ExpressionNumber> expressionNumberLessThanValidator() {
        return SpreadsheetDataValidators.expressionNumberLessThan(EXPRESSION_NUMBER_BETWEEN);
    }

    // ExpressionNumber lte..........................................................................................

    @Test
    public void testExpressionNumberLessThanEqualsBelow() {
        this.validatePassCheck(expressionNumberLessThanEqualsValidator(), EXPRESSION_NUMBER_BELOW);
    }

    @Test
    public void testExpressionNumberLessThanEqualsLower() {
        this.validatePassCheck(expressionNumberLessThanEqualsValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testExpressionNumberLessThanEqualsLessValue() {
        this.validatePassCheck(expressionNumberLessThanEqualsValidator(), EXPRESSION_NUMBER_BETWEEN);
    }

    @Test
    public void testExpressionNumberLessThanEqualsUpper() {
        this.validateFailCheck(expressionNumberLessThanEqualsValidator(), EXPRESSION_NUMBER_UPPER);
    }

    @Test
    public void testExpressionNumberLessThanEqualsAbove() {
        this.validateFailCheck(expressionNumberLessThanEqualsValidator(), EXPRESSION_NUMBER_ABOVE);
    }

    private SpreadsheetDataValidator<ExpressionNumber> expressionNumberLessThanEqualsValidator() {
        return SpreadsheetDataValidators.expressionNumberLessThanEquals(EXPRESSION_NUMBER_BETWEEN);
    }

    // ExpressionNumber between..........................................................................................

    @Test
    public void testExpressionNumberBetweenBelow() {
        this.validateFailCheck(expressionNumberBetweenValidator(), EXPRESSION_NUMBER_BELOW);
    }

    @Test
    public void testExpressionNumberBetweenLower() {
        this.validatePassCheck(expressionNumberBetweenValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testExpressionNumberBetweenBetween() {
        this.validatePassCheck(expressionNumberBetweenValidator(), EXPRESSION_NUMBER_BETWEEN);
    }

    @Test
    public void testExpressionNumberBetweenUpper() {
        this.validatePassCheck(expressionNumberBetweenValidator(), EXPRESSION_NUMBER_UPPER);
    }

    @Test
    public void testExpressionNumberBetweenAbove() {
        this.validateFailCheck(expressionNumberBetweenValidator(), EXPRESSION_NUMBER_ABOVE);
    }

    private SpreadsheetDataValidator<ExpressionNumber> expressionNumberBetweenValidator() {
        return SpreadsheetDataValidators.expressionNumberBetween(EXPRESSION_NUMBER_LOWER, EXPRESSION_NUMBER_UPPER);
    }

    // ExpressionNumber NOT between..........................................................................................

    @Test
    public void testExpressionNumberNotBetweenBelow() {
        this.validatePassCheck(expressionNumberNotBetweenValidator(), EXPRESSION_NUMBER_BELOW);
    }

    @Test
    public void testExpressionNumberNotBetweenLower() {
        this.validateFailCheck(expressionNumberNotBetweenValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testExpressionNumberNotBetweenBetween() {
        this.validateFailCheck(expressionNumberNotBetweenValidator(), EXPRESSION_NUMBER_BETWEEN);
    }

    @Test
    public void testExpressionNumberNotBetweenUpper() {
        this.validateFailCheck(expressionNumberNotBetweenValidator(), EXPRESSION_NUMBER_UPPER);
    }

    @Test
    public void testExpressionNumberNotBetweenAbove() {
        this.validatePassCheck(expressionNumberNotBetweenValidator(), EXPRESSION_NUMBER_ABOVE);
    }

    private SpreadsheetDataValidator<ExpressionNumber> expressionNumberNotBetweenValidator() {
        return SpreadsheetDataValidators.expressionNumberNotBetween(EXPRESSION_NUMBER_LOWER, EXPRESSION_NUMBER_UPPER);
    }

    // custom .........................................................................................

    @Test
    public void testCustomFormulaFalse() {
        this.validateFailCheck(this.customFormulaSpreadsheetDataValidator(), EXPRESSION_NUMBER_LOWER);
    }

    @Test
    public void testCustomFormulaTrue() {
        this.validatePassCheck(this.customFormulaSpreadsheetDataValidator(), EXPRESSION_NUMBER_UPPER);
    }

    private SpreadsheetDataValidator<Object> customFormulaSpreadsheetDataValidator() {
        return SpreadsheetDataValidators.customFormula(this.expression());
    }

    private Expression expression() {
        return Expression.greaterThan(
                Expression.reference(this.cellReference()),
                Expression.value(EXPRESSION_NUMBER_BETWEEN)
        );
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
        this.validatePassCheck(SpreadsheetDataValidators.textAbsoluteUrl(), "https://example.com");
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
        this.checkEquals(expected,
                validator.validate(value, this.createContext(value)),
                () -> validator + " " + CharSequences.quoteIfChars(value));
    }

    private SpreadsheetDataValidatorContext createContext(final Object value) {
        return BasicSpreadsheetDataValidatorContext.with(this.cellReference(), value, this.expressionEvaluationContext());
    }

    ExpressionReference cellReference() {
        return SpreadsheetReferenceKind.RELATIVE.column(1).setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    ExpressionEvaluationContext expressionEvaluationContext() {
        final Converter<ExpressionNumberConverterContext> all = Converters.collection(
                Lists.of(Converters.simple(),
                        ExpressionNumber.toConverter(Converters.simple()),
                        ExpressionNumber.fromConverter(Converters.numberToBoolean())));

        return new FakeExpressionEvaluationContext() {
            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return all.convert(value,
                        target,
                        ExpressionNumberConverterContexts.basic(Converters.fake(),
                                ConverterContexts.basic(Converters.fake(),
                                        DateTimeContexts.fake(),
                                        DecimalNumberContexts.fake()),
                                EXPRESSION_NUMBER_KIND));
            }
        };
    }

    @Override
    public Class<SpreadsheetDataValidators> type() {
        return SpreadsheetDataValidators.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
