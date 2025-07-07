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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.spreadsheet.store.FakeSpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetLabelStoreSpreadsheetLabelNameResolverTest implements SpreadsheetLabelNameResolverTesting<SpreadsheetLabelStoreSpreadsheetLabelNameResolver>,
    ToStringTesting<SpreadsheetLabelStoreSpreadsheetLabelNameResolver> {

    private final static SpreadsheetLabelName LABEL1 = SpreadsheetSelection.labelName("Label111");

    private final static SpreadsheetLabelName LABEL2 = SpreadsheetSelection.labelName("Label222");

    private final static SpreadsheetCellReference TARGET = SpreadsheetSelection.A1;

    @Test
    public void testWithNullSpreadsheetLabelStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetLabelStoreSpreadsheetLabelNameResolver.with(null)
        );
    }

    @Test
    public void testResolveLabelWithMissingLabel() {
        this.resolveLabelAndCheck(
            SpreadsheetSelection.labelName("Unknown404")
        );
    }

    @Test
    public void testResolveLabel() {
        this.resolveLabelAndCheck(
            LABEL2,
            TARGET
        );
    }

    @Test
    public void testResolveLabelToLabelToCell() {
        this.resolveLabelAndCheck(
            LABEL1,
            TARGET
        );
    }

    @Override
    public SpreadsheetLabelStoreSpreadsheetLabelNameResolver createSpreadsheetLabelNameResolver() {
        return SpreadsheetLabelStoreSpreadsheetLabelNameResolver.with(
            new FakeSpreadsheetLabelStore() {
                @Override
                public Optional<SpreadsheetLabelMapping> load(final SpreadsheetLabelName id) {
                    return Optional.ofNullable(
                        LABEL1.equals(id) ?
                            LABEL1.setLabelMappingReference(LABEL2) :
                            LABEL2.equals(id) ?
                                LABEL2.setLabelMappingReference(TARGET) :
                                null
                    );
                }
            }
        );
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetLabelStore store = SpreadsheetLabelStores.fake();

        this.toStringAndCheck(
            SpreadsheetLabelStoreSpreadsheetLabelNameResolver.with(store),
            store.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetLabelStoreSpreadsheetLabelNameResolver> type() {
        return SpreadsheetLabelStoreSpreadsheetLabelNameResolver.class;
    }
}
