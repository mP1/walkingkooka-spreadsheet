package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.type.MemberVisibility;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaTest implements ClassTesting2<SpreadsheetDelta>,
        HashCodeEqualsDefinedTesting<SpreadsheetDelta>,
        HasJsonNodeTesting<SpreadsheetDelta>,
        ToStringTesting<SpreadsheetDelta> {

    @Test
    public void testWithNullIdFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.with(null, this.cells());
        });
    }

    @Test
    public void testWithNullCellsFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetDelta.with(this.id(), null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetId id = this.id();
        final Set<SpreadsheetCell> cells = this.cells();
        final SpreadsheetDelta delta = SpreadsheetDelta.with(id, cells);
        this.checkId(delta, id);
        this.checkCells(delta, cells);
    }

    @Test
    public void testCellsReadOnly() {
        final SpreadsheetDelta delta = SpreadsheetDelta.with(this.id(), cells());
        final Set<SpreadsheetCell> cells = delta.cells();

        assertThrows(UnsupportedOperationException.class, () -> {
            cells.clear();
        });

        this.checkCells(delta, this.cells());
    }

    // equals....................................................................................................

    @Test
    public void testDifferentId() {
        this.checkNotEquals(SpreadsheetDelta.with(this.id(), Sets.of(this.cell("A99", "99"))));
    }

    @Test
    public void testDifferentCells() {
        this.checkNotEquals(SpreadsheetDelta.with(SpreadsheetId.with(999), this.cells()));
    }

    // HasJson....................................................................................................

    @Test
    public void testFromJsonMissingIdFails() {
        this.fromJsonNodeFails(JsonNode.object()
                .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells())));
    }

    @Test
    public void testFromJsonMissingCellsFails() {
        this.fromJsonNodeFails(JsonNode.object()
                .set(SpreadsheetDelta.ID_PROPERTY, this.id().toJsonNode()));
    }

    @Test
    public void testFromJsonUnknownPropertyFails() {
        this.fromJsonNodeFails(JsonNode.object()
                .set(SpreadsheetDelta.ID_PROPERTY, this.id().toJsonNode())
                .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells()))
                .set(JsonNodeName.with("unknown"), JsonNode.booleanNode(true)));
    }

    @Test
    public void testFromJson() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.id().toJsonNode())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells())),
                this.createHasJsonNode());
    }

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createHasJsonNode(),
                JsonNode.object()
                        .set(SpreadsheetDelta.ID_PROPERTY, this.id().toJsonNode())
                        .set(SpreadsheetDelta.CELLS_PROPERTY, HasJsonNode.toJsonNodeSet(this.cells())));
    }

    // toString....................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetId id = this.id();
        final Set<SpreadsheetCell> cells = this.cells();
        this.toStringAndCheck(SpreadsheetDelta.with(id, cells), "" + cells);
    }

    // ClassTesting...............................................................................................

    @Override
    public Class<SpreadsheetDelta> type() {
        return SpreadsheetDelta.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }

    // HashCodeDefinedTesting...............................................................................................

    @Override
    public SpreadsheetDelta createObject() {
        return this.createHasJsonNode();
    }

    // HasJsonTesting...............................................................................................

    @Override
    public SpreadsheetDelta createHasJsonNode() {
        return SpreadsheetDelta.with(this.id(), this.cells());
    }

    // helpers...............................................................................................

    private SpreadsheetId id() {
        return SpreadsheetId.with(1234L);
    }

    private Set<SpreadsheetCell> cells() {
        return Sets.of(this.cell("A1", "1+2"));
    }

    private SpreadsheetCell cell(final String cellReference, final String formulaText) {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse(cellReference),
                SpreadsheetFormula.with(formulaText),
                SpreadsheetCellStyle.EMPTY);
    }

    private void checkId(final SpreadsheetDelta delta, final SpreadsheetId id) {
        assertEquals(id, delta.id(), "id");
    }

    private void checkCells(final SpreadsheetDelta delta, final Set<SpreadsheetCell> cells) {
        assertEquals(cells, delta.cells(), "cells");
    }

    @Override
    public SpreadsheetDelta fromJsonNode(final JsonNode jsonNode) {
        return SpreadsheetDelta.fromJsonNode(jsonNode);
    }
}
