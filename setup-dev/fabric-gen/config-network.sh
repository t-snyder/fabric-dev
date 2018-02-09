# This script is to be run on the development machine.
# Set the os - this script is being run on linux ubuntu
os_arch=$(echo "$(uname -s)-amd64" | awk '{print tolower($0)}')

# clean up existing paths
rm -rf channel-artifacts
rm -rf crypto-config
mkdir channel-artifacts

# Generate the crypto material for the orderer and organizations defined within crypto-config-dev.yaml
../bin/cryptogen generate --config=./crypto-config-dev.yaml

# Create the genesis block for the orderer which will have all the orgs MSPs in it.
../bin/configtxgen -profile DevOrdererGenesis -outputBlock ./channel-artifacts/genesis.block

# Create the channel transaction for each channel
../bin/configtxgen -profile PublicChannel   -outputCreateChannelTx ./channel-artifacts/publicchannel.tx   -channelID publicchannel
../bin/configtxgen -profile MgmtOrg2Channel -outputCreateChannelTx ./channel-artifacts/mgmtorg2channel.tx -channelID mgmtorg2channel
../bin/configtxgen -profile MgmtOrg3Channel -outputCreateChannelTx ./channel-artifacts/mgmtorg3channel.tx -channelID mgmtorg3channel
../bin/configtxgen -profile MgmtOrg4Channel -outputCreateChannelTx ./channel-artifacts/mgmtorg4channel.tx -channelID mgmtorg4channel

# Create the anchor peer configurations for each of the Org Peers 
#../bin/configtxgen -profile PublicChannel -outputAnchorPeersUpdate ./channel-artifacts/Org1MSPanchors.tx -channelID publicchannel -asOrg Org1MSP
#../bin/configtxgen -profile PublicChannel -outputAnchorPeersUpdate ./channel-artifacts/Org2MSPanchors.tx -channelID publicchannel -asOrg Org2MSP
#../bin/configtxgen -profile PublicChannel -outputAnchorPeersUpdate ./channel-artifacts/Org3MSPanchors.tx -channelID publicchannel -asOrg Org3MSP
#../bin/configtxgen -profile PublicChannel -outputAnchorPeersUpdate ./channel-artifacts/Org4MSPanchors.tx -channelID publicchannel -asOrg Org4MSP

echo "*** Don't forget to refresh your project fabric-gen directory ***"