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
import walkingkooka.spreadsheet.SpreadsheetStrings;

public final class SpreadsheetConverterSpreadsheetBooleanToTextTest extends SpreadsheetConverterTestCase<SpreadsheetConverterBooleanToText> {

    @Test
    public void testConvertBooleanTrueToString() {
        this.convertAndCheck(
            Boolean.TRUE,
            SpreadsheetStrings.BOOLEAN_TRUE
        );
    }

    @Test
    public void testConvertBooleanFalseToString() {
        this.convertAndCheck(
            Boolean.FALSE,
            SpreadsheetStrings.BOOLEAN_FALSE
        );
    }

    @Test
    public void testConvertNullToStringFails() {
        this.convertFails(
            null,
            String.class
        );
    }

    @Test
    public void testConvertStringToStringFails() {
        this.convertFails(
            "TRUE",
            String.class
        );
    }

    @Override
    public SpreadsheetConverterBooleanToText createConverter() {
        return SpreadsheetConverterBooleanToText.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterBooleanToText.INSTANCE,
            "Boolean to String"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterBooleanToText> type() {
        return SpreadsheetConverterBooleanToText.class;
    }
}
