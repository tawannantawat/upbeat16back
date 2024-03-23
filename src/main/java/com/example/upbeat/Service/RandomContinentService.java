package com.example.upbeat.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import com.example.upbeat.Model.OwnCoordinate;
import com.example.upbeat.Model.Player;

@Service
public class RandomContinentService {
    public int[] generateRandomCoordinates() {
        Random rand = new Random();
        int x1 = rand.nextInt(18);
        int y1 = rand.nextInt(9);

        int[] coordinates = new int[]{x1, y1};
        return coordinates;
    }

    public List<OwnCoordinate> getOwnRegion(Player player) {
        return new ArrayList<>(player.getMyCoordinates());
    }
}