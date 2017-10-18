package com.ef;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by khaled.mohamed on 10/12/2017.
 */
public class Parser {
    private static String FILE_NAME_READ = "access.log";
    private static String FILE_NAME_WRITE = "resultLog.log";

    public static void main(String[] args) {
        Date startDate = null;
        Date endDate = null;
        int threshold = 0;
        BufferedWriter writer = null;

        /**
         * check if args comes from command line with file name or not
         * and check the date hourly or daily
         * and get start and end date.
         */
        if (args.length == 3) {
            String date = args[0].split("=")[args[0].split("=").length - 1];

            String duration = args[1].split("=")[args[1].split("=").length - 1];
            threshold = Integer.parseInt(args[2].split("=")[args[2].split("=").length - 1]);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
            try {
                startDate = dateFormat.parse(date);
                if (duration.equals("hourly")) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(startDate);
                    c.add(Calendar.HOUR, 1); // number of Hours to add
                    endDate = c.getTime(); // dt is now the new
                } else if (duration.equals("daily")) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(startDate);
                    c.add(Calendar.DATE, 1); // number of days to add
                    endDate = c.getTime(); // dt is now the new
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (args.length == 4) {
            FILE_NAME_READ = args[0].split("=")[args[0].split("=").length - 1];
            String date = args[1].split("=")[args[1].split("=").length - 1];
            String duration = args[2].split("=")[args[2].split("=").length - 1];
            threshold = Integer.parseInt(args[3].split("=")[args[3].split("=").length - 1]);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
            try {
                startDate = dateFormat.parse(date);
                if (duration.equals("hourly")) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(startDate);
                    c.add(Calendar.HOUR, 1); // number of Hours to add
                    endDate = c.getTime(); // dt is now the new
                } else if (duration.equals("daily")) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(startDate);
                    c.add(Calendar.DATE, 1); // number of days to add
                    endDate = c.getTime(); // dt is now the new
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        try {

            /**
             * make connection with the dateBase
             * and load the file into table
             */
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/log", "root", "root");

            //here log is database name, root is username and "" is password
            Statement statement = con.createStatement();
            statement.executeQuery("LOAD DATA LOCAL INFILE '" + FILE_NAME_READ + "' INTO TABLE logfile" +
                    " FIELDS TERMINATED BY '|'" +
                    " (datelog, ip, request, STATUS, useragent)");

            ResultSet logResult = statement.executeQuery("select logfile.* , count(*) as requests from logfile" +
                    " where datelog >= '" + new Timestamp(startDate.getTime()) + "'" +
                    " And datelog <= '" + new Timestamp(endDate.getTime()) + "' group by ip" +
                    " having requests >= " + threshold + "");

            /**
             * write result of query into file
             */
            writer = new BufferedWriter(new FileWriter(FILE_NAME_WRITE));

            while (logResult.next()) {
                writer.write(logResult.getTimestamp("datelog") + "|" +
                        logResult.getString("ip") + "|" +
                        logResult.getString("request") + "|" +
                        logResult.getInt("status") + "|" +
                        logResult.getString("useragent") +
                        "\n");
                System.out.println(logResult.getString("ip") + " has " + threshold + " or more requests between "
                        + new Timestamp(startDate.getTime()) + " and " + new Timestamp(endDate.getTime()));
            }

            if (writer != null)
                writer.close();

            /**
             *load result file in db
             */
            statement.executeQuery("LOAD DATA LOCAL INFILE 'resultLog.log' INTO TABLE resultlog" +
                    " FIELDS TERMINATED BY '|'" +
                    " (datelog, ip, request, STATUS, useragent)");
            // close connection
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

