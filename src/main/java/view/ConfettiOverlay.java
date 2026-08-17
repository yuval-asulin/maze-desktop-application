package view;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfettiOverlay extends Canvas {

    private static final Color[] COLORS = {
        Color.rgb(255, 80,  120),
        Color.rgb(255, 215, 0),
        Color.rgb(64,  196, 255),
        Color.rgb(100, 220, 100),
        Color.rgb(255, 140, 0),
        Color.rgb(180, 80,  255),
        Color.rgb(255, 255, 255),
    };

    private static class Particle {
        double x, y, vx, vy, size, rotation, rotSpeed;
        Color color;
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private AnimationTimer timer;

    public ConfettiOverlay() {
        setMouseTransparent(true);
    }

    public void start() {
        double w = getWidth();
        double h = getHeight();

        particles.clear();
        for (int i = 0; i < 160; i++) {
            particles.add(spawnParticle(w, h, true));
        }

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double width  = getWidth();
                double height = getHeight();
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, width, height);

                for (Particle p : particles) {
                    p.x        += p.vx;
                    p.y        += p.vy;
                    p.rotation += p.rotSpeed;

                    if (p.y > height + p.size) {
                        Particle fresh = spawnParticle(width, height, false);
                        p.x = fresh.x; p.y = fresh.y;
                        p.vx = fresh.vx; p.vy = fresh.vy;
                        p.size = fresh.size; p.rotation = fresh.rotation;
                        p.rotSpeed = fresh.rotSpeed; p.color = fresh.color;
                    }

                    gc.save();
                    gc.translate(p.x, p.y);
                    gc.rotate(p.rotation);
                    gc.setFill(p.color);
                    gc.fillRect(-p.size / 2, -p.size / 4, p.size, p.size / 2);
                    gc.restore();
                }
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
    }

    private Particle spawnParticle(double w, double h, boolean scatter) {
        Particle p   = new Particle();
        p.x          = random.nextDouble() * w;
        p.y          = scatter ? random.nextDouble() * -h : -10;
        p.vx         = (random.nextDouble() - 0.5) * 3;
        p.vy         = 2 + random.nextDouble() * 4;
        p.size       = 7 + random.nextDouble() * 9;
        p.rotation   = random.nextDouble() * 360;
        p.rotSpeed   = (random.nextDouble() - 0.5) * 8;
        p.color      = COLORS[random.nextInt(COLORS.length)];
        return p;
    }
}
