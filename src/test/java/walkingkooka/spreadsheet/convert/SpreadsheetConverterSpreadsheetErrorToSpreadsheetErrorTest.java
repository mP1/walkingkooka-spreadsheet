
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
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;

public final class SpreadsheetConverterSpreadsheetErrorToSpreadsheetErrorTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetErrorToSpreadsheetError> {

    @Test
    public void testConvertNonErrorFails() {
        this.convertFails(15, String.class);
    }

    @Test
    public void testConvertStringToSpreadsheetErrorFails() {
        this.convertFails(
            "Hello",
            SpreadsheetError.class
        );
    }

    @Test
    public void testConvertSpreadsheetErrorToSpreadsheetError() {
        this.convertAndCheck(
            SpreadsheetErrorKind.ERROR.setMessage("Message will be ignored")
        );
    }

    @Override
    public SpreadsheetConverterSpreadsheetErrorToSpreadsheetError createConverter() {
        return SpreadsheetConverterSpreadsheetErrorToSpreadsheetError.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterSpreadsheetErrorToSpreadsheetError.INSTANCE,
            "SpreadsheetError to SpreadsheetError"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetErrorToSpreadsheetError> type() {
        return SpreadsheetConverterSpreadsheetErrorToSpreadsheetError.class;
    }
}
