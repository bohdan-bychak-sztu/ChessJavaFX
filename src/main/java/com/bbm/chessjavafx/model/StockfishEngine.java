package com.bbm.chessjavafx.model;

import java.io.*;
import java.util.concurrent.*;

public class StockfishEngine {
    private Process engineProcess;
    private BufferedReader reader;
    private BufferedWriter writer;

    private final BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();

    private Thread outputReaderThread;

    public void startEngine(String pathToEngine) throws IOException {
        startEngine(pathToEngine, 10);
    }

    public void startEngine(String pathToEngine, int skillLevel) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(pathToEngine);
        engineProcess = builder.start();
        reader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));

        outputReaderThread = new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    outputQueue.offer(line);
                }
            } catch (IOException e) {
            }
        });
        outputReaderThread.setDaemon(true);
        outputReaderThread.start();

        isReady();
        setSkillLevel(skillLevel);
    }

    public boolean isReady() {
        sendCommand("uci");
        return waitFor("uciok", 2000);
    }

    public void sendCommand(String command) {
        try {
            writer.write(command + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSkillLevel(int level) {
        if (level < 0) level = 0;
        if (level > 20) level = 20;
        sendCommand("setoption name Skill Level value " + level);
    }

    public boolean waitFor(String expected, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        try {
            while (System.currentTimeMillis() < deadline) {
                String line = outputQueue.poll(deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                if (line == null) return false; // Таймаут
                if (line.contains(expected)) return true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    public String getBestMove(String fen, long timeoutMs) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime 100");

        long deadline = System.currentTimeMillis() + timeoutMs;
        try {
            while (System.currentTimeMillis() < deadline) {
                String line = outputQueue.poll(deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                if (line == null) return null;
                if (line.startsWith("bestmove")) {
                    return line.split(" ")[1];
                }
                System.out.println(line);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public void stop() {
        sendCommand("quit");
        try {
            if (outputReaderThread != null) {
                outputReaderThread.interrupt();
                outputReaderThread.join(1000);
            }
            reader.close();
            writer.close();
            engineProcess.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
