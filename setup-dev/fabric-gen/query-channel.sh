# the commands below are meant to be copy pasted in the cli container and run separately
CORE_PEER_ADDRESS=peer0-org1:7051 CORE_PEER_LOCALMSPID="Org1MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org1/users/Admin@Org1/msp peer chaincode query -C publicchannel -n publiccc -c '{"Args":["query","a"]}'

CORE_PEER_ADDRESS=peer0-org2:7051 CORE_PEER_LOCALMSPID="Org2MSP" CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/Org2/users/Admin@Org2/msp peer chaincode query -C publicchannel -n publiccc -c '{"Args":["query","a"]}'
