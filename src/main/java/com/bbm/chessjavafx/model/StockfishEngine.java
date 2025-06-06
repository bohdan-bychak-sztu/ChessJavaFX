package com.bbm.chessjavafx.model;

import java.io.*;

public class StockfishEngine {
    private Process engineProcess;
    private BufferedReader reader;
    private BufferedWriter writer;

    public boolean startEngine(String pathToEngine) {
        try {
            ProcessBuilder builder = new ProcessBuilder(pathToEngine);
            engineProcess = builder.start();
            reader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));

            return isReady();
        } catch (IOException ignored) { return false; }
    }

    public boolean isReady() {
        return sendCommand("uci") && waitFor("uciok");
    }

    public boolean sendCommand(String command) {
        try {
            writer.write(command + "\n");
            writer.flush();
            return true;
        } catch (IOException ignored) { return false; }
    }

    public boolean waitFor(String expected) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(expected)) {
                    return true;
                }
            }
        } catch (IOException ignored) { return false; }
        return false;
    }

    public String getBestMove(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime 100");

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    return line.split(" ")[1];
                }
            }
        } catch (IOException ignored) { }

        return null;
    }

    public void stop() {
        sendCommand("quit");
        try {
            reader.close();
            writer.close();
            engineProcess.destroy();
        } catch (IOException ignored) { }

    }
}
