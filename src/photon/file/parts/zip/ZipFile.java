package photon.file.parts.zip;

import photon.file.SlicedFile;
import photon.file.parts.EFileType;
import photon.file.parts.IPhotonProgress;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonFilePreview;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFile extends SlicedFile {
    final static Pattern layerPattern = Pattern.compile("(\\d+).png");

    @Override
    public SlicedFile readFromFile(File file, IPhotonProgress iPhotonProgress) throws Exception {
        java.util.zip.ZipFile zf = new java.util.zip.ZipFile(file);
        iPhotonProgress.showInfo("Reading Chitubox Zip file header information...");
        ZipEntry headerEntry = zf.getEntry("run.gcode");
        if (headerEntry == null) {
            iPhotonProgress.showInfo("Invalid zip file - no header found.");
            throw new FileNotFoundException("Missing run.gcode");
        }
        ZipFileHeader header = new ZipFileHeader(zf.getInputStream(headerEntry));
        fileHeader = header;

        iPhotonProgress.showInfo("Reading large preview...");
        ZipEntry entry = zf.getEntry("preview.png");
        if (entry == null) {
            iPhotonProgress.showInfo("Invalid zip file - no preview found");
            throw new FileNotFoundException("Missing preview.png");
        }
        previewOne = new PhotonFilePreview(zf.getInputStream(entry));

        iPhotonProgress.showInfo("Reading small preview...");
        entry = zf.getEntry("preview_cropping.png");
        if (entry == null) {
            iPhotonProgress.showInfo("Invalid zip file - no small preview found");
            throw new FileNotFoundException("Missing preview_cropping.png");
        }
        previewTwo = new PhotonFilePreview(zf.getInputStream(entry));

        // Don't assume the layers are in any particular order.
        PhotonFileLayer[] layerArr = new PhotonFileLayer[header.getNumberOfLayers()];

        Enumeration<? extends ZipEntry> entries = zf.entries();
        Matcher entryMatcher;
        int loaded_layer_count = 0;
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            String entryName = entry.getName().toLowerCase();
            if (entryName.equals("preview.png")
                    || entryName.equals("preview_cropping.png")
                    || entryName.equals("run.gcode")) {
                continue;
            }
            entryMatcher = layerPattern.matcher(entryName);
            if( !entryMatcher.matches() || entryMatcher.group(1) == null) {
                // TODO:: is this too harsh?
                throw new IOException("Unexpected file in zip: " + entry.getName() );
            }

            // Zip files are numbered 1..x
            int curIndex = Integer.parseInt(entryMatcher.group(1))-1;
            iPhotonProgress.showInfo("Reading zip file layer " + (++loaded_layer_count) + "/" + header.getNumberOfLayers());


            layerArr[curIndex] = PhotonFileLayer.readLayer(
                    fileHeader.getResolutionX(),
                    fileHeader.getResolutionY(),
                    zf.getInputStream(entry));

            if( curIndex < header.getBottomLayers()) {
                layerArr[curIndex].setLayerExposure(header.getBottomExposureTimeSeconds());
            } else {
                layerArr[curIndex].setLayerExposure(header.getExposureTimeSeconds());
            }
            layerArr[curIndex].setLayerOffTimeSeconds(header.getOffTimeSeconds());
            layerArr[curIndex].setLayerPositionZ(curIndex * header.getLayerHeight());
            layerArr[curIndex].setFileHeader(header);
        }
        layers = Arrays.asList(layerArr);

        return this;
    }

    // would be stuck at 0k at arbitrary point in symmetry shape
    // how is symmetry break relevent.
    //

    @Override
    protected void writeFile(OutputStream outputStream) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(outputStream);
        ZipEntry layerEntry;
        BufferedImage image;
        String name;

        for (int i = 0; i < layers.size(); i++) {
            name = String.format("%d.png", i);
            layerEntry = new ZipEntry(name);
            image = layers.get(i).getImage();
            zos.putNextEntry(layerEntry);
            ImageIO.write(image, "png", zos);
            zos.closeEntry();
        }

        ZipEntry previewEntry = new ZipEntry("preview.png");
        zos.putNextEntry(previewEntry);
        ImageIO.write(previewOne.getImage(), "png", zos);
        zos.closeEntry();

        previewEntry = new ZipEntry("preview_cropping.png");
        zos.putNextEntry(previewEntry);
        ImageIO.write(previewTwo.getImage(), "png", zos);
        zos.closeEntry();

        ZipEntry gCode = new ZipEntry("run.gcode");
        zos.putNextEntry(gCode);
        ((ZipFileHeader) fileHeader).write(zos);
        zos.closeEntry();
        zos.close();
    }

    @Override
    public SlicedFile fromSlicedFile(SlicedFile input) {
        fileHeader = new ZipFileHeader(input.getHeader());
        previewOne = input.getPreviewOne();
        previewTwo = input.getPreviewTwo();
        layers = input.getLayers();
        islandList = input.getIslandList();
        islandLayerCount = input.getIslandLayerCount();
        islandLayers = input.getIslandLayers();
        margin = input.getMargin();
        marginLayers = input.getMarginLayers();
        return this;
    }

    @Override
    public boolean hasPreviews() {
        return true;
    }

    @Override
    public EFileType getType() {
        return EFileType.Zip;
    }
}
