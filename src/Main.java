import com.sixlegs.png.PngImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * User: chris
 * Date: 3/28/11
 * Time: 5:38 PM
 */
public class Main {

    private static int sampleFrom = 2;
    private static int sampleTo = 2;
    private static int patchXstart;
    private static int patchXend;
    private static int patchYstart;
    private static int patchYend;

    private static int contentXstart;
    private static int contentXend;
    private static int contentYstart;
    private static int contentYend;

    public static void main(String[] args) throws IOException {
	if(args.length != 4)
	{

	    System.err.println("USAGE: prog <PNG_DIR> <SAMPLE_DIR> <SAMPLEFROME_INT_VAL> <SAMPLETO_INT_VAL> ");
	    System.exit(1);
	}

        File sampleDir = new File(args[1].trim());
        String currentDir =args[0].trim();// "/home/chris/Desktop/iconpack/drawable-hdpi";
	sampleFrom=Integer.parseInt(args[2].trim());
	sampleTo=Integer.parseInt(args[3].trim());
        File f = new File(currentDir);

        File[] files = f.listFiles();
        for (File ff : files) {
            if (ff.isDirectory())
                continue;
            patchImage(ff, sampleDir);
//            break;
        }
        return;

    }


    private static void patchImage(File cf, File sampleDir) throws IOException {

        String sName = cf.getName().replace(".png", ".9.png");
        File sf = new File(sampleDir.getAbsolutePath() + File.separator + sName);
        if (!cf.exists() || !sf.exists())
            return;

        PngImage img = new PngImage();
        BufferedImage bi = img.read(sf);
        int height = img.getHeight();
        int width = img.getWidth();
        for (int i = 0; i < width; ++i) {
            if (isTransparentPix(bi, i, 0))
                continue;
            patchXstart = i;
            for (; i < width; ++i) {
                if (!isTransparentPix(bi, i, 0))
                    continue;
                patchXend = i - 1;
                break;
            }
            break;
        }
        for (int i = 0; i < height; ++i) {
            if (isTransparentPix(bi, 0, i))
                continue;
            patchYstart = i;
            for (; i < height; ++i) {
                if (!isTransparentPix(bi, 0, i))
                    continue;
                patchYend = i - 1;
                break;
            }
            break;

        }

        for (int i = 0; i < width; ++i) {
            if (isTransparentPix(bi, i, height - 1))
                continue;
            contentXstart = i;
            for (; i < width; ++i) {
                if (!isTransparentPix(bi, i, height - 1))
                    continue;
                contentXend = i - 1;
                break;
            }
            break;
        }

        for (int i = 0; i < height; ++i) {
            if (isTransparentPix(bi, width - 1, i))
                continue;
            contentYstart = i;
            for (; i < height; ++i) {
                if (!isTransparentPix(bi, width - 1, i))
                    continue;
                contentYend = i - 1;
                break;
            }
            break;

        }
        patchXstart = scaleSize(patchXstart, sampleFrom, sampleTo);
        patchXend = scaleSize(patchXend, sampleFrom, sampleTo);
        patchYstart = scaleSize(patchYstart, sampleFrom, sampleTo);
        patchYend = scaleSize(patchYend, sampleFrom, sampleTo);
        contentXstart = scaleSize(contentXstart, sampleFrom, sampleTo);
        contentXend = scaleSize(contentXend, sampleFrom, sampleTo);
        contentYstart = scaleSize(contentYstart, sampleFrom, sampleTo);
        contentYend = scaleSize(contentYend, sampleFrom, sampleTo);
        img = new PngImage();
        BufferedImage out = img.read(cf);


        BufferedImage ninePatchedOut = new BufferedImage(out.getWidth() + 2, out.getHeight() + 2, BufferedImage.TYPE_INT_ARGB);
        //copy orignal pixels
        for (int x = 0; x < out.getWidth(); ++x) {
            for (int y = 0; y < out.getHeight(); ++y) {
                ninePatchedOut.setRGB(x + 1, y + 1, out.getRGB(x, y));
            }
        }
        for (int x = patchXstart; x <= patchXend && x < ninePatchedOut.getWidth(); ++x) {
            ninePatchedOut.setRGB(x, 0, 0xff000000);
        }
        for (int y = patchYstart; y <= patchYend && y < ninePatchedOut.getHeight(); ++y) {
            ninePatchedOut.setRGB(0, y, 0xff000000);
        }
        for (int x = contentXstart; x <= contentXend && x < ninePatchedOut.getWidth(); ++x) {
            ninePatchedOut.setRGB(x, ninePatchedOut.getHeight() - 1, 0xff000000);
        }
        for (int y = contentYstart; y <= contentYend && y < ninePatchedOut.getHeight(); ++y) {
            ninePatchedOut.setRGB(ninePatchedOut.getWidth() - 1, y, 0xff000000);
        }

        ImageIO.write(ninePatchedOut, "png", new File(cf.getAbsolutePath().replace(".png", ".9.png")));
        cf.delete();
    }

    private static int scaleSize(int i, int scaleFrom, int scaleTo) {
        return Math.max(1, Math.round(i * scaleTo / scaleFrom));
    }

    private static boolean isTransparentPix(BufferedImage bi, int x, int y) {
        int color = bi.getRGB(x, y);
        int alpha = (color >>> 24) & 0xff;
        return alpha == 0;
    }


}
