package project12.group19.api.domain;

import project12.group19.api.motion.Friction;

public interface Surface {
    double getHeight(double x, double y);
    Friction getFriction(double x, double y);
}
