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

import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;

/**
 * A sample include a value that may be used to provide a sample of a {@link SpreadsheetFormatter}.
 * A {@link SpreadsheetFormatterProvider} will return a {@link List} providing samples of the given {@link SpreadsheetFormatter}.
 */
public final class SpreadsheetFormatterSample<T> implements TreePrintable, Value<T> {

    public static <T> SpreadsheetFormatterSample<T> with(final String label,
                                                         final SpreadsheetFormatterSelector selector,
                                                         final T value) {
        return new SpreadsheetFormatterSample<>(
                CharSequences.failIfNullOrEmpty(label, "label"),
                Objects.requireNonNull(selector, "selector"),
                value
        );
    }

    private SpreadsheetFormatterSample(final String label,
                                       final SpreadsheetFormatterSelector selector,
                                       final T value) {
        this.label = label;
        this.selector = selector;
        this.value = value;
    }

    public String label() {
        return this.label;
    }

    private final String label;

    public SpreadsheetFormatterSelector selector() {
        return this.selector;
    }

    public SpreadsheetFormatterSample<?> setSelector(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return this.selector.equals(selector) ?
                this :
                new SpreadsheetFormatterSample<>(
                        this.label,
                        selector,
                        this.value
                );
    }

    private final SpreadsheetFormatterSelector selector;

    @Override
    public T value() {
        return this.value;
    }

    private final T value;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.label,
                this.selector,
                this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormatterSample && this.equals0((SpreadsheetFormatterSample<?>) other);
    }

    private boolean equals0(final SpreadsheetFormatterSample<?> other) {
        return this.label.equals(other.label) &&
                this.selector.equals(other.selector) &&
                Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return this.label + " " + selector + " " + CharSequences.quoteIfChars(this.value);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.label.toString());

        printer.indent();
        {
            this.selector.printTree(printer);
            TreePrintable.printTreeOrToString(
                    this.value,
                    printer
            );
            printer.lineStart();
        }
        printer.outdent();
    }

    // json.............................................................................................................

    static SpreadsheetFormatterSample<?> unmarshall(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        String label = null;
        SpreadsheetFormatterSelector selector = null;
        Object value = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case LABEL_PROPERTY_STRING:
                    label = child.stringOrFail();
                    break;
                case SELECTOR_PROPERTY_STRING:
                    selector = context.unmarshall(
                            child,
                            SpreadsheetFormatterSelector.class
                    );
                    break;
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallWithType(child);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == label) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(LABEL_PROPERTY, node);
        }
        if (null == selector) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(SELECTOR_PROPERTY, node);
        }

        return SpreadsheetFormatterSample.with(
                label,
                selector,
                value
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .setChildren(
                        Lists.of(
                                JsonNode.string(this.label).setName(LABEL_PROPERTY),
                                context.marshall(this.selector).setName(SELECTOR_PROPERTY),
                                context.marshallWithType(this.value).setName(VALUE_PROPERTY)
                        )
                );
    }

    private final static String LABEL_PROPERTY_STRING = "label";

    private final static String SELECTOR_PROPERTY_STRING = "selector";
    private final static String VALUE_PROPERTY_STRING = "value";

    // @VisibleForTesting

    final static JsonPropertyName LABEL_PROPERTY = JsonPropertyName.with(LABEL_PROPERTY_STRING);
    final static JsonPropertyName SELECTOR_PROPERTY = JsonPropertyName.with(SELECTOR_PROPERTY_STRING);
    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormatterSample.class),
                SpreadsheetFormatterSample::unmarshall,
                SpreadsheetFormatterSample::marshall,
                SpreadsheetFormatterSample.class
        );
    }
}
