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
package walkingkooka.spreadsheet.hateos;

import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.type.PublicStaticHelper;

import java.util.function.Supplier;

/**
 * A collection of factory methods to create various {@link SpreadsheetHateosHandler}.
 */
public final class SpreadsheetHateosHandlers implements PublicStaticHelper {

    /**
     * {@see SpreadsheetEngineCopyColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference, SpreadsheetDelta, SpreadsheetDelta> copyCells(final SpreadsheetEngine engine,
                                                                                                        final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineCopyCellsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineDeleteColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> deleteColumns(final SpreadsheetEngine engine,
                                                                                                              final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteColumnsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineDeleteRowsHateosHandler}
     */
    public static HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> deleteRows(final SpreadsheetEngine engine,
                                                                                                        final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineDeleteRowsHateosHandler.with(engine, context);
    }


    /**
     * {@see SpreadsheetEngineInsertColumnsHateosHandler}
     */
    public static HateosHandler<SpreadsheetColumnReference, SpreadsheetDelta, SpreadsheetDelta> insertColumns(final SpreadsheetEngine engine,
                                                                                                              final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertColumnsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineInsertRowsHateosHandler}
     */
    public static HateosHandler<SpreadsheetRowReference, SpreadsheetDelta, SpreadsheetDelta> insertRows(final SpreadsheetEngine engine,
                                                                                                        final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineInsertRowsHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineLoadCellHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> loadCell(final SpreadsheetEngine engine,
                                                                                                      final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineLoadCellHateosHandler.with(engine, context);
    }

    /**
     * {@see SpreadsheetEngineSaveCellHateosHandler}
     */
    public static HateosHandler<SpreadsheetCellReference, SpreadsheetCell, SpreadsheetDelta> saveCell(final SpreadsheetEngine engine,
                                                                                                      final Supplier<SpreadsheetEngineContext> context) {
        return SpreadsheetEngineSaveCellHateosHandler.with(engine, context);
    }

    /**
     * Stop creation.
     */
    private SpreadsheetHateosHandlers() {
        throw new UnsupportedOperationException();
    }
}
