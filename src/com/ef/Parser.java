package com.ef;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by khaled.mohamed on 10/12/2017.
 */
public class Parser {
    private static final String FILE_NAME_READ = "access.log.txt";
    private static final String FILE_NAME_WRITE = "log.txt";


    public static void main(String[] args) {

        args = new String[]{"--startDate=2017-01-01.13:00:00", "--duration=hourly", "--threshold=100"};

        if (args.length == 3) {
            String date = args[0].split("=")[args[0].split("=").length - 1];
            String duration = args[1].split("=")[args[1].split("=").length - 1];
            int threshold = Integer.parseInt(args[2].split("=")[args[2].split("=").length - 1]);

            System.out.println(date + "\n" + duration + "\n" + threshold);
            findStartAndEndDate(date, duration);
        }

        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {

            reader = new BufferedReader(new FileReader(FILE_NAME_READ));
            writer = new BufferedWriter(new FileWriter(FILE_NAME_WRITE));

            readAndWriteLogFile(reader, writer);


        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (reader != null)
                    reader.close();

                if (writer != null)
                    writer.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    private static void findStartAndEndDate(String date, String duration) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
        try {
            Date startDate = dateFormat.parse(date);
            if (duration.equals("hourly")) {
                Calendar c = Calendar.getInstance();
                c.setTime(startDate);
                c.add(Calendar.HOUR, 1);  // number of Hours to add
                Date endDate = c.getTime();  // dt is now the new date
                System.out.println(endDate);
            } else if (duration.equals("daily")) {
                Calendar c = Calendar.getInstance();
                c.setTime(startDate);
                c.add(Calendar.DATE, 1);  // number of days to add
                Date endDate = c.getTime();  // dt is now the new date
                System.out.println(endDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void readAndWriteLogFile(BufferedReader reader, BufferedWriter writer) {
        try {
            String sCurrentLine;
            while ((sCurrentLine = reader.readLine()) != null) {
//                System.out.println(sCurrentLine);
                String[] matches = sCurrentLine.split("\\|");
                for (String match : matches) {
                    System.out.print(match + "    ");
                    writer.write(match + "    ");

                }
                writer.write("\n");
                System.out.println();
            }
        } catch (IOException e) {

            e.printStackTrace();

        }
    }


}
