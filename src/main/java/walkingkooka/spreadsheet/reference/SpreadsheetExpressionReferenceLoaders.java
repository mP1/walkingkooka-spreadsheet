
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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

/**
 * Collection of factory methods returning {@link SpreadsheetExpressionReferenceLoader}.
 */
public final class SpreadsheetExpressionReferenceLoaders implements PublicStaticHelper {

    /**
     * {@see EmptySpreadsheetExpressionReferenceLoader}
     */
    public static SpreadsheetExpressionReferenceLoader empty() {
        return EmptySpreadsheetExpressionReferenceLoader.INSTANCE;
    }

    /**
     * {@see FakeSpreadsheetExpressionReferenceLoader}
     */
    public static SpreadsheetExpressionReferenceLoader fake() {
        return new FakeSpreadsheetExpressionReferenceLoader();
    }

    /**
     * {@see SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader}
     */
    public static SpreadsheetExpressionReferenceLoader spreadsheetStoreRepository(final SpreadsheetStoreRepository repository) {
        return SpreadsheetStoreRepositorySpreadsheetExpressionReferenceLoader.with(repository);
    }

    /**
     * Stop creation
     */
    private SpreadsheetExpressionReferenceLoaders() {
        throw new UnsupportedOperationException();
    }
}
