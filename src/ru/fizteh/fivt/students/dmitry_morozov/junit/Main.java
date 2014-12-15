package ru.fizteh.fivt.students.dmitry_morozov.junit;

import java.util.Scanner;

import ru.fizteh.fivt.students.dmitry_morozov.junit.interpreter.DBInterpreter;
import ru.fizteh.fivt.students.dmitry_morozov.junit.interpreter.HandlerReturn;
import ru.fizteh.fivt.students.dmitry_morozov.junit.interpreter.HandlerReturnResult;

public class Main {

    public static HandlerReturn commandSplitting(String command, DBInterpreter inter) { 
        String[] firstSplitted = command.split(" ");
        String[] toGive = new String[firstSplitted.length];
        int j = 0;
        for (int i = 0; i < firstSplitted.length; i++) {
            if (firstSplitted[i].length() > 0) {
                toGive[j] = firstSplitted[i];
                j++;
            }
        }
        if (0 == j) {
            return new HandlerReturn(HandlerReturnResult.SUCCESS, "");
        }
        return inter.handle(toGive, 0, j);
    }

    public static void batchMode(String[] args, DBInterpreter inter) {
        String currentLine = "";
        for (int i = 0; i < args.length; i++) {
            currentLine += args[i] + " ";
        }
        String[] commands = currentLine.split(";");
        try {
            for (int i = 0; i < commands.length; i++) {
                if (commands[i].length() > 0) {
                    HandlerReturn ret = commandSplitting(commands[i], inter);
                    switch (ret.getVal()) {
                    case SUCCESS:
                        System.out.print(ret.getMessage());
                        break;
                    case NO_SUCH_COMMAND:
                        System.out
                                .print("No such command: " + ret.getMessage());
                        break;
                    case NOT_ENOUGH_PARAMETRES:
                        System.out.println("Not enough parametres for command "
                                + ret.getMessage());
                        break;
                    case ERROR:
                        System.out.print("Error occured " + ret.getMessage());
                        break;
                    case TABLE_NOT_CHOSEN:
                        System.out.println("Table not chosen");
                        break;
                    case EXIT:
                        System.out.print(ret.getMessage());
                        i = commands.length;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            inter.emeregencyExit();
            System.exit(1);
        }
        inter.handleExit();
        System.exit(0);
    }

    public static void main(String[] args) {
        DBInterpreter inter = new DBInterpreter(System.getProperty("db.file"));
        if (args.length > 0) {
            batchMode(args, inter);
        }
        Scanner in = new Scanner(System.in);

        boolean contFlag = true;
        try {
            while (contFlag) {
                System.out.print(" $ ");
                String currentLine = in.nextLine();
                String[] commands = currentLine.split(";");
                for (int i = 0; i < commands.length; i++) {
                    if (commands[i].length() > 0) {
                        HandlerReturn ret = commandSplitting(commands[i], inter);
                        switch (ret.getVal()) {
                        case SUCCESS:
                            System.out.print(ret.getMessage());
                            break;
                        case NO_SUCH_COMMAND:
                            System.out.print("No such command: "
                                    + ret.getMessage());
                            break;
                        case NOT_ENOUGH_PARAMETRES:
                            System.out
                                    .println("Not enough parametres for command "
                                            + ret.getMessage());
                            break;
                        case ERROR:
                            System.out.print("Error occured "
                                    + ret.getMessage());
                            break;
                        case TABLE_NOT_CHOSEN:
                            System.out.println("Table not chosen");
                            break;
                        case EXIT:
                            System.out.print(ret.getMessage());
                            contFlag = false;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            inter.emeregencyExit();
        } finally {
            in.close();
        }

    }

}
