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
import walkingkooka.convert.Converters;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetMetadataDefaultTextResourceTest implements ClassTesting<SpreadsheetMetadataDefaultTextResource> {

    @Test
    public void testDateTimeOffsetExcelOffset() {
        SpreadsheetMetadata.EMPTY.id();

        final JsonObject resource = JsonNode.parse(new SpreadsheetMetadataDefaultTextResourceProvider().text())
                .objectOrFail();
        final SpreadsheetMetadata metadata = JsonNodeUnmarshallContexts.basic(ExpressionNumberContexts.fake())
                .unmarshall(resource, SpreadsheetMetadata.class);
        assertEquals(Converters.EXCEL_OFFSET, metadata.getOrFail(SpreadsheetMetadataPropertyName.DATETIME_OFFSET), () -> resource.toString());
    }

    @Override
    public Class<SpreadsheetMetadataDefaultTextResource> type() {
        return SpreadsheetMetadataDefaultTextResource.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
