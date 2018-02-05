# EleFile
Elementary File Writer and Reader for Java

For example, use in an Android app to export and import simple records.

## Usage

```Java
import elefile.EleFile;
```

### To export some records

```Java
        EleFile file = new EleFile("sdcard/Pointy_Arrow_Export.txt");

        file.add("name", "Tower Bridge");
        file.add("lat", "51.505541");
        file.add("lon", "-0.075339");
        file.add("geohexa", "hsza qu88");
        file.newRecord();

        file.add("name", "London Bridge");
        ...
        
        file.writeFile();
```

### To import some records

```Java
        EleFile file = new EleFile("sdcard/Pointy_Arrow_Export.txt");
        file.readFile();

        while (file.nextRecord()) {
            System.out.println( file.get("name") );
            System.out.println( file.getDouble("lat") );
            System.out.println( file.getDouble("lon") );
            System.out.println( file.get("geohexa") );
        }
```

## Notes

Only suitable for small amounts of data - the entire file is written and read in one go.

You can add more than one field with the same name in a record - but only the first occurence
will be looked at when read back.

If you try to get a field that does not exist, you will get an empty string - or zero.

You cannot include newlines in the data values (you can but it won't work).

Finally, field names can only contain regex "word" characters (a-zA-Z0-9_). Anything else will be
removed.