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

package walkingkooka.spreadsheet.engine;

import walkingkooka.text.CharSequences;

/**
 * Captures a users input movement relative to a selection, such as a cursor-left from a selection in the viewport.
 */
public enum SpreadsheetViewportNavigation {
    LEFT,
    UP,
    RIGHT,
    DOWN,
    EXTEND_LEFT,
    EXTEND_UP,
    EXTEND_RIGHT,
    EXTEND_DOWN;

    public static SpreadsheetViewportNavigation from(final String text) {
        CharSequences.failIfNullOrEmpty(text, "navigation");

        for (final SpreadsheetViewportNavigation navigation : values()) {
            if (navigation.name().toLowerCase().replace('_', '-').equals(text)) {
                return navigation;
            }
        }

        throw new IllegalArgumentException("Invalid text=" + CharSequences.quoteAndEscape(text));
    }
}
