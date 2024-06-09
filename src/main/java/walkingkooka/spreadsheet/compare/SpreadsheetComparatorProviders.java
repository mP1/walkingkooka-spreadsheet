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

import walkingkooka.reflect.PublicStaticHelper;

import java.util.Set;

/**
 * A collection of helpers and implementations of {@link SpreadsheetComparatorProvider}.
 */
public final class SpreadsheetComparatorProviders implements PublicStaticHelper {

    /**
     * {@see BuiltInSpreadsheetComparatorProvider}
     */
    public static SpreadsheetComparatorProvider builtIn() {
        return BuiltInSpreadsheetComparatorProvider.INSTANCE;
    }

    /**
     * {@see SpreadsheetComparatorProviderCollection}
     */
    public static SpreadsheetComparatorProvider collection(final Set<SpreadsheetComparatorProvider> providers) {
        return SpreadsheetComparatorProviderCollection.with(providers);
    }

    /**
     * {@see FakeSpreadsheetComparatorProvider}
     */
    public static SpreadsheetComparatorProvider fake() {
        return new FakeSpreadsheetComparatorProvider();
    }

    /**
     * Stop creation
     */
    private SpreadsheetComparatorProviders() {
        throw new UnsupportedOperationException();
    }
}
