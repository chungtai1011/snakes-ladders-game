package com.game.service;

import com.game.model.Die;
import com.game.model.GameState;
import com.game.model.Player;
import com.game.request.DieStreamingRequest;
import io.grpc.stub.StreamObserver;

public class GameService extends GameServiceGrpc.GameServiceImplBase {
    @Override
    public StreamObserver<Die> roll(StreamObserver<GameState> responseObserver) {
        Player client = Player.newBuilder().setName("Client").setPosition(0).build();
        Player server = Player.newBuilder().setName("Server").setPosition(0).build();

        return new DieStreamingRequest(client, server, responseObserver);
    }
}