package com.example.upbeat.Model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int[] continent;
    private int money = 100;
    private List<OwnCoordinate> myCoordinates = new ArrayList<>();

    public Player() {
    }

    public Player(String name, int[] continent) {
        this.name = name;
        this.continent = continent;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getContinent() {
        return continent;
    }

    public void setContinent(int[] continent) {
        this.continent = continent;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public List<OwnCoordinate> getMyCoordinates() {
        return myCoordinates;
    }

    public void setMyCoordinates(List<OwnCoordinate> myCoordinates) {
        this.myCoordinates = myCoordinates;
    }

    public List<int[]> findAdjacentCoordinates() {
        List<int[]> adjacentCoordinates = new ArrayList<>();

        for (OwnCoordinate ownCoordinate : myCoordinates) {
            int x = ownCoordinate.getCoordinates()[0];
            int y = ownCoordinate.getCoordinates()[1];

            if (x % 2 == 0 && y % 2 == 0) {
                int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, -1}, {1, 0}, {1, 1}};
                addValidAdjacentCoordinates(adjacentCoordinates, x, y, directions);
            } else if (x % 2 == 0 && y % 2 != 0) {
                int[][] directions = {{0, -1}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}, {1, 0}};
                addValidAdjacentCoordinates(adjacentCoordinates, x, y, directions);
            } else if (x % 2 != 0 && y % 2 == 0) {
                int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, -1}, {1, 0}, {1, 1}};
                addValidAdjacentCoordinates(adjacentCoordinates, x, y, directions);
            } else if (x % 2 != 0 && y % 2 != 0) {
                int[][] directions = {{0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, 0}, {-1, 0}};
                addValidAdjacentCoordinates(adjacentCoordinates, x, y, directions);
            }
        }
        return adjacentCoordinates;
    }

    private void addValidAdjacentCoordinates(List<int[]> adjacentCoordinates, int x, int y, int[][] directions) {
        for (int[] direction : directions) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            if (newX >= 0 && newX < 18 && newY >= 0 && newY < 9) {
                adjacentCoordinates.add(new int[]{newX, newY});
            }
        }
    }
}