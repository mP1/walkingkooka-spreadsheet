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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.plugin.PluginSelectorLikeTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetCellImporterSelectorTest implements PluginSelectorLikeTesting<SpreadsheetCellImporterSelector, SpreadsheetCellImporterName> {

    @Override
    public SpreadsheetCellImporterSelector createPluginSelectorLike(final SpreadsheetCellImporterName name,
                                                                    final String text) {
        return SpreadsheetCellImporterSelector.with(
                name,
                text
        );
    }

    @Override
    public SpreadsheetCellImporterName createName(final String value) {
        return SpreadsheetCellImporterName.with(value);
    }

    @Test
    public void testParseSpreadsheetCellImporter() {
        final String name = "test-sample-123";
        final String text = "@@";

        this.parseStringAndCheck(
                name + " " + text,
                SpreadsheetCellImporterSelector.with(
                        SpreadsheetCellImporterName.with(name),
                        text
                )
        );
    }

    @Override
    public SpreadsheetCellImporterSelector parseString(final String text) {
        return SpreadsheetCellImporterSelector.parse(text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellImporterSelector> type() {
        return SpreadsheetCellImporterSelector.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createJsonNodeMarshallingValue(),
                "\"test-sample-123 @@\""
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
                "\"test-sample-123 @@\"",
                this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public SpreadsheetCellImporterSelector unmarshall(final JsonNode json,
                                                      final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellImporterSelector.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetCellImporterSelector createJsonNodeMarshallingValue() {
        return SpreadsheetCellImporterSelector.with(
                SpreadsheetCellImporterName.with("test-sample-123"),
                "@@"
        );
    }

    // type name........................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetCellImporter.class.getSimpleName();
    }
}
