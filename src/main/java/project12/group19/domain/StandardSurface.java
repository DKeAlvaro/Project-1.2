package project12.group19.domain;

import project12.group19.api.domain.Item;
import project12.group19.api.domain.Surface;
import project12.group19.api.motion.Friction;
import project12.group19.math.parser.expression.PostfixExpression;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;

public record StandardSurface(
        PostfixExpression profile,
        Friction friction,
        Set<Item.Overlay> overlays
) implements Surface {
    @Override
    public double getHeight(double x, double y) {
        OptionalDouble result = profile.resolve(Map.of("x", x, "y", y)).calculate();

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Surface height is undefined at point x=" + x + ", y=" + y);
        }

        return result.getAsDouble();
    }

    @Override
    public Friction getFriction(double x, double y) {
        return overlays.stream()
                .filter(spot -> spot.includes(x, y))
                .findFirst()
                .map(Item.Overlay::friction)
                .orElse(friction);
    }
}
