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

package walkingkooka.spreadsheet.format;

import walkingkooka.ToStringTesting;
import walkingkooka.color.Color;
import walkingkooka.color.WebColorName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.Optional;

public abstract class SpreadsheetFormatterSharedTestCase<F extends SpreadsheetFormatterShared> implements SpreadsheetFormatterTesting2<F>,
    ToStringTesting<F>,
    ClassTesting2<F> {

    final static Color COLOR_INDEX_44 = Color.indexed(44);

    final static Optional<Color> OPTIONAL_COLOR_INDEX_44 = Optional.of(COLOR_INDEX_44);

    final static Color COLOR_44 = Color.parse("#444444");

    final static Optional<Color> OPTIONAL_COLOR_44 = Optional.of(COLOR_44);

    final static Color COLOR_NAME_RED = Color.named("RED");

    final static Optional<Color> OPTIONAL_COLOR_NAME_RED = Optional.of(COLOR_NAME_RED);

    final static Color COLOR_RED = WebColorName.RED.color();

    final static Optional<Color> OPTIONAL_COLOR_RED = Optional.of(COLOR_RED);

    SpreadsheetFormatterSharedTestCase() {
        super();
    }

    // class............................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetFormatterShared.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
