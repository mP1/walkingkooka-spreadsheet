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

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.color.ColorContextTesting2;
import walkingkooka.reflect.ThrowableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetColorContextTesting2<C extends SpreadsheetColorContext> extends ColorContextTesting2<C>,
    SpreadsheetColorContextTesting,
    ThrowableTesting {

    // indexedColor.....................................................................................................

    @Test
    default void testIndexedColorWithMinLessOneFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .indexedColor(
                    SpreadsheetColors.MIN - 1
                )
        );
    }

    @Test
    default void testIndexedColorWithMaxPlus1Fails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .indexedColor(
                    SpreadsheetColors.MAX + 1
                )
        );
    }

    // lookupIndexedColor...............................................................................................

    @Test
    default void testLookupIndexedColorWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .lookupIndexedColor(null)
        );
    }

    @Test
    default void testLookupIndexedColorWithMinLessOneFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .lookupIndexedColor(
                    Color.indexed(
                        SpreadsheetColors.MIN - 1
                    )
                )
        );
    }

    @Test
    default void testLookupIndexedColorWithMaxPlus1Fails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .lookupIndexedColor(
                    Color.indexed(
                        SpreadsheetColors.MAX + 1
                    )
                )
        );
    }

    @Test
    default void testLookupIndexedColorWithMin() {
        this.createContext()
            .lookupIndexedColor(
                Color.indexed(SpreadsheetColors.MIN)
            );
    }

    @Test
    default void testLookupIndexedColorWithMax() {
        this.createContext()
            .lookupIndexedColor(
                Color.indexed(SpreadsheetColors.MAX)
            );
    }

    // lookupNamedColor.................................................................................................

    @Test
    default void testLookupNamedColorWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .lookupNamedColor(null)
        );
    }

    @Test
    default void testLookupNamedColorWithInvalidCharactersFails() {
        this.getMessageAndCheck(
            assertThrows(
                IllegalArgumentException.class,
                () -> this.createContext()
                    .lookupNamedColor(
                        Color.named("Hello!World")
                    )
            ),
            "Invalid character '!' at 5"
        );
    }

    // namedColor.......................................................................................................

    @Test
    default void testNamedColorWithInvalidCharactersFails() {
        this.getMessageAndCheck(
            assertThrows(
                IllegalArgumentException.class,
                () -> this.createContext()
                    .namedColor("Hello!World")
            ),
            "Invalid character '!' at 5"
        );
    }

    // parseColor.......................................................................................................

    @Test
    default void testParseColorWithIndexedColorWithMinLessOneFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseColor(
                    String.valueOf(
                        SpreadsheetColors.MIN - 1
                    )
                )
        );
    }

    @Test
    default void testParseColorWithIndexedColorWithMaxMoreOneFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .parseColor(
                    String.valueOf(
                        SpreadsheetColors.MAX + 1
                    )
                )
        );
    }
}
