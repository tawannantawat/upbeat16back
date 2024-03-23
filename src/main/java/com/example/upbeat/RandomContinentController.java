package com.example.upbeat;

import com.example.upbeat.Model.OwnCoordinate;
import com.example.upbeat.ModelRequest.AttackReiognRequest;
import com.example.upbeat.ModelRequest.CreatePlayerRequest;
import com.example.upbeat.Model.Player;
import com.example.upbeat.Service.RandomContinentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/RandomContinent")
public class RandomContinentController {

    private final RandomContinentService randomContinentService;

    private static Player player1 = new Player();
    private static Player player2 = new Player();

    @Autowired
    public RandomContinentController(RandomContinentService randomContinentService) {
        this.randomContinentService = randomContinentService;
    }

    @GetMapping("/Get")
    public ResponseEntity<Object> getPlayers() {
        return ResponseEntity.ok().body(new Object() {
            public Player player1 = RandomContinentController.player1;
            public Player player2 = RandomContinentController.player2;
        });
    }

    @GetMapping("/Getadjacen")
    public ResponseEntity<List<int[]>> getAdjacentRegion(@RequestParam String player) {
        List<int[]> listOfRegion;
        if (player.equals("Player 1")) {
            listOfRegion = player1.findAdjacentCoordinates();
        } else {
            listOfRegion = player2.findAdjacentCoordinates();
        }
        return ResponseEntity.ok().body(listOfRegion);
    }

    @PostMapping("/CreatePlayer")
    public ResponseEntity<Object> createPlayer(@Valid @RequestBody CreatePlayerRequest model) {
        player1 = new Player();
        player1.setName(model.getPlayer1());
        player1.setContinent(randomContinentService.generateRandomCoordinates());
        OwnCoordinate mainCity1 = new OwnCoordinate();
        mainCity1.setPower(1);
        mainCity1.setCoordinates(player1.getContinent());
        player1.getMyCoordinates().add(mainCity1);

        player2 = new Player();
        player2.setName(model.getPlayer2());
        player2.setContinent(randomContinentService.generateRandomCoordinates());
        OwnCoordinate mainCity2 = new OwnCoordinate();
        mainCity2.setPower(1);
        mainCity2.setCoordinates(player2.getContinent());
        player2.getMyCoordinates().add(mainCity2);

        return ResponseEntity.ok().body(new Object() {
            public Player player1 = RandomContinentController.player1;
            public Player player2 = RandomContinentController.player2;
        });
    }

    @PutMapping("/Player1Money")
    public ResponseEntity<Object> player1Money(@RequestParam int amount) {
        player1.setMoney(player1.getMoney() + amount);
        return ResponseEntity.ok().body(player1);
    }

    @PutMapping("/Player2Money")
    public ResponseEntity<Object> player2Money(@RequestParam int amount) {
        player2.setMoney(player2.getMoney() + amount);
        return ResponseEntity.ok().body(player2);
    }

    @PostMapping("/BuyRegion")
    public ResponseEntity<Object> buyRegion(@RequestParam String player, @RequestParam int[] coordinate, @RequestParam int budget) {
        Player currentPlayer;
        Player opponentPlayer;
        if (player.equals("Player 1")) {
            currentPlayer = player1;
            opponentPlayer = player2;
        } else {
            currentPlayer = player2;
            opponentPlayer = player1;
        }

        List<OwnCoordinate> regionList = randomContinentService.getOwnRegion(currentPlayer);
        if (budget < 1 || budget > currentPlayer.getMoney())
            return ResponseEntity.badRequest().body("Invalid budget.");

        if (regionList.stream().anyMatch(x -> x.getCoordinates()[0] == coordinate[0] && x.getCoordinates()[1] == coordinate[1]))
            return ResponseEntity.badRequest().body("You already own that region.");

        List<int[]> adjacentCoordinates = currentPlayer.findAdjacentCoordinates();
        if (adjacentCoordinates.stream().noneMatch(c -> c[0] == coordinate[0] && c[1] == coordinate[1]))
            return ResponseEntity.badRequest().body("You can only buy adjacent regions.");

        OwnCoordinate buyRegion = new OwnCoordinate();
        buyRegion.setPower(budget);
        buyRegion.setCoordinates(coordinate);
        currentPlayer.setMoney(currentPlayer.getMoney() - budget);
        currentPlayer.getMyCoordinates().add(buyRegion);

        opponentPlayer.getMyCoordinates().removeIf(x -> x.getCoordinates()[0] == coordinate[0] && x.getCoordinates()[1] == coordinate[1]);

        return ResponseEntity.ok().body(currentPlayer);
    }

    @PostMapping("/AttackRegion")
    public ResponseEntity<Object> attackRegion(@RequestParam String player, @Valid @RequestBody AttackReiognRequest model) {
        Player currentPlayer;
        Player opponentPlayer;
        if (player.equals("Player 1")) {
            currentPlayer = player1;
            opponentPlayer = player2;
        } else {
            currentPlayer = player2;
            opponentPlayer = player1;
        }

        List<OwnCoordinate> playerRegionList = randomContinentService.getOwnRegion(currentPlayer);
        List<OwnCoordinate> opponentRegionList = randomContinentService.getOwnRegion(opponentPlayer);
        List<int[]> currentPlayerAdjacentRegion = currentPlayer.findAdjacentCoordinates();

        if (playerRegionList.stream().noneMatch(x -> x.getCoordinates()[0] == model.getMyRegion()[0] && x.getCoordinates()[1] == model.getMyRegion()[1]))
            return ResponseEntity.badRequest().body("You can only use attack command on your own region.");
        if (opponentRegionList.stream().noneMatch(x -> x.getCoordinates()[0] == model.getAttackRegion()[0] && x.getCoordinates()[1] == model.getAttackRegion()[1]))
            return ResponseEntity.badRequest().body("You can only attack enemy region.");
        if (currentPlayerAdjacentRegion.stream().noneMatch(x -> x[0] == model.getAttackRegion()[0] && x[1] == model.getAttackRegion()[1]))
            return ResponseEntity.badRequest().body("You can only attack adjacent regions.");

        OwnCoordinate attackerRegion = playerRegionList.stream()
                .filter(x -> x.getCoordinates()[0] == model.getMyRegion()[0] && x.getCoordinates()[1] == model.getMyRegion()[1])
                .findFirst().orElse(null);
        OwnCoordinate defenderRegion = opponentRegionList.stream()
                .filter(x -> x.getCoordinates()[0] == model.getAttackRegion()[0] && x.getCoordinates()[1] == model.getAttackRegion()[1])
                .findFirst().orElse(null);

        if (attackerRegion.getPower() <= defenderRegion.getPower())
            return ResponseEntity.badRequest().body("You can only attack a region that has lower power.");

        currentPlayer.getMyCoordinates().add(defenderRegion);
        opponentPlayer.getMyCoordinates().remove(defenderRegion);

        return ResponseEntity.ok().body(currentPlayer.getMyCoordinates());
    }

    @PostMapping("/AddInterest")
    public ResponseEntity<Object> addInterest(@RequestParam String player) {
        Player currentPlayer;
        if (player.equals("Player 1")) {
            currentPlayer = player1;
        } else {
            currentPlayer = player2;
        }

        currentPlayer.setMoney(currentPlayer.getMoney() + (currentPlayer.getMoney() * 10) / 100);

        return ResponseEntity.ok().body(currentPlayer.getMoney());
    }
}