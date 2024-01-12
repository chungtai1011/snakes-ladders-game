package com.game.request;

import com.game.model.Die;
import com.game.model.GameState;
import com.game.model.Player;
import com.game.service.SnakesAndLaddersMap;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class DieStreamingRequest implements StreamObserver<Die> {
    private static final Logger LOGGER = Logger.getLogger(DieStreamingRequest.class.getName());

    private final StreamObserver<GameState> gameStateStreamObserver;

    private Player client;
    private Player server;

    public DieStreamingRequest(Player client, Player server, StreamObserver<GameState> gameStateStreamObserver) {
        this.gameStateStreamObserver = gameStateStreamObserver;
        this.client = client;
        this.server = server;
    }

    @Override
    public void onNext(Die die) {
        this.client = this.getNewPlayerPosition(this.client, die.getValue());
        if (this.client.getPosition() != 100) {
            this.server = this.getNewPlayerPosition(this.server, ThreadLocalRandom.current().nextInt(1,7));
        }

        LOGGER.info("CLient Die: " + die.getValue());
        LOGGER.info(client.getName() + ":" + client.getPosition());
        LOGGER.info(server.getName() + ":" + server.getPosition());

        this.gameStateStreamObserver.onNext(this.getGameState());
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.severe(throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        this.gameStateStreamObserver.onCompleted();
        if (this.server.getPosition() == 100) {
            LOGGER.info(server.getName() + " win!");
        } else {
            LOGGER.info(client.getName() + " win!");
        }
        LOGGER.info("Completed!");
    }

    private GameState getGameState() {
        return GameState.newBuilder()
                .addPlayer(this.server)
                .addPlayer(this.client)
                .build();
    }
    private Player getNewPlayerPosition(Player player, int dieValue) {
        int position = player.getPosition() + dieValue;
        position = SnakesAndLaddersMap.getPosition(position);
        if (position <= 100){
            player = player.toBuilder()
                    .setPosition(position)
                    .build();
        }
        return player;
    }
}