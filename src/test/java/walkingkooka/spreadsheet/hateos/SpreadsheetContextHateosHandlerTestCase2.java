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
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.net.http.server.hateos.HateosHandlerTesting;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetContextHateosHandlerTestCase2<H extends SpreadsheetContextHateosHandler<I, R, S>,
        I extends Comparable<I> & HasHateosLinkId,
        R extends HateosResource<Optional<I>>,
        S extends HateosResource<Range<I>>>
        extends SpreadsheetContextHateosHandlerTestCase<H>
        implements HateosHandlerTesting<H, I, R, S> {

    SpreadsheetContextHateosHandlerTestCase2() {
        super();
    }

    @Test
    public final void testWithNullSpreadsheetContextFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(null);
        });
    }

    @Override
    public final H createHandler() {
        return this.createHandler(this.context());
    }

    abstract H createHandler(final SpreadsheetContext context);

    abstract SpreadsheetContext context();
}
