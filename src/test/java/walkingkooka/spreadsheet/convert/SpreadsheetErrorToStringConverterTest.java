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
import walkingkooka.spreadsheet.SpreadsheetErrorKind;

public final class SpreadsheetErrorToStringConverterTest implements ConverterTesting2<SpreadsheetErrorToStringConverter, ConverterContext> {

    @Test
    public void testNonErrorFails() {
        this.convertFails(15, String.class);
    }

    @Test
    public void testNonErrorStringFails() {
        this.convertFails("Hello", String.class);
    }

    @Test
    public void testErrorErrorToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.ERROR;

        this.convertAndCheck(
                kind.setMessage("Message will be ignored"),
                String.class,
                kind.text()
        );
    }

    @Test
    public void testErrorDiv0ToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.DIV0;

        this.convertAndCheck(
                kind.setMessage("Message will be ignored2"),
                String.class,
                kind.text()
        );
    }

    @Test
    public void testToNumber() {
        this.convertFails(
                SpreadsheetErrorKind.DIV0.setMessage("!!"),
                Number.class
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetErrorToStringConverter.INSTANCE,
                "SpreadsheetError to String"
        );
    }

    @Override
    public SpreadsheetErrorToStringConverter createConverter() {
        return SpreadsheetErrorToStringConverter.INSTANCE;
    }

    @Override
    public ConverterContext createContext() {
        return ConverterContexts.fake();
    }

    @Override
    public Class<SpreadsheetErrorToStringConverter> type() {
        return SpreadsheetErrorToStringConverter.class;
    }
}
