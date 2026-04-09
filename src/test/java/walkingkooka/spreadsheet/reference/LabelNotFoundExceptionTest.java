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
import walkingkooka.naming.HasNameTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting2;
import walkingkooka.spreadsheet.value.SpreadsheetError;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LabelNotFoundExceptionTest implements ThrowableTesting2<LabelNotFoundException>,
    HasNameTesting<SpreadsheetLabelName> {

    @Test
    public void testWithNullLabelFails() {
        assertThrows(
            NullPointerException.class,
            () -> new LabelNotFoundException(null)
        );
    }

    // getMessage.......................................................................................................

    @Test
    public void testGetMessage() {
        this.checkEquals(
            "Label \"Label123\" not found",
            new LabelNotFoundException(
                SpreadsheetSelection.labelName("Label123")
            ).getMessage()
        );
    }

    // HasName...........................................................................................................

    @Test
    public void testName() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.nameAndCheck(
            new LabelNotFoundException(label),
            label
        );
    }

    // HasSpreadsheetError..............................................................................................

    @Test
    public void tesSpreadsheetError() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.checkEquals(
            SpreadsheetError.referenceNotFound(label),
            new LabelNotFoundException(label)
                .spreadsheetError()
        );
    }

    // class............................................................................................................

    @Override
    public Class<LabelNotFoundException> type() {
        return LabelNotFoundException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
