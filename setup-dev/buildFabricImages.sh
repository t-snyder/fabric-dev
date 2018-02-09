eval $(minikube docker-env)
docker build -f Peer0-Org1-Dockerfile -t library/peer0-org1:1.0.4 .
docker build -f Peer0-Org2-Dockerfile -t library/peer0-org2:1.0.4 .
docker build -f Peer0-Org3-Dockerfile -t library/peer0-org3:1.0.4 .
docker build -f Peer0-Org4-Dockerfile -t library/peer0-org4:1.0.4 .
docker build -f CLI-Dockerfile -t library/cli-peer:1.0.4 .
docker build -f Orderer-Dockerfile -t library/orderer:1.0.4 .