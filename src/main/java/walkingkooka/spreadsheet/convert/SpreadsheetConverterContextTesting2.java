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

import walkingkooka.environment.convert.EnvironmentConverterContextTesting2;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoaderTesting2;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderContextTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting2;
import walkingkooka.storage.convert.StorageConverterContextTesting2;
import walkingkooka.tree.json.convert.JsonNodeConverterContextTesting2;

public interface SpreadsheetConverterContextTesting2<C extends SpreadsheetConverterContext> extends SpreadsheetConverterContextTesting,
    EnvironmentConverterContextTesting2<C>,
    JsonNodeConverterContextTesting2<C>,
    SpreadsheetLabelNameResolverTesting2<C>,
    SpreadsheetMetadataLoaderTesting2<C>,
    StorageConverterContextTesting2<C>,
    SpreadsheetProviderContextTesting2<C> {

    @Override
    default C createSpreadsheetLabelNameResolver() {
        return this.createContext();
    }

    @Override
    default C createSpreadsheetMetadataLoader() {
        return this.createContext();
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetConverterContext.class.getSimpleName();
    }
}
