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
import walkingkooka.Cast;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;


public final class SpreadsheetCellReferenceOrLabelNameTest implements ClassTesting<SpreadsheetCellReferenceOrLabelName>,
        JsonNodeMarshallingTesting<SpreadsheetCellReferenceOrLabelName> {

    @Test
    public void testUnmarshallCellReference() {
        this.unmarshallAndCheck2(SpreadsheetSelection.parseCell("B2"));
    }

    @Test
    public void testUnmarshallLabel() {
        this.unmarshallAndCheck2(SpreadsheetSelection.labelName("LABEL123456"));
    }

    private void unmarshallAndCheck2(final SpreadsheetCellReferenceOrLabelName reference) {
        this.unmarshallAndCheck(
                JsonNode.string(reference.toString()),
                reference
        );
    }

    @Test
    public void testUnmarshallRangeFails() {
        this.unmarshallFails2(
                SpreadsheetSelection.parseCellRange("A1:B2")
        );
    }

    private void unmarshallFails2(final SpreadsheetExpressionReference reference) {
        this.unmarshallFails(
                JsonNode.string(
                        reference.toString()
                )
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellReferenceOrLabelName> type() {
        return Cast.to(SpreadsheetCellReferenceOrLabelName.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // json..............................................................................................................

    @Override
    public SpreadsheetCellReferenceOrLabelName unmarshall(final JsonNode node,
                                                          final JsonNodeUnmarshallContext context) {
        return (SpreadsheetCellReferenceOrLabelName)
                SpreadsheetExpressionReference.unmarshallSpreadsheetCellReferenceOrLabelName(
                        node,
                        context
                );
    }

    @Override
    public SpreadsheetCellReferenceOrLabelName createJsonNodeMarshallingValue() {
        return (SpreadsheetCellReferenceOrLabelName) SpreadsheetSelection.parseCellOrLabel("A1");
    }
}
