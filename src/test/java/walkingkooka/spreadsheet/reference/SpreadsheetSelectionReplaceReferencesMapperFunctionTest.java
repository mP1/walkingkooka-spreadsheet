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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.util.FunctionTesting;

import java.util.Optional;

public final class SpreadsheetSelectionReplaceReferencesMapperFunctionTest implements ClassTesting<SpreadsheetSelectionReplaceReferencesMapperFunction>,
    FunctionTesting<SpreadsheetSelectionReplaceReferencesMapperFunction, SpreadsheetCellReference, Optional<SpreadsheetCellReference>> {

    @Test
    public void testRelativeCell() {
        this.applyAndCheck(
            "A1",
            "B2"
        );
    }

    @Test
    public void testRelativeColumnAbsoluteRow() {
        this.applyAndCheck(
            "A$1",
            "B$1"
        );
    }

    @Test
    public void testAbsoluteColumnRelativeRow() {
        this.applyAndCheck(
            "$A1",
            "$A2"
        );
    }

    @Test
    public void testAbsoluteColumnAbsoluteRow() {
        this.applyAndCheck(
            "$A$1",
            "$A$1"
        );
    }

    @Test
    public void testUnderflowColumnAndRow() {
        this.applyAndCheck(
            SpreadsheetSelectionReplaceReferencesMapperFunction.with(
                -1,
                -1
            ),
            SpreadsheetSelection.A1,
            Optional.empty()
        );
    }

    @Test
    public void testUnderflowColumn() {
        this.applyAndCheck(
            SpreadsheetSelectionReplaceReferencesMapperFunction.with(
                -1,
                0
            ),
            SpreadsheetSelection.A1,
            Optional.empty()
        );
    }

    @Test
    public void testUnderflowRow() {
        this.applyAndCheck(
            SpreadsheetSelectionReplaceReferencesMapperFunction.with(
                0,
                -1
            ),
            SpreadsheetSelection.A1,
            Optional.empty()
        );
    }

    private void applyAndCheck(final String cellBefore,
                               final String cellAfter) {
        this.applyAndCheck(
            this.createFunction(),
            SpreadsheetSelection.parseCell(cellBefore),
            Optional.of(
                SpreadsheetSelection.parseCell(cellAfter)
            )
        );
    }

    // FunctionTesting..................................................................................................

    @Override
    public SpreadsheetSelectionReplaceReferencesMapperFunction createFunction() {
        return SpreadsheetSelectionReplaceReferencesMapperFunction.with(
            1,
            1
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetSelectionReplaceReferencesMapperFunction> type() {
        return SpreadsheetSelectionReplaceReferencesMapperFunction.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
