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
import walkingkooka.Cast;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.util.FunctionTesting;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetMetadataColorFunctionTest extends SpreadsheetMetadataTestCase2<SpreadsheetMetadataColorFunction<Integer, Color>>
    implements FunctionTesting<SpreadsheetMetadataColorFunction<Integer, Color>, Integer, Optional<Color>>,
    ToStringTesting<SpreadsheetMetadataColorFunction<Integer, Color>> {

    @Test
    public void testKeyPresent() {
        this.applyAndCheck(12, Optional.of(this.color()));
    }

    @Test
    public void testKeyAbsent() {
        this.applyAndCheck(99, Optional.empty());
    }

    @Test
    public void testToString() {
        Map<Integer, Color> map = this.colorMap();
        this.toStringAndCheck(SpreadsheetMetadataColorFunction.with(map), map.toString());
    }

    @Override
    public SpreadsheetMetadataColorFunction<Integer, Color> createFunction() {
        return SpreadsheetMetadataColorFunction.with(this.colorMap());
    }

    private Map<Integer, Color> colorMap() {
        return Maps.of(12, this.color(),
            13, Color.fromRgb(0xffeedd));
    }

    private Color color() {
        return Color.fromRgb(0x112233);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataColorFunction<Integer, Color>> type() {
        return Cast.to(SpreadsheetMetadataColorFunction.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetMetadata.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return Function.class.getSimpleName();
    }
}
