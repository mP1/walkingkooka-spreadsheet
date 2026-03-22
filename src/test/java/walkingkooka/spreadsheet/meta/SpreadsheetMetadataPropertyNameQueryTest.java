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
import walkingkooka.spreadsheet.engine.SpreadsheetCellQuery;


public final class SpreadsheetMetadataPropertyNameQueryTest extends SpreadsheetMetadataPropertyNameTestCase<
    SpreadsheetMetadataPropertyNameQuery,
    SpreadsheetCellQuery> {

    @Test
    public void testCheckValueWithInvalidFails3() {
        this.checkValueFails(
            "invalid",
            "Metadata query=\"invalid\", Expected SpreadsheetCellQuery"
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Test
    public void testParseValueTextQuery() {
        final SpreadsheetCellQuery query = SpreadsheetCellQuery.parse("1+2");

        this.checkEquals(
            query,
            SpreadsheetMetadataPropertyName.QUERY
                .parseValueText(
                    query.text(),
                    CURRENCY_CONTEXT.setLocaleContext(LOCALE_CONTEXT)
                )
        );
    }

    // Object...........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameQuery.instance(),
            "query"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameQuery createName() {
        return SpreadsheetMetadataPropertyNameQuery.instance();
    }

    @Override
    SpreadsheetCellQuery propertyValue() {
        return SpreadsheetCellQuery.parse("1+2+3");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetCellQuery.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameQuery> type() {
        return SpreadsheetMetadataPropertyNameQuery.class;
    }
}
