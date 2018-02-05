package elefile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EleFileTest {

    private EleFile mSubject;

    @Before
    public void setUp() throws Exception {
        mSubject = new EleFile();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void canRunNullTest() {
    }

    @Test
    public void canCreateNewEleFileWithName() {
        mSubject = new EleFile("TestFile.txt");
        assertThat(mSubject._getFilename(), is("TestFile.txt") );
    }

    @Test
    public void nullFileNameUsesDefaultFileName() {
        mSubject = new EleFile("");
        assertThat(mSubject._getFilename(), is("EleFile.txt") );
    }

    @Test
    public void canAddAFieldAndStringValueToFile() {
        mSubject.add("Name", "Value");
        assertThat(mSubject._getField("Name"), is("Value") );
    }

    @Test
    public void canAddTwoFieldsAndStringValuesToFile() {
        mSubject.add("Name1", "Value1");
        mSubject.add("Name2", "Value2");
        assertThat(mSubject._getField("Name1"), is("Value1") );
        assertThat(mSubject._getField("Name2"), is("Value2") );
    }

    @Test
    public void canAddEmptyStringValueToFile() {
        mSubject.add("Name1", "Value1");
        mSubject.add("Name2", "");
        mSubject.add("Name3", "Value3");
        assertThat(mSubject._getField("Name2"), is("") );
    }

    @Test
    public void canAddNewRecordToFile() {
        mSubject.add("Name1", "Value1.1");
        mSubject.newRecord();
        mSubject.add("Name1", "Value1.2");
        assertThat(mSubject._getWriteRecordCount(), is(2) );
    }

    @Test
    public void spacesAreRemovedFromFieldName() {
        mSubject.add(" Na me1 ", "Value1");
        assertThat(mSubject._getField("Name1"), is("Value1") );
    }

    @Test
    public void nonWordCharactersRemovedFromFieldName() {
        mSubject.add("Na-me_1", "Value1");
        assertThat(mSubject._getField("Name_1"), is("Value1") );
    }

    @Test
    public void nullNameFieldDefaultsToNullName() {
        mSubject.add(" !- ", "Value1");
        assertThat(mSubject._getField("NullName"), is("Value1") );
    }

    @Test
    public void emptyRecordsDoNotCount() {
        mSubject.newRecord();
        mSubject.add("Name1", "Value1.1");
        mSubject.add("Name2", "Value2.1");
        mSubject.newRecord();
        mSubject.newRecord();
        mSubject.add("Name1", "Value1.2");
        mSubject.newRecord();
        assertThat(mSubject._getWriteRecordCount(), is(2) );
    }

    @Test
    public void canCovertRecordArrayToString() {
        mSubject.add("Name1", "Value1.1");
        mSubject.add("Name2", "Value2.1");
        mSubject.newRecord();
        mSubject.add("Name1", "Value1.2");
        String fileStr = mSubject.getFileAsString();
        assertThat(fileStr, containsString("Name1[[[Value1.1]]]") );
        assertThat(fileStr, containsString("Name2[[[Value2.1]]]") );
        assertThat(fileStr, containsString("Name1[[[Value1.2]]]") );
        assertThat(fileStr, containsString("\n") );
    }

    @Test
    public void canReadFieldsFromStringFile() {
        mSubject.add("Name1", "Value1.1");
        mSubject.getFileAsString();
        mSubject.nextRecord();
        assertThat(mSubject.get("Name1"), is("Value1.1"));

        mSubject.add("Name2", "Value2.1");
        mSubject.getFileAsString();
        mSubject.nextRecord();
        assertThat(mSubject.get("Name2"), is("Value2.1"));
    }

    @Test
    public void canReadFieldsFromStringFileWithMultipleRecords() {
        mSubject.add("Name1", "Value1.1");
        mSubject.newRecord();
        mSubject.add("Name1", "Value2.1");
        mSubject.getFileAsString();
        mSubject.nextRecord();
        assertThat(mSubject.get("Name1"), is("Value1.1"));
        mSubject.nextRecord();
        assertThat(mSubject.get("Name1"), is("Value2.1"));
    }

    @Test
    public void nextRecordReturnsFalseWhenThereAreNoMoreRecords() {
        assertThat(mSubject.nextRecord(), is(false));

        mSubject.add("Name1", "Value1.1");
        mSubject.newRecord();
        mSubject.add("Name1", "Value2.1");
        mSubject.getFileAsString();
        assertThat(mSubject.nextRecord(), is(true));
        assertThat(mSubject.nextRecord(), is(true));
        assertThat(mSubject.nextRecord(), is(false));
    }

    @Test
    public void getFieldReturnsEmptyStringWhenFieldDoesNotExistAtRecord() {
        mSubject.add("Name1", "Value1.1");
        mSubject.getFileAsString();
        mSubject.nextRecord();
        assertThat(mSubject.get("Name10"), is(""));
        assertThat(mSubject.get("aName1"), is(""));
    }

    @Test
    public void getFieldReturnsEmptyStringWhenCursorBeforeFirstRecord() {
        mSubject.add("Name1", "Value1.1");
        mSubject.getFileAsString();
        assertThat(mSubject.get("Name1"), is(""));
    }

    @Test
    public void nextRecordWillNotMoveCursorPastEndOfFile() {
        mSubject.add("Name1", "Value1.1");
        mSubject.getFileAsString();
        mSubject.nextRecord();
        mSubject.nextRecord();
        mSubject.nextRecord();
        mSubject.nextRecord();
        assertThat(mSubject.get("Name1"), is("Value1.1"));
    }

    @Test
    public void resetCursorSetsItBackToMinusOne() {
        mSubject.add("Name1", "Value1.1");
        mSubject.newRecord();
        mSubject.add("Name1", "Value1.2");
        mSubject.getFileAsString();
        mSubject.nextRecord();
        assertThat(mSubject.get("Name1"), is("Value1.1"));
        mSubject.nextRecord();
        assertThat(mSubject.get("Name1"), is("Value1.2"));
        mSubject.resetCursor();
        mSubject.nextRecord();
        assertThat(mSubject.get("Name1"), is("Value1.1"));
    }

    @Test
    public void linesInFileWithoutFieldsAreDiscarded() {
        String text = "abc\n\nName1[[[Value1.1]]] Name2[[[Value2.1]]]\nComment\n\n\n";
        mSubject.setFileText(text);
        assertThat(mSubject._getReadRecordCount(), is(1));
        mSubject.setFileText(text + text);
        assertThat(mSubject._getReadRecordCount(), is(2));
        mSubject.setFileText(text + text + " Hi There   Name3[[[3]]]  ");
        assertThat(mSubject._getReadRecordCount(), is(3));
    }

    @Test
    public void linesWithFieldsAreKept() {
        String text = "Name1[[[Value1.1]]] ";
        mSubject.setFileText(text);
        assertThat(mSubject._getReadRecordCount(), is(1));
    }

    @Test
    public void canGetDouble() {
        String text = "myDouble[[[1.1]]] ";
        mSubject.setFileText(text);
        mSubject.nextRecord();
        assertThat(mSubject.getDouble("myDouble"), is(1.1D));
    }

    @Test
    public void whenCannotParseDoubleZeroReturned() {
        assertThat(mSubject.getDouble("myDouble"), is(0D));
    }

}
