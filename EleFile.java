package elefile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// (package private) methods beginning with underscore are for unit test purposes only

public class EleFile {

    private String mFilename;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mValues = new ArrayList<>();
    private final String SEPARATOR = " --- ";
    private String mFile;
    private ArrayList<String> mFileList = new ArrayList<>();
    private int mCursor = -1;

    EleFile () {
        mFilename = "EleFile.txt";
    }

    public EleFile(String filename) {
        this();
        if (filename.equals("")) {
            return;
        }

        mFilename = filename;
    }

    public void add(String name, String value) {
        name = name.replaceAll("[^\\w]", "");

        if (name.equals("")) {
            name = "NullName";
        }

        mNames.add(name);
        mValues.add(value);
    }

    public void newRecord() {
        if (mNames.isEmpty()) {
            return;
        }
        if ( isSeparator(mNames.get(mNames.size()-1)) ) {
            return;
        }
        mNames.add(SEPARATOR);
        mValues.add(SEPARATOR);
    }

    public void writeFile() {
        getFileAsString();

        File targetFile = new File(mFilename);

        if (!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(targetFile, false));
            buf.append(mFile);
            buf.newLine();
            buf.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile() {
        BufferedReader reader;
        String line;
        StringBuilder builder = new StringBuilder();

        try {
            reader = new BufferedReader(new FileReader(mFilename));
            while((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            reader.close();
            setFileText( builder.toString() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String name) {
        if (mCursor < 0) {
            return "";
        }

        String regex = "\\b" + name + "\\[\\[\\[(.*?)]]]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mFileList.get(mCursor));
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    public Double getDouble(String name) {
        Double d = 0D;
        try {
            d = Double.parseDouble(get(name));
        } catch ( NumberFormatException e ) {
            // return 0 if can't parse
        }
        return d;
    }

    public boolean nextRecord() {
        if (_getReadRecordCount() - 1 > mCursor) {
            mCursor += 1;
            return true;
        }
        return false;
    }

    String getFileAsString() {
        mFile = "";
        resetCursor();

        int index = 0;
        for (String name : mNames) {
            if (isSeparator(name)) {
                mFile += "\n";
            } else {
                mFile += name + "[[[" + mValues.get(index) + "]]] ";
            }
            index += 1;
        }

        parseFile();

        return mFile;
    }

    void resetCursor() {
        mCursor = -1;
    }

    void setFileText(String file) {
        mFile = file;
        parseFile();
    }

    private boolean isSeparator(String s) {
        return s.equals(SEPARATOR);
    }

    private void parseFile() {
        ArrayList<String> fileList = new ArrayList<>(Arrays.asList(mFile.split("\\n")));

        mFileList = new ArrayList<>();
        for (String line : fileList) {
            if (line.matches(".*?\\b\\w+[\\[]{3}.*?[]]{3}.*?")) { //Must find at least one field
                mFileList.add(line);
                //System.out.println("Kept     :" + line);
            } else {
                //System.out.println("Discarded:" + line);
            }
        }
        //System.out.println("mFileList size " + mFileList.size());
    }

    String _getFilename() {
        return mFilename;
    }

    String _getField(String name) {
        int index = mNames.lastIndexOf(name);
        return mValues.get(index);
    }

    int _getWriteRecordCount() {
        int count = 0;

        if (mNames.isEmpty()) {
            return count;
        }

        for (String name : mNames) {
            if (isSeparator(name)) {
                count += 1;
            }
        }

        // if last name is a separator then there are no further records
        if (isSeparator( mNames.get(mNames.size()-1)) ) {
            return count;
        }

        return count+1;
    }

    int _getReadRecordCount() {
        return mFileList.size();
    }


}
