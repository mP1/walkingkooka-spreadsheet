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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting2;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MissingSpreadsheetMetadataPropertyNameExceptionTest implements ThrowableTesting2<MissingSpreadsheetMetadataPropertyNameException> {

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testWithNullPropertyNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> new MissingSpreadsheetMetadataPropertyNameException(null)
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.SPREADSHEET_ID;
        final MissingSpreadsheetMetadataPropertyNameException exception = new MissingSpreadsheetMetadataPropertyNameException(propertyName);
        this.checkEquals(
            propertyName,
            exception.propertyName()
        );

        this.getMessageAndCheck(
            exception,
            "Metadata: Missing property value \"spreadsheetId\""
        );
    }

    // class............................................................................................................

    @Override
    public Class<MissingSpreadsheetMetadataPropertyNameException> type() {
        return MissingSpreadsheetMetadataPropertyNameException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
