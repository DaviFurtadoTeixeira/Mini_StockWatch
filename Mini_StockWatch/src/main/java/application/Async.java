package application;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;

public final class Async {

    private static final ExecutorService POOL =
        Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "stockwatch-worker");
            t.setDaemon(true);
            return t;
        });

    private Async() {
    }

    public static <T> void executar(
            Callable<T> trabalho,
            Consumer<T> aoConcluir,
            Consumer<Throwable> aoFalhar,
            Node... bloquear) {

        Scene cena = null;

        for (Node no : bloquear) {
            if (no != null) {
                no.setDisable(true);
                if (cena == null) {
                    cena = no.getScene();
                }
            }
        }

        final Scene cenaFinal = cena;

        if (cenaFinal != null) {
            cenaFinal.setCursor(Cursor.WAIT);
        }

        Runnable liberar = () -> {
            for (Node no : bloquear) {
                if (no != null) {
                    no.setDisable(false);
                }
            }
            if (cenaFinal != null) {
                cenaFinal.setCursor(Cursor.DEFAULT);
            }
        };

        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return trabalho.call();
            }
        };

        task.setOnSucceeded(e -> {
            liberar.run();
            if (aoConcluir != null) {
                aoConcluir.accept(task.getValue());
            }
        });

        task.setOnFailed(e -> {
            liberar.run();
            Throwable erro = task.getException();
            if (erro != null) {
                erro.printStackTrace();
            }
            if (aoFalhar != null) {
                aoFalhar.accept(erro);
            }
        });

        POOL.execute(task);
    }
}