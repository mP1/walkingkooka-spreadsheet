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

package walkingkooka.spreadsheet.meta;

import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;

import java.util.List;

public interface SpreadsheetMetadataContextTesting extends SpreadsheetMetadataCreatorTesting,
    SpreadsheetMetadataLoaderTesting {

    // saveMetadata.....................................................................................................

    default void saveMetadataAndCheck(final SpreadsheetMetadataContext context,
                                      final SpreadsheetMetadata metadata,
                                      final SpreadsheetMetadata expected) {
        this.checkEquals(
            expected,
            context.saveMetadata(metadata),
            () -> "saveMetadata " + metadata
        );
    }

    // findMetadataBySpreadsheetName....................................................................................

    default void findMetadataBySpreadsheetNameAndCheck(final SpreadsheetMetadataContext context,
                                                       final String name,
                                                       final int offset,
                                                       final int count,
                                                       final SpreadsheetMetadata... expected) {
        this.findMetadataBySpreadsheetNameAndCheck(
            context,
            name,
            offset,
            count,
            Lists.of(expected)
        );
    }

    default void findMetadataBySpreadsheetNameAndCheck(final SpreadsheetMetadataContext context,
                                                       final String name,
                                                       final int offset,
                                                       final int count,
                                                       final List<SpreadsheetMetadata> expected) {
        this.checkEquals(
            expected,
            context.findMetadataBySpreadsheetName(
                name,
                offset,
                count
            ),
            () -> "findMetadataBySpreadsheetName " + CharSequences.quoteAndEscape(name) + " offset=" + offset + " count=" + count
        );
    }
}
