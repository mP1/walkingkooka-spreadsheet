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

package walkingkooka.spreadsheet.provider;

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.locale.LocaleContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.store.PluginStore;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;

public final class SpreadsheetProviderContexts implements PublicStaticHelper {

    /**
     * {@see BasicProviderContext}
     */
    public static ProviderContext basic(final PluginStore pluginStore,
                                        final EnvironmentContext environmentContext,
                                        final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext,
                                        final LocaleContext localeContext) {
        return BasicProviderContext.with(
            pluginStore,
            environmentContext,
            jsonNodeMarshallUnmarshallContext,
            localeContext
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetProviderContexts() {
        throw new UnsupportedOperationException();
    }
}
