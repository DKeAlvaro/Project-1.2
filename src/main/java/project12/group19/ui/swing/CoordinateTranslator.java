package project12.group19.ui.swing;

public class CoordinateTranslator {
    // Ratios are defined as real coordinates to pixel coordinates,
    // or "how many pixels there are in one meter"
    private final double xRatio;
    private final double yRatio;
    private final int pixelWidth;
    private final int pixelHeight;

    public CoordinateTranslator(double xRatio, double yRatio, int pixelWidth, int pixelHeight) {
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
    }

    public int toPixelX(double x) {
        return (pixelWidth / 2) + (int) (x * xRatio);
    }

    public double toRealX(int x) {
        int normalizedX = x - pixelWidth / 2;
        return normalizedX / xRatio;
    }

    public int toPixelY(double y) {
        return (pixelHeight / 2) + (int) (y * yRatio);
    }

    public double toRealY(int y) {
        int normalizedY = y - pixelHeight / 2;
        return normalizedY / yRatio;
    }
}
