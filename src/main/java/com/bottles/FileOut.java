package com.bottles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileOut implements Output, AutoCloseable {
    private final BufferedWriter bw;

    public FileOut(String filename) throws IOException {
        this.bw = new BufferedWriter(new FileWriter(filename, true));
    }

    public void out(String str) {
        try {
            bw.write(str);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        bw.close();
    }
}

