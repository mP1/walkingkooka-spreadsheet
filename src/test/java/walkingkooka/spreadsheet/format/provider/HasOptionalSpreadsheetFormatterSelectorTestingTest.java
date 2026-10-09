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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.PublicClassTesting;

public final class HasOptionalSpreadsheetFormatterSelectorTestingTest implements HasOptionalSpreadsheetFormatterSelectorTesting,
    PublicClassTesting<HasOptionalSpreadsheetFormatterSelectorTesting> {

    @Test
    public void testConstants() {
        this.checkNotEquals(
            FORMATTER_SELECTOR,
            DIFFERENT_FORMATTER_SELECTOR
        );
    }

    @Test
    public void testOptionalConstants() {
        this.checkNotEquals(
            OPTIONAL_FORMATTER_SELECTOR,
            OPTIONAL_DIFFERENT_FORMATTER_SELECTOR
        );
    }

    // class............................................................................................................

    @Override
    public Class<HasOptionalSpreadsheetFormatterSelectorTesting> type() {
        return HasOptionalSpreadsheetFormatterSelectorTesting.class;
    }
}