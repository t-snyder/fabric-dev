eval $(minikube docker-env)
kubectl apply -f ./Kafka/configure/minikube-storageclass-broker.yml
kubectl apply -f ./Kafka/configure/minikube-storageclass-zookeeper.yml
kubectl apply -f ./Kafka/zookeeper/
/bin/sleep 45
kubectl apply -f ./Kafka/kafka/
/bin/sleep 30
kubectl apply -f ./Kafka/outside-services/