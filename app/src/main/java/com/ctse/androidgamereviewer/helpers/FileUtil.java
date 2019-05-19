/*
 * CTSE Android Project - Game Reviewer
 *
 * IT16037434 Karunaratne D. C.
 * IT15146366 Hettiarachchi H. A. I. S.
 *
 * This java file is not an original creation.
 * Reference: https://github.com/zetbaitsu
 *
 * File: FileUtil.java
 */
package com.ctse.androidgamereviewer.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This code is not our work and was taken from the github repository given below.
 * It is a Helper class with methods for file I/O
 *
 * Created on: June 18, 2016
 * Author: zetbaitsu
 * GitHub: https://github.com/zetbaitsu/Compressor/blob/master/app/src/main/java/id/zelory/compressor/sample/FileUtil.java
 */
public class FileUtil {
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private FileUtil() {

    }

    /**
     *
     * Created on: June 18, 2016
     * Author: zetbaitsu
     * GitHub: https://github.com/zetbaitsu/Compressor/blob/master/app/src/main/java/id/zelory/compressor/sample/FileUtil.java
     */
    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    /**
     * Created on: June 18, 2016
     * Author: zetbaitsu
     * GitHub: https://github.com/zetbaitsu/Compressor/blob/master/app/src/main/java/id/zelory/compressor/sample/FileUtil.java
     */
    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    /**
     * Created on: June 18, 2016
     * Author: zetbaitsu
     * GitHub: https://github.com/zetbaitsu/Compressor/blob/master/app/src/main/java/id/zelory/compressor/sample/FileUtil.java
     */
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Created on: June 18, 2016
     * Author: zetbaitsu
     * GitHub: https://github.com/zetbaitsu/Compressor/blob/master/app/src/main/java/id/zelory/compressor/sample/FileUtil.java
     */
    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    /**
     * Created on: June 18, 2016
     * Author: zetbaitsu
     * GitHub: https://github.com/zetbaitsu/Compressor/blob/master/app/src/main/java/id/zelory/compressor/sample/FileUtil.java
     */
    private static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}