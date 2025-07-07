
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
import walkingkooka.spreadsheet.SpreadsheetErrorException;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.tree.expression.ExpressionNumber;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetConverterSpreadsheetErrorThrowingTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetErrorThrowing> {

    @Test
    public void testConvertNonErrorFails() {
        this.convertFails(
            15,
            String.class
        );
    }

    @Test
    public void testConvertErrorToThrows() {
        assertThrows(
            SpreadsheetErrorException.class,
            () -> SpreadsheetConverterSpreadsheetErrorThrowing.INSTANCE.convert(
                SpreadsheetErrorKind.ERROR.setMessage("Ignored"),
                ExpressionNumber.class,
                this.createContext()
            )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterSpreadsheetErrorThrowing.INSTANCE,
            "throws SpreadsheetError"
        );
    }

    @Override
    public SpreadsheetConverterSpreadsheetErrorThrowing createConverter() {
        return SpreadsheetConverterSpreadsheetErrorThrowing.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetErrorThrowing> type() {
        return SpreadsheetConverterSpreadsheetErrorThrowing.class;
    }
}
