package photon.file.parts;

import photon.file.ui.PhotonAALevel;

/**
 * Parameters is a holder class for all the known parameter values for all supported file formats.
 * If you are adding a new file format, look for existing equivalent entries first, renaming them
 * in the write/read functions as required.
 */
public enum EParameter {
    // Core parameters. All formats are likely to need these.
    layerCount,
    bottomLayerCount,
    exposureTimeS,
    bottomExposureTimeS,
    layerHeightMM,
    resolutionX,
    resolutionY,
    printTimeS,
    lightOffTimeS,


    // Photon parameters
    version,
    magicHeader,
    bedXMM,
    bedYMM,
    bedZMM,

    previewOneAddress,
    previewTwoAddress,
    layersAddress,
    parametersAddress,
    parametersSize,
    projectType,
    lightPWM,
    bottomLightPWM,
    antialiasingLevel,

    // Photon v2 parameters
    bottomLightOffTimeS,
    machineInfoAddress,
    machineInfoSize,
    volume,
    cost,
    weight,
    machineName,
    bottomLiftSpeed,
    bottomLiftDistance,
    liftSpeed,
    liftDistance,
    retractSpeed,


    // SL1 parameters
    jobName,
    action,
    fileCreationTimestamp,
    materialName,
    printerProfile,
    printProfile,
    // printerModel < use machineName
    // usedMaterial < use volume
    printerVariant,
    prusaSlicerVersion,
    slowLayerCount,

    // ZIP parameters
    fileName,
    // dropSpeed << retractSpeed
    // machineZ << bedZMM
    zSlowUpDistance,
    // price << cost
    // normalDropSpeed << retractSpeed
    startGCode,
    endGCode;
    // mirror < projectType (yes, another dupe)
    // machineType < machineName
    // resin < materialname
    // normalLayerLiftHeight < liftDistance
    // normalLayerLiftSpeed < liftSpeed
    // bottomLayerLiftHeight < bottomLiftDistance
    // bottomLayerLiftSpeed < bottomLiftSpeed

    // Default values for fields.
    public static final short DEFAULT_PWM = 255;
    public static final float DEFAULT_LIFT_DISTANCE = 5.0f;
    public static final String DEFAULT_MATERIAL_NAME = "resin";
    public static final String DEFAULT_MACHINE_NAME = "default";
}
