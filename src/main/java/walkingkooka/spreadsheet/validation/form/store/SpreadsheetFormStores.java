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

package walkingkooka.spreadsheet.validation.form.store;

import walkingkooka.reflect.PublicStaticHelper;

/**
 * A collection of factory methods for creating {@link SpreadsheetFormStore} form stores.
 */
public final class SpreadsheetFormStores implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetFormStore}
     */
    public static SpreadsheetFormStore fake() {
        return new FakeSpreadsheetFormStore();
    }

    /**
     * {@see TreeSpreadsheetFormStore}
     */
    public static SpreadsheetFormStore treeMap() {
        return TreeSpreadsheetFormStore.empty();
    }

    /**
     * Stop creation
     */
    private SpreadsheetFormStores() {
        throw new UnsupportedOperationException();
    }
}
