package com.snakesandladders.service;

import com.snakesandladders.model.*;

import java.util.*;

public class GameService {
    private Board board;
    private Dice dice;
    private Queue<Player> playersQueue;
    private List<Player> winners;

    public GameService(int n, int x, DifficultyLevel difficulty) {
        this.board = new Board(n);
        this.dice = new Dice(1, 6);
        this.playersQueue = new LinkedList<>();
        this.winners = new ArrayList<>();

        for (int i = 1; i <= x; i++) {
            playersQueue.add(new Player("Player " + i, i));
        }

        initializeBoard(n, difficulty);
    }

    private void initializeBoard(int n, DifficultyLevel difficulty) {
        int maxPosition = n * n;
        Set<Integer> usedCells = new HashSet<>();
        usedCells.add(0);
        usedCells.add(maxPosition);

        Random random = new Random();

        // Place n ladders
        int laddersCount = 0;
        while (laddersCount < n) {
            int start = random.nextInt(maxPosition - 2) + 1; // 1 to maxPosition-1
            int end = random.nextInt(maxPosition - start) + start + 1; // start+1 to maxPosition

            if (!usedCells.contains(start) && !usedCells.contains(end)) {
                board.getLadders().put(start, new Ladder(start, end));
                usedCells.add(start);
                usedCells.add(end);
                laddersCount++;
            }
        }

        // Place n snakes
        int snakesCount = 0;
        while (snakesCount < n) {
            // In hard mode, we can optionally make snakes longer, but standard logic is fine
            int tail = random.nextInt(maxPosition - 2) + 1; // 1 to maxPosition-1
            int head = random.nextInt(maxPosition - tail) + tail + 1; // tail+1 to maxPosition

            if (!usedCells.contains(head) && !usedCells.contains(tail)) {
                board.getSnakes().put(head, new Snake(head, tail));
                usedCells.add(head);
                usedCells.add(tail);
                snakesCount++;
            }
        }
    }

    public void startGame() {
        System.out.println("Starting Snakes and Ladders Game!");
        System.out.println("Board Size: " + board.getSize() + "x" + board.getSize() + " (Target: " + board.getFinishPosition() + ")");
        System.out.println("Snakes count: " + board.getSnakes().size());
        System.out.println("Ladders count: " + board.getLadders().size());

        while (playersQueue.size() > 1) { // Continue till at least 2 players are still playing to win
            Player currentPlayer = playersQueue.poll();
            int currentPos = currentPlayer.getPosition();
            int diceValue = dice.roll();
            
            int newPos = currentPos + diceValue;

            if (newPos > board.getFinishPosition()) {
                System.out.println(currentPlayer.getName() + " rolled a " + diceValue + 
                    " but needs exactly " + (board.getFinishPosition() - currentPos) + 
                    " to win. Stays at " + currentPos);
                playersQueue.add(currentPlayer);
                continue;
            }

            System.out.print(currentPlayer.getName() + " rolled a " + diceValue + 
                " and moved from " + currentPos + " to " + newPos);

            // Check for ladders/snakes
            boolean jumped = true;
            while(jumped) {
                jumped = false;
                if (board.getLadders().containsKey(newPos)) {
                    int endPos = board.getLadders().get(newPos).getEnd();
                    System.out.print(" -> Took Ladder to " + endPos);
                    newPos = endPos;
                    jumped = true;
                } else if (board.getSnakes().containsKey(newPos)) {
                    int tailPos = board.getSnakes().get(newPos).getTail();
                    System.out.print(" -> Bitten by Snake! Down to " + tailPos);
                    newPos = tailPos;
                    jumped = true;
                }
            }
            System.out.println(".");

            currentPlayer.setPosition(newPos);

            if (newPos == board.getFinishPosition()) {
                System.out.println("🎉 " + currentPlayer.getName() + " wins!");
                winners.add(currentPlayer);
            } else {
                playersQueue.add(currentPlayer);
            }
        }

        if (playersQueue.size() == 1) {
            System.out.println("Game Over! " + playersQueue.poll().getName() + " lost.");
        }
    }
}
