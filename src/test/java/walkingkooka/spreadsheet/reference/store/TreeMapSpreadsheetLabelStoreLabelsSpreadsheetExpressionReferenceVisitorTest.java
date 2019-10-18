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

package walkingkooka.spreadsheet.reference.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitorTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.reflect.JavaVisibility;

public final class TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitorTest implements SpreadsheetExpressionReferenceVisitorTesting<TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor> {

    @Test
    public void testToString() {
        final SpreadsheetCellReference a1 = SpreadsheetExpressionReference.parseCellReference("A1");
        final SpreadsheetCellReference b2 = SpreadsheetExpressionReference.parseCellReference("B2");

        final SpreadsheetLabelName label1 = SpreadsheetExpressionReference.labelName("ABCDEF123");
        final SpreadsheetLabelName label2 = SpreadsheetExpressionReference.labelName("DEFGHI456");

        final TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor visitor = new TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor(b2,
                Maps.of(label1, label1.mapping(a1), label2, label2.mapping(a1)));

        visitor.accept(b2);
        visitor.accept(label1);
        visitor.accept(label2);

        this.toStringAndCheck(visitor, "[ABCDEF123, DEFGHI456]");
    }

    @Override
    public TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor(null, null);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return TreeMapSpreadsheetLabelStore.class.getSimpleName();
    }

    @Override
    public Class<TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor> type() {
        return TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor.class;
    }
}
