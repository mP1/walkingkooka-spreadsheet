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

import org.junit.jupiter.api.Test;

public final class SpreadsheetMetadataPropertyNameModifiedByTest extends SpreadsheetMetadataPropertyNameEmailAddressTestCase<SpreadsheetMetadataPropertyNameModifiedBy> {

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameModifiedBy.instance(), "modified-by");
    }

    @Override
    SpreadsheetMetadataPropertyNameModifiedBy createName() {
        return SpreadsheetMetadataPropertyNameModifiedBy.instance();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameModifiedBy> type() {
        return SpreadsheetMetadataPropertyNameModifiedBy.class;
    }
}
