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

public abstract class SpreadsheetMetadataPropertyValueHandlerTestCase3<P extends SpreadsheetMetadataPropertyValueHandler<T>, T>
        extends SpreadsheetMetadataPropertyValueHandlerTestCase2<P, T> {

    SpreadsheetMetadataPropertyValueHandlerTestCase3() {
        super();
    }

    @Override
    final String expectedToString() {
        return this.propertyValueType();
    }

    // TypeNameTesting..................................................................................................

    @Override
    public final String typeNameSuffix() {
        return this.propertyValueType();
    }
}
