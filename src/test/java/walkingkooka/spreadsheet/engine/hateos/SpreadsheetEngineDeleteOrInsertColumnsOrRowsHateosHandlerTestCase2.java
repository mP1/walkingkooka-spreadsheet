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

package walkingkooka.spreadsheet.engine.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReference;

import java.util.Optional;

public abstract class SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandlerTestCase2<H extends SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandler<I>,
        I extends SpreadsheetColumnOrRowReference<I>>
        extends SpreadsheetEngineHateosHandlerTestCase2<H, I> {

    SpreadsheetEngineDeleteOrInsertColumnsOrRowsHateosHandlerTestCase2() {
        super();
    }

    @Test
    public final void testDeleteResourceWithCellsFails() {
        final Optional<I> id = this.id();
        final Optional<SpreadsheetDelta> resource = Optional.of(SpreadsheetDelta.with(Sets.of(this.cell())));
        this.handleFails(id,
                resource,
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public final void testDeleteResourceCollectionWithCellsFails() {
        final Range<I> id = this.collection();
        final Optional<SpreadsheetDelta> resource = Optional.of(SpreadsheetDelta.with(Sets.of(this.cell())));
        this.handleCollectionFails(id,
                resource,
                this.parameters(),
                IllegalArgumentException.class);
    }
}
