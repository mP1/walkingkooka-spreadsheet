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

package walkingkooka.spreadsheet.store.repo;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.TypeNameTesting;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public interface SpreadsheetStoreRepositoryTesting<S extends SpreadsheetStoreRepository> extends ToStringTesting<S>,
    TypeNameTesting<S> {

    @Test
    default void testCells() {
        assertNotNull(this.createStoreRepository().cells());
    }

    @Test
    default void testCellReferences() {
        assertNotNull(this.createStoreRepository().cellReferences());
    }

    @Test
    default void testGroups() {
        assertNotNull(this.createStoreRepository().groups());
    }

    @Test
    default void testLabels() {
        assertNotNull(this.createStoreRepository().labels());
    }

    @Test
    default void testLabelReferences() {
        assertNotNull(this.createStoreRepository().labelReferences());
    }

    @Test
    default void testMetadatas() {
        assertNotNull(this.createStoreRepository().metadatas());
    }

    @Test
    default void testRangeToCells() {
        assertNotNull(this.createStoreRepository().rangeToCells());
    }

    @Test
    default void testRangeToConditionalFormattingRules() {
        assertNotNull(this.createStoreRepository().rangeToConditionalFormattingRules());
    }

    @Test
    default void testUsers() {
        assertNotNull(this.createStoreRepository().users());
    }

    S createStoreRepository();

    // TypeNameTesting..................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetStoreRepository.class.getSimpleName();
    }
}
