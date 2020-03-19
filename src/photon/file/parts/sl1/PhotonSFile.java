package photon.file.parts.sl1;

import photon.file.SlicedFile;
import photon.file.parts.EFileType;
import photon.file.parts.IPhotonProgress;
import photon.file.parts.photons.PhotonsFileHeader;

import java.io.File;
import java.io.OutputStream;

public class PhotonSFile extends SlicedFile {
    @Override
    public SlicedFile readFromFile(File file, IPhotonProgress iPhotonProgress) throws Exception {
        iPhotonProgress.showInfo("Reading Photon S file header information...");
        byte[] fileData = getBinaryData(file);

        PhotonsFileHeader photonsFileHeader = new PhotonsFileHeader(fileData);
        fileHeader = photonsFileHeader;

        return this;    }

    @Override
    protected void writeFile(OutputStream outputStream) throws Exception {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public SlicedFile fromSlicedFile(SlicedFile input) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean hasPreviews() {
        return true;
    }

    @Override
    public EFileType getType() {
        return EFileType.PhotonS;
    }
}
