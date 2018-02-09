echo "Creating channels"
CORE_PEER_ADDRESS=peer0-org1:7051 peer channel create -o orderer-service:7050 -c publicchannel   -f ./channel-artifacts/publicchannel.tx
CORE_PEER_ADDRESS=peer0-org1:7051 peer channel create -o orderer-service:7050 -c mgmtorg2channel -f ./channel-artifacts/mgmtorg2channel.tx
CORE_PEER_ADDRESS=peer0-org1:7051 peer channel create -o orderer-service:7050 -c mgmtorg3channel -f ./channel-artifacts/mgmtorg3channel.tx
CORE_PEER_ADDRESS=peer0-org1:7051 peer channel create -o orderer-service:7050 -c mgmtorg4channel -f ./channel-artifacts/mgmtorg4channel.tx

echo "Joining Peers to Public Channel"
CORE_PEER_ADDRESS=peer0-org1:7051 CORE_PEER_LOCALMSPID="Org1MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org1/users/Admin@Org1/msp peer channel join -b publicchannel.block
CORE_PEER_ADDRESS=peer0-org2:7051 CORE_PEER_LOCALMSPID="Org2MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org2/users/Admin@Org2/msp peer channel join -b publicchannel.block
CORE_PEER_ADDRESS=peer0-org3:7051 CORE_PEER_LOCALMSPID="Org3MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org3/users/Admin@Org3/msp peer channel join -b publicchannel.block
CORE_PEER_ADDRESS=peer0-org4:7051 CORE_PEER_LOCALMSPID="Org4MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org4/users/Admin@Org4/msp peer channel join -b publicchannel.block

echo "joining Peers to MngmntOrg2 Channel"
CORE_PEER_ADDRESS=peer0-org1:7051 CORE_PEER_LOCALMSPID="Org1MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org1/users/Admin@Org1/msp peer channel join -b mgmtorg2channel.block
CORE_PEER_ADDRESS=peer0-org2:7051 CORE_PEER_LOCALMSPID="Org2MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org2/users/Admin@Org2/msp peer channel join -b mgmtorg2channel.block

echo "joining Peers to MngmntOrg3 Channel"
CORE_PEER_ADDRESS=peer0-org1:7051 CORE_PEER_LOCALMSPID="Org1MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org1/users/Admin@Org1/msp peer channel join -b mgmtorg3channel.block
CORE_PEER_ADDRESS=peer0-org3:7051 CORE_PEER_LOCALMSPID="Org3MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org3/users/Admin@Org3/msp peer channel join -b mgmtorg3channel.block

echo "joining Peers to MngmntOrg4 Channel"
CORE_PEER_ADDRESS=peer0-org1:7051 CORE_PEER_LOCALMSPID="Org1MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org1/users/Admin@Org1/msp peer channel join -b mgmtorg4channel.block
CORE_PEER_ADDRESS=peer0-org4:7051 CORE_PEER_LOCALMSPID="Org4MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org4/users/Admin@Org4/msp peer channel join -b mgmtorg4channel.block
