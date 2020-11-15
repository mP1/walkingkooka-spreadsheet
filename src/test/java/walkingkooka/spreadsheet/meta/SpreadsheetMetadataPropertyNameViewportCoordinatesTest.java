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
import walkingkooka.spreadsheet.SpreadsheetCoordinates;

public final class SpreadsheetMetadataPropertyNameViewportCoordinatesTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameViewportCoordinates, SpreadsheetCoordinates> {

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameViewportCoordinates.instance(), "viewport-coordinates");
    }

    @Override
    SpreadsheetMetadataPropertyNameViewportCoordinates createName() {
        return SpreadsheetMetadataPropertyNameViewportCoordinates.instance();
    }

    @Override
    SpreadsheetCoordinates propertyValue() {
        return SpreadsheetCoordinates.with(1, 2);
    }

    @Override
    String propertyValueType() {
        return SpreadsheetCoordinates.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameViewportCoordinates> type() {
        return SpreadsheetMetadataPropertyNameViewportCoordinates.class;
    }
}
