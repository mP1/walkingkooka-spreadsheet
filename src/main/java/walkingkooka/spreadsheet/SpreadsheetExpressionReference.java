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

import walkingkooka.compare.Comparators;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObjectNode;

import java.util.Comparator;

/**
 * Base class for all Spreadsheet {@link ExpressionReference}
 */
abstract public class SpreadsheetExpressionReference implements ExpressionReference, HashCodeEqualsDefined, HasJsonNode {

    /**
     * A comparator that orders {@link SpreadsheetLabelName} before {@link SpreadsheetCellReference}.
     */
    public final static Comparator<SpreadsheetExpressionReference> COMPARATOR = SpreadsheetExpressionReferenceComparator.INSTANCE;

    /**
     * {@see SpreadsheetCellReference}
     */
    public static SpreadsheetCellReference cellReference(final SpreadsheetColumnReference column,
                                                         final SpreadsheetRowReference row) {
        return SpreadsheetCellReference.with(column, row);
    }

    /**
     * {@see SpreadsheetLabelName}
     */
    public static SpreadsheetLabelName labelName(final String name) {
        return SpreadsheetLabelName.with(name);
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetExpressionReference() {
        super();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0(other);
    }

    abstract boolean canBeEqual(final Object other);

    abstract boolean equals0(final Object other);

    /**
     * Invoked by {@link SpreadsheetExpressionReferenceComparator} using double dispatch
     * to compare two {@link SpreadsheetExpressionReference}. Each sub class will use double dispatch which will invoke
     * either of the #compare0 methods. saving the need for instanceof checks.
     */
    abstract int compare(final SpreadsheetExpressionReference other);

    abstract int compare0(final SpreadsheetCellReference other);

    abstract int compare0(final SpreadsheetLabelName other);

    /**
     * Labels come before references, used as the result when a label compares with a reference.
     */
    final static int LABEL_COMPARED_WITH_CELL_RESULT = Comparators.LESS;

    /**
     * The json form of this object is also {@link #toString()}
     */
    @Override
    public final JsonNode toJsonNode() {
        return JsonObjectNode.string(this.toString());
    }
}
