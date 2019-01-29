package walkingkooka.spreadsheet.security;

import org.junit.Test;
import walkingkooka.naming.NameTestCase;
import walkingkooka.naming.PropertiesPath;
import walkingkooka.text.CaseSensitivity;

import static org.junit.Assert.assertEquals;

final public class GroupNameTest extends NameTestCase<GroupName, GroupName> {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyStringFails() {
        GroupName.with("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateContainsSeparatorFails() {
        GroupName.with("xyz" + PropertiesPath.SEPARATOR.string());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithInvalidInitialFails() {
        GroupName.with("1abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithInvalidPartFails() {
        GroupName.with("abc$def");
    }

    @Test
    public void testWith() {
        this.createNameAndCheck("Abc-123");
    }

    @Test
    public void testWith2() {
        this.createNameAndCheck("ZZZ1");
    }

    @Test
    public void testWith3() {
        this.createNameAndCheck("A123Hello");
    }

    @Test
    public void testWith4() {
        this.createNameAndCheck("A1B2C2");
    }

    @Test
    public void testWithMissingRow() {
        this.createNameAndCheck("A");
    }

    @Test
    public void testWithMissingRow2() {
        this.createNameAndCheck("ABC");
    }

    @Test
    public void testWithEnormousColumn() {
        this.createNameAndCheck("ABCDEF1");
    }

    @Test
    public void testWithEnormousColumn2() {
        this.createNameAndCheck("ABCDEF");
    }

    @Test
    public void testToString() {
        assertEquals("ABC-123", this.createName("ABC-123").toString());
    }

    @Override
    protected GroupName createName(final String name) {
        return GroupName.with(name);
    }

    @Override
    protected CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    protected String nameText() {
        return "Group123";
    }

    @Override
    protected String differentNameText() {
        return "Different";
    }

    @Override
    protected String nameTextLess() {
        return "Abc-group";
    }

    @Override
    protected Class<GroupName> type() {
        return GroupName.class;
    }
}
