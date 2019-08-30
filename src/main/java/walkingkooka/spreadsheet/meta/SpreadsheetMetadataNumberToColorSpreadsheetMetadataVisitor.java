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

import java.util.Map;

/**
 * A {@link SpreadsheetMetadataVisitor} that fills a {@link Map} of all present numbered {@link Color colors}.
 */
final class SpreadsheetMetadataNumberToColorSpreadsheetMetadataVisitor extends SpreadsheetMetadataVisitor {

    static Map<Integer, Color> numberToColorMap(final SpreadsheetMetadata metadata) {
        final SpreadsheetMetadataNumberToColorSpreadsheetMetadataVisitor visitor = new SpreadsheetMetadataNumberToColorSpreadsheetMetadataVisitor();
        visitor.accept(metadata);
        return visitor.colors;
    }

    SpreadsheetMetadataNumberToColorSpreadsheetMetadataVisitor() {
        super();
    }

    @Override
    protected void visitNumberedColor(final int number,
                                      final Color color) {
        this.colors.put(number, color);
    }

    private final Map<Integer, Color> colors = Maps.ordered();

    @Override
    public String toString() {
        return this.colors.toString();
    }
}
