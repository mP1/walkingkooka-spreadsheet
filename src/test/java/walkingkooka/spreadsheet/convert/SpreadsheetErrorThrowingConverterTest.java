
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
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.spreadsheet.SpreadsheetErrorException;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.tree.expression.ExpressionNumber;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetErrorThrowingConverterTest implements ConverterTesting2<SpreadsheetErrorThrowingConverter, ConverterContext> {

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
                () -> SpreadsheetErrorThrowingConverter.INSTANCE.convert(
                        SpreadsheetErrorKind.ERROR.setMessage("Ignored"),
                        ExpressionNumber.class,
                        this.createContext()
                )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetErrorThrowingConverter.INSTANCE,
                "throws SpreadsheetError"
        );
    }

    @Override
    public SpreadsheetErrorThrowingConverter createConverter() {
        return SpreadsheetErrorThrowingConverter.INSTANCE;
    }

    @Override
    public ConverterContext createContext() {
        return ConverterContexts.fake();
    }

    @Override
    public Class<SpreadsheetErrorThrowingConverter> type() {
        return SpreadsheetErrorThrowingConverter.class;
    }
}
