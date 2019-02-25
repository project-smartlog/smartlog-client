/*
 * Copyright 2016-2019
 *
 * Interreg Central Baltic 2014-2020 funded project
 * Smart Logistics and Freight Villages Initiative, CB426
 *
 * Kouvola Innovation Oy, FINLAND
 * Region Örebro County, SWEDEN
 * Tallinn University of Technology, ESTONIA
 * Foundation Valga County Development Agency, ESTONIA
 * Transport and Telecommunication Institute, LATVIA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.propentus.smartlog.utils;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class VersionResolver {
    private static final Logger logger = LoggerFactory.getLogger(VersionResolver.class);

    public VersionResolver() {
    }

    public static boolean updateContainer(DockerClient docker, String containerName, String updatedVersion) throws DockerException, InterruptedException {
        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.withStatusRunning());
        for(Container i : containers){
            if(i.names().get(0).substring(1).equals(containerName)){
                try {
                    ContainerInfo containerInfo = docker.inspectContainer(containerName);
                    int semiColonIndex = containerInfo.config().image().lastIndexOf(":");
                    String containerVersion = containerInfo.config().image().substring(semiColonIndex + 1);
                    if (containerVersion.equals(updatedVersion)) {
                        return false;
                    } else {
                        logger.info("Updating " + containerName);
                        return true;
                    }
                } catch(Exception e){
                    logger.info("Launching " + containerName);
                    return true;
                }
            }
        }
        logger.info("Launching " + containerName);
        return true;
    }

    public static boolean updateChaincode(DockerClient docker, String chaincodeName, String updatedVersion){
        try{
            List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
            for(Container i : containers){
                if(i.names().get(0).substring(1,4).equals("dev")){
                    String[] name = i.names().get(0).split("-");
                    if(name[name.length-2].equals(chaincodeName)){
                        if(name[name.length-1].equals(updatedVersion)){
                            return false;
                        }
                        else {
                            logger.info("Updating " + chaincodeName);
                            return true;
                        }
                    }
                }
            }
            logger.info("Launching " + chaincodeName);
            return true;
        }
        catch(Exception e){
            logger.info("Launching " + chaincodeName);
            return true;
        }
    }

    public static String getChaincodeContainerVersion(DockerClient docker, String chaincodeName){
        try{
            List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
            for(Container i : containers){
                if(i.names().get(0).substring(1,4).equals("dev")){
                    String[] name = i.names().get(0).split("-");
                    if(name[name.length-2].equals(chaincodeName)){
                        return name[name.length-1];
                    }
                }
            }
            return "Container for " + chaincodeName + " not found.";
        }
        catch(Exception e){
            return "Container for " + chaincodeName + " not found.";
        }
    }
}
