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

import java.math.BigDecimal;

public final class SpreadsheetNumberParsePatternsComponentWhitespaceTest extends SpreadsheetNumberParsePatternsComponentTestCase2<SpreadsheetNumberParsePatternsComponentWhitespace> {

    @Test
    public void testSpace() {
        this.parseAndCheck(" A",
                "A", BigDecimal.ZERO,
                true);
    }

    @Test
    public void testTab() {
        this.parseAndCheck("\tA",
                "A",
                BigDecimal.ZERO,
                true);
    }

    @Test
    public void testNonSpace() {
        this.parseFails("AB");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createComponent(), " ");
    }

    @Override
    SpreadsheetNumberParsePatternsComponentWhitespace createComponent() {
        return SpreadsheetNumberParsePatternsComponentWhitespace.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsComponentWhitespace> type() {
        return SpreadsheetNumberParsePatternsComponentWhitespace.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "Whitespace";
    }
}
