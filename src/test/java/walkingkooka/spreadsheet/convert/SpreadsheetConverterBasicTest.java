
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

public final class SpreadsheetConverterBasicTest extends SpreadsheetConverterTestCase<SpreadsheetConverterBasic> {

    @Test
    public void testConvertNullToObject() {
        this.convertAndCheck(
            null,
            Object.class
        );
    }

    @Test
    public void testConvertNullToNumber() {
        this.convertAndCheck(
            null,
            Number.class
        );
    }

    @Test
    public void testConvertNullToString() {
        this.convertAndCheck(
            null,
            String.class
        );
    }

    @Test
    public void testConvertIntegerToNumber() {
        this.convertAndCheck(
            1,
            Number.class
        );
    }

    @Test
    public void testConvertStringToObject() {
        this.convertAndCheck(
            "Hello",
            Object.class,
            "Hello"
        );
    }

    @Test
    public void testConvertStringToString() {
        this.convertAndCheck(
            "Hello"
        );
    }

    @Override
    public SpreadsheetConverterBasic createConverter() {
        return SpreadsheetConverterBasic.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "basic"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetConverterBasic> type() {
        return SpreadsheetConverterBasic.class;
    }
}
