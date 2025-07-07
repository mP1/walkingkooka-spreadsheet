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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.validation.ValidationError;

public final class SpreadsheetConverterTextToValidationErrorTest extends SpreadsheetConverterTestCase<SpreadsheetConverterTextToValidationError>
    implements SpreadsheetMetadataTesting {

    @Test
    public void testConvertEmptyString() {
        this.convertFails(
            SpreadsheetConverterTextToValidationError.INSTANCE,
            "",
            ValidationError.class,
            this.createContext(SpreadsheetSelection.A1),
            "Empty \"text\""
        );
    }

    @Test
    public void testConvertStringWithValueMessageCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetError spreadsheetError = SpreadsheetErrorKind.VALUE.setMessage(
            "Message123"
        );
        final ValidationError<SpreadsheetExpressionReference> validationError = spreadsheetError.toValidationError(cell);

        this.convertAndCheck(
            validationError.text(),
            ValidationError.class,
            this.createContext(cell),
            validationError
        );
    }

    @Test
    public void testConvertCharSequenceWithValueMessageCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetError spreadsheetError = SpreadsheetErrorKind.VALUE.setMessage(
            "Message123"
        );
        final ValidationError<SpreadsheetExpressionReference> validationError = spreadsheetError.toValidationError(cell);

        this.convertAndCheck(
            new StringBuilder(
                validationError.text()
            ),
            ValidationError.class,
            this.createContext(cell),
            validationError
        );
    }

    @Test
    public void testConvertStringWithValueMessageLabel() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final SpreadsheetError spreadsheetError = SpreadsheetErrorKind.VALUE.setMessage("Message123");
        final ValidationError<SpreadsheetExpressionReference> validationError = spreadsheetError.toValidationError(label);

        this.convertAndCheck(
            validationError.text(),
            ValidationError.class,
            this.createContext(label),
            validationError
        );
    }

    @Test
    public void testConvertStringWithOnlyMessage() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final String message = "Message123";

        this.convertAndCheck(
            message,
            ValidationError.class,
            this.createContext(cell),
            SpreadsheetError.parse(message)
                .toValidationError(cell)
        );
    }

    @Override
    public SpreadsheetConverterTextToValidationError createConverter() {
        return SpreadsheetConverterTextToValidationError.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    private SpreadsheetConverterContext createContext(final SpreadsheetExpressionReference cellOrLabel) {
        return new FakeSpreadsheetConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.textToText();

            @Override
            public SpreadsheetExpressionReference validationReference() {
                return cellOrLabel;
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterTextToValidationError.INSTANCE,
            "String to ValidationError"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterTextToValidationError> type() {
        return SpreadsheetConverterTextToValidationError.class;
    }
}
