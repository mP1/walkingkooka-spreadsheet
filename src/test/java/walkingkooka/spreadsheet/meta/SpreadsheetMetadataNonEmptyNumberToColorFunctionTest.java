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
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataNonEmptyNumberToColorFunctionTest extends SpreadsheetMetadataNumberToColorFunctionTestCase<SpreadsheetMetadataNonEmptyNumberToColorFunction> {

    @Test
    public void testInvalidNumberFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetMetadataNonEmptyNumberToColorFunction.with(Maps.empty()).apply(-1);
        });
    }

    @Test
    public void testPresent() {
        final int number = 1;
        final Color color = Color.fromRgb(0x111);

        this.numberToColorAndCheck(SpreadsheetMetadataNonEmptyNumberToColorFunction.with(Maps.of(number, color)),
                number,
                color);
    }

    @Test
    public void testAbsent() {
        this.numberToColorAndCheck(SpreadsheetMetadataNonEmptyNumberToColorFunction.with(Maps.of(1, Color.fromRgb(0x111))),
                2,
                null);
    }

    @Test
    public void testToString() {
        final Map<Integer, Color> map = Maps.of(1, Color.fromRgb(0x111), 2, Color.fromRgb(0x222));

        this.toStringAndCheck(SpreadsheetMetadataNonEmptyNumberToColorFunction.with(map), map.toString());
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<SpreadsheetMetadataNonEmptyNumberToColorFunction> type() {
        return SpreadsheetMetadataNonEmptyNumberToColorFunction.class;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetMetadataNonEmpty.class.getSimpleName();
    }
}
