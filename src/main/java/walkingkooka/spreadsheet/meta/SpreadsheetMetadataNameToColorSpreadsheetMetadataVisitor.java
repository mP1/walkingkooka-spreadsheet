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

import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;

import java.util.Map;

/**
 * A {@link SpreadsheetMetadataVisitor} that fills a {@link Map} of all present named {@link Color colors}.
 */
final class SpreadsheetMetadataNameToColorSpreadsheetMetadataVisitor extends SpreadsheetMetadataVisitor {

    static Map<SpreadsheetColorName, Color> nameToColorMap(final SpreadsheetMetadata metadata) {
        final SpreadsheetMetadataNameToColorSpreadsheetMetadataVisitor visitor = new SpreadsheetMetadataNameToColorSpreadsheetMetadataVisitor();
        visitor.accept(metadata);
        return visitor.colors;
    }

    SpreadsheetMetadataNameToColorSpreadsheetMetadataVisitor() {
        super();
    }

    @Override
    protected void visitNamedColor(final SpreadsheetColorName name, final Color color) {
        this.colors.put(name, color);
    }

    private final Map<SpreadsheetColorName, Color> colors = Maps.ordered();

    @Override
    public String toString() {
        return this.colors.toString();
    }
}
