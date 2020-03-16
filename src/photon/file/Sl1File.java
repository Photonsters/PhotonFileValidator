package photon.file;

import photon.file.parts.IPhotonProgress;
import photon.file.parts.PhotonFileLayer;
import photon.file.parts.sl1.Sl1FileHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
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

        // While _currently_ prusaslicer puts the layers in the zip in numeric order,
        // there is no point in assuming it, nor that .entries() will load them
        layers = new ArrayList<PhotonFileLayer>(header.getNumberOfLayers());
        Enumeration<? extends ZipEntry> entries = zf.entries();

        //Pattern
        while( entries.hasMoreElements() ){
            ZipEntry entry = entries.nextElement();
            if( entry.getName().equalsIgnoreCase("config.ini")) {
                continue;
            }

        }



        return this;
    }

    @Override
    protected void writeFile(OutputStream outputStream) throws Exception {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
