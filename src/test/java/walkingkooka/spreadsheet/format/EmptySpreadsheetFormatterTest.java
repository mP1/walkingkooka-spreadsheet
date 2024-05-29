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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;

public final class EmptySpreadsheetFormatterTest extends SpreadsheetFormatterTestCase<EmptySpreadsheetFormatter> {

    @Override
    public void testCanFormatFalse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testThenFormat() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testFormat() {
        this.formatAndCheck(
                "Hello2"
        );
    }

    @Override
    public EmptySpreadsheetFormatter createFormatter() {
        return EmptySpreadsheetFormatter.INSTANCE;
    }

    @Override
    public Object value() {
        return "Hello";
    }

    @Override
    public SpreadsheetFormatterContext createContext() {
        return SpreadsheetFormatterContexts.fake();
    }

    @Override
    public Class<EmptySpreadsheetFormatter> type() {
        return EmptySpreadsheetFormatter.class;
    }
}
