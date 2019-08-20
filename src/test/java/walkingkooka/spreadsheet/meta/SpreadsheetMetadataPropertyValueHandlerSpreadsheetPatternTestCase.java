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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.tree.json.HasJsonNode;

public abstract class SpreadsheetMetadataPropertyValueHandlerSpreadsheetPatternTestCase<H extends SpreadsheetMetadataPropertyValueHandlerSpreadsheetPattern<P>, P extends SpreadsheetPattern<?>>
        extends SpreadsheetMetadataPropertyValueHandlerTestCase2<H, P> {

    SpreadsheetMetadataPropertyValueHandlerSpreadsheetPatternTestCase() {
        super();
    }

    @Test
    public void testFromJsonNode() {
        final P propertyValue = this.propertyValue();
        this.fromJsonNodeAndCheck(HasJsonNode.toJsonNodeObject(propertyValue), propertyValue);
    }

    @Test
    public void testToJsonNode() {
        final P propertyValue = this.propertyValue();
        this.toJsonNodeAndCheck(propertyValue, HasJsonNode.toJsonNodeObject(propertyValue));
    }

    @Override
    final String expectedToString() {
        return this.propertyValueType();
    }

    @Override
    String propertyValueType() {
        return this.propertyValue().getClass().getSimpleName();
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNameSuffix() {
        return this.propertyValueType();
    }
}
