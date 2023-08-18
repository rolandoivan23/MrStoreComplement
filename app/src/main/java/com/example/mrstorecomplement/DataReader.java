package com.example.mrstorecomplement;

import android.content.res.AssetManager;

import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

import java.io.InputStream;
import java.nio.charset.Charset;

import java.io.IOException;
import java.text.ParseException;

public class DataReader {

    public void readDBF(InputStream productsStream) throws IOException, ParseException {
        Charset stringCharset = Charset.forName("Cp866");

        //InputStream dbf = getClass().getClassLoader().getResourceAsStream("PRODUCTO.dbf");
        InputStream dbf = productsStream;
        DbfRecord rec;
        try (DbfReader reader = new DbfReader(dbf)) {
            DbfMetadata meta = reader.getMetadata();

            System.out.println("Read DBF Metadata: " + meta);
            while ((rec = reader.read()) != null) {
                rec.setStringCharset(stringCharset);
                System.out.println("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
            }
        }
    }
}
