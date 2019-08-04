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

import walkingkooka.color.Color;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class SpreadsheetMetadataNumberToColorFunctionTestCase<F extends SpreadsheetMetadataNumberToColorFunction> extends SpreadsheetMetadataTestCase2<F>
        implements ToStringTesting<F> {

    SpreadsheetMetadataNumberToColorFunctionTestCase() {
        super();
    }

    final void numberToColorAndCheck(final F function,
                                     final int number,
                                     final Color color) {
        assertEquals(Optional.ofNullable(color),
                function.apply(number),
                () -> number + " " + function);
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public final String typeNameSuffix() {
        return "NumberToColorFunction";
    }
}
