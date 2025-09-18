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

package walkingkooka.spreadsheet.format.provider;

import walkingkooka.plugin.PluginHelperTesting;
import walkingkooka.reflect.JavaVisibility;

public final class SpreadsheetFormatterPluginHelperTest implements PluginHelperTesting<SpreadsheetFormatterPluginHelper,
    SpreadsheetFormatterName,
    SpreadsheetFormatterInfo,
    SpreadsheetFormatterInfoSet,
    SpreadsheetFormatterSelector,
    SpreadsheetFormatterAlias,
    SpreadsheetFormatterAliasSet> {

    @Override
    public SpreadsheetFormatterPluginHelper createPluginHelper() {
        return SpreadsheetFormatterPluginHelper.INSTANCE;
    }

    @Override
    public SpreadsheetFormatterName createName() {
        return SpreadsheetFormatterName.AUTOMATIC;
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterPluginHelper> type() {
        return SpreadsheetFormatterPluginHelper.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
