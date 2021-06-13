package log;

import configuration.Configuration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum LogEngine {
    instance;

    private BufferedWriter bufferedWriter;

    public void init(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public String getCurrentDate() {
        Date date = new Date();
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.S");
        return simpleDateFormat.format(date);
    }

    public void write(String text) {
        try {
            if (Configuration.instance.isDebug) {
                System.out.println(text);
            }
            if (Configuration.instance.loglevel) {
                bufferedWriter.write(getCurrentDate() + " : " + text + "\n");
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}