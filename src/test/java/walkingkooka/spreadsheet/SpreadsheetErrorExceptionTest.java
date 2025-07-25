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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetErrorExceptionTest implements ClassTesting<SpreadsheetErrorException> {

    private final static SpreadsheetError ERROR = SpreadsheetErrorKind.DIV0.setMessage("Hello");

    @Test
    public void testNewNullSpreadsheetErrorFails() {
        assertThrows(
            NullPointerException.class,
            () -> new SpreadsheetErrorException(null)
        );
    }

    @Test
    public void testNew() {
        final SpreadsheetErrorException exception = new SpreadsheetErrorException(ERROR);
        this.checkEquals(
            ERROR,
            exception.spreadsheetError(),
            "spreadsheetError"
        );
    }

    @Override
    public Class<SpreadsheetErrorException> type() {
        return SpreadsheetErrorException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
