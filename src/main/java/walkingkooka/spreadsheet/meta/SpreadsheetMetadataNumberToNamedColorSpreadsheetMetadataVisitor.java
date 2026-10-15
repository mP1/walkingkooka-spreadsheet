
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
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;

import java.util.Map;

/**
 * A {@link SpreadsheetMetadataVisitor} that fills a {@link Map} of with the key being the color number and the value the matching
 * {@link NamedColor}.
 */
final class SpreadsheetMetadataNumberToNamedColorSpreadsheetMetadataVisitor extends SpreadsheetMetadataVisitor {

    static Map<IndexedColor, NamedColor> numberToNamedColor(final SpreadsheetMetadata metadata) {
        final SpreadsheetMetadataNumberToNamedColorSpreadsheetMetadataVisitor visitor = new SpreadsheetMetadataNumberToNamedColorSpreadsheetMetadataVisitor();
        visitor.accept(metadata.defaults()); // defaults get overridden
        visitor.accept(metadata);
        return visitor.numberToNamedColor;
    }

    SpreadsheetMetadataNumberToNamedColorSpreadsheetMetadataVisitor() {
        super();
    }

    @Override
    protected void visitNamedColor(final NamedColor namedColor,
                                   final int colorNumber) {
        this.numberToNamedColor.put(
            Color.indexed(colorNumber),
            namedColor
        );
    }

    private final Map<IndexedColor, NamedColor> numberToNamedColor = Maps.ordered();

    @Override
    public String toString() {
        return this.numberToNamedColor.toString();
    }
}
