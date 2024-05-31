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

package walkingkooka.spreadsheet.compare;

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetComponentInfoLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetComparatorInfoTest implements SpreadsheetComponentInfoLikeTesting<SpreadsheetComparatorInfo, SpreadsheetComparatorName> {

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetComparatorInfo> type() {
        return SpreadsheetComparatorInfo.class;
    }

    // SpreadsheetComponentInfoLikeTesting..............................................................................

    @Override
    public SpreadsheetComparatorName createName(final String value) {
        return SpreadsheetComparatorName.with(value);
    }

    @Override
    public SpreadsheetComparatorInfo createSpreadsheetComponentInfo(final AbsoluteUrl url,
                                                                    final SpreadsheetComparatorName name) {
        return SpreadsheetComparatorInfo.with(
                url,
                name
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetComparatorInfo unmarshall(final JsonNode json,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetComparatorInfo.unmarshall(
                json,
                context
        );
    }
}
