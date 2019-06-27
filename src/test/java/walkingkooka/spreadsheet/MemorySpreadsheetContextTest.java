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
import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.store.Store;
import walkingkooka.spreadsheet.store.repo.StoreRepository;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MemorySpreadsheetContextTest implements SpreadsheetContextTesting<MemorySpreadsheetContext> {

    @Test
    public void testWithNullSpreadsheetIdDateTimeContextFails() {
        this.withFails(null,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor);
    }

    @Test
    public void testWithNullSpreadsheetIdDecimalNumberContextFails() {
        this.withFails(this::spreadsheetIdDateTimeContext,
                null,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor);
    }

    @Test
    public void testWithNullSpreadsheetIdFunctionsFails() {
        this.withFails(this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                null,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor);
    }

    @Test
    public void testWithNullSpreadsheetIdNameToColorFails() {
        this.withFails(this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdFunctions,
                null,
                this::spreadsheetIdNumberToColor);
    }

    @Test
    public void testWithNullSpreadsheetIdNumberToColorFails() {
        this.withFails(this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                null);
    }

    private void withFails(final Function<SpreadsheetId, DateTimeContext> spreadsheetIdDateTimeContext,
                           final Function<SpreadsheetId, DecimalNumberContext> spreadsheetIdDecimalFormatContext,
                           final Function<SpreadsheetId, BiFunction<ExpressionNodeName, List<Object>, Object>> spreadsheetIdFunctions,
                           final Function<SpreadsheetId, Function<String, Color>> spreadsheetIdNameToColor,
                           final Function<SpreadsheetId, Function<Integer, Color>> spreadsheetIdNumberToColor) {
        assertThrows(NullPointerException.class, () -> {
            MemorySpreadsheetContext.with(spreadsheetIdDateTimeContext,
                    spreadsheetIdDecimalFormatContext,
                    spreadsheetIdFunctions,
                    spreadsheetIdNameToColor,
                    spreadsheetIdNumberToColor);
        });
    }

    @Test
    public void testDateTimeContext() {
        assertNotEquals(null, this.createContext().dateTimeContext(this.spreadsheetId()));
    }

    @Test
    public void testDecimalNumberContext() {
        assertNotEquals(null, this.createContext().decimalNumberContext(this.spreadsheetId()));
    }

    @Test
    public void testFunctions() {
        assertNotEquals(null, this.createContext().functions(this.spreadsheetId()));
    }

    @Test
    public void testNameToColor() {
        assertNotEquals(null, this.createContext().nameToColor(this.spreadsheetId()));
    }

    @Test
    public void testNumberToColor() {
        assertNotEquals(null, this.createContext().numberToColor(this.spreadsheetId()));
    }

    @Test
    public void testStoreRepositoryUnknownSpreadsheetId() {
        final MemorySpreadsheetContext context = this.createContext();
        final SpreadsheetId id = SpreadsheetId.with(123);

        final StoreRepository repository = context.storeRepository(id);
        assertNotEquals(null, repository);

        this.countAndCheck(repository.cells(), 0);
        this.countAndCheck(repository.cellReferences(), 0);
        this.countAndCheck(repository.groups(), 0);
        this.countAndCheck(repository.labels(), 0);
        this.countAndCheck(repository.labelReferences(), 0);
        this.countAndCheck(repository.rangeToCells(), 0);
        this.countAndCheck(repository.rangeToConditionalFormattingRules(), 0);
        this.countAndCheck(repository.users(), 0);

        repository.cells().save(SpreadsheetCell.with(SpreadsheetExpressionReference.parseCellReference("A1"), SpreadsheetFormula.with("1+2")));
        this.countAndCheck(repository.cells(), 1);
    }

    @Test
    public void testStoreRepositoryDifferentSpreadsheetId() {
        final MemorySpreadsheetContext context = this.createContext();

        final SpreadsheetId id1 = SpreadsheetId.with(111);
        final StoreRepository repository1 = context.storeRepository(id1);
        assertNotEquals(null, repository1);

        final SpreadsheetId id2 = SpreadsheetId.with(222);
        final StoreRepository repository2 = context.storeRepository(id2);
        assertNotEquals(null, repository2);
    }

    @Test
    public void testStoreRepositorySameSpreadsheetId() {
        final MemorySpreadsheetContext context = this.createContext();

        final SpreadsheetId id1 = SpreadsheetId.with(111);
        final StoreRepository repository1 = context.storeRepository(id1);
        assertSame(repository1, context.storeRepository(id1));
    }

    private void countAndCheck(final Store<?, ?> store, final int count) {
        assertEquals(count, store.count(), () -> "" + store.all());
    }

    @Test
    public void testToString() {
        final MemorySpreadsheetContext context = this.createContext();
        context.storeRepository(SpreadsheetId.with(111));

        this.toStringAndCheck(context, "{111=[] {} [] [] {} {} {} {}}");
    }

    @Override
    public MemorySpreadsheetContext createContext() {
        return MemorySpreadsheetContext.with(this::spreadsheetIdDateTimeContext,
                this::spreadsheetIdDecimalNumberContext,
                this::spreadsheetIdFunctions,
                this::spreadsheetIdNameToColor,
                this::spreadsheetIdNumberToColor);
    }

    private DateTimeContext spreadsheetIdDateTimeContext(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return DateTimeContexts.fake();
    }

    private DecimalNumberContext spreadsheetIdDecimalNumberContext(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return DecimalNumberContexts.fake();
    }

    private BiFunction<ExpressionNodeName, List<Object>, Object> spreadsheetIdFunctions(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdFunctions;
    }

    private Object spreadsheetIdFunctions(final ExpressionNodeName functionName, final List<Object> parameters) {
        throw new UnsupportedOperationException(functionName + "(" + parameters + ")");
    }

    private Function<String, Color> spreadsheetIdNameToColor(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdNameToColor0;
    }

    private Color spreadsheetIdNameToColor0(final String colorName) {
        throw new UnsupportedOperationException("name to color " + colorName);
    }
    
    private Function<Integer, Color> spreadsheetIdNumberToColor(final SpreadsheetId spreadsheetId) {
        this.checkSpreadsheetId(spreadsheetId);

        return this::spreadsheetIdNumberToColor0;
    }

    private Color spreadsheetIdNumberToColor0(final Integer colorNumber) {
        throw new UnsupportedOperationException("number to color " + colorNumber);
    }

    private void checkSpreadsheetId(final SpreadsheetId spreadsheetId) {
        Objects.requireNonNull(spreadsheetId, "spreadsheetId");

        assertEquals(this.spreadsheetId(), spreadsheetId, "spreadsheetId");
    }

    private SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(0x123456);
    }

    @Override
    public Class<MemorySpreadsheetContext> type() {
        return MemorySpreadsheetContext.class;
    }
}
