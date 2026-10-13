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

package walkingkooka.spreadsheet.color;

import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.color.ColorContext;
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link ColorContext} that implements {@link Color} factory methods adding guards for color index values and named
 * colors. The {@link #lookupColor(Color)} which resolves {@link IndexedColor} and {@link NamedColor} remains abstract.
 */
public interface SpreadsheetColorContext extends ColorContext {

    @Override
    default IndexedColor indexedColor(final int index) {
        SpreadsheetColors.checkColorIndex(index);

        return Color.indexed(index);
    }

    @Override
    default Optional<Color> lookupColor(final Color color) {
        Objects.requireNonNull(color, "color");

        Color lookup = color;

        final Set<Color> cycle = Sets.ordered();

        while (null != lookup) {
            if (false == cycle.add(lookup)) {
                throw new IllegalStateException("Cycle detected looking up color " + color);
            }

            if (lookup.isIndexed()) {
                lookup = this.lookupIndexedColor(
                    (IndexedColor) lookup
                ).orElse(null);
                continue; // try again might be an indirect color
            }

            if (lookup.isNamed()) {
                lookup = this.lookupNamedColor(
                    (NamedColor) lookup
                ).orElse(null);
                continue;
            }
            break;
        }

        return Optional.ofNullable(lookup);
    }

    Optional<Color> lookupIndexedColor(final IndexedColor color);

    Optional<Color> lookupNamedColor(final NamedColor color);

    @Override
    default NamedColor namedColor(final String name) {
        SpreadsheetColors.checkColorName(name);

        return Color.named(name);
    }

    @Override
    default Color parseColor(final String color) {
        final Color colorColor = Color.parse(color);
        return colorColor.isIndexed() ?
            this.indexedColor(
                ((IndexedColor) colorColor).index()
            ) :
            colorColor.isNamed() ?
                this.namedColor(
                    ((NamedColor) colorColor).value()
                ) :
                colorColor;
    }
}
