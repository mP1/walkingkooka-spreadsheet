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

package walkingkooka.spreadsheet.export.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.plugin.PluginSelectorLikeTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetExporterSelectorTest implements PluginSelectorLikeTesting<SpreadsheetExporterSelector, SpreadsheetExporterName> {

    @Override
    public SpreadsheetExporterSelector createPluginSelectorLike(final SpreadsheetExporterName name,
                                                                final String text) {
        return SpreadsheetExporterSelector.with(
            name,
            text
        );
    }

    @Override
    public SpreadsheetExporterName createName(final String value) {
        return SpreadsheetExporterName.with(value);
    }

    @Test
    public void testParseSpreadsheetCellExporter() {
        final String name = "test-sample-123";
        final String text = "@@";

        this.parseStringAndCheck(
            name + " " + text,
            SpreadsheetExporterSelector.with(
                SpreadsheetExporterName.with(name),
                text
            )
        );
    }

    @Override
    public SpreadsheetExporterSelector parseString(final String text) {
        return SpreadsheetExporterSelector.parse(text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetExporterSelector> type() {
        return SpreadsheetExporterSelector.class;
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
    public SpreadsheetExporterSelector unmarshall(final JsonNode json,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetExporterSelector.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetExporterSelector createJsonNodeMarshallingValue() {
        return SpreadsheetExporterSelector.with(
            SpreadsheetExporterName.with("test-sample-123"),
            "@@"
        );
    }

    // type name........................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetExporter.class.getSimpleName();
    }
}
