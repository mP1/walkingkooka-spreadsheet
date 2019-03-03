package walkingkooka.spreadsheet.style;

import walkingkooka.Cast;
import walkingkooka.build.tostring.ToStringBuilder;
import walkingkooka.build.tostring.ToStringBuilderOption;
import walkingkooka.build.tostring.UsesToStringBuilder;
import walkingkooka.spreadsheet.SpreadsheetFormattedCell;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.JsonObjectNode;

import java.util.Objects;

/**
 * A container holding various text-related style attributes.
 */
public final class SpreadsheetCellStyle implements HashCodeEqualsDefined,
        HasJsonNode,
        UsesToStringBuilder {

    /**
     * A {@link SpreadsheetCellStyle} without any properties.
     */
    public final static SpreadsheetCellStyle EMPTY = new SpreadsheetCellStyle(SpreadsheetTextStyle.EMPTY);

    /**
     * Factory that creates a {@link SpreadsheetCellStyle}
     */
    public static SpreadsheetCellStyle with(final SpreadsheetTextStyle text) {
        checkText(text);

        return new SpreadsheetCellStyle(text);
    }

    /**
     * Private ctor use static factory or constant.
     */
    private SpreadsheetCellStyle(final SpreadsheetTextStyle text) {
        super();
        this.text = text;
    }

    /**
     * Creates a new {@link SpreadsheetFormattedCell} with the given text and this {@link SpreadsheetCellStyle}.
     */
    public SpreadsheetFormattedCell setCellFormattedText(final String text) {
        return SpreadsheetFormattedCell.with(text, this);
    }

    // text...................................................................................................

    public SpreadsheetTextStyle text() {
        return this.text;
    }

    public SpreadsheetCellStyle setText(final SpreadsheetTextStyle text) {
        checkText(text);

        return this.text.equals(text) ?
                this :
                this.replace(text);
    }

    /**
     * The style of the text within the cell.
     */
    private final SpreadsheetTextStyle text;

    private static void checkText(final SpreadsheetTextStyle text) {
        Objects.requireNonNull(text, "text");
    }

    /**
     * Factory that unconditionally creates a {@link SpreadsheetCellStyle}.
     */
    private SpreadsheetCellStyle replace(final SpreadsheetTextStyle text) {
        return new SpreadsheetCellStyle(text);
    }

    /**
     * Performs a cascading merge of all properties, if a property is absent in this then the property from other is used.
     */
    public SpreadsheetCellStyle merge(final SpreadsheetCellStyle other) {
        Objects.requireNonNull(other, "other");

        final SpreadsheetCellStyle merged = new SpreadsheetCellStyle(this.text.merge(other.text));

        return this.equals0(merged) ?
                this :
                other.equals0(merged) ?
                        other :
                        merged;
    }

    /**
     * Returns true if ALL properties are empty/ absent.
     */
    public boolean isEmpty() {
        return this.text.isEmpty();
    }

    // HasJsonNode.........................................................................................

    /**
     * Factory that creates a {@link SpreadsheetCellStyle} from a {@link JsonNode}.
     */
    public static SpreadsheetCellStyle fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        // object
        SpreadsheetTextStyle text = SpreadsheetTextStyle.EMPTY;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case TEXT_PROPERTY_STRING:
                        text = SpreadsheetTextStyle.fromJsonNode(child);
                        break;
                    default:
                        HasJsonNode.unknownPropertyPresent(name, node);
                }
            }
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }

        return with(text);
    }
    
    @Override
    public JsonNode toJsonNode() {
        final JsonObjectNode object = JsonNode.object();
        final SpreadsheetTextStyle text = this.text;
        return text.isEmpty() ?
                object :
                object.set(TEXT_PROPERTY, text.toJsonNode());
    }

    private final static String TEXT_PROPERTY_STRING = "text";

    // @VisibleForTesting
    final static JsonNodeName TEXT_PROPERTY = JsonNodeName.with(TEXT_PROPERTY_STRING);

    static {
        HasJsonNode.register("spreadsheet-cell-style",
                SpreadsheetCellStyle::fromJsonNode,
                SpreadsheetCellStyle.class);
    }

    // HashCodeEqualsDefined.........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.text);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetCellStyle &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetCellStyle other) {
        return this.text.equals(other.text);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.separator(" ");
        builder.value(this.text);
    }
}
