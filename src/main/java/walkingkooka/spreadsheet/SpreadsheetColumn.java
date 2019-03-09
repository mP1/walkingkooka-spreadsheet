package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.Objects;

/**
 * Represents a single column within a spreadsheet.
 */
public final class SpreadsheetColumn implements HateosResource<SpreadsheetColumnReference>,
        Comparable<SpreadsheetColumn>,
        HashCodeEqualsDefined {

    /**
     * Factory that creates a new {@link SpreadsheetColumn}
     */
    public static SpreadsheetColumn with(final SpreadsheetColumnReference reference) {
        checkReference(reference);

        return new SpreadsheetColumn(reference);
    }

    private static void checkReference(final SpreadsheetColumnReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private SpreadsheetColumn(final SpreadsheetColumnReference reference) {
        super();
        this.reference = reference;
    }

    // HasId .......................................................................................

    public SpreadsheetColumnReference id() {
        return this.reference();
    }

    // reference .............................................................................................

    public SpreadsheetColumnReference reference() {
        return this.reference;
    }

    public SpreadsheetColumn setReference(final SpreadsheetColumnReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
                this :
                this.replace(reference);
    }

    /**
     * The reference that identifies this cell.
     */
    private final SpreadsheetColumnReference reference;

    // replace .............................................................................................

    private SpreadsheetColumn replace(final SpreadsheetColumnReference reference) {
        return new SpreadsheetColumn(reference);
    }

    // HasJsonNode.......................................................................................

    public static SpreadsheetColumn fromJsonNode(final JsonNode node) {
        return with(SpreadsheetColumnReference.fromJsonNode(node));
    }

    @Override
    public JsonNode toJsonNode() {
        return this.reference.toJsonNode();
    }

    static {
        HasJsonNode.register("spreadsheet-column",
                SpreadsheetColumn::fromJsonNode,
                SpreadsheetColumn.class);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.reference);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetColumn &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetColumn other) {
        return this.reference.equals(other.reference());
    }

    @Override
    public String toString() {
        return this.reference.toString();
    }

    // Comparable..........................................................................................

    public int compareTo(final SpreadsheetColumn other) {
        return this.reference.compareTo(other.reference);
    }
}