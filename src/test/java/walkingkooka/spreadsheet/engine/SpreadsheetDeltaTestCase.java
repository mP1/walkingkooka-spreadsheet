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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColumn;
import walkingkooka.spreadsheet.SpreadsheetRow;
import walkingkooka.spreadsheet.SpreadsheetViewportWindows;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameSet;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceSet;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.SpreadsheetViewportAnchor;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.validation.ValidationValueTypeName;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetDeltaTestCase<D extends SpreadsheetDelta> implements ClassTesting2<D>,
    TypeNameTesting<D>,
    HashCodeEqualsDefinedTesting2<D>,
    ToStringTesting<D>,
    JsonNodeMarshallingTesting<D>,
    TreePrintableTesting {

    SpreadsheetDeltaTestCase() {
        super();
    }

    // selection........................................................................................................

    @Test
    public final void testSetViewportSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setViewport(this.viewport())
        );
    }

    @Test
    public final void testSetViewportDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Optional<SpreadsheetViewport> different = this.differentViewport();

        final SpreadsheetDelta after = before.setViewport(different);
        assertNotSame(
            before,
            after
        );

        this.viewportAndCheck(after, different);

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);

        this.columnCountAndCheck(after);
        this.rowCountAndCheck(after);

        this.viewportAndCheck(before);
    }


    final Optional<SpreadsheetViewport> viewport() {
        return Optional.of(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCellRange("A1:B2")
                            .setAnchor(SpreadsheetViewportAnchor.BOTTOM_RIGHT)
                    )
                )
        );
    }

    final Optional<SpreadsheetViewport> differentViewport() {
        return Optional.of(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCell("C3")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    final void viewportAndCheck(final SpreadsheetDelta delta) {
        this.viewportAndCheck(delta, this.viewport());
    }

    final void viewportAndCheck(final SpreadsheetDelta delta,
                                final Optional<SpreadsheetViewport> viewport) {
        this.checkEquals(
            viewport,
            delta.viewport(),
            "viewport"
        );
    }

    final JsonNode viewportJson() {
        final JsonNodeMarshallContext context = this.marshallContext();

        return context.marshall(
            this.viewport()
                .get()
        );
    }

    // cells............................................................................................................

    @Test
    public final void testCellsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCell> cells = delta.cells();

        assertThrows(
            UnsupportedOperationException.class,
            () -> cells.add(this.a1())
        );

        this.cellsAndCheck(
            delta,
            this.cells()
        );
    }

    @Test
    public final void testSetCellsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setCells(this.cells())
        );
    }

    @Test
    public final void testSetCellsWithDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetCell> different = Sets.of(
            SpreadsheetSelection.parseCell("E1")
                .setFormula(
                    SpreadsheetFormula.EMPTY
                        .setText("99")
                )
        );

        final SpreadsheetDelta after = before.setCells(different);
        assertNotSame(before, after);

        this.cellsAndCheck(
            after,
            different
        );
        this.labelsAndCheck(
            after,
            before.labels()
        );
    }

    @Test
    public final void testSetCellsSorted() {
        final SpreadsheetCell a1 = cell("A1", "1");
        final SpreadsheetCell b2 = cell("B2", "2");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setCells(Sets.of(b2, a1));

        this.cellsAndCheck(
            delta,
            Sets.of(
                a1,
                b2
            )
        );
        this.checkEquals(
            Lists.of(a1, b2),
            new ArrayList<>(delta.cells())
        );
    }

    final Set<SpreadsheetCell> cells() {
        return Sets.of(
            this.a1(),
            this.b2(),
            this.c3()
        );
    }

    final Set<SpreadsheetCell> differentCells() {
        return Sets.of(
            this.a1()
                .setFormula(SpreadsheetFormula.EMPTY.setText("'different A1")),
            this.b2()
                .setFormula(SpreadsheetFormula.EMPTY.setText("'different B2")),
            this.c3()
                .setFormula(SpreadsheetFormula.EMPTY.setText("'different C3"))
        );
    }

    final Set<SpreadsheetCell> cells0(final String... cellReferences) {
        return Arrays.stream(cellReferences)
            .map(r -> this.cell(r, "55"))
            .collect(Collectors.toCollection(() -> SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR)));
    }

    final SpreadsheetCell a1() {
        return this.cell("A1", "1");
    }

    final SpreadsheetCell b2() {
        return this.cell("B2", "2");
    }

    final SpreadsheetCell c3() {
        return this.cell("C3", "3");
    }

    final SpreadsheetCell cell(final String cellReference,
                               final String formulaText) {
        return SpreadsheetSelection.parseCell(cellReference)
            .setFormula(
                SpreadsheetFormula.EMPTY
                    .setText(formulaText)
            );
    }

    final void cellsAndCheck(final SpreadsheetDelta delta) {
        this.cellsAndCheck(delta, this.cells());
    }

    final void cellsAndCheck(final SpreadsheetDelta delta,
                             final Set<SpreadsheetCell> cells) {
        this.checkEquals(cells, delta.cells(), "cells");

        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.cells()
                .add(this.cell("ZZ99", "read only"))
        );
    }

    final JsonNode cellsJson() {
        final JsonNodeMarshallContext context = this.marshallContext();

        JsonObject object = JsonNode.object();
        object = cellsJson0(
            object,
            this.a1(),
            context
        );
        object = cellsJson0(
            object,
            this.b2(),
            context
        );
        object = cellsJson0(
            object,
            this.c3(),
            context
        );

        return object;
    }

    private static JsonObject cellsJson0(final JsonObject object,
                                         final SpreadsheetCell cell,
                                         final JsonNodeMarshallContext context) {
        JsonObject updated = object;
        for (Map.Entry<JsonPropertyName, JsonNode> propertyAndValue : context.marshall(cell)
            .objectOrFail()
            .asMap()
            .entrySet()) {
            updated = updated.set(propertyAndValue.getKey(), propertyAndValue.getValue());
        }
        return updated;
    }

    // columns..........................................................................................................

    @Test
    public final void testColumnsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetColumn> columns = delta.columns();

        assertThrows(
            UnsupportedOperationException.class,
            () -> columns.add(this.a())
        );

        this.columnsAndCheck(delta, this.columns());
    }

    @Test
    public final void testSetColumnsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setColumns(this.columns()));
    }

    @Test
    public final void testSetColumnsWithDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetColumn> different = Sets.of(
            SpreadsheetSelection.parseColumn("E")
                .column()
        );

        final SpreadsheetDelta after = before.setColumns(different);
        assertNotSame(before, after);

        this.columnsAndCheck(
            after,
            different
        );
        this.labelsAndCheck(after);

        this.checkNotEquals(
            before,
            after
        );
    }

    @Test
    public final void testSetColumnsSorted() {
        final SpreadsheetColumn a = a();
        final SpreadsheetColumn b = b();
        final SpreadsheetColumn c = c();

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setColumns(
                Sets.of(b, c, a));

        this.columnsAndCheck(
            delta,
            Sets.of(a, b, c)
        );

        this.checkEquals(
            Lists.of(a, b, c),
            new ArrayList<>(delta.columns())
        );
    }

    @Test
    public final void testSetColumnsWithHiddenFiltersCells() {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta();
        this.cellsAndCheck(
            delta,
            Sets.of(
                this.a1(),
                this.b2(),
                this.c3()
            )
        );

        final Set<SpreadsheetColumn> hiddenA = Sets.of(
            this.a().setHidden(true)
        );

        final SpreadsheetDelta after = delta.setColumns(hiddenA);
        this.cellsAndCheck(
            after,
            Sets.of(
                this.b2(),
                this.c3()
            )
        );
        this.columnsAndCheck(
            after,
            hiddenA
        );
    }

    final Set<SpreadsheetColumn> columns() {
        return Sets.of(
            this.a(),
            this.b(),
            this.c(),
            this.hiddenD()
        );
    }

    final Set<SpreadsheetColumn> differentColumns() {
        return Sets.of(
            this.a(),
            this.b(),
            this.c(),
            this.hiddenD().setHidden(false)
        );
    }

    final SpreadsheetColumn a() {
        return this.column("A");
    }

    final SpreadsheetColumn b() {
        return this.column("B");
    }

    final SpreadsheetColumn c() {
        return this.column("C");
    }

    final SpreadsheetColumn hiddenD() {
        return this.column("d")
            .setHidden(true);
    }

    final SpreadsheetColumn column(final String columnReference) {
        return SpreadsheetSelection.parseColumn(columnReference)
            .column();
    }

    final void columnsAndCheck(final SpreadsheetDelta delta) {
        this.columnsAndCheck(
            delta,
            this.columns()
        );
    }

    final void columnsAndCheck(final SpreadsheetDelta delta,
                               final Set<SpreadsheetColumn> columns) {
        this.checkEquals(columns, delta.columns(), "columns");

        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.columns()
                .add(this.column("Z"))
        );
    }

    final JsonNode columnsJson() {
        final JsonNodeMarshallContext context = this.marshallContext();

        JsonObject object = JsonNode.object();
        object = columnsJson0(
            object,
            this.a(),
            context
        );
        object = columnsJson0(
            object,
            this.b(),
            context
        );
        object = columnsJson0(
            object,
            this.c(),
            context
        );
        object = columnsJson0(
            object,
            this.hiddenD(),
            context
        );

        return object;
    }

    private static JsonObject columnsJson0(final JsonObject object,
                                           final SpreadsheetColumn column,
                                           final JsonNodeMarshallContext context) {
        JsonObject updated = object;
        for (Map.Entry<JsonPropertyName, JsonNode> propertyAndValue : context.marshall(column)
            .objectOrFail()
            .asMap()
            .entrySet()) {
            updated = updated.set(propertyAndValue.getKey(), propertyAndValue.getValue());
        }
        return updated;
    }

    // forms............................................................................................................

    @Test
    public final void testFormsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<Form<SpreadsheetExpressionReference>> forms = delta.forms();

        assertThrows(
            UnsupportedOperationException.class,
            () -> forms.clear()
        );

        this.formsAndCheck(delta);
    }

    @Test
    public final void testSetFormsWithNullFails() {
        final D delta = this.createSpreadsheetDelta();
        assertThrows(
            NullPointerException.class,
            () -> delta.setForms(null)
        );
    }

    @Test
    public final void testSetFormsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setForms(this.forms())
        );
    }

    @Test
    public void testSetFormsEmpty() {
        final D before = this.createSpreadsheetDelta();
        final Set<Form<SpreadsheetExpressionReference>> different = SpreadsheetDelta.NO_FORMS;

        final SpreadsheetDelta after = before.setForms(different);
        assertNotSame(before, after);

        this.formsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(
            after,
            different
        );
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    @Test
    public final void testSetFormsWithDifferent() {
        final D before = this.createSpreadsheetDelta();
        final Set<Form<SpreadsheetExpressionReference>> different = this.differentForms();

        final SpreadsheetDelta after = before.setForms(different);
        assertNotSame(
            before,
            after
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(
            after,
            different
        );
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    final Set<Form<SpreadsheetExpressionReference>> forms() {
        return Sets.of(
            this.form1()
        );
    }

    final Set<Form<SpreadsheetExpressionReference>> differentForms() {
        return Sets.of(
            this.form2()
        );
    }

    final void formsAndCheck(final SpreadsheetDelta delta) {
        this.formsAndCheck(
            delta,
            this.forms()
        );
    }

    final void formsAndCheck(final SpreadsheetDelta delta,
                             final Set<Form<SpreadsheetExpressionReference>> forms) {
        this.checkEquals(
            forms,
            delta.forms(),
            "forms"
        );
        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.forms()
                .add(
                    Form.with(
                        FormName.with("ThrowsUOE")
                    )
                )
        );
    }

    final Form<SpreadsheetExpressionReference> form1() {
        return Form.<SpreadsheetExpressionReference>with(
            FormName.with("Form111")
        ).setFields(
            Lists.of(
                SpreadsheetForms.field(SpreadsheetSelection.A1)
                    .setLabel("Label111")
                    .setType(
                        Optional.of(ValidationValueTypeName.TEXT)
                    )
            )
        );
    }

    final Form<SpreadsheetExpressionReference> form2() {
        return Form.with(FormName.with("Form222"));
    }

    final JsonNode formsJson() {
        return this.marshallContext()
            .marshallCollection(
                this.forms()
            );
    }

    // labels...........................................................................................................

    @Test
    public final void testLabelsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> labels = delta.labels();

        assertThrows(
            UnsupportedOperationException.class,
            () -> labels.add(
                this.label1b()
                    .setLabelMappingReference(
                        this.a1()
                            .reference()
                    )
            )
        );

        this.labelsAndCheck(delta);
    }

    @Test
    public final void testSetLabelsWithNullFails() {
        final D delta = this.createSpreadsheetDelta();
        assertThrows(
            NullPointerException.class,
            () -> delta.setLabels(null)
        );
    }

    @Test
    public final void testSetLabelsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setLabels(this.labels())
        );
    }

    @Test
    public void testSetLabelsEmpty() {
        final D before = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> different = SpreadsheetDelta.NO_LABELS;

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(before, after);

        this.labelsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    @Test
    public final void testSetLabelsWithDifferent() {
        final D before = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelMapping> different = this.differentLabels();

        final SpreadsheetDelta after = before.setLabels(different);
        assertNotSame(
            before,
            after
        );

        this.labelsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    final Set<SpreadsheetLabelMapping> labels() {
        return Sets.of(
            this.label1a().setLabelMappingReference(this.a1().reference()),
            this.label1b().setLabelMappingReference(this.a1().reference()),
            this.label2().setLabelMappingReference(this.b2().reference()),
            this.label3().setLabelMappingReference(SpreadsheetSelection.parseCellRange("C3:D4"))
        );
    }

    final Set<SpreadsheetLabelMapping> differentLabels() {
        final SpreadsheetCellReference a1 = this.a1().reference();

        return Sets.of(
            this.label1a().setLabelMappingReference(a1),
            this.label1b().setLabelMappingReference(a1),
            this.label2().setLabelMappingReference(a1),
            this.label3().setLabelMappingReference(a1)
        );
    }

    final void labelsAndCheck(final SpreadsheetDelta delta) {
        this.labelsAndCheck(
            delta,
            this.labels()
        );
    }

    final void labelsAndCheck(final SpreadsheetDelta delta,
                              final Set<SpreadsheetLabelMapping> labels) {
        this.checkEquals(labels, delta.labels(), "labels");
        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.labels()
                .add(
                    SpreadsheetLabelName.labelName("LabelZ")
                        .setLabelMappingReference(SpreadsheetSelection.parseCell("Z9")
                        )
                )
        );
    }

    final SpreadsheetLabelName label1a() {
        return SpreadsheetLabelName.labelName("LabelA1A");
    }

    final SpreadsheetLabelName label1b() {
        return SpreadsheetLabelName.labelName("LabelA1B");
    }

    final SpreadsheetLabelName label2() {
        return SpreadsheetLabelName.labelName("LabelB2");
    }

    final SpreadsheetLabelName label3() {
        return SpreadsheetLabelName.labelName("LabelC3");
    }

    final JsonNode labelsJson() {
        return this.marshallContext()
            .marshallCollection(
                this.labels()
            );
    }

    // rows.............................................................................................................

    @Test
    public final void testRowsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetRow> rows = delta.rows();

        assertThrows(
            UnsupportedOperationException.class,
            () -> rows.add(this.row1())
        );

        this.rowsAndCheck(delta, this.rows());
    }

    @Test
    public final void testSetRowsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setRows(this.rows())
        );
    }

    @Test
    public final void testSetRowsWithDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetRow> different = this.differentRows();
        final SpreadsheetDelta after = before.setRows(different);
        assertNotSame(before, after);
        this.rowsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);

        this.checkNotEquals(
            before,
            after
        );
    }

    @Test
    public final void testSetRowsSorted() {
        final SpreadsheetRow a = row1();
        final SpreadsheetRow b = row2();
        final SpreadsheetRow c = row3();

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setRows(
                Sets.of(b, c, a));

        this.rowsAndCheck(
            delta,
            Sets.of(a, b, c)
        );

        this.checkEquals(
            Lists.of(a, b, c),
            new ArrayList<>(delta.rows())
        );
    }

    @Test
    public final void testSetRowsWithHiddenFiltersCells() {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta();
        this.cellsAndCheck(
            delta,
            Sets.of(
                this.a1(),
                this.b2(),
                this.c3()
            )
        );

        final Set<SpreadsheetRow> hiddenRow1 = Sets.of(
            this.row1().setHidden(true)
        );

        final SpreadsheetDelta after = delta.setRows(hiddenRow1);
        this.cellsAndCheck(
            after,
            Sets.of(
                this.b2(),
                this.c3()
            )
        );
        this.rowsAndCheck(
            after,
            hiddenRow1
        );
    }

    final Set<SpreadsheetRow> rows() {
        return Sets.of(
            this.row1(),
            this.row2(),
            this.row3(),
            this.hiddenRow4()
        );
    }

    final Set<SpreadsheetRow> differentRows() {
        return Sets.of(
            this.row1(),
            this.row2(),
            this.row3(),
            this.hiddenRow4().setHidden(false)
        );
    }

    final SpreadsheetRow row1() {
        return this.row("1");
    }

    final SpreadsheetRow row2() {
        return this.row("2");
    }

    final SpreadsheetRow row3() {
        return this.row("3");
    }

    final SpreadsheetRow hiddenRow4() {
        return this.row("4")
            .setHidden(true);
    }

    final SpreadsheetRow row(final String rowReference) {
        return SpreadsheetSelection.parseRow(rowReference)
            .row();
    }

    final void rowsAndCheck(final SpreadsheetDelta delta) {
        this.rowsAndCheck(
            delta,
            this.rows()
        );
    }

    final void rowsAndCheck(final SpreadsheetDelta delta,
                            final Set<SpreadsheetRow> rows) {
        this.checkEquals(
            rows,
            delta.rows(),
            "rows"
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.rows()
                .add(this.row("999"))
        );
    }

    final JsonNode rowsJson() {
        final JsonNodeMarshallContext context = this.marshallContext();

        JsonObject object = JsonNode.object();
        object = rowsJson0(
            object,
            this.row1(),
            context
        );
        object = rowsJson0(
            object,
            this.row2(),
            context
        );
        object = rowsJson0(
            object,
            this.row3(),
            context
        );
        object = rowsJson0(
            object,
            this.hiddenRow4(),
            context
        );

        return object;
    }

    private static JsonObject rowsJson0(final JsonObject object,
                                        final SpreadsheetRow row,
                                        final JsonNodeMarshallContext context) {
        JsonObject updated = object;

        for (Map.Entry<JsonPropertyName, JsonNode> propertyAndValue : context.marshall(row)
            .objectOrFail()
            .asMap()
            .entrySet()) {
            updated = updated.set(propertyAndValue.getKey(), propertyAndValue.getValue());
        }
        return updated;
    }

    // references.......................................................................................................

    @Test
    public final void testReferencesReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references = delta.references();

        assertThrows(
            UnsupportedOperationException.class,
            references::clear
        );

        this.referencesAndCheck(
            delta,
            this.references()
        );
    }

    @Test
    public final void testSetReferencesWithNullFails() {
        final D delta = this.createSpreadsheetDelta();
        assertThrows(
            NullPointerException.class,
            () -> delta.setReferences(null)
        );
    }

    @Test
    public final void testSetReferencesWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setReferences(this.references())
        );
    }

    @Test
    public void testSetReferencesWithEmpty() {
        final D before = this.createSpreadsheetDelta();
        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> different = SpreadsheetDelta.NO_REFERENCES;

        final SpreadsheetDelta after = before.setReferences(different);
        assertNotSame(before, after);
        this.referencesAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    @Test
    public final void testSetReferencesWithDifferent() {
        final D before = this.createSpreadsheetDelta();
        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> different = this.differentReferences();

        final SpreadsheetDelta after = before.setReferences(different);
        assertNotSame(before, after);
        this.referencesAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    @Test
    public final void testSetReferencesWithDifferentMakesRelative() {
        final D before = this.createSpreadsheetDelta();

        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("$B$2");
        final SpreadsheetDelta after = before.setReferences(
            Maps.of(
                SpreadsheetSelection.A1.toAbsolute(),
                Sets.of(b2)
            )
        );
        assertNotSame(before, after);
        this.referencesAndCheck(
            after,
            Maps.of(
                SpreadsheetSelection.A1,
                Sets.of(b2.toRelative())
            )
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references() {
        return Maps.of(
            SpreadsheetSelection.A1,
            Sets.of(
                this.b2()
                    .reference(),
                SpreadsheetSelection.parseCellRange("C3:D4"),
                this.label1a()
            )
        );
    }

    final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> differentReferences() {
        return Maps.of(
            SpreadsheetSelection.A1,
            Sets.of(
                this.b2()
                    .reference()
            )
        );
    }

    final void referencesAndCheck(final SpreadsheetDelta delta) {
        this.referencesAndCheck(
            delta,
            this.references()
        );
    }

    final void referencesAndCheck(final SpreadsheetDelta delta,
                                  final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references) {
        this.checkEquals(
            references,
            delta.references(),
            "references"
        );

        this.allRelativeAndCheck(
            delta.references()
                .keySet()
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.references()
                .put(
                    b2().reference(),
                    Sets.empty()
                )
        );
    }

    final JsonNode referencesJson() {
        return JsonNode.object()
            .set(
                JsonPropertyName.with("A1"),
                JsonNode.array()
                    .appendChild(
                        marshallContext()
                            .marshallWithType(
                                SpreadsheetSelection.parseCell("B2")
                            )
                    ).appendChild(
                        marshallContext()
                            .marshallWithType(
                                SpreadsheetSelection.parseCellRange("C3:D4")
                            )
                    ).appendChild(
                        marshallContext()
                            .marshallWithType(
                                SpreadsheetSelection.labelName("LabelA1A")
                            )
                    )
            );
    }

    // deletedCells.....................................................................................................

    @Test
    public final void testDeletedCellsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCellReference> deletedCells = delta.deletedCells();

        assertThrows(
            UnsupportedOperationException.class,
            () -> deletedCells.add(
                this.a1()
                    .reference()
            )
        );

        this.deletedCellsAndCheck(
            delta,
            this.deletedCells()
        );
    }

    @Test
    public final void testSetDeletedCellsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setDeletedCells(this.deletedCells())
        );
    }

    @Test
    public final void testSetDeletedCellsWithDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetCellReference> different = this.differentDeletedCells();

        final SpreadsheetDelta after = before.setDeletedCells(different);
        assertNotSame(
            before,
            after
        );

        this.deletedCellsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);

        this.checkNotEquals(
            before,
            after
        );
    }

    @Test
    public final void testSetDeletedCellsSorted() {
        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setDeletedCells(
                Sets.of(
                    b2,
                    a1
                )
            );

        this.deletedCellsAndCheck(
            delta,
            Sets.of(a1, b2)
        );
        this.checkEquals(
            Lists.of(
                a1,
                b2
            ),
            new ArrayList<>(
                delta.deletedCells()
            )
        );
    }

    @Test
    public final void testSetDeletedCellsAllRelative() {
        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B$2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C$3");
        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("D4");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setDeletedCells(Sets.of(b2, a1, d4, c3));

        this.deletedCellsAndCheck(
            delta,
            Sets.of(
                a1.toRelative(),
                b2.toRelative(),
                c3.toRelative(),
                d4.toRelative()
            )
        );
    }

    final SpreadsheetCellReferenceSet deletedCells() {
        return SpreadsheetCellReferenceSet.parse("C1,C2");
    }

    final SpreadsheetCellReferenceSet differentDeletedCells() {
        return SpreadsheetCellReferenceSet.parse("C2");
    }

    final void deletedCellsAndCheck(final SpreadsheetDelta delta) {
        this.deletedCellsAndCheck(
            delta,
            this.deletedCells()
        );
    }

    final void deletedCellsAndCheck(final SpreadsheetDelta delta,
                                    final Set<SpreadsheetCellReference> cells) {
        this.checkEquals(
            cells,
            delta.deletedCells(),
            "deletedCells"
        );

        this.allRelativeAndCheck(
            delta.deletedColumns()
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.deletedCells()
                .add(null)
        );
    }

    final JsonNode deletedCellsJson() {
        return JsonNode.string("C1,C2");
    }

    // deletedColumns...................................................................................................

    @Test
    public final void testDeletedColumnsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetColumnReference> deletedColumns = delta.deletedColumns();

        assertThrows(
            UnsupportedOperationException.class,
            () -> deletedColumns.add(this.a1().reference().column())
        );

        this.deletedColumnsAndCheck(
            delta,
            this.deletedColumns()
        );
    }

    @Test
    public final void testSetDeletedColumnsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setDeletedColumns(
                this.deletedColumns()
            )
        );
    }

    @Test
    public final void testSetDeletedColumnsDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetColumnReference> different = this.differentDeletedColumns();

        final SpreadsheetDelta after = before.setDeletedColumns(different);
        assertNotSame(
            before,
            after
        );

        this.deletedColumnsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    @Test
    public final void testSetDeletedColumnsSorted() {
        final SpreadsheetColumnReference a = SpreadsheetSelection.parseColumn("A");
        final SpreadsheetColumnReference b = SpreadsheetSelection.parseColumn("B");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setDeletedColumns(
                Sets.of(b, a)
            );

        this.deletedColumnsAndCheck(
            delta,
            Sets.of(a, b)
        );
    }

    @Test
    public final void testSetDeletedColumnsAllRelative() {
        final SpreadsheetColumnReference a = SpreadsheetSelection.parseColumn("$A");
        final SpreadsheetColumnReference b = SpreadsheetSelection.parseColumn("B");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setDeletedColumns(Sets.of(b, a));

        this.deletedColumnsAndCheck(
            delta,
            Sets.of(
                a.toRelative(),
                b
            )
        );
    }

    final SpreadsheetColumnReferenceSet deletedColumns() {
        return SpreadsheetColumnReferenceSet.parse("C,D");
    }

    final SpreadsheetColumnReferenceSet differentDeletedColumns() {
        return SpreadsheetColumnReferenceSet.parse("E");
    }

    final void deletedColumnsAndCheck(final SpreadsheetDelta delta) {
        this.deletedColumnsAndCheck(
            delta,
            this.deletedColumns()
        );
    }

    final void deletedColumnsAndCheck(final SpreadsheetDelta delta,
                                      final Set<SpreadsheetColumnReference> columns) {
        this.checkEquals(
            columns,
            delta.deletedColumns(),
            "deletedColumns"
        );

        this.allRelativeAndCheck(
            delta.deletedColumns()
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.deletedColumns().add(null)
        );
    }

    final JsonNode deletedColumnsJson() {
        return JsonNode.string("C,D");
    }

    // deletedRows......................................................................................................

    @Test
    public final void testDeletedRowsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetRowReference> deletedRows = delta.deletedRows();

        assertThrows(
            UnsupportedOperationException.class,
            () -> deletedRows.add(this.a1().reference().row())
        );

        this.deletedRowsAndCheck(delta, this.deletedRows());
    }

    @Test
    public final void testSetDeletedRowsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setDeletedRows(this.deletedRows())
        );
    }

    @Test
    public final void testSetDeletedRowsWithDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetRowReference> different = this.differentDeletedRows();

        final SpreadsheetDelta after = before.setDeletedRows(different);
        assertNotSame(
            before,
            after
        );

        this.deletedRowsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);
    }

    @Test
    public final void testSetDeletedRowsSorted() {
        final SpreadsheetRowReference a1 = SpreadsheetSelection.parseRow("1");
        final SpreadsheetRowReference b2 = SpreadsheetSelection.parseRow("2");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setDeletedRows(Sets.of(b2, a1));

        this.deletedRowsAndCheck(
            delta,
            Sets.of(a1, b2)
        );
        this.checkEquals(
            Lists.of(a1, b2),
            new ArrayList<>(
                delta.deletedRows()
            )
        );
    }

    @Test
    public final void testSetDeletedRowsAllRelative() {
        final SpreadsheetRowReference row1 = SpreadsheetSelection.parseRow("$1");
        final SpreadsheetRowReference row2 = SpreadsheetSelection.parseRow("2");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setDeletedRows(Sets.of(row1, row2));

        this.deletedRowsAndCheck(
            delta,
            Sets.of(
                row1.toRelative(),
                row2.toRelative()
            )
        );
    }

    final SpreadsheetRowReferenceSet deletedRows() {
        return SpreadsheetRowReferenceSet.parse("3,4");
    }

    final SpreadsheetRowReferenceSet differentDeletedRows() {
        return SpreadsheetRowReferenceSet.parse("5");
    }

    final void deletedRowsAndCheck(final SpreadsheetDelta delta) {
        this.deletedRowsAndCheck(
            delta,
            this.deletedRows()
        );
    }

    final void deletedRowsAndCheck(final SpreadsheetDelta delta,
                                   final Set<SpreadsheetRowReference> rows) {
        this.checkEquals(
            rows,
            delta.deletedRows(),
            "deletedRows"
        );

        this.allRelativeAndCheck(
            delta.deletedRows()
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.deletedRows().add(null)
        );
    }

    final JsonNode deletedRowsJson() {
        return JsonNode.string("3,4");
    }

    // deletedLabels.....................................................................................................

    @Test
    public final void testDeletedLabelsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetLabelName> deletedLabels = delta.deletedLabels();

        assertThrows(
            UnsupportedOperationException.class,
            () -> deletedLabels.add(SpreadsheetSelection.labelName("AddShouldFail")));

        this.deletedLabelsAndCheck(delta, this.deletedLabels());
    }

    @Test
    public final void testSetDeletedLabelsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setDeletedLabels(this.deletedLabels())
        );
    }

    @Test
    public final void testSetDeletedLabelsWithDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetLabelName> different = this.differentDeletedLabels();

        final SpreadsheetDelta after = before.setDeletedLabels(different);
        assertNotSame(
            before,
            after
        );

        this.deletedLabelsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);

        this.checkNotEquals(
            before,
            after
        );
    }

    final SpreadsheetLabelNameSet deletedLabels() {
        return SpreadsheetLabelNameSet.parse("DeletedLabel111,DeletedLabel222");
    }

    final SpreadsheetLabelNameSet differentDeletedLabels() {
        return SpreadsheetLabelNameSet.parse("DifferentLabel333");
    }

    final void deletedLabelsAndCheck(final SpreadsheetDelta delta) {
        this.deletedLabelsAndCheck(
            delta,
            this.deletedLabels()
        );
    }

    final void deletedLabelsAndCheck(final SpreadsheetDelta delta,
                                     final Set<SpreadsheetLabelName> labels) {
        this.checkEquals(labels,
            delta.deletedLabels(),
            "deletedLabels");
        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.deletedLabels()
                .add(null)
        );
    }

    final JsonNode deletedLabelsJson() {
        return JsonNode.string("DeletedLabel111,DeletedLabel222");
    }

    // matchedCells.....................................................................................................

    @Test
    public final void testMatchedCellsReadOnly() {
        final D delta = this.createSpreadsheetDelta();
        final Set<SpreadsheetCellReference> matchedCells = delta.matchedCells();

        assertThrows(UnsupportedOperationException.class, () -> matchedCells.add(this.a1().reference()));

        this.matchedCellsAndCheck(delta, this.matchedCells());
    }

    @Test
    public final void testSetMatchedCellsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setMatchedCells(this.matchedCells()));
    }

    @Test
    public final void testSetMatchedCellsWithDifferent() {
        final D before = this.createSpreadsheetDelta();

        final Set<SpreadsheetCellReference> different = this.differentMatchedCells();

        final SpreadsheetDelta after = before.setMatchedCells(different);
        assertNotSame(
            before,
            after
        );

        this.matchedCellsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.deletedColumnsAndCheck(after);
        this.deletedRowsAndCheck(after);

        this.columnWidthsAndCheck(after);
        this.rowHeightsAndCheck(after);

        this.checkNotEquals(
            before,
            after
        );
    }

    @Test
    public final void testSetMatchedCellsSorted() {
        final SpreadsheetCellReference a1 = SpreadsheetSelection.A1;
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B2");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setMatchedCells(Sets.of(b2, a1));

        this.matchedCellsAndCheck(
            delta,
            Sets.of(a1, b2)
        );
        this.checkEquals(
            Lists.of(
                a1,
                b2
            ),
            new ArrayList<>(
                delta.matchedCells()
            )
        );
    }

    @Test
    public final void testSetMatchedCellsAllRelative() {
        final SpreadsheetCellReference a1 = SpreadsheetSelection.parseCell("$A$1");
        final SpreadsheetCellReference b2 = SpreadsheetSelection.parseCell("B$2");
        final SpreadsheetCellReference c3 = SpreadsheetSelection.parseCell("C$3");
        final SpreadsheetCellReference d4 = SpreadsheetSelection.parseCell("D4");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta()
            .setMatchedCells(Sets.of(b2, a1, d4, c3));

        this.matchedCellsAndCheck(
            delta,
            Sets.of(
                a1,
                b2,
                c3,
                d4
            )
        );
    }

    final SpreadsheetCellReferenceSet matchedCells() {
        return SpreadsheetCellReferenceSet.parse("A1,B2,C3");
    }

    final SpreadsheetCellReferenceSet differentMatchedCells() {
        return SpreadsheetCellReferenceSet.parse("C2");
    }

    final void matchedCellsAndCheck(final SpreadsheetDelta delta,
                                    final Set<SpreadsheetCellReference> cells) {
        this.checkEquals(
            cells,
            delta.matchedCells(),
            "matchedCells"
        );

        this.allRelativeAndCheck(
            delta.matchedCells()
        );

        assertThrows(
            UnsupportedOperationException.class,
            () -> delta.matchedCells().add(null)
        );
    }

    final JsonNode matchedCellsJson() {
        return JsonNode.string("A1,B2,C3");
    }

    // setColumnWidths..................................................................................................

    @Test
    public final void testSetColumnWidthsNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetDelta().setColumnWidths(null)
        );
    }

    @Test
    public final void testSetColumnWidthsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setColumnWidths(this.columnWidths())
        );
    }

    @Test
    public final void testSetColumnWidthsWithDifferent() {
        final D delta = this.createSpreadsheetDelta();
        final Map<SpreadsheetColumnReference, Double> different = this.differentColumnWidths();

        final SpreadsheetDelta after = delta.setColumnWidths(different);
        assertNotSame(delta, after);
        this.columnWidthsAndCheck(after, different);

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.rowHeightsAndCheck(after);
    }

    final Map<SpreadsheetColumnReference, Double> columnWidths() {
        return Maps.of(
            SpreadsheetSelection.parseColumn("A"),
            50.0
        );
    }

    final static JsonNode COLUMN_WIDTHS_JSON = JsonNode.parse("{\"A\": 50.0}");

    final Map<SpreadsheetColumnReference, Double> differentColumnWidths() {
        return Maps.of(
            SpreadsheetSelection.parseColumn("B"),
            999.0
        );
    }

    final void columnWidthsAndCheck(final SpreadsheetDelta delta) {
        columnWidthsAndCheck(
            delta,
            this.columnWidths()
        );
    }

    final void columnWidthsAndCheck(final SpreadsheetDelta delta,
                                    final Map<SpreadsheetColumnReference, Double> columnWidths) {
        this.checkEquals(
            columnWidths,
            delta.columnWidths(),
            "columnWidths"
        );

        this.allRelativeAndCheck(
            delta.columnWidths()
                .keySet()
        );
    }

    // setRowHeights....................................................................................................

    @Test
    public final void testSetRowHeightsWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetDelta()
                .setRowHeights(null)
        );
    }

    @Test
    public final void testSetRowHeightsWithSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setRowHeights(this.rowHeights())
        );
    }

    @Test
    public final void testSetRowHeightsWithDifferent() {
        final D delta = this.createSpreadsheetDelta();

        final Map<SpreadsheetRowReference, Double> different = this.differentRowHeights();
        final SpreadsheetDelta after = delta.setRowHeights(different);
        assertNotSame(
            delta,
            after
        );
        this.rowHeightsAndCheck(
            after,
            different
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.deletedCellsAndCheck(after);
        this.columnWidthsAndCheck(after);
    }

    final Map<SpreadsheetRowReference, Double> rowHeights() {
        return Maps.of(
            SpreadsheetSelection.parseRow("1"),
            75.0
        );
    }

    final static JsonNode ROW_HEIGHTS_JSON = JsonNode.parse("{\"1\": 75.0}");

    final Map<SpreadsheetRowReference, Double> differentRowHeights() {
        return Maps.of(
            SpreadsheetSelection.parseRow("2"),
            999.0
        );
    }

    final void rowHeightsAndCheck(final SpreadsheetDelta delta) {
        rowHeightsAndCheck(
            delta,
            this.rowHeights()
        );
    }

    final void rowHeightsAndCheck(final SpreadsheetDelta delta,
                                  final Map<SpreadsheetRowReference, Double> rowHeights) {
        this.checkEquals(
            rowHeights,
            delta.rowHeights(),
            "rowHeights"
        );

        this.allRelativeAndCheck(
            delta.rowHeights()
                .keySet()
        );
    }

    // setWindow........................................................................................................

    @Test
    public final void testSetWindowsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(
            delta,
            delta.setWindow(this.window())
        );
    }

    @Test
    public final void testSetWindowWithDifferent() {
        final D before = this.createSpreadsheetDelta();

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("A1:Z9999");
        this.checkNotEquals(
            window,
            this.window()
        );

        final SpreadsheetDelta after = before.setWindow(window);

        this.windowAndCheck(
            after,
            window
        );

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.formsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.cellsAndCheck(before);
        this.columnsAndCheck(before);
        this.rowsAndCheck(before);
    }

    @Test
    public final void testSetDifferentWindowWithLabels() {
        final D before = this.createSpreadsheetDelta();

        final SpreadsheetViewportWindows window = SpreadsheetViewportWindows.parse("A1:Z9999");
        this.checkNotEquals(
            window,
            this.window()
        );

        final SpreadsheetDelta after = before.setWindow(window);

        this.cellsAndCheck(after);
        this.columnsAndCheck(after);
        this.labelsAndCheck(after);
        this.rowsAndCheck(after);

        this.referencesAndCheck(after);

        this.windowAndCheck(after, window);

        this.cellsAndCheck(before);
        this.columnsAndCheck(before);
        this.rowsAndCheck(before);
    }

    abstract SpreadsheetViewportWindows window();

    final SpreadsheetViewportWindows differentWindow() {
        return SpreadsheetViewportWindows.parse("A1:Z99");
    }

    final void windowAndCheck(final SpreadsheetDelta delta) {
        this.windowAndCheck(
            delta,
            this.window()
        );
    }

    final void windowAndCheck(final SpreadsheetDelta delta,
                              final SpreadsheetViewportWindows window) {
        this.checkEquals(
            window,
            delta.window(),
            "window"
        );
    }

    // equals...........................................................................................................

    @Test
    public final void testEqualsDifferentSelection() {
        final Optional<SpreadsheetViewport> viewport = this.differentViewport();
        this.checkNotEquals(
            this.viewport(),
            viewport,
            "viewport() and differentViewport() must be un equal"
        );

        this.checkNotEquals(this.createSpreadsheetDelta().setViewport(viewport));
    }

    @Test
    public final void testEqualsDifferentCells() {
        final Set<SpreadsheetCell> cells = this.differentCells();
        this.checkNotEquals(this.cells(), cells, "cells() and differentCells() must be un equal");

        this.checkNotEquals(this.createSpreadsheetDelta(cells));
    }

    @Test
    public final void testEqualsDifferentColumns() {
        final Set<SpreadsheetColumn> columns = this.differentColumns();

        this.checkNotEquals(
            this.columns(),
            columns,
            "columns() and differentColumns() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta().setColumns(columns)
        );
    }

    @Test
    public final void testEqualsDifferentLabels() {
        final Set<SpreadsheetLabelMapping> labels = this.differentLabels();
        this.checkNotEquals(this.labels(), labels, "labels() and differentLabels() must be un equal");

        final SpreadsheetDelta delta = this.createSpreadsheetDelta();

        this.checkNotEquals(
            delta,
            delta.setLabels(labels)
        );
    }

    @Test
    public final void testEqualsDifferentRows() {
        final Set<SpreadsheetRow> rows = this.differentRows();

        this.checkNotEquals(
            this.rows(),
            rows,
            "rows() and differentRows() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta().setRows(rows)
        );
    }

    @Test
    public final void testEqualsDifferentReferences() {
        final Map<SpreadsheetCellReference, Set<SpreadsheetExpressionReference>> references = this.differentReferences();

        this.checkNotEquals(
            this.references(),
            references,
            "references() and differentReferences() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta()
                .setReferences(references)
        );
    }

    @Test
    public final void testEqualsDifferentDeletedLabels() {
        final Set<SpreadsheetLabelName> deletedLabels = this.differentDeletedLabels();
        this.checkNotEquals(
            this.labels(),
            deletedLabels,
            "deletedLabels() and differentDeletedLabels() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta()
                .setDeletedLabels(deletedLabels)
        );
    }

    @Test
    public final void testEqualsDifferentColumnWidths() {
        final Map<SpreadsheetColumnReference, Double> columnWidths = this.differentColumnWidths();
        this.checkNotEquals(
            this.columnWidths(),
            columnWidths,
            "columnWidths() and differentColumnWidths() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta()
                .setColumnWidths(columnWidths)
        );
    }

    @Test
    public final void testEqualsDifferentRowHeights() {
        final Map<SpreadsheetRowReference, Double> rowHeights = this.differentRowHeights();
        this.checkNotEquals(
            this.rowHeights(),
            rowHeights,
            "rowHeights() and differentRowHeights() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta()
                .setRowHeights(rowHeights)
        );
    }

    @Test
    public final void testEqualsDifferentColumnCount() {
        final OptionalInt columnCount = this.differentColumnCount();
        this.checkNotEquals(
            this.columnCount(),
            columnCount,
            "columnCount() and differentColumnCount() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta()
                .setColumnCount(columnCount)
        );
    }

    @Test
    public final void testEqualsDifferentRowCount() {
        final OptionalInt rowCount = this.differentRowCount();
        this.checkNotEquals(
            this.rowCount(),
            rowCount,
            "rowCount() and differentRowCount() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta()
                .setRowCount(rowCount)
        );
    }

    @Test
    public final void testEqualsDifferentWindow() {
        final SpreadsheetViewportWindows differentWindow = this.differentWindow();
        this.checkNotEquals(
            this.window(),
            differentWindow,
            "window() and differentWindow() must be un equal"
        );

        this.checkNotEquals(
            this.createSpreadsheetDelta().setWindow(differentWindow)
        );
    }

    @Override
    public final D createObject() {
        return this.createSpreadsheetDelta();
    }

    private <S extends SpreadsheetSelection> void checkEquals(final Set<S> expected,
                                                              final Set<S> actual,
                                                              final String message) {
        final Set<S> ignoresKindExpected = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
        ignoresKindExpected.addAll(expected);

        this.checkEquals(
            (Object) ignoresKindExpected,
            (Object) actual,
            message
        );
    }

    private void allRelativeAndCheck(final Set<? extends SpreadsheetSelection> references) {
        checkEquals(
            Sets.empty(),
            references
                .stream()
                .filter(r -> false == r.toRelative().equals(r))
                .collect(Collectors.toCollection(SortedSets::tree)),
            () -> "non relative cell references found"
        );
    }

    // helpers..........................................................................................................

    final D createSpreadsheetDelta() {
        return this.createSpreadsheetDelta(this.cells());
    }

    abstract D createSpreadsheetDelta(final Set<SpreadsheetCell> cells);

    // unmarshall.......................................................................................................

    @Test
    public final void testUnmarshallViewportWithCell() {
        this.unmarshallViewportAndCheck(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCell("B2")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public final void testUnmarshallViewportWithCellRange() {
        this.unmarshallViewportAndCheck(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseCellRange("B2:C3")
                            .setAnchor(SpreadsheetViewportAnchor.TOP_LEFT)
                    )
                )
        );
    }

    @Test
    public final void testUnmarshallViewportWithColumn() {
        this.unmarshallViewportAndCheck(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseColumn("B")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public final void testUnmarshallViewportWithColumnRange() {
        this.unmarshallViewportAndCheck(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseColumnRange("B:CD")
                            .setAnchor(SpreadsheetViewportAnchor.RIGHT)
                    )
                )
        );
    }

    @Test
    public final void testUnmarshallViewportWithRow() {
        this.unmarshallViewportAndCheck(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseRow("2")
                            .setDefaultAnchor()
                    )
                )
        );
    }

    @Test
    public final void testUnmarshallViewportWithRowRange() {
        this.unmarshallViewportAndCheck(
            SpreadsheetSelection.A1.viewportRectangle(100, 40)
                .viewport()
                .setAnchoredSelection(
                    Optional.of(
                        SpreadsheetSelection.parseRowRange("2:34")
                            .setAnchor(SpreadsheetViewportAnchor.BOTTOM)
                    )
                )
        );
    }

    @Test
    public final void testUnmarshallNullViewport() {
        this.unmarshallViewportAndCheck(null);
    }

    abstract void unmarshallViewportAndCheck(final SpreadsheetViewport viewport);

    @Override
    public final D createJsonNodeMarshallingValue() {
        return this.createSpreadsheetDelta();
    }

    @Override
    public final D unmarshall(final JsonNode jsonNode,
                              final JsonNodeUnmarshallContext context) {
        return Cast.to(SpreadsheetDelta.unmarshall(jsonNode, context));
    }

    // helpers..........................................................................................................

    // columnCount......................................................................................................

    final OptionalInt columnCount() {
        return OptionalInt.of(88);
    }

    final static JsonNode COLUMN_COUNT_JSON = JsonNode.parse("88");

    final OptionalInt differentColumnCount() {
        return OptionalInt.empty();
    }

    final void columnCountAndCheck(final SpreadsheetDelta delta) {
        columnCountAndCheck(
            delta,
            this.columnCount()
        );
    }

    final void columnCountAndCheck(final SpreadsheetDelta delta,
                                   final OptionalInt columnCount) {
        this.checkEquals(
            columnCount,
            delta.columnCount(),
            "columnCount"
        );
    }

    // rowCount.........................................................................................................

    final OptionalInt rowCount() {
        return OptionalInt.of(99);
    }

    final static JsonNode ROW_COUNT_JSON = JsonNode.parse("99");

    final OptionalInt differentRowCount() {
        return OptionalInt.empty();
    }

    final void rowCountAndCheck(final SpreadsheetDelta delta) {
        rowCountAndCheck(
            delta,
            this.rowCount()
        );
    }

    final void rowCountAndCheck(final SpreadsheetDelta delta,
                                final OptionalInt rowCount) {
        this.checkEquals(
            rowCount,
            delta.rowCount(),
            "rowCount"
        );
    }

    // class............................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetDelta.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
