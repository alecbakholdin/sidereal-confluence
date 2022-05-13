package com.bakholdin.siderealconfluence.data;

import com.bakholdin.siderealconfluence.model.BidTrackType;
import com.bakholdin.siderealconfluence.model.GameState;
import com.bakholdin.siderealconfluence.model.Phase;
import com.bakholdin.siderealconfluence.model.Player;
import com.bakholdin.siderealconfluence.model.RaceName;
import com.bakholdin.siderealconfluence.model.cards.Colony;
import com.bakholdin.siderealconfluence.model.cards.ResearchTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameStateService {
    private final CardService cardService;
    private final PlayerService playerService;
    private final ConfluenceService confluenceService;
    private final EconomyService economyService;
    private GameState gameState = null;

    public GameState getGameState() {
        if (gameState == null) {
            return startNewGame();
        }
        return gameState;
    }

    public GameState startNewGame() {
        gameState = new GameState();
        cardService.resetCards();
        playerService.resetPlayers();
        return gameState;
    }

    public void addPlayerToGame(Player player) {
        gameState.getPlayers().put(player.getId(), player);
    }

    public Player addNewPlayerToGame(String playerName, RaceName raceName) {
        if (gameIsInSession()) {
            throw new RuntimeException("Game is already in session");
        }
        Player player = playerService.createPlayer(playerName, raceName);
        getGameState().getPlayers().put(player.getId(), player);
        return player;
    }

    public boolean gameIsInSession() {
        return gameState.isGameStarted() && !gameState.isGameOver();
    }

    public GameState startGame() {
        GameState gameState = getGameState();
        int numPlayers = gameState.getPlayers().size();

        gameState.setTurn(1);
        gameState.setPhase(Phase.Trade);
        gameState.setGameStarted(true);
        gameState.setGameOver(false);

        gameState.setConfluenceList(confluenceService.getConfluenceCards(numPlayers));

        gameState.setColonyBidTrack(confluenceService.getBidTrack(numPlayers, BidTrackType.Colony));
        List<Colony> colonies = cardService.drawNColonies(numPlayers);
        gameState.setAvailableColonies(colonies.stream().map(Colony::getId).collect(Collectors.toList()));

        gameState.setResearchTeamBidTrack(confluenceService.getBidTrack(numPlayers, BidTrackType.ResearchTeam));
        List<ResearchTeam> researchTeams = cardService.drawNResearchTeams(numPlayers);
        gameState.setAvailableResearchTeams(researchTeams.stream().map(ResearchTeam::getId).collect(Collectors.toList()));

        return gameState;
    }

    public GameState advancePhase() {
        GameState gameState = getGameState();
        gameState.setPhase(getNextPhase(gameState.getPhase()));
        if (gameState.getPhase() == Phase.Trade) {
            advanceTurn(gameState);
        } else if (gameState.getPhase() == Phase.Confluence) {
            economyService.resolveEconomyStep();
        }
        return gameState;
    }

    private void advanceTurn(GameState gameState) {
        if (gameState.getTurn() == 6) {
            gameState.setGameOver(true);
        } else {
            gameState.setTurn(gameState.getTurn() + 1);
        }
    }

    private Phase getNextPhase(Phase phase) {
        switch (phase) {
            case Trade:
                return Phase.Economy;
            case Economy:
                return Phase.Confluence;
            case Confluence:
                return Phase.Trade;
            default:
                throw new IllegalArgumentException("Unknown phase: " + phase);
        }
    }
}
