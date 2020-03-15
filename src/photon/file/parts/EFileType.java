package photon.file.parts;

/**
 * Enumeration of possible file types.
 */
public enum EFileType {
    Photon("photon"),
    Cbddlp("cbddlp"),
    PhotonS("photons"),
    Sl1("sl1");

    private String extension;

    private EFileType(String value) {
        extension = value;
    }

    public String getExtension() { return extension;}


    /**
     * Given a filename, attempt to match it to one of the known filetypes by extension
     * @param fileName to match
     * @return the type of file found.
     * @throws IllegalArgumentException if this is not a known filetype.
     */
    public static EFileType identifyFile(String fileName) throws IllegalArgumentException {
        String lowerName = fileName.toLowerCase();
        for(EFileType e: EFileType.values()) {
            if(fileName.endsWith(e.getExtension())) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown file extension passed.");
    }
}
