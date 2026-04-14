import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class StripImageMetadata {
    private static final int TARGET_WIDTH = 1080;
    private static final int TARGET_HEIGHT = 1920;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: StripImageMetadata <input> <output>");
            System.exit(1);
        }

        BufferedImage input = ImageIO.read(new File(args[0]));
        if (input == null) {
            throw new IllegalArgumentException("Unsupported image: " + args[0]);
        }

        double scale = Math.max((double) TARGET_WIDTH / input.getWidth(), (double) TARGET_HEIGHT / input.getHeight());
        int scaledWidth = (int) Math.ceil(input.getWidth() * scale);
        int scaledHeight = (int) Math.ceil(input.getHeight() * scale);
        int x = (TARGET_WIDTH - scaledWidth) / 2;
        int y = 0;

        BufferedImage output = new BufferedImage(TARGET_WIDTH, TARGET_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = output.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(input, x, y, scaledWidth, scaledHeight, null);
        g.dispose();

        File out = new File(args[1]);
        File parent = out.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ImageIO.write(output, "jpg", out);
    }
}
