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

import java.util.Locale;

public final class SpreadsheetMetadataPropertyValueHandlerLocaleTest extends SpreadsheetMetadataPropertyValueHandlerTestCase3<SpreadsheetMetadataPropertyValueHandlerLocale, Locale> {

    @Test
    public void testFromJsonNode() {
        final Locale locale = this.propertyValue();
        this.fromJsonNodeAndCheck(this.toJsonNode(locale), locale);
    }

    @Test
    public void testToJsonNode() {
        final Locale locale = this.propertyValue();
        this.toJsonNodeAndCheck(locale, this.toJsonNode(locale));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerLocale handler() {
        return SpreadsheetMetadataPropertyValueHandlerLocale.INSTANCE;
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
    public Class<SpreadsheetMetadataPropertyValueHandlerLocale> type() {
        return SpreadsheetMetadataPropertyValueHandlerLocale.class;
    }
}
