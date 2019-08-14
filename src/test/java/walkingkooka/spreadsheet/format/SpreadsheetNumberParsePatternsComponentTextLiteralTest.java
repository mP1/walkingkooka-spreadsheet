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

import java.math.BigDecimal;

public final class SpreadsheetNumberParsePatternsComponentTextLiteralTest extends SpreadsheetNumberParsePatternsComponentTestCase2<SpreadsheetNumberParsePatternsComponentTextLiteral> {

    private final static String TOKEN = "ghi";

    @Test
    public void testDifferentCase() {
        this.parseFails(TOKEN.toUpperCase());
    }

    @Test
    public void testIncomplete() {
        this.parseAndCheck("gh1",
                "1",
                BigDecimal.ZERO,
                false);
    }

    @Test
    public void testToken() {
        this.parseAndCheck(TOKEN,
                "",
                BigDecimal.ZERO,
                true);
    }

    @Test
    public void testToken2() {
        final String after = "123";

        this.parseAndCheck(TOKEN + after,
                after,
                NEXT_CALLED);
    }

    @Test
    public void testToken3() {
        final String after = "123";
        final String token = "A";

        this.parseAndCheck(SpreadsheetNumberParsePatternsComponentTextLiteral.with(token),
                token + after,
                this.createContext(),
                after,
                VALUE_WITHOUT,
                NEXT_CALLED);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), TOKEN);
    }

    @Override
    SpreadsheetNumberParsePatternsComponentTextLiteral createComponent() {
        return SpreadsheetNumberParsePatternsComponentTextLiteral.with(TOKEN);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsComponentTextLiteral> type() {
        return SpreadsheetNumberParsePatternsComponentTextLiteral.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "TextLiteral";
    }
}
