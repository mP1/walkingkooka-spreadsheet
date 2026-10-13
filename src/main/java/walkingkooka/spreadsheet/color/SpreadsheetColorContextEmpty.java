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
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ColorContext} that validates {@link IndexedColor} and {@link NamedColor}, with a {@link #lookupColor(Color)}
 * that always returns {@link #NO_LOOKUP_COLOR}.
 */
final class SpreadsheetColorContextEmpty implements SpreadsheetColorContext,
    TreePrintable {

    /**
     * Singleton
     */
    final static SpreadsheetColorContextEmpty INSTANCE = new SpreadsheetColorContextEmpty();

    private SpreadsheetColorContextEmpty() {
        super();
    }

    @Override
    public Optional<Color> lookupColor(final Color color) {
        Objects.requireNonNull(color, "color");

        if (color instanceof IndexedColor) {
            SpreadsheetColors.checkColorIndex(
                ((IndexedColor) color).index()
            );
        } else {
            if (color instanceof NamedColor) {
                SpreadsheetColors.checkNamedColor(
                    ((NamedColor) color).value()
                );
            }
        }

        return NO_LOOKUP_COLOR;
    }

    // Object...........................................................................................................

    /**
     * Because this has no lookup color mappings this returns an empty string.
     */
    @Override
    public String toString() {
        return "";
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
    }
}
