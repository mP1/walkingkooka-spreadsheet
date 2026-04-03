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

package walkingkooka.spreadsheet.validation.form.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.text.CharSequences;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.store.FormStoreTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetFormStoreTesting<S extends SpreadsheetFormStore> extends FormStoreTesting<S, SpreadsheetValidationReference> {

    // findFormsByName......................................................................................................

    @Test
    default void testFindFormsByNameNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findFormsByName(
                    null,
                    0,
                    1
                )
        );
    }

    @Test
    default void testFindFormsByNameInvalidOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findFormsByName(
                    "text",
                    -1,
                    0
                )
        );
    }

    @Test
    default void testFindFormsByNameInvalidCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findFormsByName(
                    "text",
                    0,
                    -1
                )
        );
    }

    @Test
    default void testFindFormsByNameEmptyText() {
        this.findFormsByNameAndCheck(
            "",
            0,
            1
        );
    }

    @Test
    default void testFindFormsByNameZeroCount() {
        this.findFormsByNameAndCheck(
            "text",
            0,
            0
        );
    }

    default void findFormsByNameAndCheck(final String text,
                                         final int offset,
                                         final int count,
                                         final Form<SpreadsheetValidationReference>... forms) {
        this.findFormsByNameAndCheck(
            this.createStore(),
            text,
            offset,
            count,
            forms
        );
    }

    default void findFormsByNameAndCheck(final SpreadsheetFormStore store,
                                         final String text,
                                         final int offset,
                                         final int count,
                                         final Form<SpreadsheetValidationReference>... forms) {
        this.findFormsByNameAndCheck(
            store,
            text,
            offset,
            count,
            Sets.of(forms)
        );
    }

    default void findFormsByNameAndCheck(final SpreadsheetFormStore store,
                                         final String text,
                                         final int offset,
                                         final int count,
                                         final Set<Form<SpreadsheetValidationReference>> forms) {
        this.checkEquals(
            forms,
            store.findFormsByName(
                text,
                offset,
                count
            ),
            () -> "findFormsByName " + CharSequences.quoteAndEscape(text) + " offset=" + offset + " count=" + count
        );
    }
}
