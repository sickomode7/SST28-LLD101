package com.snakesandladders;

import com.snakesandladders.model.DifficultyLevel;
import com.snakesandladders.service.GameService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter size of the board (n for nxn): ");
        int n = scanner.nextInt();
        
        System.out.print("Enter number of players (x): ");
        int x = scanner.nextInt();
        
        System.out.print("Enter difficulty level (easy/hard): ");
        String diffStr = scanner.next();
        DifficultyLevel difficulty = DifficultyLevel.EASY;
        if (diffStr.equalsIgnoreCase("hard")) {
            difficulty = DifficultyLevel.HARD;
        }

        GameService gameService = new GameService(n, x, difficulty);
        gameService.startGame();
    }
}
