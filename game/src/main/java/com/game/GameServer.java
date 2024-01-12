package com.game;

import com.game.service.GameService;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GameServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(3000)
                .addService(new GameService())
                .build();

        server.start();

        server.awaitTermination();

    }
}