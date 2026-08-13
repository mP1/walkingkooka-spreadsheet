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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.convert.Converter;

/**
 * Extracted here because extracting two STORAGE constants into {@link SpreadsheetConverters} and {@link SpreadsheetConvertersGwt},
 * would result in a NULL in the BINARY field.
 */
final class SpreadsheetConvertersStorage extends SpreadsheetConvertersStorageGwt {

    @GwtIncompatible
    final static Converter<SpreadsheetConverterContext> STORAGE = SpreadsheetConverters.namedCollection(
        "STORAGE",
        SpreadsheetConverters.textToStoragePath(),
        SpreadsheetConverters.textToPath(), // if GWT is true will be filtered.
        SpreadsheetConverters.storageBinaryToStorageValueCsv(),
        SpreadsheetConverters.storageBinaryToStorageValueExpression(),
        SpreadsheetConverters.storageBinaryToStorageValueJson(),
        SpreadsheetConverters.storageBinaryToStorageValueProperties(),
        SpreadsheetConverters.storageBinaryToStorageValueTsv(),
        SpreadsheetConverters.storageBinaryToStorageValueTxt(),
        SpreadsheetConverters.storageBinaryToStorageValueBinary(),
        SpreadsheetConverters.storageValueToStorageBinaryCsv(),
        SpreadsheetConverters.storageValueToStorageBinaryExpression(),
        SpreadsheetConverters.storageValueToStorageBinaryJson(),
        SpreadsheetConverters.storageValueToStorageBinaryProperties(),
        SpreadsheetConverters.storageValueToStorageBinaryTsv(),
        SpreadsheetConverters.storageValueToStorageBinaryTxt(),
        SpreadsheetConverters.storageValueToStorageBinaryBinary(),
        SpreadsheetConverters.binary()
    );

    private SpreadsheetConvertersStorage() {
        super();
    }
}
