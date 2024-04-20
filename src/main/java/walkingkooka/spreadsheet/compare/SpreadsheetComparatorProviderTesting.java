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

import walkingkooka.reflect.ClassTesting2;

import java.util.Set;

public interface SpreadsheetComparatorProviderTesting<T extends SpreadsheetComparatorProvider> extends ClassTesting2<T> {

    default void spreadsheetComparatorAndCheck(final SpreadsheetComparatorProvider provider,
                                               final SpreadsheetComparatorName name,
                                               final SpreadsheetComparator<?> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetComparator(name),
                () -> name.toString()
        );
    }

    default void spreadsheetComparatorNamesAndCheck(final SpreadsheetComparatorProvider provider,
                                                    final Set<SpreadsheetComparatorName> expected) {
        this.checkEquals(
                expected,
                provider.spreadsheetComparatorNames(),
                () -> provider.toString()
        );
    }
}
