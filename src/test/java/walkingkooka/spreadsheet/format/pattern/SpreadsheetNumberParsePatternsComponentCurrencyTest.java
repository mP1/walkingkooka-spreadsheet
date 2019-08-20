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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetNumberParsePatternsComponentCurrencyTest extends SpreadsheetNumberParsePatternsComponentTestCase2<SpreadsheetNumberParsePatternsComponentCurrency> {

    @Test
    public void testIncomplete() {
        this.parseFails(CURRENCY.substring(0, CURRENCY.length() - 1), "");
    }

    @Test
    public void testDifferentCase() {
        assertNotEquals(CURRENCY, CURRENCY.toUpperCase(), "currency upper case should be different");
        this.parseFails(CURRENCY.toUpperCase());
    }

    @Test
    public void testToken() {
        this.parseAndCheck(CURRENCY,
                "",
                null,
                true);
    }

    @Test
    public void testToken2() {
        final String after = "123";

        this.parseAndCheck(CURRENCY + after,
                after,
                null,
                true);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), "$");
    }

    @Override
    SpreadsheetNumberParsePatternsComponentCurrency createComponent() {
        return SpreadsheetNumberParsePatternsComponentCurrency.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsComponentCurrency> type() {
        return SpreadsheetNumberParsePatternsComponentCurrency.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "Currency";
    }
}
