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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataEmptyNumberToColorFunctionTest extends SpreadsheetMetadataNumberToColorFunctionTestCase<SpreadsheetMetadataEmptyNumberToColorFunction> {

    @Test
    public void testInvalidNumberFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetMetadataEmptyNumberToColorFunction.INSTANCE.apply(-1);
        });
    }

    @Test
    public void testAbsent() {
        this.numberToColorAndCheck(SpreadsheetMetadataEmptyNumberToColorFunction.INSTANCE,
                2,
                null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataEmptyNumberToColorFunction.INSTANCE, "{}");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataEmptyNumberToColorFunction> type() {
        return SpreadsheetMetadataEmptyNumberToColorFunction.class;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetMetadataEmpty.class.getSimpleName();
    }
}
