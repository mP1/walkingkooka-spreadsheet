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

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.plugin.PluginName;
import walkingkooka.plugin.PluginNameLike;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * The {@link Name} of a {@link SpreadsheetImporter}. Note names are case-sensitive.
 */
final public class SpreadsheetImporterName implements PluginNameLike<SpreadsheetImporterName> {

    public static boolean isChar(final int pos,
                                 final char c) {
        return PluginNameLike.isChar(pos, c);
    }

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = PluginName.MAX_LENGTH;

    // SpreadsheetImporterName instances............................................................................

    /**
     * Factory that creates a {@link SpreadsheetImporterName}
     */
    public static SpreadsheetImporterName with(final String name) {
        Objects.requireNonNull(name, "name");

        final SpreadsheetImporterName SpreadsheetImporterName;

        switch (name) {
            default:
                SpreadsheetImporterName = new SpreadsheetImporterName(name);
                break;
        }

        return SpreadsheetImporterName;
    }

    /**
     * Private constructor
     */
    private SpreadsheetImporterName(final String name) {
        super();
        this.name = PluginName.with(name);
    }

    @Override
    public String value() {
        return this.name.value();
    }

    private final PluginName name;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetImporterName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetImporterName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }

    // Json.............................................................................................................

    static SpreadsheetImporterName unmarshall(final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetImporterName.class),
                SpreadsheetImporterName::unmarshall,
                SpreadsheetImporterName::marshall,
                SpreadsheetImporterName.class
        );
    }
}