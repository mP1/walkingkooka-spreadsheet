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
import walkingkooka.plugin.PluginNameSet;

public final class SpreadsheetMetadataPropertyNamePluginNameSetTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNamePluginNameSet, PluginNameSet> {

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck();
    }

    @Override
    SpreadsheetMetadataPropertyNamePluginNameSet createName() {
        return SpreadsheetMetadataPropertyNamePluginNameSet.instance();
    }

    @Override
    PluginNameSet propertyValue() {
        return PluginNameSet.parse("test-plugin-111, test-plugin-222");
    }

    @Override
    String propertyValueType() {
        return PluginNameSet.class.getSimpleName();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNamePluginNameSet> type() {
        return SpreadsheetMetadataPropertyNamePluginNameSet.class;
    }
}
