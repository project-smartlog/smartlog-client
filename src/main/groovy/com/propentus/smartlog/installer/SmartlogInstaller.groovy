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

package com.propentus.smartlog.installer

import com.propentus.common.util.file.FileUtil
import com.propentus.common.util.grails.GrailsUtil
import com.propentus.iot.cmd.CommandLineRunner
import com.propentus.iot.configs.ConfigReader
import com.propentus.iot.configs.OrganisationConfiguration
import com.propentus.smartlog.utils.VersionResolver
import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.messages.Container
import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.ContainerCreation
import com.spotify.docker.client.messages.HostConfig
import com.spotify.docker.client.messages.NetworkConfig
import com.spotify.docker.client.messages.PortBinding
import com.spotify.docker.client.messages.RegistryAuth
import grails.core.GrailsApplication
import grails.web.servlet.mvc.GrailsParameterMap
import net.lingala.zip4j.core.ZipFile
import org.apache.commons.io.IOUtils
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import sun.misc.Version

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit

class SmartlogInstaller {

    private static final Logger logger = LoggerFactory.getLogger(SmartlogInstaller.class);
    private static String mainPath = "/etc/hyperledger/"
    private static final String FULL_PATH = mainPath + "ext_config/"

    private static final String REGISTRY_USER = "testi"
    private static final String REGISTRY_PASSWORD = "2nyfUcCTdGkWwysqBtfs"

    private static final String NETWORK = "smartlog"

    static {
        String newPath = System.getProperty("smartlog.installpath")

        if (newPath != null) {
            mainPath = newPath;
        }
    }

    GrailsApplication grailsApplication = GrailsUtil.getBean(GrailsApplication.class)
    OrganisationConfiguration orgConfig
    String dbUser
    String dbPassword
    String configPassword
    String peerName
    String orgName
    String ordererName
    String channel
    String mspid
    String peerUrl
    String peerPort
    String peerVersion
    String ordererUrl
    String ordererPort
    String eventhubUrl
    String eventhubPort
    String couchDbUrl
    String couchDbPort
    String couchDbVersion
    String cliVersion
    String watchtowerVersion
    String envPath
    String ublVersion
    String transportChainVersion
    String keystoreVersion
    String currentUblVersion
    String currentTransportChainVersion
    String currentKeystoreVersion
    String[] chaincodeList
    Boolean peerUpdated

    public SmartlogInstaller(params){
        this.configPassword = (String) params.configPassword
        unZip((MultipartFile) params.configPackage)

        ConfigReader reader = new ConfigReader(FULL_PATH + "config.json")
        this.orgConfig = reader.getOrganisationConfiguration()
        this.dbUser = orgConfig.couchDbUsername
        this.dbPassword = orgConfig.couchDbPassword
        this.peerName = orgConfig.peers[0].domainName
        this.orgName = orgConfig.organisation.domainName
        this.ordererName = orgConfig.orderer.domainName
        this.channel = orgConfig.channel
        this.mspid = orgConfig.organisation.mspid
        this.peerUrl = orgConfig.peers[0].url
        String[] temp = peerUrl.split(":")
        this.peerPort = temp[temp.size() -1]
        this.peerVersion = grailsApplication.config.getProperty("smartlog.hyperledger.peer.version")
        this.ordererUrl = orgConfig.orderer.url
        temp = ordererUrl.split(":")
        this.ordererPort = temp[temp.size() -1]
        this.eventhubUrl = orgConfig.eventhub.url
        temp = eventhubUrl.split(":")
        this.eventhubPort = temp[temp.size() -1]
        this.couchDbUrl = orgConfig.couchDbUrl
        temp = couchDbUrl.split(":")
        this.couchDbPort = temp[temp.size() -1]
        this.couchDbVersion = grailsApplication.config.getProperty("smartlog.hyperledger.couchdb.version")
        this.cliVersion = grailsApplication.config.getProperty("smartlog.hyperledger.cli.version")
        this.watchtowerVersion = grailsApplication.config.getProperty("smartlog.hyperledger.watchtower.version")
        this.envPath = orgConfig.fabricEnvPath
        this.ublVersion = grailsApplication.config.getProperty("smartlog.hyperledger.chaincodes.ubl.version")
        this.transportChainVersion = grailsApplication.config.getProperty("smartlog.hyperledger.chaincodes.transportChain.version")
        this.keystoreVersion = grailsApplication.config.getProperty("smartlog.hyperledger.chaincodes.keystore.version")
        this.chaincodeList = grailsApplication.config.getProperty("smartlog.hyperledger.chaincodes")
        this.peerUpdated = false
    }

    public SmartlogInstaller(){

        ConfigReader reader = new ConfigReader(FULL_PATH + "config.json")
        this.orgConfig = reader.getOrganisationConfiguration()
        this.dbUser = orgConfig.couchDbUsername
        this.dbPassword = orgConfig.couchDbPassword
        this.peerName = orgConfig.peers[0].domainName
        this.orgName = orgConfig.organisation.domainName
        this.ordererName = orgConfig.orderer.domainName
        this.channel = orgConfig.channel
        this.mspid = orgConfig.organisation.mspid
        this.peerUrl = orgConfig.peers[0].url
        String[] temp = peerUrl.split(":")
        this.peerPort = temp[temp.size() -1]
        this.peerVersion = grailsApplication.config.getProperty("smartlog.hyperledger.peer.version")
        this.ordererUrl = orgConfig.orderer.url
        temp = ordererUrl.split(":")
        this.ordererPort = temp[temp.size() -1]
        this.eventhubUrl = orgConfig.eventhub.url
        temp = eventhubUrl.split(":")
        this.eventhubPort = temp[temp.size() -1]
        this.couchDbUrl = orgConfig.couchDbUrl
        temp = couchDbUrl.split(":")
        this.couchDbPort = temp[temp.size() -1]
        this.couchDbVersion = grailsApplication.config.getProperty("smartlog.hyperledger.couchdb.version")
        this.cliVersion = grailsApplication.config.getProperty("smartlog.hyperledger.cli.version")
        this.watchtowerVersion = grailsApplication.config.getProperty("smartlog.hyperledger.watchtower.version")
        this.envPath = orgConfig.fabricEnvPath
        this.ublVersion = grailsApplication.config.getProperty("smartlog.hyperledger.chaincodes.ubl.version")
        this.transportChainVersion = grailsApplication.config.getProperty("smartlog.hyperledger.chaincodes.transportChain.version")
        this.keystoreVersion = grailsApplication.config.getProperty("smartlog.hyperledger.chaincodes.keystore.version")
        this.peerUpdated = false
    }

    public static isInstalled(){
        try {
            ConfigReader reader = new ConfigReader(FULL_PATH + "config.json")
            OrganisationConfiguration orgConfig = reader.getOrganisationConfiguration()
            return orgConfig.installed
        } catch(Exception e){
            return false
        }
    }

    public install(){
        logger.info("Starting installation")

        final DockerClient docker = DefaultDockerClient.fromEnv().build()

        Thread.sleep(5000)

        //RegistryAuth for pulling latest docker images
        RegistryAuth registryAuth = RegistryAuth.builder()
                .username(REGISTRY_USER) // registryUser
                .password(REGISTRY_PASSWORD) // registryPassword
                .build()

        if(!isInstalled()){
            pullChaincodes()
        }
        else{
            this.currentUblVersion = VersionResolver.getChaincodeContainerVersion(docker, "UBL")
            this.currentKeystoreVersion = VersionResolver.getChaincodeContainerVersion(docker, "Keystore")
            this.currentTransportChainVersion = VersionResolver.getChaincodeContainerVersion(docker, "TransportChain")
        }

        createNetworkIfNotExisting(docker)

       // Creating containers
        createCouchdb(docker)

        //Timeout to ensure that couchdb is up and running when other containers are being created
        Thread.sleep(3000)

        createPeer(docker)

        createCli(docker)

        createWatchTower(docker)

        logger.info("Containers created")

        //30 sec timeout to make sure that all the containers are up and running before setting up chaincodes
        Thread.sleep(30000)

        setupChainCodes(docker)

        orgConfig.installed = true
        FileUtil.writeFile(FULL_PATH, "config.json", orgConfig.toJson(), true)
    }


    // Unzip the config package uploaded through the installation page
    public unZip(MultipartFile configPackage){
        logger.info("Unzipping config file")
        File dir = new File(FULL_PATH)
        if(dir.isDirectory()){
            FileUtils.cleanDirectory(dir)
        }
        String fileName = configPackage.getOriginalFilename()
        int lastDotIndex = fileName.lastIndexOf(".")
        fileName = fileName.substring(0, lastDotIndex)

        File zip = File.createTempFile(UUID.randomUUID().toString(), "temp")
        FileOutputStream outputStream = new FileOutputStream(zip)
        IOUtils.copy(configPackage.getInputStream(), outputStream)
        outputStream.close()
        String target = FULL_PATH

        ZipFile zipFile = new ZipFile(zip)

        zipFile.setPassword(configPassword)
        zipFile.extractAll(target)

        zip.delete()

        // Move files from organization folder and delete the folder
        File sourceDir = new File(FULL_PATH + fileName)
        File targetDir = new File(FULL_PATH)
        File configDir = new File(FULL_PATH + "config/")
        File[] files = sourceDir.listFiles()

        for(File f : files){
            Path sourcePath = Paths.get(sourceDir.getAbsolutePath()+"/"+f.getName())
            Path destinationPath = Paths.get(targetDir.getAbsolutePath()+"/"+f.getName())
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING)
        }

        //  Create the directory so Docker can mount it
        if (!configDir.exists()) {
            logger.info("Config dir doesn't exist, create it so Docker can mount it")
            Files.createDirectory(configDir.toPath())
        }
    }

    public createChannel(){
        InputStream createChannelTemplateStream = this.class.classLoader.getResourceAsStream('templates/create_channel_template.sh')
        String createChannel = IOUtils.toString(createChannelTemplateStream)

        createChannelTemplateStream.close()

        createChannel = createChannel.replace("{mspid}", mspid)
        createChannel = createChannel.replace("{orgName}", orgName)
        createChannel = createChannel.replace("{peerName}", peerName)
        createChannel = createChannel.replace("{channel}", channel)
        createChannel = createChannel.replace("{ordererName}", ordererName)

        String fileName = "create_channel.sh"

        FileUtil.writeFile(FULL_PATH, fileName, createChannel, true)

        CommandLineRunner.executeCommand(null, getShCommand() + FULL_PATH + fileName)
    }

    public joinChannel(){
        InputStream joinChannelTemplateStream = this.class.classLoader.getResourceAsStream('templates/join_channel_template.sh')
        String joinChannel = IOUtils.toString(joinChannelTemplateStream)

        joinChannelTemplateStream.close()

        joinChannel = joinChannel.replace("{mspid}", mspid)
        joinChannel = joinChannel.replace("{orgName}", orgName)
        joinChannel = joinChannel.replace("{peerName}", peerName)
        joinChannel = joinChannel.replace("{channel}", channel)

        String fileName = "join_channel.sh"

        FileUtil.writeFile(FULL_PATH, fileName, joinChannel, true)

        CommandLineRunner.executeCommand(null, getShCommand() + FULL_PATH + fileName)
    }

    //Pull the latest updates to chaincodes from our repository
    private static pullChaincodes(){
        logger.info("Pulling chaincodes")
        File path = new File(mainPath + "smartlog_chaincodes/")
        if(path.isDirectory()){
            FileUtils.cleanDirectory(path)
        }
        Git result = Git.cloneRepository()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("gitlab+deploy-token-20898", "8JZoRuxy8RGMNDmzsMA7"))
                .setURI("https://gitlab+deploy-token-20898:8JZoRuxy8RGMNDmzsMA7@gitlab.com/projectsmartlog/smartlog-chaincodes.git")
                .setDirectory(path)
                .call()

        result.getRepository().close()
        result.close()
    }


    // Install chaincodes

    public setupChainCodes(DockerClient docker){
        logger.info("Setting up chaincodes")


        if(!isInstalled()){
            createChannel()
            Thread.sleep(10000)
            joinChannel()
            Thread.sleep(10000)

            try{
                installChaincodes(docker, "UBL", ublVersion)
                instantiateChaincodes(docker, "UBL", ublVersion)
            }
            catch(Exception e){
                logger.warn("Chaincode installation failed, might be false positive.", e)
            }
            try{
                installChaincodes(docker, "Keystore", keystoreVersion)
                instantiateChaincodes(docker, "Keystore", keystoreVersion)
            }
            catch(Exception e){
                logger.warn("Chaincode installation failed, might be false positive.", e)
            }
            try{
                installChaincodes(docker, "TransportChain", transportChainVersion)
                instantiateChaincodes(docker, "TransportChain", transportChainVersion)
            }
            catch(Exception e){
                logger.warn("Chaincode installation failed, might be false positive.", e)
            }
        }
        else{
            if(peerUpdated){
                installChaincodes(docker, "Keystore", keystoreVersion)
                installChaincodes(docker, "TransportChain", transportChainVersion)
                installChaincodes(docker, "UBL", ublVersion)
            }
            if(currentKeystoreVersion.contains("not found")) {
                try {
                    installChaincodes(docker, "Keystore", keystoreVersion)
                    instantiateChaincodes(docker, "Keystore", keystoreVersion)
                }
                catch (Exception e){
                    logger.warn("Chaincode installation failed, might be false positive.", e)
                }
            }
            else if (VersionResolver.updateChaincode(docker, "Keystore", keystoreVersion)){
                try{
                    updateChaincodes(docker, "Keystore", keystoreVersion)
                }
                catch(Exception e){
                    logger.warn("Chaincode installation failed, might be false positive.", e)
                }
            }
            if(currentTransportChainVersion.contains("not found")) {
                try {
                    installChaincodes(docker, "TransportChain", transportChainVersion)
                    instantiateChaincodes(docker, "TransportChain", transportChainVersion)
                }
                catch (Exception e){
                    logger.warn("Chaincode installation failed, might be false positive.", e)
                }
            }
            else if (VersionResolver.updateChaincode(docker, "TransportChain", transportChainVersion)){
                try{
                    updateChaincodes(docker, "TransportChain", transportChainVersion)
                }
                catch(Exception e){
                    logger.warn("Chaincode installation failed, might be false positive.", e)
                }
            }
            if(currentUblVersion.contains("not found")) {
                try {
                    installChaincodes(docker, "UBL", ublVersion)
                    instantiateChaincodes(docker, "UBL", ublVersion)
                }
                catch (Exception e){
                    logger.warn("Chaincode installation failed, might be false positive.", e)
                }
            }
            else if (VersionResolver.updateChaincode(docker, "UBL", ublVersion)){
                try{
                    updateChaincodes(docker, "UBL", ublVersion)
                }
                catch(Exception e){
                    logger.warn("Chaincode installation failed, might be false positive.", e)
                }
            }
        }
    }

    // Update chaincodes
    public updateChaincodes(DockerClient docker, String chaincodeName, String chaincodeVersion){

        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers())
        for(Container i : containers){
            if(i.names()[0].substring(1,4) == ("dev")){
                String[] name = i.names().get(0).split("-")
                if(name[name.length-2] == chaincodeName){
                    logger.info("Removed existing container: " + i.names()[0].substring(1))
                    docker.removeContainer(i.id(), DockerClient.RemoveContainerParam.forceKill())
                    docker.removeImage(i.image())
                }
            }
        }
        pullChaincodes()

        InputStream updateTemplateStream = this.class.classLoader.getResourceAsStream('templates/update_cc_template.sh')
        String updateTemplate = IOUtils.toString(updateTemplateStream)

        updateTemplateStream.close()

        updateTemplate = updateTemplate.replace("{mspid}", mspid)
        updateTemplate = updateTemplate.replace("{orgName}", orgName)
        updateTemplate = updateTemplate.replace("{channel}", channel)
        updateTemplate = updateTemplate.replace("{ordererName}", ordererName)
        updateTemplate = updateTemplate.replace("{chaincodeVersion}", chaincodeVersion)
        updateTemplate = updateTemplate.replace("{chaincodeName}", chaincodeName)

        String fileName = "update_"+chaincodeName+".sh"

        FileUtil.writeFile(FULL_PATH, fileName, updateTemplate, true)

        CommandLineRunner.executeCommand(null, getShCommand() + FULL_PATH + fileName)
    }


    // Install chaincodes to peer
    public installChaincodes(DockerClient docker, String chaincodeName, String chaincodeVersion){

        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers())
        for(Container i : containers){
            if(i.names()[0].substring(1,4) == ("dev")){
                String[] name = i.names().get(0).split("-")
                if(name[name.length-2] == chaincodeName){
                    logger.info("Removed existing container: " + i.names()[0].substring(1))
                    docker.removeContainer(i.id(), DockerClient.RemoveContainerParam.forceKill())
                }
            }
        }

        // Build script by filling install template with required information from config file
        // Installer script in use since direct commands not working on Linux right now
        InputStream installTemplateStream = this.class.classLoader.getResourceAsStream('templates/install_cc_template.sh')
        String installTemplate = IOUtils.toString(installTemplateStream)

        installTemplateStream.close()

        installTemplate = installTemplate.replace("{mspid}", mspid)
        installTemplate = installTemplate.replace("{orgName}", orgName)
        installTemplate = installTemplate.replace("{peerName}", peerName)
        installTemplate = installTemplate.replace("{channel}", channel)
        installTemplate = installTemplate.replace("{ordererName}", ordererName)
        installTemplate = installTemplate.replace("{chaincodeName}", chaincodeName)
        installTemplate = installTemplate.replace("{chaincodeVersion}", chaincodeVersion)

        String fileName = "install_"+chaincodeName+".sh"

        FileUtil.writeFile(FULL_PATH, fileName, installTemplate, true)

        CommandLineRunner.executeCommand(null, getShCommand() + FULL_PATH + fileName)
    }


    // Instantiate chaincodes to peer
    public instantiateChaincodes(DockerClient docker, String chaincodeName, String chaincodeVersion){
        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers())
        for(Container i : containers){
            if(i.names()[0].substring(1,4) == ("dev")){
                String[] name = i.names().get(0).split("-")
                if(name[name.length-2] == chaincodeName){
                    logger.info("Removed existing container: " + i.names()[0].substring(1))
                    docker.removeContainer(i.id(), DockerClient.RemoveContainerParam.forceKill())
                }
            }
        }

        // Build script by filling install template with required information from config file
        // Installer script in use since direct commands not working on Linux right now
        InputStream instantiateTemplateStream = this.class.classLoader.getResourceAsStream('templates/instantiate_cc_template.sh')
        String instantiateTemplate = IOUtils.toString(instantiateTemplateStream)

        instantiateTemplateStream.close()

        instantiateTemplate = instantiateTemplate.replace("{mspid}", mspid)
        instantiateTemplate = instantiateTemplate.replace("{orgName}", orgName)
        instantiateTemplate = instantiateTemplate.replace("{peerName}", peerName)
        instantiateTemplate = instantiateTemplate.replace("{channel}", channel)
        instantiateTemplate = instantiateTemplate.replace("{ordererName}", ordererName)
        instantiateTemplate = instantiateTemplate.replace("{chaincodeName}", chaincodeName)
        instantiateTemplate = instantiateTemplate.replace("{chaincodeVersion}", chaincodeVersion)

        String fileName = "instantiate_"+chaincodeName+".sh"

        FileUtil.writeFile(FULL_PATH, fileName, instantiateTemplate, true)

        CommandLineRunner.executeCommand(null, getShCommand() + FULL_PATH + fileName)
    }

    // Couchdb container creator
    // Porbindings are created for all the ports that are needed and those bindings are added to hostconfig
    // Latest couchdb image is pulled and container's config is created
    // To open ports correctly the port bindings need to be added to hostconfig AND as a separate list given as exposedports parameter
    public createCouchdb(DockerClient docker){
        if(!VersionResolver.updateContainer(docker, "couchdb", couchDbVersion)){
            return
        }

        logger.info("Creating container for couchdb")

        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers())
        for(Container i : containers){
            if(i.names()[0].substring(1) == "couchdb"){
                logger.info("Removed existing container: " + i.names()[0].substring(1))
                docker.removeContainer(i.id(), DockerClient.RemoveContainerParam.forceKill())
            }
        }

        List<String> env = new ArrayList<>()
        env.add("COUCHDB_USER="+dbUser)
        env.add("COUCHDB_PASSWORD="+dbPassword)
        Set ports = new HashSet<>()
        ports.add(couchDbPort)
        final Map<String, List<PortBinding>> portBindings = new HashMap<>()
        for (String port : ports) {
            List<PortBinding> hostPorts = new ArrayList<>()
            hostPorts.add(PortBinding.of("0.0.0.0", port))
            portBindings.put(port, hostPorts)
        }

        HostConfig couchdbHostConfig = HostConfig.builder()
                .portBindings(portBindings)
                .build()

        docker.pull('hyperledger/fabric-couchdb:'+couchDbVersion)
        ContainerConfig couchdbContainerConfig = ContainerConfig.builder()
                .image('hyperledger/fabric-couchdb:'+couchDbVersion)
                .env(env)
                .hostConfig(couchdbHostConfig)
                .exposedPorts(ports)
                .workingDir("/opt/gopath/src/github.com/hyperledger/fabric/peer")
                .build()

        ContainerCreation couchdbContainer = docker.createContainer(couchdbContainerConfig)
        docker.renameContainer(couchdbContainer.id(), "couchdb")
        docker.startContainer(couchdbContainer.id())
        docker.connectToNetwork(couchdbContainer.id(), NETWORK)
        docker.disconnectFromNetwork(couchdbContainer.id(), "bridge")
    }

    // Peer container creator
    // Porbindings are created for all the ports that are needed and those bindings are added to hostconfig
    // Latest peer image is pulled and container's config is created
    // To open ports correctly the port bindings need to be added to hostconfig AND as a separate list given in exposedports parameter
    // Volume bindings are also done as a hostconfig parameters
    public createPeer(DockerClient docker){
        if(!VersionResolver.updateContainer(docker, peerName, peerVersion)){
            return
        }

        logger.info("Creating container for peer")

        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers())
        for(Container i : containers){
            if(i.names()[0].substring(1) == peerName){
                logger.info("Removed existing container: " + i.names()[0].substring(1))
                docker.removeContainer(i.id(), DockerClient.RemoveContainerParam.forceKill())
            }
        }

        List<String> env = new ArrayList<>()
        env.add("CORE_LEDGER_STATE_COUCHDBCONFIG_USERNAME="+dbUser)
        env.add("CORE_LEDGER_STATE_COUCHDBCONFIG_PASSWORD="+dbPassword)
        env.add("CORE_VM_ENDPOINT=unix:///host/var/run/docker.sock")
        env.add("CORE_PEER_ID="+peerName)
        env.add("CORE_LOGGING_PEER=debug")
        env.add("CORE_CHAINCODE_LOGGING_LEVEL=DEBUG")
        env.add("CORE_PEER_LOCALMSPID="+mspid)
        env.add("CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/peer/")
        env.add("CORE_PEER_ADDRESS="+peerName+":"+peerPort)
        env.add("CORE_PEER_TLS_ENABLED=false")
        env.add("CORE_PEER_COMMITTER_LEDGER_ORDERER=" + ordererName + ":" + ordererPort)
        env.add("CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE=" + NETWORK)
        env.add("CORE_LEDGER_STATE_STATEDATABASE=CouchDB")
        env.add("CORE_LEDGER_STATE_COUCHDBCONFIG_COUCHDBADDRESS=couchdb:5984")
        env.add("GOROOT=/opt/go/")
        env.add("GOPATH=/opt/gopath")
        Set ports = new HashSet<>()
        ports.add(peerPort)
        ports.add(eventhubPort)

        final Map<String, List<PortBinding>> portBindings = new HashMap<>()
        for (String port : ports) {
            List<PortBinding> hostPorts = new ArrayList<>()
            hostPorts.add(PortBinding.of("0.0.0.0", port))
            portBindings.put(port, hostPorts)
        }


        HostConfig peerHostConfig = HostConfig.builder()
                .privileged(true)
                .appendBinds("/var/run/:/host/var/run/")
                .appendBinds(FULL_PATH + "crypto-config/peerOrganizations/"+orgName+"/peers/"+peerName+"/msp:/etc/hyperledger/msp/peer")
                .appendBinds(FULL_PATH + "crypto-config/peerOrganizations/"+orgName+"/users:/etc/hyperledger/msp/users")
                .appendBinds(FULL_PATH + "config:/etc/hyperledger/configtx")

                .portBindings(portBindings)
                .build()

        docker.pull ('hyperledger/fabric-peer:'+peerVersion)
        ContainerConfig peerContainerConfig = ContainerConfig.builder()
                .image('hyperledger/fabric-peer:'+peerVersion)
                .hostConfig(peerHostConfig)
                .exposedPorts(ports)
                .env(env)
                .workingDir("/opt/gopath/src/github.com/hyperledger/fabric")
                .build()

        ContainerCreation peerContainer = docker.createContainer(peerContainerConfig)
        docker.renameContainer(peerContainer.id(), peerName)
        docker.startContainer(peerContainer.id())
        docker.connectToNetwork(peerContainer.id(),NETWORK)
        docker.disconnectFromNetwork(peerContainer.id(), "bridge")

        if(isInstalled()){

            for(Container i : containers){
                if(i.names()[0].substring(1,4) == ("dev")){
                    String[] name = i.names().get(0).split("-")
                    logger.info("Removed existing container: " + i.names()[0].substring(1))
                    docker.removeContainer(i.id(), DockerClient.RemoveContainerParam.forceKill())
                    docker.removeImage(i.image())
                }
            }

            peerUpdated = true

            InputStream rejoinTemplateStream = this.class.classLoader.getResourceAsStream('templates/rejoin_channel_template.sh')
            String rejoinTemplate = IOUtils.toString(rejoinTemplateStream)

            rejoinTemplateStream.close()

            rejoinTemplate = rejoinTemplate.replace("{mspid}", mspid)
            rejoinTemplate = rejoinTemplate.replace("{orgName}", orgName)
            rejoinTemplate = rejoinTemplate.replace("{peerName}", peerName)
            rejoinTemplate = rejoinTemplate.replace("{channel}", channel)
            rejoinTemplate = rejoinTemplate.replace("{ordererName}", ordererName)


            String fileName = "rejoin_channel.sh"

            FileUtil.writeFile(FULL_PATH, "rejoin_channel.sh", rejoinTemplate, true)

            Thread.sleep(30000)

            CommandLineRunner.executeCommand(null, getShCommand() + FULL_PATH + 'rejoin_channel.sh')
        }
    }

    // Cli container creation
    // Latest cli image is pulled and container's config is created
    // Volume bindings are all done as a hostconfig parameters
    public createCli(DockerClient docker){

        if(!VersionResolver.updateContainer(docker, "cli", cliVersion)){
            return
        }

        logger.info("Creating container for cli")

        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers())
        for(Container i : containers){
            if(i.names()[0].substring(1) == "cli"){
                logger.info("Removed existing container: " + i.names()[0].substring(1))
                docker.removeContainer(i.id(), DockerClient.RemoveContainerParam.forceKill())
            }
        }

        List<String> env = new ArrayList<>()
        env.add("GOPATH=/opt/gopath")
        env.add("CORE_VM_ENDPOINT=unix:///host/var/run/docker.sock")
        env.add("CORE_LOGGING_LEVEL=DEBUG")
        env.add("CORE_PEER_ID=cli")
        env.add("CORE_PEER_ADDRESS="+peerName+":"+peerPort)
        env.add("CORE_PEER_LOCALMSPID="+mspid)
        env.add("CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/"+orgName+"/users/Admin@"+orgName+"/msp")
        env.add("CORE_CHAINCODE_KEEPALIVE=10")
        env.add("CORE_PEER_TLS_ENABLED=false")

        HostConfig cliHostConfig = HostConfig.builder()
                .appendBinds("/var/run/:/host/var/run/")
                .appendBinds(mainPath + "smartlog_chaincodes/:/opt/gopath/src/github.com/smartlog_chaincodes/")
                .appendBinds(FULL_PATH + "crypto-config:/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/")
                .build()

        docker.pull("hyperledger/fabric-tools:"+cliVersion)
        ContainerConfig cliContainerConfig = ContainerConfig.builder()
                .image("hyperledger/fabric-tools:"+cliVersion)
                .hostConfig(cliHostConfig)
                .env(env)
                .workingDir("/opt/gopath/src/github.com/hyperledger/fabric/peer")
                .tty(true)
                .build()

        ContainerCreation cliContainer = docker.createContainer(cliContainerConfig)
        docker.renameContainer(cliContainer.id(),"cli")
        docker.startContainer(cliContainer.id())
        docker.connectToNetwork(cliContainer.id(),NETWORK)
        docker.disconnectFromNetwork(cliContainer.id(), "bridge")
    }


    // Watchtower container creator
    // Latest watchtower image is pulled
    // Volume bindings done as hostconfig parameter
    public createWatchTower(DockerClient docker){

        if(!VersionResolver.updateContainer(docker, "watchtower", watchtowerVersion)){
            return
        }

        List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers())
        for(Container i : containers){
            if(i.names()[0].substring(1) == "watchtower"){
                logger.info("Removed existing container: " + i.names()[0].substring(1))
                docker.removeContainer(i.id(), DockerClient.RemoveContainerParam.forceKill())
            }
        }

        logger.info("Creating container for watchtower")

        List<String> watchtowerEnv = new ArrayList<>()
        watchtowerEnv.add("REPO_USER=" + REGISTRY_USER)
        watchtowerEnv.add("REPO_PASS=" + REGISTRY_PASSWORD)

        HostConfig watchtowerHostConfig = HostConfig.builder()
                .appendBinds("/var/run/docker.sock:/var/run/docker.sock")
                .build()

        docker.pull("v2tec/watchtower:"+watchtowerVersion)
        ContainerConfig watchtowerContainerConfig = ContainerConfig.builder()
                .image('v2tec/watchtower:'+watchtowerVersion)
                .hostConfig(watchtowerHostConfig)
                .env(watchtowerEnv)
                .build()

        ContainerCreation watchtowerContainer = docker.createContainer(watchtowerContainerConfig)
        docker.renameContainer(watchtowerContainer.id(), "watchtower")
        docker.startContainer(watchtowerContainer.id())
        docker.connectToNetwork(watchtowerContainer.id(), NETWORK)
        docker.disconnectFromNetwork(watchtowerContainer.id(), "bridge")
    }

    /**
     * Returns shell command to run scripts depending from the OS
     * @return
     */
    private static final String getShCommand() {

        final String os = System.getProperty("os.name").toLowerCase()

        if (os.contains("nix") || os.contains("linux")) {
            return "sh "
        }
        else if (os.contains("win")) {
            return "cmd /c "
        }

        return "sh "
    }

    /**
     * Return full path
     * @return
     */
    public static final String getFullPath() {
        return FULL_PATH;
    }

    /**
     * Check if NETWORK is already found, otherwise create it
     */
    private static void createNetworkIfNotExisting(DockerClient docker) {

        try {

            NetworkConfig networkConfig = NetworkConfig.builder()
                    .checkDuplicate(true)
                    .attachable(true)
                    .name(NETWORK)
                    .build()

            docker.createNetwork(networkConfig)

        } catch(Exception e) {
            logger.warn("Something went wrong when creating network", e)
        }
    }
}
