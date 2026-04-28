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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.ValueTesting;
import walkingkooka.naming.HasNameTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.StandardThrowableTesting;

public final class SpreadsheetMetadataPropertyValueExceptionTest implements StandardThrowableTesting<SpreadsheetMetadataPropertyValueException>,
    HasNameTesting<SpreadsheetMetadataPropertyName<?>>,
    ValueTesting {

    @Override
    public void testWithMessage() {
    }

    @Override
    public void testWithMessageAndCause() {
    }

    @Test
    public void testCreate() {
        final SpreadsheetMetadataPropertyValueException throwable = new SpreadsheetMetadataPropertyValueException(MESSAGE,
            this.name(),
            this.value());
        this.getMessageAndCheck(
            throwable,
            "Metadata auditInfo=\"abc123\", message"
        );
        this.nameValueAndCheck(throwable);
    }

    @Test
    public void testCreateWithThrowable() {
        final SpreadsheetMetadataPropertyValueException throwable = new SpreadsheetMetadataPropertyValueException(
            MESSAGE,
            this.name(),
            this.value(),
            CAUSE
        );
        this.getMessageAndCheck(
            throwable,
            "Metadata auditInfo=\"abc123\", message"
        );
        this.nameValueAndCheck(throwable);
        this.getCauseAndCheck(throwable, CAUSE);
    }

    private void nameValueAndCheck(final SpreadsheetMetadataPropertyValueException throwable) {
        this.nameAndCheck(
            throwable,
            this.name()
        );
        this.valueAndCheck(
            throwable,
            this.value()
        );
    }

    @Override
    public SpreadsheetMetadataPropertyValueException createThrowable(final String message) {
        return new SpreadsheetMetadataPropertyValueException(message, this.name(), this.value());
    }

    @Override
    public SpreadsheetMetadataPropertyValueException createThrowable(final String message, final Throwable cause) {
        return new SpreadsheetMetadataPropertyValueException(message, this.name(), this.value(), cause);
    }

    @SuppressWarnings("SameReturnValue")
    private SpreadsheetMetadataPropertyName<?> name() {
        return SpreadsheetMetadataPropertyName.AUDIT_INFO;
    }

    @SuppressWarnings("SameReturnValue")
    private Object value() {
        return "abc123";
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueException> type() {
        return SpreadsheetMetadataPropertyValueException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
