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
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;

import java.util.List;

/**
 * Extracted here because extracting two STORAGE constants into {@link SpreadsheetConverters} and {@link SpreadsheetConvertersGwt},
 * would result in a NULL in the BINARY field.
 */
final class SpreadsheetConvertersStorage extends SpreadsheetConvertersStorageGwt {

    static {
        final List<Converter<SpreadsheetConverterContext>> converters = Lists.array();

        converters.add(
            SpreadsheetConverters.textToStoragePath()
        );

        textToPath(converters);

        converters.addAll(
            Lists.of(
                SpreadsheetConverters.storageBinaryToStorageValueCsv(),
                SpreadsheetConverters.storageBinaryToStorageValueEnvironment(),
                SpreadsheetConverters.storageBinaryToStorageValueExpression(),
                SpreadsheetConverters.storageBinaryToStorageValueJson(),
                SpreadsheetConverters.storageBinaryToStorageValueProperties(),
                SpreadsheetConverters.storageBinaryToStorageValueTsv(),
                SpreadsheetConverters.storageBinaryToStorageValueTxt(),
                SpreadsheetConverters.storageBinaryToStorageValueBinary(),
                SpreadsheetConverters.storageValueToStorageBinaryCsv(),
                SpreadsheetConverters.storageValueToStorageBinaryEnvironment(),
                SpreadsheetConverters.storageValueToStorageBinaryExpression(),
                SpreadsheetConverters.storageValueToStorageBinaryJson(),
                SpreadsheetConverters.storageValueToStorageBinaryProperties(),
                SpreadsheetConverters.storageValueToStorageBinaryTsv(),
                SpreadsheetConverters.storageValueToStorageBinaryTxt(),
                SpreadsheetConverters.storageValueToStorageBinaryBinary(),
                SpreadsheetConverters.binary()
            )
        );

        STORAGE = SpreadsheetConverters.namedCollection(
            "STORAGE",
            converters
        );
    }

    @GwtIncompatible
    static void textToPath(final List<Converter<SpreadsheetConverterContext>> converters) {
        converters.add(
            SpreadsheetConverters.textToPath()
        );
    }

    final static Converter<SpreadsheetConverterContext> STORAGE;

    private SpreadsheetConvertersStorage() {
        super();
    }
}
