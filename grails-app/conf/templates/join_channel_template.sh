#!/bin/bash

# Exit on first error, print all commands.
set -ev

# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1

# Join peer to the channel for the first time
docker exec -e "CORE_PEER_LOCALMSPID={mspid}" -e "CORE_PEER_MSPCONFIGPATH=/etc/hyperledger/msp/users/Admin@{orgName}/msp" {peerName} peer channel join -b {channel}.block