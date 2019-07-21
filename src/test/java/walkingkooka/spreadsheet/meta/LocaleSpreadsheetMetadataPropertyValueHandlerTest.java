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

import java.util.Locale;

public final class LocaleSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<LocaleSpreadsheetMetadataPropertyValueHandler, Locale> {

    @Test
    public void testFromJsonNode() {
        final Locale locale = this.propertyValue();
        this.fromJsonNodeAndCheck(HasJsonNode.toJsonNodeObject(locale), locale);
    }

    @Test
    public void testToJsonNode() {
        final Locale locale = this.propertyValue();
        this.toJsonNodeAndCheck(locale, HasJsonNode.toJsonNodeObject(locale));
    }

    @Override
    LocaleSpreadsheetMetadataPropertyValueHandler handler() {
        return LocaleSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<Locale> propertyName() {
        return SpreadsheetMetadataPropertyName.LOCALE;
    }

    @Override
    Locale propertyValue() {
        return Locale.ENGLISH;
    }

    @Override
    String propertyValueType() {
        return Locale.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return Locale.class.getSimpleName();
    }

    @Override
    public Class<LocaleSpreadsheetMetadataPropertyValueHandler> type() {
        return LocaleSpreadsheetMetadataPropertyValueHandler.class;
    }
}
