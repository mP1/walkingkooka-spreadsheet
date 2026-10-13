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
import walkingkooka.color.ColorContextTesting;
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;

import java.util.Optional;

public interface SpreadsheetColorContextTesting extends ColorContextTesting {

    // lookupIndexedColor...............................................................................................

    default void lookupIndexedColorAndCheck(final SpreadsheetColorContext context,
                                            final IndexedColor color) {
        this.lookupIndexedColorAndCheck(
            context,
            color,
            Optional.empty()
        );
    }

    default void lookupIndexedColorAndCheck(final SpreadsheetColorContext context,
                                            final IndexedColor color,
                                            final Color expected) {
        this.lookupIndexedColorAndCheck(
            context,
            color,
            Optional.of(expected)
        );
    }

    default void lookupIndexedColorAndCheck(final SpreadsheetColorContext context,
                                            final IndexedColor color,
                                            final Optional<Color> expected) {
        this.checkEquals(
            expected,
            context.lookupIndexedColor(color),
            color::toString
        );
    }

    // lookupNamedColor...............................................................................................

    default void lookupNamedColorAndCheck(final SpreadsheetColorContext context,
                                          final NamedColor color) {
        this.lookupNamedColorAndCheck(
            context,
            color,
            Optional.empty()
        );
    }

    default void lookupNamedColorAndCheck(final SpreadsheetColorContext context,
                                          final NamedColor color,
                                          final Color expected) {
        this.lookupNamedColorAndCheck(
            context,
            color,
            Optional.of(expected)
        );
    }

    default void lookupNamedColorAndCheck(final SpreadsheetColorContext context,
                                          final NamedColor color,
                                          final Optional<Color> expected) {
        this.checkEquals(
            expected,
            context.lookupNamedColor(color),
            color::toString
        );
    }
}
