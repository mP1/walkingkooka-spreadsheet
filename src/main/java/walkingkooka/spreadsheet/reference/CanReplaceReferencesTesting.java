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
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface CanReplaceReferencesTesting<T extends CanReplaceReferences<T>> extends TreePrintableTesting {

    @Test
    default void testReplaceReferencesWithNullMapperFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createReplaceReference()
                .replaceReferences(null)
        );
    }

    default <T extends CanReplaceReferences<T>> void replaceReferencesAndCheck(final T can,
                                                                               final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        assertSame(
            can,
            can.replaceReferences(mapper),
            can::toString
        );
    }

    default <T extends CanReplaceReferences<T>> void replaceReferencesAndCheck(final T can,
                                                                               final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper,
                                                                               final T expected) {
        this.checkEquals(
            expected,
            can.replaceReferences(mapper),
            can::toString
        );
    }

    T createReplaceReference();
}
