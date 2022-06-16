package project12.group19.api.geometry.plane;

public interface PlanarDimensioned {
    PlanarDimensions getDimensions();

    default double getWidth() {
        return getDimensions().getWidth();
    }

    default double getHalfWidth() {
        return getWidth() / 2;
    }

    default double getHeight() {
        return getDimensions().getHeight();
    }

    default double getHalfHeight() {
        return getHeight() / 2;
    }

    default double getSmallerDimension() {
        return Math.min(getWidth(), getHeight());
    }

    default double getLargerDimension() {
        return Math.max(getWidth(), getHeight());
    }
}
