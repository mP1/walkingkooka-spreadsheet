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

package walkingkooka.spreadsheet.format;

import walkingkooka.spreadsheet.SpreadsheetComponentNameTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

final public class SpreadsheetFormatterNameTest implements SpreadsheetComponentNameTesting<SpreadsheetFormatterName> {

    @Override
    public SpreadsheetFormatterName createName(final String name) {
        return SpreadsheetFormatterName.with(name);
    }

    @Override
    public Class<SpreadsheetFormatterName> type() {
        return SpreadsheetFormatterName.class;
    }

    @Override
    public SpreadsheetFormatterName unmarshall(final JsonNode from,
                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterName.unmarshall(from, context);
    }
}
