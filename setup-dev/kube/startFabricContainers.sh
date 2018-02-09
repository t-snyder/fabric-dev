eval $(minikube docker-env)
kubectl create -f orderer.yaml
kubectl create -f peer0.yaml
kubectl create -f peer1.yaml
kubectl create -f peer2.yaml
kubectl create -f peer3.yaml
kubectl create -f cli.yaml
