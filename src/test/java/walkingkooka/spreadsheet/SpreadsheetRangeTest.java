package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.cell.SpreadsheetCellStores;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.test.ClassTestCase;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.type.MemberVisibility;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRangeTest extends ClassTestCase<SpreadsheetRange>
        implements HashCodeEqualsDefinedTesting<SpreadsheetRange> {

    private final static int COLUMN1 = 10;
    private final static int ROW1 = 11;
    private final static int COLUMN2 = 20;
    private final static int ROW2 = 21;

    @Test
    public void testWithNullBeginFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetRange.with(null, this.cell());
        });
    }

    @Test
    public void testWithNullEndFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetRange.with(this.cell(), null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetCellReference begin =this.cell(1, 2);
        final SpreadsheetCellReference end =this.cell(3, 4);

        final SpreadsheetRange range = SpreadsheetRange.with(begin, end);
        assertSame(begin, range.begin(), "begin");
        assertSame(end, range.end(), "end");
        this.checkIsSingleCell(range, false);
    }

    @Test
    public void testWith2() {
        final int column1 = 99;
        final int row1 = 2;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row1, column1, row2, 99-3, 4-2);
        this.checkIsSingleCell(range, false);
    }

    @Test
    public void testWith3() {
        final int column1 = 1;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row2, column2, row1, 3-1, 99-4);
        this.checkIsSingleCell(range, false);
    }

    @Test
    public void testWith4() {
        final int column1 = 88;
        final int row1 = 99;
        final int column2 = 3;
        final int row2 = 4;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column2, row2, column1, row1, 88-3, 99-4);
        this.checkIsSingleCell(range, false);
    }

    // cell...........................................................

    @Test
    public void testCellNullFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetRange.cell(null);
        });
    }

    @Test
    public void testCell() {
        final int column = 88;
        final int row = 99;
        final SpreadsheetRange range = SpreadsheetRange.cell(this.cell(column, row));
        this.check(range, column, row, column + 1, row + 1, 1, 1);
        this.checkIsSingleCell(range, true);
    }

    // isSingleCell...........................................................

    @Test
    public void testIsSingleCell() {
        final int column1 = 88;
        final int row1 = 99;
        final int column2 = column1 + 1;
        final int row2 = row1 + 1;

        final SpreadsheetRange range = this.range(column1, row1, column2, row2);
        this.check(range, column1, row1, column2, row2, 1, 1);
        this.checkIsSingleCell(range, true);
    }

    // setBeginAndEnd.....................................................................................

    @Test
    public void testSetBeginAndEndWithNullBeginFails() {
        assertThrows(NullPointerException.class, () -> {
            this.range().setBeginAndEnd(null, this.end());
        });
    }

    @Test
    public void testSetBeginAndEndWithNullEndFails() {
        assertThrows(NullPointerException.class, () -> {
            this.range().setBeginAndEnd(this.begin(), null);
        });
    }

    @Test
    public void testSetBeginAndEndWithSame() {
        final SpreadsheetRange range = this.range();
        assertSame(range, range.setBeginAndEnd(this.begin(), this.end()));
    }

    @Test
    public void testSetBeginAndEndWithSame2() {
        final SpreadsheetRange range = this.range();
        assertSame(range, range.setBeginAndEnd(this.end(), this.begin()));
    }

    @Test
    public void testSetBeginAndEndWithDifferent() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetCellReference differentBegin = this.cell(1, 2);
        final SpreadsheetRange different = range.setBeginAndEnd(differentBegin, this.end());
        this.check(different, 1, 2, COLUMN2, ROW2);
    }

    @Test
    public void testSetBeginAndEndWithDifferent2() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setBeginAndEnd(this.end(), this.cell(1, 2));
        this.check(different, 1, 2, COLUMN2, ROW2);
    }

    @Test
    public void testSetBeginAndEndWithDifferent3() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setBeginAndEnd(this.begin(), this.cell(88, 99));
        this.check(different, COLUMN1, ROW1, 88, 99);
    }

    @Test
    public void testSetBeginAndEndWithDifferent4() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setBeginAndEnd(this.cell(88, 99), this.begin());
        this.check(different, COLUMN1, ROW1, 88, 99);
    }

    @Test
    public void testSetBeginAndEndWithDifferent5() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setBeginAndEnd(this.cell(1, 2), this.cell(88, 99));
        this.check(different, 1, 2, 88, 99);
    }

    @Test
    public void testSetBeginAndEndWithDifferent6() {
        final SpreadsheetRange range = this.range();
        final SpreadsheetRange different = range.setBeginAndEnd(this.cell(88, 99), this.cell(1, 2));
        this.check(different, 1, 2, 88, 99);
    }

    // stream.................................................................................................
    
    @Test
    public void testColumnStream() {
        final SpreadsheetRange range = this.range(5, 10, 8, 10);

        this.checkStream(range,
                range.columnStream(),
                this.column(5), this.column(6), this.column(7));
    }

    @Test
    public void testColumnStreamFilterAndMapAndCollect() {
        final SpreadsheetRange range = this.range(5, 10, 8, 10);
        this.checkStream(range,
                range.columnStream()
                        .map( c -> c.value())
                        .filter(c -> c >= 6),
                6, 7);
    }

    @Test
    public void testRowStream() {
        final SpreadsheetRange range = this.range(10, 5, 10, 8);

        this.checkStream(range,
                range.rowStream(),
                this.row(5), this.row(6), this.row(7));
    }

    @Test
    public void testRowStreamFilterAndMapAndCollect() {
        final SpreadsheetRange range = this.range(5, 10, 8, 20);
        this.checkStream(range,
                range.rowStream()
                        .map( r -> r.value())
                        .filter(r -> r < 13),
                10, 11, 12);
    }

    @Test
    public void testCellStream() {
        final SpreadsheetRange range = this.range(3, 7, 6, 11);

        this.checkStream(
                range,
                range.cellStream(),
                this.cell(3, 7), this.cell(4, 7), this.cell(5, 7),
                this.cell(3, 8), this.cell(4, 8), this.cell(5, 8),
                this.cell(3, 9), this.cell(4, 9), this.cell(5, 9),
                this.cell(3, 10), this.cell(4, 10), this.cell(5, 10));
    }

    @Test
    public void testCellStreamFilterAndMapAndCollect() {
        final SpreadsheetRange range = this.range(5, 10, 8, 20);
        this.checkStream(range,
                range.cellStream()
                        .filter(cell -> cell.column().value() == 5 && cell.row().value() < 13),
                this.cell(5, 10), this.cell(5, 11), this.cell(5, 12));
    }

    private <T> void checkStream(final SpreadsheetRange range, final Stream<?> stream, final Object...expected){
        final List<Object> actual = stream.collect(Collectors.toList());
        assertEquals(Lists.of(expected), actual, () -> range.toString());
    }

    // clear....................................................................................................

    @Test
    public void testClearWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            this.range().clear(null);
        });
    }

    @Test
    public void testClear() {
        final SpreadsheetCellStore store = SpreadsheetCellStores.basic();

        final SpreadsheetCell a = spreadsheetCell(1, 1);
        final SpreadsheetCell b = spreadsheetCell(2, 2);
        final SpreadsheetCell c = spreadsheetCell(3, 10);
        final SpreadsheetCell d = spreadsheetCell(4, 14);
        final SpreadsheetCell e = spreadsheetCell(5, 15);

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);
        store.save(e);

        this.range(2, 2, 4, 11).clear(store);

        assertEquals(3, store.count(), "store record count"); // a,d,e
        assertEquals(Optional.empty(), store.load(b.reference()));
        assertEquals(Optional.empty(), store.load(c.reference()));
    }

    // equals...............................................................................

    @Test
    public void testEqualsDifferentBegin() {
        this.checkNotEquals(this.range(9, ROW1, COLUMN2, ROW2));
    }

    @Test
    public void testEqualsDifferentEnd() {
        this.checkNotEquals(this.range(COLUMN1, ROW1, COLUMN2, 99));
    }

    // helpers .................................................................................

    @Override
    public SpreadsheetRange createObject() {
        return this.range(COLUMN1, ROW1, COLUMN2, ROW2);
    }

    private SpreadsheetCell spreadsheetCell(final int column, final int row) {
        return SpreadsheetCell.with(this.cell(column, row),
                SpreadsheetFormula.with(column + "+" + row),
                this.style(),
                this.format(),
                this.formatted());
    }

    private SpreadsheetCellStyle style() {
        return SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD));
    }

    private Optional<SpreadsheetCellFormat> format() {
        return SpreadsheetCell.NO_FORMAT;
    }

    private Optional<SpreadsheetFormattedCell> formatted() {
        return SpreadsheetCell.NO_FORMATTED_CELL;
    }

    // from...............................................................................................

    @Test
    public void testFromWithNullCellsFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetRange.from(null);
        });
    }

    @Test
    public void testFrom() {
        final SpreadsheetCellReference a = this.cell(111, 11);
        final SpreadsheetCellReference b = this.cell(112, 12);
        final SpreadsheetCellReference c = this.cell(113, 20);
        final SpreadsheetCellReference d = this.cell(114, 24);
        final SpreadsheetCellReference e = this.cell(115, 24);

        final SpreadsheetRange range = SpreadsheetRange.from(Lists.of(a, b, c, d, e));
        this.check(range,111, 11, 115+1, 24+1);
    }

    @Test
    public void testFrom2() {
        final SpreadsheetCellReference a = this.cell(111, 11);
        final SpreadsheetCellReference b = this.cell(112, 12);
        final SpreadsheetCellReference c = this.cell(113, 20);
        final SpreadsheetCellReference d = this.cell(114, 24);
        final SpreadsheetCellReference e = this.cell(115, 24);

        final SpreadsheetRange range = SpreadsheetRange.from(Lists.of(e, d, c, b, a));
        this.check(range,111, 11, 115+1, 24+1);
    }

    @Test
    public void testFrom3() {
        final SpreadsheetCellReference a = this.cell(111, 11);

        final SpreadsheetRange range = SpreadsheetRange.from(Lists.of(a));
        this.check(range,111, 11, 111+1, 11+1);
    }

    //helper.................................................................................................

    private SpreadsheetRange range() {
        return this.range(this.begin(), this.end());
    }

    private SpreadsheetCellReference begin() {
        return this.cell(COLUMN1, ROW1);
    }

    private SpreadsheetCellReference end() {
        return this.cell(COLUMN2, ROW2);
    }

    private SpreadsheetRange range(final int column1, final int row1, final int column2, final int row2) {
        return SpreadsheetRange.with(this.cell(column1, row1), this.cell(column2, row2));
    }

    private SpreadsheetRange range(final SpreadsheetCellReference begin, final SpreadsheetCellReference end) {
        return SpreadsheetRange.with(begin, end);
    }

    private SpreadsheetCellReference cell() {
        return this.cell(99, 88);
    }

    private SpreadsheetCellReference cell(final int column, final int row) {
        return this.column(column)
                .setRow(this.row(row));
    }
    
    private SpreadsheetColumnReference column(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column);
    }

    private SpreadsheetRowReference row(final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.row(row);
    }
    
    private void check(final SpreadsheetRange range,
                       final int column1,
                       final int row1,
                       final int column2,
                       final int row2) {
        this.checkBegin(range, column1, row1);
        this.checkEnd(range, column2, row2);
    }

    private void check(final SpreadsheetRange range,
                       final int column1,
                       final int row1,
                       final int column2,
                       final int row2,
                       final int width,
                       final int height) {
        this.check(range, column1, row1, column2, row2);
        this.checkWidth(range, width);
        this.checkHeight(range, height);
    }

    private void checkBegin(final SpreadsheetRange range, final int column, final int row) {
        this.checkBegin(range, this.cell(column, row));
    }

    private void checkBegin(final SpreadsheetRange range, final SpreadsheetCellReference begin) {
        assertEquals(begin, range.begin(), ()-> "range begin=" + range);
    }

    private void checkEnd(final SpreadsheetRange range, final int column, final int row) {
        this.checkEnd(range, this.cell(column, row));
    }

    private void checkEnd(final SpreadsheetRange range, final SpreadsheetCellReference end) {
        assertEquals(end, range.end(), ()-> "range end="+ range);
    }

    private void checkWidth(final SpreadsheetRange range, final int width) {
        assertEquals(width, range.width(), ()-> "range width="+ range);
    }

    private void checkHeight(final SpreadsheetRange range, final int height) {
        assertEquals(height, range.height(), ()-> "range height="+ range);
    }

    private void checkIsSingleCell(final SpreadsheetRange range, final boolean expected) {
        assertEquals(expected, range.isSingleCell(), ()-> "range=" + range + " isSingleCell");
    }

    @Override
    public Class<SpreadsheetRange> type() {
        return SpreadsheetRange.class;
    }

    @Override
    protected MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
