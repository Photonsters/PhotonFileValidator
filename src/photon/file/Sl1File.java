package photon.file;

import photon.file.parts.EFileType;
import photon.file.parts.IPhotonProgress;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonLayer;
import photon.file.parts.sl1.Sl1FileHeader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import java.nio.Buffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Sl1File extends SlicedFile{
    @Override
    public SlicedFile readFromFile(File file, IPhotonProgress iPhotonProgress) throws Exception {
        ZipFile zf = new ZipFile(file);
        iPhotonProgress.showInfo("Reading Prusa SL1 file header information...");
        ZipEntry headerEntry = zf.getEntry("config.ini");
        if(headerEntry == null) {
            iPhotonProgress.showInfo("Invalid SL1 file - no header found.");
            throw new FileNotFoundException("Missing config.ini");
        }
        Sl1FileHeader header = new Sl1FileHeader(zf.getInputStream(headerEntry));


        iFileHeader = header;

        iPhotonProgress.showInfo("Reading sl1 layers information...");

        // Extract and parse the first layer so we know the dimensions we are dealing with.
        // TODO:: I'm assuming it is always 5 0s. Should make this more robust.
        ZipEntry firstLayerEntry = zf.getEntry(header.getJobName()+"00000.png");
        if(firstLayerEntry == null) {
            iPhotonProgress.showInfo("Invalid SL1 file - missing first layer");
            throw new FileNotFoundException("Missing first layer file");
        }
        InputStream firstLayerIS = zf.getInputStream(firstLayerEntry);
        BufferedImage firstLayerImg = ImageIO.read(firstLayerIS);
        header.setResolutionX(firstLayerImg.getWidth());
        header.setResolutionY(firstLayerImg.getHeight());

        // While _currently_ prusaslicer puts the layers in the zip in numeric order,
        // there is no point in assuming it, nor that .entries() will load them in any order
        PhotonFileLayer[] layerArr = new PhotonFileLayer[header.getNumberOfLayers()];

        Enumeration<? extends ZipEntry> entries = zf.entries();

        Pattern entryPattern = Pattern.compile(header.getJobName() + "0*(\\d+).png");
        while( entries.hasMoreElements() ){
            ZipEntry entry = entries.nextElement();
            if( entry.getName().equalsIgnoreCase("config.ini")) {
                continue;
            }
            Matcher entryMatcher = entryPattern.matcher(entry.getName());
            if( !entryMatcher.matches() || entryMatcher.group(1) == null) {
                // TODO:: is this too harsh?
                throw new IOException("Unexpected file in zip: " + entry.getName() );
            }
            int curIndex = Integer.parseInt(entryMatcher.group(1));
            iPhotonProgress.showInfo("Reading SL1 file layer " + (curIndex+1) + "/" + header.getNumberOfLayers());

            layerArr[curIndex] = PhotonFileLayer.readLayer(
                    iFileHeader.getResolutionX(),
                    iFileHeader.getResolutionY(),
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

    @Override
    protected void writeFile(OutputStream outputStream) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(outputStream);
        ZipEntry config = new ZipEntry("config.ini");
        zos.putNextEntry(config);
        ((Sl1FileHeader)iFileHeader).write(zos);
        zos.closeEntry();
        // TODO:: AA
        String name;
        ZipEntry layerEntry;
        BufferedImage image;
        for( int i=0; i<layers.size(); i++ ) {
            name = String.format("%s%05d.png",((Sl1FileHeader)iFileHeader).getJobName(), i);
            layerEntry = new ZipEntry(name);
            image = layers.get(i).getLayer().getImage();
            zos.putNextEntry(layerEntry);
            ImageIO.write(image, "png", zos);
            zos.closeEntry();
        }
        zos.close();
    }

    @Override
    public SlicedFile fromSlicedFile(SlicedFile input) {
        iFileHeader = new Sl1FileHeader(input.iFileHeader);
        previewOne = null;
        previewTwo = null;
        layers = input.layers;
        islandList = input.islandList;
        islandLayerCount = input.islandLayerCount;
        islandLayers = input.islandLayers;
        margin = input.margin;
        marginLayers = input.marginLayers;
        return this;
    }

    @Override
    public boolean hasPreviews() {
        return false;
    }

    @Override
    public EFileType getType() {
        return EFileType.Sl1;
    }
}
