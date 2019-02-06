package walkingkooka.spreadsheet.style;

import org.junit.jupiter.api.Test;
import walkingkooka.naming.NameTesting;
import walkingkooka.test.ClassTestCase;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class FontFamilyNameTest extends ClassTestCase<FontFamilyName>
        implements NameTesting<FontFamilyName, FontFamilyName>,
        HasJsonNodeTesting<FontFamilyName> {

    private final static String TEXT = "Times New Roman";

    @Test
    public void testToJsonNode() {
        this.toJsonNodeAndCheck(this.createComparable(), JsonNode.string(TEXT));
    }

    @Override
    public FontFamilyName createName(final String name) {
        return FontFamilyName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return TEXT;
    }

    @Override
    public String differentNameText() {
        return "Different";
    }

    @Override
    public String nameTextLess() {
        return "Antiqua";
    }

    @Override
    public Class<FontFamilyName> type() {
        return FontFamilyName.class;
    }

    @Override
    public MemberVisibility typeVisibility() {
        return MemberVisibility.PUBLIC;
    }
}
