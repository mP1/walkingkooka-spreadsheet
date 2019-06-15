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

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetEngineHateosHandlerTestCase2<H extends HateosHandler<I, R, S>,
        I extends Comparable<I>,
        R extends HateosResource<?>,
        S extends HateosResource<?>>
        extends SpreadsheetEngineHateosHandlerTestCase<H>
        implements HateosHandlerTesting<H, I, R, S> {

    SpreadsheetEngineHateosHandlerTestCase2() {
        super();
    }

    @Test
    public final void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(null, this.engineContextSupplier());
        });
    }

    @Test
    public final void testWithNullEngineContextSupplierFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(this.engine(), null);
        });
    }

    @Override
    public final H createHandler() {
        return this.createHandler(this.engine(), this.engineContextSupplier());
    }

    abstract H createHandler(final SpreadsheetEngine engine,
                             final Supplier<SpreadsheetEngineContext> context);

    abstract SpreadsheetEngine engine();

    final Supplier<SpreadsheetEngineContext> engineContextSupplier() {
        return this::engineContext;
    }

    abstract SpreadsheetEngineContext engineContext();

    final Optional<SpreadsheetDelta> spreadsheetDeltaWithoutCells() {
        return Optional.of(SpreadsheetDelta.with(this.spreadsheetId(), SpreadsheetDelta.NO_CELLS));
    }

    final SpreadsheetCell cell(final String reference, final String formula) {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse(reference), SpreadsheetFormula.with(formula));
    }
}
