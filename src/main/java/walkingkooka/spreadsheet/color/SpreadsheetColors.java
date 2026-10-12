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
import walkingkooka.color.NamedColor;
import walkingkooka.reflect.PublicStaticHelper;

import java.util.Set;

public final class SpreadsheetColors implements PublicStaticHelper {

    public final static NamedColor BLACK = Color.named("Black");

    public final static NamedColor WHITE = Color.named("White");

    public final static NamedColor RED = Color.named("Red");

    public final static NamedColor GREEN = Color.named("Green");

    public final static NamedColor BLUE = Color.named("Blue");

    public final static NamedColor YELLOW = Color.named("Yellow");

    public final static NamedColor MAGENTA = Color.named("Magenta");

    public final static NamedColor CYAN = Color.named("Cyan");

    public final static Set<NamedColor> DEFAULTS = Sets.of(
        BLACK,
        WHITE,
        RED,
        GREEN,
        BLUE,
        YELLOW,
        MAGENTA,
        CYAN
    );
    
    public static final int MIN = 1;
    public static final int MAX = 56;

    /**
     * Validates the given color number is valid.
     */
    public static int checkNumber(final int number) {
        if (number < MIN || number > MAX) {
            throw new IllegalArgumentException("Invalid color number " + number + " < " + MIN + " or > " + MAX);
        }

        return number;
    }

    /**
     * Stop creation
     */
    private SpreadsheetColors() {
        throw new UnsupportedOperationException();
    }
}
