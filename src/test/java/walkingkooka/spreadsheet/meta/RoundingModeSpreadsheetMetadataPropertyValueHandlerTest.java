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
import walkingkooka.tree.json.HasJsonNode;

import java.math.RoundingMode;
import java.util.Arrays;

public final class RoundingModeSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase<RoundingModeSpreadsheetMetadataPropertyValueHandler, RoundingMode> {

    @Test
    public void testInvalidRoundingModeFails() {
        this.checkFails("invalid", "Expected RoundingMode but got \"invalid\" (String)");
    }

    @Test
    public void testFromJsonNode() {
        Arrays.stream(RoundingMode.values()).forEach(
                r -> fromJsonNodeAndCheck(HasJsonNode.toJsonNodeObject(r), r)
        );
    }

    @Test
    public void testToJsonNode() {
        Arrays.stream(RoundingMode.values()).forEach(
                r -> toJsonNodeAndCheck(r, HasJsonNode.toJsonNodeObject(r))
        );
    }

    @Override
    RoundingModeSpreadsheetMetadataPropertyValueHandler handler() {
        return RoundingModeSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<RoundingMode> propertyName() {
        return SpreadsheetMetadataPropertyName.ROUNDING_MODE;
    }

    @Override
    RoundingMode propertyValue() {
        return RoundingMode.CEILING;
    }

    @Override
    String propertyValueType() {
        return RoundingMode.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return RoundingMode.class.getSimpleName();
    }

    @Override
    public Class<RoundingModeSpreadsheetMetadataPropertyValueHandler> type() {
        return RoundingModeSpreadsheetMetadataPropertyValueHandler.class;
    }
}
