package com.game.response;

import com.game.model.Die;
import com.game.model.GameState;
import com.game.model.Player;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GameStateStreamingResponse implements StreamObserver<GameState> {

    private static final Logger LOGGER = Logger.getLogger(GameStateStreamingResponse.class.getName());
    private final CountDownLatch latch;

    private StreamObserver<Die> dieStreamObserver;

    public GameStateStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(GameState gameState) {
        List<Player> players = gameState.getPlayerList();
        players.forEach(player -> LOGGER.info(player.getName() + ":" + player.getPosition()));
        boolean isGameOver = players.stream()
                .anyMatch(player -> {
                    if (player.getPosition() == 100) {
                        LOGGER.info(player.getName() + " win!");
                        return true;
                    }
                    return false;
                });
        if (isGameOver) {
            LOGGER.info("Game Over!");
            this.dieStreamObserver.onCompleted();
        } else {
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            this.roll();
        }
        System.out.println("-------------------------------");
    }


    @Override
    public void onError(Throwable throwable) {
        LOGGER.severe(throwable.getMessage());
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        LOGGER.info("Completed");
        this.latch.countDown();
    }

    public void setDieStreamObserver(StreamObserver<Die> dieStreamObserver) {
        this.dieStreamObserver = dieStreamObserver;
    }

    public void roll() {
        int dieValue = ThreadLocalRandom.current().nextInt(1,7);
        Die die = Die.newBuilder().setValue(dieValue).build();
        this.dieStreamObserver.onNext(die);
    }
}