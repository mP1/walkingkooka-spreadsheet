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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStoreTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStore;
import walkingkooka.spreadsheet.store.SpreadsheetColumnStores;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStore;
import walkingkooka.spreadsheet.store.SpreadsheetExpressionReferenceStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.text.TextNode;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataStampingSpreadsheetEngineTest implements SpreadsheetEngineTesting<SpreadsheetMetadataStampingSpreadsheetEngine>,
        ToStringTesting<SpreadsheetMetadataStampingSpreadsheetEngine> {

    private final static SpreadsheetId ID = SpreadsheetId.parse("123");
    private final static SpreadsheetMetadata BEFORE = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
            .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
            .loadFromLocale()
            .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, ID)
            .set(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"))
            .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 0))
            .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"))
            .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 0))
            .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector());

    private final static LocalDateTime TIMESTAMP = LocalDateTime.now();

    private final static String FORMULA_VALUE = "Hello";
    private final static String FORMULA_TEXT = "'" + FORMULA_VALUE;

    @Test
    public void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetMetadataStampingSpreadsheetEngine.with(null, Function.identity()));
    }

    @Test
    public void testWithNullStamperFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetMetadataStampingSpreadsheetEngine.with(SpreadsheetEngines.fake(), null));
    }

    @Test
    public void testLoadCell() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        assertNotNull(
                engine.loadCells(
                        SpreadsheetSelection.A1,
                        SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                        SpreadsheetDeltaProperties.ALL,
                        context
                )
        );

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testLoadCellEvaluateStamps() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell cell = this.cell();

        context.storeRepository().cells().save(cell);

        final SpreadsheetDelta delta = engine.loadCells(
                cell.reference(),
                SpreadsheetEngineEvaluation.FORCE_RECOMPUTE,
                SpreadsheetDeltaProperties.ALL,
                context
        );
        final SpreadsheetCell loaded = delta.cells().iterator().next();
        this.checkEquals(Optional.of(TextNode.text(FORMULA_VALUE)), loaded.formattedValue(), "formattedValue");
        this.checkEquals(Optional.of(FORMULA_VALUE), loaded.formula().value());

        this.checkMetadataUpdated(context);
    }

    @Test
    public void testSaveCellStamped() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell cell = this.cell();
        final SpreadsheetDelta saved = engine.saveCell(cell, context);
        assertNotNull(saved);

        this.checkMetadataUpdated(context);
    }

    @Test
    public void testDeleteCellNoop() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        engine.deleteCells(SpreadsheetSelection.A1, context);

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testDeleteCellStamped() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell cell = this.cell();
        final SpreadsheetDelta saved = engine.saveCell(cell, context);
        assertNotNull(saved);

        context.storeRepository().metadatas().save(BEFORE);

        engine.deleteCells(cell.reference(), context);

        this.checkMetadataUpdated(context);
    }

    @Override
    public void testSortCellsWithNullCellRangeFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSortCellsWithNullComparatorsFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSortCellsWithNullDeltaPropertiesFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testDeleteColumnNoop() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        engine.deleteColumns(SpreadsheetSelection.parseColumn("Z"), 1, context);

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testDeleteColumnNoop2() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell cell = this.cell();
        final SpreadsheetDelta saved = engine.saveCell(cell, context);
        assertNotNull(saved);

        context.storeRepository().metadatas().save(BEFORE);

        engine.deleteColumns(cell.reference().column().add(1), 1, context);

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testDeleteColumn() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell cell = this.cell();
        final SpreadsheetDelta saved = engine.saveCell(cell, context);
        assertNotNull(saved);

        context.storeRepository().metadatas().save(BEFORE);

        engine.deleteColumns(cell.reference().column(), 1, context);

        this.checkMetadataUpdated(context);
    }

    @Test
    public void testInsertColumnNoop() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        engine.insertColumns(SpreadsheetSelection.parseColumn("Z"), 0, context);

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testInsertColumn() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        engine.insertColumns(SpreadsheetSelection.parseColumn("B"), 1, context);

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testDeleteRowNoop() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        engine.deleteRows(SpreadsheetSelection.parseRow("99"), 1, context);

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testDeleteRowNoop2() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell cell = this.cell();
        final SpreadsheetDelta saved = engine.saveCell(cell, context);
        assertNotNull(saved);

        context.storeRepository().metadatas().save(BEFORE);

        engine.deleteRows(cell.reference().row().add(1), 1, context);

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testDeleteRow() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        final SpreadsheetCell cell = this.cell();
        final SpreadsheetDelta saved = engine.saveCell(cell, context);
        assertNotNull(saved);

        context.storeRepository().metadatas().save(BEFORE);

        engine.deleteRows(cell.reference().row(), 1, context);

        this.checkMetadataUpdated(context);
    }

    @Test
    public void testInsertRowNoop() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        engine.insertRows(SpreadsheetSelection.parseRow("99"), 0, context);

        this.checkMetadataNotUpdated(context);
    }

    @Test
    public void testInsertRow() {
        final SpreadsheetMetadataStampingSpreadsheetEngine engine = this.createSpreadsheetEngine();
        final SpreadsheetEngineContext context = this.createContext();

        engine.insertRows(SpreadsheetSelection.parseRow("99"), 1, context);

        this.checkMetadataNotUpdated(context);
    }

    @Override
    public void testFilterCellsWithExpressionFalse() {
        // nop context created below hardcoded to return "Hello" ignoring Expression
    }

    private SpreadsheetCell cell() {
        return SpreadsheetSelection.A1
                .setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText(FORMULA_TEXT)
                                .setExpression(
                                        Optional.of(
                                                Expression.value(FORMULA_VALUE)
                                        )
                                ).setValue(
                                        Optional.of(FORMULA_VALUE)
                                )
                );
    }

    private void checkMetadataUpdated(final SpreadsheetEngineContext context) {
        checkMetadata(context, this.stamper().apply(BEFORE));
    }

    private void checkMetadataNotUpdated(final SpreadsheetEngineContext context) {
        this.checkMetadata(context, BEFORE);
    }

    private void checkMetadata(final SpreadsheetEngineContext context, final SpreadsheetMetadata metadata) {
        final SpreadsheetStoreRepository repository = context.storeRepository();
        final SpreadsheetMetadataStore store = repository.metadatas();
        this.checkEquals(metadata, store.loadOrFail(ID));
    }

    @Test
    public void testToString() {
        final SpreadsheetEngine engine = SpreadsheetEngines.fake();

        this.toStringAndCheck(
                SpreadsheetMetadataStampingSpreadsheetEngine.with(engine, Function.identity()),
                engine.toString()
        );
    }

    @Override
    public SpreadsheetMetadataStampingSpreadsheetEngine createSpreadsheetEngine() {
        return SpreadsheetMetadataStampingSpreadsheetEngine.with(
                SpreadsheetEngines.basic(),
                this.stamper()
        );
    }

    private Function<SpreadsheetMetadata, SpreadsheetMetadata> stamper() {
        return (m) -> {
            assertSame(BEFORE, m, "before stamp");
            return m.set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, TIMESTAMP);
        };
    }

    @Override
    public SpreadsheetEngineContext createContext() {
        final SpreadsheetCellStore cells = SpreadsheetCellStores.treeMap();
        final SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences = SpreadsheetExpressionReferenceStores.treeMap();
        final SpreadsheetColumnStore columns = SpreadsheetColumnStores.treeMap();
        final SpreadsheetLabelStore labels = SpreadsheetLabelStores.treeMap();
        final SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences = SpreadsheetExpressionReferenceStores.treeMap();
        final SpreadsheetMetadataStore metadatas = SpreadsheetMetadataStores.treeMap(
                SpreadsheetMetadataStoreTesting.CREATE_TEMPLATE,
                LocalDateTime::now
        );
        final SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells = SpreadsheetCellRangeStores.treeMap();
        final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules = SpreadsheetCellRangeStores.treeMap();
        final SpreadsheetRowStore rows = SpreadsheetRowStores.treeMap();

        metadatas.save(BEFORE);

        return new FakeSpreadsheetEngineContext() {

            @Override
            public SpreadsheetMetadata spreadsheetMetadata() {
                return BEFORE;
            }

            @Override
            public SpreadsheetStoreRepository storeRepository() {
                return new FakeSpreadsheetStoreRepository() {
                    @Override
                    public SpreadsheetCellStore cells() {
                        return cells;
                    }

                    @Override
                    public SpreadsheetExpressionReferenceStore<SpreadsheetCellReference> cellReferences() {
                        return cellReferences;
                    }

                    @Override
                    public SpreadsheetColumnStore columns() {
                        return columns;
                    }

                    @Override
                    public SpreadsheetLabelStore labels() {
                        return labels;
                    }

                    @Override
                    public SpreadsheetExpressionReferenceStore<SpreadsheetLabelName> labelReferences() {
                        return labelReferences;
                    }

                    @Override
                    public SpreadsheetMetadataStore metadatas() {
                        return metadatas;
                    }

                    @Override
                    public SpreadsheetCellRangeStore<SpreadsheetCellReference> rangeToCells() {
                        return rangeToCells;
                    }

                    @Override
                    public SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
                        return rangeToConditionalFormattingRules;
                    }

                    @Override
                    public SpreadsheetRowStore rows() {
                        return rows;
                    }

                    @Override
                    public String toString() {
                        return "cells: " + this.cells() +
                                " cellReferences: " + cellReferences +
                                " columns: " + this.columns() +
                                " labels: " + this.labels() +
                                " labelReferences: " + this.labelReferences() +
                                " metadatas: " + this.metadatas() +
                                " rangeToCells: " + this.rangeToCells() +
                                " rangeToConditionalFormattingRules: " + this.rangeToConditionalFormattingRules() +
                                " rows: " + this.rows();
                    }
                };
            }

            @Override
            public SpreadsheetFormulaParserToken parseFormula(final TextCursor formula) {
                final TextCursorSavePoint beginning = formula.save();
                formula.end();

                checkEquals(
                        FORMULA_TEXT,
                        beginning.textBetween(),
                        "formula text"
                );
                return SpreadsheetFormulaParserToken.text(
                        Lists.of(
                                SpreadsheetFormulaParserToken.apostropheSymbol("'", "'"),
                                SpreadsheetFormulaParserToken.textLiteral(FORMULA_VALUE, FORMULA_VALUE)
                        ),
                        FORMULA_TEXT
                );
            }

            @Override
            public Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token) {
                return Optional.of(
                        Expression.value("Hello")
                );
            }

            @Override
            public Object evaluate(final Expression expression,
                                   final Optional<SpreadsheetCell> cell) {
                return FORMULA_VALUE;
            }

            @Override
            public Optional<TextNode> formatValue(final Object value,
                                                  final SpreadsheetFormatter formatter) {
                checkEquals(FORMULA_VALUE, value, "formatValue");
                return Optional.of(
                        SpreadsheetText.with(FORMULA_VALUE).toTextNode()
                );
            }

            @Override
            public SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                                       final Optional<SpreadsheetFormatter> formatter) {
                return cell.setFormattedValue(
                        this.formatValue(
                                cell.formula().value().get(),
                                formatter.orElse(
                                        SpreadsheetPattern.DEFAULT_TEXT_FORMAT_PATTERN.formatter()
                                )
                        )
                );
            }

            @Override
            public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                                       final ProviderContext context) {
                return SpreadsheetParserProviders.spreadsheetParsePattern(this)
                        .spreadsheetParser(
                                selector,
                                context
                        );
            }
        };
    }

    @Override
    public Class<SpreadsheetMetadataStampingSpreadsheetEngine> type() {
        return SpreadsheetMetadataStampingSpreadsheetEngine.class;
    }
}
