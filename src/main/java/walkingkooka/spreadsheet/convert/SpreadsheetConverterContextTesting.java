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

package walkingkooka.spreadsheet.convert;

import walkingkooka.spreadsheet.provider.SpreadsheetProviderContextTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting;
import walkingkooka.storage.convert.StorageConverterContextTesting;
import walkingkooka.tree.json.convert.JsonNodeConverterContextTesting;

public interface SpreadsheetConverterContextTesting<C extends SpreadsheetConverterContext> extends JsonNodeConverterContextTesting<C>,
    SpreadsheetLabelNameResolverTesting<C>,
    StorageConverterContextTesting<C>,
    SpreadsheetProviderContextTesting<C> {

    @Override
    default C createSpreadsheetLabelNameResolver() {
        return this.createContext();
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetConverterContext.class.getSimpleName();
    }
}
