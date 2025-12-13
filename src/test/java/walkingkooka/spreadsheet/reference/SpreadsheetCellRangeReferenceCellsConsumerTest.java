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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.function.Consumer;

public final class SpreadsheetCellRangeReferenceCellsConsumerTest implements ClassTesting2<SpreadsheetCellRangeReferenceCellsConsumer>, ToStringTesting<SpreadsheetCellRangeReferenceCellsConsumer> {

    @Test
    public void testToString() {
        final Consumer<SpreadsheetCell> present = this::present;
        final Consumer<SpreadsheetCellReference> absent = this::absent;

        this.toStringAndCheck(SpreadsheetCellRangeReferenceCellsConsumer.with(Lists.empty(), present, absent), present + " " + absent);
    }

    @SuppressWarnings("EmptyMethod")
    private void present(final SpreadsheetCell cells) {
    }

    @SuppressWarnings("EmptyMethod")
    private void absent(final SpreadsheetCellReference reference) {
    }

    @Override
    public Class<SpreadsheetCellRangeReferenceCellsConsumer> type() {
        return SpreadsheetCellRangeReferenceCellsConsumer.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
