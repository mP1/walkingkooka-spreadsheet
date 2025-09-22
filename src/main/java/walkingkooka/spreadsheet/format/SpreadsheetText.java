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

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.color.Color;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.HasTextNode;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Objects;
import java.util.Optional;

/**
 * Holds the formatted text which may include a color following the formatting of a value.
 */
public final class SpreadsheetText implements HasText,
    HasTextNode,
    TreePrintable,
    UsesToStringBuilder {

    /**
     * Constant that holds an empty color.
     */
    public final static Optional<Color> WITHOUT_COLOR = Optional.empty();

    /**
     * An empty {@link SpreadsheetText}.
     */
    public final static SpreadsheetText EMPTY = new SpreadsheetText(
        WITHOUT_COLOR,
        ""
    );

    /**
     * Creates a {@link SpreadsheetText}
     */
    public static SpreadsheetText with(final String text) {
        checkText(text);

        return text.isEmpty() ?
            EMPTY :
            new SpreadsheetText(WITHOUT_COLOR, text);
    }

    private static void checkColor(final Optional<Color> color) {
        Objects.requireNonNull(color, "color");
    }

    private static void checkText(final String text) {
        Objects.requireNonNull(text, "text");
    }

    /**
     * Private ctor use factory.
     */
    private SpreadsheetText(final Optional<Color> color, final String text) {
        this.color = color;
        this.text = text;
    }

    public Optional<Color> color() {
        return this.color;
    }

    public SpreadsheetText setColor(final Optional<Color> color) {
        checkColor(color);

        return this.color.equals(color) ?
            this :
            this.replace(color, this.text);
    }

    private final Optional<Color> color;

    @Override
    public String text() {
        return this.text;
    }

    public SpreadsheetText setText(final String text) {
        checkText(text);

        return this.text.equals(text) ?
            this :
            this.replace(this.color, text);
    }

    private final String text;

    private SpreadsheetText replace(final Optional<Color> color, final String text) {
        return new SpreadsheetText(color, text);
    }

    // HasTextNode......................................................................................................

    @Override
    public TextNode textNode() {
        return this.color.map(
                c -> TextStyle.EMPTY.set(
                    TextStylePropertyName.COLOR,
                    c
                )
            ).orElse(TextStyle.EMPTY)
            .replace(TextNode.text(this.text));
    }

    // Object ..........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.text);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetText &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetText other) {
        return this.color.equals(other.color) &&
            this.text.equals(other.text);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(ToStringBuilder builder) {
        builder.separator(" ");
        builder.value(this.color);

        builder.enable(ToStringBuilderOption.QUOTE);
        builder.value(this.text);
    }

    // json..............................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetText} parse a {@link JsonNode}.
     */
    static SpreadsheetText unmarshall(final JsonNode node,
                                      final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        Color color = null;
        String text = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case COLOR_PROPERTY_STRING:
                    color = context.unmarshall(child, Color.class);
                    break;
                case TEXT_PROPERTY_STRING:
                    text = child.stringOrFail();
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == text) {
            JsonNodeUnmarshallContext.missingProperty(TEXT_PROPERTY, node);
        }

        return with(text)
            .setColor(Optional.ofNullable(color));
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object();

        final Optional<Color> color = this.color;
        if (color.isPresent()) {
            object = object.set(
                COLOR_PROPERTY,
                context.marshallOptional(color)
            );
        }

        return object.set(
            TEXT_PROPERTY,
            this.text
        );
    }

    private final static String COLOR_PROPERTY_STRING = "color";
    private final static String TEXT_PROPERTY_STRING = "text";

    final static JsonPropertyName COLOR_PROPERTY = JsonPropertyName.with(COLOR_PROPERTY_STRING);
    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);

    static {
        Color.BLACK.hashCode();

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetText.class),
            SpreadsheetText::unmarshall,
            SpreadsheetText::marshall,
            SpreadsheetText.class
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        final String text = this.text();
        if (false == text.isEmpty()) {
            final Color color = this.color().orElse(null);
            if (null != color) {
                printer.indent();
                printer.println(color.toString());
            }
            printer.indent();
            {
                printer.println(text);
            }
            printer.outdent();

            if (null != color) {
                printer.outdent();
            }
        }
    }
}
