package photon.file;

import photon.file.parts.IPhotonProgress;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.sl1.Sl1FileHeader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
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
        // TODO:: Should we add bottom layer count here?

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
        layers = new ArrayList<PhotonFileLayer>(header.getNumberOfLayers());
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


        }



        return this;
    }

    @Override
    protected void writeFile(OutputStream outputStream) throws Exception {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void fromSlicedFile(SlicedFile input) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
