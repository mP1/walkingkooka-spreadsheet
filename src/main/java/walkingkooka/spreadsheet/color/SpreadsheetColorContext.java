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

import walkingkooka.color.Color;
import walkingkooka.color.ColorContext;
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;

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
    default NamedColor namedColor(final String name) {
        SpreadsheetColors.checkNamedColor(name);

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
