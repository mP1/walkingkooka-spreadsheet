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

package walkingkooka.spreadsheet.parser.provider;

import walkingkooka.plugin.PluginSelectorTokenAlternativeLikeTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

public final class SpreadsheetParserSelectorTokenAlternativeTest implements PluginSelectorTokenAlternativeLikeTesting<SpreadsheetParserSelectorTokenAlternative>,
    ClassTesting2<SpreadsheetParserSelectorTokenAlternative> {

    @Override
    public SpreadsheetParserSelectorTokenAlternative createPluginSelectorTokenAlternativeLike(final String label,
                                                                                              final String text) {
        return SpreadsheetParserSelectorTokenAlternative.with(
            label,
            text
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParserSelectorTokenAlternative> type() {
        return SpreadsheetParserSelectorTokenAlternative.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
