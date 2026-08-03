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

package walkingkooka.spreadsheet.compare;

import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

public abstract class SpreadsheetComparatorTestCase<C extends SpreadsheetComparator<T>, T> implements SpreadsheetComparatorTesting2<C, T>,
    ClassTesting<C>,
    ToStringTesting<C>  {

    SpreadsheetComparatorTestCase() {
        super();
    }

    // class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public final String typeNamePrefix() {
        final String classSimpleName = this.getClass()
            .getSuperclass()
            .getSimpleName();

        return CharSequences.subSequence(
            classSimpleName,
            0,
            - "TestCase".length()
        ).toString();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
