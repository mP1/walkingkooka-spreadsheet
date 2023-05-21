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

package walkingkooka.spreadsheet.format.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

public final class SpreadsheetFormatParserTokenKindTest implements ClassTesting<SpreadsheetFormatParserTokenKind> {

    // labelText........................................................................................................

    @Test
    public void testLabelTextForCOLOR_NAME() {
        this.labelTextAndCheck(
                SpreadsheetFormatParserTokenKind.COLOR_NAME,
                "Color name"
        );
    }

    @Test
    public void testLabelTextForCONDITION() {
        this.labelTextAndCheck(
                SpreadsheetFormatParserTokenKind.CONDITION,
                "Condition"
        );
    }

    @Test
    public void testLabelTextForDAY_WITH_LEADING_ZERO() {
        this.labelTextAndCheck(
                SpreadsheetFormatParserTokenKind.DAY_WITH_LEADING_ZERO,
                "Day with leading zero"
        );
    }

    @Test
    public void testLabelTextForAMPM_FULL_LOWER() {
        this.labelTextAndCheck(
                SpreadsheetFormatParserTokenKind.AMPM_FULL_LOWER,
                "AMPM full lower"
        );
    }

    private void labelTextAndCheck(final SpreadsheetFormatParserTokenKind kind,
                                   final String expected) {
        this.checkEquals(
                expected,
                kind.labelText(),
                () -> kind + " labelText()"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatParserTokenKind> type() {
        return SpreadsheetFormatParserTokenKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
