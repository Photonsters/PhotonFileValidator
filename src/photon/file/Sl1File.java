package photon.file;

import photon.file.parts.IPhotonProgress;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.PhotonLayer;
import photon.file.parts.sl1.Sl1FileHeader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

        // TODO:: Fake a preview
        // TODO:: Fake a small preview
        // TODO:: create parameters object
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

            layerArr[curIndex] = readLayer(zf.getInputStream(entry));
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

    protected PhotonFileLayer readLayer(InputStream input) throws Exception {
        PhotonFileLayer target = new PhotonFileLayer();
        BufferedImage img = ImageIO.read(input);



        PhotonLayer layer = new PhotonLayer(iFileHeader.getResolutionX(), iFileHeader.getResolutionY());
        layer.clear();
        //TODO:: AA Support
        for( int y=0; y<iFileHeader.getResolutionY(); y++) {
            for( int x=0; x<iFileHeader.getResolutionX(); x++) {
                // assume the image is greyscale. TODO:: average the values?
                if( (img.getRGB(x,y)&0x000000ff) == 0x000000ff)
                    layer.supported(x,y);
            }
        }
        target.saveLayer(layer);
        return target;
    }

    @Override
    protected void writeFile(OutputStream outputStream) throws Exception {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void fromSlicedFile(SlicedFile input) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean hasPreviews() {
        return false;
    }
}
