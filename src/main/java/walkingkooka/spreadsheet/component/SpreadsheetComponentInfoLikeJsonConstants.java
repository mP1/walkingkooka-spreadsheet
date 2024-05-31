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

package walkingkooka.spreadsheet.component;

import walkingkooka.tree.json.JsonPropertyName;

/**
 * Because {@link SpreadsheetComponentInfoLike} is an interface it cannot contain non public constants, so they are held here.
 */
final class SpreadsheetComponentInfoLikeJsonConstants {

    // Json.............................................................................................................

    final static String URL_PROPERTY_STRING = "url";

    final static String NAME_PROPERTY_STRING = "name";

    // @VisibleForTesting
    final static JsonPropertyName URL_PROPERTY = JsonPropertyName.with(URL_PROPERTY_STRING);

    final static JsonPropertyName NAME_PROPERTY = JsonPropertyName.with(NAME_PROPERTY_STRING);

    /**
     * Stop creation
     */
    private SpreadsheetComponentInfoLikeJsonConstants() {
        throw new UnsupportedOperationException();
    }
}
