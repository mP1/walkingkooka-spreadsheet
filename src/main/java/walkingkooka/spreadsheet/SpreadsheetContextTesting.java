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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.ContextTesting;
import walkingkooka.test.TypeNameTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetContextTesting<C extends SpreadsheetContext> extends ContextTesting<C>,
        TypeNameTesting<C> {

    @Test
    default void testConverterNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().converter(null);
        });
    }

    @Test
    default void testDateTimeContextNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().dateTimeContext(null);
        });
    }

    @Test
    default void testDecimalNumberContextNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().decimalNumberContext(null);
        });
    }

    @Test
    default void testFunctionsNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().functions(null);
        });
    }

    @Test
    default void testGeneralDecimalFormatPatternNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().generalDecimalFormatPattern(null);
        });
    }

    @Test
    default void testNameToColorNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().nameToColor(null);
        });
    }

    @Test
    default void testNumberToColorNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().numberToColor(null);
        });
    }

    @Test
    default void testStoreRepositoryNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().storeRepository(null);
        });
    }

    @Test
    default void testWidthNullSpreadsheetIdFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createContext().width(null);
        });
    }

    // TypeNameTesting..................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetContext.class.getSimpleName();
    }
}
