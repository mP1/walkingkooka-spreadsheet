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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.HasConvertErrorTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.validation.HasValidationChoiceListTesting;
import walkingkooka.validation.ValidationChoice;
import walkingkooka.validation.ValidationChoiceList;
import walkingkooka.validation.ValidationError;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetErrorTest implements ParseStringTesting<SpreadsheetError>,
    ClassTesting2<SpreadsheetError>,
    HashCodeEqualsDefinedTesting2<SpreadsheetError>,
    JsonNodeMarshallingTesting<SpreadsheetError>,
    HasTextTesting,
    HasConvertErrorTesting,
    HasValidationChoiceListTesting,
    TreePrintableTesting,
    ToStringTesting<SpreadsheetError> {

    private final static SpreadsheetErrorKind KIND = SpreadsheetErrorKind.NA;
    private final static String MESSAGE = "message #1";
    private final static Optional<Object> VALUE = Optional.of(
        123
    );

    // with.............................................................................................................

    @Test
    public void testWithNullMessageFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetError.with(KIND, null, VALUE)
        );
    }

    @Test
    public void testWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetError.with(KIND, MESSAGE, null)
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, MESSAGE, VALUE);
        this.kindAndCheck(error, KIND);
        this.messageAndCheck(error, MESSAGE);
        this.valueAndCheck(error, VALUE);
    }

    @Test
    public void testWithEmptyMessage() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, "", VALUE);
        this.kindAndCheck(error, KIND);
        this.messageAndCheck(error, "");
        this.valueAndCheck(error, VALUE);
    }

    // cycle............................................................................................................

    @Test
    public void testCycle() {
        final SpreadsheetError error = SpreadsheetError.cycle(SpreadsheetSelection.A1);
        this.kindAndCheck(error, SpreadsheetErrorKind.REF);
        this.messageAndCheck(error, "Cycle involving \"A1\"");
        this.valueAndCheck(
            error,
            Optional.of(
                SpreadsheetSelection.A1
            )
        );
    }

    @Test
    public void testCycle2() {
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");

        final SpreadsheetError error = SpreadsheetError.cycle(b2);
        this.kindAndCheck(
            error,
            SpreadsheetErrorKind.REF
        );
        this.messageAndCheck(
            error,
            "Cycle involving \"$B$2\""
        );
        this.valueAndCheck(
            error,
            Optional.of(
                b2
            )
        );
    }

    // selectionDeleted................................................................................................

    @Test
    public void testSelectionDeleted() {
        final SpreadsheetError error = SpreadsheetError.selectionDeleted();
        this.kindAndCheck(error, SpreadsheetErrorKind.REF);
        this.messageAndCheck(error, "");
        this.valueAndCheck(error, SpreadsheetError.NO_VALUE);
    }

    // selectionNotFound................................................................................................

    @Test
    public void testSelectionNotFoundWithCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("A99");

        final SpreadsheetError error = SpreadsheetError.selectionNotFound(cell);
        this.kindAndCheck(error, SpreadsheetErrorKind.NAME);
        this.messageAndCheck(error, "Cell not found: \"A99\"");
        this.valueAndCheck(error, Optional.of(cell));
    }

    @Test
    public void testSelectionNotFoundWithLabel() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        final SpreadsheetError error = SpreadsheetError.selectionNotFound(label);
        this.kindAndCheck(error, SpreadsheetErrorKind.NAME);
        this.messageAndCheck(error, "Label not found: \"Label123\"");
        this.valueAndCheck(error, Optional.of(label));
    }

    // formatterNotFound................................................................................................

    @Test
    public void testFormatterNotFoundAndNullSpreadsheetFormatterName() {
        final SpreadsheetError error = SpreadsheetError.formatterNotFound(null);
        this.kindAndCheck(error, SpreadsheetErrorKind.ERROR);
        this.messageAndCheck(error, "Formatter not found");
        this.valueAndCheck(error, Optional.empty());
    }

    @Test
    public void testFormatterNotFoundAndValue() {
        final SpreadsheetFormatterName name = SpreadsheetFormatterName.DEFAULT_TEXT;

        final SpreadsheetError error = SpreadsheetError.formatterNotFound(name);
        this.kindAndCheck(error, SpreadsheetErrorKind.ERROR);
        this.messageAndCheck(error, "Formatter not found");
        this.valueAndCheck(error, Optional.ofNullable(name));
    }

    // functionNotFound.................................................................................................

    @Test
    public void testFunctionNotFound() {
        final ExpressionFunctionName function = SpreadsheetExpressionFunctions.name("function123");

        final SpreadsheetError error = SpreadsheetError.functionNotFound(function);
        this.kindAndCheck(error, SpreadsheetErrorKind.NAME);
        this.messageAndCheck(error, function.notFoundText());
        this.valueAndCheck(error, Optional.of(function));
    }

    // referenceNotFound................................................................................................

    @Test
    public void testReferenceNotFoundWithCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("A99");

        final SpreadsheetError error = SpreadsheetError.referenceNotFound(cell);
        this.kindAndCheck(error, SpreadsheetErrorKind.NAME);
        this.messageAndCheck(error, "Cell not found: \"A99\"");
        this.valueAndCheck(error, Optional.of(cell));
    }

    @Test
    public void testReferenceNotFoundWithLabel() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        final SpreadsheetError error = SpreadsheetError.referenceNotFound(label);
        this.kindAndCheck(error, SpreadsheetErrorKind.NAME);
        this.messageAndCheck(error, "Label not found: \"Label123\"");
        this.valueAndCheck(error, Optional.of(label));
    }

    @Test
    public void testReferenceNotFoundWithNonSpreadsheetExpressionReference() {
        final ExpressionReference reference = new FakeExpressionReference() {
            @Override
            public String toString() {
                return "123";
            }
        };

        final SpreadsheetError error = SpreadsheetError.referenceNotFound(reference);
        this.kindAndCheck(error, SpreadsheetErrorKind.NAME);
        this.messageAndCheck(error, "Missing \"123\"");
        this.valueAndCheck(
            error,
            Optional.of(reference)
        );
    }

    // validationErrors.................................................................................................

    @Test
    public void testValidationErrorsWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetError.validationErrors(null)
        );
    }

    @Test
    public void testValidationErrorWithEmptyErrors() {
        this.validationErrorsAndCheck(
            Lists.empty(),
            Optional.empty()
        );
    }

    @Test
    public void testValidationErrorWithEmptyMessageAndNoValue() {
        final SpreadsheetError error = SpreadsheetErrorKind.MISSING_PREFIX.toError();

        this.validationErrorsAndCheck(
            Lists.of(
                SpreadsheetForms.error(SpreadsheetSelection.A1)
            ),
            Optional.of(error)
        );
    }

    @Test
    public void testValidationErrorWithManyErrorsAllWithoutValues() {
        final SpreadsheetError error = SpreadsheetErrorKind.VALUE.setMessage("Message Hello 123");

        this.validationErrorsAndCheck(
            Lists.of(
                SpreadsheetForms.error(SpreadsheetSelection.A1)
                    .setMessage("#VALUE! Message Hello 123")
            ),
            Optional.of(error)
        );
    }

    @Test
    public void testValidationErrorWithSpreadsheetErrorKindValidatorAndValidationChoiceList() {
        final ValidationChoiceList choices = ValidationChoiceList.EMPTY.concat(
            ValidationChoice.with(
                "Label1",
                Optional.of(1)
            )
        ).concat(
            ValidationChoice.with(
                "Label22",
                Optional.of(22)
            )
        );

        this.validationErrorsAndCheck(
            Lists.of(
                SpreadsheetForms.error(SpreadsheetSelection.A1)
                    .setValue(
                        Optional.of(choices)
                    )
            ),
            Optional.of(
                SpreadsheetErrorKind.VALIDATION.toError()
                    .setValue(
                        Optional.of(
                            choices
                        )
                    )
            )
        );
    }

    @Test
    public void testValidationErrorWithSpreadsheetErrorKindErrorAndValidationChoiceList() {
        final ValidationChoiceList choices = ValidationChoiceList.EMPTY.concat(
            ValidationChoice.with(
                "Label1",
                Optional.of(1)
            )
        ).concat(
            ValidationChoice.with(
                "Label22",
                Optional.of(22)
            )
        );

        this.validationErrorsAndCheck(
            Lists.of(
                SpreadsheetForms.error(SpreadsheetSelection.A1)
                    .setMessage("#ERROR Message Hello 123")
                    .setValue(
                        Optional.of(choices)
                    )
            ),
            Optional.of(
                SpreadsheetErrorKind.ERROR.setMessageAndValue(
                    "Message Hello 123",
                    choices
                )
            )
        );
    }

    @Test
    public void testValidationErrorWithSpreadsheetErrorKindValueAndValidationChoiceList() {
        final ValidationChoiceList choices = ValidationChoiceList.EMPTY.concat(
            ValidationChoice.with(
                "Label1",
                Optional.of(1)
            )
        ).concat(
            ValidationChoice.with(
                "Label22",
                Optional.of(22)
            )
        );

        this.validationErrorsAndCheck(
            Lists.of(
                SpreadsheetForms.error(SpreadsheetSelection.A1)
                    .setMessage("#VALUE! Message Hello 123")
                    .setValue(
                        Optional.of(choices)
                    )
            ),
            Optional.of(
                SpreadsheetErrorKind.VALUE.setMessageAndValue(
                    "Message Hello 123",
                    choices
                )
            )
        );
    }

    private void validationErrorsAndCheck(final List<ValidationError<SpreadsheetExpressionReference>> errors,
                                          final Optional<SpreadsheetError> expected) {
        this.checkEquals(
            expected,
            SpreadsheetError.validationErrors(errors)
        );
    }

    // isMissingCell....................................................................................................

    @Test
    public void testIsMissingCellDIV0() {
        this.isMissingCellAndCheck(
            SpreadsheetErrorKind.DIV0.setMessage("Ignored"),
            false
        );
    }

    @Test
    public void testIsMissingCellDIV0WithValue() {
        this.isMissingCellAndCheck(
            SpreadsheetErrorKind.DIV0.setMessageAndValue(
                "Ignored",
                1
            ),
            false
        );
    }

    @Test
    public void testIsMissingCellNAMEWithCellReference() {
        this.isMissingCellAndCheck(
            SpreadsheetErrorKind.NAME.setMessageAndValue(
                "Ignored",
                SpreadsheetSelection.A1
            ),
            true
        );
    }

    @Test
    public void testIsMissingCellNAMEWithLabel() {
        this.isMissingCellAndCheck(
            SpreadsheetErrorKind.NAME.setMessageAndValue(
                "Ignored",
                SpreadsheetSelection.labelName("Label123")
            ),
            false
        );
    }

    private void isMissingCellAndCheck(final SpreadsheetError error,
                                       final boolean is) {
        this.checkEquals(
            is,
            error.isMissingCell(),
            () -> error + ".isMissingCell()"
        );
    }

    // setNameString....................................................................................................

    @Test
    public void testSetNameStringNonNAMEFails() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetErrorKind.DIV0.setMessage("Divide by zero!!!")
                .setNameString()
        );

        this.checkEquals(
            "SpreadsheetError.kind is not #NAME? but is #DIV/0!",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testSetNameStringNAME_STRING() {
        final SpreadsheetError error = SpreadsheetErrorKind.NAME_STRING.setMessageAndValue(
            "AAA",
            SpreadsheetSelection.A1
        );

        assertSame(
            error,
            error.setNameString()
        );
    }

    @Test
    public void testSetNameString() {
        final SpreadsheetError error = SpreadsheetError.selectionNotFound(
            SpreadsheetSelection.A1
        );

        this.checkEquals(
            SpreadsheetErrorKind.NAME_STRING.setMessageAndValue(
                error.message(),
                error.value().get()
            ),
            error.setNameString(),
            () -> error + ".setNameString"
        );
    }

    // setMessage.......................................................................................................

    @Test
    public void testSetMessageWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetError.with(KIND, MESSAGE, VALUE)
                .setMessage(null)
        );
    }

    @Test
    public void testSetMessageWithSame() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, MESSAGE, VALUE);
        assertSame(
            error,
            error.setMessage(MESSAGE)
        );
    }

    @Test
    public void testSetMessageWithDifferent() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, MESSAGE, VALUE);

        final String differentMessage = "different";
        final SpreadsheetError different = error.setMessage(differentMessage);

        this.kindAndCheck(different, KIND);
        this.messageAndCheck(different, differentMessage);
        this.valueAndCheck(different, VALUE);

        this.kindAndCheck(error, KIND);
        this.messageAndCheck(error, MESSAGE);
        this.valueAndCheck(error, VALUE);
    }

    @Test
    public void testSetMessageWithDifferentWhenKindEqualsValidation() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.VALIDATION;
        final SpreadsheetError error = SpreadsheetError.with(
            kind,
            MESSAGE,
            VALUE
        );

        final String differentMessage = "different";
        final SpreadsheetError different = error.setMessage(differentMessage);

        this.kindAndCheck(different, SpreadsheetErrorKind.ERROR);
        this.messageAndCheck(different, differentMessage);
        this.valueAndCheck(different, VALUE);

        this.kindAndCheck(error, kind);
        this.messageAndCheck(error, MESSAGE);
        this.valueAndCheck(error, VALUE);
    }

    // setValue.........................................................................................................

    @Test
    public void testSetValueWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetError.with(KIND, MESSAGE, VALUE)
                .setValue(null)
        );
    }

    @Test
    public void testSetValueWithSame() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, MESSAGE, VALUE);
        assertSame(
            error,
            error.setValue(VALUE)
        );
    }

    @Test
    public void testSetValueWithDifferent() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, MESSAGE, VALUE);

        final Optional<Object> differentValue = Optional.of("different");
        final SpreadsheetError different = error.setValue(differentValue);

        this.kindAndCheck(different, KIND);
        this.messageAndCheck(different, MESSAGE);
        this.valueAndCheck(different, differentValue);

        this.kindAndCheck(error, KIND);
        this.messageAndCheck(error, MESSAGE);
        this.valueAndCheck(error, VALUE);
    }

    // clearValue.......................................................................................................

    @Test
    public void testClearValue() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, MESSAGE, VALUE);
        final SpreadsheetError cleared = error.clearValue();

        assertNotSame(
            error,
            cleared
        );

        this.kindAndCheck(cleared, KIND);
        this.messageAndCheck(cleared, MESSAGE);
        this.valueAndCheck(cleared, SpreadsheetError.NO_VALUE);

        this.kindAndCheck(error, KIND);
        this.messageAndCheck(error, MESSAGE);
        this.valueAndCheck(error, VALUE);
    }

    private void kindAndCheck(final SpreadsheetError error,
                              final SpreadsheetErrorKind kind) {
        this.checkEquals(
            kind,
            error.kind(),
            "kind"
        );

        this.checkEquals(
            kind,
            error.spreadsheetErrorKind(),
            "spreadsheetErrorKind"
        );
    }

    private void messageAndCheck(final SpreadsheetError error,
                                 final String message) {
        this.checkEquals(
            message,
            error.message(),
            "message"
        );
    }

    private void valueAndCheck(final SpreadsheetError error,
                               final Optional<?> value) {
        this.checkEquals(
            value,
            error.value(),
            "value"
        );
    }

    // toValidationError................................................................................................

    @Test
    public void testToValidationError() {
        final String message = "Message123";
        final Optional<Object> value = Optional.of("Value456");

        final SpreadsheetError error = SpreadsheetError.with(
            SpreadsheetErrorKind.VALUE,
            message,
            value
        );

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        this.checkEquals(
            SpreadsheetForms.error(cell)
                .setMessage(message)
                .setValue(value),
            error.toValidationError(cell)
        );
    }

    // parseString......................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseEmptyString() {
        this.parseStringAndCheck(
            "",
            SpreadsheetErrorKind.MISSING_PREFIX.toError()
        );
    }

    @Test
    public void testParseMissingPrefix() {
        final String text = "Message123";

        this.parseStringAndCheck(
            text,
            SpreadsheetErrorKind.ERROR.setMessage(text)
        );
    }

    @Test
    public void testParseInvalidKindFails() {
        this.parseStringFails(
            "#Invalid123",
            new IllegalArgumentException("Invalid error kind")
        );
    }

    @Test
    public void testParseDiv0WithoutMessage() {
        this.parseStringAndCheck(
            "#DIV/0!",
            SpreadsheetErrorKind.DIV0.setMessage("")
        );
    }

    @Test
    public void testParseNaWithoutMessage2() {
        this.parseStringAndCheck(
            "#N/A",
            SpreadsheetErrorKind.NA.setMessage("")
        );
    }

    @Test
    public void testParseDiv0WithMessage() {
        this.parseStringAndCheck(
            "#DIV/0! message123",
            SpreadsheetErrorKind.DIV0.setMessage("message123")
        );
    }

    @Test
    public void testParseNaWithMessage2() {
        this.parseStringAndCheck(
            "#N/A message123",
            SpreadsheetErrorKind.NA.setMessage("message123")
        );
    }

    @Override
    public SpreadsheetError parseString(final String text) {
        return SpreadsheetError.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // HasText..........................................................................................................

    @Test
    public void testTextWithDiv0() {
        this.textAndParseStringAndTextCheck(
            SpreadsheetErrorKind.DIV0.setMessage("Hello")
        );
    }

    @Test
    public void testTextWithError() {
        this.textAndParseStringAndTextCheck(
            SpreadsheetErrorKind.ERROR.setMessage("Hello")
        );
    }

    @Test
    public void testTextWithNA() {
        this.textAndParseStringAndTextCheck(
            SpreadsheetErrorKind.NA.setMessage("Hello")
        );
    }

    @Test
    public void testTextWithRef() {
        this.textAndParseStringAndTextCheck(
            SpreadsheetErrorKind.REF.setMessage("Hello")
        );
    }

    @Test
    public void testTextWithRefIgnoresValue() {
        this.textAndParseStringAndTextCheck(
            SpreadsheetErrorKind.REF.setMessage("Hello")
                .setValue(
                    Optional.of(SpreadsheetSelection.A1)
                )
        );
    }

    @Test
    public void testTextWithReferenceNotFound() {
        this.textAndParseStringAndTextCheck(
            SpreadsheetError.referenceNotFound(SpreadsheetSelection.A1)
        );
    }

    private void textAndParseStringAndTextCheck(final SpreadsheetError error) {
        this.parseStringAndCheck(
            error.text(),
            error.kind()
                .setMessage("")
        );
    }

    //textIncludingMessage..............................................................................................

    @Test
    public void testTextIncludingMessageWithDiv0() {
        this.textIncludingMessageAndParseStringAndTextIncludingMessageCheck(
            SpreadsheetErrorKind.DIV0.setMessage("Hello")
        );
    }

    @Test
    public void testTextIncludingMessageWithError() {
        this.textIncludingMessageAndParseStringAndTextIncludingMessageCheck(
            SpreadsheetErrorKind.ERROR.setMessage("Hello")
        );
    }

    @Test
    public void testTextIncludingMessageWithNA() {
        this.textIncludingMessageAndParseStringAndTextIncludingMessageCheck(
            SpreadsheetErrorKind.NA.setMessage("Hello")
        );
    }

    @Test
    public void testTextIncludingMessageWithRef() {
        this.textIncludingMessageAndParseStringAndTextIncludingMessageCheck(
            SpreadsheetErrorKind.REF.setMessage("Hello")
        );
    }

    @Test
    public void testTextIncludingMessageWithRefIgnoresValue() {
        this.textIncludingMessageAndParseStringAndTextIncludingMessageCheck(
            SpreadsheetErrorKind.REF.setMessage("Hello")
                .setValue(
                    Optional.of(SpreadsheetSelection.A1)
                )
        );
    }

    @Test
    public void testTextIncludingMessageWithReferenceNotFound() {
        this.textIncludingMessageAndParseStringAndTextIncludingMessageCheck(
            SpreadsheetError.referenceNotFound(SpreadsheetSelection.A1)
        );
    }

    private void textIncludingMessageAndParseStringAndTextIncludingMessageCheck(final SpreadsheetError error) {
        this.parseStringAndCheck(
            error.textIncludingMessage(),
            SpreadsheetError.with(
                error.kind(),
                error.message(),
                SpreadsheetError.NO_VALUE
            )
        );
    }

    // HasConvertError..................................................................................................

    @Test
    public void testHasConvertErrorWithNULL() {
        this.convertErrorMessageAndCheck(
            SpreadsheetErrorKind.NULL.setMessage("Null blah blah")
        );
    }

    @Test
    public void testHasConvertErrorWithERROR() {
        this.convertErrorMessageAndCheck(
            SpreadsheetErrorKind.ERROR.setMessage("Null blah blah")
        );
    }

    @Test
    public void testHasConvertErrorWithValue() {
        final String message = "Hello123";

        this.convertErrorMessageAndCheck(
            SpreadsheetErrorKind.VALUE.setMessage(message),
            message
        );
    }

    // HasValidationChoiceList..........................................................................................

    @Test
    public void testValidationChoiceListWithEmptyValue() {
        this.validationChoiceListAndCheck(
            SpreadsheetErrorKind.VALUE.setMessage("No message")
        );
    }

    @Test
    public void testValidationChoiceListWithNonValidationChoiceListValue() {
        this.validationChoiceListAndCheck(
            SpreadsheetErrorKind.VALUE.setMessage("No message")
                .setValue(
                    Optional.of(
                        "@Not " + ValidationChoiceList.class.getSimpleName()
                    )
                )
        );
    }

    @Test
    public void testValidationChoiceListWithValidationErrorListMissingChoices() {
        this.validationChoiceListAndCheck(
            SpreadsheetError.validationErrors(
                SpreadsheetForms.errorList()
                    .concat(SpreadsheetForms.error(SpreadsheetSelection.A1)
                    )
            ).get()
        );
    }

    @Test
    public void testValidationChoiceListWithValidationChoiceListValue() {
        final ValidationChoiceList choices = ValidationChoiceList.EMPTY.concat(
            ValidationChoice.with(
                "Label1",
                Optional.of("Value1")
            )
        );

        this.validationChoiceListAndCheck(
            SpreadsheetError.validationErrors(
                SpreadsheetForms.errorList()
                    .concat(
                        SpreadsheetForms.error(SpreadsheetSelection.A1)
                            .setValue(
                                Optional.of(choices)
                            )
                    )
            ).get(),
            choices
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintOnlyKind() {
        this.treePrintAndCheck(
            SpreadsheetErrorKind.NA.toError(),
            "#N/A\n"
        );
    }

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createObject(),
            "#N/A\n" +
                "  \"message #1\"\n" +
                "  123\n"
        );
    }

    // equals..........................................................................................................

    @Test
    public void testEqualsDifferentKind() {
        this.checkNotEquals(
            SpreadsheetError.with(
                SpreadsheetErrorKind.NAME,
                MESSAGE,
                VALUE
            )
        );
    }

    @Test
    public void testEqualsDifferentMessage() {
        this.checkNotEquals(
            SpreadsheetError.with(
                KIND,
                "different",
                VALUE
            )
        );
    }

    @Test
    public void testEqualsMessageDifferentCase() {
        this.checkNotEquals(
            SpreadsheetError.with(
                KIND,
                MESSAGE.toUpperCase(),
                VALUE
            )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
            SpreadsheetError.with(
                SpreadsheetErrorKind.NAME,
                MESSAGE,
                Optional.of("different-value")
            )
        );
    }

    @Override
    public SpreadsheetError createObject() {
        return SpreadsheetError.with(
            KIND,
            MESSAGE,
            VALUE
        );
    }

    // json.............................................................................................................

    @Test
    public void testUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string(""));
    }

    @Test
    public void testUnmarshallString() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetError.KIND_PROPERTY,
                    KIND.name()
                ).set(
                    SpreadsheetError.MESSAGE_PROPERTY,
                    MESSAGE
                ).set(
                    SpreadsheetError.VALUE_PROPERTY,
                    this.marshallContext().
                        marshallWithType(VALUE.get())
                ),
            SpreadsheetError.with(
                KIND,
                MESSAGE,
                VALUE
            )
        );
    }

    @Test
    public void testUnmarshallWithSpreadsheetErrorKindValidator() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.VALIDATION;

        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetError.KIND_PROPERTY,
                    kind.name()
                ).set(
                    SpreadsheetError.VALUE_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(VALUE.get())
                ),
            SpreadsheetError.with(
                kind,
                "", // no message
                VALUE
            )
        );
    }

    @Test
    public void testUnmarshallWithoutMessage() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetError.KIND_PROPERTY,
                    KIND.name()
                ).set(
                    SpreadsheetError.VALUE_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(VALUE.get())
                ),
            SpreadsheetError.with(
                KIND,
                "", // no message
                VALUE
            )
        );
    }

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createObject(),
            JsonNode.object()
                .set(
                    SpreadsheetError.KIND_PROPERTY,
                    KIND.name()
                ).set(
                    SpreadsheetError.MESSAGE_PROPERTY,
                    MESSAGE
                ).set(
                    SpreadsheetError.VALUE_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(VALUE.get())
                )
        );
    }

    @Test
    public void testMarshallWithoutMessage() {
        this.marshallAndCheck(
            SpreadsheetError.with(
                KIND,
                "",
                VALUE
            ),
            JsonNode.object()
                .set(SpreadsheetError.KIND_PROPERTY, JsonNode.string(KIND.name()))
                .set(SpreadsheetError.VALUE_PROPERTY, this.marshallContext().marshallWithType(VALUE.get()))
        );
    }

    @Test
    public void testMarshallRoundtripKindMessageValue() {
        this.marshallRoundTripTwiceAndCheck(
            SpreadsheetError.selectionNotFound(
                SpreadsheetSelection.A1
            )
        );
    }

    @Test
    public void testMarshallRoundtripKindMessage() {
        this.marshallRoundTripTwiceAndCheck(
            SpreadsheetErrorKind.DIV0.setMessage("Divide by zero")
        );
    }

    @Test
    public void testMarshallRoundtripOnlyKind() {
        this.marshallRoundTripTwiceAndCheck(
            SpreadsheetErrorKind.ERROR.toError()
        );
    }

    @Override
    public SpreadsheetError createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetError unmarshall(final JsonNode jsonNode,
                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetError.unmarshall(jsonNode, context);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            KIND + " \"" + MESSAGE + "\" " + VALUE.get()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetError> type() {
        return SpreadsheetError.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
