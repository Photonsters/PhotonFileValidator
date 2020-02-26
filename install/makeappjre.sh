#!/bin/sh
jar2app ../out/artifacts/PhotonFileValidator.jar -i img/icons/validator.icns -n "Photon File Validator jre" -v 2.1.0 -s 2.1.0 -c "(C) Copyright by Bonosoft" -j "-Xmx5g -Xms1g" -r /Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk

