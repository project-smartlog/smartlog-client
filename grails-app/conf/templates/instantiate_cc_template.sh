#!/bin/bash

# Exit on first error, print all commands.
set -ev

# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1

docker exec -e "CORE_PEER_LOCALMSPID={mspid}" -e "CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/{orgName}/users/Admin@{orgName}/msp" cli peer chaincode instantiate -o {ordererName}:7050 -C {channel} -n {chaincodeName} -v {chaincodeVersion} -c '{"Args":[""]}'