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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.build.tostring.UsesToStringBuilder;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.JsonObjectNode;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;

import java.util.Objects;
import java.util.Optional;

/**
 * A spreadsheet cell including its formula, and other attributes such as format, text properties(styling) and more.
 */
public final class SpreadsheetCell implements HashCodeEqualsDefined,
        Comparable<SpreadsheetCell>,
        HateosResource<SpreadsheetCellReference>,
        UsesToStringBuilder {

    /**
     * Holds an absent {@link SpreadsheetCellFormat}.
     */
    public final static Optional<SpreadsheetCellFormat> NO_FORMAT = Optional.empty();

    /**
     * Holds an absent {@link TextNode}.
     */
    public final static Optional<TextNode> NO_FORMATTED_CELL = Optional.empty();

    /**
     * An empty {@link TextStyle}.
     */
    public final static TextStyle NO_STYLE = TextStyle.EMPTY;

    /**
     * Factory that creates a new {@link SpreadsheetCell}
     */
    public static SpreadsheetCell with(final SpreadsheetCellReference reference,
                                       final SpreadsheetFormula formula) {
        checkReference(reference);
        checkFormula(formula);

        return new SpreadsheetCell(reference, formula, NO_STYLE, NO_FORMAT, NO_FORMATTED_CELL);
    }

    private static void checkReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private static void checkFormula(final SpreadsheetFormula formula) {
        Objects.requireNonNull(formula, "formula");
    }

    private static void checkTextStyle(final TextStyle style) {
        Objects.requireNonNull(style, "style");
    }

    /**
     * Private ctor
     */
    private SpreadsheetCell(final SpreadsheetCellReference reference,
                            final SpreadsheetFormula formula,
                            final TextStyle style,
                            final Optional<SpreadsheetCellFormat> format,
                            final Optional<TextNode> formatted) {
        super();

        this.reference = reference;
        this.formula = formula;
        this.style = style;
        this.format = format;
        this.formatted = formatted;
    }

    // HasId .......................................................................................

    public SpreadsheetCellReference id() {
        return this.reference();
    }

    // reference .............................................................................................

    public SpreadsheetCellReference reference() {
        return this.reference;
    }

    public SpreadsheetCell setReference(final SpreadsheetCellReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
                this :
                this.replace(reference, this.formula, this.style, this.format, NO_FORMATTED_CELL);
    }

    /**
     * The reference that identifies this cell.
     */
    private final SpreadsheetCellReference reference;

    // formula .............................................................................................

    public SpreadsheetFormula formula() {
        return this.formula;
    }

    public SpreadsheetCell setFormula(final SpreadsheetFormula formula) {
        checkFormula(formula);

        return this.formula.equals(formula) ?
                this :
                this.replace(this.reference, formula, this.style, this.format, NO_FORMATTED_CELL);
    }

    /**
     * The formula that appears in this cell.
     */
    private final SpreadsheetFormula formula;

    // style .............................................................................................

    public TextStyle style() {
        return this.style;
    }

    public SpreadsheetCell setStyle(final TextStyle style) {
        checkTextStyle(style);

        return this.style.equals(style) ?
                this :
                this.replace(this.reference, this.formula, style, this.format, NO_FORMATTED_CELL);
    }

    /**
     * The cell style that is used to format the output of the formula.
     */
    private final TextStyle style;

    // format .............................................................................................

    public Optional<SpreadsheetCellFormat> format() {
        return this.format;
    }

    public SpreadsheetCell setFormat(final Optional<SpreadsheetCellFormat> format) {
        Objects.requireNonNull(format, "format");

        return this.format.equals(format) ?
                this :
                this.replace(this.reference, this.formula, this.style, format, NO_FORMATTED_CELL);
    }

    /**
     * Used to format the output of the cell's formula.
     */
    private final Optional<SpreadsheetCellFormat> format;

    // formatted .............................................................................................

    public Optional<TextNode> formatted() {
        return this.formatted;
    }

    public SpreadsheetCell setFormatted(final Optional<TextNode> formatted) {
        Objects.requireNonNull(formatted, "formatted");

        final Optional<TextNode> formatted2 = formatted.map(TextNode::root);
        return this.formatted.equals(formatted2) ?
                this :
                this.replace(this.reference, this.formula, this.style, this.format, formatted2);
    }

    /**
     * A cached form of the cell output formatted and formula executed.
     */
    private final Optional<TextNode> formatted;

    // replace..........................................................................................................

    /**
     * Replacing any of the properties other than formatted will clear formatted
     */
    private SpreadsheetCell replace(final SpreadsheetCellReference reference,
                                    final SpreadsheetFormula formula,
                                    final TextStyle style,
                                    final Optional<SpreadsheetCellFormat> format,
                                    final Optional<TextNode> formatted) {
        return new SpreadsheetCell(reference, formula, style, format, formatted);
    }

    // Comparable.................................................................................................

    @Override
    public int compareTo(final SpreadsheetCell other) {
        return this.reference().compareTo(other.reference());
    }

    // HasJsonNode..........................................................................................

    /**
     * Factory that creates a {@link SpreadsheetCell} from a {@link JsonNode}.
     */
    public static SpreadsheetCell fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        SpreadsheetCellReference reference = null;
        SpreadsheetFormula formula = null;
        TextStyle style = TextStyle.EMPTY;
        SpreadsheetCellFormat format = null;
        TextNode formatted = null;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case REFERENCE_PROPERTY_STRING:
                        reference = SpreadsheetCellReference.fromJsonNode(child);
                        break;
                    case FORMULA_PROPERTY_STRING:
                        formula = SpreadsheetFormula.fromJsonNode(child);
                        break;
                    case STYLE_PROPERTY_STRING:
                        style = TextStyle.fromJsonNode(child);
                        break;
                    case FORMAT_PROPERTY_STRING:
                        format = SpreadsheetCellFormat.fromJsonNode(child);
                        break;
                    case FORMATTED_PROPERTY_STRING:
                        formatted = TextNode.fromJsonNode(child);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown property " + name + "=" + node);
                }
            }
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }

        if (null == reference) {
            HasJsonNode.requiredPropertyMissing(REFERENCE_PROPERTY, node);
        }
        if (null == formula) {
            HasJsonNode.requiredPropertyMissing(FORMULA_PROPERTY, node);
        }

        return new SpreadsheetCell(reference, formula, style, Optional.ofNullable(format), Optional.ofNullable(formatted));
    }

    @Override
    public JsonNode toJsonNode() {
        JsonObjectNode object = JsonNode.object()
                .set(REFERENCE_PROPERTY, this.reference.toJsonNode())
                .set(FORMULA_PROPERTY, this.formula.toJsonNode());

        if (false == this.style.isEmpty()) {
            object = object.set(STYLE_PROPERTY, this.style.toJsonNodeWithType());
        }
        if (this.format.isPresent()) {
            object = object.set(FORMAT_PROPERTY, this.format.get().toJsonNode());
        }
        if (this.formatted.isPresent()) {
            object = object.set(FORMATTED_PROPERTY, this.formatted.get().toJsonNodeWithType());
        }

        return object;
    }

    private final static String REFERENCE_PROPERTY_STRING = "reference";
    private final static String FORMULA_PROPERTY_STRING = "formula";
    private final static String STYLE_PROPERTY_STRING = "style";
    private final static String FORMAT_PROPERTY_STRING = "format";
    private final static String FORMATTED_PROPERTY_STRING = "formatted";

    final static JsonNodeName REFERENCE_PROPERTY = JsonNodeName.with(REFERENCE_PROPERTY_STRING);
    final static JsonNodeName FORMULA_PROPERTY = JsonNodeName.with(FORMULA_PROPERTY_STRING);
    final static JsonNodeName STYLE_PROPERTY = JsonNodeName.with(STYLE_PROPERTY_STRING);
    final static JsonNodeName FORMAT_PROPERTY = JsonNodeName.with(FORMAT_PROPERTY_STRING);
    final static JsonNodeName FORMATTED_PROPERTY = JsonNodeName.with(FORMATTED_PROPERTY_STRING);

    static {
        HasJsonNode.register("spreadsheet-cell",
                SpreadsheetCell::fromJsonNode,
                SpreadsheetCell.class);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.reference, this.formula, this.style, this.format, this.formatted);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCell &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetCell other) {
        return this.reference.equals(other.reference()) &&
                this.formula.equals(other.formula()) &&
                this.style.equals(other.style) &&
                this.format.equals(other.format) &&
                this.formatted.equals(other.formatted);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.labelSeparator("=")
                .label(this.reference.toString())
                .value(this.formula)
                .value(this.style)
                .value(this.format)
                .value(this.formatted);
    }
}
