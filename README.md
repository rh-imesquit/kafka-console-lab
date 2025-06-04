# Red Hat Streams for Apache Kafka Lab

This lab aims to practically demonstrate the functionalities of the Kafka Console integrated with Red Hat Streams for Apache Kafka 2.9, enabling real-time visualization and monitoring of the main components of a Kafka cluster.

During the exercise, a simple and instructional scenario will be implemented, in which a Producer application will send order records to a Kafka topic called orders, while a Consumer application will be responsible for consuming these records and displaying them via log. The main focus will be on using the Kafka Console to explore the following features:

* Visualization of topics and their messages

* Monitoring of consumer groups

* Checking operational metrics of the cluster

* Tracking detailed information about partitions, offsets, and replication status

This environment has been prepared to facilitate the understanding of essential observability concepts in Kafka and to demonstrate how the Kafka Console can be a powerful tool in supporting the operation and diagnosis of Kafka clusters in corporate environments.

### <h2 style="color: #e5b449;">What is</h2>

**Red Hat Streams for Apache Kafka** is an enterprise distribution of Apache Kafka offered by Red Hat, aimed at developing event-driven applications in Kubernetes and OpenShift environments. It provides a reliable, secure, and integrated platform for real-time data streaming, with full Red Hat support, and includes operators to simplify the installation, configuration, management, and scalability of Kafka clusters in hybrid or on-premises cloud environments.

**Red Hat Streams for Apache Kafka Console** is a web interface included with Red Hat Streams for Apache Kafka that allows intuitive visualization and monitoring of the main components of a Kafka cluster. With it, you can inspect topics, track real-time messages, view consumer groups, check operational metrics, and access detailed information about partitions, offsets, and replication status. The console enhances Kafka observability and management, being especially useful in OpenShift environments, where it integrates natively with the cluster.

### <h2 style="color: #e5b449;">Technologies and versions used in this tutorial</h2>

| Component                                   | Version |
|---------------------------------------------|---------|
| Red Hat OpenShift Container Platform        | 4.17    |
| Red Hat Streams for Apache Kafka            | 2.9.0   |
| Red Hat Streams for Apache Kafka Console    | 2.9.0   |
| Quarkus Community (Java 17)                 | 3.20    |


### <h2 style="color: #e5b449;">How-to install and configure Red Hat Streams for Apache Kafka</h2>

First, create a new project called kafka. To do this, in the left-hand menu, select Home > Projects and click the Create Project button.

![Creating kafka project](./images/kafka/01%20-%20Creating%20the%20Kafka%20project.png)

In the window that opens, fill in the Name field with kafka and click the Create button.

Next, in the left-hand menu, go to Operators > OperatorHub. On this screen, search for "kafka" and select the operator Streams for Apache Kafka provided by Red Hat.

An information screen about the operator will appear. Click the Install button.

![Installing Kafka Operator](./images/kafka/02%20-%20Installing%20Kafka%20Operator.png)

In the form that appears, keep the default values and click Install.
Once the installation is successfully completed, the message "Installed operator: ready to use" will be displayed. At this point, click the View Operator button. The Operator details screen will appear.

![Installing Kafka Operator](./images/kafka/03%20-%20Installing%20Kafka%20Operator.png)

Now let's start creating the Kafka CRs located in the [infra/kafka/](./infra/kafka/) directory of this repository. From here on, we'll proceed via terminal using the oc CLI.

```
$ cd /infra/kafka
```

Log in to OpenShift using the command:

```
$ oc login -u <USER> -p <PASSWORD> <HOST>
```

If you're not in the kafka namespace, switch to it by running:

```
$ oc project kafka
```

Apply the Kafka resource by executing:

```
$ oc apply -f kafka.yaml
```

Wait a moment, as it may take some time for all Kafka cluster resources to be provisioned. Finally, verify that the resource was created in the Kafka tab with the Ready status.

![Creating Kafka resource](./images/kafka/04%20-%20Creating%20Kafka%20resource.png)


Apply the KafkaUser resources by executing:

```
$ oc apply -f kafka-app-user.yaml -f kafka-console-user.yaml
```

Verify that the users were created in the KafkaUser tab with the Ready status.

![Creating KafkaUser resources](./images/kafka/05%20-%20Creating%20KafkaUser%20resources.png)

Apply the KafkaTopic resource by running:

```
$ oc apply -f kafka-topic.yaml
```

Verify that the resource was created in the KafkaTopic tab with the Ready status.
![Creating KafkaTopic resources](./images/kafka/06%20-%20Creating%20KafkaTopic%20resource.png)

Check if all pods in the kafka namespace are in Running status. Go to the side menu and select Workloads > Pods.

![Kafka pods running](./images/kafka/07%20-%20Kafka%20pods%20running.png)

We’ve completed the Kafka installation part in the OpenShift cluster. Proceed to the next step.




























Obter credenciais

```
$ oc get secret kafka-console-user -n kafka -o jsonpath='{.data.password}' | base64 -d
```

```
$ oc get secret app-user -n kafka -o jsonpath='{.data.password}' | base64 -d
```



```
$ curl -X POST <ROUTE>/orders   -H "Content-Type: application/json"   -d '{
    "customerId": "cliente-123",
    "items": ["item1", "item2", "item3"]
  }'
```