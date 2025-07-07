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

package walkingkooka.spreadsheet;

import walkingkooka.net.UrlFragment;
import walkingkooka.plugin.store.Plugin;
import walkingkooka.reflect.PublicStaticHelper;

/**
 * A collection of constants common to all spreadsheet url fragments.
 */
public final class SpreadsheetUrlFragments implements PublicStaticHelper {

    public static final UrlFragment CELL = UrlFragment.with("cell");

    public static final UrlFragment COLUMN = UrlFragment.with("column");

    public static final UrlFragment ROW = UrlFragment.with("row");

    public static final UrlFragment FORMATTER = UrlFragment.with("formatter");

    public static final UrlFragment PARSER = UrlFragment.with("parser");

    public static final UrlFragment PLUGIN = UrlFragment.with(
        Plugin.HATEOS_RESOURCE_NAME.value()
    );

    /**
     * Stop creation
     */
    private SpreadsheetUrlFragments() {
        throw new UnsupportedOperationException();
    }
}
