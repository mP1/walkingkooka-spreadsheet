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

package walkingkooka.spreadsheet.convert;

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.Converter;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Set;

/**
 * Part of a report about a Converter and value and type that was not supported by a {@link walkingkooka.convert.Converter}.
 */
public final class MissingConverter implements Comparable<MissingConverter>,
        TreePrintable {

    /**
     * Tests if the given {@link Converter} is able to convert a minimum of all expected values, creating a {@link MissingConverterSet}
     * or none.
     */
    public static Set<MissingConverter> verify(final Converter<SpreadsheetConverterContext> converter,
                                               final SpreadsheetMetadataPropertyName<ConverterSelector> selector,
                                               final SpreadsheetConverterContext context) {
        return MissingConverterVerifier.verify(
                converter,
                selector,
                context
        );
    }

    public static MissingConverter with(final ConverterName name,
                                        final Set<MissingConverterValue> values) {
        return new MissingConverter(
                Objects.requireNonNull(name, "name"),
                Sets.immutable(
                        Objects.requireNonNull(values, "values")
                )
        );
    }

    private MissingConverter(final ConverterName name,
                             final Set<MissingConverterValue> values) {
        this.name = name;
        this.values = values;
    }

    public ConverterName name() {
        return this.name;
    }

    private final ConverterName name;

    public Set<MissingConverterValue> values() {
        return this.values;
    }

    MissingConverter add(final MissingConverterValue value) {
        Objects.requireNonNull(value, "value");

        final Set<MissingConverterValue> newValues = Sets.ordered();
        newValues.addAll(this.values);
        newValues.add(value);

        return this.values.equals(newValues) ?
                this :
                MissingConverter.with(
                        this.name,
                        newValues
                );
    }

    private final Set<MissingConverterValue> values;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.name,
                this.values
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof MissingConverter && this.equals0((MissingConverter) other);
    }

    private boolean equals0(final MissingConverter other) {
        return this.name.equals(other.name) &&
                this.values.equals(other.values);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.name)
                .value(this.values)
                .build();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final MissingConverter other) {
        return this.name.compareTo(other.name);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.name.value());
        printer.indent();
        {
            for (final Object value : this.values) {
                TreePrintable.printTreeOrToString(
                        value,
                        printer
                );
            }
        }
        printer.outdent();
    }

    // json.............................................................................................................

    static MissingConverter unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        ConverterName converterName = null;
        Set<MissingConverterValue> values = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case NAME_PROPERTY_STRING:
                    converterName = context.unmarshall(
                            child,
                            ConverterName.class
                    );
                    break;
                case VALUES_PROPERTY_STRING:
                    values = context.unmarshallSet(
                            child,
                            MissingConverterValue.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (converterName == null) {
            JsonNodeUnmarshallContext.missingProperty(
                    NAME_PROPERTY,
                    node
            );
        }
        if (values == null) {
            JsonNodeUnmarshallContext.missingProperty(
                    VALUES_PROPERTY,
                    node
            );
        }

        return with(
                converterName,
                values
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(
                        NAME_PROPERTY,
                        context.marshall(this.name)
                ).set(
                        VALUES_PROPERTY,
                        context.marshallCollection(this.values)
                );
    }

    private final static String NAME_PROPERTY_STRING = "name";
    final static JsonPropertyName NAME_PROPERTY = JsonPropertyName.with(NAME_PROPERTY_STRING);

    private final static String VALUES_PROPERTY_STRING = "values";
    final static JsonPropertyName VALUES_PROPERTY = JsonPropertyName.with(VALUES_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(MissingConverter.class),
                MissingConverter::unmarshall,
                MissingConverter::marshall,
                MissingConverter.class
        );
    }
}
