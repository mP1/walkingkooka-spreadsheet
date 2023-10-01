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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.List;

public abstract class SpreadsheetViewportSelectionNavigationTestCase2<T extends SpreadsheetViewportSelectionNavigation> implements ClassTesting<T>,
        ParseStringTesting<List<T>> {

    SpreadsheetViewportSelectionNavigationTestCase2() {
        super();
    }

    @Override
    public final void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public final void testParseToStringRoundtrip() {
        final T navigation = this.createSpreadsheetViewportSelectionNavigation();

        this.parseStringAndCheck(
                navigation.text(),
                Lists.of(
                        navigation
                )
        );
    }

    abstract T createSpreadsheetViewportSelectionNavigation();

    // ParseString......................................................................................................

    @Override
    public final List<T> parseString(final String string) {
        return Cast.to(
                SpreadsheetViewportSelectionNavigation.parse(string)
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
