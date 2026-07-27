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

package walkingkooka.spreadsheet.environment;

import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContext;

import java.util.function.Predicate;

/**
 * A collection of factory methods for {@link SpreadsheetEnvironmentContext}.
 */
public final class SpreadsheetEnvironmentContexts implements PublicStaticHelper {

    /**
     * {@see SpreadsheetEnvironmentContextBasic}
     */
    public static SpreadsheetEnvironmentContext basic(final Storage<SpreadsheetStorageContext> storage,
                                                      final StorageEnvironmentContext context) {
        return SpreadsheetEnvironmentContextBasic.with(
            storage,
            context
        );
    }

    /**
     * {@see FakeSpreadsheetEnvironmentContext}
     */
    public static FakeSpreadsheetEnvironmentContext fake() {
        return new FakeSpreadsheetEnvironmentContext();
    }

    /**
     * {@see SpreadsheetEnvironmentContextReadOnly}
     */
    public static SpreadsheetEnvironmentContext readOnly(final Predicate<EnvironmentValueName<?>> readOnlyFilter,
                                                         final SpreadsheetEnvironmentContext context) {
        return SpreadsheetEnvironmentContextReadOnly.with(
            readOnlyFilter,
            context
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetEnvironmentContexts() {
        throw new UnsupportedOperationException();
    }
}
