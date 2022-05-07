package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.Race;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final CardService cardService;
    private final RaceService raceService;

    private final Map<UUID, Player> players = new HashMap<>();

    public Player createPlayer(String name, RaceName raceType) {
        Race race = raceService.get(raceType);
        Player newPlayer = Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .race(race)
                .resources(race.getStartingResources())
                .cards(new ArrayList<>(race.getStartingConverterCards()))
                .build();

        for (int i = 0; i < race.getStartingColonies(); i++) {
            Colony colony = cardService.drawColonyCard();
            newPlayer.getCards().add(colony.getId());
        }

        for (int i = 0; i < race.getStartingResearchTeams(); i++) {
            ResearchTeam researchTeam = cardService.drawResearchTeamCard();
            newPlayer.getCards().add(researchTeam.getId());
        }

        players.put(newPlayer.getId(), newPlayer);
        return newPlayer;
    }

    public void resetPlayers() {
        players.clear();
    }

    public Player get(UUID id) {
        return players.get(id);
    }

}
